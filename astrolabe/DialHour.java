
package astrolabe;

import caa.CAACoordinateTransformation;

@SuppressWarnings("serial")
public class DialHour extends DialDegree {

	public DialHour( Baseline baseline ) {
		super( baseline, CAACoordinateTransformation.HoursToDegrees( 1 ) ) ;
	}
}
