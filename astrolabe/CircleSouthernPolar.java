
package astrolabe;

import java.util.Vector;

import caa.CAACoordinateTransformation;
import caa.CAANutation;
import caa.CAADate;

public class CircleSouthernPolar extends CircleParallel {

	public CircleSouthernPolar( astrolabe.model.CircleType clT, Chart chart, Horizon horizon, CAADate epoch )
	throws ParameterNotValidException, ParameterNotPlausibleException {
		super( clT, chart, horizon ) ;

		double rad90 = CAACoordinateTransformation.DegreesToRadians( 90 ) ;

		if ( ! horizon.isEquatorial() ) {
			throw new ParameterNotPlausibleException() ;
		}

		al = -rad90+CAACoordinateTransformation.DegreesToRadians( CAANutation.MeanObliquityOfEcliptic( epoch.Julian() ) ) ;
	}
}
