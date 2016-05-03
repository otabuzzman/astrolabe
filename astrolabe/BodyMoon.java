
package astrolabe;

import com.vividsolutions.jts.geom.Coordinate;

import caa.CAA2DCoordinate;
import caa.CAACoordinateTransformation;
import caa.CAAMoon;
import caa.CAANutation;

@SuppressWarnings("serial")
public class BodyMoon extends BodyOrbitalType {

	// configuration key (CK_)
	private final static String CK_STRETCH			= "stretch" ;

	private final static double DEFAULT_STRETCH		= 0 ;

	private double epoch ;

	public BodyMoon( Converter converter, Projector projector ) {
		super( converter, projector ) ;

		Double Epoch ;

		Epoch = (Double) Registry.retrieve( Epoch.class.getName() ) ;
		if ( Epoch == null )
			epoch = astrolabe.Epoch.defoult() ;
		epoch = Epoch.doubleValue() ;
	}

	public Coordinate jdToEquatorial( double jd ) {
		double l, b, o ;
		CAA2DCoordinate c ;
		double stretch ;

		if ( getStretch() )
			stretch = Configuration.getValue( this, CK_STRETCH, DEFAULT_STRETCH ) ;
		else
			stretch = 0 ;

		l = CAAMoon.EclipticLongitude( jd ) ;
		b = CAAMoon.EclipticLatitude( jd )
		+( jd-epoch()[0] )*stretch ;

		o = CAANutation.MeanObliquityOfEcliptic( epoch ) ;
		c = CAACoordinateTransformation.Ecliptic2Equatorial( l, b, o ) ;

		return new Coordinate( CAACoordinateTransformation.HoursToDegrees( c.X() ), c.Y() ) ;
	}
}
