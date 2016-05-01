
package astrolabe;

@SuppressWarnings("serial")
public class ChartStereographic extends ChartAzimuthalType implements PostscriptEmitter, Projector {

	public ChartStereographic( Peer peer ) {
		super( peer ) ;
	}

	public void emitPS( AstrolabePostscriptStream ps ) {
		super.emitPS( ps ) ;

		for ( int ho=0 ; ho<getHorizonCount() ; ho++ ) {
			PostscriptEmitter horizon ;

			horizon = AstrolabeFactory.companionOf( getHorizon( ho ), this ) ;

			ps.operator.gsave() ;

			horizon.headPS( ps ) ;
			horizon.emitPS( ps ) ;
			horizon.tailPS( ps ) ;

			ps.operator.grestore() ;
		}
	}

	public double thetaToDistance( double de ) {
		return Math.tan( ( 90-de )/2 ) ;
	}

	public double distanceToTheta( double d ) {
		return 90-Math.atan( d )*2 ;
	}
}
