
package astrolabe;

public interface Horizon extends Projector {
	public void headPS( PostscriptStream ps ) ;
	public void tailPS( PostscriptStream ps ) ;
	// inherited methods from model 
	public int getCircleCount() ;
	public astrolabe.model.Circle getCircle( int circle ) ;
	public int getBodyCount() ;
	public astrolabe.model.Body getBody( int body ) ;
	public int getCatalogCount() ;
	public astrolabe.model.Catalog getCatalog( int catalog ) ;
}
