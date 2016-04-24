
package astrolabe;

public interface Chart extends Projector {
	public void headPS( PostscriptStream ps ) ;
	public void tailPS( PostscriptStream ps ) ;
	public int getHorizonCount() ;
	public astrolabe.model.Horizon getHorizon( int horizon ) ;
}
