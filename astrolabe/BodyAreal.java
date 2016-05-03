
package astrolabe;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import caa.CAACoordinateTransformation;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;

@SuppressWarnings("serial")
public class BodyAreal extends astrolabe.model.BodyAreal implements PostscriptEmitter {

	// qualifier key (QK_)
	private final static String QK_STERADIAN	= "steradian" ;
	private final static String QK_SQUARDEGREE	= "squaredegree" ;

	// configuration key (CK_)
	private final static String CK_HALO			= "halo" ;
	private final static String CK_HALOMIN		= "halomin" ;
	private final static String CK_HALOMAX		= "halomax" ;
	private static final String CK_DISTANCE		= "distance" ;

	private final static double DEFAULT_HALO		= 4 ;
	private final static double DEFAULT_HALOMIN		= .08 ;
	private final static double DEFAULT_HALOMAX		= .4 ;
	private static final double DEFAULT_DISTANCE	= 0 ;

	private Converter converter ;
	private Projector projector ;

	public BodyAreal( Converter converter, Projector projector ) {
		this.converter = converter ;
		this.projector = projector ;
	}

	public void register() {
		PolygonSphere polygon ;
		double sr, sd ;
		DMS dms ;

		if ( getBodyArealTypeChoice().getPositionCount()>0 ) {
			polygon = new PolygonSphere( list() ) ;
			sr = polygon.area() ;
			if ( sr>( 2*java.lang.Math.PI ) )
				sr = 4*java.lang.Math.PI-sr ;
			sd = sr/java.lang.Math.pow( ( 2*java.lang.Math.PI/360. ), 2 ) ;
		} else {
			sr = 0 ;
			sd = 0 ;
		}

		dms = new DMS( sr ) ;
		dms.register( this, QK_STERADIAN ) ;
		dms.set( sd, -1 ) ;
		dms.register( this, QK_SQUARDEGREE ) ;
	}

	public void degister() {
		DMS.degister( this, QK_STERADIAN ) ;
		DMS.degister( this, QK_SQUARDEGREE ) ;
	}

	public void headPS( ApplicationPostscriptStream ps ) {
		String nature ;

		if ( ( nature = getNature() ) == null )
			return ;
		ps.script( Configuration.getValue( this, nature, null ) ) ;	
	}

	public void emitPS( ApplicationPostscriptStream ps ) {
		Configuration conf ;
		List<Coordinate[]> seg ;
		Coordinate[] lst ;
		Comparator<Coordinate[]> comparator = new Comparator<Coordinate[]>() {
			public int compare( Coordinate[] a, Coordinate[] b ) {
				double alen, blen ;

				alen = Vector.len( Vector.con( a ) ) ;
				blen = Vector.len( Vector.con( b ) ) ;

				return alen<blen?1:
					alen>blen?-1:
						0 ;
			}
		} ;
		FieldOfView fov ;
		Geometry gov, tmp, cut ;
		ChartPage page ;
		Coordinate xy ;
		Vector z, p ;
		double a ;
		astrolabe.model.Annotation annotation ;
		PostscriptEmitter emitter ;

		fov = (FieldOfView) Registry.retrieve( FieldOfView.class.getName() ) ;
		if ( fov != null && fov.isClosed() )
			gov = fov.makeGeometry() ;
		else {
			page = (ChartPage) Registry.retrieve( ChartPage.class.getName() ) ;
			if ( page != null )
				gov = FieldOfView.makeGeometry( page.getViewRectangle(), true ) ;
			else
				gov = null ;
		}

		lst = list() ;
		seg = new java.util.Vector<Coordinate[]>() ;

		if ( gov == null )
			seg.add( lst ) ;
		else {
			tmp = new GeometryFactory().createLineString( lst ) ;
			if ( ! gov.intersects( tmp ) )
				return ;
			cut = gov.intersection( tmp ) ;
			for ( int i=0 ; cut.getNumGeometries()>i ; i++ )
				seg.add( cut.getGeometryN( i ).getCoordinates() ) ;
			if ( seg.size()>1 )
				Collections.sort( seg, comparator ) ;
		}

		for ( int s=0 ; s<seg.size() ; s++ ) {
			ps.op( "gsave" ) ;

			ps.array( true ) ;
			for ( Coordinate c : seg.get( s ) ) {
				ps.push( c.x ) ;
				ps.push( c.y ) ;
			}
			ps.array( false ) ;

			ps.op( "newpath" ) ;
			ps.op( "gdraw" ) ;

			// halo stroke
			ps.op( "currentlinewidth" ) ;

			ps.op( "dup" ) ;
			ps.push( 100 ) ;
			ps.op( "div" ) ;
			conf = new Configuration( this ) ;
			ps.push( conf.getValue( CK_HALO, DEFAULT_HALO ) ) ; 
			ps.op( "mul" ) ;
			ps.push( conf.getValue( CK_HALOMIN, DEFAULT_HALOMIN ) ) ; 
			ps.op( "max" ) ;
			ps.push( conf.getValue( CK_HALOMAX, DEFAULT_HALOMAX ) ) ; 
			ps.op( "min" ) ;

			ps.push( 2 ) ;
			ps.op( "mul" ) ;
			ps.op( "add" ) ;
			ps.op( "gsave" ) ;
			ps.op( "setlinewidth" ) ;
			ps.push( 2 ) ;
			ps.op( "setlinecap" ) ;
			ps.array( true ) ;
			ps.array( false ) ;
			ps.push( 0 ) ;
			ps.op( "setdash" ) ;
			ps.push( 1 ) ;
			ps.op( "setgray" ) ;
			ps.op( "stroke" ) ;
			ps.op( "grestore" ) ;

			ps.op( "gsave" ) ;
			ps.op( "stroke" ) ;
			ps.op( "grestore" ) ;

			xy = seg.get( s )[ seg.get( s ).length-1 ] ;
			p = new Vector( xy ) ;
			xy = projector.project( converter.convert( new Coordinate( 0, 90 ), false ), false ) ;
			z = new Vector( xy ) ; // zenit

			z.sub( p ) ;
			a = Math.atan2( z.y, z.x )-90 ;

			ps.push( a ) ;
			ps.op( "rotate" ) ;

			if ( s == 0 && getAnnotation() != null ) {
				for ( int i=0 ; i<getAnnotationCount() ; i++ ) {
					annotation = getAnnotation( i ) ;

					if ( annotation.getAnnotationStraight() != null ) {
						emitter = annotation( annotation.getAnnotationStraight() ) ;
					} else { // annotation.getAnnotationCurved() != null
						emitter = annotation( annotation.getAnnotationCurved() ) ;
					}

					ps.op( "gsave" ) ;

					emitter.headPS( ps ) ;
					emitter.emitPS( ps ) ;
					emitter.tailPS( ps ) ;

					ps.op( "grestore" ) ;
				}
			}

			ps.op( "grestore" ) ;
		}
	}

	public void tailPS( ApplicationPostscriptStream ps ) {
	}

	public Coordinate[] list() {
		List<Coordinate> outline ;
		ShapeElliptical ellipse ;
		double lo, la, dist ;

		if ( getBodyArealTypeChoice().getPositionCount()>0 ) {
			outline = new java.util.Vector<Coordinate>() ;

			for ( Coordinate position : valueOf( getBodyArealTypeChoice().getPosition() ) ) {
				lo = CAACoordinateTransformation.MapTo0To360Range( position.x ) ;
				if ( lo>180 )
					lo = lo-360 ;

				la = CAACoordinateTransformation.MapTo0To360Range( position.y ) ;
				if ( la>180 )
					la = la-360 ;
				if ( la>90 )
					la = 180-la ;
				if ( la<-90 )
					la = -180-la ;

				outline.add( projector.project( converter.convert( new Coordinate( lo, la ), false ), false ) ) ;
			}

			dist = Configuration.getValue( this, CK_DISTANCE, DEFAULT_DISTANCE ) ;
			if ( dist>0 )
				return DouglasPeuckerSimplifier.simplify( new GeometryFactory().createLineString( outline.toArray( new Coordinate[0] ) ), dist ).getCoordinates() ;
			return outline.toArray( new Coordinate[0] ) ;
		} else {
			ellipse = new ShapeElliptical( converter, projector ) ;
			getBodyArealTypeChoice().getShapeElliptical().copyValues( ellipse ) ;

			return ellipse.list() ;
		}
	}

	private PostscriptEmitter annotation( astrolabe.model.AnnotationStraight peer ) {
		AnnotationStraight annotation ;

		annotation = new AnnotationStraight() ;
		peer.copyValues( annotation ) ;

		return annotation ;
	}

	private PostscriptEmitter annotation( astrolabe.model.AnnotationCurved peer ) {
		AnnotationCurved annotation ;

		annotation = new AnnotationCurved() ;
		peer.copyValues( annotation ) ;

		return annotation ;
	}
}
