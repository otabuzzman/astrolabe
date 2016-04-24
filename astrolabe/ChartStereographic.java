
package astrolabe;

@SuppressWarnings("serial")
public class ChartStereographic extends astrolabe.model.ChartStereographic implements Chart {

	private final static String DEFAULT_PAGESIZE = "210x298" ;
	private final static double DEFAULT_UNIT = 2.834646 ;

	private double unit ;
	private boolean northern ;

	private double[] pagesize = new double[2] ;
	private double scale ;

	public ChartStereographic( Object peer ) {
		String[] pagesize ;

		ApplicationHelper.setupCompanionFromPeer( this, peer ) ;

		pagesize = ApplicationHelper.getClassNode( this, getName(),
				ApplicationConstant.PN_CHART_PAGESIZE ).get( getPagesize(), DEFAULT_PAGESIZE /*a4*/ ).split( "x" ) ;
		this.pagesize[0] = new Double( pagesize[0] ).doubleValue() ;
		this.pagesize[1] = new Double( pagesize[1] ).doubleValue() ;

		unit = ApplicationHelper.getClassNode( this, getName(),
				null ).getDouble( ApplicationConstant.PK_CHART_UNIT, DEFAULT_UNIT ) ;
		scale = java.lang.Math.min( this.pagesize[0], this.pagesize[1] )/2/Math.goldensection ;
		scale = scale*getScale()/100 ;

		northern = getHemisphere().equals( ApplicationConstant.AV_CHART_NORTHERN ) ;
	}

	public double[] project( double[] eq ) {
		return project( eq[0], eq[1] ) ;
	}

	public double[] project( double RA, double d ) {
		double[] r = new double[2] ;
		Vector v = new Vector() ;

		if ( northern ) {
			v.setX( java.lang.Math.tan( ( Math.rad90-d )/2 )*java.lang.Math.cos( RA ) ) ;
			v.setY( -java.lang.Math.tan( ( Math.rad90-d )/2 )*java.lang.Math.sin( RA ) ) ;
		} else { // southern
			v.setX( java.lang.Math.tan( ( -d )/2 )*java.lang.Math.cos( RA ) ) ;
			v.setY( -java.lang.Math.tan( ( -d )/2 )*java.lang.Math.sin( RA ) ) ;
		}

		v.scale( scale ) ;

		r[0] = v.getX() ;
		r[1] = v.getY() ;

		return r ;
	}

	public double[] unproject( double[] xy ) {
		return unproject( xy[0], xy[1] ) ;
	}

	public double[] unproject( double x, double y ) {
		double[] r = new double[2] ;
		Vector v ;

		v = new Vector( x, -y ) ;
		v.scale( 1/scale ) ;

		r[0] = java.lang.Math.atan2( v.getY(), v.getX() ) ;

		if ( northern ) {
			r[1] = Math.rad90-java.lang.Math.atan( v.getX()/java.lang.Math.cos( r[0] ) )*2 ;
		} else { // southern
			r[1] = -java.lang.Math.atan( v.getX()/java.lang.Math.cos( r[0] ) )*2 ;
		}

		return r ;
	}

	public double[] convert( double[] eq ) {
		return eq ;
	}

	public double[] convert( double RA, double d ) {
		return new double[] { RA, d } ;
	}

	public double[] unconvert( double[] eq ) {
		return eq ;
	}

	public double[] unconvert( double RA, double d ) {
		return new double[] { RA, d } ;
	}

	public void headPS( PostscriptStream ps ) {
		ps.dsc.beginSetup() ;
		// Set origin at center of page.
		try {
			ps.custom( ApplicationConstant.PS_PROLOG_PAGESIZE ) ;
		} catch ( ParameterNotValidException e ) {} // pagesize is considered well-defined
		ps.operator.mul( .5 ) ;
		ps.operator.exch() ;
		ps.operator.mul( .5 ) ;
		ps.operator.exch() ;
		ps.operator.translate() ;
		ps.dsc.endSetup() ;

		ps.dsc.beginPageSetup() ;
		ps.operator.scale( unit ) ;
		ps.dsc.endPageSetup() ;

		ps.dsc.page( getName(), 1 ) ;
	}

	public void tailPS( PostscriptStream ps ) {
		ps.operator.showpage() ;
		ps.dsc.pageTrailer() ;
	}
}
