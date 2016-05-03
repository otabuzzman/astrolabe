
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

	private Converter converter ;
	private Projector projector ;

	public BodyStellar( Converter converter, Projector projector ) {
		this.converter = converter ;
		this.projector = projector ;
	}

	public void register() {
		Coordinate lo, eq ;
		DMS dms ;

		lo = valueOf( getPosition() ) ;
		eq = converter.convert( lo, false ) ;

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

		fov = (Geometry) Registry.retrieve( Geometry.class.getName() ) ;
		if ( fov == null ) {
			page = (ChartPage) Registry.retrieve( ChartPage.class.getName() ) ;
			if ( page != null )
				fov = page.getViewGeometry() ;
		}

		lo = valueOf( getPosition() ) ;
		xy = projector.project( converter.convert( lo, false ), false ) ;

		if ( fov != null && ! fov.covers( new GeometryFactory().createPoint( xy ) ) )
			return ;

		turn = -CAACoordinateTransformation.HoursToDegrees( getTurn() ) ;
		spin = -CAACoordinateTransformation.HoursToDegrees( getSpin() ) ;

		ps.op( "mark" ) ;

		ps.push( xy.x ) ;
		ps.push( xy.y ) ;
		ps.op( "moveto") ;

		ps.op( "currentpoint" ) ;
		ps.push( 2 ) ;
		ps.op( "copy" ) ;
		ps.push( 2 ) ;
		ps.op( "copy" ) ;
		ps.op( "newpath" ) ;
		ps.op( "moveto" ) ;

		script = new astrolabe.model.Script() ;
		getScript().copyValues( script ) ;

		height = Configuration.getValue( script, script.getPurpose(), -1. ) ;
		if ( height<0 )
			height = Double.valueOf( script.getPurpose() ) ;
		if ( height==0 ) {
			ps.op( "cleartomark" ) ;

			return ;
		}

		ps.array( true ) ;
		AnnotationStraight.emitPS( ps, script, height, 0, 0, 0, 0, 0 ) ;
		ps.array( false ) ;								// p, p, text

		ps.op( "dup" ) ;
		ps.push( 0 ) ;
		ps.op( "get" ) ;

		ps.op( "dup" ) ;
		ps.op( "dup" ) ;
		ps.push( 0 ) ;
		ps.op( "get" ) ;		// font
		ps.op( "exch" ) ;
		ps.push( 1 ) ;
		ps.op( "get" ) ;		// encoding
		ps.op( "setencoding" ) ;

		ps.op( "dup" ) ;
		ps.op( "dup" ) ;
		ps.push( 0 ) ;
		ps.op( "get" ) ;		// font
		ps.op( "exch" ) ;
		ps.push( 2 ) ;
		ps.op( "get" ) ;		// size
		ps.op( "selectfont" ) ;

		ps.push( 6 ) ;
		ps.op( "get" ) ; 		// glyph
		ps.push( false ) ;
		ps.op( "charpath" ) ;						// p, p, text
		ps.push( 3 ) ;
		ps.push( 1 ) ;
		ps.op( "roll" ) ;							// p, l, text, p, ll, ur, d/2
		ps.op( "pathbbox" ) ;						// p, text, p, ll, ur

		ps.push( 4 ) ;
		ps.op( "copy" ) ;
		ps.op( "vsub" ) ;
		ps.push( .5 ) ;
		ps.op( "vmul" ) ;									// p, text, p, ll, ur, d/2

		// preserve length of d/2*goldensection
		ps.push( 2 ) ;
		ps.op( "copy" ) ;
		ps.op( "vabs" ) ;
		ps.push( Math.goldensection ) ;
		ps.op( "mul" ) ;
		ps.push( 10 ) ;
		ps.push( 1 ) ;
		ps.op( "roll" ) ;							// p, l, text, p, ll, ur, d/2

		ps.op( "vadd" ) ;									// p, l, text, p, ll, gc

		ps.push( 4 ) ;
		ps.push( 2 ) ;
		ps.op( "roll" ) ;
		ps.op( "pop" ) ;
		ps.op( "pop" ) ;							// p, l, text, p, gc
		ps.op( "vsub" ) ;									// p, l, text, o

		ps.push( 6 ) ;
		ps.push( 4 ) ;
		ps.op( "roll" ) ;							// l, text, o, p
		ps.op( "translate" ) ;

		ps.op( "gsave" ) ;
		ps.push( turn ) ;
		ps.op( "rotate" ) ;

		ps.op( "moveto" ) ;

		ps.op( "tshow" ) ;
		ps.op( "grestore" ) ;	// l

		ps.push( spin ) ;
		ps.op( "rotate" ) ;

		ps.op( "newpath" ) ;
		ps.push( 0 ) ;
		ps.op( "exch" ) ;
		ps.push( 0 ) ;
		ps.op( "exch" ) ;		// 0, 0, l
		ps.push( 0 ) ;
		ps.push( 359 ) ;
		ps.op( "arc" ) ;
		ps.op( "gpath" ) ;
		ps.op( "grev" ) ;
		ps.op( "newpath" ) ;
		ps.op( "gdraw" ) ;

		ps.op( "currentpoint" ) ;
		ps.op( "translate" ) ;
		ps.push( -spin ) ;
		ps.op( "rotate" ) ;

		ps.op( "cleartomark" ) ;

		if ( getAnnotation() != null ) {
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
