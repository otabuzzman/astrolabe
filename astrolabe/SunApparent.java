
package astrolabe;

import caa.CAACoordinateTransformation;
import caa.CAASun;

public class SunApparent implements Sun {

	private double JD ;
	private boolean mean ; // ecliptic

	public SunApparent( astrolabe.model.SunType sT ) {
		mean = sT.getEcliptic().equals( ApplicationConstant.AV_SUN_ECLIPTICMEAN ) ;
	}

	public void setJD( double JD ) {
		this.JD = JD ;
	}

	public double[] positionEq() {
		double r[] ;
		double lo, la, e ;

		lo = CAASun.ApparentEclipticLongtitude( JD ) ;
		la = CAASun.ApparentEclipticLatitude( JD ) ;
		e = ApplicationHelper.getObliquityOfEcliptic( mean, Astrolabe.getEpoch().Julian() ) ;

		r = CAACoordinateTransformation.Ecliptic2Equatorial( lo, la, e ) ;
		r[0] = CAACoordinateTransformation.HoursToRadians( r[0] ) ;
		r[1] = CAACoordinateTransformation.DegreesToRadians( r[1] ) ;

		return r ;
	}
}
