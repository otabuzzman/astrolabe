
package astrolabe;

@SuppressWarnings("serial")
public class DMS extends astrolabe.model.DMS {

	private final static int DEFAULT_PRECISISON	= 2 ;

	// castor requirement for (un)marshalling
	public DMS() {
	}

	public DMS( double value ) {
		this( value, -1 ) ;
	}

	public DMS( double value, int precision ) {
		setup( value, precision ) ;
	}

	public void setup( double value, int precision ) {
		double d, m, s, p ;
		double sec, frc ;
		int deg, min ;
		int e ;

		if ( precision>-1 )
			e = precision ;
		else
			e = precision() ;
		p = java.lang.Math.pow( 10, e ) ;

		d = ( java.lang.Math.abs( value*3600. )*p+.5 )/p ;
		deg = (int) d/3600 ;
		setDeg( deg ) ;

		m = ( d-deg*3600 )/60 ;
		min = (int) m ;
		setMin( min ) ;

		s = d-deg*3600-min*60 ;
		sec = (int) s ;
		frc = (int) ( ( s-sec )*p ) ;
		setSec( sec+frc/p ) ;

		setNeg( 0>value ) ;
	}

	public int precision() {
		return Configuration.getValue(
				Configuration.getClassNode( this, null, null ),
				ApplicationConstant.PK_DMS_PRECISION, DEFAULT_PRECISISON ) ;
	}
}
