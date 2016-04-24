
package astrolabe;

public class CircleSouthernTropic extends CircleParallel {

	public CircleSouthernTropic( astrolabe.model.CircleType clT, Horizon horizon ) throws ParameterNotValidException {
		super( clT, horizon ) ;

		double al, JD ;

		if ( ! horizon.isEquatorial() ) {
			throw new ParameterNotValidException() ;
		}

		JD = horizon.dotDot()/*Chart*/.dotDot()/*Astrolabe*/.getEpoch() ;
		al = -ApplicationHelper.MeanObliquityOfEcliptic( JD ) ;
		setAl( al ) ;
	}
}
