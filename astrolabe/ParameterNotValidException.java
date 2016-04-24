
package astrolabe;

@SuppressWarnings("serial")
public class ParameterNotValidException extends Exception {

	public ParameterNotValidException() {
		super() ;
	}

	public ParameterNotValidException( String message ) {
		super( message ) ;
	}
}
