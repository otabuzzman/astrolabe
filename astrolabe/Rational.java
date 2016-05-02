
package astrolabe;

@SuppressWarnings("serial")
public class Rational extends astrolabe.model.Rational {

	private final static int DEFAULT_PRECISISON	= 6 ;

	// castor requirement for (un)marshalling
	public Rational() {
	}

	public Rational( double value ) {
		this( value, -1 ) ;
	}

	public Rational( double value, int precision ) {
		double d, p ;
		int deg, frc ;
		int e ;

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
		setValue( 0>value?-( deg+frc/p ):( deg+frc/p ) ) ;
	}
}
