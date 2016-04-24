
package astrolabe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import caa.CAACoordinateTransformation;
import caa.CAADate;
import caa.CAAEquationOfTime;

@SuppressWarnings("serial")
public class DialDay extends DialDegree {

	private Circle circle ;

	private Method sunEclipticLongitude ;
	private Method sunEclipticLatitude ;

	private double epoch ;

	private double jdAy ;
	private double jdOy ;
	private double jd0 ;

	// cache
	private double span ;

	private String[] llday = {
			ApplicationConstant.LL_SUNDAY, ApplicationConstant.LL_MONDAY, ApplicationConstant.LL_TUESDAY, ApplicationConstant.LL_WEDNESDAY,
			ApplicationConstant.LL_THURSDAY, ApplicationConstant.LL_FRIDAY, ApplicationConstant.LL_SATURDAY 
	} ;
	private String[] llmonth = {
			ApplicationConstant.LL_JANUARY, ApplicationConstant.LL_FEBRUARY, ApplicationConstant.LL_MARCH, ApplicationConstant.LL_APRIL,
			ApplicationConstant.LL_MAY, ApplicationConstant.LL_JUNE, ApplicationConstant.LL_JULY, ApplicationConstant.LL_AUGUST,
			ApplicationConstant.LL_SEPTEMBER, ApplicationConstant.LL_OCTOBER, ApplicationConstant.LL_NOVEMBER, ApplicationConstant.LL_DECEMBER
	} ;

	public DialDay( Object peer, double epoch, Circle circle ) {
		super( peer, circle, 0 ) ;

		long y ;
		double jdOy, raAy, raOy ;
		CAADate d, cdA, cdO ;

		this.circle = circle ;
		this.epoch = epoch ;

		try {
			Class c ;

			c = Class.forName( "astrolabe.ApplicationHelper" ) ;

			sunEclipticLongitude = c.getMethod(
					// sun attribut of DialDay element extends DialType.
					( (astrolabe.model.DialDay) peer ).getSun()+"EclipticLongitude", new Class[] { double.class } ) ;
			sunEclipticLatitude = c.getMethod(
					( (astrolabe.model.DialDay) peer ).getSun()+"EclipticLatitude", new Class[] { double.class } ) ;
		} catch ( ClassNotFoundException e ) {
		} catch ( NoSuchMethodException e ) {
		}

		d = new CAADate( epoch, true ) ;
		y =  d.Year() ;
		d.delete() ;

		cdA = new CAADate( y, 1, 1, true ) ;
		jdAy = cdA.Julian() ;
		raAy = sunPositionEquatorial( sunPositionEcliptical( jdAy ), epoch )[0] ;
		cdA.delete() ;

		cdO = new CAADate( y+1, 1, 1, true ) ;
		jdOy = cdO.Julian() ;
		raOy = sunPositionEquatorial( sunPositionEcliptical( jdOy ), epoch )[0] ;
		cdO.delete() ;
		while( raOy>raAy ) {
			jdOy = jdOy-1./*unit*//getGraduationSpan().getDivision() ;
			raOy = sunPositionEquatorial( sunPositionEcliptical( jdOy ), epoch )[0] ;
		}
		this.jdOy = jdOy ;
	}

	public double mapIndexToAngleOfScale( int index, double span ) throws ParameterNotValidException {
		double r ;
		double jd, ra, ra0 ;

		if ( span != this.span ) {
			// dirty cache
			this.span = span ;

			jd0 = mapIndex0ToJD( span ) ;
		}

		jd = mapIndexNToJD( index, span ) ;
		ra = sunPositionEquatorial( sunPositionEcliptical( jd ), epoch )[0] ;
		if ( ! circle.probe( ra ) ) {
			throw new ParameterNotValidException() ;
		}

		ra0 = sunPositionEquatorial( sunPositionEcliptical( jd0 ), epoch )[0] ;
		if ( index>0&&ra==ra0 ) {
			throw new ParameterNotValidException() ;
		}

		r = ra ;

		return r ;
	}

	public boolean isIndexAligned( int index, double span ) {
		boolean r ;
		double jd ;
		CAADate d ;

		jd = mapIndexNToJD( index, getGraduationSpan().getSpan() ) ;

		if ( span==7 ) {
			d = new CAADate( jd, true ) ;
			r = d.DayOfWeek()==1 ;
			d.delete() ;
		} else if ( span==30 ) {
			d = new CAADate( jd, true ) ;
			r = d.Day()==1 ;
			d.delete() ;
		} else {
			double a, b ;

			a = jd-jd0 ;
			b = span/* *unit */ ;

			r = Math.isLim0( a-(int) ( a/b )*b ) ;
		}

		return r ;
	}

	public void register( int index ) {
		CAADate d = null ;
		String dn, dns, mn, mns, key ;
		double jd, eot ;

		try {
			jd = mapIndexNToJD( index, getGraduationSpan().getSpan() ) ;

			d = new CAADate( jd, true ) ;

			dn = ApplicationHelper.getLocalizedString( ApplicationConstant.LN_CALENDAR_LONG+llday[ d.DayOfWeek() ] ) ;
			dns = ApplicationHelper.getLocalizedString( ApplicationConstant.LN_CALENDAR_SHORT+llday[ d.DayOfWeek() ] ) ;

			mn = ApplicationHelper.getLocalizedString( ApplicationConstant.LN_CALENDAR_LONG+llmonth[ -1+(int) d.Month() ] ) ;
			mns = ApplicationHelper.getLocalizedString( ApplicationConstant.LN_CALENDAR_SHORT+llmonth[ -1+(int) d.Month() ] ) ;

			eot = CAAEquationOfTime.Calculate( jd ) ;
			// convert to hours if greater than 20 minutes
			eot = ( eot>20?eot-24*60:eot )/60 ;
			eot = CAACoordinateTransformation.HoursToRadians( eot ) ;

			key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_DIAL_DAY ) ;
			ApplicationHelper.registerYMD( key, d ) ;

			d.delete() ;

			key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_DIAL_JULIANDAY ) ;
			ApplicationHelper.registerNumber( key, jd, 2 ) ;

			key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_DIAL_NAMEOFDAY ) ;
			ApplicationHelper.registerName( key, dn ) ;
			key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_DIAL_NAMEOFDAYSHORT ) ;
			ApplicationHelper.registerName( key, dns ) ;

			key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_DIAL_NAMEOFMONTH ) ;
			ApplicationHelper.registerName( key, mn ) ;
			key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_DIAL_NAMEOFMONTHSHORT ) ;
			ApplicationHelper.registerName( key, mns ) ;

			key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_DIAL_EQUATIONOFTIME ) ;
			ApplicationHelper.registerMS( key, eot, 2, true ) ;
		} catch ( ParameterNotValidException e ) {}
	}

	private double mapIndex0ToJD( double span ) {
		double r ;
		double jd, ra ;

		jd = jdAy ;

		try {
			ra = sunPositionEquatorial( sunPositionEcliptical( jd ), epoch )[0] ;
			if ( ! circle.probe( ra ) ) {
				throw new ParameterNotValidException() ;
			}
			// RA of jd > circle.begin
			try {
				double p ;

				p = Math.rad360 ;

				// Decrease jd until RA < circle.begin
				for ( jd=jd+365 ; ; jd=jd-span/* *unit */ ) {
					ra = sunPositionEquatorial( sunPositionEcliptical( jd ), epoch )[0] ;
					if ( ! circle.probe( ra ) ) {
						throw new ParameterNotValidException() ;
					}
					if ( ra > p ) {
						throw new ParameterNotValidException() ;
					} else {
						p = ra ;
					}
				}
			} catch ( ParameterNotValidException e ) {
				// Adjust jd to be 1st > circle.begin
				jd = jd+span/* *unit */ ;
			}
		} catch ( ParameterNotValidException e ) {
			// RA of jd < circle.begin

			// Increase jd until RA > circle.begin thus jd 1st > circle.begin
			for ( jd=jd+span/* *unit */ ; ; jd=jd+span/* *unit */ ) {
				try {
					ra = sunPositionEquatorial( sunPositionEcliptical( jd ), epoch )[0] ;
					if ( ! circle.probe( ra ) ) {
						throw new ParameterNotValidException() ;
					}

					break ;
				} catch ( ParameterNotValidException ee ) {
					continue ;
				}
			}
		}

		r = jd ;

		return r ;
	}

	private double mapIndexNToJD( int index, double span ) {
		double jd ;

		jd = jd0+index*span/* *unit */ ;

		return jd>jdOy?jdAy+( jd-span/* *unit */-jdOy ):jd ;
	}

	public double[] sunPositionEcliptical( double jd ) {
		double[] r = new double[2] ;
		double lo, la ;

		lo = 0 ;
		la = 0 ;

		try {
			lo = (Double) sunEclipticLongitude.invoke( null, new Object[] { new Double( jd ) } ) ;
			la = (Double) sunEclipticLatitude.invoke( null, new Object[] { new Double( jd ) } ) ;
		} catch ( InvocationTargetException e ) {
		} catch ( IllegalAccessException e ) {
		}

		r[0] = lo ;
		r[1] = la ;

		return r ;
	}

	private static double[] sunPositionEquatorial( double[] ec, double epoch ) {
		return sunPositionEquatorial( ec[0], ec[1], epoch ) ;
	}

	private static double[] sunPositionEquatorial( double lo, double la, double epoch ) {
		double[] r ;
		double e, eq[] ;

		e = ApplicationHelper.meanObliquityOfEcliptic( epoch ) ;

		eq = ApplicationHelper.ecliptic2Equatorial( lo, la, e ) ;
		r = eq ;

		return r ;
	}
}
