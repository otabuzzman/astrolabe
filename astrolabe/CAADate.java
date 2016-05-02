
package astrolabe;

public class CAADate extends caa.CAADate {

	// qualifier key (QK_)
	private final static String QK_NUMBEROFYYYY			= "YYYY" ;
	private final static String QK_NUMBEROFMMM			= "MM" ;
	private final static String QK_NUMBEROFDD			= "DD" ;

	// indirection key (IK_)
	private final static String IK_NAMEOFWEEKDAY		= "nameofweekday" ;
	private final static String IK_NAMEOFMONTH			= "nameofmonth" ;
	private final static String IK_NAMEOFWEEKDAYSHORT	= "nameofweekdayshort" ;
	private final static String IK_NAMEOFMONTHSHORT		= "nameofmonthshort" ;

	// value key (VK_)
	private final static String VK_MONDAY				= ".mon" ;
	private final static String VK_TUESDAY				= ".tue" ;
	private final static String VK_WEDNESDAY			= ".wed" ;
	private final static String VK_THURSDAY				= ".thu" ;
	private final static String VK_FRIDAY				= ".fri" ;
	private final static String VK_SATURDAY				= ".sat" ;
	private final static String VK_SUNDAY				= ".sun" ;

	private final static String VK_JANUARY				= ".jan" ;
	private final static String VK_FEBRUARY				= ".feb" ;
	private final static String VK_MARCH				= ".mar" ;
	private final static String VK_APRIL				= ".apr" ;
	private final static String VK_MAY					= ".may" ;
	private final static String VK_JUNE					= ".jun" ;
	private final static String VK_JULY					= ".jul" ;
	private final static String VK_AUGUST				= ".aug" ;
	private final static String VK_SEPTEMBER			= ".sep" ;
	private final static String VK_OCTOBER				= ".oct" ;
	private final static String VK_NOVEMBER				= ".nov" ;
	private final static String VK_DECEMBER				= ".dec" ;

	private final static String[] nameofweekday = {
		VK_SUNDAY,
		VK_MONDAY,
		VK_TUESDAY,
		VK_WEDNESDAY,
		VK_THURSDAY,
		VK_FRIDAY,
		VK_SATURDAY 
	} ;
	private final static String[] nameofmonth = {
		VK_JANUARY,
		VK_FEBRUARY,
		VK_MARCH,
		VK_APRIL,
		VK_MAY,
		VK_JUNE,
		VK_JULY,
		VK_AUGUST,
		VK_SEPTEMBER,
		VK_OCTOBER,
		VK_NOVEMBER,
		VK_DECEMBER
	} ;

	public CAADate() {
		super() ;
	}

	public CAADate( double jd, boolean gregorian ) {
		super( jd, gregorian ) ;
	}

	public void register() {
		register( this, null ) ;
	}

	public void register( Object clazz, String key ) {
		SubstituteCatalog scat ;
		MessageCatalog mcat ;
		String sub, val, k ;

		scat = new SubstituteCatalog( ApplicationConstant.GC_APPLICATION, clazz ) ;
		mcat = new MessageCatalog( ApplicationConstant.GC_APPLICATION, this ) ;

		if ( key == null ) {
			sub = scat.substitute( QK_NUMBEROFYYYY, null ) ;
			Registry.register( sub, Year() ) ;

			sub = scat.substitute( QK_NUMBEROFMMM, null ) ;
			Registry.register( sub, Month() ) ;

			sub = scat.substitute( QK_NUMBEROFDD, null ) ;
			Registry.register( sub, Day() ) ;

			sub = scat.substitute( IK_NAMEOFWEEKDAY, null ) ;
			val = mcat.message( IK_NAMEOFWEEKDAY+nameofweekday[ DayOfWeek() ], null ) ;
			Registry.register( sub, val ) ;

			sub = scat.substitute( IK_NAMEOFWEEKDAYSHORT, null ) ;
			val = mcat.message( IK_NAMEOFWEEKDAYSHORT+nameofweekday[ DayOfWeek() ], null ) ;
			Registry.register( sub, val ) ;

			sub = scat.substitute( IK_NAMEOFMONTH, null ) ;
			val = mcat.message( IK_NAMEOFMONTH+nameofmonth[ -1+(int) Month() ], null ) ;
			Registry.register( sub, val ) ;

			sub = scat.substitute( IK_NAMEOFMONTHSHORT, null ) ;
			val = mcat.message( IK_NAMEOFMONTHSHORT+nameofmonth[ -1+(int) Month() ], null ) ;
			Registry.register( sub, val ) ;
		} else {
			k = key+'.' ;

			sub = scat.substitute( k+QK_NUMBEROFYYYY, null ) ;
			Registry.register( sub, Year() ) ;

			sub = scat.substitute( k+QK_NUMBEROFMMM, null ) ;
			Registry.register( sub, Month() ) ;

			sub = scat.substitute( k+QK_NUMBEROFDD, null ) ;
			Registry.register( sub, Day() ) ;

			sub = scat.substitute( k+IK_NAMEOFWEEKDAY, null ) ;
			val = mcat.message( IK_NAMEOFWEEKDAY+nameofweekday[ DayOfWeek() ], null ) ;
			Registry.register( sub, val ) ;

			sub = scat.substitute( k+IK_NAMEOFWEEKDAYSHORT, null ) ;
			val = mcat.message( IK_NAMEOFWEEKDAYSHORT+nameofweekday[ DayOfWeek() ], null ) ;
			Registry.register( sub, val ) ;

			sub = scat.substitute( k+IK_NAMEOFMONTH, null ) ;
			val = mcat.message( IK_NAMEOFMONTH+nameofmonth[ -1+(int) Month() ], null ) ;
			Registry.register( sub, val ) ;

			sub = scat.substitute( k+IK_NAMEOFMONTHSHORT, null ) ;
			val = mcat.message( IK_NAMEOFMONTHSHORT+nameofmonth[ -1+(int) Month() ], null ) ;
			Registry.register( sub, val ) ;
		}
	}
}
