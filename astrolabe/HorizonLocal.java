
package astrolabe;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

import caa.CAA2DCoordinate;
import caa.CAACoordinateTransformation ;
import caa.CAASidereal;

@SuppressWarnings("serial")
public class HorizonLocal extends astrolabe.model.HorizonLocal implements PostscriptEmitter, Converter {

	private Projector projector ;

	// qualifier key (QK_)
	private final static String QK_LOCALTIME	= "localtime" ;
	private final static String QK_SIDEREAL		= "sidereal" ;

	public HorizonLocal( Projector projector ) {
		this.projector = projector ;
	}

	public double longitude() {
		double lo ;

		lo = valueOf( getLongitude() ) ;

		if ( lo>180 )
			while ( lo>180 )
				lo = lo-180 ;
		if ( -180>lo )
			while ( -180>lo )
				lo = lo+180 ;

		return lo ;
	}

	public double latitude() {
		double la ;

		la = valueOf( getLatitude() ) ;

		if ( la>90 )
			while ( la>90 )
				la = la-90 ;
		if ( -90>la )
			while ( -90>la )
				la = la+90 ;

		return la ;
	}

	public double getST() {
		double jd0, gmst ;
		Double Epoch ;
		double epoch ;

		Epoch = (Double) Registry.retrieve( Epoch.class.getName() ) ;
		if ( Epoch == null )
			epoch = astrolabe.Epoch.defoult() ;
		else
			epoch = Epoch.doubleValue() ;

		jd0 = (int) ( epoch-.5 )+.5 ;
		gmst = CAASidereal.MeanGreenwichSiderealTime( jd0+utc()/24 ) ;

		return gmst+longitude()/15 ;
	}

	private double standard() {
		String utc[] ;
		int h, m ;
		double t ;

		utc = getTime().getStandard()
		.substring( 4 )
		.split( ":" ) ;
		h = Integer.parseInt( utc[0] )%24 ;
		m = 0 ;
		if ( utc.length>1 )
			m = Integer.parseInt( utc[1] )%60 ;
		t = h+m/60. ;

		return getTime().getStandard().charAt( 3 ) == '-' ? -t : t ;
	}

	private double utc() {
		return CAACoordinateTransformation.MapTo0To24Range( valueOf( getTime() )-standard() ) ;
	}

	public void register() {
		DMS dms ;

		dms = new DMS( utc()+longitude()/15 ) ;
		dms.register( this, QK_LOCALTIME ) ;
		dms.set( getST(), -1 ) ;
		dms.register( this, QK_SIDEREAL ) ;
	}

	public void degister() {
		DMS.degister( this, QK_LOCALTIME ) ;
		DMS.degister( this, QK_SIDEREAL ) ;
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

	public Coordinate convert( Coordinate local, boolean inverse ) {
		return inverse ? inverse( local ) : convert( local ) ;
	}

	private Coordinate convert( Coordinate local ) {
		CAA2DCoordinate c ;

		c = CAACoordinateTransformation.Horizontal2Equatorial( local.x, local.y, latitude() ) ;

		return new Coordinate( CAACoordinateTransformation.HoursToDegrees( getST()-c.X() ), c.Y() ) ;
	}

	private Coordinate inverse( Coordinate equatorial ) {
		double st, ra ;

		st = Math.truncate( getST() ) ;
		ra = Math.truncate( CAACoordinateTransformation.DegreesToHours( equatorial.x ) ) ;

		return new astrolabe.Coordinate(
				CAACoordinateTransformation.Equatorial2Horizontal( st-ra, equatorial.y, latitude() ) ) ;
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

	private void emitPS( ApplicationPostscriptStream ps, PostscriptEmitter emitter ) {
		ps.operator.gsave() ;

		emitter.headPS( ps ) ;
		emitter.emitPS( ps ) ;
		emitter.tailPS( ps ) ;

		ps.operator.grestore() ;
	}
}
