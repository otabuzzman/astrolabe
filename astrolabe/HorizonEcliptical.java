
package astrolabe;

import org.exolab.castor.xml.ValidationException;

@SuppressWarnings("serial")
public class HorizonEcliptical extends astrolabe.model.HorizonEcliptical implements PostscriptEmitter, Projector {

	private final static String DEFAULT_PRACTICALITY = "0" ;

	private Projector projector ;

	private double la ;
	@SuppressWarnings("unused")
	private double ST ;

	private double e ; // mean obliquity of ecliptic

	public HorizonEcliptical( Object peer, double epoch, Projector projector ) throws ParameterNotValidException {
		double[] eq ;
		String key ;

		ApplicationHelper.setupCompanionFromPeer( this, peer ) ;
		try {
			validate() ;
		} catch ( ValidationException e ) {
			throw new ParameterNotValidException( e.toString() ) ;
		}

		this.projector = projector ;

		e = ApplicationHelper.meanObliquityOfEcliptic( epoch ) ;
		eq = ApplicationHelper.ecliptic2Equatorial( 0, Math.rad90, e ) ;
		la = eq[1] ;
		ST = eq[0] ;

		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_HORIZON_ECLIPTICEPSILON ) ;
		ApplicationHelper.registerDMS( key, e, 2 ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_HORIZON_LATITUDE ) ;
		ApplicationHelper.registerDMS( key, la, 2 ) ;		
	}

	public double[] project( double[] ec ) {
		return project( ec[0], ec[1] ) ;
	}

	public double[] project( double l, double b ) {
		return projector.project( convert( l, b ) ) ;
	}

	public double[] unproject( double[] xy ) {
		return unproject( xy[0], xy[1] ) ;
	}

	public double[] unproject( double x, double y ) {
		return unconvert( projector.unproject( x, y ) ) ;
	}

	public double[] convert( double[] ec ) {
		return convert( ec[0], ec[1] ) ;
	}

	public double[] convert( double l, double b ) {
		double[] r ;

		r = ApplicationHelper.ecliptic2Equatorial( l, b, e ) ;

		return r ;
	}

	public double[] unconvert( double[] eq ) {
		return unconvert( eq[0], eq[1] ) ;
	}

	public double[] unconvert( double RA, double d ) {
		double[] r ;

		r = ApplicationHelper.equatorial2Ecliptic( RA, d, e ) ;

		return r ;
	}

	public void headPS( PostscriptStream ps ) {
		String practicality ;

		practicality = ApplicationHelper.getPreferencesKV(
				ApplicationHelper.getClassNode( this, getName(), ApplicationConstant.PN_HORIZON_PRACTICALITY ),
				getPracticality(), DEFAULT_PRACTICALITY ) ;

		ApplicationHelper.emitPSPracticality( ps, practicality ) ;
	}

	public void emitPS( PostscriptStream ps ) {
	}

	public void tailPS( PostscriptStream ps ) {
	}
}
