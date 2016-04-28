
package astrolabe;

import java.util.prefs.Preferences;

import org.exolab.castor.xml.ValidationException;

import caa.CAACoordinateTransformation;

@SuppressWarnings("serial")
public class BodyStellar extends astrolabe.model.BodyStellar implements PostscriptEmitter {

	@SuppressWarnings("unused")
	private Projector projector ;

	private final static double DEFAULT_SIZE = 2.2 ;
	private final static String DEFAULT_STAR = "\uf811" ;

	private double size ;

	private double turn ;
	private double spin ;

	private double x ;
	private double y ;

	public BodyStellar( Object peer, Projector projector ) throws ParameterNotValidException {
		Preferences node ;
		double[] lo, eq, xy ;
		String key ;

		ApplicationHelper.setupCompanionFromPeer( this, peer ) ;
		try {
			validate() ;
		} catch ( ValidationException e ) {
			throw new ParameterNotValidException( e.toString() ) ;
		}

		node = ApplicationHelper.getClassNode( this, getName(), null ) ;

		this.projector = projector ;

		size = ApplicationHelper.getPreferencesKV( node, getType(), 0. ) ;
		if ( ! ( size>0 ) ) {
			size = DEFAULT_SIZE ;
			setGlyph( DEFAULT_STAR ) ;
		}

		turn = -CAACoordinateTransformation.HoursToDegrees( getTurn() ) ;
		spin = -CAACoordinateTransformation.HoursToDegrees( getSpin() ) ;

		try {
			lo = AstrolabeFactory.valueOf( getPosition() ) ;
			eq = projector.convert( lo[1], lo[2] ) ;
			xy = projector.project( lo[1], lo[2] ) ;

			x = xy[0] ;
			y = xy[1] ;

			key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_BODY_AZIMUTH ) ;
			ApplicationHelper.registerDMS( key, lo[1], 2 ) ;
			key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_BODY_ALTITUDE ) ;
			ApplicationHelper.registerDMS( key, lo[2], 2 ) ;
			key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_BODY_RIGHTASCENSION ) ;
			ApplicationHelper.registerDMS( key, eq[0], 2 ) ;
			key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_BODY_DECLINATION ) ;
			ApplicationHelper.registerDMS( key, eq[1], 2 ) ;
		} catch ( ParameterNotValidException e ) {}
	}

	public void headPS( PostscriptStream ps ) {
	}

	public void emitPS( PostscriptStream ps ) {
		astrolabe.model.TextType text ;

		try {
			ps.push( x ) ;
			ps.push( y ) ;
			ps.operator.moveto() ;

			text = new astrolabe.model.TextType() ;
			text.setValue( getGlyph() ) ;

			ps.operator.currentpoint() ;
			ps.operator.copy( 2 ) ;
			ps.operator.copy( 2 ) ;
			ps.operator.newpath() ;
			ps.operator.moveto() ;

			ps.array( true ) ;
			AnnotationStraight.emitPS( ps, text, size, 0, 0, 0, 0, 0 ) ;
			ps.array( false ) ;									// p, p, text

			ps.operator.dup() ;
			ps.operator.get( 0 ) ;

			ps.operator.dup( 2 ) ;
			ps.operator.get( 0 ) ; // font
			ps.operator.exch() ;
			ps.operator.get( 1 ) ; // encoding
			ps.custom( ApplicationConstant.PS_PROLOG_SETENCODING ) ;

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
			ps.custom( ApplicationConstant.PS_PROLOG_VSUB ) ;
			ps.push( .5 ) ;
			ps.custom( ApplicationConstant.PS_PROLOG_VMUL ) ;	// p, text, p, ll, ur, d/2

			// preserve length of d/2*goldensection
			ps.operator.copy( 2 ) ;
			ps.custom( ApplicationConstant.PS_PROLOG_VABS ) ;
			ps.operator.mul( Math.goldensection ) ;
			ps.operator.roll( 10, 1 ) ;							// p, l, text, p, ll, ur, d/2

			ps.custom( ApplicationConstant.PS_PROLOG_VADD ) ;	// p, l, text, p, ll, gc

			ps.operator.roll( 4, 2 ) ;
			ps.operator.pop( 2 ) ;								// p, l, text, p, gc
			ps.custom( ApplicationConstant.PS_PROLOG_VSUB ) ;	// p, l, text, o

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
			ps.custom( ApplicationConstant.PS_PROLOG_PATHREVERSE ) ;

			ps.operator.currentpoint() ;
			ps.operator.translate() ;
			ps.operator.rotate( -spin ) ;
		} catch ( ParameterNotValidException e ) {
			throw new RuntimeException( e.toString() ) ;
		}

		if ( getAnnotation() != null ) {
			try {
				ApplicationHelper.emitPS( ps, getAnnotation() ) ;
			} catch ( ParameterNotValidException e ) {
				throw new RuntimeException( e.toString() ) ;
			}
		}
	}

	public void tailPS( PostscriptStream ps ) {
	}	
}
