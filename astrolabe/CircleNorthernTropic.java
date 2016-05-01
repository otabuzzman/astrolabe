
package astrolabe;

import caa.CAANutation;

@SuppressWarnings("serial")
public class CircleNorthernTropic extends CircleParallel {

	public CircleNorthernTropic( Peer peer, Projector projector ) throws ParameterNotValidException {
		double epoch, ra ;

		epoch = ( (Double) Registry.retrieve( ApplicationConstant.GC_EPOCHE ) ).doubleValue() ;
		ra = CAANutation.MeanObliquityOfEcliptic( epoch ) ;

		if ( ( (astrolabe.model.CircleNorthernTropic) peer ).getAngle().getRational() == null ) {
			int d = (int) ra ;
			int m = (int) ( ( ra-d )*60 ) ;
			double s = ( ( ra-d )*60-m )*60 ;

			( (astrolabe.model.CircleNorthernTropic) peer ).getAngle().getDMS().setDeg( d ) ;
			( (astrolabe.model.CircleNorthernTropic) peer ).getAngle().getDMS().setMin( m ) ;
			( (astrolabe.model.CircleNorthernTropic) peer ).getAngle().getDMS().setSec( s ) ;
		} else {
			( (astrolabe.model.CircleNorthernTropic) peer ).getAngle().getRational().setValue( ra ) ;
		}

		setup( peer, projector ) ;
	}
}
