
package astrolabe;

@SuppressWarnings("serial")
abstract class HorizonType extends astrolabe.model.HorizonType implements PostscriptEmitter, Projector {

	public HorizonType( Peer peer, Projector projector ) {
		peer.setupCompanion( this ) ;
	}

	public void headPS( AstrolabePostscriptStream ps ) {
		ElementPracticality practicality ;

		practicality = new ElementPracticality( getPracticality() ) ;
		practicality.headPS( ps ) ;
		practicality.emitPS( ps ) ;
		practicality.tailPS( ps ) ;
	}

	public void emitPS( AstrolabePostscriptStream ps ) {
		for ( int an=0 ; an<getAnnotationStraightCount() ; an++ ) {
			PostscriptEmitter annotation ;

			annotation = new AnnotationStraight( getAnnotationStraight( an ) ) ;

			ps.operator.gsave() ;

			annotation.headPS( ps ) ;
			annotation.emitPS( ps ) ;
			annotation.tailPS( ps ) ;

			ps.operator.grestore() ;
		}

		for ( int cl=0 ; cl<getCircleCount() ; cl++ ) {
			PostscriptEmitter circle ;

			circle = AstrolabeFactory.companionOf( getCircle( cl ), this ) ;

			ps.operator.gsave() ;

			circle.headPS( ps ) ;
			circle.emitPS( ps ) ;
			circle.tailPS( ps ) ;

			ps.operator.grestore() ;
		}

		for ( int bd=0 ; bd<getBodyCount() ; bd++ ) {
			PostscriptEmitter body ;

			body = AstrolabeFactory.companionOf( getBody( bd ), this ) ;

			ps.operator.gsave() ;

			body.headPS( ps ) ;
			body.emitPS( ps ) ;
			body.tailPS( ps ) ;

			ps.operator.grestore() ;
		}

		for ( int ct=0 ; ct<getCatalogCount() ; ct++ ) {
			Catalog catalog ;

			catalog = AstrolabeFactory.companionOf( getCatalog( ct ), this ) ;

			catalog.addAllCatalogRecord() ;

			ps.operator.gsave() ;

			catalog.headPS( ps ) ;
			catalog.emitPS( ps ) ;
			catalog.tailPS( ps ) ;

			ps.operator.grestore() ;
		}
	}


	public void tailPS( AstrolabePostscriptStream ps ) {
	}

	public double[] project( double[] ho ) {
		return project( ho[0], ho[1] ) ;
	}

	public abstract double[] project( double phi, double theta ) ;

	public double[] unproject( double[] xy ) {
		return unproject( xy[0], xy[1] ) ;
	}

	public abstract double[] unproject( double x, double y ) ;

	public double[] convert( double[] ho ) {
		return convert( ho[0], ho[1] ) ;
	}

	public abstract double[] convert( double phi, double thetaq ) ;

	public double[] unconvert( double[] eq ) {
		return unconvert( eq[0], eq[1] ) ;
	}

	public abstract double[] unconvert( double RA, double d ) ;
}
