
package astrolabe;

import java.util.List;

public interface Baseline {
	public double[] project( double angle ) ;
	public double[] project( double angle, double shift ) ;
	public double[] convert( double angle ) ;
	public double unconvert( double[] eq ) ;
	public double[] tangent( double angle ) ;
	public List<double[]> list() ;
	public List<double[]> list( double shift ) ;
	public List<double[]> list( double begin, double end, double shift ) ;
	public List<double[]> list( List<Double> list ) ;
	public List<double[]> list( List<Double> list, double shift ) ;
	public List<double[]> list( List<Double> list, double begin, double end, double shift ) ;
	public boolean probe( double angle ) ;
	public double mapIndexToScale( int index ) ;
	public double mapIndexToScale( double span ) ;
	public double mapIndexToScale( int index, double span ) ;
}
