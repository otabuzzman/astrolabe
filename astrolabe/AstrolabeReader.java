
package astrolabe;

import java.io.Reader;
import java.io.StringReader;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

public class AstrolabeReader {

	public AstrolabeReader() {
	}

	public Astrolabe read( Reader model ) throws ParameterNotValidException {
		astrolabe.model.Astrolabe al ;
		Astrolabe a = null ;

		try {
			al = (astrolabe.model.Astrolabe) astrolabe.model.Astrolabe.unmarshal( model ) ;
		} catch ( MarshalException e ) {
			throw new ParameterNotValidException( e.toString() ) ;
		} catch ( ValidationException e ) {
			throw new ParameterNotValidException( e.toString() ) ;
		}

		a = new Astrolabe( al ) ;

		return a ;
	}

	public Astrolabe read( String model ) throws ParameterNotValidException {
		StringReader r ;
		Astrolabe a ;

		r = new StringReader( model ) ;

		try {
			a = read( r ) ;
		} finally {
			r.close() ;
		}

		return a ;
	}
}
