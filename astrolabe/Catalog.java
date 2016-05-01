
package astrolabe;

public interface Catalog extends PostscriptEmitter {
	public void addAllCatalogRecord() ;
	public CatalogRecord getCatalogRecord( String ident ) ;
	public CatalogRecord[] getCatalogRecord() ;
}
