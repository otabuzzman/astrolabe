
package astrolabe;
@SuppressWarnings("serial")
public class AtlasOrthographic extends AtlasAzimuthalType {

	// castor requirement for (un)marshalling
	public AtlasOrthographic() {
	}

	public AtlasOrthographic( Projector projector, astrolabe.model.Atlas atlas ) {
		super( projector, atlas ) ;
	}
}
