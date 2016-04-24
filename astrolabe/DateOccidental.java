
package astrolabe;

import caa.CAADate;

public class DateOccidental extends Model implements Date {

	private CAADate date ;

	public DateOccidental( int[] date, double time ) {
		this.date = new CAADate( date[0], date[1], date[2]+time/24, DateHelper.isDateGregorian( date, time ) ) ;
	}

	public CAADate getDateOccidental() {
		return date ;
	}
}
