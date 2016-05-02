
package astrolabe;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

@SuppressWarnings("serial")
public class HorizonEquatorial extends astrolabe.model.HorizonEquatorial implements PostscriptEmitter, Converter {

	private Projector projector ;

	public HorizonEquatorial( Projector projector ) {
		this.projector = projector ;
	}

	public void headPS( ApplicationPostscriptStream ps ) {
		String gstate ;

		gstate = Configuration.getValue( this, getPracticality(), null ) ;	

		if ( gstate == null || gstate.length() == 0 )
			return ;

		for ( String token : gstate.trim().split( "\\p{Space}+" ) )
			ps.push( token ) ;
	}

	public void emitPS( ApplicationPostscriptStream ps ) {
		astrolabe.model.Circle circle ;
		astrolabe.model.Body body ;
		astrolabe.model.Catalog catalog ;
		PostscriptEmitter emitter ;

		for ( int an=0 ; an<getAnnotationStraightCount() ; an++ ) {
			emitter = new AnnotationStraight() ;
			getAnnotationStraight( an ).copyValues( emitter ) ;

			ps.operator.gsave() ;

			emitter.headPS( ps ) ;
			emitter.emitPS( ps ) ;
			emitter.tailPS( ps ) ;

			ps.operator.grestore() ;
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

		for ( int ct=0 ; ct<getCatalogCount() ; ct++ ) {
			catalog = getCatalog( ct ) ;

			if ( catalog.getCatalogADC1239H() != null ) {
				emitter = catalog( catalog.getCatalogADC1239H() ) ;
			} else if (  catalog.getCatalogADC1239T() != null ) {
				emitter = catalog( catalog.getCatalogADC1239T() ) ;
			} else if ( catalog.getCatalogADC5050() != null ) {
				emitter = catalog( catalog.getCatalogADC5050() ) ;
			} else if (  catalog.getCatalogADC5109() != null ) {
				emitter = catalog( catalog.getCatalogADC5109() ) ;
			} else if ( catalog.getCatalogADC6049() != null ) {
				emitter = catalog( catalog.getCatalogADC6049() ) ;
			} else if ( catalog.getCatalogADC7118() != null ) {
				emitter = catalog( catalog.getCatalogADC7118() ) ;
			} else if ( catalog.getCatalogADC7237() != null ) {
				emitter = catalog( catalog.getCatalogADC7237() ) ;
			} else { // catalog.getCatalogDS9() != null
				emitter = catalog( catalog.getCatalogDS9() ) ;
			}

			ps.operator.gsave() ;

			emitter.headPS( ps ) ;
			emitter.emitPS( ps ) ;
			emitter.tailPS( ps ) ;

			ps.operator.grestore() ;
		}
	}

	public void tailPS( ApplicationPostscriptStream ps ) {
	}

	public Coordinate convert( Coordinate local, boolean inverse ) {
		return inverse ? inverse( local ) : convert( local ) ;
	}

	private Coordinate convert( Coordinate local ) {
		return new Coordinate( local ) ;
	}

	private Coordinate inverse( Coordinate equatorial ) {
		return new Coordinate( equatorial ) ;
	}

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

		body = new BodySun( this, projector ) ;
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

		body = new BodyPlanet( this, projector ) ;
		peer.copyValues( body ) ;

		emitPS( ps, body ) ;
	}

	private void body( ApplicationPostscriptStream ps, astrolabe.model.BodyElliptical peer ) {
		BodyElliptical body ;

		body = new BodyElliptical( this, projector ) ;
		peer.copyValues( body ) ;

		emitPS( ps, body ) ;
	}

	private void body( ApplicationPostscriptStream ps, astrolabe.model.BodyParabolical peer ) {
		BodyParabolical body ;

		body = new BodyParabolical( this, projector ) ;
		peer.copyValues( body ) ;

		emitPS( ps, body ) ;
	}

	private PostscriptEmitter catalog( astrolabe.model.CatalogADC1239H peer ) {
		CatalogADC1239H catalog ;

		catalog = new CatalogADC1239H( this, projector ) ;
		peer.copyValues( catalog ) ;

		catalog.addAllCatalogRecord() ;
		return catalog ;
	}

	private PostscriptEmitter catalog( astrolabe.model.CatalogADC1239T peer ) {
		CatalogADC1239T catalog ;

		catalog = new CatalogADC1239T( this, projector ) ;
		peer.copyValues( catalog ) ;

		catalog.addAllCatalogRecord() ;
		return catalog ;
	}

	private PostscriptEmitter catalog( astrolabe.model.CatalogADC5050 peer ) {
		CatalogADC5050 catalog ;

		catalog = new CatalogADC5050( this, projector ) ;
		peer.copyValues( catalog ) ;

		catalog.addAllCatalogRecord() ;
		return catalog ;
	}

	private PostscriptEmitter catalog( astrolabe.model.CatalogADC5109 peer ) {
		CatalogADC5109 catalog ;

		catalog = new CatalogADC5109( this, projector ) ;
		peer.copyValues( catalog ) ;

		catalog.addAllCatalogRecord() ;
		return catalog ;
	}

	private PostscriptEmitter catalog( astrolabe.model.CatalogADC6049 peer ) {
		CatalogADC6049 catalog ;

		catalog = new CatalogADC6049( this, projector ) ;
		peer.copyValues( catalog ) ;

		catalog.addAllCatalogRecord() ;
		return catalog ;
	}

	private PostscriptEmitter catalog( astrolabe.model.CatalogADC7118 peer ) {
		CatalogADC7118 catalog ;

		catalog = new CatalogADC7118( this, projector ) ;
		peer.copyValues( catalog ) ;

		catalog.addAllCatalogRecord() ;
		return catalog ;
	}

	private PostscriptEmitter catalog( astrolabe.model.CatalogADC7237 peer ) {
		CatalogADC7237 catalog ;

		catalog = new CatalogADC7237( this, projector ) ;
		peer.copyValues( catalog ) ;

		catalog.addAllCatalogRecord() ;
		return catalog ;
	}

	private PostscriptEmitter catalog( astrolabe.model.CatalogDS9 peer ) {
		CatalogDS9 catalog ;

		catalog = new CatalogDS9( this, projector ) ;
		peer.copyValues( catalog ) ;

		catalog.addAllCatalogRecord() ;
		return catalog ;
	}

	private void emitPS( ApplicationPostscriptStream ps, PostscriptEmitter emitter ) {
		ps.operator.gsave() ;

		emitter.headPS( ps ) ;
		emitter.emitPS( ps ) ;
		emitter.tailPS( ps ) ;

		ps.operator.grestore() ;
	}
}
