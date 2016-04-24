
package astrolabe;

public class Vector {

	private double x = 0 ;
	private double y = 0 ;

	public Vector() {
		set( 0, 0 ) ;
	}

	public Vector( double[] xy ) {
		set( xy ) ;
	}

	public Vector( double x, double y ) {
		set( x, y ) ;
	}

	public void set( double[] xy ) {
		set( xy[0], xy[1] ) ;
	}

	public void set( double x, double y ) {
		this.x = x ;
		this.y = y ;
	}

	public void setX( double x ) {
		this.x = x ;
	}

	public void setY( double y ) {
		this.y = y ;
	}

	public double getX() {
		return x ;
	}

	public double getY() {
		return y ;
	}

	public Vector add( Vector cartesian ) {
		x = x+cartesian.getX() ;
		y = y+cartesian.getY() ;

		return this ;
	}

	public Vector sub( Vector cartesian ) {
		x = x-cartesian.getX() ;
		y = y-cartesian.getY() ;

		return this ;
	}

	public double dot( Vector cartesian ) {
		return x*cartesian.getX()+y*cartesian.getY() ;
	}

	public Vector scale( double s ) {
		x = x*s ;
		y = y*s ;

		return this ;
	}

	public Vector size( double length ) {
		double l = abs() ;

		x = x/l*length ;
		y = y/l*length ;

		return this ;
	}

	public double abs() {
		return java.lang.Math.abs( java.lang.Math.sqrt( x*x+y*y ) ) ;
	}

	public Vector rotate( double angle ) {
		double l = abs() ;
		double t = java.lang.Math.atan2( y, x ) ;

		x = l*java.lang.Math.cos( t+angle ) ;
		y = l*java.lang.Math.sin( t+angle ) ;

		return this ;
	}

	public double phi( Vector cartesian ) {
		return java.lang.Math.acos( dot( cartesian )/( abs()*cartesian.abs() ) ) ;
	}

	// clone()
	public Vector copy() {
		return new Vector( x, y ) ;
	}
}
