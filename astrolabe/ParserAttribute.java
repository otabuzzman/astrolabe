
package astrolabe;

public class ParserAttribute extends ParserSubstitute {

	public String parse( String string ) {
		String s, l, t, v ;
		java.util.regex.Pattern p ;
		java.util.regex.Matcher m ;
		int c ;

		t = new String( string ) ;

		p = java.util.regex.Pattern.compile( "\\{[^\\{\\}]+\\}" ) ;

		c = 0 ;
		m = p.matcher( t ) ;
		while ( m.find( 0 ) ) {
			c++ ;

			s = t.substring( m.start(), m.end() ) ;
			l = s.substring( 1, s.length()-1 ) ;
			v = super.parse( l ) ;
			t = m.replaceFirst( v ) ;
			m = p.matcher( t ) ;
		}

		return c>0?t:null ;
	}

	public boolean booleanValue( String string ) {
		return new Boolean( stringValue( string ) ).booleanValue() ;
	}

	public int intValue( String string ) {
		return (int) longValue( string ) ;
	}

	public long longValue( String string ) {
		try {
			return new Double( stringValue( string ) ).longValue() ;
		} catch ( NumberFormatException e ) {
			return 0 ;
		}
	}

	public double doubleValue( String string ) {
		try {
			return new Double( stringValue( string ) ).doubleValue() ;
		} catch ( NumberFormatException e ) {
			return 0 ;
		}
	}

	public String stringValue( String string ) {
		return parse( string ) ;
	}
}
