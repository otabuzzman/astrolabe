
package astrolabe;

@SuppressWarnings("serial")
public class Rational extends astrolabe.model.Rational {

	// configuration key (CK_)
	private final static String CK_PRECISION	= "precision" ;

	private final static int DEFAULT_PRECISION	= 6 ;

	public Rational( double value ) {
		this( value, -1 ) ;
	}

	public Rational( double value, int precision ) {
		set( value, precision ) ;
	}

	public void set( double value, int precision ) {
		double d, p ;
		int deg, frc ;
		int e ;

		if ( precision>-1 )
			e = precision ;
		else
			e = Configuration.getValue( this, CK_PRECISION, DEFAULT_PRECISION ) ;
		p = java.lang.Math.pow( 10, e ) ;

		d = (int) ( java.lang.Math.abs( value )*p+.5 )/p ;
		deg = (int) d ;
		frc = (int) ( ( d-deg )*p ) ;
		setValue( 0>value?-( deg+frc/p ):( deg+frc/p ) ) ;
	}
}
