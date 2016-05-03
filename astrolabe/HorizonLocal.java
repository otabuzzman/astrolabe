
package astrolabe;

import com.vividsolutions.jts.geom.Coordinate;

import caa.CAA2DCoordinate;
import caa.CAACoordinateTransformation ;
import caa.CAASidereal;

@SuppressWarnings("serial")
public class HorizonLocal extends HorizonType {

	// qualifier key (QK_)
	private final static String QK_LOCALTIME	= "localtime" ;
	private final static String QK_SIDEREAL		= "sidereal" ;

	private astrolabe.model.HorizonLocal peer ;

	public HorizonLocal( astrolabe.model.HorizonLocal peer, Projector projector ) {
		super( projector ) ;

		this.peer = peer ;
	}

	public double longitude() {
		double lo ;

		lo = valueOf( peer.getLongitude() ) ;

		if ( lo>180 )
			while ( lo>180 )
				lo = lo-180 ;
		if ( -180>lo )
			while ( -180>lo )
				lo = lo+180 ;

		return lo ;
	}

	public double latitude() {
		double la ;

		la = valueOf( peer.getLatitude() ) ;

		if ( la>90 )
			while ( la>90 )
				la = la-90 ;
		if ( -90>la )
			while ( -90>la )
				la = la+90 ;

		return la ;
	}

	public double getST() {
		double jd0, gmst ;
		Double Epoch ;
		double epoch ;

		Epoch = (Double) Registry.retrieve( Epoch.class.getName() ) ;
		if ( Epoch == null )
			epoch = astrolabe.Epoch.defoult() ;
		else
			epoch = Epoch.doubleValue() ;

		jd0 = (int) ( epoch-.5 )+.5 ;
		gmst = CAASidereal.MeanGreenwichSiderealTime( jd0+utc()/24 ) ;

		return gmst+longitude()/15 ;
	}

	private double standard() {
		String utc[] ;
		int h, m ;
		double t ;

		utc = peer.getTime().getStandard()
		.substring( 4 )
		.split( ":" ) ;
		h = Integer.parseInt( utc[0] )%24 ;
		m = 0 ;
		if ( utc.length>1 )
			m = Integer.parseInt( utc[1] )%60 ;
		t = h+m/60. ;

		return peer.getTime().getStandard().charAt( 3 ) == '-' ? -t : t ;
	}

	private double utc() {
		return CAACoordinateTransformation.MapTo0To24Range( valueOf( peer.getTime() )-standard() ) ;
	}

	public void register() {
		DMS dms ;

		dms = new DMS( utc()+longitude()/15 ) ;
		dms.register( this, QK_LOCALTIME ) ;
		dms.set( getST(), -1 ) ;
		dms.register( this, QK_SIDEREAL ) ;
	}

	public void degister() {
		DMS.degister( this, QK_LOCALTIME ) ;
		DMS.degister( this, QK_SIDEREAL ) ;
	}

	public Coordinate convert( Coordinate local, boolean inverse ) {
		return inverse ? inverse( local ) : convert( local ) ;
	}

	private Coordinate convert( Coordinate local ) {
		CAA2DCoordinate c ;

		c = CAACoordinateTransformation.Horizontal2Equatorial( local.x, local.y, latitude() ) ;

		return new Coordinate( CAACoordinateTransformation.HoursToDegrees( getST()-c.X() ), c.Y() ) ;
	}

	private Coordinate inverse( Coordinate equatorial ) {
		double st, ra ;

		st = Math.truncate( getST() ) ;
		ra = Math.truncate( CAACoordinateTransformation.DegreesToHours( equatorial.x ) ) ;

		return new astrolabe.Coordinate(
				CAACoordinateTransformation.Equatorial2Horizontal( st-ra, equatorial.y, latitude() ) ) ;
	}
}
