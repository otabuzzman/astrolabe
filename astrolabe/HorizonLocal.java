
package astrolabe;

import caa.CAACoordinateTransformation ;
import caa.CAANutation;
import caa.CAADate;

public class HorizonLocal implements Horizon {

	private double grayscale ;
	private double la ;
	private double ST ;
	private double lo ;

	public HorizonLocal( astrolabe.model.HorizonType hoT ) {
		String key ;

		this.grayscale = ApplicationHelper.getClassNode( this, hoT.getName(), ApplicationConstant.PN_HORIZON_PRACTICALITY ).getDouble( hoT.getPracticality(), 0 ) ;

		try {
			la = AstrolabeFactory.valueOf( hoT.getLatitude() ) ;
			key = Astrolabe.getLocalizedString( ApplicationConstant.LK_HORIZON_LATITUDE ) ;
			ApplicationHelper.registerDMS( key, la, 2 ) ;
		} catch ( ParameterNotValidException e ) {}
		try {			
			lo = AstrolabeFactory.valueOf( hoT.getLongitude() ) ;
			key = Astrolabe.getLocalizedString( ApplicationConstant.LK_HORIZON_LONGITUDE ) ;
			ApplicationHelper.registerDMS( key, lo, 2 ) ;
		} catch ( ParameterNotValidException e ) {
			lo = 0 ;
		}
		try {
			double rad180, rad360 ;
			double lt, ra0, lo0, la0, e ;
			CAADate date ;

			rad180 = CAACoordinateTransformation.DegreesToRadians( 180 ) ;
			rad360 = CAACoordinateTransformation.DegreesToRadians( 360 ) ;

			date = AstrolabeFactory.valueOf( hoT.getDate() ) ;
			lt = date.Hour()+date.Minute()/60.+date.Second()/3600 ;
			lt = lt+CAACoordinateTransformation.RadiansToHours( lo>rad180?lo-rad360:lo ) ;
			key = Astrolabe.getLocalizedString( ApplicationConstant.LK_HORIZON_TIMELOCAL ) ;
			ApplicationHelper.registerHMS( key,
					CAACoordinateTransformation.HoursToRadians( lt ), 2 ) ;

			lo0 = ApplicationHelper.MeanEclipticLongitude( date.Julian() ) ;
			la0 = ApplicationHelper.MeanEclipticLatitude( date.Julian() ) ;
			e = CAANutation.MeanObliquityOfEcliptic( Astrolabe.getEpoch().Julian() ) ;

			ra0 = CAACoordinateTransformation.Ecliptic2Equatorial( lo0, la0, e )[0] ;

			ST = CAACoordinateTransformation.HoursToRadians(
					CAACoordinateTransformation.MapTo0To24Range( ra0+lt-12 ) ) ;
			key = Astrolabe.getLocalizedString( ApplicationConstant.LK_HORIZON_TIMESIDEREAL ) ;
			ApplicationHelper.registerHMS( key, ST, 2 ) ;
		} catch ( ParameterNotValidException e ) {
			ST = 0 ;
		}
	}

	public double[] convert( double[] holo ) {
		return convert( holo[0], holo[1] ) ;
	}

	public double[] convert( double A, double h ) {
		double[] r ;

		r = CAACoordinateTransformation.Horizontal2Equatorial(
				CAACoordinateTransformation.RadiansToDegrees( A ),
				CAACoordinateTransformation.RadiansToDegrees( h ),
				CAACoordinateTransformation.RadiansToDegrees( la ) ) ;

		// r[0] is HA is ST-lo-RA.
		r[0] = ST-CAACoordinateTransformation.HoursToRadians( r[0] ) ;
		r[1] = CAACoordinateTransformation.DegreesToRadians( r[1] ) ;

		return r ;
	}

	public void initPS( PostscriptStream ps ) {
		ps.operator.setgray( grayscale ) ; 
	}

	public double[] unconvert( double[] eq ) {
		return unconvert( eq[0], eq[1] ) ;
	}

	public double[] unconvert( double RA, double d ) {
		double[] r ;

		r = CAACoordinateTransformation.Equatorial2Horizontal(
				CAACoordinateTransformation.RadiansToHours( this.ST-RA ),
				CAACoordinateTransformation.RadiansToDegrees( d ),
				CAACoordinateTransformation.RadiansToDegrees( la ) ) ;

		r[0] = CAACoordinateTransformation.DegreesToRadians( r[0] ) ;
		r[1] = CAACoordinateTransformation.DegreesToRadians( r[1] ) ;

		return r ;
	}

	public double getLa() {
		return la ;
	}

	public double getST() {
		return ST ;
	}

	public double getLo() {
		return lo ;
	}
	public boolean isEcliptical() {
		return false ;
	}

	public boolean isEquatorial() {
		return false ;
	}

	public boolean isGalactic() {
		return false ;
	}

	public boolean isLocal() {
		return true ;
	}
}
