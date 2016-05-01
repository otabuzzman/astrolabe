
package astrolabe;

@SuppressWarnings("serial")
public class AtlasEquidistant extends AtlasAzimuthalType {

	private astrolabe.model.ChartEquidistant peerCE ;

	// castor requirement for (un)marshalling
	public AtlasEquidistant() {
	}

	public AtlasEquidistant( Peer peer ) {
		super( peer ) ;

		peerCE = ( (astrolabe.model.AtlasEquidistant) peer ).getChartEquidistant() ; 
	}

	public ChartAzimuthalType getChartAzimuthalType() {
		return ( new ChartEquidistant( peerCE ) ) ;
	}

	public astrolabe.model.ChartAzimuthalType setChartAzimuthalType( astrolabe.model.Chart chart ) {
		astrolabe.model.ChartEquidistant companionCE ;

		companionCE = new astrolabe.model.ChartEquidistant() ;
		peerCE.setupCompanion( companionCE ) ;

		chart.setChartEquidistant( companionCE ) ;

		return companionCE ;
	}
}
