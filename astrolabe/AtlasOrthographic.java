
package astrolabe;

import org.exolab.castor.xml.ValidationException;

@SuppressWarnings("serial")
public class AtlasOrthographic extends AtlasAzimuthalType implements Atlas {

	private astrolabe.model.ChartOrthographic chartAzimuthalType ;

	private ChartStereographic companion ;

	// castor requirement for (un)marshalling
	public AtlasOrthographic() {
	}

	public AtlasOrthographic( Peer peer ) {
		super( peer ) ;

		chartAzimuthalType = ( (astrolabe.model.AtlasOrthographic) peer ).getChartOrthographic() ;

		companion = new ChartStereographic( chartAzimuthalType ) ;
	}

	public astrolabe.model.Chart[] toModel() throws ValidationException {
		astrolabe.model.Chart[] model ;
		astrolabe.model.ChartOrthographic chart ;

		model = new astrolabe.model.Chart[ getAtlasPageCount() ] ;

		for ( int ap=0 ; ap<getAtlasPageCount() ; ap++ ) {
			model[ap] = new astrolabe.model.Chart() ;

			chart = new astrolabe.model.ChartOrthographic() ;
			chartAzimuthalType.setupCompanion( chart ) ;
			if ( getName() == null )
				chart.setName( ApplicationConstant.GC_NS_ATL ) ;
			else
				chart.setName( ApplicationConstant.GC_NS_ATL+getName() ) ;
			super.toModel( chart, ap ) ;

			model[ap].setChartOrthographic( chart ) ;

			model[ap].validate() ;
		}

		return model ;
	}

	public void emitPS( AstrolabePostscriptStream ps ) {
		companion.headPS( ps ) ;
		super.emitPS( ps );
		companion.tailPS( ps ) ;
	}

	public astrolabe.model.ChartAzimuthalType getChartAzimuthalType() {
		return chartAzimuthalType ;
	}

	public Projector projector() {
		return companion ;
	}
}
