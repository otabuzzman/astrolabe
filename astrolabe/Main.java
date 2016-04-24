
package astrolabe;

public class Main {
	public static void main( String[] argv ) {
		astrolabe.model.AstrolabeType alT ;
		PostscriptStream ps ;
		Astrolabe a ;

		try {
			java.util.prefs.Preferences.importPreferences( new java.io.FileInputStream( "astrolabe.preferences" ) ) ;

			alT = (astrolabe.model.AstrolabeType) astrolabe.model.Astrolabe.unmarshal( new java.io.FileReader( argv[0] ) ) ;

			ps = new PostscriptStream( new PrintStream( System.out ) ) ;
			a = new Astrolabe( alT ) ;
			a.emitPS( ps ) ;

			ps.close();
		} catch ( Exception e ) {
			e.printStackTrace() ;
			System.exit( 1 ) ;
		}

		System.exit( 0 ) ;
	} 

	public java.util.prefs.Preferences getClassNode( String instance, String qualifier ) {        
		String i = instance != null ? "/"+instance : "" ;
		String q = qualifier != null ? "/"+qualifier : "" ;
		String d = "/"+this.getClass().getName().replaceAll( "\\.", "/" ).split( "\\$", 2 )[0] ;
		String n = d+i+q ;

		try {
			if ( ! java.util.prefs.Preferences.systemRoot().nodeExists( n ) ) {
				n = d+q ;
			}
		} catch ( java.util.prefs.BackingStoreException e ) {}

		return java.util.prefs.Preferences.systemRoot().node( n ) ;
	} 

	public java.util.prefs.Preferences getNestedClassNode( String instance, String qualifier ) {        
		String q = qualifier != null ? "/"+qualifier : "" ;
		String c[] = this.getClass().getName().replaceAll( "\\.", "/" ).split( "\\$", 2 ) ;
		String s = c[1].replaceAll( "[$]", "/" ) ;
		String n = "/"+c[0]+( instance != null? "/"+instance+"/" : "/" )+s+q ;

		try {
			if ( ! java.util.prefs.Preferences.systemRoot().nodeExists( n ) ) {
				n = "/"+c[0]+"/"+s+q ;
			}
		} catch ( java.util.prefs.BackingStoreException e ) {}

		return java.util.prefs.Preferences.systemRoot().node( n ) ;
	} 

	public java.util.prefs.Preferences getPackageNode( String instance, String qualifier ) {        
		java.lang.Package p = this.getClass().getPackage() ;
		String i = instance != null ? "/"+instance : "" ;
		String q = qualifier != null ? "/"+qualifier : "" ;
		String d, n ;

		if ( p != null ) {
			d = "/"+p.getName().replaceAll( "\\.", "/" ) ;
			n = d+i+q ;
			try {
				if ( ! java.util.prefs.Preferences.systemRoot().nodeExists( n ) ) {
					n = d+q ;
				}
			} catch ( java.util.prefs.BackingStoreException e ) {}
		} else {
			d = "/"+"default" ;
			n = d+i+q ;
			try {
				if ( ! java.util.prefs.Preferences.systemRoot().nodeExists( n ) ) {
					n = d+q ;
				}
			} catch ( java.util.prefs.BackingStoreException e ) {}
		}

		return java.util.prefs.Preferences.systemRoot().node( n ) ;
	} 
}
