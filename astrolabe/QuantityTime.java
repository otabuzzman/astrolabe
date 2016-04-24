
package astrolabe;

import caa.CAACoordinateTransformation;

public class QuantityTime extends QuantityAngle {

	public QuantityTime( Circle circle ) {
		super( circle ) ;

		unit = CAACoordinateTransformation.DegreesToRadians( 15 ) ;
	}
}
