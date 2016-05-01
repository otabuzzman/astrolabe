
package astrolabe;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MessageCatalog {

	final static Log log = LogFactory.getLog( MessageCatalog.class ) ;

	private ResourceBundle catalog ;

	public MessageCatalog( String name ) {
		catalog = ResourceBundle.getBundle( name ) ;
	}

	public String message( String key ) {
		return message( catalog, key ) ;
	}

	public static String message( String catalog, String key ) {
		return message( ResourceBundle.getBundle( catalog ), key ) ;
	}

	private static String message( ResourceBundle bundle, String key ) {
		String r ;

		try {
			r = bundle.getString( key ) ;
		} catch ( MissingResourceException e ) {
			r = "\"\"" ;

			log.warn( key+": "+r ) ;
		}

		return r ;
	}
}
