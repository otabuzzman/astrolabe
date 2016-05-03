
package astrolabe;

import com.vividsolutions.jts.geom.Coordinate;

public interface Baseline {
	public Vector posVecOfScaleMarkVal( double angle ) ;
	public double valOfScaleMarkN( int mark, double span ) ;
	public Coordinate[] list( double begin, double end ) ;
}
