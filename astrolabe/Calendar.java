
package astrolabe;

import caa.CAADate;

public class Calendar {

	private CAADate date ;

	public Calendar( long[] date, double time ) {
		this.date = new CAADate( date[0], date[1], date[2]+time/24, true ) ;
	}

	public CAADate getDate() {
		return date ;
	}

	public void finalize() {
		date.delete() ;
	}
}
