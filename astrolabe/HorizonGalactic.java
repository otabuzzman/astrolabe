
package astrolabe;

import com.vividsolutions.jts.geom.Coordinate;

import caa.CAA2DCoordinate;
import caa.CAACoordinateTransformation;

@SuppressWarnings("serial")
public class HorizonGalactic extends HorizonType {

	// qualifier key (QK_)
	private final static String QK_SIDEREAL	= "sidereal" ;
	private final static String QK_LATITUDE	= "latitude" ;

	public HorizonGalactic( Projector projector ) {
		super( projector ) ;
	}

	public void register() {
		CAA2DCoordinate c ;
		DMS dms ;

		c = CAACoordinateTransformation.Galactic2Equatorial( 0, 90 ) ;

		dms = new DMS( c.X() ) ;
		dms.register( this, QK_SIDEREAL ) ;
		dms.set( c.Y(), -1 ) ;
		dms.register( this, QK_LATITUDE ) ;
	}

	public void degister() {
		DMS.degister( this, QK_SIDEREAL ) ;
		DMS.degister( this, QK_LATITUDE ) ;
	}

	public Coordinate convert( Coordinate local, boolean inverse ) {
		return inverse ? inverse( local ) : convert( local ) ;
	}

	private Coordinate convert( Coordinate local ) {
		CAA2DCoordinate c ;

		c = CAACoordinateTransformation.Galactic2Equatorial( local.x, local.y ) ;

		return new Coordinate( CAACoordinateTransformation.HoursToDegrees( c.X() ), c.Y() ) ;
	}

	private Coordinate inverse( Coordinate equatorial ) {
		return new astrolabe.Coordinate(
				CAACoordinateTransformation.Equatorial2Galactic(
						CAACoordinateTransformation.DegreesToHours( equatorial.x ), equatorial.y ) ) ;
	}
}
