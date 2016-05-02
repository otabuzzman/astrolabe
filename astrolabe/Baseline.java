
package astrolabe;

import java.util.List;

public interface Baseline {
	public double[] project( double angle, double shift ) ;
	public double[] convert( double angle ) ;
	public double unconvert( double[] eq ) ;
	public double[] tangent( double angle ) ;
	public List<double[]> list( List<Double> list, double begin, double end, double shift ) ;
	public double scaleMarkNth( int mark, double span ) ;
}
