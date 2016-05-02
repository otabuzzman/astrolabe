
package astrolabe;

import caa.CAA2DCoordinate;
import caa.CAACoordinateTransformation;

@SuppressWarnings("serial")
public class HorizonGalactic extends astrolabe.model.HorizonGalactic implements PostscriptEmitter, Projector {

	private Projector projector ;

	private double la ;
	private double ST ;

	public HorizonGalactic( Peer peer, Projector projector ) {
		peer.setupCompanion( this ) ;

		CAA2DCoordinate c ;
		double[] eq = new double[2] ;
		MessageCatalog m ;
		String key ;

		this.projector = projector ;

		c = CAACoordinateTransformation.Galactic2Equatorial( 0, 90 ) ;
		eq[0] = CAACoordinateTransformation.HoursToDegrees( c.X() ) ;
		eq[1] = c.Y() ;
		la = eq[1] ;
		ST = eq[0] ;

		m = new MessageCatalog( ApplicationConstant.GC_APPLICATION ) ;

		key = m.message( ApplicationConstant.LK_HORIZON_LATITUDE ) ;
		AstrolabeRegistry.registerDMS( key, la ) ;		
		key = m.message( ApplicationConstant.LK_HORIZON_TIMESIDEREAL ) ;
		AstrolabeRegistry.registerHMS( key, ST ) ;
	}

	public void headPS( AstrolabePostscriptStream ps ) {
		GSPaintColor practicality ;

		practicality = new GSPaintColor( getPracticality(), getName() ) ;
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
	}

	public void tailPS( AstrolabePostscriptStream ps ) {
	}

	public double[] project( double[] ho ) {
		return project( ho[0], ho[1] ) ;
	}

	public double[] project( double l, double b ) {
		return projector.project( convert( l, b ) ) ;
	}

	public double[] unproject( double[] xy ) {
		return unproject( xy[0], xy[1] ) ;
	}

	public double[] unproject( double x, double y ) {
		return unconvert( projector.unproject( x, y ) ) ;
	}

	public double[] convert( double[] ho ) {
		return convert( ho[0], ho[1] ) ;
	}

	public double[] convert( double l, double b ) {
		CAA2DCoordinate c ;
		double[] r = new double[2];

		c = CAACoordinateTransformation.Galactic2Equatorial( l, b ) ;
		r[0] = CAACoordinateTransformation.HoursToDegrees( c.X() ) ;
		r[1] = c.Y() ;

		return r ;
	}

	public double[] unconvert( double[] eq ) {
		return unconvert( eq[0], eq[1] ) ;
	}

	public double[] unconvert( double RA, double d ) {
		CAA2DCoordinate c ;
		double[] r = new double[2];

		c = CAACoordinateTransformation.Equatorial2Galactic( CAACoordinateTransformation.DegreesToHours( RA ), d ) ;
		r[0] = c.X() ;
		r[1] = c.Y() ;

		return r ;
	}
}
