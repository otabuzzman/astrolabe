
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

	public Coordinate project( Coordinate local, boolean inverse ) {
		return inverse ? inverse( local ) : project( local ) ;
	}

	public Coordinate convert( Coordinate local, boolean inverse ) {
		return project( local, inverse ) ;
	}

	private Coordinate project( Coordinate azimuthal ) {
		Coordinate center ;
		double phi, sin, cos ;
		Vector vp, v0 ;

		vp = new Vector( polarToCartesianCS( localToPolarCS( azimuthal ) ) ) ;

		if ( getChartPage().getCenter() == null )
			return vp.mul( scale() ).toCoordinate() ;

		center = valueOf( getChartPage().getCenter() ) ;
		v0 = new Vector( polarToCartesianCS( localToPolarCS( center ) ) ) ;
		vp.sub( v0 ) ;
		phi = 90+Math.atan2( v0.y, v0.x ) ;
		sin = Math.sin( phi ) ;
		cos = Math.cos( phi ) ;
		vp.apply( new double[] { cos, sin, 0, -sin, cos, 0, 0, 0, 1 } ) ;

		return vp.mul( scale() ).toCoordinate() ;
	}

	private Coordinate inverse( Coordinate cartesian ) {
		Coordinate center ;
		double phi, sin, cos ;
		Vector vp, v0 ;

		vp = new Vector( cartesian ) ;
		vp.mul( 1/scale() ) ;

		if ( getChartPage().getCenter() == null )
			return polarToLocalCS( cartesianToPolarCS( vp.toCoordinate() ) ) ;

		center = valueOf( getChartPage().getCenter() ) ;
		v0 = new Vector( polarToCartesianCS( localToPolarCS( center ) ) ) ;
		phi = 90+Math.atan2( v0.y, v0.x ) ;
		sin = Math.sin( phi ) ;
		cos = Math.cos( phi ) ;
		vp.apply( new double[] { cos, -sin, 0, sin, cos, 0, 0, 0, 1 } ) ;
		vp.add( v0 ) ;

		return polarToLocalCS( cartesianToPolarCS( vp.toCoordinate() ) ) ;
	}

	private Coordinate localToPolarCS( Coordinate local ) {
		return getNorthern()?flipNorthern( local ):flipSouthern( local ) ;
	}

	private Coordinate polarToCartesianCS( Coordinate polar ) {
		double d ;

		d = distance( polar.y, false ) ;

		return new Coordinate(
				d*Math.cos( polar.x ),
				d*Math.sin( polar.x ) ) ;
	}

	private Coordinate polarToLocalCS( Coordinate polar ) {
		return getNorthern()?flipNorthern( polar ):flipSouthern( polar ) ;
	}

	private Coordinate cartesianToPolarCS( Coordinate cartesian ) {
		return new Coordinate(
				CAACoordinateTransformation.MapTo0To360Range( Math.atan2( cartesian.y, cartesian.x ) ),
				distance( java.lang.Math.sqrt( cartesian.x*cartesian.x+cartesian.y*cartesian.y ), true ) ) ;
	}

	private Coordinate flipNorthern( Coordinate value ) {
		return new Coordinate( CAACoordinateTransformation.MapTo0To360Range( -value.x ), value.y ) ;
	}

	private Coordinate flipSouthern( Coordinate value ) {
		return new Coordinate( CAACoordinateTransformation.MapTo0To360Range( 180+value.x ), -value.y ) ;
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
