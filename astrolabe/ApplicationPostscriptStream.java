
package astrolabe;

import java.io.OutputStream;
import java.util.HashSet;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class ApplicationPostscriptStream extends UnicodePostscriptStream {

	private final HashSet<String> prolog = new HashSet<String>() ;

	// configuration key (CK_), node (CN_)
	private final static String CK_PROLOG 	= "prolog" ;
	private final static String CN_PROLIB 	= "prolib" ;

	private final static String CK_FONTNAME	= "fontname" ;
	private final static String CK_ENCODING	= "encoding" ;

	public ApplicationPostscriptStream( OutputStream out ) {
		super( out ) ;

		String desc[], name, fontname, encoding ;
		Preferences node, block ;

		try {
			name = this.getClass().getName().replaceAll( "\\.", "/" ) ;
			if ( ! Preferences.userRoot().nodeExists( name ) )
				return ;
			node = Preferences.userRoot().node( name ) ;
			desc = node.childrenNames() ;
			for ( int b=0 ; b<desc.length ; b++ )
				if ( desc[b].matches( "[0-9A-Fa-f]{4,6}\\.\\.[0-9A-Fa-f]{4,6}-[0-9]+" ) ) {
					block = node.node( desc[b] ) ;
					fontname = block.get( CK_FONTNAME, null ) ;
					encoding = block.get( CK_ENCODING, null ) ;
					if ( fontname == null || encoding == null )
						continue ;
					addUnicodeControlBlock( desc[b], fontname, encoding ) ;
				}
			if ( ! node.nodeExists( CN_PROLIB ) )
				return ;
			for ( String key : node.node( CN_PROLIB ).keys() )
				prolog.add( key ) ;
		} catch ( BackingStoreException e ) {
			return ;
		}
	}

	public boolean op( String op ) {
		if ( prolog.contains( op ) )
			return script( op ) ;
		return super.op( op ) ;
	}

	public void emitDSCHeader() {
		dc( "!PS-Adobe-3.0", null ) ;
		dc( "%Creator:", new String[] { getClass().getName() } ) ;
		dc( "%CreationDate:", new String[] { new java.util.Date().toString() } ) ;
		dc( "%EndComments", null ) ;
	}

	public void emitDSCTrailer() {
		dc( "%Trailer", null ) ;
		dc( "%EOF", null ) ;
	}

	public void emitDSCProlog() {
		Preferences node ;
		String name, prolog ;

		try {
			name = this.getClass().getName().replaceAll( "\\.", "/" ) ;
			if ( ! Preferences.userRoot().nodeExists( name ) )
				return ;
			node = Preferences.userRoot().node( name ) ;
			prolog = node.get( CK_PROLOG, null ) ;
			if ( prolog == null )
				return ;
		} catch ( BackingStoreException e ) {
			return ;
		}

		dc( "%BeginProlog", null ) ;
		script( prolog ) ;
		dc( "%EndProlog", null ) ;
	}
}
