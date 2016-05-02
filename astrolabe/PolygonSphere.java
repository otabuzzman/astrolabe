
package astrolabe;

import java.util.List;

import caa.CAACoordinateTransformation;

public class PolygonSphere {

	private List<double[]> polygon ;

	public PolygonSphere( List<double[]> polygon ) {
		this.polygon = polygon ;
	}

	public double area() {
		double d, s ;
		double[] b, f, v ;
		int n ;

		s = 0 ;
		n = 1 ;

		for ( ; n<=polygon.size() ; n++ ) {
			b = polygon.get( n-1 ) ;
			v = polygon.get( n%polygon.size() ) ;
			f = polygon.get( ( n+1 )%polygon.size() ) ;
			d = transform( v, b )-transform( v, f ) ;
			s = s+( d<0?d+360:d ) ;
		}

		return CAACoordinateTransformation.DegreesToRadians( s-180*( polygon.size()-2 ) ) ;
	}

	private static double transform( double[] p, double[] q ) {
		double t, b ;

		t = Math.sin( q[0]-p[0] )*Math.cos( q[1] ) ;
		b = Math.sin( q[1] )*Math.cos( p[1] )
		-Math.cos( q[1] )*Math.sin( p[1] )*Math.cos( q[0]-p[0] ) ;

		return Math.atan2( t, b ) ;
	}
}
