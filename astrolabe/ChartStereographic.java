
package astrolabe;

@SuppressWarnings("serial")
public class ChartStereographic extends ChartAzimuthalType {

	public ChartStereographic( Peer peer ) {
		super( peer ) ;
	}

	double thetaToDistance( double de ) {
		return Math.tan( ( 90-de )/2 ) ;
	}

	double distanceToTheta( double d ) {
		return 90-Math.atan( d )*2 ;
	}
}
