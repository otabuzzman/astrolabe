
package astrolabe;

@SuppressWarnings("serial")
public class ChartEquidistant extends ChartAzimuthalType {

	public ChartEquidistant( Peer peer ) {
		super( peer ) ;
	}

	double thetaToDistance( double de ) {
		return ( 90-de )/90 ;
	}

	double distanceToTheta( double d ) {
		return 90-d*90 ;
	}
}
