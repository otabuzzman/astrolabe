
package astrolabe;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

@SuppressWarnings("serial")
public class ChartPage extends astrolabe.model.ChartPage {

	private final static String DEFAULT_LAYOUT = "100x100" ;

	public void register() {
		double size[], viewx, viewy ;
		String layout ;
		Coordinate[] coordinate ;
		Geometry fov ;

		size = size() ;
		viewx = size[0]*getView()/100 ;
		viewy = size[1]*getView()/100 ;

		layout = Configuration.getValue(
				Configuration.getClassNode( this, getName(), null ),
				ApplicationConstant.PK_CHARTPAGE_LAYOUT, DEFAULT_LAYOUT ) ;
		Registry.register( ApplicationConstant.GC_LAYOUT,
				new Layout( layout, new double[] {
						-size[0]/2, -size[1]/2,
						size[0]/2, size[1]/2 } ) ) ;

		coordinate = new Coordinate[] {
				new Coordinate( -viewx/2, -viewy/2 ),
				new Coordinate( -viewx/2, viewy/2 ),
				new Coordinate( viewx/2, viewy/2 ),
				new Coordinate( viewx/2, -viewy/2 ),
				new Coordinate( -viewx/2, -viewy/2 ),
		} ;
		fov = new GeometryFactory().createPolygon(
				new GeometryFactory().createLinearRing(
						new CoordinateArraySequence( coordinate ) ), null ) ;
		Registry.register( ApplicationConstant.GC_FOVUNI, fov ) ;
	}

	public double[] size() {
		String sv, sd[] ;

		sv = Configuration.getValue(
				Configuration.getClassNode( this, getName(), null ),
				getSize(), null ) ;
		if ( sv == null ) {
			sd = getSize().split( "x" ) ;
		} else {
			sd = sv.split( "x" ) ;
		}

		return new double[] {
				new Double( sd[0] ).doubleValue(),
				new Double( sd[1] ).doubleValue() } ;
	}

	public double[] view() {
		double[] size ;

		size = size() ;

		return new double[] {
				size[0]*getView()/100,
				size[1]*getView()/100 } ;
	}
}
