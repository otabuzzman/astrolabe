
package astrolabe;

import caa.CAACoordinateTransformation;

public class CircleNorthernPolar extends CircleParallel {

	public CircleNorthernPolar( astrolabe.model.CircleType clT, Chart chart, Horizon horizon ) throws ParameterNotValidException {
		super( clT, chart, horizon ) ;

		double rad90, al ;

		rad90 = CAACoordinateTransformation.DegreesToRadians( 90 ) ;

		if ( ! horizon.isEquatorial() ) {
			throw new ParameterNotValidException() ;
		}

		al = rad90-ApplicationHelper.getObliquityOfEcliptic( Astrolabe.isEclipticMean(),
				Astrolabe.getEpoch().Julian() ) ;
		setAl( al ) ;
	}
}
