
package astrolabe;

@SuppressWarnings("serial")
public class AtlasEquidistant extends AtlasAzimuthalType {

	private astrolabe.model.ChartEquidistant chart ;

	public AtlasEquidistant( Object peer ) throws ParameterNotValidException {
		super( peer ) ;

		chart = ( (astrolabe.model.AtlasEquidistant) peer ).getChartEquidistant() ; 
	}

	public ChartAzimuthalType chartAzimuthalType() {
		try {
			return ( new ChartEquidistant( chart ) ) ;
		} catch ( ParameterNotValidException e ) {
			throw new RuntimeException( e.toString() ) ;
		}
	}

	public astrolabe.model.Chart chart( astrolabe.model.ChartAzimuthalType chart ) {
		astrolabe.model.Chart c ;

		c = new astrolabe.model.Chart() ;
		c.setChartEquidistant( (astrolabe.model.ChartEquidistant) chart ) ;

		return c ;
	}
}
