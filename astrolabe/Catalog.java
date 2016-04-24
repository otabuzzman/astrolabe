
package astrolabe;

import java.util.Hashtable;

public interface Catalog {
	public void headPS( PostscriptStream ps ) ;
	public void emitPS( PostscriptStream ps ) ;
	public void tailPS( PostscriptStream ps ) ;
	public void read() throws ParameterNotValidException ;
	public Hashtable<String, CatalogRecord> read( java.net.URL catalog ) ;
	public Hashtable<String, CatalogRecord> read( String catalog ) ;
	public Hashtable<String, CatalogRecord> read( java.io.Reader catalog ) ;
	public CatalogRecord entry( String ident ) ;
	public CatalogRecord record( java.io.Reader catalog ) throws ParameterNotValidException ;
	public CatalogRecord[] arrange( Hashtable<String, CatalogRecord> catalog ) ;
}
