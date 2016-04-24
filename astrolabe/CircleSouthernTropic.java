
package astrolabe;

public class CircleSouthernTropic extends CircleParallel {

	public CircleSouthernTropic( astrolabe.model.CircleType clT, Chart chart, Horizon horizon ) throws ParameterNotValidException {
		super( clT, chart, horizon ) ;

		double al ;

		if ( ! horizon.isEquatorial() ) {
			throw new ParameterNotValidException() ;
		}

		al = -ApplicationHelper.getObliquityOfEcliptic( Astrolabe.isEclipticMean(),
				Astrolabe.getEpoch().Julian() ) ;
		setAl( al ) ;
	}
}
