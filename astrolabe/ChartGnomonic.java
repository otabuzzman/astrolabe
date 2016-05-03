
package astrolabe;

@SuppressWarnings("serial")
public class ChartGnomonic extends ChartAzimuthalType {

	public double distance( double value, boolean inverse ) {
		double v, a ;

		if ( inverse ) {
			v = 90-Math.atan( value ) ;
			return getNorthern()?v:-v ;
		} else {
			a = getNorthern()?value:-value ;
			return Math.tan( 90-a ) ;
		}
	}
}
