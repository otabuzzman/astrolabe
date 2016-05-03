
package astrolabe;

import caa.CAANutation;

@SuppressWarnings("serial")
public class CircleNorthernTropic extends CircleParallel {

	public CircleNorthernTropic( Converter converter, Projector projector ) {
		super( converter, projector ) ;
	}

	public astrolabe.model.Angle getAngle() {
		astrolabe.model.Angle r ;
		double e, o ;
		Epoch epoch ;

		epoch = (Epoch) Registry.retrieve( Epoch.class.getName() ) ;
		if ( epoch == null )
			e = new Epoch().alpha() ;
		else
			e = epoch.alpha() ;
		o = CAANutation.MeanObliquityOfEcliptic( e ) ;

		r = new astrolabe.model.Angle() ;
		r.setRational( new astrolabe.model.Rational() ) ;
		r.getRational().setValue( o ) ;

		return r ;
	}
}
