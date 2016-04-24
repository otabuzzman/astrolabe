
package astrolabe;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

import caa.CAADate;

public class Astrolabe {

	public static final String application = "astrolabe" ;

	private static ResourceBundle messages ;

	private static CAADate epoch ;

	private astrolabe.model.AstrolabeType alT ;

	public Astrolabe( astrolabe.model.AstrolabeType alT ) throws ParameterNotValidException {
		String pn ;
		String ln, lc[] ;
		String key ;
		java.io.FileInputStream pr ;

		this.alT = alT ;

		try {
			pn = alT.getName() ;
			if ( pn == null ) {
				pn = application ;
			}
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

		ln = alT.getLocale() ;
		if ( ln == null ) {
			messages = ResourceBundle.getBundle( application, Locale.getDefault() ) ;
		} else {
			lc = ln.split( "_" ) ;
			messages = ResourceBundle.getBundle( application, new Locale( lc[0], lc[1] ) ) ;
		}

		try {
			epoch = AstrolabeFactory.valueOf( alT.getEpoch() ) ;
			key = Astrolabe.getLocalizedString( ApplicationConstant.LK_ASTROLABE_EPOCH ) ;
			ApplicationHelper.registerYMD( key, epoch ) ;
		} catch ( ParameterNotValidException e ) {}
	}

	public PostscriptStream initPS( java.io.PrintStream ps ) throws ParameterNotValidException {
		return new PostscriptStream( new PrintStream( ps ) ) ;
	}

	public void emitPS( PostscriptStream ps ) throws ParameterNotValidException {
		ps.emitDSCHeader() ; // DSCHeader.ps
		ps.emitDSCProlog() ; // DSCProlog.ps

		// Chart processing.
		for ( int ch=0 ; ch<alT.getChartCount() ; ch++ ) {				
			astrolabe.model.ChartType chT ;
			Chart chart ;

			chT = AstrolabeFactory.getChartType( alT.getChart( ch ) ) ;
			chart = AstrolabeFactory.createChart( alT.getChart( ch ), ps) ;
			chart.emitPS( ps ) ;

			ps.dsc.page( chT.getName(), ch ) ;

			// Horizon processing.
			for ( int ho=0 ; ho<chT.getHorizonCount() ; ho++ ) {
				astrolabe.model.HorizonType hoT ;
				Horizon horizon ;

				ps.operator.gsave() ;

				hoT = AstrolabeFactory.getHorizonType( chT.getHorizon( ho ) ) ;
				horizon = AstrolabeFactory.createHorizon( chT.getHorizon( ho ) ) ;
				horizon.initPS( ps ) ;

				// Circle processing.
				for ( int cl=0 ; cl<hoT.getCircleCount() ; cl++ ) {
					astrolabe.model.CircleType clT ;
					Circle circle ;

					ps.operator.gsave() ;

					clT = AstrolabeFactory.getCircleType(hoT.getCircle( cl ) ) ;
					circle = AstrolabeFactory.createCircle( hoT.getCircle( cl ), chart, horizon) ;

					try {
						new Registry().register( clT.getName(), circle ) ;
					} catch ( ParameterNotValidException e ) {}

					circle.initPS( ps ) ;

					java.util.Vector<astrolabe.Vector> vV ;
					java.util.Vector<double[]> vD ;

					vV = circle.cartesianList() ;
					vD = ApplicationHelper.convertCartesianVectorToDouble( vV ) ;
					ps.polyline( vD ) ;
					ps.operator.stroke() ;

					// Circle annotation processing.
					for ( int an=0 ; an<clT.getAnnotationCount() ; an++ ) {
						Annotation annotation = null ;

						ps.operator.gsave() ;

						annotation = AstrolabeFactory.createAnnotation( clT.getAnnotation( an ) ) ;
						annotation.emitPS( ps ) ;

						ps.operator.grestore() ;
					}

					// Dial processing.
					try {
						astrolabe.model.DialType dlT ;
						Dial dial ;

						ps.operator.gsave() ;

						dlT = AstrolabeFactory.getDialType( clT.getDial() ) ;
						dial = AstrolabeFactory.createDial( clT.getDial(), circle ) ;
						dial.emitPS( ps ) ;

						// Dial annotation processing.
						for ( int an=0 ; an<dlT.getAnnotationCount() ; an++ ) {
							Annotation annotation = null ;

							ps.operator.gsave() ;

							annotation = AstrolabeFactory.createAnnotation( dlT.getAnnotation( an ) ) ;
							annotation.emitPS( ps ) ;

							ps.operator.grestore() ;
						}

						ps.operator.grestore() ;
					} catch ( NullPointerException e ) {}

					ps.operator.grestore() ;
				} // Circle processing.

				ps.operator.grestore() ;
			} // Horizon processing.

			ps.operator.showpage() ;
			ps.dsc.pageTrailer() ;

			chart.rollup( ps ) ;
		} // Chart processing.

		ps.emitDSCTrailer() ; // DSCTrailer.ps

		ps.close();
	}

	public static String getLocalizedString( String key ) throws ParameterNotValidException {
		String value ;

		try {
			value = messages.getString( key ) ;
		} catch ( MissingResourceException e ) {
			throw new ParameterNotValidException() ;
		}

		return value ;
	}

	public static CAADate getEpoch() {
		return epoch ;
	}
}
