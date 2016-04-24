package astrolabe;

public class Body extends Glyph {

	public Body( astrolabe.model.GlyphType glT, Chart chart, Horizon horizon ) throws ParameterNotValidException {
		super( glT, chart ) ;

		double[] lo, eq ;
		String key ;

		lo = AstrolabeFactory.valueOf( glT.getPosition() ) ;
		eq = horizon.convert( lo[1], lo[2] ) ;
		setRA( eq[0] ) ;
		setDe( eq[1] ) ;

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
}
