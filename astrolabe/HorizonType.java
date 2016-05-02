
package astrolabe;

@SuppressWarnings("serial")
abstract public class HorizonType extends astrolabe.model.HorizonType {

	public HorizonType( Peer peer, Projector projector ) {
		peer.setupCompanion( this ) ;
	}

	public void headPS( AstrolabePostscriptStream ps ) {
		GSPaintColor practicality ;

		practicality = new GSPaintColor( getPracticality() ) ;
		practicality.headPS( ps ) ;
		practicality.emitPS( ps ) ;
		practicality.tailPS( ps ) ;
	}

	public void emitPS( AstrolabePostscriptStream ps ) {
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
