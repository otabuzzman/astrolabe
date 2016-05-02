package astrolabe;

public class Wheel360Scale extends WheelScale {

	public Wheel360Scale( double span, double[] range ) {
		super( span, range, new double[] { 0, 360 } ) ;
	}
}
