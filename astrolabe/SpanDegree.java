
package astrolabe;

import caa.CAACoordinateTransformation;

public class SpanDegree implements Span {

	private Circle circle ;

	private double span ;
	private double unit ;

	public SpanDegree( Circle circle ) {
		this.circle = circle ;

		unit = CAACoordinateTransformation.DegreesToRadians( 1 ) ;
	}

	public void register( int n ) {
		double d, rad12h ;
		String key ;

		try {
			d = circle.distanceN( span, n ) ;
			rad12h = CAACoordinateTransformation.HoursToRadians( 12 ) ;

			key = Astrolabe.getLocalizedString( ApplicationConstant.LK_DIAL_DEGREE ) ;
			ApplicationHelper.registerDMS( key, d, 2 ) ;
			key = Astrolabe.getLocalizedString( ApplicationConstant.LK_DIAL_HOUR ) ;
			ApplicationHelper.registerTime( key, d, 2 ) ;
			key = Astrolabe.getLocalizedString( ApplicationConstant.LK_DIAL_AZIMUTHTIME ) ;
			ApplicationHelper.registerTime( key, d+rad12h, 2 ) ;
		} catch ( ParameterNotValidException  e ) {}
	}

	public void set( double span ) {
		this.span = span*unit ;
	}

	public double distance0() {
		return circle.distance0( span ) ;
	}

	public double distanceN( int n ) throws ParameterNotValidException {
		return circle.distanceN( span, n ) ;
	}

	public boolean isGraduationModN( double graduation, int n ) {
		boolean r ;

		try {
			r = Math.remainder( circle.distanceN( span, n ), graduation*unit )==0 ;
		} catch ( ParameterNotValidException e ) {
			r = false ;
		}

		return r ;
	}

	public void setUnit( double unit ) {
		this.unit = unit ;
	}
}
