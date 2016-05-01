
package astrolabe;

@SuppressWarnings("serial")
public class AtlasStereographic extends AtlasAzimuthalType {

	private astrolabe.model.ChartStereographic peerCS ;

	// castor requirement for (un)marshalling
	public AtlasStereographic() {
	}

	public AtlasStereographic( Peer peer ) {
		super( peer ) ;

		peerCS = ( (astrolabe.model.AtlasStereographic) peer ).getChartStereographic() ; 
	}

	public ChartAzimuthalType getChartAzimuthalType() {
		return ( new ChartStereographic( peerCS ) ) ;
	}

	public astrolabe.model.ChartAzimuthalType setChartAzimuthalType( astrolabe.model.Chart chart ) {
		astrolabe.model.ChartStereographic companionCS ;

		companionCS = new astrolabe.model.ChartStereographic() ;
		peerCS.setupCompanion( companionCS ) ;

		chart.setChartStereographic( companionCS ) ;

		return companionCS ;
	}
}
