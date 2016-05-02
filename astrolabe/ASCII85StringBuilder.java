
package astrolabe;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class ASCII85StringBuilder {

	// configuration key (CK_)
	private final static String CK_CPL		= "cpl" ;

	private final static int DEFAULT_CPL	= 72 ;

	private int cpl ;

	private int[] mem ;
	private int cnt ;
	private int[] a85 ;
	private boolean fin ;

	private int pos ;

	private StringBuilder buf ;

	private static long f1 = 85;
	private static long f2 = f1*f1;
	private static long f3 = f2*f1;
	private static long f4 = f3*f1;

	public ASCII85StringBuilder() {
		cpl = Configuration.getValue( this, CK_CPL, DEFAULT_CPL ) ;

		mem = new int[] { 0, 0, 0, 0 } ;
		cnt = 0 ;
		a85 = new int[] { 0, 0, 0, 0, 0 } ;
		fin = false ;

		pos = 0 ;

		buf = new StringBuilder() ;
	}

	public void append( byte b ) {
		if ( fin ) {
			fin = false ;
			buf.delete( 0, buf.length() ) ;
		}

		mem[cnt++] = b&0xff ;
		if ( cnt == 4 )
			encode() ;
	}

	public void finish() {
		if ( fin )
			return ;

		fin = true ;
		if ( cnt>0 )
			encode() ;
		buf.append( '~' ) ;
		buf.append( '>' ) ;

		buf.append( '\n' ) ;
	}

	public String toString() {
		return buf.toString() ;
	}

	private void encode() {
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

	private void line( int c ) {
		if ( pos == cpl ) {
			pos = 0 ;
			buf.append( '\n' ) ;
		}
		pos++ ;
		buf.append( (char) c ) ;
	}

	public static void main( String[] argv ) {
		ASCII85StringBuilder o ;
		InputStream i ;
		byte[] b ;

		o = new ASCII85StringBuilder() ;

		try {
			i = new BufferedInputStream( new FileInputStream( new File( argv[0] ) ) ) ;
			b = new byte[1] ;

			while( i.read( b, 0, 1 )>-1 )
				o.append( b[0] ) ;
			o.finish() ;
		} catch ( Exception e ) {
			e.printStackTrace() ;
			System.exit( 1 ) ;
		}

		System.exit( 0 ) ;
	}
}
