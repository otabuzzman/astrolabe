
package astrolabe;

public class Registry {

	private static final java.util.Hashtable<String, Object> registry = new java.util.Hashtable<String, Object>() ;

	private Registry() {
	}

	public static Object retrieve( String key ) throws ParameterNotValidException {
		Object r ;

		r = registry.get( key ) ;

		if ( r == null ) {
			throw new ParameterNotValidException() ;
		}

		return r ;
	} 

	public static void register( String key, Object value ) throws ParameterNotValidException {

		if ( key == null ) {
			throw new ParameterNotValidException() ;
		}

		registry.put( key, value ) ;
	} 
}
