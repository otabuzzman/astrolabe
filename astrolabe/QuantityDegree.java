
package astrolabe;

import caa.CAACoordinateTransformation;

public class QuantityDegree implements Quantity {

	private Circle circle ;

	private double span ;
	private double unit ;

	public QuantityDegree( Circle circle ) {
		this.circle = circle ;

		unit = CAACoordinateTransformation.DegreesToRadians( 1 ) ;
	}

	public void register( int n ) {
		double d, rad12h ;
		String key ;

		try {
			d = circle.spanNDistance( span, n ) ;
			rad12h = CAACoordinateTransformation.HoursToRadians( 12 ) ;

			key = Astrolabe.getLocalizedString( ApplicationConstant.LK_DIAL_DEGREE ) ;
			ApplicationHelper.registerDMS( key, d, 2 ) ;
			key = Astrolabe.getLocalizedString( ApplicationConstant.LK_DIAL_HOUR ) ;
			ApplicationHelper.registerTime( key, d, 2 ) ;
			key = Astrolabe.getLocalizedString( ApplicationConstant.LK_DIAL_AZIMUTHTIME ) ;
			ApplicationHelper.registerTime( key, d+rad12h, 2 ) ;
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

	public void setUnit( double unit ) {
		this.unit = unit ;
	}
}
