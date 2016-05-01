
package astrolabe;

import org.exolab.castor.xml.ValidationException;

@SuppressWarnings("serial")
public class AtlasGnomonic extends AtlasAzimuthalType implements Atlas {

	private astrolabe.model.ChartGnomonic chartAzimuthalType ;

	private ChartStereographic companion ;

	// castor requirement for (un)marshalling
	public AtlasGnomonic() {
	}

	public AtlasGnomonic( Peer peer ) {
		super( peer ) ;

		chartAzimuthalType = ( (astrolabe.model.AtlasGnomonic) peer ).getChartGnomonic() ;

		companion = new ChartStereographic( chartAzimuthalType ) ;
	}

	public astrolabe.model.Chart[] toModel() throws ValidationException {
		astrolabe.model.Chart[] model ;
		astrolabe.model.ChartGnomonic chart ;

		model = new astrolabe.model.Chart[ getAtlasPageCount() ] ;

		for ( int ap=0 ; ap<getAtlasPageCount() ; ap++ ) {
			model[ap] = new astrolabe.model.Chart() ;

			chart = new astrolabe.model.ChartGnomonic() ;
			chart.setName( ApplicationConstant.GC_NS_ATL+getName() ) ;

			chartAzimuthalType.setupCompanion( chart ) ;

			model[ap].setChartGnomonic( chart ) ;

			model[ap].validate() ;

			super.toModel( chart, ap ) ;
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
