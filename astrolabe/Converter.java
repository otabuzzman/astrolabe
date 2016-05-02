
package astrolabe;

import com.vividsolutions.jts.geom.Coordinate;

public interface Converter {
	public Coordinate convert( Coordinate local, boolean inverse ) ;
}
