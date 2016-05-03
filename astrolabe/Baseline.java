
package astrolabe;

import com.vividsolutions.jts.geom.Coordinate;

public interface Baseline {
	public Coordinate positionOfScaleMarkValue( double angle, double shift ) ;
	public Coordinate directionOfScaleMarkValue( double angle ) ;
	public double valueOfScaleMarkN( int mark, double span ) ;
	public Coordinate[] list( double begin, double end, double shift ) ;
}
