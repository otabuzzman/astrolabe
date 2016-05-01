
package astrolabe;

@SuppressWarnings("serial")
public class AtlasGnomonic extends AtlasAzimuthalType {

	private astrolabe.model.ChartGnomonic peerCG ;

	// castor requirement for (un)marshalling
	public AtlasGnomonic() {
	}

	public AtlasGnomonic( Peer peer ) {
		super( peer ) ;

		peerCG = ( (astrolabe.model.AtlasGnomonic) peer ).getChartGnomonic() ; 
	}

	public ChartAzimuthalType getChartAzimuthalType() {
		return ( new ChartGnomonic( peerCG ) ) ;
	}

	public astrolabe.model.ChartAzimuthalType setChartAzimuthalType( astrolabe.model.Chart chart ) {
		astrolabe.model.ChartGnomonic companionCO ;

		companionCO = new astrolabe.model.ChartGnomonic() ;
		peerCG.setupCompanion( companionCO ) ;

		chart.setChartGnomonic( companionCO ) ;

		return companionCO ;
	}
}
