
package astrolabe;

public class Sun {

	private final static int SUN_APPARENT = 1 ;
	private final static int SUN_MEAN = 2 ;
	private final static int SUN_TRUE = 3 ;

	private int position ;

	public Sun() {
		position = SUN_MEAN ;
	}

	public void setPositionApparent() {
		position = SUN_APPARENT ;
	}

	public void setPositionMean() {
		position = SUN_MEAN ;
	}

	public void setPositionTrue() {
		position = SUN_TRUE ;
	}

	public double[] positionEq( double JD ) {
		double[] r ;
		double lo, la, e ;

		e = ApplicationHelper.MeanObliquityOfEcliptic( JD ) ;

		switch ( position ) {
		case SUN_APPARENT :
			lo = ApplicationHelper.ApparentEclipticLongitude( JD ) ;
			la = ApplicationHelper.ApparentEclipticLatitude( JD ) ;
			break ;
		default :
		case SUN_MEAN :
			lo = ApplicationHelper.MeanEclipticLongitude( JD ) ;
			la = ApplicationHelper.MeanEclipticLatitude( JD ) ;
			break ;
		case SUN_TRUE :
			lo = ApplicationHelper.GeometricEclipticLongitude( JD ) ;
			la = ApplicationHelper.GeometricEclipticLatitude( JD ) ;
			break ;
		}

		r = ApplicationHelper.Ecliptic2Equatorial( lo, la, e ) ;

		return r ;
	}
}
