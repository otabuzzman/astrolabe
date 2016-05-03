
package astrolabe;

import caa.CAAEquationOfTime;

@SuppressWarnings("serial")
public class DialDay extends DialDeg {

	// qualifier key (QK_)
	private final static String QK_DAY				= "day" ;
	private final static String QK_EQUATIONOFTIME	= "equationoftime" ;

	public DialDay( Baseline baseline ) {
		super( baseline ) ;
	}

	protected void register( double jd ) {
		String sub ;
		CAADate date ;
		DMS hms ;
		double eot ;

		sub = SubstituteCatalog.substitute( this, QK_DAY, QK_DAY ) ;
		Registry.register( sub, (long) ( jd*100+.5 )/100 ) ;

		date = new CAADate() ;
		date.Set( jd, true ) ;
		date.register( this, QK_DAY ) ;
		date.delete() ;

		eot = CAAEquationOfTime.Calculate( jd ) ;
		// convert to hours if greater than 20 minutes
		eot = ( eot>20?eot-24*60:eot )/60 ;

		hms = new DMS( eot ) ;
		hms.register( this, QK_EQUATIONOFTIME ) ;
	}

	protected void degister() {
		String sub ;

		sub = SubstituteCatalog.substitute( this, QK_DAY, QK_DAY ) ;
		Registry.degister( sub ) ;

		CAADate.degister( this, QK_DAY ) ;

		DMS.degister( this, QK_EQUATIONOFTIME ) ;
	}

	public boolean isMultipleSpan( double mark, double span ) {
		CAADate d ;
		int dw ;
		long dm ;

		d = new CAADate() ;
		d.Set( mark, true ) ;
		dw = d.DayOfWeek() ;
		dm = d.Day() ;
		d.delete() ;

		if ( span == 7 )
			return dw == 1 ;
		if ( span == 30 )
			return dm == 1 ;

		return super.isMultipleSpan( mark, span ) ;
	}
}
