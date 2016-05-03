
package astrolabe;

import com.vividsolutions.jts.geom.Coordinate;

@SuppressWarnings("serial")
public class HorizonEquatorial extends HorizonType {

	private astrolabe.model.HorizonEquatorial peer ;
	private Projector projector ;

	public HorizonEquatorial( astrolabe.model.HorizonEquatorial peer, Projector projector ) {
		super( projector ) ;

		this.peer = peer ;
		this.projector = projector ;
	}

	public void emitPS( ApplicationPostscriptStream ps ) {
		astrolabe.model.Catalog catalog ;
		PostscriptEmitter emitter ;

		super.emitPS( ps ) ;

		for ( int ct=0 ; ct<peer.getCatalogCount() ; ct++ ) {
			catalog = peer.getCatalog( ct ) ;

			if ( catalog.getCatalogADC1239H() != null ) {
				emitter = catalog( catalog.getCatalogADC1239H() ) ;
			} else if (  catalog.getCatalogADC1239T() != null ) {
				emitter = catalog( catalog.getCatalogADC1239T() ) ;
			} else if ( catalog.getCatalogADC5050() != null ) {
				emitter = catalog( catalog.getCatalogADC5050() ) ;
			} else if (  catalog.getCatalogADC5109() != null ) {
				emitter = catalog( catalog.getCatalogADC5109() ) ;
			} else if ( catalog.getCatalogADC6049() != null ) {
				emitter = catalog( catalog.getCatalogADC6049() ) ;
			} else if ( catalog.getCatalogADC7118() != null ) {
				emitter = catalog( catalog.getCatalogADC7118() ) ;
			} else if ( catalog.getCatalogADC7237() != null ) {
				emitter = catalog( catalog.getCatalogADC7237() ) ;
			} else { // catalog.getCatalogDS9() != null
				emitter = catalog( catalog.getCatalogDS9() ) ;
			}

			ps.op( "gsave" ) ;

			emitter.headPS( ps ) ;
			emitter.emitPS( ps ) ;
			emitter.tailPS( ps ) ;

			ps.op( "grestore" ) ;
		}
	}

	public Coordinate convert( Coordinate local, boolean inverse ) {
		return inverse ? inverse( local ) : convert( local ) ;
	}

	private Coordinate convert( Coordinate local ) {
		return new Coordinate( local ) ;
	}

	private Coordinate inverse( Coordinate equatorial ) {
		return new Coordinate( equatorial ) ;
	}

	private PostscriptEmitter catalog( astrolabe.model.CatalogADC1239H peer ) {
		CatalogADC1239H catalog ;

		catalog = new CatalogADC1239H( this, projector ) ;
		peer.copyValues( catalog ) ;

		catalog.addAllCatalogRecord() ;
		return catalog ;
	}

	private PostscriptEmitter catalog( astrolabe.model.CatalogADC1239T peer ) {
		CatalogADC1239T catalog ;

		catalog = new CatalogADC1239T( this, projector ) ;
		peer.copyValues( catalog ) ;

		catalog.addAllCatalogRecord() ;
		return catalog ;
	}

	private PostscriptEmitter catalog( astrolabe.model.CatalogADC5050 peer ) {
		CatalogADC5050 catalog ;

		catalog = new CatalogADC5050( this, projector ) ;
		peer.copyValues( catalog ) ;

		catalog.addAllCatalogRecord() ;
		return catalog ;
	}

	private PostscriptEmitter catalog( astrolabe.model.CatalogADC5109 peer ) {
		CatalogADC5109 catalog ;

		catalog = new CatalogADC5109( this, projector ) ;
		peer.copyValues( catalog ) ;

		catalog.addAllCatalogRecord() ;
		return catalog ;
	}

	private PostscriptEmitter catalog( astrolabe.model.CatalogADC6049 peer ) {
		CatalogADC6049 catalog ;

		catalog = new CatalogADC6049( this, projector ) ;
		peer.copyValues( catalog ) ;

		catalog.addAllCatalogRecord() ;
		return catalog ;
	}

	private PostscriptEmitter catalog( astrolabe.model.CatalogADC7118 peer ) {
		CatalogADC7118 catalog ;

		catalog = new CatalogADC7118( this, projector ) ;
		peer.copyValues( catalog ) ;

		catalog.addAllCatalogRecord() ;
		return catalog ;
	}

	private PostscriptEmitter catalog( astrolabe.model.CatalogADC7237 peer ) {
		CatalogADC7237 catalog ;

		catalog = new CatalogADC7237( this, projector ) ;
		peer.copyValues( catalog ) ;

		catalog.addAllCatalogRecord() ;
		return catalog ;
	}

	private PostscriptEmitter catalog( astrolabe.model.CatalogDS9 peer ) {
		CatalogDS9 catalog ;

		catalog = new CatalogDS9( this, projector ) ;
		peer.copyValues( catalog ) ;

		return catalog ;
	}
}
