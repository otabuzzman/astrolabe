
package astrolabe;

@SuppressWarnings("serial")
public class Artefact extends astrolabe.model.Artefact implements PostscriptEmitter {

	private Projector projector ;
	private Catalog catalog ;

	public Artefact( Peer peer, Projector projector, Catalog catalog ) {
		peer.setupCompanion( this ) ;

		this.projector = projector ;
		this.catalog = catalog ;
	}

	public void headPS( AstrolabePostscriptStream ps ) {
	}

	public void emitPS( AstrolabePostscriptStream ps ) {
		Sign companion ;

		for ( astrolabe.model.Sign peer : getSign() ) {
			companion = new Sign( peer, projector, catalog ) ;

			ps.operator.gsave() ;

			companion.headPS( ps ) ;
			companion.headPS( ps ) ;
			companion.headPS( ps ) ;

			ps.operator.grestore() ;
		}
	}

	public void tailPS( AstrolabePostscriptStream ps ) {
	}
}
