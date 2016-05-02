
package astrolabe;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

@SuppressWarnings("serial")
public class FOV extends Polygon {

	// registry key (RK_)
	public final static String RK_FOV = FOV.class.getName() ;

	public FOV( Polygon polygon ) {
		super( new GeometryFactory().createLinearRing( polygon.getExteriorRing().getCoordinates() ), null, polygon.getFactory() ) ;
	}
}
