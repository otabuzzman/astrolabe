
package astrolabe;

public interface Baseline {
	public double[] project( double angle ) ;
	public double[] project( double angle, double shift ) ;
	public double[] tangent( double angle ) ;
	public java.util.Vector<double[]> list() ;
	public java.util.Vector<double[]> list( double shift ) ;
	public java.util.Vector<double[]> list( double begin, double end, double shift ) ;
	public boolean probe( double angle ) ;
	public double mapIndexToScale( int index ) ;
	public double mapIndexToScale( double span ) ;
	public double mapIndexToScale( int index, double span ) ;
}
