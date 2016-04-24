
package astrolabe;

public interface Circle {
	public double[] project( double angle ) ;
	public double[] project( double angle, double shift ) ;
	public double[] tangent( double angle ) ;
	public java.util.Vector<double[]> list() ;
	public java.util.Vector<double[]> list( double shift ) ;
	public java.util.Vector<double[]> list( double begin, double end, double shift ) ;
	public void headPS( PostscriptStream ps ) ;
	public void emitPS( PostscriptStream ps ) ;
	public void tailPS( PostscriptStream ps ) ;
	public double intersect( CircleParallel circle, boolean leading ) throws ParameterNotValidException ;
	public double intersect( CircleMeridian circle, boolean leading ) throws ParameterNotValidException ;
	public boolean probe( double angle ) ;
	public double mapIndexToAngleOfScale( int index ) ;
	public double mapIndexToAngleOfScale( double span ) ;
	public double mapIndexToAngleOfScale( int index, double span ) ;
	public double[] zenit() ;
	public boolean isParallel() ;
	public boolean isMeridian() ;
	// inherited methods from model 
	public astrolabe.model.Annotation[] getAnnotation() ;
	public astrolabe.model.Dial getDial() ;
}
