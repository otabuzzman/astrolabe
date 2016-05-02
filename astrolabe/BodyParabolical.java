
package astrolabe;

import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

@SuppressWarnings("serial")
public class BodyParabolical extends astrolabe.model.BodyParabolical implements PostscriptEmitter, Baseline {

	public BodyParabolical( Projector projector ) {
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

	public Coordinate project( double angle, double shift ) {
		// TODO Auto-generated method stub
		return null;
	}

	public Coordinate tangent( double angle ) {
		// TODO Auto-generated method stub
		return null;
	}

	public double scaleMarkNth( int mark, double span ) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Coordinate[] list( final List<Double> list, double begin, double end, double shift ) {
		// TODO Auto-generated method stub
		return null;
	}
}
