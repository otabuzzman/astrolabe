
package astrolabe;

import caa.CAACoordinateTransformation ;
import caa.CAADate;

@SuppressWarnings("serial")
public class HorizonLocal extends HorizonType {

	private Projector projector ;

	private double la ;
	private double ST ;
	private double lo ;

	public HorizonLocal( Object peer, Projector projector ) throws ParameterNotValidException {
		super( peer, projector ) ;

		String key ;
		double epoch ;

		this.projector = projector ;

		la = AstrolabeFactory.valueOf( ( (astrolabe.model.HorizonLocal) peer ).getLatitude() ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_HORIZON_LATITUDE ) ;
		ApplicationHelper.registerDMS( key, la ) ;

		try {			
			lo = AstrolabeFactory.valueOf( ( (astrolabe.model.HorizonLocal) peer ).getLongitude() ) ;
			key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_HORIZON_LONGITUDE ) ;
			ApplicationHelper.registerDMS( key, lo ) ;
		} catch ( ParameterNotValidException e ) {
			lo = 0 ;
		}
		try {
			double lt, ra0, lo0, la0, e, jd ;
			CAADate d ;

			jd = AstrolabeFactory.valueOf( ( (astrolabe.model.HorizonLocal) peer ).getDate() ) ;
			d = new CAADate( jd, true ) ;

			lt = CAACoordinateTransformation.HoursToRadians( d.Hour()+d.Minute()/60.+d.Second()/3600 ) ;

			d.delete() ;

			lt = lt+( lo>Math.rad180?lo-Math.rad360:lo ) ;
			key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_HORIZON_TIMELOCAL ) ;
			ApplicationHelper.registerHMS( key, lt ) ;

			lo0 = ApplicationHelper.meanEclipticLongitude( jd ) ;
			la0 = ApplicationHelper.meanEclipticLatitude( jd ) ;
			epoch = ( (Double) Registry.retrieve( ApplicationConstant.GC_EPOCHE ) ).doubleValue() ; 
			e = ApplicationHelper.meanObliquityOfEcliptic( epoch ) ;

			ra0 = ApplicationHelper.ecliptic2Equatorial( lo0, la0, e )[0] ;

			ST = ApplicationHelper.mapTo0To24Range( ra0+lt-Math.rad180/*12h*/ ) ;
			key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_HORIZON_TIMESIDEREAL ) ;
			ApplicationHelper.registerHMS( key, ST ) ;
		} catch ( ParameterNotValidException e ) {
			ST = 0 ;
		}
	}

	public double[] project( double A, double h ) {
		return projector.project( convert( A, h ) ) ;
	}

	public double[] unproject( double x, double y ) {
		return unconvert( projector.unproject( x, y ) ) ;
	}

	public double[] convert( double A, double h ) {
		double[] r ;

		r = ApplicationHelper.horizontal2Equatorial( A, h, la ) ;

		// r[0] is HA is ST-lo-RA.
		r[0] = ST-r[0] ;

		return r ;
	}

	public double[] unconvert( double RA, double d ) {
		double[] r ;

		r = ApplicationHelper.equatorial2Horizontal( this.ST-RA, d, la ) ;

		return r ;
	}
}
