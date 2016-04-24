
package astrolabe;

public class ChartGnomonic extends ChartStereographic {

	public ChartGnomonic( astrolabe.model.ChartType chT, PostscriptStream ps ) {
		super( chT, ps ) ;

		String pss ;
		double[] psn ;
		double unit, scale, gs ;
		boolean northern ;

		unit = ApplicationHelper.getClassNode( this, chT.getName(), null ).getDouble( ApplicationConstant.PK_CHART_UNIT, 2.834646 ) ;
		setUnit( unit ) ;

		pss = ApplicationHelper.getClassNode( this, chT.getName(), ApplicationConstant.PN_CHART_PAGESIZE ).get( chT.getPagesize(), "210x297" /*a4*/ ) ;
		psn = parsePagesize( pss ) ;
		setPagesize( psn ) ;

		gs = ( 1+java.lang.Math.sqrt( 5 ) )/2 ;
		scale = java.lang.Math.min( psn[0], psn[1] )/gs/2 ;
		scale = scale*chT.getScale()/100 ;
		setScale( scale ) ;

		northern = chT.getHemisphere().equals( ApplicationConstant.AV_CHART_NORTHERN ) ;
		setNorthern( northern ) ;

		setupViewerProcess( chT.getName(), ps ) ;
	}

	public Vector project( double RA, double d ) {
		return null ;
	}

	public double[] unproject( double x, double y ) {
		return null ;
	}
}
