
package astrolabe;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import caa.CAACoordinateTransformation;

@SuppressWarnings("serial")
public class BodyStellar extends astrolabe.model.BodyStellar implements PostscriptEmitter {

	private Projector projector ;

	public BodyStellar( Projector projector ) {
		this.projector = projector ;
	}

	public void register() {
		double[] lo, eq ;
		MessageCatalog m ;
		String key ;

		lo = AstrolabeFactory.valueOf( getPosition() ) ;
		eq = projector.convert( lo[1], lo[2] ) ;

		m = new MessageCatalog( ApplicationConstant.GC_APPLICATION ) ;

		key = m.message( ApplicationConstant.LK_BODY_AZIMUTH ) ;
		AstrolabeRegistry.registerDMS( key, lo[1] ) ;
		key = m.message( ApplicationConstant.LK_BODY_ALTITUDE ) ;
		AstrolabeRegistry.registerDMS( key, lo[2] ) ;
		key = m.message( ApplicationConstant.LK_BODY_RIGHTASCENSION ) ;
		AstrolabeRegistry.registerDMS( key, eq[0] ) ;
		key = m.message( ApplicationConstant.LK_BODY_DECLINATION ) ;
		AstrolabeRegistry.registerDMS( key, eq[1] ) ;
	}

	public void headPS( AstrolabePostscriptStream ps ) {
	}

	public void emitPS( AstrolabePostscriptStream ps ) {
		astrolabe.model.Script script ;
		Geometry fov ;
		double height ;
		double[] lo, xy ;
		double turn, spin ;

		fov = (Geometry) Registry.retrieve( ApplicationConstant.GC_FOVEFF ) ;
		if ( fov == null ) {
			fov = (Geometry) Registry.retrieve( ApplicationConstant.GC_FOVUNI ) ;
		}

		lo = AstrolabeFactory.valueOf( getPosition() ) ;
		xy = projector.project( lo[1], lo[2] ) ;

		if ( ! fov.covers( new GeometryFactory().createPoint(
				new JTSCoordinate( new double[] { xy[0], xy[1] } ) ) ) )
			return ;

		turn = -CAACoordinateTransformation.HoursToDegrees( getTurn() ) ;
		spin = -CAACoordinateTransformation.HoursToDegrees( getSpin() ) ;

		ps.operator.mark() ;

		ps.push( xy[0] ) ;
		ps.push( xy[1] ) ;
		ps.operator.moveto() ;

		ps.operator.currentpoint() ;
		ps.operator.copy( 2 ) ;
		ps.operator.copy( 2 ) ;
		ps.operator.newpath() ;
		ps.operator.moveto() ;

		script = new astrolabe.model.Script() ;
		getScript().setupCompanion( script ) ;

		height = Configuration.getValue(
				Configuration.getClassNode( script, script.getName(), null ),
				script.getPurpose(), -1. ) ;
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
		ps.push( ApplicationConstant.PS_PROLOG_SETENCODING ) ;

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
		ps.push( ApplicationConstant.PS_PROLOG_VSUB ) ;
		ps.push( .5 ) ;
		ps.push( ApplicationConstant.PS_PROLOG_VMUL ) ;	// p, text, p, ll, ur, d/2

		// preserve length of d/2*goldensection
		ps.operator.copy( 2 ) ;
		ps.push( ApplicationConstant.PS_PROLOG_VABS ) ;
		ps.operator.mul( Math.goldensection ) ;
		ps.operator.roll( 10, 1 ) ;						// p, l, text, p, ll, ur, d/2

		ps.push( ApplicationConstant.PS_PROLOG_VADD ) ;	// p, l, text, p, ll, gc

		ps.operator.roll( 4, 2 ) ;
		ps.operator.pop( 2 ) ;							// p, l, text, p, gc
		ps.push( ApplicationConstant.PS_PROLOG_VSUB ) ;	// p, l, text, o

		ps.operator.roll( 6, 4 ) ;						// l, text, o, p
		ps.operator.translate() ;

		ps.operator.gsave() ;
		ps.operator.rotate( turn ) ;

		ps.operator.moveto() ;

		ps.push( ApplicationConstant.PS_PROLOG_TSHOW ) ;
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
		ps.push( ApplicationConstant.PS_PROLOG_GPATH ) ;
		ps.push( ApplicationConstant.PS_PROLOG_GREV ) ;
		ps.operator.newpath() ;
		ps.push( ApplicationConstant.PS_PROLOG_GDRAW ) ;

		ps.operator.currentpoint() ;
		ps.operator.translate() ;
		ps.operator.rotate( -spin ) ;

		ps.operator.cleartomark() ;

		if ( getAnnotation() != null ) {
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
	}

	public void tailPS( AstrolabePostscriptStream ps ) {
	}	
}
