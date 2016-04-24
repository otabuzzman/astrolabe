
package astrolabe;

import java.util.Vector;

import caa.CAACoordinateTransformation;
import caa.CAANutation;
import caa.CAADate;

public class CircleSouthernTropic extends CircleParallel {

	public CircleSouthernTropic( astrolabe.model.CircleType clT, Chart chart, Horizon horizon, CAADate epoch )
	throws ParameterNotValidException, ParameterNotPlausibleException {
		super( clT, chart, horizon ) ;

		if ( ! horizon.isEquatorial() ) {
			throw new ParameterNotPlausibleException() ;
		}

		al = -CAACoordinateTransformation.DegreesToRadians( CAANutation.MeanObliquityOfEcliptic( epoch.Julian() ) ) ;
	}
}
