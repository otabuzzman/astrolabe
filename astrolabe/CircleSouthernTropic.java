
package astrolabe;

import caa.CAACoordinateTransformation;

@SuppressWarnings("serial")
public class CircleSouthernTropic extends CircleParallel {

	public CircleSouthernTropic( Object peer, double epoch, Projector projector ) throws ParameterNotValidException {
		double radal, degal ;

		radal = -ApplicationHelper.meanObliquityOfEcliptic( epoch ) ;
		degal = CAACoordinateTransformation.RadiansToDegrees( radal ) ;

		if ( ( (astrolabe.model.CircleSouthernTropic) peer ).getAngle().getRational() == null ) {
			int d = (int) degal ;
			int m = (int) ( ( degal-d )*60 ) ;
			double s = ( ( degal-d )*60-m )*60 ;

			( (astrolabe.model.CircleSouthernTropic) peer ).getAngle().getDMS().setDeg( d ) ;
			( (astrolabe.model.CircleSouthernTropic) peer ).getAngle().getDMS().setMin( m ) ;
			( (astrolabe.model.CircleSouthernTropic) peer ).getAngle().getDMS().setSec( s ) ;
		} else {
			( (astrolabe.model.CircleSouthernTropic) peer ).getAngle().getRational().setValue( degal ) ;
		}

		setup( peer, epoch, projector ) ;
	}
}
