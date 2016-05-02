
package astrolabe;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

import caa.CAA2DCoordinate;
import caa.CAACoordinateTransformation;
import caa.CAANutation;

@SuppressWarnings("serial")
public class HorizonEcliptical extends astrolabe.model.HorizonEcliptical implements PostscriptEmitter, Projector {

	// qualifier key (QK_)
	private final static String QK_EPSILON	= "epsilon" ;
	private final static String QK_LATITUDE	= "latitude" ;

	private Projector projector ;

	public HorizonEcliptical( Projector projector ) {
		this.projector = projector ;
	}

	public void register() {
		CAA2DCoordinate c ;
		double e ;
		DMS dms ;

		e = CAANutation.MeanObliquityOfEcliptic( epoch() ) ;
		c = CAACoordinateTransformation.Ecliptic2Equatorial( 0, 90, e ) ;

		dms = new DMS( e ) ;
		dms.register( this, QK_EPSILON ) ;
		dms.set( c.Y(), -1 ) ;
		dms.register( this, QK_LATITUDE ) ;
	}

	public void degister() {
		DMS.degister( this, QK_EPSILON ) ;
		DMS.degister( this, QK_LATITUDE ) ;
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
	}

	public void tailPS( ApplicationPostscriptStream ps ) {
	}

	public double[] project( double[] ho ) {
		return project( ho[0], ho[1] ) ;
	}

	public double[] project( double l, double b ) {
		return projector.project( convert( l, b ) ) ;
	}

	public double[] unproject( double[] xy ) {
		return unproject( xy[0], xy[1] ) ;
	}

	public double[] unproject( double x, double y ) {
		return unconvert( projector.unproject( x, y ) ) ;
	}

	public double[] convert( double[] ho ) {
		return convert( ho[0], ho[1] ) ;
	}

	public double[] convert( double l, double b ) {
		CAA2DCoordinate c ;
		double[] r = new double[2] ;
		double e ;

		e = CAANutation.MeanObliquityOfEcliptic( epoch() ) ;
		c = CAACoordinateTransformation.Ecliptic2Equatorial( l, b, e ) ;
		r[0] = CAACoordinateTransformation.HoursToDegrees( c.X() ) ;
		r[1] = c.Y() ;

		return r ;
	}

	public double[] unconvert( double[] eq ) {
		return unconvert( eq[0], eq[1] ) ;
	}

	public double[] unconvert( double RA, double d ) {
		CAA2DCoordinate c ;
		double[] r = new double[2] ;
		double e ;

		e = CAANutation.MeanObliquityOfEcliptic( epoch() ) ;
		c = CAACoordinateTransformation.Equatorial2Ecliptic( CAACoordinateTransformation.DegreesToHours( RA ), d, e ) ;
		r[0] = c.X() ;
		r[1] = c.Y() ;

		return r ;
	}

	private double epoch() {
		Double Epoch ;

		Epoch = (Double) Registry.retrieve( astrolabe.Epoch.RK_EPOCH ) ;
		if ( Epoch == null )
			return astrolabe.Epoch.defoult() ;
		return Epoch.doubleValue() ;
	}

	private void circle( ApplicationPostscriptStream ps, astrolabe.model.CircleMeridian peer ) {
		CircleMeridian circle ;

		circle = new CircleMeridian( this ) ;
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

		circle = new CircleParallel( this ) ;
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

		circle = new CircleNorthernPolar( this ) ;
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

		circle = new CircleNorthernTropic( this ) ;
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

		circle = new CircleSouthernTropic( this ) ;
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

		circle = new CircleSouthernPolar( this ) ;
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

			Registry.register( FOV.RK_FOV, poly ) ;
		}
	}

	private void body( ApplicationPostscriptStream ps, astrolabe.model.BodyStellar peer ) {
		BodyStellar body ;

		body = new BodyStellar( this ) ;
		peer.copyValues( body ) ;

		body.register() ;
		emitPS( ps, body ) ;
		body.degister() ;
	}

	private void body( ApplicationPostscriptStream ps, astrolabe.model.BodyAreal peer ) {
		BodyAreal body ;

		body = new BodyAreal( this ) ;
		peer.copyValues( body ) ;

		body.register() ;
		emitPS( ps, body ) ;
		body.degister() ;
	}

	private void body( ApplicationPostscriptStream ps, astrolabe.model.BodySun peer ) {
		BodySun body ;

		body = new BodySun( this ) ;
		peer.copyValues( body ) ;

		emitPS( ps, body ) ;
	}

	private void body( ApplicationPostscriptStream ps, astrolabe.model.BodyMoon peer ) {
		BodyMoon body ;

		body = new BodyMoon( this ) ;
		peer.copyValues( body ) ;

		emitPS( ps, body ) ;
	}

	private void body( ApplicationPostscriptStream ps, astrolabe.model.BodyPlanet peer ) {
		BodyPlanet body ;

		body = new BodyPlanet( this ) ;
		peer.copyValues( body ) ;

		emitPS( ps, body ) ;
	}

	private void body( ApplicationPostscriptStream ps, astrolabe.model.BodyElliptical peer ) {
		BodyElliptical body ;

		body = new BodyElliptical( this ) ;
		peer.copyValues( body ) ;

		emitPS( ps, body ) ;
	}

	private void body( ApplicationPostscriptStream ps, astrolabe.model.BodyParabolical peer ) {
		BodyParabolical body ;

		body = new BodyParabolical( this ) ;
		peer.copyValues( body ) ;

		emitPS( ps, body ) ;
	}

	private void emitPS( ApplicationPostscriptStream ps, PostscriptEmitter emitter ) {
		ps.operator.gsave() ;

		emitter.headPS( ps ) ;
		emitter.emitPS( ps ) ;
		emitter.tailPS( ps ) ;

		ps.operator.grestore() ;
	}
}
