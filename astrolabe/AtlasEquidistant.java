
package astrolabe;

import org.exolab.castor.xml.ValidationException;

@SuppressWarnings("serial")
public class AtlasEquidistant extends AtlasAzimuthalType implements Atlas {

	private astrolabe.model.ChartEquidistant chartAzimuthalType ;

	private ChartStereographic companion ;

	// castor requirement for (un)marshalling
	public AtlasEquidistant() {
	}

	public AtlasEquidistant( Peer peer ) {
		super( peer ) ;

		chartAzimuthalType = ( (astrolabe.model.AtlasEquidistant) peer ).getChartEquidistant() ;

		companion = new ChartStereographic( chartAzimuthalType ) ;
	}

	public astrolabe.model.Chart[] toModel() throws ValidationException {
		astrolabe.model.Chart[] model ;
		astrolabe.model.ChartEquidistant chart ;

		model = new astrolabe.model.Chart[ getAtlasPageCount() ] ;

		for ( int ap=0 ; ap<getAtlasPageCount() ; ap++ ) {
			model[ap] = new astrolabe.model.Chart() ;

			chart = new astrolabe.model.ChartEquidistant() ;
			chartAzimuthalType.setupCompanion( chart ) ;

			model[ap].setChartEquidistant( chart ) ;

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
