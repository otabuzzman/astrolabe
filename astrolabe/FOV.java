
package astrolabe;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class FOV {

	private Polygon fov ;

	@SuppressWarnings("unchecked")
	public FOV( String fov ) throws ParameterNotValidException {
		java.util.Vector<String> fe ; // fov elements
		java.util.Vector<double[]> ol ; // fov outline
		java.util.Vector<double[]> ed ; // fov edge
		double[] f, l ;
		int s ;

		fe = (java.util.Vector<String>) Registry.retrieve( fov ) ;
		ol = new java.util.Vector<double[]>() ;

		for ( int e=0 ; e<fe.size() ; e++ ) {
			ed = ( (Baseline) Registry.retrieve( fe.get( e ) ) ).list() ;

			s = 0 ;

			if ( ol.size()>0 ) {
				l = ol.lastElement() ;
				f = ed.get( 0 ) ;

				if ( l[0]==f[0]&&l[1]==f[1] ) {
					s = 1 ;
				}
			}

			ol.addAll( ed.subList( s, ed.size() ) ) ;
		}

		this.fov = toGeometry( ol ) ;
	}

	public void insert( FOV hole ) throws ParameterNotValidException {
		LinearRing fe, he, fi[] ;
		int ni ; // number of interior rings

		if ( fov.contains( hole.geometry() ) ) {
			he = new GeometryFactory().createLinearRing( hole.geometry().getCoordinates() ) ;

			fe = new GeometryFactory().createLinearRing( fov.getExteriorRing().getCoordinates() ) ;

			ni = fov.getNumInteriorRing() ;
			fi = new LinearRing[ni+1] ;

			if ( ni>0 ) {
				for ( int i=0 ; i<ni ; i++ ) {
					fi[i] = new GeometryFactory().createLinearRing( fov.getInteriorRingN( i ).getCoordinates() ) ;
				}
			}
			fi[fi.length-1] = he ; // append new hole

			fov = new GeometryFactory().createPolygon( fe, fi ) ;
		} else {
			throw new ParameterNotValidException() ;
		}
	}

	public boolean covers( java.util.Vector<double[]> area ) {
		Geometry a ;

		a = toGeometry( area ) ;

		return fov.covers( a ) ;
	}

	public boolean covers( double[] point ) {
		Geometry p ;

		p = toGeometry( point ) ;

		return fov.covers( p ) ;
	}

	public boolean intersects( java.util.Vector<double[]> area ) {
		Geometry a ;

		a = toGeometry( area ) ;

		return fov.intersects( a ) ;
	}

	public Geometry geometry() {
		return fov ;
	}

	private Coordinate[] toCoordinateArray( java.util.Vector<double[]> vector ) {
		Coordinate c[] ;
		double[] a ;

		c = new Coordinate[vector.size()] ;
		for ( int v=0 ; v<vector.size() ; v++ ) {
			a = vector.get( v ) ;
			c[v] = new Coordinate( a[0], a[1] ) ;
		}

		return c ;
	}

	private Polygon toGeometry( java.util.Vector<double[]> outline ) {
		java.util.Vector<double[]> c ;
		double[] f, l ;
		LinearRing o ;
		Polygon r ;

		f = outline.firstElement() ;
		l = outline.lastElement() ;
		if ( ! ( f[0]==l[0]&&f[1]==l[1] ) ) {
			c = new java.util.Vector<double[]>() ;
			c.addAll( outline ) ;
			c.add( f ) ;
		} else {
			c = outline ;
		}

		o = new GeometryFactory().createLinearRing( toCoordinateArray( c ) ) ;
		r = new GeometryFactory().createPolygon( o, null ) ;

		return  r ;
	}

	private Point toGeometry( double[] point ) {
		Point r ;

		r = new GeometryFactory().createPoint( new Coordinate( point[0], point[1] ) ) ;

		return  r ;
	}
}
