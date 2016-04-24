
package astrolabe;

import caa.CAACoordinateTransformation ;

public class CAASun {

	static double MeanEclipticLongitude( double JD ) {
		double r ;
		double rho, rho2, rho3, rho4, rho5 ;

		rho = ( JD-2451545 )/365250 ;
		rho2 = rho*rho ;
		rho3 = rho2*rho ;
		rho4 = rho3*rho ;
		rho5 = rho4*rho ;

		r = CAACoordinateTransformation.MapTo0To360Range(
				280.4664567+360007.6982779*rho+0.03032028*rho2+rho3/49931-rho4/15300-rho5/2000000 ) ;

		return r ;
	}

	static double MeanEclipticLatitude( double JD ) {
		return 0 ;
	}
}
