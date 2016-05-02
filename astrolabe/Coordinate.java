
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

		if ( Double.doubleToRawLongBits( z ) == Double.doubleToRawLongBits( Double.NaN ) )
			z = 0 ;
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

	public void register() {
		register( this, null ) ;
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

	public double[] toArray() {
		return new double[] { x, y, z } ;
	}
}
