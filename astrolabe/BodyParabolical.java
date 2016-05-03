
package astrolabe;

import com.vividsolutions.jts.geom.Coordinate;

@SuppressWarnings("serial")
public class BodyParabolical extends BodyOrbitalType {

	public BodyParabolical( astrolabe.model.BodyParabolical peer, Converter converter, Projector projector ) {
		super( converter, projector ) ;
	}

	public void headPS( ApplicationPostscriptStream ps ) {
		// TODO Auto-generated method stub
	}

	public void emitPS( ApplicationPostscriptStream ps ) {
		// TODO Auto-generated method stub
	}

	public void tailPS( ApplicationPostscriptStream ps ) {
		// TODO Auto-generated method stub
	}

	public double valOfScaleMarkN( int mark, double span ) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Coordinate[] list( double begin, double end ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Coordinate jdToEquatorial( double jd ) {
		// TODO Auto-generated method stub
		return null;
	}
}
