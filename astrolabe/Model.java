
package astrolabe;

import astrolabe.model.* ;

import caa.CAACoordinateTransformation;
import caa.CAADate;

public class Model extends Main {

	public static CAADate condense( astrolabe.model.Date date ) throws ParameterNotValidException {
		CAADate d = null ;	
		DateType dT ;

		if ( date == null ) {
			throw new ParameterNotValidException() ;
		}

		if ( ( dT = date.getDateOccidental() ) != null ) {
			d = new DateOccidental( Model.condense( dT.getYMD() ), dT.getTime()!=null?CAACoordinateTransformation.RadiansToHours( Model.condense( dT.getTime() ) ):0 ).getDateOccidental() ;
		} /* else if ( ( dT = date.getDateJewish() ) != null ) {
			d = new DateJewish( Model.condense( dT.getYMD() ), dT.getTime()!=null?CAACoordinateTransformation.RadiansToHours( Model.condense( dT.getTime() ) ):0 ).getDateOccidental() ;
		} else if ( ( dT = date.getDateIslamic() ) != null ) {
			d = new DateIslamic( Model.condense( dT.getYMD() ), dT.getTime()!=null?CAACoordinateTransformation.RadiansToHours( Model.condense( dT.getTime() ) ):0 ).getDateOccidental() ;
		} */

		return d ;
	}

	public static double condense( astrolabe.model.Time time ) throws ParameterNotValidException {
		double r = 0 ;
		TimeType tT ;

		if ( time == null ) {
			throw new ParameterNotValidException() ;
		}

		if ( ( tT = time.getTimeUT() ) != null ) {
			r = new TimeUT( Model.condense( tT ) ).getTimeUT() ;
		} /* else if ( ( tT = dT.getTime().getTimeST() ) != null ) {
			r = new TimeST( Model.condense( tT ) ).getTimeUT() ;
		} else if ( ( tT = dT.getTime().getTimeSD() ) != null ) {
			r = new TimeTD( Model.condense( tT ) ).getTimeUT() ;
		} */

		return r ;
	}

	public static double condense( TimeType time ) throws ParameterNotValidException {
		double r ;

		if ( time == null ) {
			throw new ParameterNotValidException() ;
		}

		try {
			r = CAACoordinateTransformation.HoursToRadians( Model.condense( time.getRational() ) ) ;
		} catch ( ParameterNotValidException e ) {
			r = Model.condense( time.getHMS() ) ;
		}

		return r ;
	}

	public static double[] condense( SphericalType spherical ) throws ParameterNotValidException {
		double[] r = { 1, 0, 0 } ;

		if ( spherical == null ) {
			throw new ParameterNotValidException() ;
		}

		r[1] = Model.condense( spherical.getPhi() ) ;
		r[2] = Model.condense( spherical.getTheta() ) ;

		try {
			r[0] = condense( spherical.getR() ) ;
		} catch ( ParameterNotValidException e ) {}

		return r ;
	}

	public static double[] condense( PolarType polar ) throws ParameterNotValidException {
		double[] r = { 1, 0 } ;

		if ( polar == null ) {
			throw new ParameterNotValidException() ;
		}

		r[1] = Model.condense( polar.getPhi() ) ;

		try {
			r[0] = Model.condense( polar.getR() ) ;
		} catch ( ParameterNotValidException e ) {}

		return r ;
	}

	public static double[] condense( CartesianType cartesian ) throws ParameterNotValidException {
		if ( cartesian == null ) {
			throw new ParameterNotValidException() ;
		}

		return new double[] { cartesian.getX(), cartesian.getY(), cartesian.hasZ()?cartesian.getZ():0 } ;
	}

	public static double condense( AngleType angle ) throws ParameterNotValidException {
		double r ;

		if ( angle == null ) {
			throw new ParameterNotValidException() ;
		}

		try {
			r = CAACoordinateTransformation.DegreesToRadians( Model.condense( angle.getRational() ) ) ;
		} catch ( ParameterNotValidException e ) {
			r = Model.condense( angle.getDMS() ) ;
		}

		return r ;
	}

	public static java.lang.String condense( StringType string ) throws ParameterNotValidException {
		if ( string == null ) {
			throw new ParameterNotValidException() ;
		}

		return string.getValue() ;
	}

	public static long[] condense( YMDType ymd ) throws ParameterNotValidException {
		if ( ymd == null ) {
			throw new ParameterNotValidException() ;
		}

		return new long[] { ymd.getY(), ymd.getM(), ymd.getD() } ;
	}

	public static double condense( DMSType dms ) throws ParameterNotValidException {
		if ( dms == null ) {
			throw new ParameterNotValidException() ;
		}

		return CAACoordinateTransformation.DegreesToRadians( dms.getDeg()+dms.getMin()/60.+dms.getSec()/3600 ) ;
	}

	public static double condense( HMSType hms ) throws ParameterNotValidException {
		if ( hms == null ) {
			throw new ParameterNotValidException() ;
		}

		return CAACoordinateTransformation.HoursToRadians( hms.getHrs()+hms.getMin()/60.+hms.getSec()/3600 ) ;
	}

	public static double condense( RationalType rational ) throws ParameterNotValidException {
		if ( rational == null ) {
			throw new ParameterNotValidException() ;
		}

		return rational.getValue() ;
	} 
}
