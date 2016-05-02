
package astrolabe;

import java.util.List;

@SuppressWarnings("serial")
public class BodyParabolical extends astrolabe.model.BodyParabolical implements PostscriptEmitter, Baseline {

	public BodyParabolical( Projector projector ) {
	}

	public void headPS( AstrolabePostscriptStream ps ) {
		// TODO Auto-generated method stub
	}

	public void emitPS( AstrolabePostscriptStream ps ) {
		// TODO Auto-generated method stub
	}

	public void tailPS( AstrolabePostscriptStream ps ) {
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
