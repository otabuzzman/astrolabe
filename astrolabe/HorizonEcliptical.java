
package astrolabe;

@SuppressWarnings("serial")
public class HorizonEcliptical extends HorizonType {

	private Projector projector ;

	private double la ;

	private double e ; // mean obliquity of ecliptic

	public HorizonEcliptical( Object peer, Projector projector ) throws ParameterNotValidException {
		super( peer, projector ) ;

		double epoch, eq[] ;
		String key ;

		this.projector = projector ;

		epoch = ( (Double) Registry.retrieve( ApplicationConstant.GC_EPOCHE ) ).doubleValue() ;
		e = ApplicationHelper.meanObliquityOfEcliptic( epoch ) ;
		eq = ApplicationHelper.ecliptic2Equatorial( 0, Math.rad90, e ) ;
		la = eq[1] ;

		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_HORIZON_ECLIPTICEPSILON ) ;
		ApplicationHelper.registerDMS( key, e ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_HORIZON_LATITUDE ) ;
		ApplicationHelper.registerDMS( key, la ) ;		
	}

	public double[] project( double l, double b ) {
		return projector.project( convert( l, b ) ) ;
	}

	public double[] unproject( double x, double y ) {
		return unconvert( projector.unproject( x, y ) ) ;
	}

	public double[] convert( double l, double b ) {
		double[] r ;

		r = ApplicationHelper.ecliptic2Equatorial( l, b, e ) ;

		return r ;
	}

	public double[] unconvert( double RA, double d ) {
		double[] r ;

		r = ApplicationHelper.equatorial2Ecliptic( RA, d, e ) ;

		return r ;
	}
}
