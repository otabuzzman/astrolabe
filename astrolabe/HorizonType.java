
package astrolabe;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

@SuppressWarnings("serial")
abstract public class HorizonType extends astrolabe.model.HorizonType implements PostscriptEmitter, Converter {

	private Projector projector ;

	public HorizonType( Projector projector ) {
		this.projector = projector ;
	}

	public void headPS( ApplicationPostscriptStream ps ) {
		String gstate ;

		if ( ( gstate = Configuration.getValue( this, getPracticality(), null ) ) == null )
			return ;
		ps.script( gstate ) ;
	}

	public void emitPS( ApplicationPostscriptStream ps ) {
		astrolabe.model.Circle circle ;
		astrolabe.model.Body body ;
		PostscriptEmitter emitter ;

		for ( int an=0 ; an<getAnnotationStraightCount() ; an++ ) {
			emitter = new AnnotationStraight() ;
			getAnnotationStraight( an ).copyValues( emitter ) ;

			ps.op( "gsave" ) ;

			emitter.headPS( ps ) ;
			emitter.emitPS( ps ) ;
			emitter.tailPS( ps ) ;

			ps.op( "grestore" ) ;
		}

		for ( int cl=0 ; cl<getCircleCount() ; cl++ ) {
			circle = getCircle( cl ) ;

			if ( circle.getCircleParallel() != null ) {
				circle( ps, circle.getCircleParallel() ) ;
			} else if ( circle.getCircleMeridian() != null ) {
				circle( ps, circle.getCircleMeridian() ) ;
			} else if ( circle.getCircleSouthernPolar() != null ) {
				circle( ps, circle.getCircleSouthernPolar() ) ;
			} else if ( circle.getCircleNorthernPolar() != null ) {
				circle( ps, circle.getCircleNorthernPolar() ) ;
			} else if (  circle.getCircleSouthernTropic() != null ) {
				circle( ps, circle.getCircleSouthernTropic() ) ;
			} else { // circle.getCircleNorthernTropic() != null
				circle( ps, circle.getCircleNorthernTropic() ) ;
			}
		}

		for ( int bd=0 ; bd<getBodyCount() ; bd++ ) {
			body = getBody( bd ) ;

			if ( body.getBodyStellar() != null ) {
				body( ps, body.getBodyStellar() ) ;
			} else if ( body.getBodyAreal() != null ) {
				body( ps, body.getBodyAreal() ) ;
			} else if ( body.getBodyPlanet() != null ) {
				body( ps, body.getBodyPlanet() ) ;
			} else if ( body.getBodyMoon() != null ) {
				body( ps, body.getBodyMoon() ) ;
			} else if ( body.getBodySun() != null ) {
				body( ps, body.getBodySun() ) ;
			} else if ( body.getBodyElliptical() != null ) {
				body( ps, body.getBodyElliptical() ) ;
			} else { // body.getBodyParabolical() != null
				body( ps, body.getBodyParabolical() ) ;
			}
		}
	}

	public void tailPS( ApplicationPostscriptStream ps ) {
	}

	abstract public Coordinate convert( Coordinate local, boolean inverse ) ;

	private void circle( ApplicationPostscriptStream ps, astrolabe.model.CircleMeridian peer ) {
		CircleMeridian circle ;

		circle = new CircleMeridian( this, projector ) ;
		peer.copyValues( circle ) ;

		circle.register() ;
		emitPS( ps, circle ) ;
		circle.degister() ;

		if ( circle.getName() != null )
			Registry.register( circle.getName(), circle ) ;

		if ( circle.getName() != null )
			circleFOV( circle.getCircleGeometry() ) ;
	}

	private void circle( ApplicationPostscriptStream ps, astrolabe.model.CircleParallel peer ) {
		CircleParallel circle ;

		circle = new CircleParallel( this, projector ) ;
		peer.copyValues( circle ) ;

		circle.register() ;
		emitPS( ps, circle ) ;
		circle.degister() ;

		if ( circle.getName() != null )
			Registry.register( circle.getName(), circle ) ;

		if ( circle.getName() != null )
			circleFOV( circle.getCircleGeometry() ) ;
	}

	private void circle( ApplicationPostscriptStream ps, astrolabe.model.CircleNorthernPolar peer ) {
		CircleNorthernPolar circle ;

		circle = new CircleNorthernPolar( this, projector ) ;
		peer.copyValues( circle ) ;

		circle.register() ;
		emitPS( ps, circle ) ;
		circle.degister() ;

		if ( circle.getName() != null )
			Registry.register( circle.getName(), circle ) ;

		if ( circle.getName() != null )
			circleFOV( circle.getCircleGeometry() ) ;
	}

	private void circle( ApplicationPostscriptStream ps, astrolabe.model.CircleNorthernTropic peer ) {
		CircleNorthernTropic circle ;

		circle = new CircleNorthernTropic( this, projector ) ;
		peer.copyValues( circle ) ;

		circle.register() ;
		emitPS( ps, circle ) ;
		circle.degister() ;

		if ( circle.getName() != null )
			Registry.register( circle.getName(), circle ) ;

		if ( circle.getName() != null )
			circleFOV( circle.getCircleGeometry() ) ;
	}

	private void circle( ApplicationPostscriptStream ps, astrolabe.model.CircleSouthernTropic peer ) {
		CircleSouthernTropic circle ;

		circle = new CircleSouthernTropic( this, projector ) ;
		peer.copyValues( circle ) ;

		circle.register() ;
		emitPS( ps, circle ) ;
		circle.degister() ;

		if ( circle.getName() != null )
			Registry.register( circle.getName(), circle ) ;

		if ( circle.getName() != null )
			circleFOV( circle.getCircleGeometry() ) ;
	}

	private void circle( ApplicationPostscriptStream ps, astrolabe.model.CircleSouthernPolar peer ) {
		CircleSouthernPolar circle ;

		circle = new CircleSouthernPolar( this, projector ) ;
		peer.copyValues( circle ) ;

		circle.register() ;
		emitPS( ps, circle ) ;
		circle.degister() ;

		if ( circle.getName() != null )
			Registry.register( circle.getName(), circle ) ;

		if ( circle.getName() != null )
			circleFOV( circle.getCircleGeometry() ) ;
	}

	private void circleFOV( LineString line ) {
		LinearRing ring ;
		Polygon poly ;

		if ( line.isRing() ) {
			ring = new GeometryFactory().createLinearRing( line.getCoordinates() ) ;
			poly = new GeometryFactory().createPolygon( ring, null ) ;

			Registry.register( Geometry.class.getName(), poly ) ;
		}
	}

	private void body( ApplicationPostscriptStream ps, astrolabe.model.BodyStellar peer ) {
		BodyStellar body ;

		body = new BodyStellar( this, projector ) ;
		peer.copyValues( body ) ;

		body.register() ;
		emitPS( ps, body ) ;
		body.degister() ;
	}

	private void body( ApplicationPostscriptStream ps, astrolabe.model.BodyAreal peer ) {
		BodyAreal body ;

		body = new BodyAreal( this, projector ) ;
		peer.copyValues( body ) ;

		body.register() ;
		emitPS( ps, body ) ;
		body.degister() ;
	}

	private void body( ApplicationPostscriptStream ps, astrolabe.model.BodySun peer ) {
		BodySun body ;

		body = new BodySun( peer, this, projector ) ;
		peer.copyValues( body ) ;

		emitPS( ps, body ) ;
	}

	private void body( ApplicationPostscriptStream ps, astrolabe.model.BodyMoon peer ) {
		BodyMoon body ;

		body = new BodyMoon( this, projector ) ;
		peer.copyValues( body ) ;

		emitPS( ps, body ) ;
	}

	private void body( ApplicationPostscriptStream ps, astrolabe.model.BodyPlanet peer ) {
		BodyPlanet body ;

		body = new BodyPlanet( peer, this, projector ) ;
		peer.copyValues( body ) ;

		emitPS( ps, body ) ;
	}

	private void body( ApplicationPostscriptStream ps, astrolabe.model.BodyElliptical peer ) {
		BodyElliptical body ;

		body = new BodyElliptical( peer, this, projector ) ;
		peer.copyValues( body ) ;

		emitPS( ps, body ) ;
	}

	private void body( ApplicationPostscriptStream ps, astrolabe.model.BodyParabolical peer ) {
		BodyParabolical body ;

		body = new BodyParabolical( peer, this, projector ) ;
		peer.copyValues( body ) ;

		emitPS( ps, body ) ;
	}

	private void emitPS( ApplicationPostscriptStream ps, PostscriptEmitter emitter ) {
		ps.op( "gsave" ) ;

		emitter.headPS( ps ) ;
		emitter.emitPS( ps ) ;
		emitter.tailPS( ps ) ;

		ps.op( "grestore" ) ;
	}
}