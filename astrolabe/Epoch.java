
package astrolabe;

import java.util.Calendar;

import caa.CAADate;

@SuppressWarnings("serial")
public class Epoch extends astrolabe.model.Epoch {

	// registry key (RK_)
	public final static String RK_EPOCH = Epoch.class.getName() ;

	public Epoch() {
		set( defoult() ) ;
	}

	public Epoch( double epoch ) {
		set( epoch ) ;
	}

	public void set( double epoch ) {
		if ( getJD() == null )
			setJD( new astrolabe.model.JD() ) ;
		getJD().setValue( epoch ) ;
	}

	public static double defoult() {
		Double now ;
		Calendar calendar ;
		CAADate today ;

		calendar = Calendar.getInstance() ;
		today = new CAADate(
				calendar.get( Calendar.YEAR ),
				calendar.get( Calendar.MONTH ),
				calendar.get( Calendar.DAY_OF_MONTH ), 12, 0, 0, true ) ;
		now = new Double( today.Julian() ) ;
		today.delete() ;

		return now.doubleValue() ;
	}
}
