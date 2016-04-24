
package astrolabe;

import caa.CAACoordinateTransformation ;

public class CAAHelper {

	static double MapTo0To24Range( double h ) {
		return CAACoordinateTransformation.DegreesToRadians(
				CAACoordinateTransformation.MapTo0To24Range(
						CAACoordinateTransformation.RadiansToHours( h ) ) ) ;
	}

	static double MapTo0To90Range( double d ) {
		double r ;

		r = d ;

		while ( r<0 ) {
			r = r+java.lang.Math.PI/2 ;
		}
		while ( r>java.lang.Math.PI/2 ) {
			r = r-java.lang.Math.PI/2 ;
		}

		return r ;
	}

	static double MapTo0To360Range( double d ) {
		return CAACoordinateTransformation.DegreesToRadians(
				CAACoordinateTransformation.MapTo0To360Range(
						CAACoordinateTransformation.RadiansToDegrees( d ) ) ) ;
	}

	static double[] Horizontal2Equatorial( double az, double al, double la ) {
		double[] r ;

		r = CAACoordinateTransformation.Horizontal2Equatorial(
				CAACoordinateTransformation.RadiansToDegrees( az  ),
				CAACoordinateTransformation.RadiansToDegrees( al ),
				CAACoordinateTransformation.RadiansToDegrees( la ) ) ;
		r[0] = CAACoordinateTransformation.HoursToRadians( r[0] ) ;
		r[1] = CAACoordinateTransformation.DegreesToRadians( r[1] ) ;

		return r ;
	}

	static double[] Equatorial2Horizontal( double HA, double de, double la ) {
		double[] r ;

		r = CAACoordinateTransformation.Equatorial2Horizontal(
				CAACoordinateTransformation.RadiansToHours( HA  ),
				CAACoordinateTransformation.RadiansToDegrees( de ),
				CAACoordinateTransformation.RadiansToDegrees( la ) ) ;
		r[0] = CAACoordinateTransformation.DegreesToRadians( r[0] ) ;
		r[1] = CAACoordinateTransformation.DegreesToRadians( r[1] ) ;

		return r ;
	}

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
