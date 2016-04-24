
package astrolabe;

import caa.CAACoordinateTransformation;

public class CircleSouthernPolar extends CircleParallel {

	public CircleSouthernPolar( astrolabe.model.CircleType clT, Horizon horizon ) throws ParameterNotValidException {
		super( clT, horizon ) ;

		double rad90, al, JD ;

		rad90 = CAACoordinateTransformation.DegreesToRadians( 90 ) ;

		if ( ! horizon.isEquatorial() ) {
			throw new ParameterNotValidException() ;
		}

		JD = horizon.dotDot()/*Chart*/.dotDot()/*Astrolabe*/.getEpoch() ;
		al = -rad90+ApplicationHelper.MeanObliquityOfEcliptic( JD ) ;
		setAl( al ) ;
	}
}
