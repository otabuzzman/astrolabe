
package astrolabe;

@SuppressWarnings("serial")
public class AtlasEquidistant extends AtlasAzimuthalType {

	// castor requirement for (un)marshalling
	public AtlasEquidistant() {
	}

	public AtlasEquidistant( Projector projector, astrolabe.model.Atlas atlas ) {
		super( projector, atlas ) ;
	}
}
