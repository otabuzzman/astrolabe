
package astrolabe;

import org.exolab.castor.xml.ValidationException;

@SuppressWarnings("serial")
public class ChartOrthographic extends ChartAzimuthalType implements PostscriptEmitter, Projector {

	public ChartOrthographic( Peer peer ) {
		super( peer ) ;
	}

	public void emitPS( AstrolabePostscriptStream ps ) {
		AtlasOrthographic atlas ;

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

		if ( getAtlas() != null ) {
			try {
				atlas = new AtlasOrthographic( this, getAtlas() ) ;
				atlas.addAllAtlasPage( new double[] { viewx, viewy }, getNorthern() ) ;
			} catch ( ValidationException e ) {
				throw new RuntimeException( e.toString() ) ;
			}

			atlas.headAUX() ;
			atlas.emitAUX() ;
			atlas.tailAUX() ;

			ps.operator.gsave() ;

			atlas.headPS( ps ) ;
			atlas.emitPS( ps ) ;
			atlas.tailPS( ps ) ;

			ps.operator.grestore() ;
		}
	}

	public double thetaToDistance( double de ) {
		return Math.cos( de ) ;
	}

	public double distanceToTheta( double d ) {
		return Math.acos( d ) ;
	}
}
