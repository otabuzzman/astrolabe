
package astrolabe;

import java.lang.ThreadGroup;
import java.util.Hashtable;
import java.util.Stack;

public class Registry {

	private final static Hashtable<String, Stack<Object>>
	global = new Hashtable<String, Stack<Object>>() ;
	private final static Hashtable<ThreadGroup, Hashtable<String, Stack<Object>>>
	local = new Hashtable<ThreadGroup, Hashtable<String, Stack<Object>>>() ;

	public static Object retrieve( String key ) {
		Object value ;

		value = retrieveLocal( key ) ;
		if ( value == null )
			return retrieveGlobal( key ) ;
		return value ;
	} 

	public static Object retrieveGlobal( String key ) {
		return retrieve( global, key ) ;
	}

	public static Object retrieveLocal( String key ) {
		ThreadGroup registry ;

		registry = Thread.currentThread().getThreadGroup() ;

		if ( local.containsKey( registry ) )
			return retrieve( local.get( registry ), key ) ;
		return null ;
	}

	private static Object retrieve( Hashtable<String, Stack<Object>> registry, String key ) {
		if ( registry.containsKey( key ) )
			return registry.get( key ).peek() ;
		return null ;
	}

	public static void register( String key, Object value ) {
		Object check ;

		check = retrieveLocal( key ) ;
		if ( check == null )
			registerGlobal( key, value ) ;
		registerLocal( key, value ) ;
	}

	public static void registerGlobal( String key, Object value ) {
		register( global, key, value ) ;
	}

	public static void registerLocal( String key, Object value ) {
		ThreadGroup registry ;

		registry = Thread.currentThread().getThreadGroup() ;

		if ( ! local.containsKey( registry ) )
			local.put( registry, new Hashtable<String, Stack<Object>>() ) ;
		register( local.get( registry ), key, value ) ;
	}

	private static void register( Hashtable<String, Stack<Object>> registry, String key, Object value ) {
		Stack<Object> stack ;

		if ( registry.containsKey( key ) )
			stack = registry.get( key ) ;
		else
			stack = new Stack<Object>() ;
		stack.push( value ) ;

		registry.put( key, stack ) ;
	}

	public static Object degister( String key ) {
		Object check ;

		check = retrieveLocal( key ) ;
		if ( check == null )
			return degisterGlobal( key ) ;
		return degisterLocal( key ) ;
	}

	public static Object degisterGlobal( String key ) {
		return degister( global, key ) ;
	}

	public static Object degisterLocal( String key ) {
		ThreadGroup registry ;

		registry = Thread.currentThread().getThreadGroup() ;

		if ( local.containsKey( registry ) )
			return degister( local.get( registry ), key ) ;
		return null ;
	}

	private static Object degister( Hashtable<String, Stack<Object>> registry, String key ) {
		Stack<Object> value ;
		Object object ;

		if ( ! registry.containsKey( key ) )
			return null ;

		value = registry.get( key ) ;
		object = value.lastElement() ;

		value.pop() ;		
		if ( value.size() == 0 )
			registry.remove( key ) ;

		return object ;
	}

	public static void remove() {
		removeLocal() ;
	}

	public static void removeLocal() {
		ThreadGroup registry ;

		registry = Thread.currentThread().getThreadGroup() ;

		if ( local.containsKey( registry ) )
			local.remove( registry ) ;
	}
}
