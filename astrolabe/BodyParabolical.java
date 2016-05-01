
package astrolabe;

import java.util.List;

@SuppressWarnings("serial")
public class BodyParabolical extends astrolabe.model.BodyParabolical implements PostscriptEmitter, Baseline {

	public BodyParabolical( Peer peer, Projector projector ) {
		peer.setupCompanion( this ) ;
	}

	public void emitPS(AstrolabePostscriptStream ps) {
		// TODO Auto-generated method stub

	}

	public void headPS(AstrolabePostscriptStream ps) {
		// TODO Auto-generated method stub

	}

	public void tailPS(AstrolabePostscriptStream ps) {
		// TODO Auto-generated method stub

	}

	public double[] convert(double angle) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<double[]> list() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<double[]> list(double shift) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<double[]> list(double begin, double end, double shift) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<double[]> list(List<Double> list) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<double[]> list(List<Double> list, double shift) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<double[]> list(List<Double> list, double begin, double end,
			double shift) {
		// TODO Auto-generated method stub
		return null;
	}

	public double mapIndexToScale(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double mapIndexToScale(double span) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double mapIndexToScale(int index, double span) {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean probe(double angle) {
		// TODO Auto-generated method stub
		return false;
	}

	public double[] project(double angle) {
		// TODO Auto-generated method stub
		return null;
	}

	public double[] project(double angle, double shift) {
		// TODO Auto-generated method stub
		return null;
	}

	public double[] tangent(double angle) {
		// TODO Auto-generated method stub
		return null;
	}

	public double unconvert(double[] eq) {
		// TODO Auto-generated method stub
		return 0;
	}
}
