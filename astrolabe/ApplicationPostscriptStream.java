
package astrolabe;

import java.io.OutputStream;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class ApplicationPostscriptStream extends UnicodePostscriptStream {

	// configuration key (CK_)
	private final static String CK_PROLOG 	= "prolog" ;

	private final static String CK_FONTNAME	= "fontname" ;
	private final static String CK_ENCODING	= "encoding" ;

	public ApplicationPostscriptStream( OutputStream out ) {
		super( out ) ;

		String desc[], name, fontname, encoding ;
		Preferences node, block ;

		try {
			name = this.getClass().getName().replaceAll( "\\.", "/" ) ;
			if ( ! Preferences.systemRoot().nodeExists( name ) )
				return ;
			node = Preferences.systemRoot().node( name ) ;
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
		} catch ( BackingStoreException e ) {
			return ;
		}
	}

	public void push( String string ) {
		try {
			super.push( string ) ;
		} catch ( ParameterNotValidException e ) {
			throw new RuntimeException( e.getMessage() ) ;
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
		String name, prolog ;

		try {
			name = this.getClass().getName().replaceAll( "\\.", "/" ) ;
			if ( ! Preferences.systemRoot().nodeExists( name ) )
				return ;
			node = Preferences.systemRoot().node( name ) ;
			prolog = node.get( CK_PROLOG, null ) ;
			if ( prolog == null )
				return ;
		} catch ( BackingStoreException e ) {
			return ;
		}

		dsc.beginProlog() ;
		for ( String token : prolog.trim().split( "\\p{Space}+" ) )
			print( token+"\n" ) ;
		dsc.endProlog() ;
	}

	public void pagesize() {        
		print( "pagesize\n" ) ;
	}

	public void tpath() {        
		print( "tpath\n" ) ;
	}

	public void tshow() {        
		print( "tshow\n" ) ;
	}

	public void twidth() {        
		print( "twidth\n" ) ;
	}

	public void setencoding() {        
		print( "setencoding\n" ) ;
	}

	public void gdraw() {        
		print( "gdraw\n" ) ;
	}

	public void glen() {        
		print( "glen\n" ) ;
	}

	public void gmove() {        
		print( "gmove\n" ) ;
	}

	public void gpath() {        
		print( "gpath\n" ) ;
	}

	public void grev() {        
		print( "grev\n" ) ;
	}

	public void max() {        
		print( "max\n" ) ;
	}

	public void min() {        
		print( "min\n" ) ;
	}

	public void vabs() {        
		print( "vabs\n" ) ;
	}

	public void vadd() {        
		print( "vadd\n" ) ;
	}

	public void vsub() {        
		print( "vsub\n" ) ;
	}

	public void vmul() {        
		print( "vmul\n" ) ;
	}
}
