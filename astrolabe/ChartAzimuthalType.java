
package astrolabe;

import caa.CAACoordinateTransformation;

@SuppressWarnings("serial")
abstract public class ChartAzimuthalType extends astrolabe.model.ChartAzimuthalType implements PostscriptEmitter, Projector {

	public double scale() {
		ChartPage page ;
		double[] view ;

		page = new ChartPage() ;
		getChartPage().copyValues( page ) ;

		view = page.view() ;

		return java.lang.Math.min( view[0], view[1] )/2
		/Math.goldensection
		*getScale()/100 ;
	}

	public double[] project( double RA, double d ) {
		return project( new double[] { RA, d } ) ;
	}

	public double[] project( double[] eq ) {
		double center[], phi, sin, cos ;
		Vector vp, v0 ;

		vp = new Vector( polarToWorld( hemisphereToPolar( eq ) ) ) ;

		if ( getChartPage().getCenter() == null )
			return vp.mul( scale() ).toArray() ;

		center = valueOf( getChartPage().getCenter() ) ;
		v0 = new Vector( polarToWorld( hemisphereToPolar( new double[] { center[1], center[2] } ) ) ) ;
		vp.sub( v0 ) ;
		phi = 90+Math.atan2( v0.y, v0.x ) ;
		sin = Math.sin( phi ) ;
		cos = Math.cos( phi ) ;
		vp.apply( new double[] { cos, sin, 0, -sin, cos, 0, 0, 0, 1 } ) ;

		return vp.mul( scale() ).toArray() ;
	}

	public double[] unproject( double x, double y ) {
		return unproject( new double[] { x, y } ) ;
	}

	public double[] unproject( double[] xy ) {
		double center[], phi, sin, cos ;
		Vector vp, v0 ;

		vp = new Vector( xy ) ;
		vp.mul( 1/scale() ) ;

		if ( getChartPage().getCenter() == null )
			return polarToHemisphere( worldToPolar( vp.toArray() ) ) ;

		center = valueOf( getChartPage().getCenter() ) ;
		v0 = new Vector( polarToWorld( hemisphereToPolar( new double[] { center[1], center[2] } ) ) ) ;
		phi = 90+Math.atan2( v0.y, v0.x ) ;
		sin = Math.sin( phi ) ;
		cos = Math.cos( phi ) ;
		vp.apply( new double[] { cos, -sin, 0, sin, cos, 0, 0, 0, 1 } ) ;
		vp.add( v0 ) ;

		return polarToHemisphere( worldToPolar( vp.toArray() ) ) ;
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

		page = new ChartPage() ;
		getChartPage().copyValues( page ) ;

		page.headPS( ps ) ;
		page.emitPS( ps ) ;
		page.tailPS( ps ) ;
	}

	public void emitPS( ApplicationPostscriptStream ps ) {
		astrolabe.model.Horizon horizon ;

		for ( int ho=0 ; ho<getHorizonCount() ; ho++ ) {
			horizon = getHorizon( ho ) ;

			if ( horizon.getHorizonLocal() != null ) {
				horizon( ps, horizon.getHorizonLocal() ) ;
			} else if ( horizon.getHorizonEquatorial() != null ) {
				horizon( ps, horizon.getHorizonEquatorial() ) ;
			} else if ( horizon.getHorizonEcliptical() != null ) {
				horizon( ps, horizon.getHorizonEcliptical() ) ;
			} else { // horizon.getHorizonGalactic() != null
				horizon( ps, horizon.getHorizonGalactic() ) ;
			}
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

	private void horizon( ApplicationPostscriptStream ps, astrolabe.model.HorizonLocal peer ) {
		HorizonLocal horizon ;

		horizon = new HorizonLocal( this ) ;
		peer.copyValues( horizon ) ;

		horizon.register() ;
		emitPS( ps, horizon ) ;
		horizon.degister() ;
	}

	private void horizon( ApplicationPostscriptStream ps, astrolabe.model.HorizonEquatorial peer ) {
		HorizonEquatorial horizon ;

		horizon = new HorizonEquatorial( this ) ;
		peer.copyValues( horizon ) ;

		emitPS( ps, horizon ) ;
	}

	private void horizon( ApplicationPostscriptStream ps, astrolabe.model.HorizonEcliptical peer ) {
		HorizonEcliptical horizon ;

		horizon = new HorizonEcliptical( this ) ;
		peer.copyValues( horizon ) ;

		horizon.register() ;
		emitPS( ps, horizon ) ;
		horizon.degister() ;
	}

	private void horizon( ApplicationPostscriptStream ps, astrolabe.model.HorizonGalactic peer ) {
		HorizonGalactic horizon ;

		horizon = new HorizonGalactic( this ) ;
		peer.copyValues( horizon ) ;

		horizon.register() ;
		emitPS( ps, horizon ) ;
		horizon.degister() ;
	}

	private void emitPS( ApplicationPostscriptStream ps, PostscriptEmitter emitter ) {
		ps.operator.gsave() ;

		emitter.headPS( ps ) ;
		emitter.emitPS( ps ) ;
		emitter.tailPS( ps ) ;

		ps.operator.grestore() ;
	}
}
