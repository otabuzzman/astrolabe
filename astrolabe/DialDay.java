
package astrolabe;

import caa.CAACoordinateTransformation;
import caa.CAADate;
import caa.CAAEquationOfTime;

@SuppressWarnings("serial")
public class DialDay extends DialDegree {

	private String[] nameofday = {
			ApplicationConstant.LK_CALENDAR_GREGORIAN_WEEKDAY_SUNDAY,
			ApplicationConstant.LK_CALENDAR_GREGORIAN_WEEKDAY_MONDAY,
			ApplicationConstant.LK_CALENDAR_GREGORIAN_WEEKDAY_TUESDAY,
			ApplicationConstant.LK_CALENDAR_GREGORIAN_WEEKDAY_WEDNESDAY,
			ApplicationConstant.LK_CALENDAR_GREGORIAN_WEEKDAY_THURSDAY,
			ApplicationConstant.LK_CALENDAR_GREGORIAN_WEEKDAY_FRIDAY,
			ApplicationConstant.LK_CALENDAR_GREGORIAN_WEEKDAY_SATURDAY 
	} ;
	private String[] shortnameofday = {
			ApplicationConstant.LK_CALENDAR_GREGORIAN_WEEKDAY_SHORT_SUNDAY,
			ApplicationConstant.LK_CALENDAR_GREGORIAN_WEEKDAY_SHORT_MONDAY,
			ApplicationConstant.LK_CALENDAR_GREGORIAN_WEEKDAY_SHORT_TUESDAY,
			ApplicationConstant.LK_CALENDAR_GREGORIAN_WEEKDAY_SHORT_WEDNESDAY,
			ApplicationConstant.LK_CALENDAR_GREGORIAN_WEEKDAY_SHORT_THURSDAY,
			ApplicationConstant.LK_CALENDAR_GREGORIAN_WEEKDAY_SHORT_FRIDAY,
			ApplicationConstant.LK_CALENDAR_GREGORIAN_WEEKDAY_SHORT_SATURDAY 
	} ;
	private String[] nameofmonth = {
			ApplicationConstant.LK_CALENDAR_GREGORIAN_MONTH_JANUARY,
			ApplicationConstant.LK_CALENDAR_GREGORIAN_MONTH_FEBRUARY,
			ApplicationConstant.LK_CALENDAR_GREGORIAN_MONTH_MARCH,
			ApplicationConstant.LK_CALENDAR_GREGORIAN_MONTH_APRIL,
			ApplicationConstant.LK_CALENDAR_GREGORIAN_MONTH_MAY,
			ApplicationConstant.LK_CALENDAR_GREGORIAN_MONTH_JUNE,
			ApplicationConstant.LK_CALENDAR_GREGORIAN_MONTH_JULY,
			ApplicationConstant.LK_CALENDAR_GREGORIAN_MONTH_AUGUST,
			ApplicationConstant.LK_CALENDAR_GREGORIAN_MONTH_SEPTEMBER,
			ApplicationConstant.LK_CALENDAR_GREGORIAN_MONTH_OCTOBER,
			ApplicationConstant.LK_CALENDAR_GREGORIAN_MONTH_NOVEMBER,
			ApplicationConstant.LK_CALENDAR_GREGORIAN_MONTH_DECEMBER
	} ;
	private String[] shortnameofmonth = {
			ApplicationConstant.LK_CALENDAR_GREGORIAN_MONTH_SHORT_JANUARY,
			ApplicationConstant.LK_CALENDAR_GREGORIAN_MONTH_SHORT_FEBRUARY,
			ApplicationConstant.LK_CALENDAR_GREGORIAN_MONTH_SHORT_MARCH,
			ApplicationConstant.LK_CALENDAR_GREGORIAN_MONTH_SHORT_APRIL,
			ApplicationConstant.LK_CALENDAR_GREGORIAN_MONTH_SHORT_MAY,
			ApplicationConstant.LK_CALENDAR_GREGORIAN_MONTH_SHORT_JUNE,
			ApplicationConstant.LK_CALENDAR_GREGORIAN_MONTH_SHORT_JULY,
			ApplicationConstant.LK_CALENDAR_GREGORIAN_MONTH_SHORT_AUGUST,
			ApplicationConstant.LK_CALENDAR_GREGORIAN_MONTH_SHORT_SEPTEMBER,
			ApplicationConstant.LK_CALENDAR_GREGORIAN_MONTH_SHORT_OCTOBER,
			ApplicationConstant.LK_CALENDAR_GREGORIAN_MONTH_SHORT_NOVEMBER,
			ApplicationConstant.LK_CALENDAR_GREGORIAN_MONTH_SHORT_DECEMBER
	} ;

	private Baseline baseline ;

	public DialDay( Baseline baseline ) {
		super( baseline, 1 ) ;

		this.baseline = baseline ;
	}

	public void register( int index ) {
		MessageCatalog m ;
		CAADate d = new CAADate() ;
		String dn, dns, mn, mns, key ;
		double jd, eot ;

		m = new MessageCatalog( ApplicationConstant.GC_APPLICATION ) ;

		jd = baseline.scaleMarkNth( index, getGraduationSpan().getValue() ) ;

		d.Set( jd, true ) ;

		dn = m.message( nameofday[ d.DayOfWeek() ] ) ;
		dns = m.message( shortnameofday[ d.DayOfWeek() ] ) ;

		mn = m.message( nameofmonth[ -1+(int) d.Month() ] ) ;
		mns = m.message( shortnameofmonth[ -1+(int) d.Month() ] ) ;

		key = m.message( ApplicationConstant.LK_DIAL_DAY ) ;
		AstrolabeRegistry.registerYMD( key, d ) ;

		key = m.message( ApplicationConstant.LK_DIAL_JULIANDAY ) ;
		AstrolabeRegistry.registerNumber( key, jd, 2 ) ;

		key = m.message( ApplicationConstant.LK_DIAL_NAMEOFDAY ) ;
		AstrolabeRegistry.registerName( key, dn ) ;
		key = m.message( ApplicationConstant.LK_DIAL_NAMEOFDAYSHORT ) ;
		AstrolabeRegistry.registerName( key, dns ) ;

		key = m.message( ApplicationConstant.LK_DIAL_NAMEOFMONTH ) ;
		AstrolabeRegistry.registerName( key, mn ) ;
		key = m.message( ApplicationConstant.LK_DIAL_NAMEOFMONTHSHORT ) ;
		AstrolabeRegistry.registerName( key, mns ) ;

		eot = CAAEquationOfTime.Calculate( jd ) ;
		// convert to hours if greater than 20 minutes
		eot = ( eot>20?eot-24*60:eot )/60 ;
		eot = CAACoordinateTransformation.HoursToDegrees( eot ) ;

		key = m.message( ApplicationConstant.LK_DIAL_EQUATIONOFTIME ) ;
		AstrolabeRegistry.registerHMS( key, eot ) ;

		d.delete() ;
	}

	public boolean isMultipleSpan( double mark, double span ) {
		boolean r ;
		CAADate d ;
		double jd0 ;

		jd0 = baseline.scaleMarkNth( 0, 1 ) ;

		d = new CAADate() ;

		if ( span==7 ) {
			d.Set( mark, true ) ;
			r = d.DayOfWeek()==1 ;
		} else if ( span==30 ) {
			d.Set( mark, true ) ;
			r = d.Day()==1 ;
		} else {
			r = Math.isLim0( ( mark-jd0 )/span-(int) ( ( mark-jd0 )/span ) ) ;
		}

		d.delete() ;

		return r ;
	}
}
