
package astrolabe;

@SuppressWarnings("serial")
public class ChartGnomonic extends ChartAzimuthalType {

	public ChartGnomonic( Object peer ) throws ParameterNotValidException {
		super( peer ) ;
	}

	double thetaToDistance( double de ) {
		return java.lang.Math.tan( Math.rad90-de ) ;
	}

	double distanceToTheta( double d ) {
		return Math.rad90-java.lang.Math.atan( d ) ;
	}
}
