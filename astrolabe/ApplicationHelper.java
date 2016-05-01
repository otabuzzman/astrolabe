
package astrolabe;

import java.util.HashSet;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import caa.CAACoordinateTransformation;
import caa.CAADate;
import caa.CAASun;

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

		h = CAACoordinateTransformation.DegreesToHours( hms ) ;
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
		DMS dDMS ;
		String ind ;

		dDMS = new DMS( dms ) ;

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

	public static double meanEclipticLongitude( double JD ) {
		double rho, rho2, rho3, rho4, rho5 ;

		rho = ( JD-2451545 )/365250 ;
		rho2 = rho*rho ;
		rho3 = rho2*rho ;
		rho4 = rho3*rho ;
		rho5 = rho4*rho ;

		return CAACoordinateTransformation.MapTo0To360Range(
				280.4664567+360007.6982779*rho+0.03032028*rho2+rho3/49931-rho4/15300-rho5/2000000 ) ;
	}

	public static double meanEclipticLatitude( double JD ) {
		return 0 ;
	}

	public static double trueEclipticLongitude( double JD ) {
		return CAASun.GeometricEclipticLongitude( JD ) ;
	}

	public static double trueEclipticLatitude( double JD ) {
		return CAASun.GeometricEclipticLatitude( JD ) ;
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

	public static void emitPS( PostscriptStream ps, astrolabe.model.Annotation[] an ) throws ParameterNotValidException {
		PostscriptEmitter annotation ;

		for ( int a=0 ; a<an.length ; a++ ) {
			ps.operator.gsave() ;

			annotation = AstrolabeFactory.companionOf( an[a] ) ;
			annotation.headPS( ps ) ;
			annotation.emitPS( ps ) ;
			annotation.tailPS( ps ) ;

			ps.operator.grestore() ;
		}
	}

	public static void emitPS( PostscriptStream ps, astrolabe.model.AnnotationCurved[] an ) throws ParameterNotValidException {
		PostscriptEmitter annotation ;

		for ( int a=0 ; a<an.length ; a++ ) {
			ps.operator.gsave() ;

			annotation = new AnnotationCurved( an[a] ) ;
			annotation.headPS( ps ) ;
			annotation.emitPS( ps ) ;
			annotation.tailPS( ps ) ;

			ps.operator.grestore() ;
		}
	}

	public static void emitPS( PostscriptStream ps, astrolabe.model.AnnotationStraight[] an ) throws ParameterNotValidException {
		PostscriptEmitter annotation ;

		for ( int a=0 ; a<an.length ; a++ ) {
			ps.operator.gsave() ;

			annotation = new AnnotationStraight( an[a] ) ;
			annotation.headPS( ps ) ;
			annotation.emitPS( ps ) ;
			annotation.tailPS( ps ) ;

			ps.operator.grestore() ;
		}
	}
}
