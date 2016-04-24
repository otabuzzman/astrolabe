
package astrolabe;

import caa.CAACoordinateTransformation;
import caa.CAADate;
import caa.CAAEquationOfTime;

public class QuantityDay implements Quantity {

	private Circle circle ;
	private Sun sun ;

	private double span ;
	private double unit ;

	private double jd0y ;
	private double jd0 ;
	private double jd0ra ;
	private boolean jd0ready ;

	private String[] llday = {
			ApplicationConstant.LL_SUNDAY, ApplicationConstant.LL_MONDAY, ApplicationConstant.LL_TUESDAY, ApplicationConstant.LL_WEDNESDAY,
			ApplicationConstant.LL_THURSDAY, ApplicationConstant.LL_FRIDAY, ApplicationConstant.LL_SATURDAY 
	} ;

	private String[] llmonth = {
			ApplicationConstant.LL_JANUARY, ApplicationConstant.LL_FEBRUARY, ApplicationConstant.LL_MARCH, ApplicationConstant.LL_APRIL,
			ApplicationConstant.LL_MAY, ApplicationConstant.LL_JUNE, ApplicationConstant.LL_JULY, ApplicationConstant.LL_AUGUST,
			ApplicationConstant.LL_SEPTEMBER, ApplicationConstant.LL_OCTOBER, ApplicationConstant.LL_NOVEMBER, ApplicationConstant.LL_DECEMBER
	} ;

	public QuantityDay( Circle circle, Sun sun ) throws ParameterNotValidException {
		long y ;
		boolean g ;

		if ( ! ( circle.isParallel() &&
				( (CircleParallel) circle).getHo().isEquatorial() ) ) {
			throw new ParameterNotValidException() ;
		}

		this.circle = circle ;
		this.sun = sun ;

		y = Astrolabe.getEpoch().Year() ;
		g = ApplicationHelper.isDateGregorian( y, 1, 1 ) ;
		jd0y = new CAADate( y, 1, 1, g ).Julian() ;

		unit = 1 ;
	}

	public boolean isSpanModN( double span, int n ) {
		boolean r ;
		double jd ;
		CAADate d ;
		boolean g ;

		jd = spanN2JD( n ) ;

		g = ApplicationHelper.isDateGregorian( jd ) ;
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

	public void register( int n ) {
		CAADate d ;
		boolean g ;
		String dn, dns, mn, mns, key ;
		double jd, eot ;

		try {
			jd = spanN2JD( n ) ;

			g = ApplicationHelper.isDateGregorian( jd ) ;
			d = new CAADate( jd, g ) ;

			dn = Astrolabe.getLocalizedString( ApplicationConstant.LN_CALENDAR_LONG+llday[ d.DayOfWeek() ] ) ;
			dns = Astrolabe.getLocalizedString( ApplicationConstant.LN_CALENDAR_SHORT+llday[ d.DayOfWeek() ] ) ;

			mn = Astrolabe.getLocalizedString( ApplicationConstant.LN_CALENDAR_LONG+llmonth[ -1+(int) d.Month() ] ) ;
			mns = Astrolabe.getLocalizedString( ApplicationConstant.LN_CALENDAR_SHORT+llmonth[ -1+(int) d.Month() ] ) ;

			eot = CAAEquationOfTime.Calculate( jd ) ;
			// Adjust in case of more than 20 minutes, convert to hours
			eot = ( eot>20?eot-24*60:eot )/60 ;
			eot = CAACoordinateTransformation.HoursToRadians( eot ) ;

			key = Astrolabe.getLocalizedString( ApplicationConstant.LK_DIAL_DAY ) ;
			ApplicationHelper.registerYMD( key, d ) ;

			key = Astrolabe.getLocalizedString( ApplicationConstant.LK_DIAL_JULIANDAY ) ;
			ApplicationHelper.registerNumber( key, d.Julian(), 2 ) ;

			key = Astrolabe.getLocalizedString( ApplicationConstant.LK_DIAL_NAMEOFDAY ) ;
			ApplicationHelper.registerName( key, dn ) ;
			key = Astrolabe.getLocalizedString( ApplicationConstant.LK_DIAL_NAMEOFDAYSHORT ) ;
			ApplicationHelper.registerName( key, dns ) ;

			key = Astrolabe.getLocalizedString( ApplicationConstant.LK_DIAL_NAMEOFMONTH ) ;
			ApplicationHelper.registerName( key, mn ) ;
			key = Astrolabe.getLocalizedString( ApplicationConstant.LK_DIAL_NAMEOFMONTHSHORT ) ;
			ApplicationHelper.registerName( key, mns ) ;

			key = Astrolabe.getLocalizedString( ApplicationConstant.LK_DIAL_EQUATIONOFTIME ) ;
			ApplicationHelper.registerMS( key, eot, 2, true ) ;
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
			sun.setJD( jd0 ) ;
			ra = sun.positionEq()[0] ;
		} else {
			jd = jd0y ;

			try {
				sun.setJD( jd ) ;
				eq = sun.positionEq() ;			
				circle.cartesian( eq[0], 0 ) ;
				// RA of jd > circle.begin
				try {
					double p ;

					p = CAACoordinateTransformation.DegreesToRadians( 360 ) ;

					// Decrease jd until RA < circle.begin
					for ( jd=jd+365 ; ; jd=jd-span ) {
						sun.setJD( jd ) ;
						eq = sun.positionEq() ;
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
						sun.setJD( jd ) ;
						eq = sun.positionEq() ;
						circle.cartesian( eq[0], 0 ) ;

						break ;
					} catch ( ParameterNotValidException ee ) {
						continue ;
					}
				}
			}

			sun.setJD( jd ) ;
			ra = sun.positionEq()[0] ;

			jd0 = jd ;
			jd0ra = ra ;
			jd0ready = true ;
		}

		return ra ;
	}

	public double spanNDistance( int n ) throws ParameterNotValidException {
		double ra ;
		double eq[] ;

		if ( jd0ready ) {
			sun.setJD( spanN2JD( n ) ) ;
			eq = sun.positionEq() ;

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
		g = ApplicationHelper.isDateGregorian( jd ) ;
		d = new CAADate( jd, g ) ;

		return d.Year()>Astrolabe.getEpoch().Year()?d.DayOfYear()+jd0y:jd ;
	}
}
