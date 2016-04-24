
package astrolabe;

public class ChartGnomonic extends ChartStereographic {

	public ChartGnomonic( astrolabe.model.ChartType chT, PostscriptStream ps ) {
		super( chT, ps ) ;

		unit = getClassNode( chT.getName(), null ).getDouble( "unit", 2.834646 ) ;

		pagesize = chT.getPagesize() ;
		scale = getClassNode( chT.getName(), "scale" ).getDouble( pagesize, 1 ) ;

		northern = chT.getHemisphere().equals( "northern" ) ? true : /*"southern"*/ false ;

		setupUserPagesize( pagesize ) ;
		setupViewerProcess( chT.getName(), ps ) ;
	}

	public Vector project( double RA, double d ) {
		Vector r = new Vector( 0, 0 ) ;

		return r.scale( scale ) ;
	}

	public double[] unproject( double x, double y ) {
		return null ;
	}
}
