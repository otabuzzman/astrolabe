
package astrolabe;

@SuppressWarnings("serial")
public class ChartOrthographic extends ChartAzimuthalType {

	public ChartOrthographic( Object peer ) throws ParameterNotValidException {
		super( peer ) ;
	}

	double thetaToDistance( double de ) {
		return java.lang.Math.cos( de ) ;
	}

	double distanceToTheta( double d ) {
		return java.lang.Math.acos( d ) ;
	}
}
