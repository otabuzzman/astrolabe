
package astrolabe;

import java.io.Reader;
import java.net.URL;
import java.util.Hashtable;

public interface Catalog extends PostscriptEmitter {
	public Hashtable<String, CatalogRecord> read() throws ParameterNotValidException ;
	public Hashtable<String, CatalogRecord> read( URL catalog ) ;
	public Hashtable<String, CatalogRecord> read( String catalog ) ;
	public Hashtable<String, CatalogRecord> read( Reader catalog ) ;
}