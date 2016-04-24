
package astrolabe;

import caa.CAACoordinateTransformation;
import caa.CAADate;
import caa.CAAEquationOfTime;

@SuppressWarnings("serial")
public class DialDay extends DialDegree {

	private Baseline baseline ;

	private String[] llday = {
			ApplicationConstant.LL_SUNDAY, ApplicationConstant.LL_MONDAY, ApplicationConstant.LL_TUESDAY, ApplicationConstant.LL_WEDNESDAY,
			ApplicationConstant.LL_THURSDAY, ApplicationConstant.LL_FRIDAY, ApplicationConstant.LL_SATURDAY 
	} ;
	private String[] llmonth = {
			ApplicationConstant.LL_JANUARY, ApplicationConstant.LL_FEBRUARY, ApplicationConstant.LL_MARCH, ApplicationConstant.LL_APRIL,
			ApplicationConstant.LL_MAY, ApplicationConstant.LL_JUNE, ApplicationConstant.LL_JULY, ApplicationConstant.LL_AUGUST,
			ApplicationConstant.LL_SEPTEMBER, ApplicationConstant.LL_OCTOBER, ApplicationConstant.LL_NOVEMBER, ApplicationConstant.LL_DECEMBER
	} ;

	public DialDay( Object peer, Baseline baseline ) {
		super( peer, baseline, 0 ) ;

		this.baseline = baseline ;
	}

	public double mapIndexToScale( int index, double span ) throws ParameterNotValidException {
		double r ;

		r = baseline.mapIndexToScale( index, span ) ;
		if ( ! baseline.probe( r ) ) {
			throw new ParameterNotValidException() ;
		}

		return r ;
	}

	public boolean isIndexAligned( int index, double span ) {
		CAADate d = new CAADate() ;
		boolean r ;
		double jd ;

		jd = baseline.mapIndexToScale( index, getGraduationSpan().getSpan() ) ;

		if ( span==7 ) {
			d.Set( jd, true ) ;
			r = d.DayOfWeek()==1 ;
		} else if ( span==30 ) {
			d.Set( jd, true ) ;
			r = d.Day()==1 ;
		} else {
			r = Math.isLim0( jd-(int) ( jd/span )*span ) ;
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

			dn = ApplicationHelper.getLocalizedString( ApplicationConstant.LN_CALENDAR_LONG+llday[ d.DayOfWeek() ] ) ;
			dns = ApplicationHelper.getLocalizedString( ApplicationConstant.LN_CALENDAR_SHORT+llday[ d.DayOfWeek() ] ) ;

			mn = ApplicationHelper.getLocalizedString( ApplicationConstant.LN_CALENDAR_LONG+llmonth[ -1+(int) d.Month() ] ) ;
			mns = ApplicationHelper.getLocalizedString( ApplicationConstant.LN_CALENDAR_SHORT+llmonth[ -1+(int) d.Month() ] ) ;

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
			ApplicationHelper.registerMS( key, eot, 2, true ) ;
		} catch ( ParameterNotValidException e ) {}

		d.delete() ;
	}
}
