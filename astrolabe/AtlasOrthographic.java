
package astrolabe;
@SuppressWarnings("serial")
public class AtlasOrthographic extends AtlasAzimuthalType {

	// castor requirement for (un)marshalling
	public AtlasOrthographic() {
	}

	public AtlasOrthographic( astrolabe.model.Atlas atlas, double[] size, boolean northern, Projector projector ) {
		super( atlas, size, northern, projector ) ;
	}
}
