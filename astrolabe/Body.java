
package astrolabe;

public interface Body {
	public void headPS( PostscriptStream ps ) ;
	public void emitPS( PostscriptStream ps ) ;
	public void tailPS( PostscriptStream ps ) ;
	// inherited methods from model
	public void setAnnotation( astrolabe.model.Annotation[] annotation ) ;
}
