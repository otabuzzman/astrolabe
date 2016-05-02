
package astrolabe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

@SuppressWarnings("serial")
public class Astrolabe extends astrolabe.model.Astrolabe implements PostscriptEmitter {

	// configuration key (CK_)
	private final static String CK_VIEWER = "viewer" ;

	public Astrolabe() {
		String pn ;
		FileInputStream pr ;
		Locale dl ;
		String ln, lc[] ;

		try {
			pn = ApplicationConstant.GC_APPLICATION ;
			pn = pn+".preferences" ;
			pr = new FileInputStream( pn ) ;
			Preferences.importPreferences( pr ) ;
		} catch ( FileNotFoundException e ) {
			throw new RuntimeException( e.toString() ) ;
		} catch ( IOException e ) {
			throw new RuntimeException( e.toString() ) ;
		} catch ( InvalidPreferencesFormatException e ) {
			throw new RuntimeException( e.toString() ) ;
		}

		ln = getLocale() ;
		if ( ln != null ) {			
			lc = ln.split( "_" ) ;
			dl = new Locale( lc[0], lc[1] ) ;

			Locale.setDefault( dl ) ;
		}
	}

	public void register() {
		double jd ;

		jd = ApplicationFactory.valueOf( getEpoch() ) ;
		Registry.register( ApplicationConstant.GC_EPOCH, jd ) ;
	}

	public void headPS( ApplicationPostscriptStream ps ) {
		ps.emitDSCHeader() ;
		ps.emitDSCProlog() ;
	}

	public void emitPS( ApplicationPostscriptStream ps ) {
		for ( int ch=0 ; ch<getChartCount() ; ch++ ) {				
			PostscriptEmitter chart ;

			chart = ApplicationFactory.companionOf( getChart( ch ) ) ;

			ps.operator.gsave() ;

			chart.headPS( ps ) ;
			chart.emitPS( ps ) ;
			chart.tailPS( ps ) ;

			ps.operator.grestore() ;
		}
	}

	public void tailPS( ApplicationPostscriptStream ps ) {
		ps.emitDSCTrailer() ;
	}

	public static void main( String[] argv ) {
		File f ;
		FileInputStream s ;
		InputStreamReader r ;
		Astrolabe astrolabe ;
		String viewerDecl ;
		Process viewerProc ;
		TeeOutputStream out ;
		ApplicationPostscriptStream ps ;

		try {
			f = new File( argv[0] ) ;
			s = new FileInputStream( f ) ;
			r = new InputStreamReader( s, "UTF-8" ) ;
			astrolabe = new AstrolabeReader().read( r ) ;

			out =  new TeeOutputStream( System.out ) ;

			// Configuration.verbose() ;
			// ApplicationResource.verbose() ;

			viewerDecl = Configuration.getValue(
					Astrolabe.class, CK_VIEWER, null ) ;
			if ( viewerDecl == null ) {
				viewerProc = null ;
			} else {
				try {
					viewerProc = Runtime.getRuntime().exec( viewerDecl.trim().split( "\\p{Space}+" ) ) ;

					viewerProc.getInputStream().close() ;
					viewerProc.getErrorStream().close() ;

					out.add( viewerProc.getOutputStream() ) ;
				} catch ( IOException e ) {
					viewerDecl = null ;
					viewerProc = null ;
				}
			}

			ps = new ApplicationPostscriptStream( out ) ;

			astrolabe.headPS( ps ) ;
			astrolabe.emitPS( ps ) ;
			astrolabe.tailPS( ps ) ;

			ps.flush() ;
			ps.flush() ;
			ps.close() ;

			if ( viewerDecl != null )
				viewerProc.waitFor() ;

			Registry.remove() ;
		} catch ( Exception e ) {
			e.printStackTrace() ;
			System.exit( 1 ) ;
		}

		System.exit( 0 ) ;
	} 

	static {
		System.loadLibrary( ApplicationConstant.GC_NATLIB_CAA ) ;
	}
}
