
package astrolabe;

public class PrintStream extends java.io.PrintStream {

	private java.util.Hashtable<String, java.io.PrintStream> tee = new java.util.Hashtable< String, java.io.PrintStream>() ;

	public PrintStream( java.io.OutputStream out ) throws ParameterNotValidException {
		super( out ) ;
		addPrintStream( new java.io.PrintStream( out ) ) ;
	}

	public void addPrintStream( java.io.PrintStream out ) throws ParameterNotValidException {

		if ( out == null ) {
			throw new ParameterNotValidException() ;
		}

		tee.put( out.toString(), out ) ;
	}

	public void delPrintStream( java.io.PrintStream out ) throws ParameterNotValidException {

		if ( out == null ) {
			throw new ParameterNotValidException() ;
		}

		tee.remove( out.toString() ) ;
	}

	// Override PrintStream methods.
	public void print( String string ) {
		java.util.Enumeration<java.io.PrintStream> list ;
		java.io.PrintStream out ;

		list = tee.elements() ;

		for ( int t=0 ; t<tee.size() ; t++ ) {
			out = list.nextElement() ;

			out.print( string ) ;
			out.flush() ;
		}
	}
}
