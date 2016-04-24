
package astrolabe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import caa.CAA2DCoordinate;
import caa.CAACoordinateTransformation;
import caa.CAADate;
import caa.CAAJupiter;
import caa.CAAMars;
import caa.CAAMercury;
import caa.CAANeptune;
import caa.CAANutation;
import caa.CAASaturn;
import caa.CAASun;
import caa.CAAUranus;
import caa.CAAVenus;

public final class ApplicationHelper {

	private final static Log log = LogFactory.getLog( ApplicationHelper.class ) ;

	private ApplicationHelper() {
	}

	public static void registerYMD( String key, CAADate date ) {
		String ind ;

		ind = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_YMD_NUMBEROFYEAR ) ;
		registerNumber( key+ind, date.Year() ) ;
		ind = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_YMD_NUMBEROFMONTH ) ;
		registerNumber( key+ind, date.Month() ) ;
		ind = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_YMD_NUMBEROFDAY ) ;
		registerNumber( key+ind, date.Day() ) ;
	}

	public static void registerHMS( String key, double hms, double precision ) {
		double h ;
		String ind ;

		h = CAACoordinateTransformation.RadiansToHours( hms<0?hms-.000000001:hms+.000000001 ) ;

		ind = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_HMS_HOURS ) ;
		registerNumber( key+ind, (long) h ) ;
		ind = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_HMS_MSPREFIX ) ;
		registerMS( key+ind, java.lang.Math.abs( hms ), precision, true ) ;
	}

	public static void registerDMS( String key, double dms, double precision ) {
		double d ;
		String ind ;

		d = CAACoordinateTransformation.RadiansToDegrees( dms<0?dms-.000000001:dms+.000000001 ) ;

		ind = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_DMS_DEGREES ) ;
		registerNumber( key+ind, (long) d ) ;
		ind = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_DMS_MSPREFIX ) ;
		registerMS( key+ind, java.lang.Math.abs( dms ), precision, false ) ;
	}

	public static void registerMS( String key, double value, double precision, boolean h ) {
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
		ind = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_MS_MINUTES ) ;
		registerNumber( key+ind, m ) ;
		ind = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_MS_SECONDS ) ;
		registerNumber( key+ind, java.lang.Math.abs( s ) ) ;
		ind = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_MS_FRACTION ) ;
		registerNumber( key+ind, java.lang.Math.abs( f ) ) ;
	}

	public static void registerNumber( String key, double value, int precision ) {
		double p, v ;

		p = java.lang.Math.pow( 10, precision ) ;
		v = (long) ( ( value*p+.5 ) )/p ;

		registerNumber( key, v ) ;
	}

	public static void registerNumber( String key, double value ) {
		Registry.register( key, (Object) new Double( value ).toString() ) ;
	}

	public static void registerNumber( String key, long value ) {
		Registry.register( key, (Object) new Long( value ).toString() ) ;
	}

	public static void registerName( String key, String value ) {
		Registry.register( key, new String( value ) ) ;
	}

	public static void registerAngle( String key, double value, int precision ) {
		double a ;

		a = mapTo0To360Range( value ) ;

		registerDMS( key, a, precision ) ;
	}

	public static void registerTime( String key, double value, int precision ) {
		double t ;

		t = mapTo0To24Range( value ) ;

		registerHMS( key, t, precision ) ;
	}

	public static double jdOfNow() {
		return jdOfCalendar( Calendar.getInstance() ) ;
	}

	public static double jdOfNoon() {
		Calendar c ;

		c = Calendar.getInstance() ;

		c.set( Calendar.HOUR_OF_DAY, 12 ) ;
		c.set( Calendar.MINUTE, 0 ) ;
		c.set( Calendar.SECOND, 0 ) ;

		return jdOfCalendar( c ) ;
	}

	public static double jdOfToday() {
		Calendar c ;

		c = Calendar.getInstance() ;

		c.set( Calendar.HOUR_OF_DAY, 0 ) ;
		c.set( Calendar.MINUTE, 0 ) ;
		c.set( Calendar.SECOND, 0 ) ;

		return jdOfCalendar( c ) ;
	}

	public static double jdOfYear() {
		Calendar c ;

		c = Calendar.getInstance() ;

		return jdOfYear( c.get( Calendar.YEAR ) ) ;
	}

	public static double jdOfYear( long year ) {
		Calendar c ;

		c = Calendar.getInstance() ;
		c.set( Calendar.HOUR_OF_DAY, 0 ) ;
		c.set( Calendar.MINUTE, 0 ) ;
		c.set( Calendar.SECOND, 0 ) ;

		c.set( (int) year, 1, 1 ) ;

		return jdOfCalendar( c ) ;
	}

	public static double jdOfCalendar( Calendar calendar ) {
		double r ;
		double t ;
		CAADate cd ;

		t = calendar.get( Calendar.HOUR_OF_DAY )
		+calendar.get( Calendar.MINUTE )/60.
		+calendar.get( Calendar.SECOND )/3600. ;

		cd = new CAADate( calendar.get( Calendar.YEAR ),
				calendar.get( Calendar.MONTH ),
				calendar.get( Calendar.DATE )+t/24, true ) ;
		r = cd.Julian() ;
		cd.delete() ;

		return r ;
	}

	public static java.util.Vector<double[]> reverseVector( java.util.Vector<double[]> vector ) {
		java.util.Vector<double[]> r ;

		r = new java.util.Vector<double[]>() ;
		for ( int n=vector.size() ; n>0 ; n-- ) {
			r.add( vector.get( n-1 ) ) ;
		}

		return r ;
	}

	public static double mapTo0To24Range( double h ) {
		return CAACoordinateTransformation.HoursToRadians(
				CAACoordinateTransformation.MapTo0To24Range(
						CAACoordinateTransformation.RadiansToHours( h ) ) ) ;
	}

	public static double mapTo0To90Range( double d ) {
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

	public static double mapTo0To360Range( double d ) {
		return CAACoordinateTransformation.DegreesToRadians(
				CAACoordinateTransformation.MapTo0To360Range(
						CAACoordinateTransformation.RadiansToDegrees( d ) ) ) ;
	}

	public static double mapToNTo360Range( double d ) {
		double r ;
		double a ;

		a = mapTo0To360Range( d ) ;

		if ( d<0&&a>Math.rad180 ) {
			r = a-Math.rad360 ;
		} else {
			r = a ;
		}

		return r ;
	}

	public static double meanObliquityOfEcliptic( double JD ) {
		return CAACoordinateTransformation.DegreesToRadians( CAANutation.MeanObliquityOfEcliptic( JD ) ) ;
	}

	public static double trueObliquityOfEcliptic( double JD ) {
		return CAACoordinateTransformation.DegreesToRadians( CAANutation.TrueObliquityOfEcliptic( JD ) ) ;
	}

	public static double apparentEclipticLongitude( double JD ) {
		return CAACoordinateTransformation.DegreesToRadians( CAASun.ApparentEclipticLongtitude( JD ) ) ;
	}

	public static double apparentEclipticLatitude( double JD ) {
		return CAACoordinateTransformation.DegreesToRadians( CAASun.ApparentEclipticLatitude( JD ) ) ;
	}

	public static double meanEclipticLongitude( double JD ) {
		double r ;
		double rho, rho2, rho3, rho4, rho5 ;

		rho = ( JD-2451545 )/365250 ;
		rho2 = rho*rho ;
		rho3 = rho2*rho ;
		rho4 = rho3*rho ;
		rho5 = rho4*rho ;

		r = CAACoordinateTransformation.MapTo0To360Range(
				280.4664567+360007.6982779*rho+0.03032028*rho2+rho3/49931-rho4/15300-rho5/2000000 ) ;

		return CAACoordinateTransformation.DegreesToRadians( r ) ;
	}

	public static double meanEclipticLatitude( double JD ) {
		return 0 ;
	}

	public static double trueEclipticLongitude( double JD ) {
		return geometricEclipticLongitude( JD ) ;
	}

	public static double trueEclipticLatitude( double JD ) {
		return geometricEclipticLatitude( JD ) ;
	}

	public static double geometricEclipticLongitude( double JD ) {
		return CAACoordinateTransformation.DegreesToRadians( CAASun.GeometricEclipticLongitude( JD ) ) ;
	}

	public static double geometricEclipticLatitude( double JD ) {
		return CAACoordinateTransformation.DegreesToRadians( CAASun.GeometricEclipticLatitude( JD ) ) ;
	}

	public static double mercuryEclipticLongitude( double JD ) {
		return CAACoordinateTransformation.DegreesToRadians( CAAMercury.EclipticLongitude( JD ) ) ;
	}

	public static double mercuryEclipticLatitude( double JD ) {
		return CAACoordinateTransformation.DegreesToRadians( CAAMercury.EclipticLatitude( JD ) ) ;
	}

	public static double venusEclipticLongitude( double JD ) {
		return CAACoordinateTransformation.DegreesToRadians( CAAVenus.EclipticLongitude( JD ) ) ;
	}

	public static double venusEclipticLatitude( double JD ) {
		return CAACoordinateTransformation.DegreesToRadians( CAAVenus.EclipticLatitude( JD ) ) ;
	}

	public static double marsEclipticLongitude( double JD ) {
		return CAACoordinateTransformation.DegreesToRadians( CAAMars.EclipticLongitude( JD ) ) ;
	}

	public static double marsEclipticLatitude( double JD ) {
		return CAACoordinateTransformation.DegreesToRadians( CAAMars.EclipticLatitude( JD ) ) ;
	}

	public static double jupiterEclipticLongitude( double JD ) {
		return CAACoordinateTransformation.DegreesToRadians( CAAJupiter.EclipticLongitude( JD ) ) ;
	}

	public static double jupiterEclipticLatitude( double JD ) {
		return CAACoordinateTransformation.DegreesToRadians( CAAJupiter.EclipticLatitude( JD ) ) ;
	}

	public static double saturnEclipticLongitude( double JD ) {
		return CAACoordinateTransformation.DegreesToRadians( CAASaturn.EclipticLongitude( JD ) ) ;
	}

	public static double saturnEclipticLatitude( double JD ) {
		return CAACoordinateTransformation.DegreesToRadians( CAASaturn.EclipticLatitude( JD ) ) ;
	}

	public static double uranusEclipticLongitude( double JD ) {
		return CAACoordinateTransformation.DegreesToRadians( CAAUranus.EclipticLongitude( JD ) ) ;
	}

	public static double uranusEclipticLatitude( double JD ) {
		return CAACoordinateTransformation.DegreesToRadians( CAAUranus.EclipticLatitude( JD ) ) ;
	}

	public static double neptuneEclipticLongitude( double JD ) {
		return CAACoordinateTransformation.DegreesToRadians( CAANeptune.EclipticLongitude( JD ) ) ;
	}

	public static double neptuneEclipticLatitude( double JD ) {
		return CAACoordinateTransformation.DegreesToRadians( CAANeptune.EclipticLatitude( JD ) ) ;
	}

	public static double[] galactic2Equatorial( double l, double b ) {
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

	public static double[] equatorial2Galactic( double Al, double De ) {
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

	public static double[] ecliptic2Equatorial( double La, double Be, double e ) {
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

	public static double[] equatorial2Ecliptic( double Al, double De, double e ) {
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

	public static double[] horizontal2Equatorial( double az, double al, double la ) {
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

	public static double[] equatorial2Horizontal( double HA, double de, double la ) {
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
		} catch ( BackingStoreException e ) {
			throw new RuntimeException( e.toString() ) ;
		}

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
		} catch ( BackingStoreException e ) {
			throw new RuntimeException( e.toString() ) ;
		}

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
		} catch ( BackingStoreException e ) {
			throw new RuntimeException( e.toString() ) ;
		}

		return r ;
	}

	public static String getLocalizedString( String key ) {
		String v ;
		ResourceBundle rb ;

		rb = ResourceBundle.getBundle( ApplicationConstant.GC_APPLICATION ) ;

		try {
			v = rb.getString( key ) ;
		} catch ( MissingResourceException e ) {
			v = "" ;

			log.warn( e.toString() ) ;
		}

		return v ;
	} 

	public static void setupCompanionFromPeer( Object companion, Object peer ) {
		Method method[] , gm, sm ;
		String gn, sn ;
		Class[] pt ;

		method = peer.getClass().getMethods() ;
		for ( int m=0 ; m<method.length ; m++ ) {
			gn = method[m].getName() ;
			if ( gn.matches( "get[A-Z].*" ) ) {
				gm = method[m] ;

				try {
					pt = new Class[] { gm.getReturnType() } ;
					sn = gn.replaceFirst( "g", "s" ) ;
					sm = companion.getClass().getMethod( sn, pt ) ;
					sm.invoke( companion, gm.invoke( peer, (Object[]) null ) ) ;
				} catch ( NoSuchMethodException e ) {
					continue ;
				} catch ( InvocationTargetException e ) {
					throw new RuntimeException( e.toString() ) ;
				} catch ( IllegalAccessException e ) {
					throw new RuntimeException( e.toString() ) ;
				}
			}
		}
	}

	public static void emitPS( PostscriptStream ps, astrolabe.model.Annotation[] an ) throws ParameterNotValidException {
		for ( int a=0 ; a<an.length ; a++ ) {
			Annotation annotation ;

			ps.operator.gsave() ;

			annotation = AstrolabeFactory.companionOf( an[a] ) ;
			annotation.headPS( ps ) ;
			annotation.emitPS( ps ) ;
			annotation.tailPS( ps ) ;

			ps.operator.grestore() ;
		}
	}

	public static void emitPS( PostscriptStream ps, astrolabe.model.AnnotationCurved[] an ) throws ParameterNotValidException {
		for ( int a=0 ; a<an.length ; a++ ) {
			Annotation annotation ;

			ps.operator.gsave() ;

			annotation = new AnnotationCurved( an[a] ) ;
			annotation.headPS( ps ) ;
			annotation.emitPS( ps ) ;
			annotation.tailPS( ps ) ;

			ps.operator.grestore() ;
		}
	}

	public static void emitPS( PostscriptStream ps, astrolabe.model.AnnotationStraight[] an ) throws ParameterNotValidException {
		for ( int a=0 ; a<an.length ; a++ ) {
			Annotation annotation ;

			ps.operator.gsave() ;

			annotation = new AnnotationStraight( an[a] ) ;
			annotation.headPS( ps ) ;
			annotation.emitPS( ps ) ;
			annotation.tailPS( ps ) ;

			ps.operator.grestore() ;
		}
	}
}
