
package astrolabe;

public class BodyStellar extends Glyph {

	private Horizon horizon ;

	private double x ;
	private double y ;

	public BodyStellar( astrolabe.model.BodyStellarType bsT, Horizon horizon ) throws ParameterNotValidException {
		super( bsT.getGlyph() ) ;

		double[] lo, eq ;
		Vector xy ;
		String key ;

		this.horizon = horizon ;

		lo = AstrolabeFactory.valueOf( bsT.getPosition() ) ;
		eq = horizon.convert( lo[1], lo[2] ) ;
		xy = horizon.dotDot()/*Chart*/.project( eq ) ;
		x = xy.getX() ;
		y = xy.getY() ;

		try {
			key = Astrolabe.getLocalizedString( ApplicationConstant.LK_BODY_AZIMUTH ) ;
			ApplicationHelper.registerDMS( key, lo[1], 2 ) ;
			key = Astrolabe.getLocalizedString( ApplicationConstant.LK_BODY_ALTITUDE ) ;
			ApplicationHelper.registerDMS( key, lo[2], 2 ) ;
			key = Astrolabe.getLocalizedString( ApplicationConstant.LK_BODY_RIGHTASCENSION ) ;
			ApplicationHelper.registerDMS( key, eq[0], 2 ) ;
			key = Astrolabe.getLocalizedString( ApplicationConstant.LK_BODY_DECLINATION ) ;
			ApplicationHelper.registerDMS( key, eq[1], 2 ) ;
		} catch ( ParameterNotValidException e ) {}
	}

	public void emitPS( PostscriptStream ps ) throws ParameterNotValidException {
		ps.push( x ) ;
		ps.push( y ) ;
		ps.operator.moveto() ;

		super.emitPS( ps ) ;
	}

	public Horizon dotDot() {
		return horizon ;
	}
}
