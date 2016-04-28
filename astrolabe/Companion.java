
package astrolabe;

public interface Companion extends PostscriptEmitter {
	public void addToModel( Object[] modelWithArgs ) throws ParameterNotValidException ;
	public void emitAUX() ;
}
