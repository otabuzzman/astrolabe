
package astrolabe;

public class ChartEquidistant extends ChartStereographic {

	public ChartEquidistant( astrolabe.model.ChartType chT, Astrolabe astrolabe ) {
		super( chT, astrolabe ) ;
	}

	public Vector project( double RA, double d ) {
		return null ;
	}

	public double[] unproject( double x, double y ) {
		return null ;
	}
}
