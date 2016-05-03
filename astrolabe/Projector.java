
package astrolabe;

import com.vividsolutions.jts.geom.Coordinate;

public interface Projector {
	public Coordinate project( Coordinate celestial, boolean inverse ) ;
}
