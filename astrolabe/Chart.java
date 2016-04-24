
package astrolabe;

public interface Chart extends Projector {
	public void headPS( PostscriptStream ps ) throws ParameterNotValidException ;
	public void tailPS( PostscriptStream ps ) ;
	// inherited methods from model 
	public String getName() ;
	public int getHorizonCount() ;
	public astrolabe.model.Horizon getHorizon( int horizon ) ;
}
