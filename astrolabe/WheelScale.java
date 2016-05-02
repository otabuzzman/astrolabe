package astrolabe;

public class WheelScale extends LinearScale {

	private double wheelb ;
	private double wheele ;

	private double mod ;

	public WheelScale( double span, double[] range, double[] wheel ) {
		super( span, range ) ;

		wheelb = wheel[0] ;
		wheele = wheel[1] ;

		mod = wheele-wheelb ;
	}

	public double markN( int mark ) {
		double a ;

		a = super.markN( mark ) ;

		if ( wheele>wheelb ) {
			while( a>wheele )
				a = a-mod ;
			while( a<wheelb )
				a = a+mod ;
		} else {
			while( a<wheele )
				a = a+mod ;
			while( a>wheelb )
				a = a-mod ;
		}

		return a ;
	}
}
