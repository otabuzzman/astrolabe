
package astrolabe;

import com.vividsolutions.jts.geom.Coordinate;

import caa.CAA2DCoordinate;
import caa.CAACoordinateTransformation;
import caa.CAANutation;

@SuppressWarnings("serial")
public class HorizonEcliptical extends HorizonType {

	// qualifier key (QK_)
	private final static String QK_EPSILON	= "epsilon" ;
	private final static String QK_LATITUDE	= "latitude" ;

	private double epoch ;

	public HorizonEcliptical( Projector projector ) {
		super( projector ) ;

		Epoch  epoch ;

		epoch = (Epoch) Registry.retrieve( Epoch.class.getName() ) ;
		if ( epoch == null )
			this.epoch = new Epoch().alpha() ;
		this.epoch = epoch.alpha() ;
	}

	public void register() {
		CAA2DCoordinate c ;
		double e ;
		DMS dms ;

		e = CAANutation.MeanObliquityOfEcliptic( epoch ) ;
		c = CAACoordinateTransformation.Ecliptic2Equatorial( 0, 90, e ) ;

		dms = new DMS( e ) ;
		dms.register( this, QK_EPSILON ) ;
		dms.set( c.Y(), -1 ) ;
		dms.register( this, QK_LATITUDE ) ;
	}

	public void degister() {
		DMS.degister( this, QK_EPSILON ) ;
		DMS.degister( this, QK_LATITUDE ) ;
	}

	public Coordinate convert( Coordinate local, boolean inverse ) {
		return inverse ? inverse( local ) : convert( local ) ;
	}

	private Coordinate convert( Coordinate local ) {
		CAA2DCoordinate c ;
		double e ;

		e = CAANutation.MeanObliquityOfEcliptic( epoch ) ;
		c = CAACoordinateTransformation.Ecliptic2Equatorial( local.x, local.y, e ) ;

		return new Coordinate( CAACoordinateTransformation.HoursToDegrees( c.X() ), c.Y() ) ;
	}

	private Coordinate inverse( Coordinate equatorial ) {
		double e ;

		e = CAANutation.MeanObliquityOfEcliptic( epoch ) ;

		return new astrolabe.Coordinate(
				CAACoordinateTransformation.Equatorial2Ecliptic(
						CAACoordinateTransformation.DegreesToHours( equatorial.x ), equatorial.y, e ) ) ;
	}
}
