
package astrolabe;

import caa.CAANutation;

@SuppressWarnings("serial")
public class CircleSouthernPolar extends CircleParallel {

	public CircleSouthernPolar( Projector projector ) {
		super( projector ) ;
	}

	public astrolabe.model.Angle getAngle() {
		astrolabe.model.Angle r ;
		double epoch, o ;
		Double Epoch ;

		Epoch = (Double) Registry.retrieve( astrolabe.Epoch.RK_EPOCH ) ;
		if ( Epoch == null )
			epoch = astrolabe.Epoch.defoult() ;
		else
			epoch = Epoch.doubleValue() ;
		o = CAANutation.MeanObliquityOfEcliptic( epoch ) ;

		r = new astrolabe.model.Angle() ;
		r.setRational( new astrolabe.model.Rational() ) ;
		r.getRational().setValue( -90+o ) ;

		return r ;
	}
}
