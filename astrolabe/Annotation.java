
package astrolabe;

public interface Annotation {
	public void headPS( PostscriptStream ps ) ;
	public void emitPS( PostscriptStream ps ) throws ParameterNotValidException ;
	public void tailPS( PostscriptStream ps ) ;
}
