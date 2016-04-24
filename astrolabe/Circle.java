
package astrolabe;

public interface Circle extends Baseline {
	public double[] convert( double angle ) ;
	public double unconvert( double[] eq ) ;
	public void headPS( PostscriptStream ps ) ;
	public void emitPS( PostscriptStream ps ) ;
	public void tailPS( PostscriptStream ps ) ;
	public double intersect( CircleParallel circle, boolean leading ) throws ParameterNotValidException ;
	public double intersect( CircleMeridian circle, boolean leading ) throws ParameterNotValidException ;
	public double[] zenit() ;
	public boolean isParallel() ;
	public boolean isMeridian() ;
	// inherited methods from model 
	public astrolabe.model.Annotation[] getAnnotation() ;
	public astrolabe.model.Dial getDial() ;
}
