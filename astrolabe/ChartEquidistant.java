
package astrolabe;

@SuppressWarnings("serial")
public class ChartEquidistant extends ChartAzimuthalType {

	public ChartEquidistant( Object peer ) throws ParameterNotValidException {
		super( peer ) ;
	}

	double thetaToDistance( double de ) {
		return ( Math.rad90-de )/Math.rad90 ;
	}

	double distanceToTheta( double d ) {
		return Math.rad90-d*Math.rad90 ;
	}
}
