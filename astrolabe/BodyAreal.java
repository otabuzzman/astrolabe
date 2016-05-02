
package astrolabe;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import caa.CAACoordinateTransformation;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

@SuppressWarnings("serial")
public class BodyAreal extends astrolabe.model.BodyAreal implements PostscriptEmitter {

	// qualifier key (QK_)
	private final static String QK_STERADIAN	= "steradian" ;
	private final static String QK_SQUARDEGREE	= "squaredegree" ;

	// configuration key (CK_)
	private final static String CK_HALO			= "halo" ;
	private final static String CK_HALOMIN		= "halomin" ;
	private final static String CK_HALOMAX		= "halomax" ;

	private final static double DEFAULT_HALO	= 4 ;
	private final static double DEFAULT_HALOMIN	= .08 ;
	private final static double DEFAULT_HALOMAX	= .4 ;

	private Projector projector ;

	public BodyAreal( Projector projector ) {
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
		String gstate ;

		gstate = Configuration.getValue( this, getNature(), null ) ;	

		if ( gstate == null || gstate.length() == 0 )
			return ;

		for ( String token : gstate.trim().split( "\\p{Space}+" ) )
			ps.push( token ) ;
	}

	public void emitPS( ApplicationPostscriptStream ps ) {
		Configuration conf ;
		ListCutter cutter ;
		List<Coordinate[]> segment ;
		Coordinate[] list ;
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
		Geometry fov ;
		ChartPage page ;
		Coordinate xy ;
		Vector z, p ;
		double a ;
		astrolabe.model.Annotation annotation ;
		PostscriptEmitter emitter ;

		fov = (Geometry) Registry.retrieve( FOV.RK_FOV ) ;
		if ( fov == null ) {
			page = (ChartPage) Registry.retrieve( ChartPage.RK_CHARTPAGE ) ;
			if ( page != null )
				fov = page.getViewGeometry() ;
		}

		list = list() ;
		if ( fov == null )
			( segment = new java.util.Vector<Coordinate[]>() ).add( list ) ;
		else {
			cutter = new ListCutter( list, fov ) ;
			segment = cutter.segmentsIntersecting( true ) ;
			if ( segment.size()>1 )
				Collections.sort( segment, comparator ) ;
		}

		for ( int s=0 ; s<segment.size() ; s++ ) {
			ps.operator.gsave() ;

			ps.array( true ) ;
			for ( Coordinate c : segment.get( s ) ) {
				ps.push( c.x ) ;
				ps.push( c.y ) ;
			}
			ps.array( false ) ;

			ps.operator.newpath() ;
			ps.gdraw() ;

			// halo stroke
			ps.operator.currentlinewidth() ;

			ps.operator.dup() ;
			ps.operator.div( 100 ) ;
			conf = new Configuration( this ) ;
			ps.push( conf.getValue( CK_HALO, DEFAULT_HALO ) ) ; 
			ps.operator.mul() ;
			ps.push( conf.getValue( CK_HALOMIN, DEFAULT_HALOMIN ) ) ; 
			ps.max() ;
			ps.push( conf.getValue( CK_HALOMAX, DEFAULT_HALOMAX ) ) ; 
			ps.min() ;

			ps.operator.mul( 2 ) ;
			ps.operator.add() ;
			ps.operator.gsave() ;
			ps.operator.setlinewidth() ;
			ps.operator.setlinecap( 2 ) ;
			ps.operator.setdash( 0 ) ;
			ps.operator.setgray( 1 ) ;
			ps.operator.stroke() ;
			ps.operator.grestore() ;

			ps.operator.gsave() ;
			ps.operator.stroke() ;
			ps.operator.grestore() ;

			xy = segment.get( s )[ segment.get( s ).length-1 ] ;
			p = new Vector( xy ) ;
			xy = projector.project( new Coordinate( 0, 90 ), false ) ;
			z = new Vector( xy ) ; // zenit

			z.sub( p ) ;
			a = Math.atan2( z.y, z.x )-90 ;

			ps.operator.rotate( a ) ;

			if ( s == 0 && getAnnotation() != null ) {
				for ( int i=0 ; i<getAnnotationCount() ; i++ ) {
					annotation = getAnnotation( i ) ;

					if ( annotation.getAnnotationStraight() != null ) {
						emitter = annotation( annotation.getAnnotationStraight() ) ;
					} else { // annotation.getAnnotationCurved() != null
						emitter = annotation( annotation.getAnnotationCurved() ) ;
					}

					ps.operator.gsave() ;

					emitter.headPS( ps ) ;
					emitter.emitPS( ps ) ;
					emitter.tailPS( ps ) ;

					ps.operator.grestore() ;
				}
			}

			ps.operator.grestore() ;
		}
	}

	public void tailPS( ApplicationPostscriptStream ps ) {
	}

	public Coordinate[] list() {
		List<Coordinate> outline ;
		ShapeElliptical ellipse ;
		double lo, la ;

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

				outline.add( projector.project( new Coordinate( lo, la ), false ) ) ;
			}

			return outline.toArray( new Coordinate[0] ) ;
		} else {
			ellipse = new ShapeElliptical( projector ) ;
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
