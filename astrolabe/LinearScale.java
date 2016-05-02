
package astrolabe;

public class LinearScale {

	private double span ;

	private double rangeb ;
	private double rangee ;

	private double scalea ;
	private double scaleo ;

	public LinearScale( double span, double[] range ) {
		this.span = span ;

		rangeb = range[0] ;
		rangee = range[1] ;

		scalea = java.lang.Math.floor( rangeb/span )*span ;
		scaleo = java.lang.Math.floor( rangee/span )*span ;
	}

	public double markA() {
		return markN( 0 ) ;
	}

	public double markO() {
		return markN( -1 ) ;
	}

	public double markN( int mark ) {
		if ( 0>mark ) {
			if ( rangee>rangeb )
				return scaleo<rangee ? scaleo : rangee ;
			else
				return scaleo<rangee ? scaleo+span : rangee ;
		} else {
			if ( rangee>rangeb )
				return ( scalea<rangeb ? scalea+span : rangeb )+mark*span ;
			else
				return ( scalea<rangeb ? scalea : rangeb )-mark*span ;
		}
	}
}
