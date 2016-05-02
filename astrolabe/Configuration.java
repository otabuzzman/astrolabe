
package astrolabe;

import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class Configuration {

	// message key (MK_)
	private final static String MK_DEFAULT = "default" ;

	private final static Log log = LogFactory.getLog( Configuration.class ) ;
	private static boolean verbose = false ;

	private final static StringBuffer mem = new StringBuffer() ;

	private Class<?> clazz ;
	private String instance = null ;

	public Configuration( Object clazz ) {
		this( clazz.getClass() ) ;

		Method m ;
		Object r ;

		try {
			m = clazz.getClass().getMethod( "getName", (Class[]) null ) ;
			r = m.invoke( clazz, (Object[]) null ) ;
			if ( r instanceof String && ( (String) r ).length()>0 )
				instance =
					( (String) r )
					.replaceAll( "/", "\\." ) ;
		} catch ( NoSuchMethodException e ) {
		} catch ( IllegalAccessException e ) {
		} catch ( InvocationTargetException e ) {
		}
	}

	public Configuration( Class<?> clazz ) {
		this.clazz = clazz ;
	}

	public static boolean init() {
		String file ;

		file = Configuration.class.getPackage().getName()+".preferences" ;

		try {
			Preferences.importPreferences( new FileInputStream( file ) ) ;
		} catch (Exception e) {
			if ( verbose )
				log.info( ParameterNotValidError.errmsg( file, e.getLocalizedMessage() ) ) ;
			return false ;
		}

		return true ;
	}

	public Configuration() {
		this( Configuration.class ) ;
	}

	public Preferences getNode( String node ) {
		Preferences val ;

		val = recurseC4node( clazz, node ) ;

		if ( val == null && verbose )
			log.info( ParameterNotValidError.errmsg( par4PNV( node ), msg4PNV( null ) ) ) ;
		mem.delete( 0, mem.length() ) ;

		return val ;
	}

	private Preferences recurseC4node( Class<?> clazz, String node ) {
		Preferences pref ;

		if ( clazz == null )
			return null ;

		pref = recurseI4node( clazz, instance, node ) ;
		if ( pref == null )
			return recurseC4node( clazz.getSuperclass(), node ) ;
		return pref ;
	}

	private Preferences recurseI4node( Class<?> clazz, String instance, String node ) {
		String n ;
		int p ;

		n = "/"+clazz.getName()
		.replaceAll( "\\.", "/" )
		.split( "\\$", 2 )[0] ;

		if ( instance != null )
			n += "/"+instance ;
		if ( node != null )
			n += "/"+node ;

		try {
			if ( Preferences.systemRoot().nodeExists( n ) ) {
				return Preferences.systemRoot().node( n ) ;
			}
		} catch ( BackingStoreException e ) {
			return null ;
		}

		if ( instance == null )
			return null ;

		if ( verbose )
			mem.append( "." ) ;

		p = instance.lastIndexOf( "/" ) ;
		if ( 0>p )
			return recurseI4node( clazz, null, node ) ;
		return recurseI4node( clazz, instance.substring( 0, p ), node ) ;
	}

	public Preferences getNode() {
		return getNode( null ) ;
	}

	public static Preferences getNode( Object clazz, String node ) {
		return new Configuration( clazz ).getNode( node ) ;
	}

	public static Preferences getNode( Class<?> clazz, String node ) {
		return new Configuration( clazz ).getNode( node ) ;
	}

	public String getValue( String key, String def  ) {
		String val ;

		val = recurseC4key( clazz, key ) ;

		if ( val == null && verbose )
			log.info( ParameterNotValidError.errmsg( par4PNV( key ), msg4PNV( def ) ) ) ;
		mem.delete( 0, mem.length() ) ;

		return val == null ? def : val ;
	}

	private String recurseC4key( Class<?> clazz, String key ) {
		String val ;

		if ( clazz == null )
			return null ;

		val = recurseI4key( clazz, instance, key ) ;
		if ( val == null )
			return recurseC4key( clazz.getSuperclass(), key ) ;
		return val ;
	}

	private String recurseI4key( Class<?> clazz, String instance, String key ) {
		String n, nPartOfKey, kPartOfKey, v ;
		int p, f ;

		n = "/"+clazz.getName()
		.replaceAll( "\\.", "/" )
		.split( "\\$", 2 )[0] ;

		f = key.lastIndexOf( "/" ) ;
		if ( f>0 ) {
			nPartOfKey = "/"+key.substring( 0, f ) ;
			kPartOfKey = key.substring( f+1, key.length() ) ;
		} else {
			nPartOfKey = "" ;
			kPartOfKey = key ;
		}

		if ( instance == null )
			n +=
				nPartOfKey ;
		else
			n +=
				"/"+instance
				+nPartOfKey ;
		try {
			if ( Preferences.systemRoot().nodeExists( n ) ) {
				v = Preferences.systemRoot().node( n ).get( kPartOfKey, null ) ;
				if ( v != null )
					return v ;
			}
		} catch ( BackingStoreException e ) {
			return null ;
		}

		if ( instance == null )
			return null ;

		if ( verbose )
			mem.append( "." ) ;

		p = instance.lastIndexOf( "/" ) ;
		if ( 0>p )
			return recurseI4key( clazz, null, key ) ;
		return recurseI4key( clazz, instance.substring( 0, p ), key ) ;
	}

	public boolean getValue( String key, boolean def  ) {
		String val ;

		val = recurseC4key( clazz, key ) ;
		if ( val == null )
			return def ;
		return Boolean.parseBoolean( val ) ;
	}

	public int getValue( String key, int def  ) {
		String val ;

		val = recurseC4key( clazz, key ) ;
		if ( val == null )
			return def ;
		return Integer.parseInt( val ) ;
	}

	public double getValue( String key, double def  ) {
		String val ;

		val = recurseC4key( clazz, key ) ;
		if ( val == null )
			return def ;
		return Double.parseDouble( val ) ;
	}

	public static String getValue( Object clazz, String key, String def ) {
		return new Configuration( clazz ).getValue( key, def ) ;
	}

	public static String getValue( Class<?> clazz, String key, String def ) {
		return new Configuration( clazz ).getValue( key, def ) ;
	}

	public static boolean getValue( Object clazz, String key, boolean def ) {
		return new Configuration( clazz ).getValue( key, def ) ;
	}

	public static boolean getValue( Class<?> clazz, String key, boolean def ) {
		return new Configuration( clazz ).getValue( key, def ) ;
	}

	public static int getValue( Object clazz, String key, int def ) {
		return new Configuration( clazz ).getValue( key, def ) ;
	}

	public static int getValue( Class<?> clazz, String key, int def ) {
		return new Configuration( clazz ).getValue( key, def ) ;
	}

	public static double getValue( Object clazz, String key, double def ) {
		return new Configuration( clazz ).getValue( key, def ) ;
	}

	public static double getValue( Class<?> clazz, String key, double def ) {
		return new Configuration( clazz ).getValue( key, def ) ;
	}

	public static boolean verbose() {
		return ! ( verbose = ! verbose ) ;
	}

	private String par4PNV( String key ) {
		StringBuffer buf = new StringBuffer() ;

		buf.append(
				clazz.getName()
				.replaceAll( "\\.", "/" )
				.split( "\\$", 2 )[0] ) ;
		if ( instance != null ) {
			buf.append( '/' ) ;
			buf.append(
					instance
					.replaceAll( "\\.", "/" ) ) ;			
		}
		buf.append( '/' ) ;
		buf.append( key ) ;

		return buf.toString() ;
	}

	private String msg4PNV( String def ) {
		MessageCatalog cat ;
		StringBuffer msg ;
		String fmt ;

		cat = new MessageCatalog( this ) ;
		fmt = cat.message( MK_DEFAULT, null ) ;
		if ( fmt != null ) {
			msg = new StringBuffer() ;
			msg.append( mem.toString() ) ;
			msg.append( MessageFormat.format( fmt, new Object[] { def } ) ) ;
			return msg.toString() ;
		}

		return null ;
	}
}
