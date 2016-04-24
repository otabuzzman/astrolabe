
package astrolabe;

import caa.CAACoordinateTransformation;
import caa.CAADate;
import caa.CAAEquationOfTime;

public class SpanDay implements Span {

	private Circle circle ;
	private Sun sun ;

	private double span ;
	private double unit ;

	private double jdAy ;
	private double jdOy ;
	private double jd0 ;
	private boolean jd0ready ;

	private double raJD0 ;

	private String[] llday = {
			ApplicationConstant.LL_SUNDAY, ApplicationConstant.LL_MONDAY, ApplicationConstant.LL_TUESDAY, ApplicationConstant.LL_WEDNESDAY,
			ApplicationConstant.LL_THURSDAY, ApplicationConstant.LL_FRIDAY, ApplicationConstant.LL_SATURDAY 
	} ;

	private String[] llmonth = {
			ApplicationConstant.LL_JANUARY, ApplicationConstant.LL_FEBRUARY, ApplicationConstant.LL_MARCH, ApplicationConstant.LL_APRIL,
			ApplicationConstant.LL_MAY, ApplicationConstant.LL_JUNE, ApplicationConstant.LL_JULY, ApplicationConstant.LL_AUGUST,
			ApplicationConstant.LL_SEPTEMBER, ApplicationConstant.LL_OCTOBER, ApplicationConstant.LL_NOVEMBER, ApplicationConstant.LL_DECEMBER
	} ;

	public SpanDay( Circle circle, Sun sun ) throws ParameterNotValidException {
		long y ;
		double JD ;
		CAADate e, cdA, cdO ;

		if ( ! circle.dotDot()/*Horizon*/.isEquatorial() || ! circle.isParallel() ) {
			throw new ParameterNotValidException() ;
		}

		this.circle = circle ;
		this.sun = sun ;

		JD = circle.dotDot()/*Horizon*/.dotDot()/*Chart*/.dotDot()/*Astrolabe*/.getEpoch() ;
		e = new CAADate( JD, true ) ;
		y =  e.Year() ;

		e.delete() ;

		cdA = new CAADate( y, 1, 1, true ) ;
		jdAy = cdA.Julian() ;
		cdO = new CAADate( y+1, 1, 1, true ) ;
		jdOy = cdO.Julian() ;

		cdA.delete() ;
		cdO.delete() ;

		unit = 1 ;
	}

	public boolean isGraduationModN( double graduation, int n ) {
		boolean r ;
		double jd ;
		CAADate d ;

		jd = convertN2JD( n ) ;

		d = new CAADate( jd, true ) ;

		if ( graduation==7 ) {
			r = d.DayOfWeek()==1 ;
		} else if ( graduation==30 ) {
			r = d.Day()==1 ;
		} else {
			r = Math.remainder( jd-jd0, graduation*unit )==0 ;
		}

		d.delete() ;

		return r ;
	}

	public void register( int n ) {
		CAADate d = null ;
		String dn, dns, mn, mns, key ;
		double jd, eot ;

		try {
			jd = convertN2JD( n ) ;

			d = new CAADate( jd, true ) ;

			dn = Astrolabe.getLocalizedString( ApplicationConstant.LN_CALENDAR_LONG+llday[ d.DayOfWeek() ] ) ;
			dns = Astrolabe.getLocalizedString( ApplicationConstant.LN_CALENDAR_SHORT+llday[ d.DayOfWeek() ] ) ;

			mn = Astrolabe.getLocalizedString( ApplicationConstant.LN_CALENDAR_LONG+llmonth[ -1+(int) d.Month() ] ) ;
			mns = Astrolabe.getLocalizedString( ApplicationConstant.LN_CALENDAR_SHORT+llmonth[ -1+(int) d.Month() ] ) ;

			eot = CAAEquationOfTime.Calculate( jd ) ;
			// convert to hours if greater than 20 minutes
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
		} catch ( ParameterNotValidException e ) {
		} finally {
			d.delete() ;
		}
	}

	public void set( double span ) {
		this.span = span*unit ;
		this.jd0ready = false ;
	}

	public double distance0() {
		double jd, ra ;

		if ( jd0ready ) {
			raJD0 = sun.positionEq( jd0 )[0] ;
		} else {
			jd = jdAy ;

			try {
				ra = sun.positionEq( jd )[0] ;			
				circle.cartesian( ra, 0 ) ;
				// RA of jd > circle.begin
				try {
					double p ;

					p = CAACoordinateTransformation.DegreesToRadians( 360 ) ;

					// Decrease jd until RA < circle.begin
					for ( jd=jd+365 ; ; jd=jd-span ) {
						ra = sun.positionEq( jd )[0] ;
						circle.cartesian( ra, 0 ) ;
						if ( ra > p ) {
							throw new ParameterNotValidException() ;
						} else {
							p = ra ;
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
						ra = sun.positionEq( jd )[0] ;
						circle.cartesian( ra, 0 ) ;

						break ;
					} catch ( ParameterNotValidException ee ) {
						continue ;
					}
				}
			}

			raJD0 = sun.positionEq( jd )[0] ;

			jd0 = jd ;
			jd0ready = true ;
		}

		return raJD0 ;
	}

	public double distanceN( int n ) throws ParameterNotValidException {
		double jd, ra ;
		double eq[] ;

		if ( jd0ready ) {
			jd = convertN2JD( n ) ;
			eq = sun.positionEq( jd ) ;

			circle.cartesian( eq[0], 0 ) ;
			if ( n>0&&eq[0]==raJD0 ) {
				throw new ParameterNotValidException() ;
			}

			ra = eq[0] ;
		} else {
			distance0() ;
			ra = distanceN( n ) ;
		}

		return ra ;
	}

	private double convertN2JD( int n ) {
		double jd ;

		jd = jd0+n*span ;

		return jd>=jdOy?jdAy+( jd-jdOy ):jd ;
	}
}
