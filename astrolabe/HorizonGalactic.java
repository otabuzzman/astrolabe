
package astrolabe;

import caa.CAA2DCoordinate;
import caa.CAACoordinateTransformation;

@SuppressWarnings("serial")
public class HorizonGalactic extends HorizonType {

	private Projector projector ;

	private double la ;
	private double ST ;

	public HorizonGalactic( Peer peer, Projector projector ) {
		super( peer, projector ) ;

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

	public double[] project( double l, double b ) {
		return projector.project( convert( l, b ) ) ;
	}

	public double[] unproject( double x, double y ) {
		return unconvert( projector.unproject( x, y ) ) ;
	}

	public double[] convert( double l, double b ) {
		CAA2DCoordinate c ;
		double[] r = new double[2];

		c = CAACoordinateTransformation.Galactic2Equatorial( l, b ) ;
		r[0] = CAACoordinateTransformation.HoursToDegrees( c.X() ) ;
		r[1] = c.Y() ;

		return r ;
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
