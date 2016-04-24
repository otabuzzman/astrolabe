
package astrolabe;

public interface Catalog {
	public void headPS( PostscriptStream ps ) ;
	public void emitPS( PostscriptStream ps ) ;
	public void tailPS( PostscriptStream ps ) ;
	public void read() throws ParameterNotValidException ;
	public java.util.Vector<CatalogRecord> read( java.net.URL catalog ) ;
	public java.util.Vector<CatalogRecord> read( String catalog ) ;
	public java.util.Vector<CatalogRecord> read( java.io.Reader catalog ) ;
	public CatalogRecord record( java.io.Reader catalog ) throws ParameterNotValidException ;
}
