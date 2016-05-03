
package astrolabe;

import com.vividsolutions.jts.geom.Coordinate;

@SuppressWarnings("serial")
abstract public class ChartType extends astrolabe.model.ChartType implements PostscriptEmitter, Projector {

	public double scale() {
		ChartPage page ;
		double x, view[] ;

		page = new ChartPage() ;
		getChartPage().copyValues( page ) ;

		view = page.view() ;
		x = view[0]/2 ;

		return x*getScale()/100 ;
	}

	public void headPS( ApplicationPostscriptStream ps ) {
		ChartPage page ;

		page = new ChartPage() ;
		getChartPage().copyValues( page ) ;

		page.headPS( ps ) ;
		page.emitPS( ps ) ;
		page.tailPS( ps ) ;
	}

	public void emitPS( ApplicationPostscriptStream ps ) {
		astrolabe.model.Horizon horizon ;

		for ( int ho=0 ; ho<getHorizonCount() ; ho++ ) {
			horizon = getHorizon( ho ) ;

			if ( horizon.getHorizonLocal() != null ) {
				horizon( ps, horizon.getHorizonLocal() ) ;
			} else if ( horizon.getHorizonEquatorial() != null ) {
				horizon( ps, horizon.getHorizonEquatorial() ) ;
			} else if ( horizon.getHorizonEcliptical() != null ) {
				horizon( ps, horizon.getHorizonEcliptical() ) ;
			} else { // horizon.getHorizonGalactic() != null
				horizon( ps, horizon.getHorizonGalactic() ) ;
			}
		}
	}

	public void tailPS( ApplicationPostscriptStream ps ) {
		ps.op( "showpage" ) ;
		ps.dc( "%PageTrailer", null ) ;
	}

	abstract public Coordinate project( Coordinate celestial, boolean inverse ) ;

	private void horizon( ApplicationPostscriptStream ps, astrolabe.model.HorizonLocal peer ) {
		HorizonLocal horizon ;

		horizon = new HorizonLocal( peer, this ) ;
		peer.copyValues( horizon ) ;

		horizon.register() ;
		emitPS( ps, horizon ) ;
		horizon.degister() ;
	}

	private void horizon( ApplicationPostscriptStream ps, astrolabe.model.HorizonEquatorial peer ) {
		HorizonEquatorial horizon ;

		horizon = new HorizonEquatorial( peer, this ) ;
		peer.copyValues( horizon ) ;

		emitPS( ps, horizon ) ;
	}

	private void horizon( ApplicationPostscriptStream ps, astrolabe.model.HorizonEcliptical peer ) {
		HorizonEcliptical horizon ;

		horizon = new HorizonEcliptical( this ) ;
		peer.copyValues( horizon ) ;

		horizon.register() ;
		emitPS( ps, horizon ) ;
		horizon.degister() ;
	}

	private void horizon( ApplicationPostscriptStream ps, astrolabe.model.HorizonGalactic peer ) {
		HorizonGalactic horizon ;

		horizon = new HorizonGalactic( this ) ;
		peer.copyValues( horizon ) ;

		horizon.register() ;
		emitPS( ps, horizon ) ;
		horizon.degister() ;
	}

	private void emitPS( ApplicationPostscriptStream ps, PostscriptEmitter emitter ) {
		ps.op( "gsave" ) ;

		emitter.headPS( ps ) ;
		emitter.emitPS( ps ) ;
		emitter.tailPS( ps ) ;

		ps.op( "grestore" ) ;
	}
}
