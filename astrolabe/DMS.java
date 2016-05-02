
package astrolabe;

@SuppressWarnings("serial")
public class DMS extends astrolabe.model.DMS {

	// configuration key (CK_)
	private final static String CK_PRECISION	= "precision" ;

	private final static int DEFAULT_PRECISION	= 2 ;

	// qualifier key (QK_)
	private final static String QK_NEG = "neg" ;
	private final static String QK_DEG = "deg" ;
	private final static String QK_MIN = "min" ;
	private final static String QK_SEC = "sec" ;
	private final static String QK_FRC = "frc" ;

	// castor requirement for (un)marshalling
	public DMS() {
	}

	public DMS( double value ) {
		this( value, -1 ) ;
	}

	public DMS( double value, int precision ) {
		set( value, precision ) ;
	}

	public void set( double value, int precision ) {
		double d, m, s, p ;
		double sec, frc ;
		int deg, min ;
		int e ;

		setNeg( 0>value ) ;

		if ( precision>-1 )
			e = precision ;
		else
			e = Configuration.getValue( this, CK_PRECISION, DEFAULT_PRECISION ) ;
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
	}

	public void register() {
		register( this, null ) ;
	}

	public void register( Object clazz, String key ) {
		SubstituteCatalog cat ;
		String sub, sec[], k ;

		cat = new SubstituteCatalog( ApplicationConstant.GC_APPLICATION, clazz ) ;

		if ( key == null ) {
			sub = cat.substitute( QK_NEG, null ) ;
			Registry.register( sub, getNeg() ) ;

			sub = cat.substitute( QK_DEG, null ) ;
			Registry.register( sub, getDeg() ) ;

			sub = cat.substitute( QK_MIN, null ) ;
			Registry.register( sub, getMin() ) ;

			sec = Double.toString( getSec() ).split( "\\." ) ;
			sub = cat.substitute( QK_SEC, null ) ;
			Registry.register( sub, Long.parseLong( sec[0] ) ) ;

			sub = cat.substitute( QK_FRC, null ) ;
			Registry.register( sub, Long.parseLong( sec[1] ) ) ;
		} else {
			k = key+'.' ;

			sub = cat.substitute( k+QK_NEG, null ) ;
			Registry.register( sub, getNeg() ) ;

			sub = cat.substitute( k+QK_DEG, null ) ;
			Registry.register( sub, getDeg() ) ;

			sub = cat.substitute( k+QK_MIN, null ) ;
			Registry.register( sub, getMin() ) ;

			sec = Double.toString( getSec() ).split( "\\." ) ;
			sub = cat.substitute( k+QK_SEC, null ) ;
			Registry.register( sub, Long.parseLong( sec[0] ) ) ;

			sub = cat.substitute( k+QK_FRC, null ) ;
			Registry.register( sub, Long.parseLong( sec[1] ) ) ;
		}
	}
}
