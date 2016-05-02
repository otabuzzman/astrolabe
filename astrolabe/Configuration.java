
package astrolabe;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Configuration {

	public static boolean getValue( Preferences node, String key, boolean def ) {
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
				r = Configuration.getValue( node.parent(), key, def ) ;
			}
		}

		return r ;
	}

	public static int getValue( Preferences node, String key, int def ) {
		int r = def ;
		List<String> keys ;

		if ( node == null ) {
			r = def ;
		} else {
			try {
				keys = Arrays.asList( node.keys() ) ;
			} catch ( BackingStoreException e ) {
				throw new RuntimeException( e.toString() ) ;
			}
			if ( keys.contains( key ) ) {
				r = node.getInt( key, def ) ;
			} else {
				r = Configuration.getValue( node.parent(), key, def ) ;
			}
		}

		return r ;
	}

	public static double getValue( Preferences node, String key, double def ) {
		double r ;
		List<String> keys ;

		if ( node == null ) {
			r = def ;
		} else {
			try {
				keys = Arrays.asList( node.keys() ) ;
			} catch ( BackingStoreException e ) {
				throw new RuntimeException( e.toString() ) ;
			}
			if ( keys.contains( key ) ) {
				r = node.getDouble( key, def ) ;
			} else {
				r = Configuration.getValue( node.parent(), key, def ) ;
			}
		}

		return r ;
	}

	public static String getValue( Preferences node, String key, String def ) {
		String r ;
		List<String> keys ;

		if ( node == null ) {
			r = def ;
		} else {
			try {
				keys = Arrays.asList( node.keys() ) ;
			} catch ( BackingStoreException e ) {
				throw new RuntimeException( e.toString() ) ;
			}
			if ( keys.contains( key ) ) {
				r = node.get( key, def ) ;
			} else {
				r = Configuration.getValue( node.parent(), key, def ) ;
			}
		}

		return r ;
	}

	public static Preferences getClassNode( Object clazz, String instance, String qualifier ) {
		return getClassNode( clazz.getClass(), instance, qualifier) ;
	}

	public static Preferences getClassNode( Class<?> clazz, String instance, String qualifier ) {
		Preferences r, node ;
		String name ;

		if ( clazz == null ) {
			r = null ;
		} else {
			name = "/"+clazz.getName()
			.replaceAll( "\\.", "/" )
			.split( "\\$", 2 )[0] ;

			node = getClassNode( name, instance, qualifier) ;
			if ( node == null )
				node = getClassNode( clazz.getSuperclass(), instance, qualifier) ;

			r = node ;
		}

		return r ;
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
					r = Preferences.systemRoot() ;
				} else {
					pd = instance.lastIndexOf( "/" ) ;
					pi = pd<0?null:instance.substring( 0, pd ) ;

					r = Configuration.getClassNode( clazz, pi , qualifier ) ;
				}
			}
		} catch ( BackingStoreException e ) {
			throw new RuntimeException( e.toString() ) ;
		} catch ( IllegalArgumentException e ) {
			throw new RuntimeException( e.toString() ) ;
		}

		return r ;
	}
}
