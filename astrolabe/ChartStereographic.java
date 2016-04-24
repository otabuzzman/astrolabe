
package astrolabe;

import org.exolab.castor.xml.ValidationException;

@SuppressWarnings("serial")
public class ChartStereographic extends astrolabe.model.ChartStereographic implements Chart {

	private final static String DEFAULT_PAGESIZE = "210x297" ;
	private final static double DEFAULT_UNIT = 2.834646 ;
	private final static double DEFAULT_HALO = 4 ;
	private final static double DEFAULT_HALOMIN = .08 ;
	private final static double DEFAULT_HALOMAX = .4 ;

	private double unit ;
	private boolean northern ;

	private double[] pagesize = new double[2] ;
	private double scale ;

	private double[] origin ;

	private double halo ;
	private double halomin ;
	private double halomax ;

	public ChartStereographic( Object peer ) throws ParameterNotValidException {
		String[] pagesize ;

		ApplicationHelper.setupCompanionFromPeer( this, peer ) ;
		try {
			validate() ;
		} catch ( ValidationException e ) {
			throw new ParameterNotValidException( e.toString() ) ;
		}

		pagesize = ApplicationHelper.getClassNode( this, getName(),
				ApplicationConstant.PN_CHART_PAGESIZE ).get( getPagesize(), DEFAULT_PAGESIZE /*a4*/ ).split( "x" ) ;
		this.pagesize[0] = new Double( pagesize[0] ).doubleValue() ;
		this.pagesize[1] = new Double( pagesize[1] ).doubleValue() ;

		unit = ApplicationHelper.getClassNode( this, getName(),
				null ).getDouble( ApplicationConstant.PK_CHART_UNIT, DEFAULT_UNIT ) ;
		scale = java.lang.Math.min( this.pagesize[0], this.pagesize[1] )/2/Math.goldensection ;
		scale = scale*getScale()/100 ;

		northern = getHemisphere().equals( ApplicationConstant.AV_CHART_NORTHERN ) ;

		origin = AstrolabeFactory.valueOf( getOrigin() ) ;

		halo = ApplicationHelper.getClassNode( this, getName(),
				null ).getDouble( ApplicationConstant.PK_CHART_HALO, DEFAULT_HALO ) ;
		halomin = ApplicationHelper.getClassNode( this, getName(),
				null ).getDouble( ApplicationConstant.PK_CHART_HALOMIN, DEFAULT_HALOMIN ) ;
		halomax = ApplicationHelper.getClassNode( this, getName(),
				null ).getDouble( ApplicationConstant.PK_CHART_HALOMAX, DEFAULT_HALOMAX ) ;
	}

	public double[] project( double[] eq ) {
		return project( eq[0], eq[1] ) ;
	}

	public double[] project( double RA, double d ) {
		Vector vo, vp0 ;
		double x, y, px, py ;

		if ( northern ) {
			x = java.lang.Math.tan( ( Math.rad90-origin[2] )/2 )*java.lang.Math.cos( origin[1] ) ;
			y = -java.lang.Math.tan( ( Math.rad90-origin[2] )/2 )*java.lang.Math.sin( origin[1] ) ;
			vo = new Vector( x, y ) ;

			x = java.lang.Math.tan( ( Math.rad90-d )/2 )*java.lang.Math.cos( RA ) ;
			y = -java.lang.Math.tan( ( Math.rad90-d )/2 )*java.lang.Math.sin( RA ) ;
			vp0 = new Vector( x, y ) ;
		} else { // southern
			x = -java.lang.Math.tan( ( Math.rad90+origin[2] )/2 )*java.lang.Math.cos( origin[1] ) ;
			y = -java.lang.Math.tan( ( Math.rad90+origin[2] )/2 )*java.lang.Math.sin( origin[1] ) ;
			vo = new Vector( x, y ) ;

			x = -java.lang.Math.tan( ( Math.rad90+d )/2 )*java.lang.Math.cos( RA ) ;
			y = -java.lang.Math.tan( ( Math.rad90+d )/2 )*java.lang.Math.sin( RA ) ;
			vp0 = new Vector( x, y ) ;
		}

		vo.mul( scale ) ;
		vp0.mul( scale ) ;

		if ( vo.abs()>0 ) {
			Vector vp, vux, vuy ;

			vp = new Vector( vp0 ).sub( vo ) ;
			vuy = new Vector( vo ).mul( -1 ).scale( 1 ) ;
			vux = new Vector( vuy ).apply( new double[] { 0, 1, 0, -1, 0, 0, 0, 0, 1 } ) ;

			px = vp.dot( vux ) ;
			py = vp.dot( vuy ) ;
		} else {
			px = vp0.x ;
			py = vp0.y ;
		}

		return new double[] { px, py } ;
	}

	public double[] unproject( double[] xy ) {
		return unproject( xy[0], xy[1] ) ;
	}

	public double[] unproject( double x, double y ) {
		double[] r = new double[2] ;
		Vector vo, vp0, vp ;

		if ( northern ) {
			vo = new Vector( java.lang.Math.tan( ( Math.rad90-origin[2] )/2 )*java.lang.Math.cos( origin[1] ) ,
					-java.lang.Math.tan( ( Math.rad90-origin[2] )/2 )*java.lang.Math.sin( origin[1] ) ) ;
		} else { // southern
			vo = new Vector( -java.lang.Math.tan( ( Math.rad90+origin[2] )/2 )*java.lang.Math.cos( origin[1] ) ,
					-java.lang.Math.tan( ( Math.rad90+origin[2] )/2 )*java.lang.Math.sin( origin[1] ) ) ;
		}

		vp0 = new Vector( x, y ) ;
		vp0.mul( 1/scale ) ;

		if ( vo.abs()>0 ) {
			vo = new Vector( 0, vo.abs() ) ;
			vp = new Vector( vp0 ).sub( vo ) ;
			vp = new Vector( -vp.y, vp.x ).apply( new double[] {
					java.lang.Math.cos( origin[1] ), java.lang.Math.sin( origin[1] ), 0,
					-java.lang.Math.sin( origin[1] ), java.lang.Math.cos( origin[1] ), 0,
					0, 0, 1 } ) ;
		} else {
			vp = new Vector( vp0 ) ;
		}

		if ( northern ) {
			r[0] = java.lang.Math.atan2( -vp.y, vp.x ) ;
			r[1] = Math.rad90-java.lang.Math.atan( vp.x/java.lang.Math.cos( r[0] ) )*2 ;
		} else { // southern
			r[0] = java.lang.Math.atan2( -vp.y, -vp.x ) ;
			r[1] = -Math.rad90+java.lang.Math.atan( -vp.x/java.lang.Math.cos( r[0] ) )*2 ;
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
		} catch ( ParameterNotValidException e ) {
			throw new RuntimeException( e.toString() ) ;
		}
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

		try {
			ps.push( "/"+ApplicationConstant.PS_PROLOG_HALO ) ;
			ps.push( halo ) ;
			ps.operator.def() ;
			ps.push( "/"+ApplicationConstant.PS_PROLOG_HALOMIN ) ;
			ps.push( halomin ) ;
			ps.operator.def() ;
			ps.push( "/"+ApplicationConstant.PS_PROLOG_HALOMAX ) ;
			ps.push( halomax ) ;
			ps.operator.def() ;
		} catch ( ParameterNotValidException e ) {
			throw new RuntimeException( e.toString() ) ;
		}
	}

	public void tailPS( PostscriptStream ps ) {
		ps.operator.showpage() ;
		ps.dsc.pageTrailer() ;
	}
}
