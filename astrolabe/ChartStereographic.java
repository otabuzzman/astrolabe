
package astrolabe;

import org.exolab.castor.xml.ValidationException;

@SuppressWarnings("serial")
public class ChartStereographic extends ChartAzimuthalType implements PostscriptEmitter, Projector {

	public void emitPS( AstrolabePostscriptStream ps ) {
		ChartPage page ;
		double[] view ;
		AtlasStereographic atlas ;

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
			page = new ChartPage() ;
			getChartPage().setupCompanion( page ) ;

			view = page.view() ;

			try {
				atlas = new AtlasStereographic( getAtlas(), new double[] { view[0], view[1] }, getNorthern(), this ) ;
				atlas.addAllAtlasPage() ;
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
		return Math.tan( ( 90-de )/2 ) ;
	}

	public double distanceToTheta( double d ) {
		return 90-Math.atan( d )*2 ;
	}
}
