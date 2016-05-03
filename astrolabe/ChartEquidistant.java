
package astrolabe;

@SuppressWarnings("serial")
public class ChartEquidistant extends ChartAzimuthalType {

	public double distance( double value, boolean inverse ) {
		double v, a ;

		if ( inverse ) {
			v = 90-value*90 ;
			return getNorthern()?v:-v ;
		} else {
			a = getNorthern()?value:-value ;
			return ( 90-a )/90 ;
		}
	}
}
