
package astrolabe;

public class DateHelper extends Model {

	public static boolean isDateGregorian( int[] date, double time ) {
		return isDateGregorian( date[0], date[1], date[2], time ) ;
	}

	public static boolean isDateGregorian( int y, int m, int d, double time ) {
		boolean r = false ;

		if ( y > 1582 ) {
			r = true ;
		} else if ( y == 1582 ) {
			if ( m > 10 ) {
				r = true ;
			} else if ( m == 10 ) {
				if ( d+time/24 >= 15 ) {
					r = true ;
				}
			}
		}

		return r ;
	}
}
