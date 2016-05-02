
package astrolabe;

import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;

@SuppressWarnings("serial")
public class BodyDS9 extends BodyAreal {

	public BodyDS9( Projector projector ) {
		super( projector ) ;
	}

	public List<double[]> list() {
		List<double[]> r ;
		LineString list ;
		Geometry simp ;

		list = new GeometryFactory().createLineString(
				new JTSCoordinateArraySequence( super.list() ) ) ;
		simp = DouglasPeuckerSimplifier.simplify( list, .1 ) ;	

		r = new java.util.Vector<double[]>() ;
		for ( Coordinate c : simp.getCoordinates() )
			r.add( new double[] { c.x, c.y } ) ;

		return r ;
	}
}
