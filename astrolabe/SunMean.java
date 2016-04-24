
package astrolabe;

import caa.CAACoordinateTransformation;
import caa.CAANutation;

public class SunMean extends Model implements Sun {

	public double[] positionEq( double JD ) {
		double r[] ;
		double lo, la, e ;

		lo = CAASun.MeanEclipticLongitude( JD ) ;
		la = CAASun.MeanEclipticLatitude( JD ) ;
		e = CAANutation.MeanObliquityOfEcliptic( JD ) ;

		r = CAACoordinateTransformation.Ecliptic2Equatorial( lo, la, e ) ;
		r[0] = CAACoordinateTransformation.HoursToRadians( r[0] ) ;

		return r ;
	}
}
