
package astrolabe;

import caa.CAACoordinateTransformation;
import caa.CAANutation;

public class CircleSouthernTropic extends CircleParallel {

	public CircleSouthernTropic( astrolabe.model.CircleType clT, Chart chart, Horizon horizon ) throws ParameterNotValidException {
		super( clT, chart, horizon ) ;

		double al ;

		if ( ! horizon.isEquatorial() ) {
			throw new ParameterNotValidException() ;
		}

		al = -CAACoordinateTransformation.DegreesToRadians( CAANutation.MeanObliquityOfEcliptic( Astrolabe.getEpoch().Julian() ) ) ;
		setAl( al ) ;
	}
}
