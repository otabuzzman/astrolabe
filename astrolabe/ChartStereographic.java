
package astrolabe;

@SuppressWarnings("serial")
public class ChartStereographic extends ChartAzimuthalType {

	public ChartStereographic( Object peer ) throws ParameterNotValidException {
		super( peer ) ;
	}

	double thetaToDistance( double de ) {
		return java.lang.Math.tan( ( Math.rad90-de )/2 ) ;
	}

	double distanceToTheta( double d ) {
		return Math.rad90-java.lang.Math.atan( d )*2 ;
	}
}
