
package astrolabe;

import caa.CAACoordinateTransformation ;
import caa.CAADate;

public class HorizonLocal implements Horizon {

	private Chart chart ;

	private double grayscale ;
	private double la ;
	private double ST ;
	private double lo ;

	public HorizonLocal( astrolabe.model.HorizonType hoT, Chart chart ) {
		String key ;
		CAADate d = null;

		this.chart = chart ;
		grayscale = ApplicationHelper.getClassNode( this, hoT.getName(), ApplicationConstant.PN_HORIZON_PRACTICALITY ).getDouble( hoT.getPracticality(), 0 ) ;

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
			double lt, ra0, lo0, la0, JD, e ;

			rad180 = CAACoordinateTransformation.DegreesToRadians( 180 ) ;
			rad360 = CAACoordinateTransformation.DegreesToRadians( 360 ) ;

			d = AstrolabeFactory.valueOf( hoT.getDate() ) ;
			lt = CAACoordinateTransformation.HoursToRadians( d.Hour()+d.Minute()/60.+d.Second()/3600 ) ;
			lt = lt+( lo>rad180?lo-rad360:lo ) ;
			key = Astrolabe.getLocalizedString( ApplicationConstant.LK_HORIZON_TIMELOCAL ) ;
			ApplicationHelper.registerHMS( key, lt, 2 ) ;

			lo0 = ApplicationHelper.MeanEclipticLongitude( d.Julian() ) ;
			la0 = ApplicationHelper.MeanEclipticLatitude( d.Julian() ) ;
			JD = chart.dotDot()/*Astrolabe*/.getEpoch() ;
			e = ApplicationHelper.MeanObliquityOfEcliptic( JD ) ;

			ra0 = ApplicationHelper.Ecliptic2Equatorial( lo0, la0, e )[0] ;

			ST = ApplicationHelper.MapTo0To24Range( ra0+lt-rad180/*12h*/ ) ;
			key = Astrolabe.getLocalizedString( ApplicationConstant.LK_HORIZON_TIMESIDEREAL ) ;
			ApplicationHelper.registerHMS( key, ST, 2 ) ;
		} catch ( ParameterNotValidException e ) {
			ST = 0 ;
		} finally {
			if ( d!=null ) {
				d.delete() ;
			}
		}
	}

	public double[] convert( double[] holo ) {
		return convert( holo[0], holo[1] ) ;
	}

	public double[] convert( double A, double h ) {
		double[] r ;

		r = ApplicationHelper.Horizontal2Equatorial( A, h, la ) ;

		// r[0] is HA is ST-lo-RA.
		r[0] = ST-r[0] ;

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

		r = ApplicationHelper.Equatorial2Horizontal( this.ST-RA, d, la ) ;

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

	public Chart dotDot() {
		return chart ;
	}
}
