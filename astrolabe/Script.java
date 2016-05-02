
package astrolabe;

@SuppressWarnings("serial")
public class Script extends astrolabe.model.Script {

	public Script( Peer peer ) {
		peer.setupCompanion( this ) ;
	}
}
