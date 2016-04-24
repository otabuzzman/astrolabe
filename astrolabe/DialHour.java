
package astrolabe;

import caa.CAACoordinateTransformation;

@SuppressWarnings("serial")
public class DialHour extends DialDegree {

	public DialHour( Object peer, Baseline baseline ) {
		super( peer, baseline, CAACoordinateTransformation.HoursToRadians( 1 ) ) ;
	}
}
