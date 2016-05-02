
package astrolabe;

import java.util.Date;

import caa.CAACoordinateTransformation;

@SuppressWarnings("serial")
abstract public class ChartAzimuthalType extends astrolabe.model.ChartAzimuthalType {

	// configuration key (CK_)
	private final static String CK_UNIT			= "unit" ;

	private final static double DEFAULT_UNIT	= 2.834646 ;

	private double unit ;

	public ChartAzimuthalType() {
		unit = Configuration.getValue( this, CK_UNIT, DEFAULT_UNIT ) ;
	}

	public double scale() {
		ChartPage page ;
		double[] view ;

		page = new ChartPage() ;
		getChartPage().setupCompanion( page ) ;
		page.register() ;

		view = page.view() ;

		return java.lang.Math.min( view[0], view[1] )/2
		/Math.goldensection
		*getScale()/100 ;
	}

	public double[] project( double RA, double d ) {
		return project( new double[] { RA, d } ) ;
	}

	public double[] project( double[] eq ) {
		double[] r, center ;
		Vector vp, vo, vZ, vY ;
		CoordinateSystem cs ;

		vp = new Vector( polarToWorld( hemisphereToPolar( eq ) ) ) ;

		center = ApplicationFactory.valueOf( getCenter() ) ;
		vo = new Vector( polarToWorld( hemisphereToPolar( new double[] { center[1], center[2] } ) ) ) ;
		if ( vo.abs()>0 ) {
			vZ = new Vector( 0, 0, 1 ) ;
			vY = new Vector( vo ).mul( -1 ) ;
			cs = new CoordinateSystem( vo, vZ, vY ) ;
			vp.set( cs.local( vp.toArray() ) ) ;
		}

		vp.mul( scale() ) ;

		r = new double[] { vp.x, vp.y } ;

		return r ;
	}

	public double[] unproject( double x, double y ) {
		return unproject( new double[] { x, y } ) ;
	}

	public double[] unproject( double[] xy ) {
		double[] r, center ;
		Vector vp, vo, vZ, vY ;
		CoordinateSystem cs ;

		vp = new Vector( xy ) ;
		vp.mul( 1/scale() ) ;

		center = ApplicationFactory.valueOf( getCenter() ) ;
		vo = new Vector( polarToWorld( hemisphereToPolar( new double[] { center[1], center[2] } ) ) ) ;
		if ( vo.abs()>0 ) {
			vZ = new Vector( 0, 0, 1 ) ;
			vY = new Vector( vo ).mul( -1 ) ;
			cs = new CoordinateSystem( vo, vZ, vY ) ;
			vp.set( cs.world( vp.toArray() ) ) ;
		}

		r = polarToHemisphere( worldToPolar( new double[] { vp.x, vp.y } ) ) ;

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

	public void headPS( ApplicationPostscriptStream ps ) {
		ChartPage page ;
		double[] size ;
		long seed ;


		page = new ChartPage() ;
		getChartPage().setupCompanion( page ) ;
		page.register() ;

		size = page.size() ;

		ps.dsc.beginSetup() ;

		ps.dict( true ) ;
		ps.push( "/PageSize" ) ;
		ps.array( true ) ;
		ps.push( size[0]*unit ) ;
		ps.push( size[1]*unit ) ;
		ps.array( false ) ;
		ps.dict( false ) ;
		ps.operator.setpagedevice() ;

		seed = new Date().getTime()/1000 ;
		ps.operator.srand( seed ) ;

		ps.dsc.endSetup() ;

		ps.dsc.beginPageSetup() ;

		ps.operator.scale( unit ) ;

		ps.dsc.endPageSetup() ;
		ps.dsc.page( getName(), 1 ) ;
	}

	public void emitPS( ApplicationPostscriptStream ps ) {
		ChartPage page ;
		double[] size, view ;

		page = new ChartPage() ;
		getChartPage().setupCompanion( page ) ;
		page.register() ;

		size = page.size() ;
		view = page.view() ;

		if ( size[0]>view[0] ) {
			ps.array( true ) ;
			ps.push( -view[0]/2 ) ;
			ps.push( -view[1]/2 ) ;
			ps.push( -view[0]/2 ) ;
			ps.push( view[1]/2 ) ;
			ps.push( view[0]/2 ) ;
			ps.push( view[1]/2 ) ;
			ps.push( view[0]/2 ) ;
			ps.push( -view[1]/2 ) ;
			ps.array( false ) ;

			ps.operator.newpath() ;
			ps.gdraw() ;

			ps.operator.closepath() ;
			ps.operator.stroke() ;
		}
	}

	public void tailPS( ApplicationPostscriptStream ps ) {
		ps.operator.showpage() ;
		ps.dsc.pageTrailer() ;
	}

	public double[] hemisphereToPolar( double[] eq ) {
		return getNorthern()?flipNorthern( eq ):flipSouthern( eq ) ;
	}

	public double[] polarToHemisphere( double[] p ) {
		return getNorthern()?flipNorthern( p ):flipSouthern( p ) ;
	}

	private double[] flipNorthern( double[] eq ) {
		return new double[] { CAACoordinateTransformation.MapTo0To360Range( -eq[0] ), eq[1] } ;
	}

	private double[] flipSouthern( double[] eq ) {
		return new double[] { CAACoordinateTransformation.MapTo0To360Range( 180+eq[0] ), -eq[1] } ;
	}

	public double[] polarToWorld( double[] eq ) {
		double d ;

		d = thetaToDistance( eq[1] ) ;

		return new double[] {
				d*Math.cos( eq[0] ),
				d*Math.sin( eq[0] ) } ;
	}

	public double[] worldToPolar( double[] xy ) {
		return new double[] {
				CAACoordinateTransformation.MapTo0To360Range( Math.atan2( xy[1], xy[0] ) ),
				distanceToTheta( java.lang.Math.sqrt( xy[0]*xy[0]+xy[1]*xy[1] ) ) } ;
	}

	abstract public double thetaToDistance( double de ) ;
	abstract public double distanceToTheta( double d ) ;
}
