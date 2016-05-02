
package astrolabe;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import caa.CAACoordinateTransformation;

import com.vividsolutions.jts.geom.Geometry;

@SuppressWarnings("serial")
public class BodyAreal extends astrolabe.model.BodyAreal implements PostscriptEmitter {

	private Projector projector ;

	private List<double[]> outline = new java.util.Vector<double[]>() ;

	public BodyAreal( Peer peer, Projector projector ) {
		PolygonSphere polygon ;
		List<double[]> position ;
		double lo, la ;
		double sr, sd ;
		OutlineElliptical ellipse ;
		MessageCatalog m ;
		String key ;

		peer.setupCompanion( this ) ;

		this.projector = projector ;

		if ( getBodyArealTypeChoice().getPositionCount()>0 ) {
			position = new java.util.Vector<double[]>() ;
			for ( double[] p : AstrolabeFactory.valueOf( getBodyArealTypeChoice().getPosition() ) ) {
				lo = CAACoordinateTransformation.MapTo0To360Range( p[1] ) ;
				if ( lo>180 )
					lo = lo-360 ;

				la = CAACoordinateTransformation.MapTo0To360Range( p[2] ) ;
				if ( la>180 )
					la = la-360 ;
				if ( la>90 )
					la = 180-la ;
				if ( la<-90 )
					la = -180-la ;

				position.add( new double[] { 1, lo, la } ) ;
				outline.add( projector.project( lo, la ) ) ;
			}

			polygon = new PolygonSphere( position ) ;
			sr = polygon.area() ;
			if ( sr>( 2*java.lang.Math.PI ) )
				sr = 4*java.lang.Math.PI-sr ;
			sd = sr/java.lang.Math.pow( ( 2*java.lang.Math.PI/360. ), 2 ) ;
		} else {
			ellipse = new OutlineElliptical( getBodyArealTypeChoice().getOutlineElliptical(), projector ) ;

			outline.addAll( ellipse.list() ) ;

			sr = 0 ;
			sd = 0 ;
		}

		m = new MessageCatalog( ApplicationConstant.GC_APPLICATION ) ;

		key = m.message( ApplicationConstant.LK_BODY_STERADIAN ) ;
		AstrolabeRegistry.registerDMS( key, sr ) ;
		key = m.message( ApplicationConstant.LK_BODY_SQUAREDEGREE ) ;
		AstrolabeRegistry.registerDMS( key, sd ) ;		
	}

	public void headPS( AstrolabePostscriptStream ps ) {
		GSPaintStroke nature ;

		nature = new GSPaintStroke( getNature(), getName() ) ;
		nature.headPS( ps ) ;
		nature.emitPS( ps ) ;
		nature.tailPS( ps ) ;
	}

	public void emitPS( AstrolabePostscriptStream ps ) {
		ListCutter cutter ;
		List<List<double[]>> segment ;
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
		double[] xy ;
		Vector z, p ;
		double a ;

		fov = (Geometry) Registry.retrieve( ApplicationConstant.GC_FOVEFF ) ;
		if ( fov == null ) {
			fov = (Geometry) AstrolabeRegistry.retrieve( ApplicationConstant.GC_FOVUNI ) ;
		}

		cutter = new ListCutter( outline, fov ) ;

		segment = cutter.segmentsIntersecting( true ) ;
		if ( segment.size()>1 )
			Collections.sort( segment, comparator ) ;

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
			ps.push( ApplicationConstant.PS_PROLOG_GDRAW ) ;

			// halo stroke
			ps.operator.currentlinewidth() ;

			ps.operator.dup() ;
			ps.operator.div( 100 ) ;
			ps.push( (Double) ( AstrolabeRegistry.retrieve( ApplicationConstant.PK_CHART_HALO ) ) ) ; 
			ps.operator.mul() ;
			ps.push( (Double) ( AstrolabeRegistry.retrieve( ApplicationConstant.PK_CHART_HALOMIN ) ) ) ; 
			ps.push( ApplicationConstant.PS_PROLOG_MAX ) ;
			ps.push( (Double) ( AstrolabeRegistry.retrieve( ApplicationConstant.PK_CHART_HALOMAX ) ) ) ; 
			ps.push( ApplicationConstant.PS_PROLOG_MIN ) ;

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

					annotation = AstrolabeFactory.companionOf( getAnnotation( i ) ) ;
					annotation.headPS( ps ) ;
					annotation.emitPS( ps ) ;
					annotation.tailPS( ps ) ;

					ps.operator.grestore() ;
				}
			}

			ps.operator.grestore() ;
		}
	}

	public void tailPS( AstrolabePostscriptStream ps ) {
	}

	public List<double[]> list() {
		return new java.util.Vector<double[]>( outline ) ;
	}
}
