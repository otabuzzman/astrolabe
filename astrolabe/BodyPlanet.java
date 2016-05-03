
package astrolabe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;

import caa.CAA2DCoordinate;
import caa.CAACoordinateTransformation;
import caa.CAANutation;

@SuppressWarnings("serial")
public class BodyPlanet extends BodyOrbitalType {

	// configuration key (CK_)
	private final static String CK_INTERVAL			= "interval" ;
	private static final String CK_DISTANCE			= "distance" ;
	private final static String CK_STRETCH			= "stretch" ;

	private final static double DEFAULT_INTERVAL	= 1 ;
	private static final double DEFAULT_DISTANCE	= 0 ;
	private final static double DEFAULT_STRETCH		= 0 ;


	private astrolabe.model.BodyPlanet peer ;	
	private Projector projector ;

	private double epoch ;

	public BodyPlanet( astrolabe.model.BodyPlanet peer, Converter converter, Projector projector ) {
		super( converter, projector ) ;

		Double Epoch ;

		this.peer = peer ;
		this.projector = projector ;

		Epoch = (Double) Registry.retrieve( Epoch.class.getName() ) ;
		if ( Epoch == null )
			epoch = astrolabe.Epoch.defoult() ;
		epoch = Epoch.doubleValue() ;
	}

	public Coordinate positionOfScaleMarkValue( double jd, double shift ) {
		Coordinate eq, xy ;
		Vector v, t ;

		eq = jdToEquatorial( jd ) ;
		xy = projector.project( eq, false ) ;
		v = new Vector( xy  ) ;

		if ( shift != 0 ) {
			xy = directionOfScaleMarkValue( jd ) ;
			t = new Vector( xy ) ;
			t.apply( new double[] { 0, -1, 0, 1, 0, 0, 0, 0, 1 } ) ; // rotate 90 degrees counter clockwise
			t.scale( shift ) ;
			v.add( t ) ;
		}

		return new Coordinate( v.x, v.y ) ;
	}

	public Coordinate directionOfScaleMarkValue( double jd ) {
		Coordinate eq, xy ;
		Vector v, t ;

		eq = jdToEquatorial( jd+1./86400 ) ;
		xy = projector.project( eq, false ) ;
		v = new Vector( xy ) ;
		eq = jdToEquatorial( jd ) ;
		xy = projector.project( eq, false ) ;
		t = new Vector( xy ) ;

		v.sub( t ) ;

		return new Coordinate( v.x, v.y ) ;
	}

	public double valueOfScaleMarkN( int mark, double span ) {
		return new LinearScale( span, epoch() ).markN( mark ) ;
	}

	public Coordinate[] list( final List<Double> listjd, double jdA, double jdO, double shift ) {
		List<Coordinate> listxy ;
		double interval ;
		double d, e, g, dist ;

		interval = Configuration.getValue( this, CK_INTERVAL, DEFAULT_INTERVAL ) ;

		listxy = new java.util.Vector<Coordinate>() ;

		listxy.add( positionOfScaleMarkValue( jdA, shift ) ) ;
		if ( listjd != null )
			listjd.add( jdA ) ;

		d = jdO-jdA ;
		e = d-(int) ( d/interval )*interval ;
		g = ( Math.isLim0( e )?interval:e )/2 ;

		for ( double jd=jdA+g ; jd<jdO ; jd=jd+interval ) {
			listxy.add( positionOfScaleMarkValue( jd, shift ) ) ;
			if ( listjd != null )
				listjd.add( jd ) ;
		}

		listxy.add( positionOfScaleMarkValue( jdO, shift ) ) ;
		if ( listjd != null )
			listjd.add( jdO ) ;

		dist = Configuration.getValue( this, CK_DISTANCE, DEFAULT_DISTANCE ) ;
		if ( dist>0 && listxy.size()>2 )
			return DouglasPeuckerSimplifier.simplify( new GeometryFactory().createLineString( listxy.toArray( new Coordinate[0] ) ), dist ).getCoordinates() ;
		else
			return listxy.toArray( new Coordinate[0] ) ;
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
