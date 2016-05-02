
package astrolabe;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.castor.xml.BackwardCompatibilityContext;
import org.castor.xml.InternalContext;
import org.exolab.castor.xml.FieldValidator;
import org.exolab.castor.xml.NodeType;
import org.exolab.castor.xml.TypeValidator;
import org.exolab.castor.xml.ValidationContext;
import org.exolab.castor.xml.XMLClassDescriptor;
import org.exolab.castor.xml.XMLFieldDescriptor;

import com.vividsolutions.jts.geom.Coordinate;

import caa.CAACoordinateTransformation;
import caa.CAADate;

import astrolabe.model.AngleType;
import astrolabe.model.CalendarType;
import astrolabe.model.CartesianType;
import astrolabe.model.DMSType;
import astrolabe.model.DateType;
import astrolabe.model.HMSType;
import astrolabe.model.RationalType;
import astrolabe.model.SphericalType;
import astrolabe.model.TimeType;
import astrolabe.model.YMDType;

public class Peer {

	// configuration node (CN_)
	private final static String CN_DEFAULT	= "default" ;

	public void initValues() {
		Preferences node ;
		String keyDis[], keyVal ;
		Constructor<?> keyCon ;
		Method keyMth ;
		Object keyObj ;
		Class<?> keyCls ;
		HashMap<Class<?>, Class<?>> jPring ; // Java primitive types and java.lang.String

		node = Configuration.getNode( this, CN_DEFAULT ) ;
		if ( node == null )
			return ;

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

				keyMth = getClass().getMethod( keyDis[0], new Class[] { jPring.get( keyCls ) } ) ;
				keyMth.invoke( this, new Object[] { keyObj } ) ;
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

	public void copyValues( Object companion ) {
		ParserAttribute parser ;
		Method method[] , gm, sm ;
		String gn, sn, vs ;
		Class<?>[] pt ;
		Object vp ;

		parser = (ParserAttribute) Registry.retrieve( ParserAttribute.class.getName() ) ;
		if ( parser == null )
			parser = new ParserAttribute() ;

		method = getClass().getMethods() ;
		for ( int m=0 ; m<method.length ; m++ ) {
			gn = method[m].getName() ;
			if ( gn.matches( "get[A-Z].*" ) ) {
				gm = method[m] ;

				try {
					pt = new Class[] { gm.getReturnType() } ;
					sn = gn.replaceFirst( "g", "s" ) ;
					sm = companion.getClass().getMethod( sn, pt ) ;
					vp = gm.invoke( this, (Object[]) null ) ;
					if ( vp instanceof String ) {
						vs = parser.parse( (String) vp ) ;
						if ( vs != null ) {
							validate( sm.getName().substring( 3 ).toLowerCase(), vs ) ;

							sm.invoke( companion, vs ) ;
							continue ;
						}
					}
					sm.invoke( companion, vp ) ;
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

	private void validate( String field, String value ) {
		String cn, pn, sn, dn ;
		Class<?> pCls ;
		Constructor<?> pCon ;
		XMLClassDescriptor dCls ;
		XMLFieldDescriptor dFld ;
		FieldValidator vFld ;
		TypeValidator vTyp ;
		ValidationContext vCnt ;
		InternalContext iCnt ;

		try {
			cn = getClass().getName() ;
			pn = cn.substring( 0, cn.lastIndexOf( ".") ) ;
			sn = cn.substring( cn.lastIndexOf( ".")+1 ) ;
			dn = pn+".descriptors."+sn+"Descriptor" ;

			pCls = Class.forName( dn ) ;
			pCon = pCls.getConstructor( (Class[]) null ) ;
			dCls = (XMLClassDescriptor) pCon.newInstance( (Object[]) null ) ;
			dFld = dCls.getFieldDescriptor( field, null, NodeType.Attribute ) ;
			vFld = dFld.getValidator() ;
			vTyp = vFld.getTypeValidator() ;
			vCnt = new ValidationContext() ;
			iCnt = new BackwardCompatibilityContext() ;
			iCnt.setClassLoader( value.getClass().getClassLoader() ) ;
			vCnt.setInternalContext( iCnt ) ;

			vTyp.validate( value, vCnt ) ;
		} catch ( Exception e ) {
			throw new RuntimeException( e.toString() ) ;
		}
	}

	public static double valueOf( DateType date ) {
		if ( date.getJD() == null )
			return valueOf( date.getCalendar() ) ;
		else
			return valueOf( date.getJD() ) ;
	}

	public static double valueOf( CalendarType calendar ) {
		double r, t ;
		CAADate d ;
		long[] c ;

		c = valueOf( calendar.getYMD() ) ;

		if ( calendar.getTime() != null )
			t = valueOf( calendar.getTime() ) ;
		else
			t = 0 ;

		d = new CAADate( c[0], c[1], c[2]+t, true ) ;
		r = d.Julian() ;
		d.delete() ;

		return r ;
	}

	public static long[] valueOf( YMDType ymd ) {
		return new long[] { ymd.getY(), ymd.getM(), ymd.getD() } ;
	}

	public static double valueOf( TimeType time ) {
		if ( time.getRational() == null )
			return valueOf( time.getHMS() ) ;
		else
			return valueOf( time.getRational() ) ;
	}

	public static double valueOf( HMSType hms ) {
		double r ;

		r = hms.getHrs()+hms.getMin()/60.+hms.getSec()/3600 ;

		return hms.getNeg()?-r:r ;
	}

	public static double valueOf( RationalType rational ) {
		return rational.getValue() ;
	} 

	public static Coordinate[] valueOf( SphericalType[] spherical ) {
		Coordinate[] list = new Coordinate[ spherical.length ] ;

		for ( int s=0 ; s<spherical.length ; s++ )
			list[s] = valueOf( spherical[s] ) ;

		return list ;
	}

	public static Coordinate valueOf( SphericalType spherical ) {
		double lon, lat, rad ;

		lon = valueOf( spherical.getLon() ) ;
		lat = valueOf( spherical.getLat() ) ;
		if ( spherical.getRad() != null )
			rad = valueOf( spherical.getRad() ) ;
		else
			rad = 0 ;

		return new Coordinate( lon, lat, rad ) ;
	}

	public static double valueOf( AngleType angle ) {
		if ( angle.getRational() == null ) {
			if ( angle.getDMS() == null )
				return CAACoordinateTransformation.HoursToDegrees( valueOf( angle.getHMS() ) ) ;
			else
				return valueOf( angle.getDMS() ) ;
		} else
			return valueOf( angle.getRational() ) ;
	}

	public static double valueOf( DMSType dms ) {
		double r ;

		r = dms.getDeg()+dms.getMin()/60.+dms.getSec()/3600 ;

		return dms.getNeg()?-r:r ;
	}

	public static Coordinate valueOf( CartesianType cartesian ) {
		double x, y, z ;

		x = cartesian.getX() ;
		y = cartesian.getY() ;
		if ( cartesian.hasZ() ) 
			z = cartesian.getZ() ;
		else
			z = 0 ;

		return new Coordinate( x, y, z ) ;
	}
}
