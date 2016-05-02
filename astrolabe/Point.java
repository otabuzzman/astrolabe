
package astrolabe;

public class Point {

	private final static String QK_X = "x" ;
	private final static String QK_Y = "y" ;
	private final static String QK_Z = "z" ;

	public double x = 0 ;
	public double y = 0 ;
	public double z = 0 ;

	public Point() {
		set( 0, 0, 0 ) ;
	}

	public Point( Vector v ) {
		set( v.x, v.y, v.z ) ;
	}

	public Point( double[] xyz ) {
		set( xyz ) ;
	}

	public Point( double x, double y ) {
		set( x, y, 0 ) ;
	}

	public Point( double x, double y, double z ) {
		set ( x, y, z )	;
	}

	public void set( Point p ) {
		set( p.x, p.y, p.z ) ;
	}

	public void set( double[] xyz ) {
		if ( xyz.length>2 )
			set( xyz[0], xyz[1], xyz[2] ) ;
		else
			set( xyz[0], xyz[1], 0 ) ;
	}

	public void set( double x, double y ) {
		set( x, y, 0 ) ;
	}

	public void set( double x, double y, double z ) {
		this.x = x ;
		this.y = y ;
		this.z = z ;
	}

	public void register() {
		register( this, null ) ;
	}

	public void register( Object clazz, String key ) {
		SubstituteCatalog cat ;
		String sub, k ;

		cat = new SubstituteCatalog( clazz ) ;

		if ( key == null ) {
			sub = cat.substitute( QK_X, null ) ;
			Registry.register( sub, x ) ;
			sub = cat.substitute( QK_Y, null ) ;
			Registry.register( sub, y ) ;
			sub = cat.substitute( QK_Z, null ) ;
			Registry.register( sub, z ) ;
		} else {
			k = key+'.' ;

			sub = cat.substitute( k+QK_X, null ) ;
			Registry.register( sub, x ) ;
			sub = cat.substitute( k+QK_Y, null ) ;
			Registry.register( sub, y ) ;
			sub = cat.substitute( k+QK_Z, null ) ;
			Registry.register( sub, z ) ;
		}
	}
}
