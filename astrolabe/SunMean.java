
package astrolabe;

import caa.CAACoordinateTransformation;
import caa.CAANutation;

public class SunMean implements Sun {

	public double[] positionEq( double JD ) {
		double r[] ;
		double lo, la, e ;

		lo = CAAHelper.MeanEclipticLongitude( JD ) ;
		la = CAAHelper.MeanEclipticLatitude( JD ) ;
		e = CAANutation.MeanObliquityOfEcliptic( JD ) ;

		r = CAACoordinateTransformation.Ecliptic2Equatorial( lo, la, e ) ;
		r[0] = CAACoordinateTransformation.HoursToRadians( r[0] ) ;
		r[1] = CAACoordinateTransformation.DegreesToRadians( r[1] ) ;

		return r ;
	}
}
