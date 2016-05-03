
package astrolabe;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import astrolabe.model.SignType;

@SuppressWarnings("serial")
public class Sign extends astrolabe.model.Sign implements PostscriptEmitter {

	// configuration key (CK_), node (CN_)
	private final static String CK_SHORTENING		= "shortening" ;

	private final static double DEFAULT_SHORTENING	= 2.62 ;

	private Projector projector ;

	public Sign( Projector projector ) {
		this.projector = projector ;
	}

	public void headPS( ApplicationPostscriptStream ps ) {
		String gstate ;

		if ( ( gstate = Configuration.getValue( this, getNature(), null ) ) == null )
			return ;
		ps.script( gstate ) ;
	}

	public void emitPS( ApplicationPostscriptStream ps ) {
		emitPS( ps, this ) ;
	}

	public void tailPS( ApplicationPostscriptStream ps ) {
	}

	private void emitPS( ApplicationPostscriptStream ps, SignType limb ) {
		double shortening ;
		Vector va, vb, vc, vca, vco ;
		FieldOfView fov ;
		Geometry gov, ab, l ;
		ChartPage page ;
		Coordinate a, b ;
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

		shortening = Configuration.getValue( this, CK_SHORTENING, DEFAULT_SHORTENING ) ;

		a = projector.project( valueOf( limb.getPosition( 0 ) ), false ) ;
		b = projector.project( valueOf( limb.getPosition( 1 ) ), false ) ;

		if ( a.distance( b )>2*shortening ) {
			va = new Vector( a ) ;
			vb = new Vector( b ) ;
			vc = vb.sub( va ) ;
			vca = new Vector( va ).add( new Vector( vc ).scale( shortening ) ) ;
			vco = new Vector( va ).add( new Vector( vc ).scale( vc.abs()-shortening ) ) ;

			ab = l = new GeometryFactory().createLineString( new Coordinate[] { vca, vco } ) ;
		} else
			ab = l = new GeometryFactory().createLineString( new Coordinate[] { a, b } ) ;

		if ( gov != null && gov.intersects( ab ) ) {
			if ( ! gov.contains( ab ) )
				l = gov.intersection( ab ) ;

			ps.array( true ) ;
			for ( Coordinate xy : l.getCoordinates() ) {
				ps.push( xy.x ) ;
				ps.push( xy.y ) ;
			}
			ps.array( false ) ;

			ps.op( "newpath" ) ;
			ps.op( "gdraw" ) ;

			ps.op( "stroke" ) ;

			if ( limb.getAnnotation() != null ) {
				annotation = limb.getAnnotation() ;

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

		if ( limb.getLimb() != null )
			emitPS( ps, limb.getLimb() ) ;
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
