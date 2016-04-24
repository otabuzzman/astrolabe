
package astrolabe;

@SuppressWarnings("serial")
public class BodyOrbital extends astrolabe.model.BodyOrbital implements Body {

	private Projector projector ;

	public BodyOrbital( Object peer, Projector projector ) {		
		ApplicationHelper.setupCompanionFromPeer( this, peer ) ;

		this.projector = projector ;
	}

	public void headPS( PostscriptStream ps ) {
	}

	public void emitPS( PostscriptStream ps ) {
	}

	public void tailPS( PostscriptStream ps ) {
	}
}
