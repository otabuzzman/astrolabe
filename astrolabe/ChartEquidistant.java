
package astrolabe;

import org.exolab.castor.xml.ValidationException;

@SuppressWarnings("serial")
public class ChartEquidistant extends ChartAzimuthalType implements PostscriptEmitter, Projector {

	public ChartEquidistant( Peer peer ) {
		super( peer ) ;
	}

	public void emitPS( AstrolabePostscriptStream ps ) {
		AtlasEquidistant atlas ;

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
				atlas = new AtlasEquidistant( this, getAtlas() ) ;
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
		return ( 90-de )/90 ;
	}

	public double distanceToTheta( double d ) {
		return 90-d*90 ;
	}
}
