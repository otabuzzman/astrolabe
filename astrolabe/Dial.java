
package astrolabe;

public interface Dial {
	public void emitPS( PostscriptStream ps ) throws ParameterNotValidException ;
	public Circle dotDot() ;
}
