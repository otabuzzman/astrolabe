
package astrolabe;

import caa.CAADate;

public final class DateHelper {

	public static boolean isDateGregorian( double jd ) {
		return jd>new CAADate( 1582, 10, 4, false ).Julian() ;
	}

	public static boolean isDateGregorian( long[] date ) {
		return isDateGregorian( date[0], date[1], date[2] ) ;
	}

	public static boolean isDateGregorian( long y, long m, long d ) {
		boolean r = false ;

		if ( y > 1582 ) {
			r = true ;
		} else if ( y == 1582 ) {
			if ( m > 10 ) {
				r = true ;
			} else if ( m == 10 ) {
				if ( d >= 15 ) {
					r = true ;
				}
			}
		}

		return r ;
	}
}
