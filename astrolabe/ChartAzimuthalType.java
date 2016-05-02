
package astrolabe;

import com.vividsolutions.jts.geom.Coordinate;

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

	public Coordinate project( Coordinate coordinate, boolean inverse ) {
		return inverse ? inverse( coordinate ) : project( coordinate ) ;
	}

	private Coordinate project( Coordinate celestial ) {
		Vector vp ;

		vp = new Vector( transformCenter( sphericalToProjection( celestialToSpherical( celestial ) ), false ) ) ;
		vp.mul( scale() ) ;

		return vp.toCoordinate() ;
	}

	private Coordinate inverse( Coordinate projection ) {
		Vector vp ;

		vp = new Vector( projection ) ;
		vp.mul( 1/scale() ) ;

		return sphericalToCelestial( projectionToSpherical( transformCenter( vp.toCoordinate(), true ) ) ) ;
	}

	private Coordinate transformCenter( Coordinate projection, boolean inverse ) {
		Coordinate center ;
		Vector v0, vp ;
		double p, s, c ;

		if ( getChartPage().getCenter() == null )
			return projection ;
		center = valueOf( getChartPage().getCenter() ) ;

		v0 = new Vector( sphericalToProjection( celestialToSpherical( center ) ) ) ;
		vp = new Vector( projection ) ;
		p = 90+Math.atan2( v0.y, v0.x ) ;
		s = Math.sin( p ) ;
		c = Math.cos( p ) ;
		if ( inverse )
			vp
			.apply( new double[] { c, -s, 0, s, c, 0, 0, 0, 1 } )
			.add( v0 ) ;
		else
			vp
			.sub( v0 )
			.apply( new double[] { c, s, 0, -s, c, 0, 0, 0, 1 } ) ;

		return vp.toCoordinate() ;
	}

	private Coordinate celestialToSpherical( Coordinate celestial ) {
		return getNorthern() ?
				new Coordinate( CAACoordinateTransformation.MapTo0To360Range( -celestial.x ), celestial.y ) :
					new Coordinate( CAACoordinateTransformation.MapTo0To360Range( 180+celestial.x ), celestial.y ) ;
	}

	private Coordinate sphericalToCelestial( Coordinate spherical ) {
		return celestialToSpherical( spherical ) ;
	}

	private Coordinate sphericalToProjection( Coordinate spherical ) {
		double d ;

		d = distance( spherical.y, false ) ;

		return new Coordinate(
				d*Math.cos( spherical.x ),
				d*Math.sin( spherical.x ) ) ;
	}

	private Coordinate projectionToSpherical( Coordinate projection ) {
		return new Coordinate(
				CAACoordinateTransformation.MapTo0To360Range( Math.atan2( projection.y, projection.x ) ),
				distance( java.lang.Math.sqrt( projection.x*projection.x+projection.y*projection.y ), true ) ) ;
	}

	public Coordinate cartesian( Coordinate coordinate, boolean inverse ) {
		return inverse ? 
				sphericalToCelestial( cartesianToSpherical( coordinate ) ) :
					sphericalToCartesian( celestialToSpherical( coordinate ) ) ;
	}

	private Coordinate sphericalToCartesian( Coordinate spherical ) {
		return new Coordinate(
				Math.cos( spherical.y )*Math.cos( spherical.x ),
				Math.cos( spherical.y )*Math.sin( spherical.x ),
				Math.sin( spherical.y ) ) ;
	}

	private Coordinate cartesianToSpherical( Coordinate cartesian ) {
		double p, r, t ;

		p = Math.atan2( cartesian.y, cartesian.x ) ;

		r = java.lang.Math.sqrt( cartesian.x*cartesian.x+cartesian.y*cartesian.y+cartesian.z*cartesian.z ) ;
		t = Math.asin( cartesian.z/r ) ;

		return new Coordinate( p, t ) ;
	}

	abstract public double distance( double value, boolean inverse ) ;

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
