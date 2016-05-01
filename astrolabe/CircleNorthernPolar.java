
package astrolabe;

import caa.CAANutation;

@SuppressWarnings("serial")
public class CircleNorthernPolar extends CircleParallel {

	public CircleNorthernPolar( Peer peer, Projector projector ) {
		double epoch, ra ;

		epoch = ( (Double) AstrolabeRegistry.retrieve( ApplicationConstant.GC_EPOCHE ) ).doubleValue() ;

		ra = 90-CAANutation.MeanObliquityOfEcliptic( epoch ) ;

		if ( ( (astrolabe.model.CircleNorthernPolar) peer ).getAngle().getRational() == null ) {
			int d = (int) ra ;
			int m = (int) ( ( ra-d )*60 ) ;
			double s = ( ( ra-d )*60-m )*60 ;

			( (astrolabe.model.CircleNorthernPolar) peer ).getAngle().getDMS().setDeg( d ) ;
			( (astrolabe.model.CircleNorthernPolar) peer ).getAngle().getDMS().setMin( m ) ;
			( (astrolabe.model.CircleNorthernPolar) peer ).getAngle().getDMS().setSec( s ) ;
		} else {
			( (astrolabe.model.CircleNorthernPolar) peer ).getAngle().getRational().setValue( ra ) ;
		}

		setup( peer, projector ) ;
	}
}
