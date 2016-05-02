
package astrolabe;

@SuppressWarnings("serial")
public class AtlasGnomonic extends AtlasAzimuthalType {

	// castor requirement for (un)marshalling
	public AtlasGnomonic() {
	}

	public AtlasGnomonic( Projector projector, astrolabe.model.Atlas atlas ) {
		super( projector, atlas ) ;
	}
}
