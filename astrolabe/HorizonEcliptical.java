
package astrolabe;

import caa.CAA2DCoordinate;
import caa.CAACoordinateTransformation;
import caa.CAANutation;

@SuppressWarnings("serial")
public class HorizonEcliptical extends HorizonType {

	private Projector projector ;

	private double la ;

	private double e ; // mean obliquity of ecliptic

	public HorizonEcliptical( Object peer, Projector projector ) throws ParameterNotValidException {
		super( peer, projector ) ;

		CAA2DCoordinate c ;
		double epoch, eq[] = new double[2] ;
		String key ;

		this.projector = projector ;

		epoch = ( (Double) Registry.retrieve( ApplicationConstant.GC_EPOCHE ) ).doubleValue() ;
		e = CAANutation.MeanObliquityOfEcliptic( epoch ) ;
		c = CAACoordinateTransformation.Ecliptic2Equatorial( 0, 90, e ) ;
		eq[0] = CAACoordinateTransformation.HoursToDegrees( c.X() ) ;
		eq[1] = c.Y() ;
		la = eq[1] ;

		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_HORIZON_ECLIPTICEPSILON ) ;
		ApplicationHelper.registerDMS( key, e ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_HORIZON_LATITUDE ) ;
		ApplicationHelper.registerDMS( key, la ) ;		
	}

	public double[] project( double l, double b ) {
		return projector.project( convert( l, b ) ) ;
	}

	public double[] unproject( double x, double y ) {
		return unconvert( projector.unproject( x, y ) ) ;
	}

	public double[] convert( double l, double b ) {
		CAA2DCoordinate c ;
		double[] r = new double[2] ;

		c = CAACoordinateTransformation.Ecliptic2Equatorial( l, b, e ) ;
		r[0] = CAACoordinateTransformation.HoursToDegrees( c.X() ) ;
		r[1] = c.Y() ;

		return r ;
	}

	public double[] unconvert( double RA, double d ) {
		CAA2DCoordinate c ;
		double[] r = new double[2] ;

		c = CAACoordinateTransformation.Equatorial2Ecliptic( CAACoordinateTransformation.DegreesToHours( RA ), d, e ) ;
		r[0] = c.X() ;
		r[1] = c.Y() ;

		return r ;
	}
}
