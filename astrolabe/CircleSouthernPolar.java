
package astrolabe;

import caa.CAANutation;

@SuppressWarnings("serial")
public class CircleSouthernPolar extends CircleParallel {

	public CircleSouthernPolar( Peer peer, Projector projector ) {
		super( peer, projector ) ;
	}

	public astrolabe.model.Angle getAngle() {
		astrolabe.model.Angle r ;
		double e, o ;

		e = ( (Double) AstrolabeRegistry.retrieve( ApplicationConstant.GC_EPOCH ) ).doubleValue() ;
		o = CAANutation.MeanObliquityOfEcliptic( e ) ;

		r = new astrolabe.model.Angle() ;
		r.setRational( new astrolabe.model.Rational() ) ;
		r.getRational().setValue( -90+o ) ;

		return r ;
	}
}
