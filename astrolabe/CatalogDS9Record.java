
package astrolabe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

@SuppressWarnings("serial")
public class CatalogDS9Record extends astrolabe.model.CatalogDS9Record implements CatalogRecord {

	public List<String[]> element = new java.util.Vector<String[]>() ;

	public CatalogDS9Record( String data ) throws ParameterNotValidException {
		BufferedReader b ;
		String cl, eq[] ;

		try {
			b = new BufferedReader( new StringReader( data ) ) ;
			while ( ( cl = b.readLine() ) != null ) {
				eq = cl.trim().split( "\\p{Space}+" ) ;
				if ( eq.length != 2 )
					throw new ParameterNotValidException( cl ) ;

				element.add( eq ) ;
			}
		} catch ( IOException e ) {
			throw new RuntimeException( e.toString() ) ;
		}
	}

	public boolean isOK() {
		try {
			inspect() ;
		} catch ( ParameterNotValidException e ) {
			return false ;
		}

		return true ;
	}

	public void inspect() throws ParameterNotValidException {
	}

	public double RA() {
		return Double.valueOf( element.get( 0 )[0] ) ;
	}

	public double de() {
		return Double.valueOf( element.get( 0 )[1] ) ;
	}

	public List<double[]> list() {
		List<double[]> r = new java.util.Vector<double[]>() ;
		String[] eq ;

		for ( int i=0 ; i<element.size() ; i++ ) {
			eq = element.get( i ) ;

			r.add( new double[] {
					Double.valueOf( eq[0] ),
					Double.valueOf( eq[1] ) } ) ;
		}

		return r ;
	}
}
