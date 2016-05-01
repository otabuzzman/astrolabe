
package astrolabe;

import java.lang.ThreadGroup;
import java.text.MessageFormat;
import java.util.Hashtable;

public class Registry {

	private final static Hashtable<String, Object> global = new Hashtable<String, Object>() ;
	private final static Hashtable<ThreadGroup, Hashtable<String, Object>> local = new Hashtable<ThreadGroup, Hashtable<String, Object>>() ;

	public static Object retrieve( String key ) throws ParameterNotValidException {
		Object r ;

		if ( global.containsKey( key ) ) {
			r = retrieveGlobal( key ) ;
		} else {
			r = retrieveLocal( key ) ;
		}

		return r ;
	} 

	public static Object retrieveGlobal( String key ) throws ParameterNotValidException {
		return retrieve( global, key ) ;
	}

	public static Object retrieveLocal( String key ) throws ParameterNotValidException {
		Object r = null ;
		ThreadGroup registry ;

		registry = Thread.currentThread().getThreadGroup() ;

		if ( ! local.containsKey( registry ) ) {
			local.put( registry, new Hashtable<String, Object>() ) ;
		}

		r = retrieve( local.get( registry ), key ) ;

		return r ;
	}

	private static Object retrieve( Hashtable<String, Object> registry, String key ) throws ParameterNotValidException {
		Object r = null ;

		if ( registry.containsKey( key ) ) {
			r = registry.get( key ) ;
		} else {
			String msg ;

			msg = MessageCatalog.message( ApplicationConstant.GC_APPLICATION, ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
			msg = MessageFormat.format( msg, new Object[] { "\""+key+"\"", null } ) ;
			throw new ParameterNotValidException( msg ) ;
		}

		return r ;
	}

	public static void register( String key, Object value ) {
		if ( ! global.containsKey( key ) ) {
			registerLocal( key, value ) ;
		} else {
			registerGlobal( key, value ) ;
		}
	}

	public static void registerGlobal( String key, Object value ) {
		register( global, key, value ) ;
	}

	public static void registerLocal( String key, Object value ) {
		ThreadGroup registry ;

		registry = Thread.currentThread().getThreadGroup() ;

		if ( ! local.containsKey( registry ) ) {
			local.put( registry, new Hashtable<String, Object>() ) ;
		}

		register( local.get( registry ), key, value ) ;
	}

	private static void register( Hashtable<String, Object> registry, String key, Object value ) {
		registry.put( key, value ) ;
	}

	public static void remove() {
		ThreadGroup registry ;

		registry = Thread.currentThread().getThreadGroup() ;

		if ( local.containsKey( registry ) ) {
			local.remove( registry ) ;
		}
	}

	public static void registerNumber( String key, double value, int precision ) {
		double p, v ;

		p = java.lang.Math.pow( 10, precision ) ;
		v = (long) ( ( value*p+.5 ) )/p ;

		registerNumber( key, v ) ;
	}

	public static void registerNumber( String key, double value ) {
		register( key, (Object) new Double( value ) ) ;
	}

	public static void registerNumber( String key, long value ) {
		register( key, (Object) new Long( value ) ) ;
	}

	public static void registerName( String key, String value ) {
		register( key, new String( value ) ) ;
	}
}
