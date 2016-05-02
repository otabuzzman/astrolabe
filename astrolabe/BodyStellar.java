
package astrolabe;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import caa.CAACoordinateTransformation;

@SuppressWarnings("serial")
public class BodyStellar extends astrolabe.model.BodyStellar implements PostscriptEmitter {

	// qualifier key (QK_)
	private final static String QK_RA			= "RA" ;
	private final static String QK_DECLINATION	= "declination" ;

	private Projector projector ;

	public BodyStellar( Projector projector ) {
		this.projector = projector ;
	}

	public void register() {
		Coordinate lo, eq ;
		DMS dms ;

		lo = valueOf( getPosition() ) ;
		eq = projector.convert( lo, false ) ;

		dms = new DMS( eq.x ) ;
		dms.register( this, QK_RA ) ;
		dms.set( eq.y, -1 ) ;
		dms.register( this, QK_DECLINATION ) ;
	}

	public void degister() {
		DMS.degister( this, QK_RA ) ;
		DMS.degister( this, QK_DECLINATION ) ;
	}

	public void headPS( ApplicationPostscriptStream ps ) {
	}

	public void emitPS( ApplicationPostscriptStream ps ) {
		astrolabe.model.Script script ;
		Geometry fov ;
		ChartPage page ;
		double height ;
		Coordinate lo, xy ;
		double turn, spin ;
		astrolabe.model.Annotation annotation ;
		PostscriptEmitter emitter ;

		fov = (Geometry) Registry.retrieve( FOV.RK_FOV ) ;
		if ( fov == null ) {
			page = (ChartPage) Registry.retrieve( ChartPage.RK_CHARTPAGE ) ;
			if ( page != null )
				fov = page.getViewGeometry() ;
		}

		lo = valueOf( getPosition() ) ;
		xy = projector.project( lo, false ) ;

		if ( fov != null && ! fov.covers( new GeometryFactory().createPoint( xy ) ) )
			return ;

		turn = -CAACoordinateTransformation.HoursToDegrees( getTurn() ) ;
		spin = -CAACoordinateTransformation.HoursToDegrees( getSpin() ) ;

		ps.operator.mark() ;

		ps.push( xy.x ) ;
		ps.push( xy.y ) ;
		ps.operator.moveto() ;

		ps.operator.currentpoint() ;
		ps.operator.copy( 2 ) ;
		ps.operator.copy( 2 ) ;
		ps.operator.newpath() ;
		ps.operator.moveto() ;

		script = new astrolabe.model.Script() ;
		getScript().copyValues( script ) ;

		height = Configuration.getValue( script, script.getPurpose(), -1. ) ;
		if ( height<0 )
			height = Double.valueOf( script.getPurpose() ) ;
		if ( height==0 ) {
			ps.operator.cleartomark() ;

			return ;
		}

		ps.array( true ) ;
		AnnotationStraight.emitPS( ps, script, height, 0, 0, 0, 0, 0 ) ;
		ps.array( false ) ;								// p, p, text

		ps.operator.dup() ;
		ps.operator.get( 0 ) ;

		ps.operator.dup( 2 ) ;
		ps.operator.get( 0 ) ; // font
		ps.operator.exch() ;
		ps.operator.get( 1 ) ; // encoding
		ps.setencoding() ;

		ps.operator.dup( 2 ) ;
		ps.operator.get( 0 ) ; // font
		ps.operator.exch() ;
		ps.operator.get( 2 ) ; // size
		ps.operator.selectfont() ;

		ps.operator.get( 6 ) ; // glyph
		ps.push( false ) ;
		ps.operator.charpath() ;						// p, p, text
		ps.operator.roll( 3, 1 ) ;						// p, text, p
		ps.operator.pathbbox() ;						// p, text, p, ll, ur

		ps.operator.copy( 4 ) ;
		ps.vsub() ;
		ps.push( .5 ) ;
		ps.vmul() ;										// p, text, p, ll, ur, d/2

		// preserve length of d/2*goldensection
		ps.operator.copy( 2 ) ;
		ps.vabs() ;
		ps.operator.mul( Math.goldensection ) ;
		ps.operator.roll( 10, 1 ) ;						// p, l, text, p, ll, ur, d/2

		ps.vadd() ;										// p, l, text, p, ll, gc

		ps.operator.roll( 4, 2 ) ;
		ps.operator.pop( 2 ) ;							// p, l, text, p, gc
		ps.vsub() ;										// p, l, text, o

		ps.operator.roll( 6, 4 ) ;						// l, text, o, p
		ps.operator.translate() ;

		ps.operator.gsave() ;
		ps.operator.rotate( turn ) ;

		ps.operator.moveto() ;

		ps.tshow() ;
		ps.operator.grestore() ;	// l

		ps.operator.rotate( spin ) ;

		ps.operator.newpath() ;
		ps.push( 0 ) ;
		ps.operator.exch() ;
		ps.push( 0 ) ;
		ps.operator.exch() ;		// 0, 0, l
		ps.push( 0 ) ;
		ps.push( 359 ) ;
		ps.operator.arc() ;
		ps.gpath() ;
		ps.grev() ;
		ps.operator.newpath() ;
		ps.gdraw() ;

		ps.operator.currentpoint() ;
		ps.operator.translate() ;
		ps.operator.rotate( -spin ) ;

		ps.operator.cleartomark() ;

		if ( getAnnotation() != null ) {
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
	}

	public void tailPS( ApplicationPostscriptStream ps ) {
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
