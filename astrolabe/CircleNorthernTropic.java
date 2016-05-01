
package astrolabe;

import caa.CAACoordinateTransformation;

@SuppressWarnings("serial")
public class CircleNorthernTropic extends CircleParallel {

	public CircleNorthernTropic( Object peer, Projector projector ) throws ParameterNotValidException {
		double epoch, radal, degal ;

		epoch = ( (Double) Registry.retrieve( ApplicationConstant.GC_EPOCHE ) ).doubleValue() ;
		radal = ApplicationHelper.meanObliquityOfEcliptic( epoch ) ;
		degal = CAACoordinateTransformation.RadiansToDegrees( radal ) ;

		if ( ( (astrolabe.model.CircleNorthernTropic) peer ).getAngle().getRational() == null ) {
			int d = (int) degal ;
			int m = (int) ( ( degal-d )*60 ) ;
			double s = ( ( degal-d )*60-m )*60 ;

			( (astrolabe.model.CircleNorthernTropic) peer ).getAngle().getDMS().setDeg( d ) ;
			( (astrolabe.model.CircleNorthernTropic) peer ).getAngle().getDMS().setMin( m ) ;
			( (astrolabe.model.CircleNorthernTropic) peer ).getAngle().getDMS().setSec( s ) ;
		} else {
			( (astrolabe.model.CircleNorthernTropic) peer ).getAngle().getRational().setValue( degal ) ;
		}

		setup( peer, projector ) ;
	}
}
