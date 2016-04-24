
import astrolabe.Astrolabe;
import astrolabe.AstrolabeReader;
import astrolabe.PostscriptStream;

import java.io.FileReader;

public class Main {

	public Main() {
	}

	public static void main( String[] argv ) {
		FileReader model ;
		PostscriptStream ps ;
		Astrolabe a ;

		try {
			model = new FileReader( argv[0] ) ;
			a = new AstrolabeReader().read( model ) ;
			ps = a.initPS( System.out ) ;
			a.emitPS( ps ) ;
		} catch ( Exception e ) {
			e.printStackTrace() ;
			System.exit( 1 ) ;
		}

		System.exit( 0 ) ;
	} 
}
