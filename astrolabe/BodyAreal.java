
package astrolabe;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import caa.CAACoordinateTransformation;

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

	public void headPS( ApplicationPostscriptStream ps ) {
		GSPaintStroke nature ;

		nature = new GSPaintStroke( getNature() ) ;

		nature.headPS( ps ) ;
		nature.emitPS( ps ) ;
		nature.tailPS( ps ) ;
	}

	public void emitPS( ApplicationPostscriptStream ps ) {
		Configuration conf ;
		ListCutter cutter ;
		List<List<double[]>> segment ;
		List<double[]> list ;
		Comparator<List<double[]>> comparator = new Comparator<List<double[]>>() {
			public int compare( List<double[]> a, List<double[]> b ) {
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
		double[] xy ;
		Vector z, p ;
		double a ;

		fov = (Geometry) Registry.retrieve( FOV.RK_FOV ) ;
		if ( fov == null ) {
			page = (ChartPage) Registry.retrieve( ChartPage.RK_CHARTPAGE ) ;
			if ( page != null )
				fov = page.getViewGeometry() ;
		}

		list = list() ;

		if ( fov == null )
			( segment = new java.util.Vector<List<double[]>>() ).add( list ) ;
		else {
			cutter = new ListCutter( list(), fov ) ;
			segment = cutter.segmentsIntersecting( true ) ;
			if ( segment.size()>1 )
				Collections.sort( segment, comparator ) ;
		}

		for ( int s=0 ; s<segment.size() ; s++ ) {
			ps.operator.gsave() ;

			ps.array( true ) ;
			for ( int c=0 ; c<segment.get( s ).size() ; c++ ) {
				xy = segment.get( s ).get( c ) ;
				ps.push( xy[0] ) ;
				ps.push( xy[1] ) ;
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

			xy = segment.get( s ).get( segment.get( s ).size()-1 ) ;
			p = new Vector( xy[0], xy[1] ) ;
			xy = projector.project( 0, 90 ) ;
			z = new Vector( xy[0], xy[1] ) ; // zenit

			z.sub( p ) ;

			a = Math.atan2( z.y, z.x )-90 ;

			ps.operator.rotate( a ) ;

			if ( s == 0 && getAnnotation() != null ) {
				PostscriptEmitter annotation ;

				for ( int i=0 ; i<getAnnotationCount() ; i++ ) {
					ps.operator.gsave() ;

					annotation = ApplicationFactory.companionOf( getAnnotation( i ) ) ;
					annotation.headPS( ps ) ;
					annotation.emitPS( ps ) ;
					annotation.tailPS( ps ) ;

					ps.operator.grestore() ;
				}
			}

			ps.operator.grestore() ;
		}
	}

	public void tailPS( ApplicationPostscriptStream ps ) {
	}

	public List<double[]> list() {
		List<double[]> outline ;
		ShapeElliptical ellipse ;
		double lo, la ;

		outline = new java.util.Vector<double[]>() ;

		if ( getBodyArealTypeChoice().getPositionCount()>0 ) {
			for ( double[] position : ApplicationFactory.valueOf( getBodyArealTypeChoice().getPosition() ) ) {
				lo = CAACoordinateTransformation.MapTo0To360Range( position[1] ) ;
				if ( lo>180 )
					lo = lo-360 ;

				la = CAACoordinateTransformation.MapTo0To360Range( position[2] ) ;
				if ( la>180 )
					la = la-360 ;
				if ( la>90 )
					la = 180-la ;
				if ( la<-90 )
					la = -180-la ;

				outline.add( projector.project( lo, la ) ) ;
			}
		} else {
			ellipse = new ShapeElliptical( projector ) ;
			getBodyArealTypeChoice().getShapeElliptical().copyValues( ellipse ) ;

			outline.addAll( ellipse.list() ) ;
		}

		return outline ;
	}
}
