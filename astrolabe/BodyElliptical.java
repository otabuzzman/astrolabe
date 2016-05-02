
package astrolabe;

import java.util.List;

@SuppressWarnings("serial")
public class BodyElliptical extends astrolabe.model.BodyElliptical implements PostscriptEmitter, Baseline {

	public BodyElliptical( Projector projector ) {
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

	public double[] project( double angle, double shift ) {
		// TODO Auto-generated method stub
		return null;
	}

	public double[] convert( double angle ) {
		// TODO Auto-generated method stub
		return null;
	}

	public double unconvert( double[] eq ) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double[] tangent( double angle ) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<double[]> list( List<Double> list, double begin, double end, double shift ) {
		// TODO Auto-generated method stub
		return null;
	}

	public double scaleMarkNth( int mark, double span ) {
		// TODO Auto-generated method stub
		return 0;
	}
}
