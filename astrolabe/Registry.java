
package astrolabe;

import java.lang.ThreadGroup;
import java.util.Hashtable;

public class Registry {

	private final static Hashtable<String, Object> global = new Hashtable<String, Object>() ;
	private final static Hashtable<ThreadGroup, Hashtable<String, Object>> local = new Hashtable<ThreadGroup, Hashtable<String, Object>>() ;

	public static Object retrieve( String key ) {
		if ( global.containsKey( key ) )
			return retrieveGlobal( key ) ;
		else
			return retrieveLocal( key ) ;
	} 

	public static Object retrieveGlobal( String key ) {
		return retrieve( global, key ) ;
	}

	public static Object retrieveLocal( String key ) {
		ThreadGroup registry ;

		registry = Thread.currentThread().getThreadGroup() ;

		if ( ! local.containsKey( registry ) )
			local.put( registry, new Hashtable<String, Object>() ) ;

		return retrieve( local.get( registry ), key ) ;
	}

	private static Object retrieve( Hashtable<String, Object> registry, String key ) {
		if ( registry.containsKey( key ) )
			return registry.get( key ) ;
		return null ;
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

	public static void register( String key, String value ) {
		register( key, (Object) new String( value ) ) ;
	}

	public static void register( String key, double value ) {
		register( key, new Double( value ) ) ;
	}

	public static void register( String key, long value ) {
		register( key, new Long( value ) ) ;
	}

	public static void register( String key, boolean value ) {
		register( key, new String( Boolean.toString( value ) ) ) ;
	}
}
