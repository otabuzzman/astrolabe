
package astrolabe;

import caa.CAADate;

public class Calendar {

	private CAADate date ;

	public Calendar( long[] date, double time ) {
		boolean g ;

		g = ApplicationHelper.isDateGregorian( date ) ;
		this.date = new CAADate( date[0], date[1], date[2]+time/24, g ) ;
	}

	public CAADate getDate() {
		return date ;
	}
}
