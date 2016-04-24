
package astrolabe;

import caa.CAACoordinateTransformation;

public class Glyph {

	private String glyph ;
	private double size ;

	private double turn ;
	private double spin ;

	public Glyph( astrolabe.model.Glyph gl ) {
		glyph = gl.getValue() ;
		size = ApplicationHelper.getClassNode( this, gl.getName(), null ).getDouble( ApplicationConstant.PN_GLYPH_SIZE, 2.2 ) ;

		turn = -CAACoordinateTransformation.HoursToDegrees( gl.getTurn() ) ;
		spin = -CAACoordinateTransformation.HoursToDegrees( gl.getSpin() ) ;
	}

	public void emitPS( PostscriptStream ps ) throws ParameterNotValidException {
		astrolabe.model.TextType text ;

		text = new astrolabe.model.TextType() ;
		text.setValue( glyph ) ;

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
		ps.operator.dup() ;
		ps.operator.dup() ;
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
		ps.custom( ApplicationConstant.PS_PROLOG_VSCALE ) ;	// p, text, p, ll, ur, d/2

		// preserve length of d/2*goldensection
		ps.operator.copy( 2 ) ;
		ps.custom( ApplicationConstant.PS_PROLOG_VABS ) ;
		ps.operator.dup() ;
		ps.operator.div( Math.goldensection ) ;
		ps.operator.add() ;
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
		ps.push( 360 ) ;
		ps.operator.arc() ;
		ps.custom( ApplicationConstant.PS_PROLOG_PATHREVERSE ) ;

		ps.operator.currentpoint() ;
		ps.operator.translate() ;
		ps.operator.rotate( -spin ) ;
	}
}
