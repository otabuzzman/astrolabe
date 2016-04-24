
package astrolabe;

import caa.CAACoordinateTransformation;

public class SpanHour extends SpanDegree {

	public SpanHour( Circle circle ) {
		super( circle ) ;

		double unit ;

		unit = CAACoordinateTransformation.DegreesToRadians( 15 ) ;
		setUnit( unit ) ;
	}
}
