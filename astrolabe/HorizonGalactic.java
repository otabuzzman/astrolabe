
package astrolabe;

import caa.CAACoordinateTransformation;

public class HorizonGalactic implements Horizon {

	private Chart chart ;

	private double grayscale ;
	private double la ;
	private double ST ;

	public HorizonGalactic( astrolabe.model.HorizonType hoT, Chart chart ) {
		double[] eq ;
		double rad90 ;
		String key ;

		rad90 = CAACoordinateTransformation.DegreesToRadians( 90 ) ;

		this.chart = chart ;
		grayscale = ApplicationHelper.getClassNode( this, hoT.getName(), ApplicationConstant.PN_HORIZON_PRACTICALITY ).getDouble( hoT.getPracticality(), 0 ) ;

		eq = ApplicationHelper.Galactic2Equatorial( 0, rad90 ) ;
		la = eq[1] ;
		ST = eq[0] ;

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

		r = ApplicationHelper.Galactic2Equatorial( l, b ) ;

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

		r = ApplicationHelper.Equatorial2Galactic( RA, d ) ;

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

	public Chart dotDot() {
		return chart ;
	}
}
