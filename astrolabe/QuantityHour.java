
package astrolabe;

import caa.CAACoordinateTransformation;

public class QuantityHour extends QuantityDegree {

	public QuantityHour( Circle circle ) {
		super( circle ) ;

		double unit ;

		unit = CAACoordinateTransformation.DegreesToRadians( 15 ) ;
		setUnit( unit ) ;
	}
}
