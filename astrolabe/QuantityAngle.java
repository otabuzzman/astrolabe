
package astrolabe;

import caa.CAACoordinateTransformation;

public class QuantityAngle extends Model implements Quantity {

	private Circle circle ;

	private double span ;
	protected double unit ;

	public QuantityAngle( Circle circle ) {
		this.circle = circle ;

		unit = CAACoordinateTransformation.DegreesToRadians( 1 ) ;
	}

	public void register( String key, int n ) {
		double d ;

		try {
			d = circle.spanNDistance( span, n ) ;

			ReplacementHelper.registerDMS( key, d, 2 ) ;
			ReplacementHelper.registerHMS( key, d, 2 ) ;
		} catch ( ParameterNotValidException  e ) {}
	}

	public void setSpan( double span ) {
		this.span = span*unit ;
	}

	public double span0Distance() {
		return circle.span0Distance( span ) ;
	}

	public double spanNDistance( int n ) throws ParameterNotValidException {
		return circle.spanNDistance( span, n ) ;
	}

	public boolean isSpanModN( double span, int n ) {
		boolean r ;

		try {
			r = Math.remainder( circle.spanNDistance( this.span, n ), span*unit )==0 ;
		} catch ( ParameterNotValidException e ) {
			r = false ;
		}

		return r ;
	}
}
