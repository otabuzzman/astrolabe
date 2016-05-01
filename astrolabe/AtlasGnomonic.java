
package astrolabe;

@SuppressWarnings("serial")
public class AtlasGnomonic extends AtlasAzimuthalType {

	private astrolabe.model.ChartGnomonic peerCG ;

	// castor requirement for (un)marshalling
	public AtlasGnomonic() {
	}

	public AtlasGnomonic( Object peer ) throws ParameterNotValidException {
		super( peer ) ;

		peerCG = ( (astrolabe.model.AtlasGnomonic) peer ).getChartGnomonic() ; 
	}

	public ChartAzimuthalType getChartAzimuthalType() {
		try {
			return ( new ChartGnomonic( peerCG ) ) ;
		} catch ( ParameterNotValidException e ) {
			throw new RuntimeException( e.toString() ) ;
		}
	}

	public astrolabe.model.ChartAzimuthalType setChartAzimuthalType( astrolabe.model.Chart chart ) {
		astrolabe.model.ChartGnomonic companionCO ;

		companionCO = new astrolabe.model.ChartGnomonic() ;
		ApplicationHelper.setupCompanionFromPeer( companionCO, peerCG ) ;

		chart.setChartGnomonic( companionCO ) ;

		return companionCO ;
	}
}
