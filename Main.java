
import astrolabe.Astrolabe;
import astrolabe.AstrolabeReader;
import astrolabe.PostscriptStream;

import java.io.FileReader;

public class Main {

	public Main() {
	}

	public static void main( String[] argv ) {
		PostscriptStream ps ;
		FileReader m ;
		Astrolabe a ;

		try {
			ps = Astrolabe.initPS() ;

			m = new FileReader( argv[0] ) ;
			a = new AstrolabeReader().read( m ) ;

			a.emitPS( ps ) ;

			ps.close();
		} catch ( Exception e ) {
			e.printStackTrace() ;
			System.exit( 1 ) ;
		}

		System.exit( 0 ) ;
	} 
}
