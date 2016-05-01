
package astrolabe;

import caa.CAANutation;

@SuppressWarnings("serial")
public class CircleSouthernTropic extends CircleParallel {

	public CircleSouthernTropic( Object peer, Projector projector ) throws ParameterNotValidException {
		double epoch, ra ;

		epoch = ( (Double) Registry.retrieve( ApplicationConstant.GC_EPOCHE ) ).doubleValue() ;
		ra = -CAANutation.MeanObliquityOfEcliptic( epoch ) ;

		if ( ( (astrolabe.model.CircleSouthernTropic) peer ).getAngle().getRational() == null ) {
			int d = (int) ra ;
			int m = (int) ( ( ra-d )*60 ) ;
			double s = ( ( ra-d )*60-m )*60 ;

			( (astrolabe.model.CircleSouthernTropic) peer ).getAngle().getDMS().setDeg( d ) ;
			( (astrolabe.model.CircleSouthernTropic) peer ).getAngle().getDMS().setMin( m ) ;
			( (astrolabe.model.CircleSouthernTropic) peer ).getAngle().getDMS().setSec( s ) ;
		} else {
			( (astrolabe.model.CircleSouthernTropic) peer ).getAngle().getRational().setValue( ra ) ;
		}

		setup( peer, projector ) ;
	}
}
