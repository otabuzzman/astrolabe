
import astrolabe.Astrolabe;
import astrolabe.AstrolabeReader;
import astrolabe.PostscriptStream;
import astrolabe.Registry;

import java.io.FileReader;

public class Main {

	public Main() { 
	}

	public static void main( String[] argv ) {
		FileReader m ;
		Astrolabe a ;
		PostscriptStream ps ;

		try {
			m = new FileReader( argv[0] ) ;
			a = new AstrolabeReader().read( m ) ;

			ps = Astrolabe.initPS() ;

			a.emitPS( ps ) ;

			ps.close() ;

			Registry.remove() ;
		} catch ( Exception e ) {
			e.printStackTrace() ;
			System.exit( 1 ) ;
		}

		System.exit( 0 ) ;
	} 
}
