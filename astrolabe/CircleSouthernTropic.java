
package astrolabe;

import caa.CAACoordinateTransformation;

@SuppressWarnings("serial")
public class CircleSouthernTropic extends CircleParallel {

	public CircleSouthernTropic( Object peer, double epoch, Projector projector ) throws ParameterNotValidException {
		double radal, degal ;

		radal = -ApplicationHelper.meanObliquityOfEcliptic( epoch ) ;
		degal = CAACoordinateTransformation.RadiansToDegrees( radal ) ;
		( (astrolabe.model.CircleSouthernTropic) peer ).getAngle().getRational().setValue( degal ) ;
		setup( peer, projector ) ;
	}
}
