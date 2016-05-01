
package astrolabe;

import org.exolab.castor.xml.ValidationException;

@SuppressWarnings("serial")
public class HorizonGalactic extends astrolabe.model.HorizonGalactic implements PostscriptEmitter, Projector {

	private final static String DEFAULT_PRACTICALITY = "0" ;

	private Projector projector ;

	private double la ;
	private double ST ;

	public HorizonGalactic( Object peer, Projector projector ) throws ParameterNotValidException {
		double[] eq ;
		String key ;

		ApplicationHelper.setupCompanionFromPeer( this, peer ) ;
		try {
			validate() ;
		} catch ( ValidationException e ) {
			throw new ParameterNotValidException( e.toString() ) ;
		}

		this.projector = projector ;

		eq = ApplicationHelper.galactic2Equatorial( 0, Math.rad90 ) ;
		la = eq[1] ;
		ST = eq[0] ;

		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_HORIZON_LATITUDE ) ;
		ApplicationHelper.registerDMS( key, la ) ;		
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_HORIZON_TIMESIDEREAL ) ;
		ApplicationHelper.registerHMS( key, ST ) ;
	}

	public double[] project( double[] ga ) {
		return project( ga[0], ga[1] ) ;
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
