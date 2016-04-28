
package astrolabe;

public class CoordinateSystem {

	private Vector origin = new Vector() ;

	private Vector unitX = new Vector() ;
	private Vector unitY = new Vector() ;
	private Vector unitZ = new Vector() ;

	public CoordinateSystem( Vector origin, Vector unitZ, double[] pX ) {
		Vector vx ;

		this.origin.set( origin ) ;
		this.unitZ.set( unitZ ) ;

		vx = new Vector( pX ) ;
		vx.sub( origin ) ;

		unitY.set( unitZ ) ;
		unitY.cross( vx ).scale( 1 ) ;

		unitX.set( unitY ) ;
		unitX.cross( unitZ ).scale( 1 ) ;
	}

	public CoordinateSystem( Vector origin, Vector unitZ, Vector vY ) {
		this.origin.set( origin ) ;
		this.unitZ.set( unitZ ) ;

		unitX.set( vY ) ;
		unitX.cross( unitZ ).scale( 1 ) ;

		unitY.set( unitZ ) ;
		unitY.cross( unitX ).scale( 1 ) ;
	}

	public double[] local( double[] xyz ) {
		Vector p = new Vector( xyz ) ;
		double x, y, z ;

		p.sub( origin ) ;
		x = p.dot( unitX ) ;
		y = p.dot( unitY ) ;
		z = p.dot( unitZ ) ;

		return new Vector( x, y, z ).toArray() ;
	}

	public double[] world( double[] xyz ) {
		Vector p = new Vector( xyz ), a, b ;

		a = new Vector( unitX ).mul( p.x ).add( origin ) ;
		b = new Vector( unitY ).mul( p.y ).add( a ) ;

		return new Vector( unitZ ).mul( p.z ).add( b ).toArray() ;
	}
}
