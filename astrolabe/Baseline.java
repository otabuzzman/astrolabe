
package astrolabe;

import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

public interface Baseline {
	public Coordinate project( double angle, double shift ) ;
	public Coordinate tangent( double angle ) ;
	public double scaleMarkNth( int mark, double span ) ;
	public Coordinate[] list( final List<Double> list, double begin, double end, double shift ) ;
}
