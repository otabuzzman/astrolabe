
package astrolabe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

import caa.CAADate ;

@SuppressWarnings("serial")
public class Astrolabe extends astrolabe.model.Astrolabe implements PostscriptEmitter {

	public Astrolabe( Peer peer ) {
		String pn ;
		FileInputStream pr ;
		Locale dl ;
		String ln, lc[] ;
		double epoch ;
		CAADate d ;
		String key ;

		peer.setupCompanion( this ) ;

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

		epoch = AstrolabeFactory.valueOf( getEpoch() ) ;
		d = new CAADate( epoch, true ) ;

		Registry.registerNumber( ApplicationConstant.GC_EPOCH, d.Julian() ) ;

		key = MessageCatalog.message( ApplicationConstant.GC_APPLICATION, ApplicationConstant.LK_ASTROLABE_EPOCH ) ;
		AstrolabeRegistry.registerYMD( key, d ) ;

		d.delete() ;
	}

	public void headPS( AstrolabePostscriptStream ps ) {
		ps.emitDSCHeader() ;
		ps.emitDSCProlog() ;
	}

	public void emitPS( AstrolabePostscriptStream ps ) {
		for ( int ch=0 ; ch<getChartCount() ; ch++ ) {				
			PostscriptEmitter chart ;

			chart = AstrolabeFactory.companionOf( getChart( ch ) ) ;

			ps.operator.gsave() ;

			chart.headPS( ps ) ;
			chart.emitPS( ps ) ;
			chart.tailPS( ps ) ;

			ps.operator.grestore() ;
		}
	}

	public void tailPS( AstrolabePostscriptStream ps ) {
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
		AstrolabePostscriptStream ps ;

		try {
			f = new File( argv[0] ) ;
			s = new FileInputStream( f ) ;
			r = new InputStreamReader( s, "UTF-8" ) ;
			astrolabe = new AstrolabeReader().read( r ) ;

			out =  new TeeOutputStream( System.out ) ;

			viewerDecl = Configuration.getValue(
					Configuration.getClassNode( Astrolabe.class, astrolabe.getName(), null ),
					ApplicationConstant.PK_ASTROLABE_VIEWER, null ) ;
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

			ps = new AstrolabePostscriptStream( out ) ;

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
