package astrolabe;

import java.util.Hashtable;

import org.exolab.castor.xml.ValidationException;

@SuppressWarnings("serial")
public class CatalogADC7118 extends astrolabe.model.CatalogADC7118 implements Catalog {

	public CatalogADC7118( Object peer, Projector projector ) throws ParameterNotValidException {
		ApplicationHelper.setupCompanionFromPeer( this, peer ) ;
		try {
			validate() ;
		} catch ( ValidationException e ) {
			throw new ParameterNotValidException( e.toString() ) ;
		}
	}

	public void headPS(PostscriptStream ps) {
	}

	public void emitPS(PostscriptStream ps) {
	}

	public void tailPS(PostscriptStream ps) {
	}

	public Hashtable<String, BodyStellar> read() {
		return null;
	}
}
