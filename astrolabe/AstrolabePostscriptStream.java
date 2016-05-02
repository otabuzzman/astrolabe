
package astrolabe;

import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class AstrolabePostscriptStream extends UnicodePostscriptStream {

	public AstrolabePostscriptStream( OutputStream out ) {
		super( out ) ;

		String name[], fontname, encoding ;
		Preferences node, block ;

		try {
			node = Configuration.getClassNode( this, null , null ) ;
			name = node.childrenNames() ;
			for ( int b=0 ; b<name.length ; b++ )
				if ( name[b].matches( "[0-9A-Fa-f]{4,6}\\.\\.[0-9A-Fa-f]{4,6}-[0-9]+" ) ) {
					block = node.node( name[b] ) ;
					fontname = block.get( ApplicationConstant.PK_POSTSCRIPT_FONTNAME, null ) ;
					encoding = block.get( ApplicationConstant.PK_POSTSCRIPT_ENCODING, null ) ;
					if ( fontname == null || encoding == null )
						continue ;
					addUnicodeControlBlock( name[b], fontname, encoding ) ;
				}
		} catch ( BackingStoreException e ) {
			throw new RuntimeException ( e.toString() ) ;
		}
	}

	public void push( String string ) {
		try {
			super.push( string ) ;
		} catch ( ParameterNotValidException e ) {
			String msg ;

			msg = MessageCatalog.message( ApplicationConstant.GC_APPLICATION, ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
			msg = MessageFormat.format( msg, new Object[] { e.toString(), "" } ) ;

			throw new RuntimeException( msg ) ;
		}
	} 

	public void push( String[] string ) {        
		array( true ) ;
		for ( String def : string )
			push( def ) ;
		array( false ) ;
	}

	public void emitDSCHeader() {
		push( "%!PS-Adobe-3.0" ) ;
		dsc.creator( getClass().getName() ) ;
		dsc.creationDate( new java.util.Date().toString() ) ;
		dsc.endComments() ;
	}

	public void emitDSCTrailer() {
		dsc.trailer() ;
		dsc.eOF() ;
	}

	public void emitDSCProlog() {
		Preferences node ;
		String prolog ;

		node = Configuration.getClassNode( this, null , null ) ;
		prolog = node.get( ApplicationConstant.PK_POSTSCRIPT_PROLOG, null ) ;
		if ( prolog == null )
			return ;

		dsc.beginProlog() ;
		for ( String token : prolog.trim().split( "\\p{Space}+" ) )
			print( token+"\n" ) ;
		dsc.endProlog() ;
	}
}
