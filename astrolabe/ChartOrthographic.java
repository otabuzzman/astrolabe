
package astrolabe;

@SuppressWarnings("serial")
public class ChartOrthographic extends ChartAzimuthalType {

	public double distance( double value, boolean inverse ) {
		double v, a ;

		if ( inverse ) {
			v = Math.acos( value ) ;
			return getNorthern()?v:-v ;
		} else {
			a = getNorthern()?value:-value ;
			return Math.cos( a ) ;
		}
	}
}
