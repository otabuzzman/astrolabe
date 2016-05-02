
package astrolabe;

import java.util.Calendar;

import caa.CAA2DCoordinate;
import caa.CAACoordinateTransformation ;
import caa.CAADate;
import caa.CAANutation;

@SuppressWarnings("serial")
public class HorizonLocal extends astrolabe.model.HorizonLocal implements PostscriptEmitter, Projector {

	private Projector projector ;

	public HorizonLocal( Projector projector ) {
		this.projector = projector ;
	}

	public double date() {
		Calendar calendar ;
		CAADate datetime ;
		double jd ;

		if ( getDate() == null ) {
			calendar = Calendar.getInstance() ;

			datetime = new CAADate(
					calendar.get( Calendar.YEAR ),
					calendar.get( Calendar.MONTH ),
					calendar.get( Calendar.DAY_OF_MONTH ),
					calendar.get( Calendar.HOUR_OF_DAY ),
					calendar.get( Calendar.MINUTE ), 0, true ) ;
			jd = datetime.Julian() ;
			datetime.delete() ;

			return jd ;
		} else
			return AstrolabeFactory.valueOf( getDate() ) ;
	}

	public double longitude() {
		if ( getLongitude() == null )
			return 0 ;
		else
			return AstrolabeFactory.valueOf( getLongitude() ) ;
	}

	public double getLT( double jd ) {
		double lo, lt ;
		CAADate d ;

		d = new CAADate( jd, true ) ;

		lt = CAACoordinateTransformation.HoursToDegrees( d.Hour()+d.Minute()/60.+d.Second()/3600 ) ;
		lo = longitude() ;

		d.delete() ;

		return lt+( lo>180?lo-360:lo ) ;
	}

	public double getST( double jd ) {
		double la0, lo0, epoch, e, ra0, lt ;
		CAA2DCoordinate c ;

		la0 = BodySun.meanEclipticLatitude( jd ) ;
		lo0 = BodySun.meanEclipticLongitude( jd ) ;
		epoch = ( (Double) Registry.retrieve( ApplicationConstant.GC_EPOCH ) ).doubleValue() ; 
		e = CAANutation.MeanObliquityOfEcliptic( epoch ) ;

		c = CAACoordinateTransformation.Ecliptic2Equatorial( lo0, la0, e ) ;
		ra0 = CAACoordinateTransformation.HoursToDegrees( c.X() ) ;

		lt = getLT( jd ) ;

		return CAACoordinateTransformation.MapTo0To360Range( ra0+lt-180/*12h*/ ) ;
	}

	public void register() {
		MessageCatalog m ;
		String key ;
		double jd, la ;

		m = new MessageCatalog( ApplicationConstant.GC_APPLICATION ) ;

		jd = date() ;
		key = m.message( ApplicationConstant.LK_HORIZON_TIMELOCAL ) ;
		AstrolabeRegistry.registerHMS( key, getLT( jd ) ) ;
		key = m.message( ApplicationConstant.LK_HORIZON_TIMESIDEREAL ) ;
		AstrolabeRegistry.registerHMS( key, getST( jd ) ) ;

		la = AstrolabeFactory.valueOf( getLatitude() ) ;
		key = m.message( ApplicationConstant.LK_HORIZON_LATITUDE ) ;
		AstrolabeRegistry.registerDMS( key, la ) ;
		key = m.message( ApplicationConstant.LK_HORIZON_LONGITUDE ) ;
		AstrolabeRegistry.registerDMS( key, longitude() ) ;
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
			AnnotationStraight annotation ;

			annotation = new AnnotationStraight() ;
			getAnnotationStraight( an ).setupCompanion( annotation ) ;
			annotation.register() ;

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

	public double[] project( double A, double h ) {
		return projector.project( convert( A, h ) ) ;
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

	public double[] convert( double A, double h ) {
		double[] r = new double[2] ;
		CAA2DCoordinate c ;
		double la ;

		la = AstrolabeFactory.valueOf( getLatitude() ) ;

		c = CAACoordinateTransformation.Horizontal2Equatorial( A, h, la ) ;
		r[0] = CAACoordinateTransformation.HoursToDegrees( c.X() ) ;
		r[1] = c.Y() ;

		// r[0] is HA is ST-lo-RA.
		r[0] = getST( date() )-r[0] ;

		return r ;
	}

	public double[] unconvert( double[] eq ) {
		return unconvert( eq[0], eq[1] ) ;
	}

	public double[] unconvert( double RA, double d ) {
		double[] r = new double[2];
		CAA2DCoordinate c ;
		double la ;

		la = AstrolabeFactory.valueOf( getLatitude() ) ;

		c = CAACoordinateTransformation.Equatorial2Horizontal(
				CAACoordinateTransformation.DegreesToHours( getST( date() )-RA ), d, la ) ;
		r[0] = c.X() ;
		r[1] = c.Y() ;

		return r ;
	}
}
