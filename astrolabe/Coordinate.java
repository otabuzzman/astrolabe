
package astrolabe;

import caa.CAA2DCoordinate;
import caa.CAA3DCoordinate;

@SuppressWarnings("serial")
public class Coordinate extends com.vividsolutions.jts.geom.Coordinate {

	private final static String QK_X = "x" ;
	private final static String QK_Y = "y" ;
	private final static String QK_Z = "z" ;

	public Coordinate( com.vividsolutions.jts.geom.Coordinate coordinate ) {
		super( coordinate ) ;

		setCoordinate( coordinate ) ;
	}

	public Coordinate( CAA2DCoordinate coordinate ) {
		super( coordinate.X(), coordinate.Y(), 0 ) ;
	}

	public Coordinate( CAA3DCoordinate coordinate ) {
		super( coordinate.X(), coordinate.Y(), coordinate.Z() ) ;
	}

	public Coordinate( double x, double y, double z ) {
		super( x, y, z ) ;
	}

	public void setCoordinate( com.vividsolutions.jts.geom.Coordinate other ) {
		x = Double.isNaN( other.x )?0:other.x ;
		y = Double.isNaN( other.y )?0:other.y ;
		z = Double.isNaN( other.z )?0:other.z ;
	}

	public com.vividsolutions.jts.geom.Coordinate spherical() {
		return new com.vividsolutions.jts.geom.Coordinate(
				Math.atan2( y, x ), Math.asin( z/java.lang.Math.sqrt( x*x+y*y+z*z ) ) ) ;
	}

	public com.vividsolutions.jts.geom.Coordinate cartesian() {
		return new com.vividsolutions.jts.geom.Coordinate(
				Math.cos( y )*Math.cos( x ),
				Math.cos( y )*Math.sin( x ),
				Math.sin( y ) ) ;
	}

	public void register( Object clazz, String key ) {
		SubstituteCatalog cat ;
		String sub, k ;

		cat = new SubstituteCatalog( clazz ) ;

		if ( key == null ) {
			sub = cat.substitute( QK_X, QK_X ) ;
			Registry.register( sub, x ) ;
			sub = cat.substitute( QK_Y, QK_Y ) ;
			Registry.register( sub, y ) ;
			sub = cat.substitute( QK_Z, QK_Z ) ;
			Registry.register( sub, z ) ;
		} else {
			k = key+'.' ;

			sub = cat.substitute( k+QK_X, k+QK_X ) ;
			Registry.register( sub, x ) ;
			sub = cat.substitute( k+QK_Y, k+QK_Y ) ;
			Registry.register( sub, y ) ;
			sub = cat.substitute( k+QK_Z, k+QK_Z ) ;
			Registry.register( sub, z ) ;
		}
	}

	public static void degister( Object clazz, String key ) {
		SubstituteCatalog cat ;
		String sub, k ;

		cat = new SubstituteCatalog( clazz ) ;

		if ( key == null ) {
			sub = cat.substitute( QK_X, QK_X ) ;
			Registry.degister( sub ) ;
			sub = cat.substitute( QK_Y, QK_Y ) ;
			Registry.degister( sub ) ;
			sub = cat.substitute( QK_Z, QK_Z ) ;
			Registry.degister( sub ) ;
		} else {
			k = key+'.' ;

			sub = cat.substitute( k+QK_X, k+QK_X ) ;
			Registry.degister( sub ) ;
			sub = cat.substitute( k+QK_Y, k+QK_Y ) ;
			Registry.degister( sub ) ;
			sub = cat.substitute( k+QK_Z, k+QK_Z ) ;
			Registry.degister( sub ) ;
		}
	}

	public static void parallelShift( com.vividsolutions.jts.geom.Coordinate[] list, double dist ) {
		double m90[] = new double[] { 0, -1, 1, 0 } ;
		double m90c[] = new double[] { 0, 1, -1, 0 } ;
		Vector a, b, c ;
		int n, i ;

		n = list.length ;

		a = new Vector( list[n-1] ) ;
		b = new Vector( list[n-2] ) ;
		c = new Vector ( a ).add( b.sub( a ).scale( dist ).apply( m90c ) ) ;

		for ( i=0 ; ( n-1 )>i ; i++ ) {
			a.setCoordinate( list[i] ) ;
			b.setCoordinate( list[i+1] ) ;
			list[i].setCoordinate( a.add( b.sub( a ).scale( dist ).apply( m90 ) ) ) ;
		}

		list[i].setCoordinate( c ) ;
	}

	public static com.vividsolutions.jts.geom.Coordinate[] cloneAll( com.vividsolutions.jts.geom.Coordinate[] list ) {
		com.vividsolutions.jts.geom.Coordinate[] clone ;
		int n = list.length ;

		clone = new com.vividsolutions.jts.geom.Coordinate[n] ;
		for ( int i=0 ; n>i ; i++ )
			clone[i] = (com.vividsolutions.jts.geom.Coordinate) list[i].clone() ;

		return clone ;
	}

	public double[] toArray() {
		return new double[] { x, y, z } ;
	}

}
