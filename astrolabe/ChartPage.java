
package astrolabe;

import java.util.List;

import org.exolab.castor.xml.ValidationException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

@SuppressWarnings("serial")
public class ChartPage extends astrolabe.model.ChartPage {

	private double[] size = new double[2] ;
	private double[] real = new double[2] ;

	public ChartPage( Peer peer ) throws ParameterNotValidException {
		String sv, sd[], lv ;
		List<double[]> cl ;
		Geometry fov ;

		peer.setupCompanion( this ) ;
		try {
			validate() ;
		} catch ( ValidationException e ) {
			throw new ParameterNotValidException( e.toString() ) ;
		}

		sv = Configuration.getValue(
				Configuration.getClassNode( this, getName(), null ),
				getSize(), null ) ;
		if ( sv == null ) {
			sd = getSize().split( "x" ) ;
		} else {
			sd = sv.split( "x" ) ;
		}
		size[0] = new Double( sd[0] ).doubleValue() ;
		size[1] = new Double( sd[1] ).doubleValue() ;
		real[0] = size[0]*getReal()/100 ;
		real[1] = size[1]*getReal()/100 ;

		lv = Configuration.getValue(
				Configuration.getClassNode( this, getName(), ApplicationConstant.PN_CHARTPAGE_LAYOUT ),
				getLayout(), null ) ;
		if ( lv == null ) {
			lv = getLayout() ;
		}
		Registry.register( ApplicationConstant.GC_LAYOUT,
				new Layout( lv, new double[] {
						-size[0]/2, -size[1]/2,
						size[0]/2, size[1]/2 } ) ) ;

		cl = new java.util.Vector<double[]>() ;
		cl.add( new double[] { -real[0]/2, -real[1]/2 } ) ;
		cl.add( new double[] { -real[0]/2, real[1]/2 } ) ;
		cl.add( new double[] { real[0]/2, real[1]/2 } ) ;
		cl.add( new double[] { real[0]/2, -real[1]/2 } ) ;
		cl.add( cl.get( 0 ) ) ;

		fov = new GeometryFactory().createPolygon(
				new GeometryFactory().createLinearRing(
						new JTSCoordinateArraySequence( cl ) ), null ) ;

		Registry.register( ApplicationConstant.GC_FOVUNI, fov ) ;
	}

	public double[] size() {
		return new double[] { size[0], size[1] } ;
	}

	public double sizex() {
		return size[0] ;
	}

	public double sizey() {
		return size[1] ;
	}

	public double[] real() {
		return new double[] { real[0], real[1] } ;
	}

	public double realx() {
		return real[0] ;
	}

	public double realy() {
		return real[1] ;
	}
}
