
package astrolabe;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

@SuppressWarnings("serial")
public class ChartPage extends astrolabe.model.ChartPage {

	private final static String DEFAULT_LAYOUT = "100x100" ;

	private double sizex ;
	private double sizey ;
	private double viewx ;
	private double viewy ;

	public ChartPage( Peer peer ) {
		String sv, sd[], l ;
		Coordinate[] c ;
		Geometry fov ;

		peer.setupCompanion( this ) ;

		sv = Configuration.getValue(
				Configuration.getClassNode( this, getName(), null ),
				getSize(), null ) ;
		if ( sv == null ) {
			sd = getSize().split( "x" ) ;
		} else {
			sd = sv.split( "x" ) ;
		}
		sizex = new Double( sd[0] ).doubleValue() ;
		sizey = new Double( sd[1] ).doubleValue() ;
		viewx = sizex*getView()/100 ;
		viewy = sizey*getView()/100 ;

		l = Configuration.getValue(
				Configuration.getClassNode( this, getName(), null ),
				ApplicationConstant.PK_CHARTPAGE_LAYOUT, DEFAULT_LAYOUT ) ;
		Registry.register( ApplicationConstant.GC_LAYOUT,
				new Layout( l, new double[] {
						-sizex/2, -sizey/2,
						sizex/2, sizey/2 } ) ) ;

		c = new Coordinate[] {
				new Coordinate( -viewx/2, -viewy/2 ),
				new Coordinate( -viewx/2, viewy/2 ),
				new Coordinate( viewx/2, viewy/2 ),
				new Coordinate( viewx/2, -viewy/2 ),
				new Coordinate( -viewx/2, -viewy/2 ),
		} ;
		fov = new GeometryFactory().createPolygon(
				new GeometryFactory().createLinearRing(
						new CoordinateArraySequence( c ) ), null ) ;
		Registry.register( ApplicationConstant.GC_FOVUNI, fov ) ;
	}

	public double sizex() {
		return sizex ;
	}

	public double sizey() {
		return sizey ;
	}

	public double viewx() {
		return viewx ;
	}

	public double viewy() {
		return viewy ;
	}
}
