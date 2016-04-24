
package astrolabe;

public interface Dial {
	public void headPS( PostscriptStream ps ) ;
	public void emitPS( PostscriptStream ps ) ;
	public void tailPS( PostscriptStream ps ) ;
	public double mapIndexToAngleOfScale( int index, double span ) throws ParameterNotValidException ;
	public boolean isIndexAligned( int index, double span ) ;
	public void register( int index ) ;
	// inherited methods from model 
	public astrolabe.model.Annotation[] getAnnotation() ;
}
