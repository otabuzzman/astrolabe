
package astrolabe;

@SuppressWarnings("serial")
public class AtlasEquidistant extends AtlasAzimuthalType {

	// castor requirement for (un)marshalling
	public AtlasEquidistant() {
	}

	public AtlasEquidistant( astrolabe.model.Atlas atlas, double[] size, boolean northern, Projector projector ) {
		super( atlas, size, northern, projector ) ;
	}
}
