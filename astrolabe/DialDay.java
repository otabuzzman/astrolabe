
package astrolabe;

import caa.CAAEquationOfTime;

@SuppressWarnings("serial")
public class DialDay extends DialDegree {

	// qualifier key (QK_)
	private final static String QK_DAY				= "day" ;
	private final static String QK_EQUATIONOFTIME	= "equationoftime" ;

	private Baseline baseline ;

	public DialDay( Baseline baseline ) {
		super( baseline ) ;

		this.baseline = baseline ;
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
		boolean r ;
		CAADate d ;
		double jd0 ;

		jd0 = baseline.scaleMarkNth( 0, 1 ) ;

		d = new CAADate() ;

		if ( span==7 ) {
			d.Set( mark, true ) ;
			r = d.DayOfWeek()==1 ;
		} else if ( span==30 ) {
			d.Set( mark, true ) ;
			r = d.Day()==1 ;
		} else {
			r = Math.isLim0( ( mark-jd0 )/span-(int) ( ( mark-jd0 )/span ) ) ;
		}

		d.delete() ;

		return r ;
	}
}
