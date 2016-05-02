
package astrolabe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

@SuppressWarnings("serial")
public class CatalogDS9Record extends astrolabe.model.CatalogDS9Record implements CatalogRecord {

	public List<String[]> element = new java.util.Vector<String[]>() ;

	// message key (MK_)
	private final static String MK_ERECFMT = "erecfmt" ;

	public CatalogDS9Record( String data ) throws ParameterNotValidException {
		BufferedReader b ;
		String cl, eq[] ;
		String msg ;

		try {
			b = new BufferedReader( new StringReader( data ) ) ;
			while ( ( cl = b.readLine() ) != null ) {
				eq = cl.trim().split( "\\p{Space}+" ) ;
				if ( eq.length != 2 ) {
					msg = MessageCatalog.message( this, MK_ERECFMT, null ) ;

					throw new ParameterNotValidException( ParameterNotValidError.errmsg( data.length(), msg ) ) ;
				}
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

	public Coordinate[] list() {
		Coordinate[] list ;
		String[] eq ;

		list = new Coordinate[ element.size() ] ;

		for ( int i=0 ; i<element.size() ; i++ ) {
			eq = element.get( i ) ;

			list[i] = new Coordinate(
					Double.valueOf( eq[0] ),
					Double.valueOf( eq[1] ) ) ;
		}

		return list ;
	}
}
