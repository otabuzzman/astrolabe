
package astrolabe;

public interface PostscriptEmitter {
	public void headPS( PostscriptStream ps ) ;
	public void emitPS( PostscriptStream ps ) ;
	public void tailPS( PostscriptStream ps ) ;
}
