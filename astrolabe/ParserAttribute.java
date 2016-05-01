
package astrolabe;

public class ParserAttribute extends ParserSubstitute {

	public String parse( String value ) throws ParameterNotValidException {
		String s, l, t, v ;
		java.util.regex.Pattern p ;
		java.util.regex.Matcher m ;

		t = new String( value ) ;

		p = java.util.regex.Pattern.compile( ApplicationConstant.LP_SUBSTITUTE ) ;
		m = p.matcher( t ) ;

		while ( m.find( 0 ) ) {
			s = t.substring( m.start(), m.end() ) ;
			l = s.substring( 1, s.length()-1 ) ;
			v = super.parse( l ) ;
			t = m.replaceFirst( v ) ;
			m = p.matcher( t ) ;
		}

		return t ;
	}

	public boolean booleanValue( String string ) {
		try {
			return new Boolean( parse( string ) ).booleanValue() ;
		} catch ( ParameterNotValidException e ) {
			throw new RuntimeException( e.toString() ) ;
		}
	}

	public int intValue( String string ) {
		return (int) longValue( string ) ;
	}

	public long longValue( String string ) {
		try {
			try {
				return new Double( parse( string ) ).longValue() ;
			} catch ( NumberFormatException e ) {
				return 0 ;
			}
		} catch ( ParameterNotValidException e ) {
			throw new RuntimeException( e.toString() ) ;
		}
	}

	public double doubleValue( String string ) {
		try {
			try {
				return new Double( parse( string ) ).doubleValue() ;
			} catch ( NumberFormatException e ) {
				return 0 ;
			}
		} catch ( ParameterNotValidException e ) {
			throw new RuntimeException( e.toString() ) ;
		}
	}

	public String stringValue( String string ) {
		try {
			return parse( string ) ;
		} catch ( ParameterNotValidException e ) {
			throw new RuntimeException( e.toString() ) ;
		}
	}
}
