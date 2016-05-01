
package astrolabe;

import java.util.prefs.Preferences;

@SuppressWarnings("serial")
public class DMS extends astrolabe.model.DMS {

	private final static int DEFAULT_PRECISISON	= 2 ;

	private boolean sign ;

	private int deg ;
	private int min ;
	private int sec ;
	private int frc ;

	// castor requirement for (un)marshalling
	public DMS() {
	}

	public DMS( double value ) {
		this( value, -1 ) ;
	}

	public DMS( double value, int precision ) {
		Preferences node ;
		double d, m, s, p ;
		int e ;

		sign = 0>value ;

		if ( precision>-1 ) {
			e = precision ;
		} else {
			node = ApplicationHelper.getClassNode( this, null, null ) ;
			e = ApplicationHelper.getPreferencesKV( node, ApplicationConstant.PK_DMS_PRECISION, DEFAULT_PRECISISON ) ;
		}
		p = java.lang.Math.pow( 10, e ) ;

		d = java.lang.Math.abs( value ) ;
		deg = (int) d ;
		setDeg( sign?-deg:deg ) ;

		m = 60*( d-deg ) ;
		min = (int) m ;
		setMin( sign?-min:min ) ;

		s = 60*( m-min ) ;
		sec = (int) s ;
		frc = (int) ( ( s-sec )*p ) ;
		setSec( sign?-( sec+frc/p ):( sec+frc/p ) ) ;
	}

	public boolean sign() {
		return sign ;
	}

	public int deg() {
		return deg ;
	}

	public int min() {
		return min ;
	}

	public int sec() {
		return sec ;
	}

	public int frc() {
		return frc ;
	}

	public int[] discrete() {
		return new int[] { deg(), min(), sec(), frc() } ;
	}

	public int[] relevant() {
		int v[], a, o ;

		v = discrete() ;
		for ( a=0 ; a<4 ; a++ ) {
			if ( v[a] > 0 ) {
				break ;
			}
		}
		if ( a == 4 ) { // all zero
			a = 0 ;
			o = 0 ;
		} else {
			for ( o=4 ; o>0 ; o-- ) {
				if ( v[o-1] > 0 ) {
					break ;
				}
			}
		}

		return new int[] { a, o } ;
	}
}
