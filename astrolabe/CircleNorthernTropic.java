
package astrolabe;

import java.util.Vector;

import caa.CAACoordinateTransformation;
import caa.CAANutation;
import caa.CAADate;

public class CircleNorthernTropic extends CircleParallel {

	public CircleNorthernTropic( astrolabe.model.CircleType clT, Chart chart, Horizon horizon, CAADate epoch )
	throws ParameterNotValidException, ParameterNotPlausibleException {
		super( clT, chart, horizon ) ;

		if ( ! horizon.isEquatorial() ) {
			throw new ParameterNotPlausibleException() ;
		}

		al = CAACoordinateTransformation.DegreesToRadians( CAANutation.MeanObliquityOfEcliptic( epoch.Julian() ) ) ;
	}
}
