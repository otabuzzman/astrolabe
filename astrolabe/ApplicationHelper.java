
package astrolabe;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import caa.CAA2DCoordinate;
import caa.CAACoordinateTransformation;
import caa.CAADate;
import caa.CAAJupiter;
import caa.CAAMars;
import caa.CAAMercury;
import caa.CAAMoon;
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

		ind = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_INDICATOR_YMD_NUMBEROFYEAR ) ;
		registerNumber( key+ind, date.Year() ) ;
		ind = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_INDICATOR_YMD_NUMBEROFMONTH ) ;
		registerNumber( key+ind, date.Month() ) ;
		ind = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_INDICTAOR_YMD_NUMBEROFDAY ) ;
		registerNumber( key+ind, date.Day() ) ;
	}

	public static void registerHMS( String key, double hms ) {
		double h ;
		DMS hDMS ;
		String ind ;

		h = CAACoordinateTransformation.RadiansToHours( hms ) ;
		hDMS = new DMS( h ) ;

		ind = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_INDICTAOR_HMS_HOURS ) ;
		registerNumber( key+ind, hDMS.deg() ) ;
		ind = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_INDICTAOR_HMS_HOURMINUTES ) ;
		registerNumber( key+ind, hDMS.min() ) ;
		ind = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_INDICTAOR_HMS_HOURSECONDS ) ;
		registerNumber( key+ind, hDMS.sec() ) ;
		ind = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_INDICTAOR_HMS_HOURFRACTION ) ;
		registerNumber( key+ind, hDMS.frc() ) ;

		ind = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_INDICTAOR_SIG_MATH ) ;
		registerName( key+ind, hDMS.sign()?"-":"" ) ;
		ind = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_INDICTAOR_SIG_BOTH ) ;
		registerName( key+ind, hDMS.sign()?"-":"+" ) ;
	}

	public static void registerDMS( String key, double dms ) {
		double d ;
		DMS dDMS ;
		String ind ;

		d = CAACoordinateTransformation.RadiansToDegrees( dms ) ;
		dDMS = new DMS( d ) ;

		ind = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_INDICTAOR_DMS_DEGREES ) ;
		registerNumber( key+ind, dDMS.deg() ) ;
		ind = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_INDICTAOR_DMS_DEGREEMINUTES ) ;
		registerNumber( key+ind, dDMS.min() ) ;
		ind = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_INDICTAOR_DMS_DEGREESECONDS ) ;
		registerNumber( key+ind, dDMS.sec() ) ;
		ind = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_INDICTAOR_DMS_DEGREEFRACTION ) ;
		registerNumber( key+ind, dDMS.frc() ) ;

		ind = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_INDICTAOR_SIG_MATH ) ;
		registerName( key+ind, dDMS.sign()?"-":"" ) ;
		ind = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_INDICTAOR_SIG_BOTH ) ;
		registerName( key+ind, dDMS.sign()?"-":"+" ) ;
	}

	public static void registerNumber( String key, double value, int precision ) {
		double p, v ;

		p = java.lang.Math.pow( 10, precision ) ;
		v = (long) ( ( value*p+.5 ) )/p ;

		registerNumber( key, v ) ;
	}

	public static void registerNumber( String key, double value ) {
		Registry.register( key, (Object) new Double( value ) ) ;
	}

	public static void registerNumber( String key, long value ) {
		Registry.register( key, (Object) new Long( value ) ) ;
	}

	public static void registerName( String key, String value ) {
		Registry.register( key, new String( value ) ) ;
	}

	public static void registerAngle( String key, double value ) {
		double a ;

		a = mapTo0To360Range( value ) ;

		registerDMS( key, a ) ;
	}

	public static void registerTime( String key, double value ) {
		double t ;

		t = mapTo0To24Range( value ) ;

		registerHMS( key, t ) ;
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

	public static java.util.Vector<double[]> createReverseVector( java.util.Vector<double[]> vector ) {
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

	public static double moonEclipticLongitude( double JD ) {
		return CAACoordinateTransformation.DegreesToRadians( CAAMoon.EclipticLongitude( JD ) ) ;
	}

	public static double moonEclipticLatitude( double JD ) {
		return CAACoordinateTransformation.DegreesToRadians( CAAMoon.EclipticLatitude( JD ) ) ;
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

	public static boolean getPreferencesKV( Preferences node, String key, boolean def ) {
		boolean r = def ;
		HashSet<String> keys ;

		if ( node == null ) {
			r = def ;
		} else {
			try {
				keys = new HashSet<String>() ;
				for ( String k : node.keys() ) {
					keys.add( k ) ;
				}
			} catch ( BackingStoreException e ) {
				throw new RuntimeException( e.toString() ) ;
			}
			if ( keys.contains( key ) ) {
				r = node.getBoolean( key, def ) ;
			} else {
				r = ApplicationHelper.getPreferencesKV( node.parent(), key, def ) ;
			}
		}

		return r ;
	}

	public static int getPreferencesKV( Preferences node, String key, int def ) {
		int r = def ;
		HashSet<String> keys ;

		if ( node == null ) {
			r = def ;
		} else {
			try {
				keys = new HashSet<String>() ;
				for ( String k : node.keys() ) {
					keys.add( k ) ;
				}
			} catch ( BackingStoreException e ) {
				throw new RuntimeException( e.toString() ) ;
			}
			if ( keys.contains( key ) ) {
				r = node.getInt( key, def ) ;
			} else {
				r = ApplicationHelper.getPreferencesKV( node.parent(), key, def ) ;
			}
		}

		return r ;
	}

	public static double getPreferencesKV( Preferences node, String key, double def ) {
		double r ;
		HashSet<String> keys ;

		if ( node == null ) {
			r = def ;
		} else {
			try {
				keys = new HashSet<String>() ;
				for ( String k : node.keys() ) {
					keys.add( k ) ;
				}
			} catch ( BackingStoreException e ) {
				throw new RuntimeException( e.toString() ) ;
			}
			if ( keys.contains( key ) ) {
				r = node.getDouble( key, def ) ;
			} else {
				r = ApplicationHelper.getPreferencesKV( node.parent(), key, def ) ;
			}
		}

		return r ;
	}

	public static String getPreferencesKV( Preferences node, String key, String def ) {
		String r ;
		HashSet<String> keys ;

		if ( node == null ) {
			r = def ;
		} else {
			try {
				keys = new HashSet<String>() ;
				for ( String k : node.keys() ) {
					keys.add( k ) ;
				}
			} catch ( BackingStoreException e ) {
				throw new RuntimeException( e.toString() ) ;
			}
			if ( keys.contains( key ) ) {
				Registry.register( "node", node.name() ) ;
				r = new ParserAttribute().stringValue( node.get( key, def ) ) ;
			} else {
				r = ApplicationHelper.getPreferencesKV( node.parent(), key, def ) ;
			}
		}

		return r ;
	}

	public static Preferences getClassNode( Class<?> clazz, String instance, String qualifier ) {
		String name ;

		name = "/"+clazz.getName().replaceAll( "\\.", "/" ).split( "\\$", 2 )[0] ;

		return getClassNode( name, instance, qualifier) ;
	}

	public static Preferences getClassNode( Object clazz, String instance, String qualifier ) {
		String name ;

		name = "/"+clazz.getClass().getName().replaceAll( "\\.", "/" ).split( "\\$", 2 )[0] ;

		return getClassNode( name, instance, qualifier) ;
	} 

	public static Preferences getClassNode( String clazz, String instance, String qualifier ) {
		Preferences r ;
		String i, q, n ;
		String pi ; // parent instance
		int pd ;	// parent delimiter

		i = instance != null ? "/"+instance : "" ;
		q = qualifier != null ? "/"+qualifier : "" ;

		n = clazz+i+q ;

		try {
			if ( Preferences.systemRoot().nodeExists( n ) ) {
				r = Preferences.systemRoot().node( n ) ;
			} else {
				if ( instance == null ) {
					r = null ;
				} else {
					pd = instance.lastIndexOf( "/" ) ;
					pi = pd<0?null:instance.substring( 0, pd ) ;

					r = ApplicationHelper.getClassNode( clazz, pi , qualifier ) ;
				}
			}
		} catch ( BackingStoreException e ) {
			throw new RuntimeException( e.toString() ) ;
		} catch ( IllegalArgumentException e ) {
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
		Class<?>[] pt ;

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

	public static void setupPeerFromClassNode( Object peer, Preferences node ) {
		String keyDis[], keyVal ;
		Constructor<?> keyCon ;
		Method keyMth ;
		Object keyObj ;
		Class<?> keyCls ;
		HashMap<Class<?>, Class<?>> jPring ; // Java primitive types and java.lang.String

		if ( ! node.name().equals( peer.getClass().getSimpleName() ) ) {
			setupPeerFromClassNode( peer, node.parent() ) ;
		}

		try {
			jPring = new HashMap<Class<?>, Class<?>>() ;
			// primitives
			jPring.put( Boolean.class,		boolean.class ) ;
			jPring.put( Byte.class,			byte.class ) ;
			jPring.put( Character.class,	char.class ) ;
			jPring.put( Short.class,		short.class ) ;
			jPring.put( Integer.class,		int.class ) ;
			jPring.put( Long.class,			long.class ) ;
			jPring.put( Float.class,		float.class ) ;
			jPring.put( Double.class,		double.class ) ;
			jPring.put( Void.class,			void.class ) ;
			// convenience
			jPring.put( String.class,		String.class ) ;

			for ( String k : node.keys() ) {
				keyVal = node.get( k, null ) ;
				keyDis = k.split( "," ) ;

				keyCls = Class.forName( keyDis[1] ) ;
				keyCon = keyCls.getConstructor( new Class[] { String.class } ) ;
				keyObj = keyCon.newInstance( new Object[] { keyVal } ) ;

				keyMth = peer.getClass().getMethod( keyDis[0], new Class[] { jPring.get( keyCls ) } ) ;
				keyMth.invoke( peer, new Object[] { keyObj } ) ;
			}
		} catch ( BackingStoreException e ) {
			throw new RuntimeException( e.toString() ) ;
		} catch ( NoSuchMethodException e ) {
			throw new RuntimeException( e.toString() ) ;
		} catch ( ClassNotFoundException e ) {
			throw new RuntimeException( e.toString() ) ;
		} catch ( InvocationTargetException e ) {
			throw new RuntimeException( e.toString() ) ;
		} catch ( IllegalAccessException e ) {
			throw new RuntimeException( e.toString() ) ;
		} catch ( InstantiationException e ) {
			throw new RuntimeException( e.toString() ) ;
		}
	}

	public static void emitPS( PostscriptStream ps, astrolabe.model.Annotation[] an ) throws ParameterNotValidException {
		for ( int a=0 ; a<an.length ; a++ ) {
			PostscriptEmitter annotation ;

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
			PostscriptEmitter annotation ;

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
			PostscriptEmitter annotation ;

			ps.operator.gsave() ;

			annotation = new AnnotationStraight( an[a] ) ;
			annotation.headPS( ps ) ;
			annotation.emitPS( ps ) ;
			annotation.tailPS( ps ) ;

			ps.operator.grestore() ;
		}
	}

	public static void emitPSPracticality( PostscriptStream ps, String practicality ) {
		double r, g, b ;
		String[] p ;

		p = practicality.split( ":" ) ;

		if ( p.length>1 ) {
			r = new Double( p[0] ).doubleValue() ;
			g = new Double( p[1] ).doubleValue() ;
			b = new Double( p[2] ).doubleValue() ;
			ps.operator.setrgbcolor( r, g, b ) ;
		} else { // setgray
			ps.operator.setgray( new Double( p[0] ).doubleValue() ) ;
		}
	}

	public static void emitPSImportance( PostscriptStream ps, String importance ) {
		String[] i ;

		i = importance.split( ":" ) ;

		ps.operator.setlinewidth( new Double( i[0] ).doubleValue() ) ;
		if ( i.length>2 ) {
			ps.array( true ) ;
			ps.push( new Double( i[1] ).doubleValue() ) ;
			ps.push( new Double( i[1] ).doubleValue()*new Double( i[2] ).doubleValue() ) ;
			ps.array( false ) ;
			ps.push( 0 ) ;
			ps.operator.setdash() ;
		} else {
			ps.operator.setdash( new Double( i[1] ).doubleValue() ) ;
		}
	}

	public static Coordinate jtsToCoordinate( double[] xy ) {
		return new Coordinate( xy[0], xy[1] ) ;
	}

	public static Coordinate[] jtsToCoordinateArray( List<double[]> list ) {
		Coordinate[] r ;

		r = new Coordinate[list.size()] ;

		for ( int c=0 ; c<list.size() ; c++ ) {
			r[c] = jtsToCoordinate( list.get( c ) ) ;
		}

		return r ;
	}

	public static Point jtsToPoint( double[] xy ) {
		return new GeometryFactory().createPoint( jtsToCoordinate( xy ) ) ;
	}

	public static LineString jtsToLineString( List<double[]> list ) {
		return new GeometryFactory().createLineString( jtsToCoordinateArray( list ) ) ;
	}

	public static LinearRing jtsToLinearRing( List<double[]> list ) {
		double[] a, o ;
		Coordinate[] c ;
		java.util.Vector<double[]> l ;

		a = list.get( 0 ) ;
		o = list.get( list.size()-1 ) ;

		if ( a[0]==o[0]&&a[1]==o[1] ) {
			c = jtsToCoordinateArray( list ) ;
		} else {
			l = new java.util.Vector<double[]>() ;
			for ( double[] xy : list ) {
				l.add( xy ) ;
			}
			l.add( list.get( 0 ) ) ;
			c = jtsToCoordinateArray( l ) ;
		}

		return new GeometryFactory().createLinearRing( c ) ;
	}

	public static Polygon jtsToPolygon( java.util.Vector<double[]> list ) {
		return new GeometryFactory().createPolygon( jtsToLinearRing( list ), null ) ;
	}

	public static double[] jtsToXY( Coordinate coordinate ) {
		double[] r = new double[2] ;

		r[0] = coordinate.x ;
		r[1] = coordinate.y ;

		return r ;
	}

	public static java.util.Vector<double[]> jtsToVector( Coordinate[] coordinate ) {
		java.util.Vector<double[]> r = new java.util.Vector<double[]>() ;

		for ( Coordinate c : coordinate ) {
			r.add( jtsToXY( c ) ) ;
		}

		return r ;
	}

	public static double[] jtsToXY( Point point ) {
		return jtsToXY( point.getCoordinate() ) ;
	}

	public static java.util.Vector<double[]> jtsToVector( LineString geometry ) {
		java.util.Vector<double[]> r ;

		r = jtsToVector( geometry.getCoordinates() ) ;
		if ( r.size()>0 ) {
			r.remove( r.size()-1 ) ;
		}

		return r ;
	}

	public static java.util.Vector<double[]> jtsToVector( LinearRing geometry ) {
		java.util.Vector<double[]> r ;

		r = jtsToVector( geometry.getCoordinates() ) ;
		if ( r.size()>0 ) {
			r.remove( r.size()-1 ) ;
		}

		return r ;
	}

	public static java.util.Vector<double[]> jtsToVector( Polygon geometry ) {
		java.util.Vector<double[]> r ;

		r = jtsToVector( geometry.getCoordinates() ) ;
		if ( r.size()>0 ) {
			r.remove( r.size()-1 ) ;
		}

		return r ;
	}

	public static java.util.Vector<double[]> jtsToVector( Geometry geometry ) {
		return jtsToVector( geometry.getCoordinates() ) ;
	}

	public static void setFovGlobal( Geometry fov ) {
		Registry.register( ApplicationConstant.GC_NS_FOVG+Thread.currentThread().getId(), fov ) ;
	}

	public static Geometry getFovGlobal() {
		Geometry r ;

		try {
			r = (Geometry) Registry.retrieve( ApplicationConstant.GC_NS_FOVG+Thread.currentThread().getId() ) ;
		} catch ( ParameterNotValidException e ) {
			r = null ;
		}

		return r ;
	}

	public static void setFovEffective( Geometry fov ) {
		Registry.register( ApplicationConstant.GC_NS_FOVE+Thread.currentThread().getId(), fov ) ;
	}

	public static Geometry getFovEffective() {
		Geometry r ;

		try {
			r = (Geometry) Registry.retrieve( ApplicationConstant.GC_NS_FOVE+Thread.currentThread().getId() ) ;
		} catch ( ParameterNotValidException e ) {
			r = null ;
		}

		return r ;
	}

	public static String getFovNSMark( double[] xy ) {
		Coordinate[] fov ;
		double minx, miny, maxx, maxy, curx, cury ;
		int Q ;	// quadrant (1/I, 2/II, ...)
		int e ; // edge (1/top, 2/right, 3/bottom, 4/left)
		String ns, nsQ, nse ;

		Q = ( xy[0] >= 0 && xy[1] >= 0 ) ? 1 :				// QI
			( xy[0] < 0 && xy[1] >= 0 ) ? 2 :				// QII
				( xy[0] < 0 && xy[1] < 0 ) ? 3 : 4 ;		// QIII, QIV
		//					( xy[0] > 0 && xy[1] < 0 ) ? 4 : 0 ;	// QIV

		nsQ = Q==1?"I":Q==2?"II":Q==3?"III":Q==4?"IV":"" ;

		fov = getFovGlobal()
		.getEnvelope()
		.getCoordinates() ;

		minx = java.lang.Math.floor( fov[0].x*1000. ) ;
		miny = java.lang.Math.floor( fov[0].y*1000. ) ;
		maxx = java.lang.Math.floor( fov[2].x*1000. ) ;
		maxy = java.lang.Math.floor( fov[2].y*1000. ) ;

		curx = java.lang.Math.floor( xy[0]*1000. ) ;
		cury = java.lang.Math.floor( xy[1]*1000. ) ;

		e = cury == maxy ? 1 :				// top edge
			curx == maxx ? 2 :				// right edge
				cury == miny ? 3 :			// bottom edge
					curx == minx ? 4 : 0 ;	// left edge

		nse = e==1?"t":e==2?"r":e==3?"b":e==4?"l":"" ;

		ns = nsQ+nse ;
		if ( ! ns.equals( "" ) ) {
			ns = ns+":" ;
		}

		return ns ;
	}

	public static Polygon extendFov( Polygon fov, java.util.Vector<double[]> list ) {
		Polygon r ;
		java.util.Vector<double[]> fl ;
		Vector flo, cla, clo ;

		// - closed circle replaces fov
		// - open circle appends fov

		cla = new Vector( list.firstElement() ) ;
		clo = new Vector( list.lastElement() ) ;

		if ( cla.x==clo.x&&cla.y==clo.y ) {
			r = jtsToPolygon( list ) ;
		} else {
			fl = jtsToVector( fov ) ;
			if ( fl.size()>0 ) {
				flo = new Vector( fl.lastElement() ) ;
				if ( new Vector( flo ).sub( cla ).abs()>new Vector( flo ).sub( clo ).abs() ) {
					fl.addAll( createReverseVector( list ) ) ;
				} else {
					fl.addAll( list ) ;
				}
			} else {
				fl.addAll( list ) ;
			}
			r = jtsToPolygon( fl ) ;
		}

		return r ;
	}
}
