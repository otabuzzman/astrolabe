
package astrolabe;

import java.util.Calendar;

import caa.CAADate;

@SuppressWarnings("serial")
public class Epoch extends astrolabe.model.Epoch {

	// registry key (RK_)
	public final static String RK_EPOCH = Epoch.class.getName() ;

	public static double retrieve() {
		Double epoch ;
		Calendar calendar ;
		CAADate today ;

		epoch = (Double) Registry.retrieve( RK_EPOCH ) ;
		if ( epoch == null ) {
			calendar = Calendar.getInstance() ;
			today = new CAADate(
					calendar.get( Calendar.YEAR ),
					calendar.get( Calendar.MONTH ),
					calendar.get( Calendar.DAY_OF_MONTH ), 12, 0, 0, true ) ;
			epoch = new Double( today.Julian() ) ;
			Registry.register( RK_EPOCH, epoch ) ;
			today.delete() ;
		}

		return epoch.doubleValue() ;
	}
}
