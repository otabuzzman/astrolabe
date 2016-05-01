
package astrolabe;

@SuppressWarnings("serial")
public class AtlasStereographic extends AtlasAzimuthalType {

	private astrolabe.model.ChartStereographic chart ;

	public AtlasStereographic( Object peer ) throws ParameterNotValidException {
		super( peer ) ;

		chart = ( (astrolabe.model.AtlasStereographic) peer ).getChartStereographic() ; 
	}

	public ChartAzimuthalType chartAzimuthalType() {
		try {
			return ( new ChartStereographic( chart ) ) ;
		} catch ( ParameterNotValidException e ) {
			throw new RuntimeException( e.toString() ) ;
		}
	}

	public astrolabe.model.Chart chart( astrolabe.model.ChartAzimuthalType chart ) {
		astrolabe.model.Chart c ;

		c = new astrolabe.model.Chart() ;
		c.setChartStereographic( (astrolabe.model.ChartStereographic) chart ) ;

		return c ;
	}
}
