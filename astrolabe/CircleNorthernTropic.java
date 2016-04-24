
package astrolabe;

import caa.CAACoordinateTransformation;

@SuppressWarnings("serial")
public class CircleNorthernTropic extends CircleParallel {

	public CircleNorthernTropic( Object peer, double epoch, Projector projector ) throws ParameterNotValidException {
		double radal, degal ;

		radal = ApplicationHelper.meanObliquityOfEcliptic( epoch ) ;
		degal = CAACoordinateTransformation.RadiansToDegrees( radal ) ;
		( (astrolabe.model.CircleNorthernTropic) peer ).getAngle().getRational().setValue( degal ) ;
		setup( peer, projector ) ;
	}
}
