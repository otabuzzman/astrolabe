
package astrolabe;

import caa.CAA2DCoordinate;
import caa.CAACoordinateTransformation ;
import caa.CAADate;
import caa.CAANutation;

@SuppressWarnings("serial")
public class HorizonLocal extends HorizonType {

	private Projector projector ;

	private double la ;
	private double ST ;
	private double lo ;

	public HorizonLocal( Peer peer, Projector projector ) {
		super( peer, projector ) ;

		String key ;
		MessageCatalog m ;
		double epoch ;

		this.projector = projector ;

		m = new MessageCatalog( ApplicationConstant.GC_APPLICATION ) ;

		la = AstrolabeFactory.valueOf( ( (astrolabe.model.HorizonLocal) peer ).getLatitude() ) ;
		key = m.message( ApplicationConstant.LK_HORIZON_LATITUDE ) ;
		AstrolabeRegistry.registerDMS( key, la ) ;

		if ( ( (astrolabe.model.HorizonLocal) peer ).getLongitude() == null ) {
			lo = 0 ;
		} else {
			lo = AstrolabeFactory.valueOf( ( (astrolabe.model.HorizonLocal) peer ).getLongitude() ) ;
			key = m.message( ApplicationConstant.LK_HORIZON_LONGITUDE ) ;
			AstrolabeRegistry.registerDMS( key, lo ) ;
		}

		if ( ( (astrolabe.model.HorizonLocal) peer ).getDate() == null ) {
			ST = 0 ;
		} else {
			CAA2DCoordinate c ;
			double lt, ra0, lo0, la0, e, jd ;
			CAADate d ;

			jd = AstrolabeFactory.valueOf( ( (astrolabe.model.HorizonLocal) peer ).getDate() ) ;
			d = new CAADate( jd, true ) ;

			lt = CAACoordinateTransformation.HoursToDegrees( d.Hour()+d.Minute()/60.+d.Second()/3600 ) ;

			d.delete() ;

			lt = lt+( lo>180?lo-360:lo ) ;
			key = m.message( ApplicationConstant.LK_HORIZON_TIMELOCAL ) ;
			AstrolabeRegistry.registerHMS( key, lt ) ;

			lo0 = BodySun.meanEclipticLongitude( jd ) ;
			la0 = BodySun.meanEclipticLatitude( jd ) ;
			epoch = ( (Double) AstrolabeRegistry.retrieve( ApplicationConstant.GC_EPOCHE ) ).doubleValue() ; 
			e = CAANutation.MeanObliquityOfEcliptic( epoch ) ;

			c = CAACoordinateTransformation.Ecliptic2Equatorial( lo0, la0, e ) ;
			ra0 = CAACoordinateTransformation.HoursToDegrees( c.X() ) ;

			ST = CAACoordinateTransformation.MapTo0To360Range( ra0+lt-180/*12h*/ ) ;
			key = m.message( ApplicationConstant.LK_HORIZON_TIMESIDEREAL ) ;
			AstrolabeRegistry.registerHMS( key, ST ) ;
		}
	}

	public double[] project( double A, double h ) {
		return projector.project( convert( A, h ) ) ;
	}

	public double[] unproject( double x, double y ) {
		return unconvert( projector.unproject( x, y ) ) ;
	}

	public double[] convert( double A, double h ) {
		CAA2DCoordinate c ;
		double[] r = new double[2] ;

		c = CAACoordinateTransformation.Horizontal2Equatorial( A, h, la ) ;
		r[0] = CAACoordinateTransformation.HoursToDegrees( c.X() ) ;
		r[1] = c.Y() ;

		// r[0] is HA is ST-lo-RA.
		r[0] = ST-r[0] ;

		return r ;
	}

	public double[] unconvert( double RA, double d ) {
		CAA2DCoordinate c ;
		double[] r = new double[2];

		c = CAACoordinateTransformation.Equatorial2Horizontal(
				CAACoordinateTransformation.DegreesToHours( this.ST-RA ), d, la ) ;
		r[0] = c.X() ;
		r[1] = c.Y() ;

		return r ;
	}
}
