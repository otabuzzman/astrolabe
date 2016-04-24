
package astrolabe;

import astrolabe.model.AnnotationType;
import astrolabe.model.ChartType;
import astrolabe.model.CircleType;
import astrolabe.model.DialType;
import astrolabe.model.HorizonType;
import astrolabe.model.SunType;

import astrolabe.model.AngleType;
import astrolabe.model.CalendarType;
import astrolabe.model.CartesianType;
import astrolabe.model.DateType;
import astrolabe.model.DMSType;
import astrolabe.model.HMSType;
import astrolabe.model.RationalType;
import astrolabe.model.PolarType;
import astrolabe.model.SphericalType;
import astrolabe.model.StringType;
import astrolabe.model.TimeType;
import astrolabe.model.YMDType;

import caa.CAACoordinateTransformation;
import caa.CAADate;

public final class AstrolabeFactory {

	private AstrolabeFactory() {
	}

	public static ChartType getChartType( astrolabe.model.Chart ch ) {
		ChartType chT ;

		if ( ch.getChartStereographic() != null ) {
			chT = ch.getChartStereographic() ;
		} else if ( ch.getChartOrthographic() != null ) {
			chT = ch.getChartOrthographic() ;
		} else if ( ch.getChartEquidistant() != null ) {
			chT = ch.getChartEquidistant() ;
		} else if ( ch.getChartGnomonic() != null ) {
			chT = ch.getChartGnomonic() ;
		} else { // ChartEqualarea
			chT = ch.getChartEqualarea() ;
		}
		return chT ;
	}

	public static Chart createChart( astrolabe.model.Chart ch, PostscriptStream ps ) {
		ChartType chT ;
		Chart chart ;

		if ( ( chT = ch.getChartStereographic() ) != null ) {
			chart = new ChartStereographic( chT, ps ) ;
		} else if ( ( chT = ch.getChartOrthographic() ) != null ) {
			chart = new ChartOrthographic( chT, ps ) ;
		} else if ( ( chT = ch.getChartEquidistant() ) != null ) {
			chart = new ChartEquidistant( chT, ps ) ;
		} else if ( ( chT = ch.getChartGnomonic() ) != null ) {
			chart = new ChartGnomonic( chT, ps ) ;
		} else { // ChartEqualarea
			chart = new ChartEqualarea( ch.getChartEqualarea(), ps ) ;
		}
		return chart ;
	}

	public static HorizonType getHorizonType( astrolabe.model.Horizon ho ) {
		HorizonType hoT ;

		if ( ( hoT = ho.getHorizonLocal() ) != null  ) {
			hoT = ho.getHorizonLocal() ;
		} else if ( ( hoT = ho.getHorizonEquatorial() ) != null  ) {
			hoT = ho.getHorizonEquatorial() ;
		} else if ( ( hoT = ho.getHorizonEcliptical() ) != null  ) {
			hoT = ho.getHorizonEcliptical() ;
		} else { // HorizonGalactic
			hoT = ho.getHorizonGalactic() ;
		}

		return hoT ;
	}

	public static Horizon createHorizon( astrolabe.model.Horizon ho ) {
		HorizonType hoT ;
		Horizon horizon ;

		if ( ( hoT = ho.getHorizonLocal() ) != null  ) {
			horizon = new HorizonLocal( hoT ) ;
		} else if ( ( hoT = ho.getHorizonEquatorial() ) != null  ) {
			horizon = new HorizonEquatorial( hoT ) ;
		} else if ( ( hoT = ho.getHorizonEcliptical() ) != null  ) {
			horizon = new HorizonEcliptical( hoT ) ;
		} else { // HorizonGalactic
			horizon = new HorizonGalactic( ho.getHorizonGalactic() ) ;
		}

		return horizon ;
	}

	public static CircleType getCircleType( astrolabe.model.Circle cl ) {
		CircleType clT ;

		if ( cl.getCircleParallel() != null ) {
			clT = cl.getCircleParallel() ;
		} else if ( cl.getCircleMeridian() != null ) {
			clT = cl.getCircleMeridian() ;
		} else if ( cl.getCircleSouthernPolar() != null ) {
			clT = cl.getCircleSouthernPolar() ;
		} else if ( cl.getCircleNorthernPolar() != null ) {
			clT = cl.getCircleNorthernPolar() ;
		} else if ( cl.getCircleSouthernTropic() != null ) {
			clT = cl.getCircleSouthernTropic() ;
		} else { // CircleNorthernTropic
			clT = cl.getCircleNorthernTropic() ;
		}

		return clT ;
	}

	public static Circle createCircle( astrolabe.model.Circle cl, Chart chart, Horizon horizon ) throws ParameterNotValidException {
		CircleType clT ;
		Circle circle = null ;

		if ( ( clT = cl.getCircleParallel() ) != null ) {
			circle = new CircleParallel( clT, chart, horizon ) ;
		} else if ( ( clT = cl.getCircleMeridian() ) != null ) {
			circle = new CircleMeridian( clT, chart, horizon ) ;
		} else if ( ( clT = cl.getCircleSouthernPolar() ) != null ) {
			circle = new CircleSouthernPolar( clT, chart, horizon ) ;
		} else if ( ( clT = cl.getCircleNorthernPolar() ) != null ) {
			circle = new CircleNorthernPolar( clT, chart, horizon ) ;
		} else if ( ( clT = cl.getCircleSouthernTropic() ) != null ) {
			circle = new CircleSouthernTropic( clT, chart, horizon ) ;
		} else { // CircleNorthernTropic
			circle = new CircleNorthernTropic( cl.getCircleNorthernTropic(), chart, horizon ) ;
		}

		return circle ;
	}

	public static DialType getDialType( astrolabe.model.Dial dl ) {
		DialType dlT ;

		if ( dl.getDialDegree() != null ) {
			dlT = dl.getDialDegree() ;
		} else if ( dl.getDialHour() != null ) {
			dlT = dl.getDialHour() ;
		} else { // DialDay
			dlT = dl.getDialDay() ;
		}

		return dlT ;
	}

	public static Dial createDial( astrolabe.model.Dial dl, Circle circle ) throws ParameterNotValidException {
		DialType dlT ;
		Dial dial = null ;

		if ( ( dlT = dl.getDialDegree() ) != null ) {
			dial = new DialDegree( dlT, circle ) ;
		} else if ( ( dlT = dl.getDialHour() ) != null ) {
			dial = new DialHour( dlT, circle ) ;
		} else { // DialDay
			dial = new DialDay( dl.getDialDay(), circle ) ;
		}

		return dial ;
	}

	public static AnnotationType getAnnotationType( astrolabe.model.Annotation an ) {
		AnnotationType anT ;

		if ( an.getAnnotationStraight() != null ) {
			anT = an.getAnnotationStraight() ;
		} else { // AnnotationCurved
			anT = an.getAnnotationCurved() ;
		}

		return anT ;
	}

	public static Annotation createAnnotation( astrolabe.model.Annotation an ) {
		AnnotationType anT ;
		Annotation annotation = null ;

		if ( ( anT = an.getAnnotationStraight() ) != null ) {
			annotation = new AnnotationStraight( anT ) ;
		} else { // AnnotationCurved
			annotation = new AnnotationCurved( an.getAnnotationCurved() ) ;
		}

		return annotation ;
	}

	public static SunType getSunType( astrolabe.model.Sun sn ) {
		SunType snT ;

		if ( sn.getSunApparent() != null ) {
			snT = sn.getSunApparent() ;
		} else if ( sn.getSunMean() != null ) {
			snT = sn.getSunMean() ;
		} else { // SunTrue
			snT = sn.getSunTrue() ;
		}

		return snT ;
	}

	public static Sun createSun( astrolabe.model.Sun sn ) {
		SunType snT ;
		Sun sun ;

		if ( ( snT = sn.getSunApparent() ) != null ) {
			sun = new SunApparent( snT ) ;
		} else if ( ( snT = sn.getSunMean() ) != null ) {
			sun = new SunMean( snT ) ;
		} else { // SunTrue
			sun = new SunTrue( sn.getSunTrue() ) ;
		}

		return sun ;
	}

	public static CAADate valueOf( DateType date ) throws ParameterNotValidException {
		CAADate r = null ;

		if ( date == null ) {
			throw new ParameterNotValidException() ;
		}

		if ( date.getJD() == null ) {
			r = AstrolabeFactory.valueOf( date.getCalendar() ) ;
		} else {
			double jd ;

			jd = AstrolabeFactory.valueOf( date.getJD() ) ;

			r = new CAADate( jd, ApplicationHelper.isDateGregorian( jd ) ) ;
		}

		return r ;
	}

	public static CAADate valueOf( CalendarType calendar ) throws ParameterNotValidException {
		CAADate r = null ;
		Calendar c ;
		double t ;

		if ( calendar == null ) {
			throw new ParameterNotValidException() ;
		}

		if ( calendar.getTime() == null ) {
			t = 0 ;
		} else {
			t = CAACoordinateTransformation.RadiansToHours( AstrolabeFactory.valueOf( calendar.getTime() ) ) ;
		}

		c = new astrolabe.Calendar( AstrolabeFactory.valueOf( calendar.getYMD() ), t ) ;

		r = c.getDate() ;

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

	public static java.lang.String valueOf( StringType string ) throws ParameterNotValidException {
		if ( string == null ) {
			throw new ParameterNotValidException() ;
		}

		return string.getValue() ;
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
