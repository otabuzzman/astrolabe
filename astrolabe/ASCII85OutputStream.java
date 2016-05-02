
package astrolabe;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ASCII85OutputStream extends FilterOutputStream {

	// configuration key (CK_)
	private final static String CK_CPL		= "cpl" ;

	private final static int DEFAULT_CPL	= 72 ;

	private int cpl ;

	private int[] mem ;
	private int cnt ;
	private int[] a85 ;
	private boolean fin ;

	private int pos ;

	private static long f1 = 85;
	private static long f2 = f1*f1;
	private static long f3 = f2*f1;
	private static long f4 = f3*f1;

	public ASCII85OutputStream( OutputStream out ) {
		super( out ) ;

		cpl = Configuration.getValue( this, CK_CPL, DEFAULT_CPL ) ;

		mem = new int[] { 0, 0, 0, 0 } ;
		cnt = 0 ;
		a85 = new int[] { 0, 0, 0, 0, 0 } ;
		fin = false ;

		pos = 0 ;
	}

	public void write( byte b ) throws IOException {
		if ( fin )
			fin = false ;

		mem[cnt++] = b&0xff ;
		if ( cnt == 4 )
			encode() ;
	}

	public void close() throws IOException {
		finish() ;
		super.close();
	}

	public void finish() throws IOException {
		if ( fin )
			return ;

		fin = true ;
		if ( cnt>0 )
			encode() ;
		write( '~' ) ;
		write( '>' ) ;

		write( '\n' ) ;
		flush() ;
		flush() ;

		if ( out instanceof ASCII85OutputStream )
			( (ASCII85OutputStream) out ).finish() ;
	}

	private void encode() throws IOException {
		long d ;

		d = ( ( mem[0]<<24 )|( mem[1]<<16 )|( mem[2]<<8 )|mem[3] )&0xffffffffl ;

		if ( d == 0 && cnt == 4 )
			line( 'z' ) ;
		else {
			a85[0] = (int) ( d/f4+33 ) ;
			d = d%f4 ;
			a85[1] = (int) ( d/f3+33 ) ;
			d = d%f3 ;
			a85[2] = (int) ( d/f2+33 ) ;
			d = d%f2 ;
			a85[3] = (int) ( d/f1+33 ) ;
			a85[4] = (int) ( d%f1+33 ) ;

			for ( int c=0 ; c<cnt+1 ; c++ )
				line( a85[c] ) ;
		}

		for ( int i=0 ; 4>i ; i++ )
			mem[i] = 0 ;

		cnt = 0 ;
	}

	private void line( int c ) throws IOException {
		if ( pos == cpl ) {
			pos = 0 ;
			write( '\n' ) ;
		}
		pos++ ;
		write( c ) ;
	}

	public static void main( String[] argv ) {
		ASCII85OutputStream o ;
		InputStream i ;
		byte[] b ;

		o = new ASCII85OutputStream( System.out ) ;

		try {
			i = new BufferedInputStream( new FileInputStream( new File( argv[0] ) ) ) ;
			b = new byte[1] ;

			while( i.read( b, 0, 1 )>-1 )
				o.write( b[0] ) ;
			o.finish() ;
			o.close() ;
		} catch ( Exception e ) {
			e.printStackTrace() ;
			System.exit( 1 ) ;
		}

		System.exit( 0 ) ;
	}
}
