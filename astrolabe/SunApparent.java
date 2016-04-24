
package astrolabe;

import caa.CAACoordinateTransformation;
import caa.CAANutation;

public class SunApparent implements Sun {

	public double[] positionEq( double JD ) {
		double r[] ;
		double lo, la, e ;

		lo = caa.CAASun.ApparentEclipticLongtitude( JD ) ;
		la = caa.CAASun.ApparentEclipticLatitude( JD ) ;
		e = CAANutation.MeanObliquityOfEcliptic( JD ) ;

		r = CAACoordinateTransformation.Ecliptic2Equatorial( lo, la, e ) ;
		r[0] = CAACoordinateTransformation.HoursToRadians( r[0] ) ;
		r[1] = CAACoordinateTransformation.DegreesToRadians( r[1] ) ;

		return r ;
	}
}
