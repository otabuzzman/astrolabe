
package astrolabe;

import caa.CAADate;

public class DateOccidental extends Model implements Date {

	private CAADate date ;

	public DateOccidental( long[] date, double time ) {
		boolean g ;

		g = DateHelper.isDateGregorian( date ) ;
		this.date = new CAADate( date[0], date[1], date[2]+time/24, g ) ;
	}

	public CAADate getDateOccidental() {
		return date ;
	}
}
