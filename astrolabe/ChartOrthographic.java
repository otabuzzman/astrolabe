
package astrolabe;

@SuppressWarnings("serial")
public class ChartOrthographic extends ChartAzimuthalType {

	public ChartOrthographic( Object peer ) throws ParameterNotValidException {
		super( peer ) ;
	}

	double thetaToDistance( double de ) {
		return Math.cos( de ) ;
	}

	double distanceToTheta( double d ) {
		return Math.acos( d ) ;
	}
}
