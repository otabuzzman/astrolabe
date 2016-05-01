
package astrolabe;

public interface PostscriptEmitter {
	public void headPS( AstrolabePostscriptStream ps ) ;
	public void emitPS( AstrolabePostscriptStream ps ) ;
	public void tailPS( AstrolabePostscriptStream ps ) ;
}
