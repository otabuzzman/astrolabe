
package astrolabe;

public class ChartGnomonic extends ChartStereographic {

	public ChartGnomonic( astrolabe.model.ChartType chT, Astrolabe astrolabe ) {
		super( chT, astrolabe ) ;
	}

	public Vector project( double RA, double d ) {
		return null ;
	}

	public double[] unproject( double x, double y ) {
		return null ;
	}
}
