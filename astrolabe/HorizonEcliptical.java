
package astrolabe;

import caa.CAACoordinateTransformation;

public class HorizonEcliptical implements Horizon {

	private Chart chart ;

	private double grayscale ;
	private double la ;
	private double ST ;

	private double e ; // mean obliquity of ecliptic

	public HorizonEcliptical( astrolabe.model.HorizonType hoT, Chart chart ) {
		double[] eq ;
		double JD, rad90 ;
		String key ;

		rad90 = CAACoordinateTransformation.DegreesToRadians( 90 ) ;

		this.chart = chart ;
		grayscale = ApplicationHelper.getClassNode( this, hoT.getName(), ApplicationConstant.PN_HORIZON_PRACTICALITY ).getDouble( hoT.getPracticality(), 0 ) ;

		JD = chart.dotDot()/*Astrolabe*/.getEpoch() ;
		e = ApplicationHelper.MeanObliquityOfEcliptic( JD ) ;
		eq = ApplicationHelper.Ecliptic2Equatorial( 0, rad90, e ) ;
		la = eq[1] ;
		ST = eq[0] ;

		try {
			key = Astrolabe.getLocalizedString( ApplicationConstant.LK_HORIZON_ECLIPTICEPSILON ) ;
			ApplicationHelper.registerDMS( key, e, 2 ) ;
			key = Astrolabe.getLocalizedString( ApplicationConstant.LK_HORIZON_LATITUDE ) ;
			ApplicationHelper.registerDMS( "latitude", la, 2 ) ;		
		} catch ( ParameterNotValidException ePNV ) {}
	}

	public double[] convert( double[] hoec ) {
		return convert( hoec[0], hoec[1] ) ;
	}

	public double[] convert( double l, double b ) {
		double[] r ;

		r = ApplicationHelper.Ecliptic2Equatorial( l, b, e ) ;

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

		r = ApplicationHelper.Equatorial2Ecliptic( RA, d, e ) ;

		return r ;
	}

	public double getLa() {
		return la ;
	}

	public double getST() {
		return ST ;
	}

	public boolean isEcliptical() {
		return true ;
	}

	public boolean isEquatorial() {
		return false ;
	}

	public boolean isGalactic() {
		return false ;
	}

	public boolean isLocal() {
		return false ;
	}

	public Chart dotDot() {
		return chart ;
	}
}
