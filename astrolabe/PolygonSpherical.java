
package astrolabe;

public class PolygonSpherical {

	private java.util.Vector<double[]> polygon ;

	public PolygonSpherical( java.util.Vector<double[]> polygon ) {
		this.polygon = polygon ;
	}

	public double area() {
		double d, s, r ;
		double[] a, b, p ;

		a = polygon.lastElement() ;
		b = polygon.get( 1 ) ;
		p = polygon.firstElement() ;
		d = transform( p, a )-transform( p, b ) ;
		s = d<0?d+Math.rad360:d ;
		for ( int n=1 ; n<polygon.size()-1 ; n++ ) {
			a = polygon.get( n-1 ) ;
			b = polygon.get( n+1 ) ;
			p = polygon.get( n ) ;
			d = transform( p, a )-transform( p, b ) ;
			s = s+( d<0?d+Math.rad360:d ) ;
		}
		a = polygon.get( polygon.size()-2 ) ;
		b = polygon.firstElement() ;
		p = polygon.lastElement() ;
		d = transform( p, a )-transform( p, b ) ;
		s = s+( d<0?d+Math.rad360:d ) ;

		r = s-Math.rad180*( polygon.size()-2 ) ;

		return r ;
	}

	private static double transform( double[] p, double[] q ) {
		double t, b ;

		t = java.lang.Math.sin( q[2]-p[2] )*java.lang.Math.cos( q[1] ) ;
		b = java.lang.Math.sin( q[1] )*java.lang.Math.cos( p[1] )
		-java.lang.Math.cos( q[1] )*java.lang.Math.sin( p[1] )*java.lang.Math.cos( q[2]-p[2] ) ;

		return java.lang.Math.atan2( t, b ) ;
	}
}
