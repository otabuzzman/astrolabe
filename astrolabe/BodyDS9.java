
package astrolabe;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;

@SuppressWarnings("serial")
public class BodyDS9 extends BodyAreal {

	public BodyDS9( Converter converter, Projector projector ) {
		super( converter, projector ) ;
	}

	public Coordinate[] list() {
		LineString list ;
		Geometry simp ;

		list = new GeometryFactory().createLineString( super.list() )  ;
		simp = DouglasPeuckerSimplifier.simplify( list, .1 ) ;	

		return simp.getCoordinates() ;
	}
}
