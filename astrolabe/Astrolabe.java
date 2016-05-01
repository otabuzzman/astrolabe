
package astrolabe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

import org.exolab.castor.xml.ValidationException;

import caa.CAADate ;

@SuppressWarnings("serial")
public class Astrolabe extends astrolabe.model.Astrolabe implements PostscriptEmitter {

	private double epoch ;

	public Astrolabe( Object peer ) throws ParameterNotValidException {
		String pn ;
		FileInputStream pr ;
		Locale dl ;
		String ln, lc[] ;
		CAADate d ;
		String key ;

		ApplicationHelper.setupCompanionFromPeer( this, peer ) ;
		try {
			validate() ;
		} catch ( ValidationException e ) {
			throw new ParameterNotValidException( e.toString() ) ;
		}

		try {
			pn = ApplicationConstant.GC_APPLICATION ;
			pn = pn+".preferences" ;
			pr = new FileInputStream( pn ) ;
			Preferences.importPreferences( pr ) ;
		} catch ( FileNotFoundException e ) {
			throw new ParameterNotValidException( e.toString() ) ;
		} catch ( IOException e ) {
			throw new RuntimeException( e.toString() ) ;
		} catch ( InvalidPreferencesFormatException e ) {
			throw new ParameterNotValidException( e.toString() ) ;
		}

		ln = getLocale() ;
		if ( ln != null ) {			
			lc = ln.split( "_" ) ;
			dl = new Locale( lc[0], lc[1] ) ;

			Locale.setDefault( dl ) ;
		}

		epoch = AstrolabeFactory.valueOf( getEpoch() ) ;
		d = new CAADate( epoch, true ) ;

		ApplicationHelper.registerNumber( ApplicationConstant.GC_EPOCHE, d.Julian() ) ;

		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ASTROLABE_EPOCH ) ;
		ApplicationHelper.registerYMD( key, d ) ;

		d.delete() ;
	}

	public void headPS( PostscriptStream ps ) {
		ps.emitDSCHeader() ;
		ps.emitDSCProlog() ;
	}

	public void emitPS( PostscriptStream ps ) {
		for ( int at=0 ; at<getAtlasCount() ; at++ ) {
			Atlas atlas ;

			try {
				atlas = AstrolabeFactory.companionOf( getAtlas( at ) ) ;
			} catch ( ParameterNotValidException e ) {
				throw new RuntimeException( e.toString() ) ;
			}

			// Atlas interface
			atlas.addAllAtlasPage() ;
			for ( astrolabe.model.Chart atlasPage : atlas.toModel() ) {
				addChart( atlasPage ) ;
			}

			// AuxiliaryEmitter interface
			atlas.headAUX() ;
			atlas.emitAUX() ;
			atlas.tailAUX() ;

			// PostscriptEmitter interface
			ps.operator.gsave() ;

			atlas.headPS( ps ) ;
			atlas.emitPS( ps ) ;
			atlas.tailPS( ps ) ;

			ps.operator.grestore() ;
		}

		for ( int ch=0 ; ch<getChartCount() ; ch++ ) {				
			PostscriptEmitter chart ;

			try {
				chart = AstrolabeFactory.companionOf( getChart( ch ) ) ;
			} catch ( ParameterNotValidException e ) {
				throw new RuntimeException( e.toString() ) ;
			}

			ps.operator.gsave() ;

			chart.headPS( ps ) ;
			chart.emitPS( ps ) ;
			chart.tailPS( ps ) ;

			ps.operator.grestore() ;
		}
	}

	public void tailPS( PostscriptStream ps ) {
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
		PostscriptStream ps ;

		try {
			f = new File( argv[0] ) ;
			s = new FileInputStream( f ) ;
			r = new InputStreamReader( s, "UTF-8" ) ;
			astrolabe = new AstrolabeReader().read( r ) ;

			out =  new TeeOutputStream( System.out ) ;

			viewerDecl = ApplicationHelper.getPreferencesKV(
					ApplicationHelper.getClassNode( Astrolabe.class, astrolabe.getName(), null ),
					ApplicationConstant.PK_ASTROLABE_VIEWER, null ) ;
			if ( viewerDecl == null ) {
				viewerProc = null ;
			} else {
				try {
					viewerProc = Runtime.getRuntime().exec( viewerDecl.split( " " ) ) ;

					viewerProc.getInputStream().close() ;
					viewerProc.getErrorStream().close() ;

					out.add( viewerProc.getOutputStream() ) ;
				} catch ( IOException e ) {
					viewerDecl = null ;
					viewerProc = null ;
				}
			}

			ps = new PostscriptStream( out ) ;

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
