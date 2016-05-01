
package astrolabe;

@SuppressWarnings("serial")
public class HorizonEquatorial extends HorizonType {

	private Projector projector ;

	public HorizonEquatorial( Peer peer, Projector projector ) {
		super( peer, projector ) ;

		this.projector = projector ;
	}

	public double[] project( double RA, double d ) {
		return projector.project( convert( RA, d ) ) ;
	}

	public double[] unproject( double x, double y ) {
		return unconvert( projector.unproject( x, y ) ) ;
	}

	public double[] convert( double RA, double d ) {
		double[] r = new double[2] ;

		r[0] = RA ;
		r[1] = d ;

		return r ;
	}

	public double[] unconvert( double RA, double d ) {
		double[] r = new double[2] ;

		r[0] = RA ;
		r[1] = d ;

		return r ;
	}
}
