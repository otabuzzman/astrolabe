
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
	private static boolean mean ;

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

		mean = alT.getEpoch().getEcliptic().equals( ApplicationConstant.AV_ASTROLABE_ECLIPTICMEAN ) ;
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
					double[] xy ;

					vV = circle.cartesianList() ;
					vD = ApplicationHelper.convertCartesianVectorToDouble( vV ) ;
					ps.operator.mark() ;
					for ( int c=vD.size() ; c>0 ; c-- ) {
						xy = (double[]) vD.get( c-1 ) ;
						ps.push( xy[0] ) ;
						ps.push( xy[1] ) ;
					}
					ps.custom( ApplicationConstant.PS_PROLOG_POLYLINE ) ;
					ps.operator.stroke() ;

					// Circle annotation processing.
					emitPS( ps, clT.getAnnotation() ) ;

					// Dial processing.
					try {
						astrolabe.model.DialType dlT ;
						Dial dial ;

						ps.operator.gsave() ;

						dlT = AstrolabeFactory.getDialType( clT.getDial() ) ;
						dial = AstrolabeFactory.createDial( clT.getDial(), circle ) ;
						dial.emitPS( ps ) ;

						// Dial annotation processing.
						emitPS( ps, dlT.getAnnotation() ) ;

						ps.operator.grestore() ;
					} catch ( NullPointerException e ) {}

					ps.operator.grestore() ;
				} // Circle processing.

				// Body processing
				for ( int bd=0 ; bd<hoT.getBodyCount() ; bd++ ) {
					Body body ;

					ps.operator.gsave() ;

					body = new Body( hoT.getBody( bd ), chart, horizon ) ;
					body.emitPS( ps ) ;

					// Body annotation processing.
					emitPS( ps, hoT.getBody( bd ).getAnnotation() ) ;

					ps.operator.grestore() ;
				}

				ps.operator.grestore() ;
			} // Horizon processing.

			ps.operator.showpage() ;
			ps.dsc.pageTrailer() ;

			chart.rollup( ps ) ;
		} // Chart processing.

		ps.emitDSCTrailer() ; // DSCTrailer.ps

		ps.close();
	}

	private static void emitPS( PostscriptStream ps, astrolabe.model.Annotation[] an ) throws ParameterNotValidException {
		for ( int a=0 ; a<an.length ; a++ ) {
			Annotation annotation = null ;

			ps.operator.gsave() ;

			annotation = AstrolabeFactory.createAnnotation( an[a] ) ;
			annotation.emitPS( ps ) ;

			ps.operator.grestore() ;
		}
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

	public static boolean isEclipticMean() {
		return mean ;
	}

	public static boolean isEclipticTrue() {
		return ! mean ;
	}
}
