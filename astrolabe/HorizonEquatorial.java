
package astrolabe;

public class HorizonEquatorial implements Horizon {

	private double grayscale ;
	private double la ;

	public HorizonEquatorial( astrolabe.model.HorizonType hoT ) {
		grayscale = ApplicationHelper.getClassNode( this, hoT.getName(), ApplicationConstant.PN_HORIZON_PRACTICALITY ).getDouble( hoT.getPracticality(), 0 ) ;
		la = caa.CAACoordinateTransformation.DegreesToRadians( 90 ) ;
	}

	public double[] convert( double[] hoeq ) {
		return convert( hoeq[0], hoeq[1] ) ;
	}

	public double[] convert( double RA, double d ) {
		double[] r = new double[2] ;

		r[0] = RA ;
		r[1] = d ;

		return r ;
	}

	public void initPS( PostscriptStream ps ) {
		ps.operator.setgray( grayscale ) ; 
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

	public double getLa() {
		return la ;
	}

	public double getST() {
		return 0 ;
	}

	public boolean isEcliptical() {
		return false ;
	}

	public boolean isEquatorial() {
		return true ;
	}

	public boolean isGalactic() {
		return false ;
	}

	public boolean isLocal() {
		return false ;
	}
}
