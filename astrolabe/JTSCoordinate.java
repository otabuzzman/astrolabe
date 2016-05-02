
package astrolabe;

import com.vividsolutions.jts.geom.Coordinate;

@SuppressWarnings("serial")
public class JTSCoordinate extends Coordinate {

	public JTSCoordinate( double[] xy ) {
		super( xy[0], xy[1] ) ;
	}
}
