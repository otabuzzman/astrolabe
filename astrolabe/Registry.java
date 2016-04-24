
package astrolabe;

import java.text.MessageFormat;

public class Registry {

	private static final java.util.Hashtable<String, Object> registry = new java.util.Hashtable<String, Object>() ;

	private Registry() {
	}

	public static Object retrieve( String key ) throws ParameterNotValidException {
		Object r ;

		r = registry.get( key ) ;

		if ( r == null ) {
			String msg ;

			msg = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
			msg = MessageFormat.format( msg, new Object[] { null, "\""+key+"\"" } ) ;
			throw new ParameterNotValidException( msg ) ;
		}

		return r ;
	} 

	public static void register( String key, Object value ) {
		registry.put( key, value ) ;
	} 
}
