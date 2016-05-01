
package astrolabe;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;

public class TeeOutputStream extends FilterOutputStream {

	private HashSet<FilterOutputStream> tee = new HashSet<FilterOutputStream>() ;

	public TeeOutputStream( OutputStream out ) {
		super( out ) ;
	}

	public void add( OutputStream out ) {
		tee.add( new FilterOutputStream( out ) ) ;
	}

	public void clear() {
		tee.clear() ;
	}

	public void write( int b ) throws IOException {
		super.write( b ) ;

		for( FilterOutputStream out : tee ) {
			out.write( b ) ;
		}
	}

	public void flush() throws IOException {
		super.flush() ;

		for( FilterOutputStream out : tee ) {
			out.flush() ;
		}
	}

	public void close() throws IOException {
		super.close() ;

		for( FilterOutputStream out : tee ) {
			out.close() ;
		}
	}
}
