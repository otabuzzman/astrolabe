
package astrolabe;

public interface PostscriptEmitter {
	public void headPS( ApplicationPostscriptStream ps ) ;
	public void emitPS( ApplicationPostscriptStream ps ) ;
	public void tailPS( ApplicationPostscriptStream ps ) ;
}
