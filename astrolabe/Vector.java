
package astrolabe;

import java.util.List;

public class Vector {

	public double x = 0 ;
	public double y = 0 ;
	public double z = 0 ;

	public Vector() {
		set( 0, 0, 0 ) ;
	}

	public Vector( Vector v ) {
		set( v.x, v.y, v.z ) ;
	}

	public Vector( double x, double y ) {
		set( x, y, 0 ) ;
	}

	public Vector( double x, double y, double z ) {
		set ( x, y, z )	;
	}

	public Vector( double[] xyz ) {
		set ( xyz )	;
	}

	public void set( Vector v ) {
		set( v.x, v.y, v.z ) ;
	}

	public void set( double x, double y ) {
		set( x, y, 0 ) ;
	}

	public void set( double x, double y, double z ) {
		this.x = x ;
		this.y = y ;
		this.z = z ;
	}

	public void set( double[] xyz ) {
		this.x = xyz[0] ;
		this.y = xyz[1] ;
		if ( xyz.length>2 ) {
			this.z = xyz[2] ;
		} else {
			this.z = 0 ;
		}
	}

	public double abs() {
		return java.lang.Math.sqrt( x*x+y*y+z*z ) ;
	}

	public Vector add( Vector v ) {
		x = x+v.x ;
		y = y+v.y ;
		z = z+v.z ;

		return this ;
	}

	public Vector sub( Vector v ) {
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

	public double dot( Vector v ) {
		return x*v.x+y*v.y+z*v.z ;
	}

	public double angle( Vector v ) {
		return Math.acos( dot( v )/( abs()*v.abs() ) ) ;
	}

	public Vector cross( Vector v ) {
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

		this.x = x*m[0]+y*m[1]+z*m[2] ;
		this.y = x*m[3]+y*m[4]+z*m[5] ;
		this.z = x*m[6]+y*m[7]+z*m[8] ;

		return this ;
	}

	public double[] toArray() {
		return new double[] { x, y, z } ;
	}

	public static double length( List<double[]> list ) {
		Vector a, b ;
		double l = 0 ;

		b = new Vector( list.get( 0 ) ) ;
		for ( int i=1 ; i<list.size() ; i++ ) {
			a = new Vector( list.get( i ) ) ;
			l = l+a.sub( b ).abs() ;
			b = new Vector( a ) ;
		}

		return l ;
	}
}
