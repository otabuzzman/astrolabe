
package astrolabe;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class PolygonPlane {

	private Polygon polygon ;

	public PolygonPlane( Coordinate[] polygon ) {
		Coordinate[] c ;
		Coordinate a, o ;
		LinearRing r ;
		int ea, eo ;

		ea = 0 ;
		eo = polygon.length-1 ;

		a = polygon[ea] ; // firstElement() ;
		o = polygon[eo] ; // lastElement() ;
		if ( a.x!=o.x&&a.y!=o.y ) {
			c = new Coordinate[polygon.length+1] ;
			c[c.length-1] = new Coordinate( a.x, a.y ) ;
		} else {
			c = new Coordinate[polygon.length] ;
		}

		for ( int n=0 ; n<polygon.length ; n++ ) {
			c[n] = new Coordinate( polygon[n] ) ;
		}

		r = new GeometryFactory().createLinearRing( c ) ;
		this.polygon = new GeometryFactory().createPolygon( r, null ) ;
	}

	public double area() {
		double r ;
		Coordinate c[], p, t ;

		c = polygon.getCoordinates() ;

		// a = 1/2*((x1-x2)*(y1+y2)+(x2-x3)*(y2+y3)+...+(xn-x1)*(yn+y1))
		p = c[0] ;
		t = c[1] ;
		r = ( p.x-t.x )*( p.y+t.y ) ;
		p = t ;
		for ( int n=2 ; n<c.length ; n++ ) {
			t = c[n] ;
			r = r+( p.x-t.x )*( p.y+t.y ) ;
			p = t ;
		}
		p = c[0] ;
		r = ( r+( p.x-t.x )*( p.y+t.y ) )/2 ;

		return r ;
	}

	public int sign() {
		double sin, cos ;
		Vector a0, a1, b0 ;
		Coordinate c[] ;
		Point p ;

		sin = Math.sin( 1 ) ;
		cos = Math.cos( 1 ) ;

		c = polygon.getCoordinates() ;

		a0 = new Vector( c[0] ) ;
		a1 = new Vector( c[1] ) ;
		b0 = new Vector( a1.sub( a0 ) ) ;
		b0.apply( new double[] { cos, -sin, 0, sin, cos, 0, 0, 0, 1 } ) ; // rotate 1 degree counter clockwise
		a0.add( b0 ) ;
		p = new GeometryFactory().createPoint( new Coordinate( a0.x, a0.y ) ) ;

		return polygon.covers( p )?-1:1 ;
	}
}
