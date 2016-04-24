
package astrolabe;

import caa.CAACoordinateTransformation;

@SuppressWarnings("serial")
public class CircleNorthernPolar extends CircleParallel {

	public CircleNorthernPolar( astrolabe.model.CircleNorthernPolar peer, double epoch, Projector projector ) throws ParameterNotValidException {
		double radal, degal ;

		radal = Math.rad90-ApplicationHelper.meanObliquityOfEcliptic( epoch ) ;
		degal = CAACoordinateTransformation.RadiansToDegrees( radal ) ;

		peer.getAngle().getRational().setValue( degal ) ;
		setup( peer, projector ) ;
	}
}
