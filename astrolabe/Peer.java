
package astrolabe;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Peer {

	public void setupPeer( Preferences node ) {
		String keyDis[], keyVal ;
		Constructor<?> keyCon ;
		Method keyMth ;
		Object keyObj ;
		Class<?> keyCls ;
		HashMap<Class<?>, Class<?>> jPring ; // Java primitive types and java.lang.String

		if ( ! node.name().equals( getClass().getSimpleName() ) ) {
			setupPeer( node.parent() ) ;
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

	public void setupCompanion( Object companion ) {
		ParserAttribute parser ;
		Method method[] , gm, sm ;
		String gn, sn ;
		Class<?>[] pt ;
		Object v ;

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
					v = gm.invoke( this, (Object[]) null ) ;
					if ( v instanceof String )
						sm.invoke( companion, parser.stringValue( (String) v ) ) ;
					else
						sm.invoke( companion, v ) ;
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
}
