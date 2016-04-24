
package astrolabe;

import java.io.IOException;

public class PrintStream extends java.io.PrintStream {

	private Process viewer ;
	private java.io.PrintStream ps ;

	public PrintStream( java.io.PrintStream ps ) {
		super( ps ) ;

		String command ;

		command = ApplicationHelper.getClassNode( this/*PostscriptStream*/,
				null, null ).get( ApplicationConstant.PK_PRINTSTREAM_VIEWER, null ) ;

		try {
			viewer = Runtime.getRuntime().exec( command.split( " " ) ) ;
			viewer.getInputStream().close() ;
			viewer.getErrorStream().close() ;

			this.ps = new java.io.PrintStream( viewer.getOutputStream() ) ;
		} catch ( IOException e ) {}
	}

	// override
	public void print( String string ) {
		super.print( string ) ;

		ps.print( string ) ;
		ps.flush() ;
	}

	// override
	public void close() {
		super.close() ;

		ps.close() ;

		viewer.destroy() ;
	}
}
