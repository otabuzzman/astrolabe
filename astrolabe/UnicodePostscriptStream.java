
package astrolabe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UnicodePostscriptStream extends PostscriptStream {

	private final static Log log = LogFactory.getLog( UnicodePostscriptStream.class ) ;

	private final static int DEFAULT_CHARTSIZE		= 256 ;

	private final static String DEFAULT_FONTNAME	= "/Times-Roman" ;
	private final static String DEFAULT_CHARPROC	= "/.notdef" ;

	private int chartsize ;

	private String fontname ;
	private String charproc ;

	private final Map<String, UnicodeControlBlock> block = new HashMap<String, UnicodeControlBlock>() ;

	public final class UnicodeControlBlock {

		public String fontname ;
		public String[] encoding ;

		public String string ;

		private UnicodeControlBlock( String fontname, String[] encodingVector ) {
			this.fontname = fontname ;
			this.encoding = encodingVector ;
		}

		private UnicodeControlBlock( UnicodeControlBlock unicodeControlBlock ) {
			this( unicodeControlBlock.fontname, unicodeControlBlock.encoding ) ;
		}
	}

	public UnicodePostscriptStream( OutputStream out ) {
		super( out ) ;

		Class<?> clazz ;
		Preferences nodeClass, nodeBlock ;
		String fontname, encoding ;

		clazz = this.getClass() ;
		while ( ! clazz.getSimpleName().equals( "UnicodePostscriptStream" ) )
			clazz = clazz.getSuperclass() ;

		nodeClass = Configuration.getClassNode( clazz, null , null ) ;

		if ( nodeClass == null )
			return ;

		this.chartsize = Configuration.getValue( nodeClass,
				ApplicationConstant.PK_POSTSCRIPT_CHARTSIZE, DEFAULT_CHARTSIZE ) ;
		this.fontname = Configuration.getValue( nodeClass,
				ApplicationConstant.PK_POSTSCRIPT_FONTNAME, DEFAULT_FONTNAME ) ;
		this.charproc = Configuration.getValue( nodeClass,
				ApplicationConstant.PK_POSTSCRIPT_CHARPROC, DEFAULT_CHARPROC ) ;

		try {
			for ( String name : nodeClass.childrenNames() )
				if ( name.matches( "[0-9A-Fa-f]{4,6}\\.\\.[0-9A-Fa-f]{4,6}-[0-9]+" ) ) {
					nodeBlock = nodeClass.node( name ) ;
					fontname = nodeBlock.get( ApplicationConstant.PK_POSTSCRIPT_FONTNAME, null ) ;
					encoding = nodeBlock.get( ApplicationConstant.PK_POSTSCRIPT_ENCODING, null ) ;
					if ( fontname == null || encoding == null )
						continue ;
					addUnicodeControlBlock( name, fontname, encoding ) ;
				}
		} catch ( BackingStoreException e ) {
			throw new RuntimeException ( e.toString() ) ;
		}
	}

	public boolean addUnicodeControlBlock( String name, String fontname, String encoding ) {
		String[] nv, encodingVector ;
		int blockstart, blockend, chart ;
		UnicodeBlock unicodeBlock ;
		java.lang.Character.UnicodeBlock characterUnicodeBlock ;

		if ( ! name.matches( "[0-9A-Fa-f]{4,6}\\.\\.[0-9A-Fa-f]{4,6}-[0-9]+" ) )
			return false ;

		nv = name.split( "\\.\\." ) ;
		blockstart = Integer.valueOf( nv[0], 16 ) ;
		nv = nv[1].split( "-" ) ;
		blockend = Integer.valueOf( nv[0], 16 ) ;
		chart = Integer.valueOf( nv[1] ) ;

		characterUnicodeBlock = Character.UnicodeBlock.of( blockstart ) ;
		if ( characterUnicodeBlock == null )
			return false ;

		unicodeBlock = UnicodeBlock.forName( characterUnicodeBlock.toString() ) ;
		if ( unicodeBlock == null )
			return false ;
		if ( blockend>unicodeBlock.start+unicodeBlock.size )
			return false ;
		if ( chart*chartsize>unicodeBlock.size )
			return false ;

		if ( fontname.charAt( 0 ) != '/' )
			return false ;

		encodingVector = encoding.trim().split( "\\p{Space}+" ) ;
		if ( encodingVector.length>chartsize )
			return false ;

		block.put( name, new UnicodeControlBlock( fontname, encodingVector ) ) ;

		return true ;
	}

	public UnicodeControlBlock getUnicodeControlBlock( UnicodeBlock unicodeBlock, int chart, String string ) {
		UnicodeControlBlock unicodeControlBlock, r ;
		String name, encoding[] ;

		if ( unicodeBlock.start>0xffff )
			name = String.format( "%X..%X-%d",
					unicodeBlock.start, unicodeBlock.start+unicodeBlock.size-1, chart ) ;
		else
			name = String.format( "%04X..%04X-%d",
					unicodeBlock.start, unicodeBlock.start+unicodeBlock.size-1, chart ) ;

		unicodeControlBlock = block.get( name ) ;
		if ( unicodeControlBlock == null ) {
			if ( unicodeBlock.size<chartsize )
				encoding = new String[ unicodeBlock.size ] ;
			else
				encoding = new String[ chartsize ] ;
			for ( int e=0 ; e<encoding.length ; e++ )
				encoding[e] = this.charproc ;
			r = new UnicodeControlBlock( fontname, encoding ) ;
		} else
			r = new UnicodeControlBlock( unicodeControlBlock ) ;

		r.string = new String() ;
		for ( int c=0 ; c<string.length() ; c++ )
			r.string = r.string+String.format( "\\%03o",
					string.codePointAt( c )-( unicodeBlock.start+chart*chartsize ) ) ;

		return r ;
	}

	public UnicodeControlBlock[] getUnicodeControlBlockArray( String string ) {
		java.util.Vector<UnicodeControlBlock> r = new java.util.Vector<UnicodeControlBlock>() ;
		Character.UnicodeBlock currentCharacterUnicodeBlock, nextCharacterUnicodeBlock ;
		UnicodeBlock currentUnicodeBlock, nextUnicodeBlock ;
		UnicodeControlBlock unicodeControlBlock ;
		int currentCodePoint, nextCodePoint ;
		int currentChart, nextChart ;
		int cdx, ndx ;

		cdx = 0 ;
		ndx = 1 ;

		try {
			currentCodePoint = Character.codePointAt( string.toCharArray(), cdx ) ;
			currentCharacterUnicodeBlock = Character.UnicodeBlock.of( currentCodePoint ) ;
			if ( currentCharacterUnicodeBlock == null )
				throw new ParameterNotValidException(
						currentCodePoint>0xffff?
								String.format( "0x%x", currentCodePoint ):
									String.format( "0x%04x", currentCodePoint )) ;

			currentUnicodeBlock = UnicodeBlock.forName( currentCharacterUnicodeBlock.toString() ) ;
			if ( currentUnicodeBlock == null )
				throw new ParameterNotValidException( currentCharacterUnicodeBlock.toString() ) ;
			currentChart = ( currentCodePoint-currentUnicodeBlock.start )/chartsize ;

			for ( ; ndx<string.length() ; ndx++ ) {
				nextCodePoint = Character.codePointAt( string.toCharArray(), ndx ) ;
				nextCharacterUnicodeBlock = Character.UnicodeBlock.of( nextCodePoint ) ;
				if ( nextCharacterUnicodeBlock == null )
					throw new ParameterNotValidException(
							currentCodePoint>0xffff?
									String.format( "0x%x", nextCodePoint ):
										String.format( "0x%04x", nextCodePoint )) ;

				nextUnicodeBlock = UnicodeBlock.forName( nextCharacterUnicodeBlock.toString() ) ;
				if ( nextUnicodeBlock == null )
					throw new ParameterNotValidException( nextCharacterUnicodeBlock.toString() ) ;
				nextChart = ( nextCodePoint-nextUnicodeBlock.start )/chartsize ;
				if ( currentUnicodeBlock.start == nextUnicodeBlock.start &&
						currentChart == nextChart )
					continue ;

				unicodeControlBlock = getUnicodeControlBlock( currentUnicodeBlock, currentChart,
						string.substring( cdx, ndx ) ) ;

				r.add( unicodeControlBlock ) ;

				cdx = ndx ;

				currentUnicodeBlock = nextUnicodeBlock ;
				currentCodePoint = nextCodePoint ;
				currentChart = nextChart ;
			}

			unicodeControlBlock = getUnicodeControlBlock( currentUnicodeBlock, currentChart,
					string.substring( cdx, ndx ) ) ;

			r.add( unicodeControlBlock ) ;
		} catch ( ParameterNotValidException e ) {
			String msg ;

			msg = MessageCatalog.message( ApplicationConstant.GC_APPLICATION, ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
			msg = MessageFormat.format( msg, new Object[] { e.toString(), "\""+string+"\"" } ) ;
			log.warn( msg ) ;

			return null ;
		}

		return r.toArray( new UnicodeControlBlock[0] ) ;
	}

	public static void main( String[] argv ) {
		HashMap<String, String[]> block ;
		BufferedReader stdin ;
		String line, token[], name, nameArray[], encodingVector[] ;
		int chartsize ;
		int codepoint ;
		java.lang.Character.UnicodeBlock characterUnicodeBlock ;
		UnicodeBlock unicodeBlock ;

		block = new HashMap<String, String[]>() ;
		chartsize = Configuration.getValue(
				Configuration.getClassNode( UnicodePostscriptStream.class, null , null ),
				ApplicationConstant.PK_POSTSCRIPT_CHARTSIZE, DEFAULT_CHARTSIZE ) ;

		try {
			stdin = new BufferedReader( new InputStreamReader( System.in ) ) ;
			while ( ( line = stdin.readLine() ) != null ) {
				token = line.trim()
				.split( "\\p{Space}+" ) ;

				codepoint = Integer.valueOf( token[1], 16 ) ;
				characterUnicodeBlock = Character.UnicodeBlock.of( codepoint ) ;
				if ( characterUnicodeBlock == null ) {
					System.err.println( line ) ;

					continue ;
				}
				unicodeBlock = UnicodeBlock.forName( characterUnicodeBlock.toString() ) ;
				if ( unicodeBlock == null ) {
					System.err.println( line ) ;

					continue ;
				}

				name = String.format( codepoint>0xffff?"%X..%X-%d":"%04X..%04X-%d",
						unicodeBlock.start,
						unicodeBlock.start+unicodeBlock.size-1,
						( codepoint-unicodeBlock.start )/chartsize ) ;
				encodingVector = block.get( name ) ;
				if ( encodingVector == null ) {
					encodingVector = new String[chartsize] ;
					for ( int i=0 ; i<chartsize ; i++ )
						encodingVector[i] = DEFAULT_CHARPROC ;
					block.put( name, encodingVector ) ;
				}
				encodingVector[( codepoint-unicodeBlock.start )%chartsize] = token[2] ;
			}
		} catch ( IOException e ) {
			System.exit( 1 ) ;
		}

		nameArray = block.keySet().toArray( new String[0] ) ;
		Arrays.sort( nameArray ) ;

		for ( String key : nameArray ) {
			System.out.print( String.format( "<node name=\"%s\">", key ) ) ;

			System.out.print( "<map>" ) ;

			System.out.print( String.format( "<entry key=\"fontname\" value=\"%s\"/>", argv[0] ) ) ;

			System.out.print( "<entry key=\"encoding\" value=\"" ) ;
			encodingVector = block.get( key ) ;
			System.out.print( encodingVector[0] ) ;
			for ( int i=1 ; i<encodingVector.length ; i++ )
				System.out.print( " "+encodingVector[i] ) ;
			System.out.print( "\"/>" ) ;

			System.out.print( "</map>" ) ;

			System.out.print( "</node>" ) ;
		}
	}
}
