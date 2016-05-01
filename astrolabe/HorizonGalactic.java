
package astrolabe;

@SuppressWarnings("serial")
public class HorizonGalactic extends HorizonType {

	private Projector projector ;

	private double la ;
	private double ST ;

	public HorizonGalactic( Object peer, Projector projector ) throws ParameterNotValidException {
		super( peer, projector ) ;

		double[] eq ;
		String key ;

		this.projector = projector ;

		eq = ApplicationHelper.galactic2Equatorial( 0, Math.rad90 ) ;
		la = eq[1] ;
		ST = eq[0] ;

		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_HORIZON_LATITUDE ) ;
		ApplicationHelper.registerDMS( key, la ) ;		
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_HORIZON_TIMESIDEREAL ) ;
		ApplicationHelper.registerHMS( key, ST ) ;
	}

	public double[] project( double l, double b ) {
		return projector.project( convert( l, b ) ) ;
	}

	public double[] unproject( double x, double y ) {
		return unconvert( projector.unproject( x, y ) ) ;
	}

	public double[] convert( double l, double b ) {
		double[] r ;

		r = ApplicationHelper.galactic2Equatorial( l, b ) ;

		return r ;
	}

	public double[] unconvert( double RA, double d ) {
		double[] r ;

		r = ApplicationHelper.equatorial2Galactic( RA, d ) ;

		return r ;
	}
}
