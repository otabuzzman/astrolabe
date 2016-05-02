
package astrolabe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ApplicationResource {

	private final static Log log = LogFactory.getLog( ApplicationResource.class ) ;
	private static boolean verbose = false ;

	private final static StringBuffer mem = new StringBuffer() ;

	private ResourceBundle catalog ;

	private Class<?> clazz ;
	private String instance = null ;

	public ApplicationResource( String catalog, Object clazz ) {
		this( catalog, clazz.getClass() ) ;

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

	public ApplicationResource( String catalog, Class<?> clazz ) {
		this.catalog = ResourceBundle.getBundle( catalog ) ;
		this.clazz = clazz ;
	}

	public ApplicationResource( String catalog ) {
		this( catalog, ApplicationResource.class ) ;
	}

	public ApplicationResource( Object clazz ) {
		this( clazz.getClass() ) ;
	}

	public ApplicationResource( Class<?> clazz ) {
		this( clazz.getPackage().getName(), clazz ) ;
	}

	public ApplicationResource() {
		this( ApplicationResource.class.getPackage().getName(), ApplicationResource.class ) ;
	}

	public String getString( String key, String def  ) {
		String val ;

		val = recurseC( clazz, key ) ;
		if ( val == null && verbose )
			log.error( ParameterNotValidError.errmsg( par4PNV( key ), null ) ) ;
		mem.delete( 0, mem.length() ) ;

		return val == null ? def : val ;
	}

	private String recurseC( Class<?> clazz, String key ) {
		String val ;

		if ( clazz == null )
			return null ;

		val = recurseI( clazz, instance, key ) ;
		if ( val == null )
			return recurseC( clazz.getSuperclass(), key ) ;
		return val ;
	}

	private String recurseI( Class<?> clazz, String instance, String key ) {
		String k ;
		int p ;

		if ( instance == null )
			k =
				clazz.getName()
				+"."+key ;
		else
			k =
				clazz.getName()
				+"."+instance
				+"."+key ;
		try {
			return catalog.getString( k ) ;
		} catch ( MissingResourceException e ) {
		}

		if ( verbose )
			mem.append( "." ) ;

		if ( instance == null )
			return null ;
		p = instance.lastIndexOf( "." ) ;
		if ( 0>p )
			return recurseI( clazz, null, key ) ;
		return recurseI( clazz, instance.substring( 0, p ), key ) ;
	}

	public static boolean verbose() {
		return ! ( verbose = ! verbose ) ;
	}

	private String par4PNV( String key ) {
		StringBuffer buf = new StringBuffer() ;

		buf.append(
				clazz.getName() ) ;
		if ( instance != null ) {
			buf.append( '.' ) ;
			buf.append(
					instance
					.replaceAll( "/", "." ) ) ;			
		}
		buf.append( '.' ) ;
		buf.append( key ) ;

		return buf.toString() ;
	}
}
