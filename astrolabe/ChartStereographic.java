
package astrolabe;

@SuppressWarnings("serial")
public class ChartStereographic extends ChartAzimuthalType {

	public double distance( double value, boolean inverse ) {
		double v, a ;

		if ( inverse ) {
			v = 90-Math.atan( value )*2 ;
			return getNorthern()?v:-v ;
		} else {
			a = getNorthern()?value:-value ;
			return Math.tan( ( 90-a )/2 ) ;
		}
	}
}
