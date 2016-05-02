
package astrolabe;

@SuppressWarnings("serial")
public class Rational extends astrolabe.model.Rational {

	private final static int DEFAULT_PRECISISON	= 6 ;

	private boolean sign ;

	private int deg ;
	private int frc ;

	// castor requirement for (un)marshalling
	public Rational() {
	}

	public Rational( double value ) {
		this( value, -1 ) ;
	}

	public Rational( double value, int precision ) {
		setup( value, precision ) ;
	}

	public void setup( double value, int precision ) {
		double d, p ;
		int e ;

		sign = 0>value ;

		if ( precision>-1 ) {
			e = precision ;
		} else {
			e = Configuration.getValue(
					Configuration.getClassNode( this, null, null ),
					ApplicationConstant.PK_RATIONAL_PRECISION, DEFAULT_PRECISISON ) ;
		}
		p = java.lang.Math.pow( 10, e ) ;

		d = (int) ( java.lang.Math.abs( value )*p+.5 )/p ;
		deg = (int) d ;
		frc = (int) ( ( d-deg )*p ) ;
		setValue( sign?-( deg+frc/p ):( deg+frc/p ) ) ;
	}

	public boolean sign() {
		return sign ;
	}

	public int deg() {
		return deg ;
	}

	public int frc() {
		return frc ;
	}
}
