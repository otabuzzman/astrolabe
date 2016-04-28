
package astrolabe;

import org.exolab.castor.xml.ValidationException;

@SuppressWarnings("serial")
public class HorizonEquatorial extends astrolabe.model.HorizonEquatorial implements PostscriptEmitter, Projector {

	private final static String DEFAULT_PRACTICALITY = "0" ;

	private Projector projector ;

	@SuppressWarnings("unused")
	private double la ;
	@SuppressWarnings("unused")
	private double ST ;

	public HorizonEquatorial( Object peer, Projector projector ) throws ParameterNotValidException {
		ApplicationHelper.setupCompanionFromPeer( this, peer ) ;
		try {
			validate() ;
		} catch ( ValidationException e ) {
			throw new ParameterNotValidException( e.toString() ) ;
		}

		this.projector = projector ;

		la = Math.rad90 ;
		ST = 0 ;
	}

	public double[] project( double[] ho ) {
		return project( ho[0], ho[1] ) ;
	}

	public double[] project( double RA, double d ) {
		return projector.project( convert( RA, d ) ) ;
	}

	public double[] unproject( double[] xy ) {
		return unproject( xy[0], xy[1] ) ;
	}

	public double[] unproject( double x, double y ) {
		return unconvert( projector.unproject( x, y ) ) ;
	}

	public double[] convert( double[] eq ) {
		return convert( eq[0], eq[1] ) ;
	}

	public double[] convert( double RA, double d ) {
		double[] r = new double[2] ;

		r[0] = RA ;
		r[1] = d ;

		return r ;
	}

	public double[] unconvert( double[] eq ) {
		return unconvert( eq[0], eq[1] ) ;
	}

	public double[] unconvert( double RA, double d ) {
		double[] r = new double[2] ;

		r[0] = RA ;
		r[1] = d ;

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
