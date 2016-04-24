
package astrolabe;

@SuppressWarnings("serial")
public class ChartGnomonic extends ChartStereographic {

	public ChartGnomonic( astrolabe.model.ChartGnomonic peer ) {
		setup( peer ) ;
	}

	public double[] project( double RA, double d ) {
		return null ;
	}

	public double[] unproject( double x, double y ) {
		return null ;
	}
}
