
package astrolabe;

import astrolabe.model.AngleType;
import astrolabe.model.CalendarType;
import astrolabe.model.CartesianType;
import astrolabe.model.DateType;
import astrolabe.model.DMSType;
import astrolabe.model.HMSType;
import astrolabe.model.RationalType;
import astrolabe.model.PolarType;
import astrolabe.model.SphericalType;
import astrolabe.model.TimeType;
import astrolabe.model.YMDType;

import caa.CAACoordinateTransformation;
import caa.CAADate;

public final class AstrolabeFactory {

	private AstrolabeFactory() {
	}

	public static Chart companionOf( astrolabe.model.Chart ch ) throws ParameterNotValidException {
		astrolabe.model.ChartStereographic chS ;
		astrolabe.model.ChartOrthographic chO ;
		astrolabe.model.ChartEquidistant chE ;
		astrolabe.model.ChartGnomonic chG ;
		Chart chart ;

		if ( ( chS = ch.getChartStereographic() ) != null ) {
			chart = new ChartStereographic( chS ) ;
		} else if ( ( chO = ch.getChartOrthographic() ) != null ) {
			chart = new ChartOrthographic( chO ) ;
		} else if ( ( chE = ch.getChartEquidistant() ) != null ) {
			chart = new ChartEquidistant( chE ) ;
		} else if ( ( chG = ch.getChartGnomonic() ) != null ) {
			chart = new ChartGnomonic( chG ) ;
		} else { // ch.getChartEqualarea() != null
			chart = new ChartEqualarea( ch.getChartEqualarea() ) ;
		}
		return chart ;
	}

	public static Horizon companionOf( astrolabe.model.Horizon ho, double epoch, Projector p ) throws ParameterNotValidException {
		astrolabe.model.HorizonLocal hoLo ;
		astrolabe.model.HorizonEquatorial hoEq ;
		astrolabe.model.HorizonEcliptical hoEc ;
		Horizon horizon ;

		if ( ( hoLo = ho.getHorizonLocal() ) != null  ) {
			horizon = new HorizonLocal( hoLo, epoch, p ) ;
		} else if ( ( hoEq = ho.getHorizonEquatorial() ) != null  ) {
			horizon = new HorizonEquatorial( hoEq, p ) ;
		} else if ( ( hoEc = ho.getHorizonEcliptical() ) != null  ) {
			horizon = new HorizonEcliptical( hoEc, epoch, p ) ;
		} else { // ho.getHorizonGalactic() != null
			horizon = new HorizonGalactic( ho.getHorizonGalactic(), p ) ;
		}

		return horizon ;
	}

	public static Circle companionOf( astrolabe.model.Circle cl, double epoch, Projector p ) throws ParameterNotValidException {
		astrolabe.model.CircleParallel clP ;
		astrolabe.model.CircleMeridian clM ;
		astrolabe.model.CircleSouthernPolar clSP ;
		astrolabe.model.CircleNorthernPolar clNP ;
		astrolabe.model.CircleSouthernTropic clST ;
		Circle circle ;

		if ( ( clP = cl.getCircleParallel() ) != null ) {
			circle = new CircleParallel( clP, p ) ;
		} else if ( ( clM = cl.getCircleMeridian() ) != null ) {
			circle = new CircleMeridian( clM, p ) ;
		} else if ( ( clSP = cl.getCircleSouthernPolar() ) != null ) {
			circle = new CircleSouthernPolar( clSP, epoch, p ) ;
		} else if ( ( clNP = cl.getCircleNorthernPolar() ) != null ) {
			circle = new CircleNorthernPolar( clNP, epoch, p ) ;
		} else if ( ( clST = cl.getCircleSouthernTropic() ) != null ) {
			circle = new CircleSouthernTropic( clST, epoch, p ) ;
		} else { // cl.getCircleNorthernTropic() != null
			circle = new CircleNorthernTropic( cl.getCircleNorthernTropic(), epoch, p ) ;
		}

		return circle ;
	}

	public static Dial companionOf( astrolabe.model.Dial dl, Baseline baseline ) throws ParameterNotValidException {
		astrolabe.model.DialDegree dlD ;
		Dial dial ;

		if ( ( dlD = dl.getDialDegree() ) != null ) {
			dial = new DialDegree( dlD, baseline ) ;
		} else { // dl.getDialHour() != null
			dial = new DialHour( dl.getDialHour(), baseline ) ;
		}

		return dial ;
	}

	public static Annotation companionOf( astrolabe.model.Annotation an ) throws ParameterNotValidException {
		astrolabe.model.AnnotationStraight anS ;
		Annotation annotation ;

		if ( ( anS = an.getAnnotationStraight() ) != null ) {
			annotation = new AnnotationStraight( anS ) ;
		} else { // an.getAnnotationCurved() != null
			annotation = new AnnotationCurved( an.getAnnotationCurved() ) ;
		}

		return annotation ;
	}

	public static Body companionOf( astrolabe.model.Body bd, Projector p, double epoch ) throws ParameterNotValidException {
		astrolabe.model.BodyStellar bdS ;
		astrolabe.model.BodyAreal bdA ;
		astrolabe.model.BodyPlanet bdP ;
		astrolabe.model.BodyMoon bdM ;
		astrolabe.model.BodySun bdH ;
		astrolabe.model.BodyElliptical bdE ;
		Body body ;

		if ( ( bdS = bd.getBodyStellar() ) != null ) {
			body = new BodyStellar( bdS, p ) ;
		} else if ( ( bdA = bd.getBodyAreal() ) != null ) {
			body = new BodyAreal( bdA, p ) ;
		} else if ( ( bdP = bd.getBodyPlanet() ) != null ) {
			body = new BodyPlanet( bdP, epoch, p ) ;
		} else if ( ( bdM = bd.getBodyMoon() ) != null ) {
			body = new BodyMoon( bdM, epoch, p ) ;
		} else if ( ( bdH = bd.getBodySun() ) != null ) {
			body = new BodySun( bdH, epoch, p ) ;
		} else if ( ( bdE = bd.getBodyElliptical() ) != null ) {
			body = new BodyElliptical( bdE, p ) ;
		} else { // bd.getBodyParabolic() != null
			body = new BodyParabolic( bd.getBodyParabolic(), p ) ;
		}

		return body ;
	}

	public static Catalog companionOf( astrolabe.model.Catalog ct, Projector p, double epoch ) throws ParameterNotValidException {
		astrolabe.model.CatalogADC1239H ct1239h ;
		astrolabe.model.CatalogADC1239T ct1239t ;
		astrolabe.model.CatalogADC5050 ct5050 ;
		astrolabe.model.CatalogADC6049 ct6049 ;
		Catalog catalog ;

		if ( ( ct1239h = ct.getCatalogADC1239H() ) != null ) {
			catalog = new CatalogADC1239H( ct1239h, p, epoch ) ;
		} else if ( ( ct1239t = ct.getCatalogADC1239T() ) != null ) {
			catalog = new CatalogADC1239T( ct1239t, p, epoch ) ;
		} else if ( ( ct5050 = ct.getCatalogADC5050() ) != null ) {
			catalog = new CatalogADC5050( ct5050, p, epoch ) ;
		} else if ( ( ct6049 = ct.getCatalogADC6049() ) != null ) {
			catalog = new CatalogADC6049( ct6049, p, epoch ) ;
		} else { // // ct.getCatalogADC7118() != null
			catalog = new CatalogADC7118( ct.getCatalogADC7118(), p, epoch ) ;
		}

		return catalog ;
	}

	public static astrolabe.model.Position modelPosition( double phi, double theta ) {
		astrolabe.model.Position p = new astrolabe.model.Position() ;

		modelSphericalType( p, 1, phi, theta ) ;

		return p ;
	}

	public static astrolabe.model.SphericalType modelSphericalType( double r, double phi, double theta ) {
		astrolabe.model.SphericalType sT = new astrolabe.model.SphericalType() ;

		modelSphericalType( sT, r, phi, theta ) ;

		return sT ;
	}

	private static void modelSphericalType( astrolabe.model.SphericalType sT, double r, double phi, double theta ) {
		sT.setR( new astrolabe.model.R() ) ;
		sT.getR().setValue( r ) ;
		sT.setPhi( new astrolabe.model.Phi() ) ;
		sT.getPhi().setRational( new astrolabe.model.Rational() ) ;
		sT.getPhi().getRational().setValue( phi ) ;
		sT.setTheta( new astrolabe.model.Theta() ) ;
		sT.getTheta().setRational( new astrolabe.model.Rational() ) ;
		sT.getTheta().getRational().setValue( theta ) ;
	}

	public static double valueOf( DateType date ) throws ParameterNotValidException {
		double r ;

		if ( date == null ) {
			throw new ParameterNotValidException( DateType.class.toString()+":"+null ) ;
		}

		if ( date.getJD() == null ) {
			r = AstrolabeFactory.valueOf( date.getCalendar() ) ;
		} else {
			r = AstrolabeFactory.valueOf( date.getJD() ) ;
		}

		return r ;
	}

	public static double valueOf( CalendarType calendar ) throws ParameterNotValidException {
		double r ;
		CAADate d ;
		long[] c ;
		double t ;

		if ( calendar == null ) {
			throw new ParameterNotValidException( CalendarType.class.toString()+":"+null ) ;
		}

		c = AstrolabeFactory.valueOf( calendar.getYMD() ) ;

		if ( calendar.getTime() == null ) {
			t = 0 ;
		} else {
			t = CAACoordinateTransformation.RadiansToHours( AstrolabeFactory.valueOf( calendar.getTime() ) ) ;
		}

		d = new CAADate( c[0], c[1], c[2]+t/24, true ) ;
		r = d.Julian() ;
		d.delete() ;

		return r ;
	}

	public static double valueOf( TimeType time ) throws ParameterNotValidException {
		double r = 0 ;

		if ( time == null ) {
			throw new ParameterNotValidException( TimeType.class.toString()+":"+null ) ;
		}

		try {
			r = CAACoordinateTransformation.HoursToRadians( AstrolabeFactory.valueOf( time.getRational() ) ) ;
		} catch ( ParameterNotValidException e ) {
			r = AstrolabeFactory.valueOf( time.getHMS() ) ;
		}

		return r ;
	}

	public static java.util.Vector<double[]> valueOf( SphericalType[] spherical ) throws ParameterNotValidException {
		java.util.Vector<double[]> r = new java.util.Vector<double[]>() ;

		if ( spherical == null ) {
			throw new ParameterNotValidException( SphericalType[].class.toString()+":"+null ) ;
		}

		for ( int n=0 ; n<spherical.length ; n++ ) {
			r.add( valueOf( spherical[n] ) ) ;
		}

		return r ;
	}

	public static double[] valueOf( SphericalType spherical ) throws ParameterNotValidException {
		double[] r = { 1, 0, 0 } ;

		if ( spherical == null ) {
			throw new ParameterNotValidException( SphericalType.class.toString()+":"+null ) ;
		}

		r[1] = AstrolabeFactory.valueOf( spherical.getPhi() ) ;
		r[2] = AstrolabeFactory.valueOf( spherical.getTheta() ) ;

		try {
			r[0] = valueOf( spherical.getR() ) ;
		} catch ( ParameterNotValidException e ) {} // optional

		return r ;
	}

	public static double[] valueOf( PolarType polar ) throws ParameterNotValidException {
		double[] r = { 1, 0 } ;

		if ( polar == null ) {
			throw new ParameterNotValidException( PolarType.class.toString()+":"+null ) ;
		}

		r[1] = AstrolabeFactory.valueOf( polar.getPhi() ) ;

		try {
			r[0] = AstrolabeFactory.valueOf( polar.getR() ) ;
		} catch ( ParameterNotValidException e ) {} // optional

		return r ;
	}

	public static double[] valueOf( CartesianType cartesian ) throws ParameterNotValidException {
		if ( cartesian == null ) {
			throw new ParameterNotValidException( CartesianType.class.toString()+":"+null ) ;
		}

		return new double[] { cartesian.getX(), cartesian.getY(), cartesian.hasZ()?cartesian.getZ():0 } ;
	}

	public static double valueOf( AngleType angle ) throws ParameterNotValidException {
		double r ;

		if ( angle == null ) {
			throw new ParameterNotValidException( AngleType.class.toString()+":"+null ) ;
		}

		try {
			r = CAACoordinateTransformation.DegreesToRadians( AstrolabeFactory.valueOf( angle.getRational() ) ) ;
		} catch ( ParameterNotValidException e ) {
			r = AstrolabeFactory.valueOf( angle.getDMS() ) ;
		}

		return r ;
	}

	public static long[] valueOf( YMDType ymd ) throws ParameterNotValidException {
		if ( ymd == null ) {
			throw new ParameterNotValidException( YMDType.class.toString()+":"+null ) ;
		}

		return new long[] { ymd.getY(), ymd.getM(), ymd.getD() } ;
	}

	public static double valueOf( DMSType dms ) throws ParameterNotValidException {
		if ( dms == null ) {
			throw new ParameterNotValidException( DMSType.class.toString()+":"+null ) ;
		}

		return CAACoordinateTransformation.DegreesToRadians( dms.getDeg()+dms.getMin()/60.+dms.getSec()/3600 ) ;
	}

	public static double valueOf( HMSType hms ) throws ParameterNotValidException {
		if ( hms == null ) {
			throw new ParameterNotValidException( HMSType.class.toString()+":"+null ) ;
		}

		return CAACoordinateTransformation.HoursToRadians( hms.getHrs()+hms.getMin()/60.+hms.getSec()/3600 ) ;
	}

	public static double valueOf( RationalType rational ) throws ParameterNotValidException {
		if ( rational == null ) {
			throw new ParameterNotValidException( RationalType.class.toString()+":"+null ) ;
		}

		return rational.getValue() ;
	} 
}
