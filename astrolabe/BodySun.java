
package astrolabe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.vividsolutions.jts.geom.Coordinate;

import caa.CAA2DCoordinate;
import caa.CAACoordinateTransformation;
import caa.CAANutation;
import caa.CAASun;

@SuppressWarnings("serial")
public class BodySun extends BodyOrbitalType {

	// configuration key (CK_)
	private final static String CK_STRETCH			= "stretch" ;

	private final static double DEFAULT_STRETCH		= 0 ;

	private astrolabe.model.BodySun peer ;	

	private double epoch ;

	public BodySun( astrolabe.model.BodySun peer, Converter converter, Projector projector ) {
		super( converter, projector ) ;

		Double Epoch ;

		this.peer = peer ;

		Epoch = (Double) Registry.retrieve( Epoch.class.getName() ) ;
		if ( Epoch == null )
			epoch = astrolabe.Epoch.defoult() ;
		epoch = Epoch.doubleValue() ;
	}

	public Coordinate positionOfScaleMarkValue( double jd, double shift ) {
		String circle ;
		Baseline base ;
		double a ;

		if ( ( circle = peer.getCircle() ) != null )
			base = (Baseline) Registry.retrieve( circle ) ;
		else
			base = null ;

		if ( base == null )
			return super.positionOfScaleMarkValue( jd, shift ) ;

		a = angle( jd ) ;
		return base.positionOfScaleMarkValue( a, shift ) ;
	}

	public Coordinate directionOfScaleMarkValue( double jd ) {
		String circle ;
		Baseline base ;
		double a ;

		if ( ( circle = peer.getCircle() ) != null )
			base = (Baseline) Registry.retrieve( circle ) ;
		else
			base = null ;

		if ( base == null )
			return super.directionOfScaleMarkValue( jd ) ;

		a = angle( jd ) ;
		return base.directionOfScaleMarkValue( a ) ;
	}

	private double angle( double jd ) {
		Object circle ;
		Coordinate eq, lo ;

		eq = jdToEquatorial( jd ) ;

		circle = Registry.retrieve( peer.getCircle() ) ;
		lo = ( (Converter) circle ).convert( eq, true ) ;

		if ( circle instanceof CircleMeridian )
			return lo.y ;
		return lo.x ;
	}

	public Coordinate jdToEquatorial( double jd ) {
		double l, b, o ;
		double stretch ;
		CAA2DCoordinate c ;
		Method eclipticLongitude ;
		Method eclipticLatitude ;

		l = 0 ;
		b = 0 ;

		if ( getStretch() )
			stretch = Configuration.getValue( this, CK_STRETCH, DEFAULT_STRETCH ) ;
		else
			stretch = 0 ;

		try {
			eclipticLongitude = getClass().getMethod( peer.getType()+"EclipticLongitude", new Class[] { double.class } ) ;
			eclipticLatitude = getClass().getMethod( peer.getType()+"EclipticLatitude", new Class[] { double.class } ) ;

			l = (Double) eclipticLongitude.invoke( null, new Object[] { new Double( jd ) } ) ;
			b = (Double) eclipticLatitude.invoke( null, new Object[] { new Double( jd ) } )
			+( jd-epoch()[0] )*stretch ;
		} catch ( NoSuchMethodException e ) {
			throw new RuntimeException( e.toString() ) ;
		} catch ( InvocationTargetException e ) {
			throw new RuntimeException( e.toString() ) ;
		} catch ( IllegalAccessException e ) {
			throw new RuntimeException( e.toString() ) ;
		}

		o = CAANutation.MeanObliquityOfEcliptic( epoch ) ;
		c = CAACoordinateTransformation.Ecliptic2Equatorial( l, b, o ) ;

		return new Coordinate( CAACoordinateTransformation.HoursToDegrees( c.X() ), c.Y() ) ;
	}

	public static double meanEclipticLongitude( double JD ) {
		double rho, rho2, rho3, rho4, rho5 ;

		rho = ( JD-2451545 )/365250 ;
		rho2 = rho*rho ;
		rho3 = rho2*rho ;
		rho4 = rho3*rho ;
		rho5 = rho4*rho ;

		return CAACoordinateTransformation.MapTo0To360Range(
				280.4664567+360007.6982779*rho+0.03032028*rho2+rho3/49931-rho4/15300-rho5/2000000 ) ;
	}

	public static double meanEclipticLatitude( double JD ) {
		return 0 ;
	}

	public static double trueEclipticLongitude( double JD ) {
		return CAASun.GeometricEclipticLongitude( JD ) ;
	}

	public static double trueEclipticLatitude( double JD ) {
		return CAASun.GeometricEclipticLatitude( JD ) ;
	}
}
