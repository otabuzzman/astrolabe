
package astrolabe;

public interface Catalog extends PostscriptEmitter {
	public void addAllCatalogRecord() ;
	public void delAllCatalogRecord() ;
	public CatalogRecord[] getCatalogRecord() ;
}
