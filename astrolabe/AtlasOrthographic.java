
package astrolabe;

@SuppressWarnings("serial")
public class AtlasOrthographic extends AtlasAzimuthalType {

	private astrolabe.model.ChartOrthographic chart ;

	public AtlasOrthographic( Object peer ) throws ParameterNotValidException {
		super( peer ) ;

		chart = ( (astrolabe.model.AtlasOrthographic) peer ).getChartOrthographic() ; 
	}

	public ChartAzimuthalType chartAzimuthalType() {
		try {
			return ( new ChartOrthographic( chart ) ) ;
		} catch ( ParameterNotValidException e ) {
			throw new RuntimeException( e.toString() ) ;
		}
	}

	public astrolabe.model.Chart chart( astrolabe.model.ChartAzimuthalType chart ) {
		astrolabe.model.Chart c ;

		c = new astrolabe.model.Chart() ;
		c.setChartOrthographic( (astrolabe.model.ChartOrthographic) chart ) ;

		return c ;
	}
}
