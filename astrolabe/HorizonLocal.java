
package astrolabe;

import org.exolab.castor.xml.ValidationException;

import caa.CAACoordinateTransformation ;
import caa.CAADate;

@SuppressWarnings("serial")
public class HorizonLocal extends astrolabe.model.HorizonLocal implements PostscriptEmitter, Projector {

	private final static String DEFAULT_PRACTICALITY = "0" ;

	private Projector projector ;

	private double la ;
	private double ST ;
	private double lo ;

	public HorizonLocal( Object peer, double epoch, Projector projector ) throws ParameterNotValidException {
		String key ;

		ApplicationHelper.setupCompanionFromPeer( this, peer ) ;
		try {
			validate() ;
		} catch ( ValidationException e ) {
			throw new ParameterNotValidException( e.toString() ) ;
		}

		this.projector = projector ;

		la = AstrolabeFactory.valueOf( getLatitude() ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_HORIZON_LATITUDE ) ;
		ApplicationHelper.registerDMS( key, la ) ;

		try {			
			lo = AstrolabeFactory.valueOf( getLongitude() ) ;
			key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_HORIZON_LONGITUDE ) ;
			ApplicationHelper.registerDMS( key, lo ) ;
		} catch ( ParameterNotValidException e ) {
			lo = 0 ;
		}
		try {
			double lt, ra0, lo0, la0, e, jd ;
			CAADate d ;

			jd = AstrolabeFactory.valueOf( getDate() ) ;
			d = new CAADate( jd, true ) ;

			lt = CAACoordinateTransformation.HoursToRadians( d.Hour()+d.Minute()/60.+d.Second()/3600 ) ;

			d.delete() ;

			lt = lt+( lo>Math.rad180?lo-Math.rad360:lo ) ;
			key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_HORIZON_TIMELOCAL ) ;
			ApplicationHelper.registerHMS( key, lt ) ;

			lo0 = ApplicationHelper.meanEclipticLongitude( jd ) ;
			la0 = ApplicationHelper.meanEclipticLatitude( jd ) ;
			e = ApplicationHelper.meanObliquityOfEcliptic( epoch ) ;

			ra0 = ApplicationHelper.ecliptic2Equatorial( lo0, la0, e )[0] ;

			ST = ApplicationHelper.mapTo0To24Range( ra0+lt-Math.rad180/*12h*/ ) ;
			key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_HORIZON_TIMESIDEREAL ) ;
			ApplicationHelper.registerHMS( key, ST ) ;
		} catch ( ParameterNotValidException e ) {
			ST = 0 ;
		}
	}

	public double[] project( double[] lo ) {
		return project( lo[0], lo[1] ) ;
	}

	public double[] project( double A, double h ) {
		return projector.project( convert( A, h ) ) ;
	}

	public double[] unproject( double[] xy ) {
		return unproject( xy[0], xy[1] ) ;
	}

	public double[] unproject( double x, double y ) {
		return unconvert( projector.unproject( x, y ) ) ;
	}

	public double[] convert( double[] lo ) {
		return convert( lo[0], lo[1] ) ;
	}

	public double[] convert( double A, double h ) {
		double[] r ;

		r = ApplicationHelper.horizontal2Equatorial( A, h, la ) ;

		// r[0] is HA is ST-lo-RA.
		r[0] = ST-r[0] ;

		return r ;
	}

	public double[] unconvert( double[] eq ) {
		return unconvert( eq[0], eq[1] ) ;
	}

	public double[] unconvert( double RA, double d ) {
		double[] r ;

		r = ApplicationHelper.equatorial2Horizontal( this.ST-RA, d, la ) ;

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
