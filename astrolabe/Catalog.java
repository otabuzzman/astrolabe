
package astrolabe;

public interface Catalog {
	public void headPS( PostscriptStream ps ) ;
	public void emitPS( PostscriptStream ps ) ;
	public void tailPS( PostscriptStream ps ) ;
	public java.util.Hashtable<String, CatalogRecord> read() throws ParameterNotValidException ;
}
