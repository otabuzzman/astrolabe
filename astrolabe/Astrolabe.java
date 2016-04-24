
package astrolabe;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

import caa.CAADate ;

@SuppressWarnings("serial")
public class Astrolabe extends astrolabe.model.Astrolabe {

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
			pn = ApplicationConstant.GC_APPLICATION ;
			pn = pn+".preferences" ;
			pr = new FileInputStream( pn ) ;
			Preferences.importPreferences( pr ) ;
		} catch ( FileNotFoundException e ) {
			throw new ParameterNotValidException() ;
		} catch ( IOException e ) {
			throw new ParameterNotValidException() ;
		} catch ( InvalidPreferencesFormatException e ) {
			throw new ParameterNotValidException() ;
		}

		ln = getLocale() ;
		if ( ln != null ) {			
			lc = ln.split( "_" ) ;
			dl = new Locale( lc[0], lc[1] ) ;

			Locale.setDefault( dl ) ;
		}

		try {
			epoch = AstrolabeFactory.valueOf( getEpoch() ) ;
			d = new CAADate( epoch, true ) ;

			key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ASTROLABE_EPOCH ) ;
			ApplicationHelper.registerYMD( key, d ) ;

			d.delete() ;
		} catch ( ParameterNotValidException e ) {}
	}

	static public PostscriptStream initPS() {
		return initPS( System.out ) ;
	}

	static public PostscriptStream initPS( java.io.PrintStream ps ) {
		return new PostscriptStream( ps ) ;
	}

	public void emitPS( PostscriptStream ps ) {
		ps.emitDSCHeader() ;
		ps.emitDSCProlog() ;

		// Chart processing.
		for ( int ch=0 ; ch<getChartCount() ; ch++ ) {				
			Chart chart ;

			try {
				chart = AstrolabeFactory.createChart( getChart( ch ) ) ;
			} catch ( ParameterNotValidException e ) {
				break ; // if ch unequal 0 there must be a chart
			}
			chart.headPS( ps ) ;

			// Horizon processing.
			for ( int ho=0 ; ho<chart.getHorizonCount() ; ho++ ) {
				Horizon horizon ;

				ps.operator.gsave() ;

				try {
					horizon = AstrolabeFactory.createHorizon( chart.getHorizon( ho ), epoch, chart ) ;
				} catch ( ParameterNotValidException e ) {
					break ; // if ho unequal 0 there must be a horizon
				}
				horizon.headPS( ps ) ;

				// Circle processing.
				for ( int cl=0 ; cl<horizon.getCircleCount() ; cl++ ) {
					Circle circle ;

					ps.operator.gsave() ;
					try {
						circle = AstrolabeFactory.createCircle( horizon.getCircle( cl ), epoch, horizon ) ;
					} catch ( ParameterNotValidException e ) {
						break ; // if cl unequal 0 there must be a circle
					}

					try {
						new Registry().register( circle.getName(), circle ) ;
					} catch ( ParameterNotValidException e ) {}

					circle.headPS( ps ) ;
					circle.emitPS( ps ) ;

					// Dial processing.
					try {
						Dial dial ;

						ps.operator.gsave() ;

						dial = AstrolabeFactory.createDial( circle.getDial(), epoch, circle ) ;
						dial.headPS( ps ) ;
						dial.emitPS( ps ) ;
						dial.tailPS( ps ) ;

						// Dial annotation processing.
						ApplicationHelper.emitPS( ps, dial.getAnnotation() ) ;

						ps.operator.grestore() ;
					} catch ( ParameterNotValidException e ) {} // optional

					circle.tailPS( ps ) ;

					ps.operator.grestore() ;
				} // Circle processing.

				// Body processing
				try {
					ApplicationHelper.emitPS( ps, horizon.getBodyStellar(), horizon ) ;
				} catch ( ParameterNotValidException e ) {} // optional

				horizon.tailPS( ps ) ;

				ps.operator.grestore() ;
			} // Horizon processing.

			chart.tailPS( ps ) ;
		} // Chart processing.

		ps.emitDSCTrailer() ;
	}

	static {
		System.loadLibrary( ApplicationConstant.GC_NATLIB_CAA ) ;
	}
}
