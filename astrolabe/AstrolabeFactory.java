
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

	public static Chart createChart( astrolabe.model.Chart ch ) throws ParameterNotValidException {
		astrolabe.model.ChartStereographic chS ;
		astrolabe.model.ChartOrthographic chO ;
		astrolabe.model.ChartEquidistant chE ;
		astrolabe.model.ChartGnomonic chG ;
		Chart chart ;

		if ( ch == null ) {
			throw new ParameterNotValidException() ;
		}

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

	public static Horizon createHorizon( astrolabe.model.Horizon ho, double epoch, Projector p ) throws ParameterNotValidException {
		astrolabe.model.HorizonLocal hoLo ;
		astrolabe.model.HorizonEquatorial hoEq ;
		astrolabe.model.HorizonEcliptical hoEc ;
		Horizon horizon ;

		if ( ho == null ) {
			throw new ParameterNotValidException() ;
		}

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

	public static Circle createCircle( astrolabe.model.Circle cl, double epoch, Projector p ) throws ParameterNotValidException {
		astrolabe.model.CircleParallel clP ;
		astrolabe.model.CircleMeridian clM ;
		astrolabe.model.CircleSouthernPolar clSP ;
		astrolabe.model.CircleNorthernPolar clNP ;
		astrolabe.model.CircleSouthernTropic clST ;
		Circle circle ;

		if ( cl == null ) {
			throw new ParameterNotValidException() ;
		}

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

	public static Dial createDial( astrolabe.model.Dial dl, double epoch, Circle circle ) throws ParameterNotValidException {
		astrolabe.model.DialDegree dlD ;
		astrolabe.model.DialHour dlH ;
		Dial dial ;

		if ( dl == null ) {
			throw new ParameterNotValidException() ;
		}

		if ( ( dlD = dl.getDialDegree() ) != null ) {
			dial = new DialDegree( dlD, circle ) ;
		} else if ( ( dlH = dl.getDialHour() ) != null ) {
			dial = new DialHour( dlH, circle ) ;
		} else { // dl.getDialDay() != null
			dial = new DialDay( dl.getDialDay(), epoch, circle ) ;
		}

		return dial ;
	}

	public static Annotation createAnnotation( astrolabe.model.Annotation an ) throws ParameterNotValidException {
		astrolabe.model.AnnotationStraight anS ;
		Annotation annotation ;

		if ( an == null ) {
			throw new ParameterNotValidException() ;
		}

		if ( ( anS = an.getAnnotationStraight() ) != null ) {
			annotation = new AnnotationStraight( anS ) ;
		} else { // an.getAnnotationCurved() != null
			annotation = new AnnotationCurved( an.getAnnotationCurved() ) ;
		}

		return annotation ;
	}

	public static double valueOf( DateType date ) throws ParameterNotValidException {
		double r ;

		if ( date == null ) {
			throw new ParameterNotValidException() ;
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
			throw new ParameterNotValidException() ;
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
			throw new ParameterNotValidException() ;
		}

		try {
			r = CAACoordinateTransformation.HoursToRadians( AstrolabeFactory.valueOf( time.getRational() ) ) ;
		} catch ( ParameterNotValidException e ) {
			r = AstrolabeFactory.valueOf( time.getHMS() ) ;
		}

		return r ;
	}

	public static double[] valueOf( SphericalType spherical ) throws ParameterNotValidException {
		double[] r = { 1, 0, 0 } ;

		if ( spherical == null ) {
			throw new ParameterNotValidException() ;
		}

		r[1] = AstrolabeFactory.valueOf( spherical.getPhi() ) ;
		r[2] = AstrolabeFactory.valueOf( spherical.getTheta() ) ;

		try {
			r[0] = valueOf( spherical.getR() ) ;
		} catch ( ParameterNotValidException e ) {}

		return r ;
	}

	public static double[] valueOf( PolarType polar ) throws ParameterNotValidException {
		double[] r = { 1, 0 } ;

		if ( polar == null ) {
			throw new ParameterNotValidException() ;
		}

		r[1] = AstrolabeFactory.valueOf( polar.getPhi() ) ;

		try {
			r[0] = AstrolabeFactory.valueOf( polar.getR() ) ;
		} catch ( ParameterNotValidException e ) {}

		return r ;
	}

	public static double[] valueOf( CartesianType cartesian ) throws ParameterNotValidException {
		if ( cartesian == null ) {
			throw new ParameterNotValidException() ;
		}

		return new double[] { cartesian.getX(), cartesian.getY(), cartesian.hasZ()?cartesian.getZ():0 } ;
	}

	public static double valueOf( AngleType angle ) throws ParameterNotValidException {
		double r ;

		if ( angle == null ) {
			throw new ParameterNotValidException() ;
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
			throw new ParameterNotValidException() ;
		}

		return new long[] { ymd.getY(), ymd.getM(), ymd.getD() } ;
	}

	public static double valueOf( DMSType dms ) throws ParameterNotValidException {
		if ( dms == null ) {
			throw new ParameterNotValidException() ;
		}

		return CAACoordinateTransformation.DegreesToRadians( dms.getDeg()+dms.getMin()/60.+dms.getSec()/3600 ) ;
	}

	public static double valueOf( HMSType hms ) throws ParameterNotValidException {
		if ( hms == null ) {
			throw new ParameterNotValidException() ;
		}

		return CAACoordinateTransformation.HoursToRadians( hms.getHrs()+hms.getMin()/60.+hms.getSec()/3600 ) ;
	}

	public static double valueOf( RationalType rational ) throws ParameterNotValidException {
		if ( rational == null ) {
			throw new ParameterNotValidException() ;
		}

		return rational.getValue() ;
	} 
}
