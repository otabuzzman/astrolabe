
package astrolabe;

import caa.CAACoordinateTransformation;

public final class ReplacementHelper {

	public static void registerYMD( String key, caa.CAADate date ) throws ParameterNotValidException {

		ReplacementHelper.registerNumber( key+"Y", date.Year() ) ;
		ReplacementHelper.registerNumber( key+"M", date.Month() ) ;
		ReplacementHelper.registerNumber( key+"D", date.Day() ) ;
	}

	public static void registerHMS( String key, double hms, double precision ) throws ParameterNotValidException {
		double h ;

		h = CAACoordinateTransformation.RadiansToHours( hms<0?hms-.000000001:hms+.000000001 ) ;

		ReplacementHelper.registerNumber( key+"hrs", (long) h ) ;
		ReplacementHelper.registerMS( key+"h", java.lang.Math.abs( hms ), precision ) ;
	}

	public static void registerDMS( String key, double dms, double precision ) throws ParameterNotValidException {
		double d ;

		d = CAACoordinateTransformation.RadiansToDegrees( dms<0?dms-.000000001:dms+.000000001 ) ;

		ReplacementHelper.registerNumber( key+"deg", (long) d ) ;
		ReplacementHelper.registerMS( key+"d", java.lang.Math.abs( dms ), precision ) ;
	}

	public static void registerMS( String key, double value, double precision ) throws ParameterNotValidException {
		double v, p, ms ;
		long m, s, f ;

		v = CAACoordinateTransformation.RadiansToHours( value ) ;

		p = java.lang.Math.pow( 10, precision ) ;
		ms = v-(int) v ;
		m = (long) ( 60*ms ) ;
		s = (long) ( 60*( 60*ms-m ) ) ;
		f = (long) ( ( ( 60*( 60*ms-m ) )-s )*p+.5 ) ;
		ReplacementHelper.registerNumber( key+"min", m ) ;
		ReplacementHelper.registerNumber( key+"sec", java.lang.Math.abs( s ) ) ;
		ReplacementHelper.registerNumber( key+"frc", java.lang.Math.abs( f ) ) ;
	}

	public static void registerNumber( String key, double value, int precision ) throws ParameterNotValidException {
		double p ;
		long v ;

		if (precision < 0 ) {
			throw new ParameterNotValidException() ;
		}

		p = java.lang.Math.pow( 10, precision ) ;
		v = (long) ( ( value*p+.5 )/p ) ;

		ReplacementHelper.registerNumber( key, v ) ;
	}

	public static void registerNumber( String key, double value ) throws ParameterNotValidException {
		Registry r ;

		r = new Registry() ;
		r.register( key, (Object) new Double( value ).toString() ) ;
	}

	public static void registerNumber( String key, long value ) throws ParameterNotValidException {
		Registry r ;

		r = new Registry() ;
		r.register( key, (Object) new Long( value ).toString() ) ;
	}

	public static void registerName( String key, String value ) throws ParameterNotValidException {
		Registry r ;

		r = new Registry() ;
		r.register( key, new String( value ) ) ;
	}

	public static void registerAngle( String key, double value, int precision ) throws ParameterNotValidException {
		double a ;

		a = CAACoordinateTransformation.RadiansToDegrees( value ) ;
//		a = CAACoordinateTransformation.MapTo0To360Range( a ) ;
		a = CAACoordinateTransformation.DegreesToRadians( a ) ;

		registerDMS( key, a, precision ) ;
	}

	public static void registerTime( String key, double value, int precision ) throws ParameterNotValidException {
		double t ;

		t = CAACoordinateTransformation.RadiansToHours( value ) ;
//		t = CAACoordinateTransformation.MapTo0To24Range( t ) ;
		t = CAACoordinateTransformation.HoursToRadians( t ) ;

		registerHMS( key, t, precision ) ;
	}
}
