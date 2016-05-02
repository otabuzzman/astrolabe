
package astrolabe;

@SuppressWarnings("serial")
public class AtlasStereographic extends AtlasAzimuthalType {

	// castor requirement for (un)marshalling
	public AtlasStereographic() {
	}

	public AtlasStereographic( Projector projector, astrolabe.model.Atlas atlas ) {
		super( projector, atlas ) ;
	}
}
