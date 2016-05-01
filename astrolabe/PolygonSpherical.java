
package astrolabe;

import java.util.List;

public class PolygonSpherical {

	private List<double[]> polygon ;

	public PolygonSpherical( List<double[]> polygon ) {
		this.polygon = polygon ;
	}

	public double area() {
		double d, s, r ;
		double[] a, b, p ;
		int ea, eo ;

		ea = 0 ;
		eo = polygon.size()-1 ;

		a = polygon.get( eo ) ; // lastElement() ;
		b = polygon.get( 1 ) ;
		p = polygon.get( ea ) ; // firstElement() ;
		d = transform( p, a )-transform( p, b ) ;
		s = d<0?d+360:d ;
		for ( int n=1 ; n<polygon.size()-1 ; n++ ) {
			a = polygon.get( n-1 ) ;
			b = polygon.get( n+1 ) ;
			p = polygon.get( n ) ;
			d = transform( p, a )-transform( p, b ) ;
			s = s+( d<0?d+360:d ) ;
		}
		a = polygon.get( polygon.size()-2 ) ;
		b = polygon.get( ea ) ; // firstElement() ;
		p = polygon.get( eo ) ; // lastElement() ;
		d = transform( p, a )-transform( p, b ) ;
		s = s+( d<0?d+360:d ) ;

		r = s-180*( polygon.size()-2 ) ;

		return r ;
	}

	private static double transform( double[] p, double[] q ) {
		double t, b ;

		t = Math.sin( q[2]-p[2] )*Math.cos( q[1] ) ;
		b = Math.sin( q[1] )*Math.cos( p[1] )
		-Math.cos( q[1] )*Math.sin( p[1] )*Math.cos( q[2]-p[2] ) ;

		return Math.atan2( t, b ) ;
	}
}
