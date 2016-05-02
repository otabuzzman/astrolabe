
package astrolabe;

import com.vividsolutions.jts.geom.Coordinate;

public interface Projector {
	public Coordinate project( Coordinate local, boolean inverse ) ;
	public Coordinate cartesian( Coordinate local, boolean inverse ) ;
}
