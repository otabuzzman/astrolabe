
package astrolabe;

import org.exolab.castor.xml.ValidationException;

@SuppressWarnings("serial")
abstract class HorizonType extends astrolabe.model.HorizonType implements PostscriptEmitter, Projector {

	public HorizonType( Object peer, Projector projector ) throws ParameterNotValidException {
		ApplicationHelper.setupCompanionFromPeer( this, peer ) ;
		try {
			validate() ;
		} catch ( ValidationException e ) {
			throw new ParameterNotValidException( e.toString() ) ;
		}
	}

	public void headPS( PostscriptStream ps ) {
		ElementPracticality practicality ;

		practicality = new ElementPracticality( getPracticality() ) ;
		practicality.headPS( ps ) ;
		practicality.emitPS( ps ) ;
		practicality.tailPS( ps ) ;
	}

	public void emitPS( PostscriptStream ps ) {
		for ( int an=0 ; an<getAnnotationStraightCount() ; an++ ) {
			PostscriptEmitter annotation ;

			try {
				annotation = new AnnotationStraight( getAnnotationStraight( an ) ) ;
			} catch ( ParameterNotValidException e ) {
				throw new RuntimeException( e.toString() ) ;
			}

			ps.operator.gsave() ;

			annotation.headPS( ps ) ;
			annotation.emitPS( ps ) ;
			annotation.tailPS( ps ) ;

			ps.operator.grestore() ;
		}

		for ( int cl=0 ; cl<getCircleCount() ; cl++ ) {
			PostscriptEmitter circle ;

			try {
				circle = AstrolabeFactory.companionOf( getCircle( cl ), this ) ;
			} catch ( ParameterNotValidException e ) {
				throw new RuntimeException( e.toString() ) ;
			}

			ps.operator.gsave() ;

			circle.headPS( ps ) ;
			circle.emitPS( ps ) ;
			circle.tailPS( ps ) ;

			ps.operator.grestore() ;
		}

		for ( int bd=0 ; bd<getBodyCount() ; bd++ ) {
			PostscriptEmitter body ;

			try {
				body = AstrolabeFactory.companionOf( getBody( bd ), this ) ;
			} catch ( ParameterNotValidException e ) {
				throw new RuntimeException( e.toString() ) ;
			}

			ps.operator.gsave() ;

			body.headPS( ps ) ;
			body.emitPS( ps ) ;
			body.tailPS( ps ) ;

			ps.operator.grestore() ;
		}

		for ( int ct=0 ; ct<getCatalogCount() ; ct++ ) {
			PostscriptEmitter catalog ;

			try {
				catalog = AstrolabeFactory.companionOf( getCatalog( ct ), this ) ;
			} catch ( ParameterNotValidException e ) {
				throw new RuntimeException( e.toString() ) ;
			}

			ps.operator.gsave() ;

			catalog.headPS( ps ) ;
			catalog.emitPS( ps ) ;
			catalog.tailPS( ps ) ;

			ps.operator.grestore() ;
		}
	}


	public void tailPS( PostscriptStream ps ) {
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
