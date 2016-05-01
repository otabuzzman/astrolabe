
package astrolabe;

@SuppressWarnings("serial")
public class AtlasEquidistant extends AtlasAzimuthalType {

	private astrolabe.model.ChartEquidistant peerCE ;

	// castor requirement for (un)marshalling
	public AtlasEquidistant() {
	}

	public AtlasEquidistant( Object peer ) throws ParameterNotValidException {
		super( peer ) ;

		peerCE = ( (astrolabe.model.AtlasEquidistant) peer ).getChartEquidistant() ; 
	}

	public ChartAzimuthalType getChartAzimuthalType() {
		try {
			return ( new ChartEquidistant( peerCE ) ) ;
		} catch ( ParameterNotValidException e ) {
			throw new RuntimeException( e.toString() ) ;
		}
	}

	public astrolabe.model.ChartAzimuthalType setChartAzimuthalType( astrolabe.model.Chart chart ) {
		astrolabe.model.ChartEquidistant companionCE ;

		companionCE = new astrolabe.model.ChartEquidistant() ;
		ApplicationHelper.setupCompanionFromPeer( companionCE, peerCE ) ;

		chart.setChartEquidistant( companionCE ) ;

		return companionCE ;
	}
}
