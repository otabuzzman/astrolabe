
package astrolabe;

import java.util.Calendar;

import caa.CAADate;

@SuppressWarnings("serial")
public class Epoch extends astrolabe.model.Epoch {

	private double alpha = Double.NEGATIVE_INFINITY ;
	private double omega = Double.NEGATIVE_INFINITY ;

	public double alpha() {
		int y ;
		CAADate a ;

		if ( alpha>Double.NEGATIVE_INFINITY )
			return alpha ;

		if ( getCalendar() != null )
			return alpha = valueOf( getCalendar() ) ;
		if ( getJD() != null )
			return alpha = valueOf( getJD() ) ;

		y = Calendar.getInstance().get( Calendar.YEAR ) ;
		a = new CAADate( y, 1, 1, 12, 0, 0, true ) ;
		alpha = a.Julian() ;
		a.delete() ;

		setJD( new astrolabe.model.JD() ) ;
		getJD().setValue( alpha ) ;

		return alpha ;
	}

	public double omega() {
		double alpha ;
		CAADate t, a ;

		if ( omega>Double.NEGATIVE_INFINITY )
			return omega ;

		if ( getOmegaDay() != null ) {
			if ( getOmegaDay().getCalendar() != null )
				return omega = valueOf( getOmegaDay().getCalendar() ) ;
			return omega = valueOf( getOmegaDay().getJD() ) ;
		}

		alpha = alpha() ;
		t = new CAADate( alpha, true ) ;
		a = new CAADate(
				t.Year()+1, t.Month(), t.Day(),
				t.Hour(), t.Minute(), t.Second(), true ) ;
		omega = a.Julian() ;
		t.delete() ;
		a.delete() ;

		return omega ;
	}
}
