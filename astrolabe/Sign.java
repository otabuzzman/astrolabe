
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
		Geometry fov, ab, l ;
		ChartPage page ;
		Coordinate a, b ;
		astrolabe.model.Annotation annotation ;
		PostscriptEmitter emitter ;

		fov = (Geometry) Registry.retrieve( Geometry.class.getName() ) ;
		if ( fov == null ) {
			page = (ChartPage) Registry.retrieve( ChartPage.class.getName() ) ;
			if ( page != null )
				fov = page.getViewGeometry() ;
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

			ab = l = new GeometryFactory().createLineString( new Coordinate[] { vca.toCoordinate(), vco.toCoordinate() } ) ;
		} else
			ab = l = new GeometryFactory().createLineString( new Coordinate[] { a, b } ) ;

		if ( fov != null && fov.intersects( ab ) ) {
			if ( ! fov.contains( ab ) )
				l = fov.intersection( ab ) ;

			ps.array( true ) ;
			for ( Coordinate xy : l.getCoordinates() ) {
				ps.push( xy.x ) ;
				ps.push( xy.y ) ;
			}
			ps.array( false ) ;

			ps.operator.newpath() ;
			ps.gdraw() ;

			ps.operator.stroke() ;

			if ( limb.getAnnotation() != null ) {
				annotation = limb.getAnnotation() ;

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
