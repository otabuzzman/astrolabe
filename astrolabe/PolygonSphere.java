
package astrolabe;

import com.vividsolutions.jts.geom.Coordinate;

import caa.CAACoordinateTransformation;

public class PolygonSphere {

	private Coordinate[] polygon ;

	public PolygonSphere( Coordinate[] polygon ) {
		this.polygon = new Coordinate[polygon.length] ;
		System.arraycopy( polygon, 0, this.polygon, 0, polygon.length ) ;
	}

	public double area() {
		double d, s ;
		Coordinate b, f, v ;
		int n ;

		s = 0 ;
		n = 1 ;

		for ( ; n<=polygon.length ; n++ ) {
			b = polygon[ n-1 ] ;
			v = polygon[ n%polygon.length ] ;
			f = polygon[ ( n+1 )%polygon.length ] ;
			d = transform( v, b )-transform( v, f ) ;
			s = s+( d<0?d+360:d ) ;
		}

		return CAACoordinateTransformation.DegreesToRadians( s-180*( polygon.length-2 ) ) ;
	}

	private static double transform( Coordinate p, Coordinate q ) {
		double t, b ;

		t = Math.sin( q.x-p.x )*Math.cos( q.y ) ;
		b = Math.sin( q.y )*Math.cos( p.y )
		-Math.cos( q.y )*Math.sin( p.y )*Math.cos( q.x-p.x ) ;

		return Math.atan2( t, b ) ;
	}
}
