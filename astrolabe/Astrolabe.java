
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

		Registry.register( ApplicationConstant.GC_EPOCHE, new Double( d.Julian() ) ) ;

		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ASTROLABE_EPOCH ) ;
		ApplicationHelper.registerYMD( key, d ) ;

		d.delete() ;
	}

	public void headPS( PostscriptStream ps ) {
		ps.emitDSCHeader() ;
		ps.emitDSCProlog() ;
	}

	public void emitPS( PostscriptStream ps ) {
		// Atlas processing.
		for ( int at=0 ; at<getAtlasCount() ; at++ ) {
			Atlas atlas ;

			ps.operator.gsave() ;

			try {
				atlas = AstrolabeFactory.companionOf( getAtlas( at ) ) ;
			} catch ( ParameterNotValidException e ) {
				throw new RuntimeException( e.toString() ) ;
			}

			for ( astrolabe.model.Chart atlasPage : atlas.toModel() ) {
				addChart( atlasPage ) ;
			}

			ps.operator.gsave() ;

			atlas.headAUX() ;
			atlas.emitAUX() ;
			atlas.tailAUX() ;
		}

		// Chart processing.
		for ( int ch=0 ; ch<getChartCount() ; ch++ ) {				
			PostscriptEmitter chart ;

			ps.operator.gsave() ;

			try {
				chart = AstrolabeFactory.companionOf( getChart( ch ) ) ;
			} catch ( ParameterNotValidException e ) {
				throw new RuntimeException( e.toString() ) ;
			}

			chart.headPS( ps ) ;
			chart.emitPS( ps ) ;

			// Horizon processing.
			for ( int ho=0 ; ho<( (astrolabe.model.ChartAzimuthalType) chart ).getHorizonCount() ; ho++ ) {
				PostscriptEmitter horizon ;

				ps.operator.gsave() ;

				try {
					astrolabe.model.Horizon modelHorizon = ( (astrolabe.model.ChartAzimuthalType) chart ).getHorizon( ho ) ;
					horizon = AstrolabeFactory.companionOf( modelHorizon, epoch, (Projector) chart ) ;
				} catch ( ParameterNotValidException e ) {
					throw new RuntimeException( e.toString() ) ;
				}

				horizon.headPS( ps ) ;
				horizon.emitPS( ps ) ;

				// AnnotationStraight processing.
				for ( int an=0 ; an<( (astrolabe.model.HorizonType) horizon ).getAnnotationStraightCount() ; an++ ) {
					PostscriptEmitter annotation ;

					ps.operator.gsave() ;

					try {
						annotation = new AnnotationStraight( ( (astrolabe.model.HorizonType) horizon ).getAnnotationStraight( an ) ) ;
					} catch ( ParameterNotValidException e ) {
						throw new RuntimeException( e.toString() ) ;
					}

					annotation.headPS( ps ) ;
					annotation.emitPS( ps ) ;
					annotation.tailPS( ps ) ;

					ps.operator.grestore() ;
				}

				// Circle processing.
				for ( int cl=0 ; cl<( (astrolabe.model.HorizonType) horizon ).getCircleCount() ; cl++ ) {
					PostscriptEmitter circle ;

					ps.operator.gsave() ;

					try {
						astrolabe.model.Circle modelCircle = ( (astrolabe.model.HorizonType) horizon ).getCircle( cl ) ;
						circle = AstrolabeFactory.companionOf( modelCircle, epoch, (Projector) horizon ) ;
					} catch ( ParameterNotValidException e ) {
						throw new RuntimeException( e.toString() ) ;
					}

					circle.headPS( ps ) ;
					circle.emitPS( ps ) ;
					circle.tailPS( ps ) ;

					ps.operator.grestore() ;
				} // Circle processing.

				// Body processing
				try {
					for ( int bd=0 ; bd<( (astrolabe.model.HorizonType) horizon ).getBodyCount() ; bd++ ) {
						PostscriptEmitter body ;

						ps.operator.gsave() ;

						astrolabe.model.Body modelBody = ( (astrolabe.model.HorizonType) horizon ).getBody( bd ) ;
						body = AstrolabeFactory.companionOf( modelBody, (Projector) horizon, epoch ) ;

						body.headPS( ps ) ;
						body.emitPS( ps ) ;
						body.tailPS( ps ) ;

						ps.operator.grestore() ;
					}
				} catch ( ParameterNotValidException e ) {
					throw new RuntimeException( e.toString() ) ;
				}

				// Catalog processing
				try {
					for ( int ct=0 ; ct<( (astrolabe.model.HorizonType) horizon ).getCatalogCount() ; ct++ ) {
						PostscriptEmitter catalog ;

						ps.operator.gsave() ;

						astrolabe.model.Catalog modelCatalog = ( (astrolabe.model.HorizonType) horizon ).getCatalog( ct ) ;
						catalog = AstrolabeFactory.companionOf( modelCatalog, (Projector) horizon, epoch ) ;

						catalog.headPS( ps ) ;
						catalog.emitPS( ps ) ;
						catalog.tailPS( ps ) ;

						ps.operator.grestore() ;
					}
				} catch ( ParameterNotValidException e ) {
					throw new RuntimeException( e.toString() ) ;
				}

				horizon.tailPS( ps ) ;

				ps.operator.grestore() ;
			} // Horizon processing.

			chart.tailPS( ps ) ;

			ps.operator.grestore() ;
		} // Chart processing.
	}

	public void tailPS( PostscriptStream ps ) {
		ps.emitDSCTrailer() ;
	}

	public static void main( String[] argv ) {
		File f ;
		FileInputStream s ;
		InputStreamReader r ;
		Astrolabe a ;
		PostscriptStream ps ;

		try {
			f = new File( argv[0] ) ;
			s = new FileInputStream( f ) ;
			r = new InputStreamReader( s, "UTF-8" ) ;
			a = new AstrolabeReader().read( r ) ;

			ps = new PostscriptStream( System.out, a.getName() ) ;

			a.headPS( ps ) ;
			a.emitPS( ps ) ;
			a.tailPS( ps ) ;

			ps.close() ;

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
