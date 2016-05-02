
package astrolabe;

import com.vividsolutions.jts.geom.Coordinate;

@SuppressWarnings("serial")
public class GraduationHalf extends GraduationSpan {

	public GraduationHalf( Coordinate origin, Coordinate tangent ) {
		super( origin, tangent ) ;
	}
}
