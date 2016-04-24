
package astrolabe;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import caa.CAA2DCoordinate;
import caa.CAACoordinateTransformation;
import caa.CAANutation;
import caa.CAASun;

public final class ApplicationHelper {

	private ApplicationHelper() {
	}

	public static double getObliquityOfEcliptic( boolean mean, double JD ) {
		double r ;

		r = mean?CAANutation.MeanObliquityOfEcliptic( JD ):CAANutation.TrueObliquityOfEcliptic( JD ) ;
		r = CAACoordinateTransformation.DegreesToRadians( r ) ;

		return r ;
	}

	public static void registerYMD( String key, caa.CAADate date ) throws ParameterNotValidException {
		String ind ;

		ind = Astrolabe.getLocalizedString( ApplicationConstant.LK_YMD_NUMBEROFYEAR ) ;
		registerNumber( key+ind, date.Year() ) ;
		ind = Astrolabe.getLocalizedString( ApplicationConstant.LK_YMD_NUMBEROFMONTH ) ;
		registerNumber( key+ind, date.Month() ) ;
		ind = Astrolabe.getLocalizedString( ApplicationConstant.LK_YMD_NUMBEROFDAY ) ;
		registerNumber( key+ind, date.Day() ) ;
	}

	public static void registerHMS( String key, double hms, double precision ) throws ParameterNotValidException {
		double h ;
		String ind ;

		h = CAACoordinateTransformation.RadiansToHours( hms<0?hms-.000000001:hms+.000000001 ) ;

		ind = Astrolabe.getLocalizedString( ApplicationConstant.LK_HMS_HOURS ) ;
		registerNumber( key+ind, (long) h ) ;
		ind = Astrolabe.getLocalizedString( ApplicationConstant.LK_HMS_MSPREFIX ) ;
		registerMS( key+ind, java.lang.Math.abs( hms ), precision, true ) ;
	}

	public static void registerDMS( String key, double dms, double precision ) throws ParameterNotValidException {
		double d ;
		String ind ;

		d = CAACoordinateTransformation.RadiansToDegrees( dms<0?dms-.000000001:dms+.000000001 ) ;

		ind = Astrolabe.getLocalizedString( ApplicationConstant.LK_DMS_DEGREES ) ;
		registerNumber( key+ind, (long) d ) ;
		ind = Astrolabe.getLocalizedString( ApplicationConstant.LK_DMS_MSPREFIX ) ;
		registerMS( key+ind, java.lang.Math.abs( dms ), precision, false ) ;
	}

	public static void registerMS( String key, double value, double precision, boolean h ) throws ParameterNotValidException {
		double v, p, ms ;
		long m, s, f ;
		String ind ;

		if ( h ) {
			v = CAACoordinateTransformation.RadiansToHours( value ) ;
		} else {
			v = CAACoordinateTransformation.RadiansToDegrees( value ) ;
		}

		p = java.lang.Math.pow( 10, precision ) ;
		ms = v-(int) v ;
		m = (long) ( 60*ms ) ;
		s = (long) ( 60*( 60*ms-m ) ) ;
		f = (long) ( ( ( 60*( 60*ms-m ) )-s )*p+.5 ) ;
		ind = Astrolabe.getLocalizedString( ApplicationConstant.LK_MS_MINUTES ) ;
		registerNumber( key+ind, m ) ;
		ind = Astrolabe.getLocalizedString( ApplicationConstant.LK_MS_SECONDS ) ;
		registerNumber( key+ind, java.lang.Math.abs( s ) ) ;
		ind = Astrolabe.getLocalizedString( ApplicationConstant.LK_MS_FRACTION ) ;
		registerNumber( key+ind, java.lang.Math.abs( f ) ) ;
	}

	public static void registerNumber( String key, double value, int precision ) throws ParameterNotValidException {
		double p ;
		long v ;

		if ( precision < 0 ) {
			throw new ParameterNotValidException() ;
		}

		p = java.lang.Math.pow( 10, precision ) ;
		v = (long) ( ( value*p+.5 )/p ) ;

		registerNumber( key, v ) ;
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

		a = MapTo0To360Range( value ) ;

		registerDMS( key, a, precision ) ;
	}

	public static void registerTime( String key, double value, int precision ) throws ParameterNotValidException {
		double t ;

		t = MapTo0To24Range( value ) ;

		registerHMS( key, t, precision ) ;
	}

	public static java.util.Vector<double[]> convertCartesianVectorToDouble( java.util.Vector<Vector> vector ) {
		java.util.Vector<double[]> r ;
		Vector v ;

		r = new java.util.Vector<double[]>() ;
		for ( int n=0 ; n<vector.size() ; n++ ) {
			v = vector.get( n ) ;
			r.add( new double[] { v.getX(), v.getY() } ) ;
		}

		return r ;
	}

	public static java.util.Vector<Vector> convertCartesianDoubleToVector( java.util.Vector<double[]> vector ) {
		java.util.Vector<Vector> r ;
		double[] v ;

		r = new java.util.Vector<Vector>() ;
		for ( int n=0 ; n<vector.size() ; n++ ) {
			v = vector.get( n ) ;
			r.add( new Vector( v[0], v[1] ) ) ;
		}

		return r ;
	}

	public static java.util.Vector<Vector> reverseVector( java.util.Vector<Vector> vector ) {
		java.util.Vector<Vector> r ;

		r = new java.util.Vector<Vector>() ;
		for ( int n=vector.size() ; n>0 ; n-- ) {
			r.add( vector.get( n-1 ) ) ;
		}

		return r ;
	}

	public static double MapTo0To24Range( double h ) {
		return CAACoordinateTransformation.HoursToRadians(
				CAACoordinateTransformation.MapTo0To24Range(
						CAACoordinateTransformation.RadiansToHours( h ) ) ) ;
	}

	public static double MapTo0To90Range( double d ) {
		double r ;

		r = d ;

		while ( r<0 ) {
			r = r+java.lang.Math.PI/2 ;
		}
		while ( r>java.lang.Math.PI/2 ) {
			r = r-java.lang.Math.PI/2 ;
		}

		return r ;
	}

	public static double MapTo0To360Range( double d ) {
		return CAACoordinateTransformation.DegreesToRadians(
				CAACoordinateTransformation.MapTo0To360Range(
						CAACoordinateTransformation.RadiansToDegrees( d ) ) ) ;
	}

	public static double[] Horizontal2Equatorial( double az, double al, double la ) {
		CAA2DCoordinate c ;
		double[] r ;

		r = new double[2] ;

		c = CAACoordinateTransformation.Horizontal2Equatorial(
				CAACoordinateTransformation.RadiansToDegrees( az  ),
				CAACoordinateTransformation.RadiansToDegrees( al ),
				CAACoordinateTransformation.RadiansToDegrees( la ) ) ;
		r[0] = CAACoordinateTransformation.HoursToRadians( c.X() ) ;
		r[1] = CAACoordinateTransformation.DegreesToRadians( c.Y() ) ;

		c.delete() ;

		return r ;
	}

	public static double[] Equatorial2Horizontal( double HA, double de, double la ) {
		CAA2DCoordinate c ;
		double[] r ;

		r = new double[2] ;

		c = CAACoordinateTransformation.Equatorial2Horizontal(
				CAACoordinateTransformation.RadiansToHours( HA  ),
				CAACoordinateTransformation.RadiansToDegrees( de ),
				CAACoordinateTransformation.RadiansToDegrees( la ) ) ;
		r[0] = CAACoordinateTransformation.DegreesToRadians( c.X() ) ;
		r[1] = CAACoordinateTransformation.DegreesToRadians( c.Y() ) ;

		c.delete() ;

		return r ;
	}

	public static double ApparentEclipticLongtitude( double JD ) {
		return CAACoordinateTransformation.DegreesToRadians( CAASun.ApparentEclipticLongtitude( JD ) ) ;
	}

	public static double ApparentEclipticLatitude( double JD ) {
		return CAACoordinateTransformation.DegreesToRadians( CAASun.ApparentEclipticLatitude( JD ) ) ;
	}

	public static double GeometricEclipticLongitude( double JD ) {
		return CAACoordinateTransformation.DegreesToRadians( CAASun.GeometricEclipticLongitude( JD ) ) ;
	}

	public static double GeometricEclipticLatitude( double JD ) {
		return CAACoordinateTransformation.DegreesToRadians( CAASun.GeometricEclipticLatitude( JD ) ) ;
	}

	public static double[] Ecliptic2Equatorial( double La, double Be, double e ) {
		CAA2DCoordinate c ;
		double[] r ;

		r = new double[2] ;

		c = CAACoordinateTransformation.Ecliptic2Equatorial(
				CAACoordinateTransformation.RadiansToDegrees( La  ),
				CAACoordinateTransformation.RadiansToDegrees( Be ),
				CAACoordinateTransformation.RadiansToDegrees( e ) ) ;
		r[0] = CAACoordinateTransformation.HoursToRadians( c.X() ) ;
		r[1] = CAACoordinateTransformation.DegreesToRadians( c.Y() ) ;

		c.delete() ;

		return r ;
	}

	public static double[] Equatorial2Ecliptic( double Al, double De, double e ) {
		CAA2DCoordinate c ;
		double[] r ;

		r = new double[2] ;

		c = CAACoordinateTransformation.Equatorial2Ecliptic(
				CAACoordinateTransformation.RadiansToHours( Al  ),
				CAACoordinateTransformation.RadiansToDegrees( De ),
				CAACoordinateTransformation.RadiansToDegrees( e ) ) ;
		r[0] = CAACoordinateTransformation.DegreesToRadians( c.X() ) ;
		r[1] = CAACoordinateTransformation.DegreesToRadians( c.Y() ) ;

		c.delete() ;

		return r ;
	}

	public static double[] Galactic2Equatorial( double l, double b ) {
		CAA2DCoordinate c ;
		double[] r ;

		r = new double[2] ;

		c = CAACoordinateTransformation.Galactic2Equatorial(
				CAACoordinateTransformation.RadiansToDegrees( l  ),
				CAACoordinateTransformation.RadiansToDegrees( b ) ) ;
		r[0] = CAACoordinateTransformation.HoursToRadians( c.X() ) ;
		r[1] = CAACoordinateTransformation.DegreesToRadians( c.Y() ) ;

		c.delete() ;

		return r ;
	}

	public static double[] Equatorial2Galactic( double Al, double De ) {
		CAA2DCoordinate c ;
		double[] r ;

		r = new double[2] ;

		c = CAACoordinateTransformation.Equatorial2Galactic(
				CAACoordinateTransformation.RadiansToHours( Al  ),
				CAACoordinateTransformation.RadiansToDegrees( De ) ) ;
		r[0] = CAACoordinateTransformation.DegreesToRadians( c.X() ) ;
		r[1] = CAACoordinateTransformation.DegreesToRadians( c.Y() ) ;

		c.delete() ;

		return r ;
	}

	public static double MeanEclipticLongitude( double JD ) {
		double r ;
		double rho, rho2, rho3, rho4, rho5 ;

		rho = ( JD-2451545 )/365250 ;
		rho2 = rho*rho ;
		rho3 = rho2*rho ;
		rho4 = rho3*rho ;
		rho5 = rho4*rho ;

		r = caa.CAACoordinateTransformation.MapTo0To360Range(
				280.4664567+360007.6982779*rho+0.03032028*rho2+rho3/49931-rho4/15300-rho5/2000000 ) ;

		return CAACoordinateTransformation.DegreesToRadians( r ) ;
	}

	public static double MeanEclipticLatitude( double JD ) {
		return 0 ;
	}

	public static Preferences getClassNode( Object clazz, String instance, String qualifier ) {        
		String i = instance != null ? "/"+instance : "" ;
		String q = qualifier != null ? "/"+qualifier : "" ;
		String d = "/"+clazz.getClass().getName().replaceAll( "\\.", "/" ).split( "\\$", 2 )[0] ;
		String n = d+i+q ;
		Preferences r = null ;

		try {
			if ( ! Preferences.systemRoot().nodeExists( n ) ) {
				n = d+q ;
			}

			r = Preferences.systemRoot().node( n ) ;
		} catch ( BackingStoreException e ) {}

		return r ;
	} 

	public static Preferences getNestedClassNode( Object clazz, String instance, String qualifier ) {        
		String q = qualifier != null ? "/"+qualifier : "" ;
		String c[] = clazz.getClass().getName().replaceAll( "\\.", "/" ).split( "\\$", 2 ) ;
		String s = c[1].replaceAll( "[$]", "/" ) ;
		String n = "/"+c[0]+( instance != null? "/"+instance+"/" : "/" )+s+q ;
		Preferences r = null ;

		try {
			if ( ! Preferences.systemRoot().nodeExists( n ) ) {
				n = "/"+c[0]+"/"+s+q ;
			}

			r = Preferences.systemRoot().node( n ) ;
		} catch ( BackingStoreException e ) {}

		return r ;
	} 

	public static Preferences getPackageNode( Object clazz, String instance, String qualifier ) {        
		java.lang.Package p = clazz.getClass().getPackage() ;
		String i = instance != null ? "/"+instance : "" ;
		String q = qualifier != null ? "/"+qualifier : "" ;
		String d, n ;
		Preferences r = null ;

		try {
			if ( p != null ) {
				d = "/"+p.getName().replaceAll( "\\.", "/" ) ;
				n = d+i+q ;
				if ( ! Preferences.systemRoot().nodeExists( n ) ) {
					n = d+q ;
				}
			} else {
				d = "/"+"default" ;
				n = d+i+q ;
				if ( ! Preferences.systemRoot().nodeExists( n ) ) {
					n = d+q ;
				}
			}

			r = Preferences.systemRoot().node( n ) ;
		} catch ( BackingStoreException e ) {}

		return r ;
	} 
}
