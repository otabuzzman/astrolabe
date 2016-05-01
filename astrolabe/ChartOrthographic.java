
package astrolabe;

@SuppressWarnings("serial")
public class ChartOrthographic extends ChartAzimuthalType {

	public ChartOrthographic( Peer peer ) {
		super( peer ) ;
	}

	double thetaToDistance( double de ) {
		return Math.cos( de ) ;
	}

	double distanceToTheta( double d ) {
		return Math.acos( d ) ;
	}
}
