
package astrolabe;

import caa.CAACoordinateTransformation;

@SuppressWarnings("serial")
public class CircleNorthernPolar extends CircleParallel {

	public CircleNorthernPolar( Object peer, double epoch, Projector projector ) throws ParameterNotValidException {
		double radal, degal ;

		radal = Math.rad90-ApplicationHelper.meanObliquityOfEcliptic( epoch ) ;
		degal = CAACoordinateTransformation.RadiansToDegrees( radal ) ;
		( (astrolabe.model.CircleNorthernPolar) peer ).getAngle().getRational().setValue( degal ) ;
		setup( peer, projector ) ;
	}
}
