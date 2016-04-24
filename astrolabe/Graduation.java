
package astrolabe;

public interface Graduation {
	public void headPS( PostscriptStream ps ) ;
	public void emitPS( PostscriptStream ps ) throws ParameterNotValidException ;
	public void tailPS( PostscriptStream ps ) ;
}
