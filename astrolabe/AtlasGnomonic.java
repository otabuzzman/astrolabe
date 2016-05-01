
package astrolabe;

@SuppressWarnings("serial")
public class AtlasGnomonic extends AtlasAzimuthalType {

	private astrolabe.model.ChartGnomonic chart ;

	public AtlasGnomonic( Object peer ) throws ParameterNotValidException {
		super( peer ) ;

		chart = ( (astrolabe.model.AtlasGnomonic) peer ).getChartGnomonic() ; 
	}

	public ChartAzimuthalType chartAzimuthalType() {
		try {
			return ( new ChartGnomonic( chart ) ) ;
		} catch ( ParameterNotValidException e ) {
			throw new RuntimeException( e.toString() ) ;
		}
	}

	public astrolabe.model.Chart chart( astrolabe.model.ChartAzimuthalType chart ) {
		astrolabe.model.Chart c ;

		c = new astrolabe.model.Chart() ;
		c.setChartGnomonic( (astrolabe.model.ChartGnomonic) chart ) ;

		return c ;
	}
}
