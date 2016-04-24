
package astrolabe;

import caa.CAACoordinateTransformation;
import caa.CAANutation;
import caa.CAADate;

public class HorizonEcliptical extends Model implements Horizon {

	private CAADate epoch ;

	private double grayscale ;
	private double la ;
	private double ST ;

	public HorizonEcliptical( astrolabe.model.HorizonType hoT, CAADate epoch ) {
		double[] eq ;
		double e ;
		double JD ;

		this.epoch = epoch ;
		this.grayscale = getClassNode( hoT.getName(), "practicality" ).getDouble( hoT.getPracticality(), 0 ) ;
		JD = epoch.Julian() ;
		e = CAANutation.MeanObliquityOfEcliptic( JD ) ;
		eq = CAACoordinateTransformation.Ecliptic2Equatorial( 0, 90, e ) ;
		la = CAACoordinateTransformation.DegreesToRadians( eq[1] ) ;
		ST = CAACoordinateTransformation.HoursToRadians( eq[0] ) ;

		try {
			ReplacementHelper.registerDMS( "ecliptic",
					CAACoordinateTransformation.DegreesToRadians( e ), 2 ) ;		
			ReplacementHelper.registerDMS( "latitude", la, 2 ) ;		
			ReplacementHelper.registerHMS( "sidereal", ST, 2 ) ;
		} catch ( ParameterNotValidException ePNV ) {}
	}

	public double[] convert( double[] hoec ) {
		return convert( hoec[0], hoec[1] ) ;
	}

	public double[] convert( double l, double b ) {
		double[] r ;
		double e ;
		double JD ;

		JD = epoch.Julian() ;
		e = CAANutation.MeanObliquityOfEcliptic( JD ) ;
		r = CAACoordinateTransformation.Ecliptic2Equatorial(
				CAACoordinateTransformation.RadiansToDegrees( l ),
				CAACoordinateTransformation.RadiansToDegrees( b ) , e ) ;

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
		double e ;
		double JD ;

		JD = epoch.Julian() ;
		e = CAANutation.MeanObliquityOfEcliptic( JD ) ;
		r = CAACoordinateTransformation.Equatorial2Ecliptic(
				CAACoordinateTransformation.RadiansToHours( RA ),
				CAACoordinateTransformation.RadiansToDegrees( d ), e ) ;

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
}
