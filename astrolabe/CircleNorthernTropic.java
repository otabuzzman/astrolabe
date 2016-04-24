
package astrolabe;

import caa.CAACoordinateTransformation;
import caa.CAANutation;
import caa.CAADate;

public class CircleNorthernTropic extends CircleParallel {

	public CircleNorthernTropic( astrolabe.model.CircleType clT, Chart chart, Horizon horizon, CAADate epoch )
	throws ParameterNotValidException {
		super( clT, chart, horizon ) ;

		if ( ! horizon.isEquatorial() ) {
			throw new ParameterNotValidException() ;
		}

		al = CAACoordinateTransformation.DegreesToRadians( CAANutation.MeanObliquityOfEcliptic( epoch.Julian() ) ) ;
	}
}
