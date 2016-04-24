
package astrolabe;

@SuppressWarnings("serial")
public class BodyElliptical extends astrolabe.model.BodyElliptical implements Body {

	public BodyElliptical( Object peer, Projector projector ) throws ParameterNotValidException {
		ApplicationHelper.setupCompanionFromPeer( this, peer ) ;
	}

	public void headPS( PostscriptStream ps ) {
	}

	public void emitPS( PostscriptStream ps ) {
	}

	public void tailPS( PostscriptStream ps ) {
	}
}
