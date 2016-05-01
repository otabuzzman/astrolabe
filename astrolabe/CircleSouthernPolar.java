
package astrolabe;

import caa.CAACoordinateTransformation;

@SuppressWarnings("serial")
public class CircleSouthernPolar extends CircleParallel {

	public CircleSouthernPolar( Object peer, Projector projector ) throws ParameterNotValidException {
		double epoch, radal, degal ;

		epoch = ( (Double) Registry.retrieve( ApplicationConstant.GC_EPOCHE ) ).doubleValue() ;
		radal = -Math.rad90+ApplicationHelper.meanObliquityOfEcliptic( epoch ) ;
		degal = CAACoordinateTransformation.RadiansToDegrees( radal ) ;

		if ( ( (astrolabe.model.CircleSouthernPolar) peer ).getAngle().getRational() == null ) {
			int d = (int) degal ;
			int m = (int) ( ( degal-d )*60 ) ;
			double s = ( ( degal-d )*60-m )*60 ;

			( (astrolabe.model.CircleSouthernPolar) peer ).getAngle().getDMS().setDeg( d ) ;
			( (astrolabe.model.CircleSouthernPolar) peer ).getAngle().getDMS().setMin( m ) ;
			( (astrolabe.model.CircleSouthernPolar) peer ).getAngle().getDMS().setSec( s ) ;
		} else {
			( (astrolabe.model.CircleSouthernPolar) peer ).getAngle().getRational().setValue( degal ) ;
		}

		setup( peer, projector ) ;
	}
}
