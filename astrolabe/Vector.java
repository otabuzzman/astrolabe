
package astrolabe;

import com.vividsolutions.jts.geom.Coordinate;

@SuppressWarnings("serial")
public class Vector extends Coordinate {

	public Vector() {
		this( 0, 0, 0 ) ;
	}

	public Vector( Coordinate c ) {
		this( c.x, c.y, c.z ) ;

		setCoordinate( c ) ;
	}

	public Vector( double x, double y, double z ) {
		super( x, y, z ) ;
	}

	public void setCoordinate( Coordinate other ) {
		x = Double.isNaN( other.x )?0:other.x ;
		y = Double.isNaN( other.y )?0:other.y ;
		z = Double.isNaN( other.z )?0:other.z ;
	}

	public Vector neg() {
		x = -x ;
		y = -y ;
		z = -z ;

		return this ;
	}

	public double abs() {
		return java.lang.Math.sqrt( x*x+y*y+z*z ) ;
	}

	public Vector add( Coordinate v ) {
		x = x+v.x ;
		y = y+v.y ;
		z = z+v.z ;

		return this ;
	}

	public Vector sub( Coordinate v ) {
		x = x-v.x ;
		y = y-v.y ;
		z = z-v.z ;

		return this ;
	}

	public Vector mul( double s ) {
		x = x*s ;
		y = y*s ;
		z = z*s ;

		return this ;
	}

	public Vector scale( double l ) {
		double p = abs() ;

		x = x/p*l ;
		y = y/p*l ;
		z = z/p*l ;

		return this ;
	}

	public double dot( Coordinate v ) {
		return x*v.x+y*v.y+z*v.z ;
	}

	public double angle( Vector v ) {
		return Math.acos( dot( v )/( abs()*v.abs() ) ) ;
	}

	public Vector cross( Coordinate v ) {
		double x = this.x ;
		double y = this.y ;
		double z = this.z ;

		this.x = y*v.z-z*v.y ;
		this.y = z*v.x-x*v.z ;
		this.z = x*v.y-y*v.x ;

		return this ;
	}

	public Vector apply( double[] m ) {
		double x = this.x ;
		double y = this.y ;
		double z = this.z ;

		switch ( m.length ) {
		case 4:
			this.x = x*m[0]+y*m[1] ;
			this.y = x*m[2]+y*m[3] ;
			break ;
		case 9:
			this.x = x*m[0]+y*m[1]+z*m[2] ;
			this.y = x*m[3]+y*m[4]+z*m[5] ;
			this.z = x*m[6]+y*m[7]+z*m[8] ;
			break ;
		default:
		}

		return this ;
	}

	public static Vector[] subAll( Coordinate[] list ) {
		Vector[] sub = new Vector[ list.length-1 ] ;

		for ( int c=1 ; list.length>c ; c++ )
			sub[c-1] = new Vector( list[c] )
		.sub( new Vector( list[c-1] ) ) ;

		return sub ;
	}

	public static Vector addAll( Coordinate[] list ) {
		Vector add = new Vector() ;

		for ( Coordinate a : list )
			add.add( new Vector( a ) ) ;

		return add ;
	}
}
