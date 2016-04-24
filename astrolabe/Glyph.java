
package astrolabe;

import caa.CAACoordinateTransformation;

public class Glyph {

	private double RA ;
	private double de ;

	private String glyph ;
	private double size ;

	private double turn ;

	private Chart chart ;

	public Glyph( astrolabe.model.GlyphType glT, Chart chart ) throws ParameterNotValidException {
		double[] eq ;
		String key ;

		this.chart = chart ;

		eq = AstrolabeFactory.valueOf( glT.getPosition() ) ;
		setRA( eq[1] ) ;
		setDe( eq[2] ) ;

		glyph = glT.getValue() ;
		size = ApplicationHelper.getClassNode( this, glT.getName(), null ).getDouble( ApplicationConstant.PN_GLYPTH_SIZE, 2.2 ) ;

		turn = -CAACoordinateTransformation.HoursToDegrees( glT.getTurn() ) ;

		try {
			key = Astrolabe.getLocalizedString( ApplicationConstant.LK_BODY_RIGHTASCENSION ) ;
			ApplicationHelper.registerDMS( key, eq[1], 2 ) ;
			key = Astrolabe.getLocalizedString( ApplicationConstant.LK_BODY_DECLINATION ) ;
			ApplicationHelper.registerDMS( key, eq[2], 2 ) ;
		} catch ( ParameterNotValidException e ) {}
	}

	public void emitPS( PostscriptStream ps ) throws ParameterNotValidException {
		astrolabe.model.TextType text ;
		Vector xy ;

		xy = chart.project( RA, de ) ;

		text = new astrolabe.model.TextType() ;
		text.setValue( glyph ) ;

		ps.operator.newpath() ;
		ps.push( xy.getX() ) ;
		ps.push( xy.getY() ) ;
		ps.operator.moveto() ;

		ps.array( true ) ;
		AnnotationStraight.emitPS( ps, text, size, 0, 0, 0, 0, 0 ) ;
		ps.array( false ) ;									// text

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
		ps.operator.charpath() ;
		ps.push( xy.getX() ) ;
		ps.push( xy.getY() ) ;
		ps.operator.pathbbox() ;							// text, p, ll, ur

		ps.operator.copy( 4 ) ;
		ps.custom( ApplicationConstant.PS_PROLOG_VSUB ) ;
		ps.push( .5 ) ;
		ps.custom( ApplicationConstant.PS_PROLOG_VSCALE ) ;	// text, p, ll, ur, d/2

		// preserve length of d/2*goldensection
		ps.operator.copy( 2 ) ;
		ps.custom( ApplicationConstant.PS_PROLOG_VABS ) ;
		ps.operator.dup() ;
		ps.operator.div( Math.goldensection ) ;
		ps.operator.add() ;
		ps.operator.roll( 10, 1 ) ;							// l, text, p, ll, ur, d/2

		ps.custom( ApplicationConstant.PS_PROLOG_VADD ) ;	// l, text, p, ll, gc

		ps.operator.roll( 4, 2 ) ;
		ps.operator.pop( 2 ) ;								// l, text, p, gc
		ps.custom( ApplicationConstant.PS_PROLOG_VSUB ) ;	// l, text, o

		ps.push( xy.getX() ) ;
		ps.push( xy.getY() ) ;
		ps.operator.translate() ;

		ps.operator.gsave() ;
		ps.operator.rotate( turn ) ;

		ps.operator.moveto() ;

		ps.operator.show() ;
		ps.operator.grestore() ;	// l

		ps.operator.newpath() ;
		ps.push( 0 ) ;
		ps.operator.exch() ;
		ps.push( 0 ) ;
		ps.operator.exch() ;		// 0, 0, l
		ps.push( 0 ) ;
		ps.push( 360 ) ;
		ps.operator.arc() ;
		ps.custom( ApplicationConstant.PS_PROLOG_PATHREVERSE ) ;
	}

	public void setRA( double RA ) {
		this.RA = RA ;
	}

	public void setDe( double de ) {
		this.de = de ;
	}

	public void setEq( double[] eq ) {
		setRA( eq[0] ) ;
		setDe( eq[1] ) ;
	}
}
