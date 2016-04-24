
package astrolabe;

import caa.CAACoordinateTransformation;
import caa.CAADate;
import caa.CAAEquationOfTime;

public class QuantityDate implements Quantity {

	private Circle circle ;
	private Sun sun ;
	private CAADate epoch ;

	private double span ;
	private double unit ;

	private double jd0y ;
	private double jd0 ;
	private double jd0ra ;
	private boolean jd0ready ;

	private String[] dayprop ;
	private String[] monprop ;

	public QuantityDate( Circle circle, Sun sun, CAADate epoch ) throws ParameterNotValidException {
		long y ;
		boolean g ;

		if ( ! ( circle.isParallel() &&
				( (CircleParallel) circle).horizon.isEquatorial() ) ) {
			throw new ParameterNotValidException() ;
		}

		this.circle = circle ;
		this.sun = sun ;
		this.epoch = epoch ;

		y = epoch.Year() ;
		g = DateHelper.isDateGregorian( y, 1, 1 ) ;
		jd0y = new CAADate( y, 1, 1, g ).Julian() ;

		unit = 1 ;

		dayprop = new String[] {
				"sunday",
				"monday",
				"tuesday",
				"wednesday",
				"thursday",
				"friday",
				"saturday"
		} ;
		monprop = new String[] {
				"january",
				"february",
				"march",
				"april",
				"may",
				"june",
				"july",
				"august",
				"september",
				"october",
				"november",
				"december"
		} ;
	}

	public boolean isSpanModN( double span, int n ) {
		boolean r ;
		double jd ;
		CAADate d ;
		boolean g ;

		jd = spanN2JD( n ) ;

		g = DateHelper.isDateGregorian( jd ) ;
		d = new CAADate( jd, g ) ;

		if ( span == 7 ) {
			r = d.DayOfWeek()==1 ;
		} else if ( span == 30 ) {
			r = d.Day()==1 ;
		} else {
			r = Math.remainder( jd-jd0, span*unit )==0 ;
		}

		return r ;
	}

	public void register( String key, int n ) {
		CAADate d ;
		boolean g ;
		String dn, mn ;
		double jd, eot ;

		try {
			jd = spanN2JD( n ) ;

			g = DateHelper.isDateGregorian( jd ) ;
			d = new CAADate( jd, g ) ;

			dn = Messages.getString( "calendar."+dayprop[ d.DayOfWeek() ] ) ;
			mn = Messages.getString( "calendar."+monprop[ -1+(int) d.Month() ] ) ;

			eot = CAAEquationOfTime.Calculate( jd ) ;
			// Adjust in case of more than 20 minutes, convert to hours
			eot = ( eot>20?eot-24*60:eot )/60 ;
			eot = CAACoordinateTransformation.HoursToRadians( eot ) ;

			ReplacementHelper.registerYMD( key, d ) ;
			ReplacementHelper.registerNumber( key+"JD", d.Julian(), 2 ) ;
			ReplacementHelper.registerName( key+"DNAM", dn ) ;
			ReplacementHelper.registerName( key+"MNAM", mn ) ;
			ReplacementHelper.registerMS( key+"eot", eot, 2 ) ;
		} catch ( ParameterNotValidException e ) {}
	}

	public void setSpan( double span ) {
		this.span = span*unit ;
		this.jd0ready = false ;
	}

	public double span0Distance() {
		double ra ;
		double jd, eq[] ;

		if ( jd0ready ) {
			ra = sun.positionEq( jd0 )[0] ;
		} else {
			jd = jd0y ;

			try {
				eq = sun.positionEq( jd ) ;			
				circle.cartesian( eq[0], 0 ) ;
				// RA of jd > circle.begin
				try {
					double p ;

					p = CAACoordinateTransformation.DegreesToRadians( 360 ) ;

					// Decrease jd until RA < circle.begin
					for ( jd=jd+365 ; ; jd=jd-span ) {
						eq = sun.positionEq( jd ) ;
						circle.cartesian( eq[0], 0 ) ;
						if ( eq[0] > p ) {
							throw new ParameterNotValidException() ;
						} else {
							p = eq[0] ;
						}
					}
				} catch ( ParameterNotValidException e ) {
					// Adjust jd to be 1st > circle.begin
					jd = jd+span ;
				}
			} catch ( ParameterNotValidException e ) {
				// RA of jd < circle.begin

				// Increase jd until RA > circle.begin thus jd 1st > circle.begin
				for ( jd=jd+span ; ; jd=jd+span ) {
					try {
						eq = sun.positionEq( jd ) ;
						circle.cartesian( eq[0], 0 ) ;

						break ;
					} catch ( ParameterNotValidException ee ) {
						continue ;
					}
				}
			}

			ra = sun.positionEq( jd )[0] ;

			jd0 = jd ;
			jd0ra = ra ;
			jd0ready = true ;
		}

		return ra ;
	}

	public double spanNDistance( int n ) throws ParameterNotValidException {
		double ra ;
		double jd, eq[] ;

		if ( jd0ready ) {
			jd = spanN2JD( n ) ;
			eq = sun.positionEq( jd ) ;

			circle.cartesian( eq[0], 0 ) ;
			if ( n > 0 && eq[0] == jd0ra ) {
				throw new ParameterNotValidException() ;
			}

			ra = eq[0] ;
		} else {
			span0Distance() ;
			ra = spanNDistance( n ) ;
		}

		return ra ;
	}

	private double spanN2JD( int n ) {
		CAADate d ;
		boolean g ;
		double jd ;

		jd = jd0+n*span ;
		g = DateHelper.isDateGregorian( jd ) ;
		d = new CAADate( jd, g ) ;

		return d.Year()>epoch.Year()?d.DayOfYear()+jd0y:jd ;
	}
}
