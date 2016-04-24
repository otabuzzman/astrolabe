
package astrolabe;

@SuppressWarnings("serial")
public class HorizonGalactic extends astrolabe.model.HorizonGalactic implements Horizon {

	private final static double DEFAULT_PRACTICALITY = 0 ;

	private Projector projector ;

	private double grayscale ;

	private double la ;
	private double ST ;

	public HorizonGalactic( astrolabe.model.HorizonGalactic peer, Projector projector ) {
		double[] eq ;
		String key ;

		ApplicationHelper.setupCompanionFromPeer( this, peer ) ;

		this.projector = projector ;

		grayscale = ApplicationHelper.getClassNode( this,
				getName(), ApplicationConstant.PN_HORIZON_PRACTICALITY ).getDouble( getPracticality(), DEFAULT_PRACTICALITY ) ;

		eq = ApplicationHelper.galactic2Equatorial( 0, Math.rad90 ) ;
		la = eq[1] ;
		ST = eq[0] ;

		try {
			key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_HORIZON_LATITUDE ) ;
			ApplicationHelper.registerDMS( key, la, 2 ) ;		
			key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_HORIZON_TIMESIDEREAL ) ;
			ApplicationHelper.registerHMS( key, ST, 2 ) ;
		} catch ( ParameterNotValidException e ) {}
	}

	public double[] project( double[] ga ) {
		return project( ga[0], ga[1] ) ;
	}

	public double[] project( double l, double b ) {
		return projector.project( convert( l, b ) ) ;
	}

	public double[] unproject( double[] xy ) {
		return project( xy[0], xy[1] ) ;
	}

	public double[] unproject( double x, double y ) {
		return unconvert( projector.unproject( x, y ) ) ;
	}

	public double[] convert( double[] ga ) {
		return convert( ga[0], ga[1] ) ;
	}

	public double[] convert( double l, double b ) {
		double[] r ;

		r = ApplicationHelper.galactic2Equatorial( l, b ) ;

		return r ;
	}

	public double[] unconvert( double[] eq ) {
		return unconvert( eq[0], eq[1] ) ;
	}

	public double[] unconvert( double RA, double d ) {
		double[] r ;

		r = ApplicationHelper.equatorial2Galactic( RA, d ) ;

		return r ;
	}

	public void headPS( PostscriptStream ps ) {
		ps.operator.setgray( grayscale ) ; 
	}

	public void tailPS( PostscriptStream ps ) {
	}
}
