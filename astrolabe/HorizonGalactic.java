
package astrolabe;

import caa.CAACoordinateTransformation;

public class HorizonGalactic implements Horizon {

	private double grayscale ;
	private double la ;
	private double ST ;

	public HorizonGalactic( astrolabe.model.HorizonType hoT ) {
		double[] eq ;
		String key ;

		grayscale = ApplicationHelper.getClassNode( this, hoT.getName(), ApplicationConstant.PN_HORIZON_PRACTICALITY ).getDouble( hoT.getPracticality(), 0 ) ;
		eq = CAACoordinateTransformation.Galactic2Equatorial( 0, 90 ) ;
		la = CAACoordinateTransformation.DegreesToRadians( eq[1] ) ;
		ST = CAACoordinateTransformation.HoursToRadians( eq[0] ) ;

		try {
			key = Astrolabe.getLocalizedString( ApplicationConstant.LK_HORIZON_LATITUDE ) ;
			ApplicationHelper.registerDMS( key, la, 2 ) ;		
			key = Astrolabe.getLocalizedString( ApplicationConstant.LK_HORIZON_TIMESIDEREAL ) ;
			ApplicationHelper.registerHMS( key, ST, 2 ) ;
		} catch ( ParameterNotValidException e ) {}
	}

	public double[] convert( double[] hoga ) {
		return convert( hoga[0], hoga[1] ) ;
	}

	public double[] convert( double l, double b ) {
		double[] r ;

		r = CAACoordinateTransformation.Galactic2Equatorial(
				CAACoordinateTransformation.RadiansToDegrees( l ),
				CAACoordinateTransformation.RadiansToDegrees( b ) ) ;

		r[0] = CAACoordinateTransformation.HoursToRadians( r[0] ) ;
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

		r = CAACoordinateTransformation.Equatorial2Galactic(
				CAACoordinateTransformation.RadiansToHours( RA ),
				CAACoordinateTransformation.RadiansToDegrees( d ) ) ;

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

	public boolean isEcliptical() {
		return false ;
	}

	public boolean isEquatorial() {
		return false ;
	}

	public boolean isGalactic() {
		return true ;
	}

	public boolean isLocal() {
		return false ;
	}
}
