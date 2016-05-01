
package astrolabe;

import java.text.MessageFormat;

import caa.CAACoordinateTransformation;

@SuppressWarnings("serial")
public class BodyStellar extends astrolabe.model.BodyStellar implements PostscriptEmitter {

	private double turn ;
	private double spin ;

	private double x ;
	private double y ;

	public BodyStellar( Peer peer, Projector projector ) {
		double[] lo, eq, xy ;
		MessageCatalog m ;
		String key ;

		peer.setupCompanion( this ) ;

		turn = -CAACoordinateTransformation.HoursToDegrees( getTurn() ) ;
		spin = -CAACoordinateTransformation.HoursToDegrees( getSpin() ) ;

		lo = AstrolabeFactory.valueOf( getPosition() ) ;
		eq = projector.convert( lo[1], lo[2] ) ;
		xy = projector.project( lo[1], lo[2] ) ;

		x = xy[0] ;
		y = xy[1] ;

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
		Script script ;

		ps.push( x ) ;
		ps.push( y ) ;
		ps.operator.moveto() ;

		ps.operator.currentpoint() ;
		ps.operator.copy( 2 ) ;
		ps.operator.copy( 2 ) ;
		ps.operator.newpath() ;
		ps.operator.moveto() ;

		ps.array( true ) ;
		try {
			script = new Script( getScript() ) ;

			AnnotationStraight.emitPS( ps, script, script.size(), 0, 0, 0, 0, 0 ) ;
		} catch ( ParameterNotValidException e ) {
			String msg ;

			msg = MessageCatalog.message( ApplicationConstant.GC_APPLICATION, ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
			msg = MessageFormat.format( msg, new Object[] { e.toString(), "" } ) ;

			throw new RuntimeException( msg ) ;
		}
		ps.array( false ) ;									// p, p, text

		ps.operator.dup() ;
		ps.operator.get( 0 ) ;

		ps.operator.dup( 2 ) ;
		ps.operator.get( 0 ) ; // font
		ps.operator.exch() ;
		ps.operator.get( 1 ) ; // encoding
		ps.custom( ApplicationConstant.PS_CUSTOM_SETENCODING ) ;

		ps.operator.dup( 2 ) ;
		ps.operator.get( 0 ) ; // font
		ps.operator.exch() ;
		ps.operator.get( 2 ) ; // size
		ps.operator.selectfont() ;

		ps.operator.get( 6 ) ; // glyph
		ps.push( false ) ;
		ps.operator.charpath() ;							// p, p, text
		ps.operator.roll( 3, 1 ) ;							// p, text, p
		ps.operator.pathbbox() ;							// p, text, p, ll, ur

		ps.operator.copy( 4 ) ;
		ps.custom( ApplicationConstant.PS_CUSTOM_VSUB ) ;
		ps.push( .5 ) ;
		ps.custom( ApplicationConstant.PS_CUSTOM_VMUL ) ;	// p, text, p, ll, ur, d/2

		// preserve length of d/2*goldensection
		ps.operator.copy( 2 ) ;
		ps.custom( ApplicationConstant.PS_CUSTOM_VABS ) ;
		ps.operator.mul( Math.goldensection ) ;
		ps.operator.roll( 10, 1 ) ;							// p, l, text, p, ll, ur, d/2

		ps.custom( ApplicationConstant.PS_CUSTOM_VADD ) ;	// p, l, text, p, ll, gc

		ps.operator.roll( 4, 2 ) ;
		ps.operator.pop( 2 ) ;								// p, l, text, p, gc
		ps.custom( ApplicationConstant.PS_CUSTOM_VSUB ) ;	// p, l, text, o

		ps.operator.roll( 6, 4 ) ;							// l, text, o, p
		ps.operator.translate() ;

		ps.operator.gsave() ;
		ps.operator.rotate( turn ) ;

		ps.operator.moveto() ;

		ps.operator.show() ;
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
		ps.custom( ApplicationConstant.PS_CUSTOM_PATHREVERSE ) ;

		ps.operator.currentpoint() ;
		ps.operator.translate() ;
		ps.operator.rotate( -spin ) ;

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
