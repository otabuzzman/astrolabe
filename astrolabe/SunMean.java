
package astrolabe;

import caa.CAACoordinateTransformation;
import caa.CAANutation;

public class SunMean implements Sun {

	private double JD ;
	private boolean mean ; // ecliptic

	public SunMean( astrolabe.model.SunType sT ) {
		mean = sT.getEcliptic().equals( ApplicationConstant.AV_SUN_MEAN ) ;
	}

	public void setJD( double JD ) {
		this.JD = JD ;
	}

	public double[] positionEq() {
		double r[] ;
		double lo, la, e ;

		lo = ApplicationHelper.MeanEclipticLongitude( JD ) ;
		la = ApplicationHelper.MeanEclipticLatitude( JD ) ;
		if ( mean ) {
			e = CAANutation.MeanObliquityOfEcliptic( Astrolabe.getEpoch().Julian() ) ;
		} else {
			e = CAANutation.TrueObliquityOfEcliptic( Astrolabe.getEpoch().Julian() ) ;
		}

		r = CAACoordinateTransformation.Ecliptic2Equatorial( lo, la, e ) ;
		r[0] = CAACoordinateTransformation.HoursToRadians( r[0] ) ;
		r[1] = CAACoordinateTransformation.DegreesToRadians( r[1] ) ;

		return r ;
	}
}
