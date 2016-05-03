
package astrolabe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.vividsolutions.jts.geom.Coordinate;

import caa.CAA2DCoordinate;
import caa.CAACoordinateTransformation;
import caa.CAANutation;

@SuppressWarnings("serial")
public class BodyPlanet extends BodyOrbitalType {

	// configuration key (CK_)
	private final static String CK_STRETCH			= "stretch" ;

	private final static double DEFAULT_STRETCH		= 0 ;


	private astrolabe.model.BodyPlanet peer ;	

	private double epoch ;

	public BodyPlanet( astrolabe.model.BodyPlanet peer, Converter converter, Projector projector ) {
		super( converter, projector ) ;

		Double Epoch ;

		this.peer = peer ;

		Epoch = (Double) Registry.retrieve( Epoch.class.getName() ) ;
		if ( Epoch == null )
			epoch = astrolabe.Epoch.defoult() ;
		epoch = Epoch.doubleValue() ;
	}

	public Coordinate jdToEquatorial( double jd ) {
		double l, b, o ;
		Class<?> c ;
		double stretch ;
		CAA2DCoordinate c2d ;
		Method eclipticLongitude ;
		Method eclipticLatitude ;

		l = 0 ;
		b = 0 ;

		if ( getStretch() )
			stretch = Configuration.getValue( this, CK_STRETCH, DEFAULT_STRETCH ) ;
		else
			stretch = 0 ;

		try {
			c = Class.forName( "caa.CAA"+peer.getType().substring( 0, 1 ).toUpperCase()+peer.getType().substring( 1 ) ) ;

			eclipticLongitude = c.getMethod( "EclipticLongitude", new Class[] { double.class } ) ;
			eclipticLatitude = c.getMethod( "EclipticLatitude", new Class[] { double.class } ) ;

			l = (Double) eclipticLongitude.invoke( null, new Object[] { new Double( jd ) } ) ;
			b = (Double) eclipticLatitude.invoke( null, new Object[] { new Double( jd ) } )
			+( jd-epoch()[0] )*stretch ;
		} catch ( ClassNotFoundException e ) {
			throw new RuntimeException( e.toString() ) ;
		} catch ( NoSuchMethodException e ) {
			throw new RuntimeException( e.toString() ) ;
		} catch ( InvocationTargetException e ) {
			throw new RuntimeException( e.toString() ) ;
		} catch ( IllegalAccessException e ) {
			throw new RuntimeException( e.toString() ) ;
		}

		o = CAANutation.MeanObliquityOfEcliptic( epoch ) ;
		c2d = CAACoordinateTransformation.Ecliptic2Equatorial( l, b, o ) ;

		return new Coordinate( CAACoordinateTransformation.HoursToDegrees( c2d.X() ), c2d.Y() ) ;
	}
}
