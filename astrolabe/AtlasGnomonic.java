
package astrolabe;

@SuppressWarnings("serial")
public class AtlasGnomonic extends AtlasAzimuthalType {

	// castor requirement for (un)marshalling
	public AtlasGnomonic() {
	}

	public AtlasGnomonic( astrolabe.model.Atlas atlas, double[] size, boolean northern, Projector projector ) {
		super( atlas, size, northern, projector ) ;
	}
}
