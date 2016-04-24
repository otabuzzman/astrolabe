
package astrolabe;

import caa.CAACoordinateTransformation;

@SuppressWarnings("serial")
public class DialHour extends DialDegree {

	public DialHour( Object peer, Circle circle ) {
		super( peer, circle, CAACoordinateTransformation.HoursToRadians( 1 ) ) ;
	}
}
