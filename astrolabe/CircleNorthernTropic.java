
package astrolabe;

import caa.CAANutation;

@SuppressWarnings("serial")
public class CircleNorthernTropic extends CircleParallel {

	public CircleNorthernTropic( Converter converter, Projector projector ) {
		super( converter, projector ) ;
	}

	public astrolabe.model.Angle getAngle() {
		astrolabe.model.Angle r ;
		double epoch, o ;
		Double Epoch ;

		Epoch = (Double) Registry.retrieve( Epoch.class.getName() ) ;
		if ( Epoch == null )
			epoch = astrolabe.Epoch.defoult() ;
		else
			epoch = Epoch.doubleValue() ;
		o = CAANutation.MeanObliquityOfEcliptic( epoch ) ;

		r = new astrolabe.model.Angle() ;
		r.setRational( new astrolabe.model.Rational() ) ;
		r.getRational().setValue( o ) ;

		return r ;
	}
}
