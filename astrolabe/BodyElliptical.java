
package astrolabe;

import java.util.Vector;

import org.exolab.castor.xml.ValidationException;

@SuppressWarnings("serial")
public class BodyElliptical extends astrolabe.model.BodyElliptical implements PostscriptEmitter, Baseline {

	public BodyElliptical( Peer peer, Projector projector ) throws ParameterNotValidException {
		peer.setupCompanion( this ) ;
		try {
			validate() ;
		} catch ( ValidationException e ) {
			throw new ParameterNotValidException( e.toString() ) ;
		}
	}

	public void emitPS(PostscriptStream ps) {
		// TODO Auto-generated method stub

	}

	public void headPS(PostscriptStream ps) {
		// TODO Auto-generated method stub

	}

	public void tailPS(PostscriptStream ps) {
		// TODO Auto-generated method stub

	}

	public double[] convert(double angle) {
		// TODO Auto-generated method stub
		return null;
	}

	public Vector<double[]> list() {
		// TODO Auto-generated method stub
		return null;
	}

	public Vector<double[]> list(double shift) {
		// TODO Auto-generated method stub
		return null;
	}

	public Vector<double[]> list(double begin, double end, double shift) {
		// TODO Auto-generated method stub
		return null;
	}

	public Vector<double[]> list(Vector<Double> list) {
		// TODO Auto-generated method stub
		return null;
	}

	public Vector<double[]> list(Vector<Double> list, double shift) {
		// TODO Auto-generated method stub
		return null;
	}

	public Vector<double[]> list(Vector<Double> list, double begin, double end,
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
