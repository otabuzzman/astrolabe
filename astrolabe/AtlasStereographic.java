
package astrolabe;

@SuppressWarnings("serial")
public class AtlasStereographic extends AtlasAzimuthalType {

	// castor requirement for (un)marshalling
	public AtlasStereographic() {
	}

	public AtlasStereographic( astrolabe.model.Atlas atlas, double[] size, boolean northern, Projector projector ) {
		super( atlas, size, northern, projector ) ;
	}
}
