
package astrolabe;

@SuppressWarnings("serial")
public class HorizonEquatorial extends HorizonType implements PostscriptEmitter, Projector {

	private Projector projector ;

	public HorizonEquatorial( Peer peer, Projector projector ) {
		super( peer, projector ) ;

		this.projector = projector ;
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
System.err.println( new java.util.Date().toString() ) ;
			catalog.addAllCatalogRecord() ;
System.err.println( new java.util.Date().toString() ) ;

			ps.operator.gsave() ;

			catalog.headPS( ps ) ;
			catalog.emitPS( ps ) ;
			catalog.tailPS( ps ) ;

			ps.operator.grestore() ;
		}
	}

	public double[] project( double RA, double d ) {
		return projector.project( convert( RA, d ) ) ;
	}

	public double[] unproject( double x, double y ) {
		return unconvert( projector.unproject( x, y ) ) ;
	}

	public double[] convert( double RA, double d ) {
		double[] r = new double[2] ;

		r[0] = RA ;
		r[1] = d ;

		return r ;
	}

	public double[] unconvert( double RA, double d ) {
		double[] r = new double[2] ;

		r[0] = RA ;
		r[1] = d ;

		return r ;
	}
}
