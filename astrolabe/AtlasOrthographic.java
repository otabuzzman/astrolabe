
package astrolabe;

@SuppressWarnings("serial")
public class AtlasOrthographic extends AtlasAzimuthalType {

	private astrolabe.model.ChartOrthographic peerCO ;

	// castor requirement for (un)marshalling
	public AtlasOrthographic() {
	}

	public AtlasOrthographic( Peer peer ) {
		super( peer ) ;

		peerCO = ( (astrolabe.model.AtlasOrthographic) peer ).getChartOrthographic() ; 
	}

	public ChartAzimuthalType getChartAzimuthalType() {
		return ( new ChartOrthographic( peerCO ) ) ;
	}

	public astrolabe.model.ChartAzimuthalType setChartAzimuthalType( astrolabe.model.Chart chart ) {
		astrolabe.model.ChartOrthographic companionCO ;

		companionCO = new astrolabe.model.ChartOrthographic() ;
		peerCO.setupCompanion( companionCO ) ;

		chart.setChartOrthographic( companionCO ) ;

		return companionCO ;
	}
}
