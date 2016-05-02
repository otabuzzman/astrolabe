
package astrolabe;

import java.text.MessageFormat;

public class ParameterNotValidError {

	// message key (MK_)
	private final static String MK_SUBJECT = "subject" ;

	private StringBuffer msg = new StringBuffer() ;

	public ParameterNotValidError( String param ) {
		MessageCatalog cat ;
		String fmt ;

		cat = new MessageCatalog( ApplicationConstant.GC_APPLICATION, this ) ;
		fmt = cat.message( MK_SUBJECT, null ) ;		
		if ( fmt != null )
			msg.append( MessageFormat.format( fmt, new Object[] { param } ) ) ;
		else
			msg.append( param ) ;
	}

	public ParameterNotValidError( String[] param ) {
		this( toString( param ) ) ;
	}

	public ParameterNotValidError( int param ) {
		this( Integer.toString( param ) ) ;
	}

	public ParameterNotValidError( int[] param ) {
		this( toString( param ) ) ;
	}

	public ParameterNotValidError( double param ) {
		this( Double.toString( param ) ) ;
	}

	public ParameterNotValidError( double[] param ) {
		this( toString( param ) ) ;
	}

	public String errmsg() {
		return errmsg( null ) ;
	}

	public String errmsg( String message ) {
		if ( message == null )
			return msg.toString()+'.' ;
		return msg.toString()+": "+message ;
	}

	public static String errmsg( String param, String message ) {
		return new ParameterNotValidError( param ).errmsg( message ) ;
	}

	public static String errmsg( String[] param, String message ) {
		return new ParameterNotValidError( param ).errmsg( message ) ;
	}

	public static String errmsg( int param, String message ) {
		return new ParameterNotValidError( param ).errmsg( message ) ;
	}

	public static String errmsg( int[] param, String message ) {
		return new ParameterNotValidError( param ).errmsg( message ) ;
	}

	public static String errmsg( double param, String message ) {
		return new ParameterNotValidError( param ).errmsg( message ) ;
	}

	public static String errmsg( double[] param, String message ) {
		return new ParameterNotValidError( param ).errmsg( message ) ;
	}

	private static String toString( String[] param ) {
		StringBuffer s = new StringBuffer() ;

		s.append( '[' );
		if ( param.length>0 ) {
			s.append( param[0] ) ;
			for ( int i=1 ; i<param.length ; i++ )
				s.append( ','+'"'+param[i]+'"' ) ;
		}
		s.append( ']' );

		return s.toString() ;
	}

	private static String toString( int[] param ) {
		String[] s = new String[ param.length ] ;

		for ( int i=0 ; i<param.length ; i++ )
			s[i] = Integer.toString( param[i] ) ;

		return toString( s ) ;
	}

	private static String toString( double[] param ) {
		String[] s = new String[ param.length ] ;

		for ( int i=0 ; i<param.length ; i++ )
			s[i] = Double.toString( param[i] ) ;

		return toString( s ) ;
	}
}
