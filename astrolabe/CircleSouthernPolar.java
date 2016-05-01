
package astrolabe;

import caa.CAANutation;

@SuppressWarnings("serial")
public class CircleSouthernPolar extends CircleParallel {

	public CircleSouthernPolar( Object peer, Projector projector ) throws ParameterNotValidException {
		double epoch, ra ;

		epoch = ( (Double) Registry.retrieve( ApplicationConstant.GC_EPOCHE ) ).doubleValue() ;
		ra = -90+CAANutation.MeanObliquityOfEcliptic( epoch ) ;

		if ( ( (astrolabe.model.CircleSouthernPolar) peer ).getAngle().getRational() == null ) {
			int d = (int) ra ;
			int m = (int) ( ( ra-d )*60 ) ;
			double s = ( ( ra-d )*60-m )*60 ;

			( (astrolabe.model.CircleSouthernPolar) peer ).getAngle().getDMS().setDeg( d ) ;
			( (astrolabe.model.CircleSouthernPolar) peer ).getAngle().getDMS().setMin( m ) ;
			( (astrolabe.model.CircleSouthernPolar) peer ).getAngle().getDMS().setSec( s ) ;
		} else {
			( (astrolabe.model.CircleSouthernPolar) peer ).getAngle().getRational().setValue( ra ) ;
		}

		setup( peer, projector ) ;
	}
}
