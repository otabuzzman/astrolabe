
package astrolabe;

public class PrintStream extends java.io.PrintStream {

	private java.util.Vector<java.io.PrintStream> tee = new java.util.Vector<java.io.PrintStream>() ;

	public PrintStream( java.io.OutputStream out ) throws ParameterNotValidException {
		super( out ) ;
		addPrintStream( new java.io.PrintStream( out ) ) ;
	}

	public void addPrintStream( java.io.PrintStream out ) throws ParameterNotValidException {

		if ( out == null ) {
			throw new ParameterNotValidException() ;
		}

		tee.add( out ) ;
	}

	// Override PrintStream methods.
	public void print( String string ) {
		java.io.PrintStream out ;

		for ( int t=0 ; t<tee.size() ; t++ ) {
			out = tee.get( t ) ;

			out.print( string ) ;
			out.flush() ; }
	}
}
