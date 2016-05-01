
package astrolabe;

import java.text.MessageFormat;

import caa.CAACoordinateTransformation;
import caa.CAADate;
import caa.CAAEquationOfTime;

@SuppressWarnings("serial")
public class DialDay extends DialDegree {

	private Baseline baseline ;

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

	public DialDay( Object peer, Baseline baseline ) throws ParameterNotValidException {
		super( peer, baseline, 0 ) ;

		this.baseline = baseline ;
	}

	public double mapIndexToScale( int index, double span ) throws ParameterNotValidException {
		double r ;

		r = baseline.mapIndexToScale( index, span ) ;
		if ( ! baseline.probe( r ) ) {
			String msg ;

			msg = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
			msg = MessageFormat.format( msg, new Object[] { index+","+span, r } ) ;
			throw new ParameterNotValidException( msg ) ;
		}

		return r ;
	}

	public boolean isIndexAligned( int index, double span ) {
		CAADate d = new CAADate() ;
		boolean r ;
		double s, jd0, jd ;

		s = getGraduationSpan().getSpan() ;

		jd0 = baseline.mapIndexToScale( 0, s ) ;
		jd = baseline.mapIndexToScale( index, s ) ;

		if ( span==7 ) {
			d.Set( jd, true ) ;
			r = d.DayOfWeek()==1 ;
		} else if ( span==30 ) {
			d.Set( jd, true ) ;
			r = d.Day()==1 ;
		} else {
			r = Math.isLim0( ( jd-jd0 )/span-(int) ( ( jd-jd0 )/span ) ) ;
		}

		d.delete() ;

		return r ;
	}

	public void register( int index ) {
		CAADate d = new CAADate() ;
		String dn, dns, mn, mns, key ;
		double jd, eot ;

		try {
			jd = mapIndexToScale( index, getGraduationSpan().getSpan() ) ;

			d.Set( jd, true ) ;

			dn = ApplicationHelper.getLocalizedString( nameofday[ d.DayOfWeek() ] ) ;
			dns = ApplicationHelper.getLocalizedString( shortnameofday[ d.DayOfWeek() ] ) ;

			mn = ApplicationHelper.getLocalizedString( nameofmonth[ -1+(int) d.Month() ] ) ;
			mns = ApplicationHelper.getLocalizedString( shortnameofmonth[ -1+(int) d.Month() ] ) ;

			key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_DIAL_DAY ) ;
			ApplicationHelper.registerYMD( key, d ) ;

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

			eot = CAAEquationOfTime.Calculate( jd ) ;
			// convert to hours if greater than 20 minutes
			eot = ( eot>20?eot-24*60:eot )/60 ;
			eot = CAACoordinateTransformation.HoursToRadians( eot ) ;

			key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_DIAL_EQUATIONOFTIME ) ;
			ApplicationHelper.registerHMS( key, eot, 2 ) ;
		} catch ( ParameterNotValidException e ) {}

		d.delete() ;
	}
}
