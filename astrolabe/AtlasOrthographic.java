
package astrolabe;

@SuppressWarnings("serial")
public class AtlasOrthographic extends AtlasAzimuthalType {

	private astrolabe.model.ChartOrthographic peerCO ;

	// castor requirement for (un)marshalling
	public AtlasOrthographic() {
	}

	public AtlasOrthographic( Object peer ) throws ParameterNotValidException {
		super( peer ) ;

		peerCO = ( (astrolabe.model.AtlasOrthographic) peer ).getChartOrthographic() ; 
	}

	public ChartAzimuthalType getChartAzimuthalType() {
		try {
			return ( new ChartOrthographic( peerCO ) ) ;
		} catch ( ParameterNotValidException e ) {
			throw new RuntimeException( e.toString() ) ;
		}
	}

	public astrolabe.model.ChartAzimuthalType setChartAzimuthalType( astrolabe.model.Chart chart ) {
		astrolabe.model.ChartOrthographic companionCO ;

		companionCO = new astrolabe.model.ChartOrthographic() ;
		ApplicationHelper.setupCompanionFromPeer( companionCO, peerCO ) ;

		chart.setChartOrthographic( companionCO ) ;

		return companionCO ;
	}
}
