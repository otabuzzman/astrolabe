
package astrolabe;

@SuppressWarnings("serial")
public class Sign extends astrolabe.model.Sign implements PostscriptEmitter {

	private Projector projector ;

	public Sign( Projector projector ) {
		this.projector = projector ;
	}

	public void headPS( ApplicationPostscriptStream ps ) {
	}

	public void emitPS( ApplicationPostscriptStream ps ) {
	}

	public void tailPS( ApplicationPostscriptStream ps ) {
	}
}
