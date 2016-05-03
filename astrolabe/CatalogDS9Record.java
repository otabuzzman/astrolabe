
package astrolabe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

@SuppressWarnings("serial")
public class CatalogDS9Record extends astrolabe.model.CatalogDS9Record implements CatalogRecord {

	// configuration key (CK_)
	private final static String CK_EPS		= "eps" ;

	private final static double DEFAULT_EPS	= 1.5 ;

	private double eps ;
	
	// pragma
	protected int combine = 0 ;

	private List<Coordinate[]> record = new java.util.Vector<Coordinate[]>() ;

	public CatalogDS9Record( String data ) throws ParameterNotValidException {
		java.util.Vector<Coordinate> bufl ;
		BufferedReader bufr ;
		String segr, segd[] ;

		bufl = new java.util.Vector<Coordinate>() ;

		try {
			bufr = new BufferedReader( new StringReader( data ) ) ;

			while ( ( segr = bufr.readLine() ) != null ) {
				if ( segr.length() == 0 ) {
					if ( bufl.size()>1 ) {
						record.add( bufl.toArray( new Coordinate[0] ) ) ;
						bufl.clear() ;
					}
					continue ;
				}

				segd = segr.trim().split( "\\p{Space}+" ) ;

				if ( segd.length == 2 )
					bufl.add( new Coordinate(
							Double.valueOf( segd[0] ),
							Double.valueOf( segd[1] ) ) ) ;
			}

			if ( bufl.size()>0 )
				record.add( bufl.toArray( new Coordinate[0] ) ) ;
		} catch ( IOException e ) {
			throw new RuntimeException( e.toString() ) ;
		}
	}

	public boolean isOK() {
		try {
			inspect() ;
		} catch ( ParameterNotValidException e ) {
			return false ;
		}

		return true ;
	}

	public void inspect() throws ParameterNotValidException {
	}

	public double RA() {
		return Double.POSITIVE_INFINITY ;		
	}

	public double de() {
		return Double.POSITIVE_INFINITY ;		
	}

	public Geometry list() {
		int n ;
		List<Geometry> rec ;
		Coordinate[] elm, act ;
		Coordinate a, o ;

		eps = Configuration.getValue( this, CK_EPS, DEFAULT_EPS ) ;

		n = record.size() ;
		rec = new java.util.Vector<Geometry>() ;

		elm = null ;

		for ( int i=0 ; n>i ; i++ ) {
			act = record.get( i ) ;

			a = act[0] ;
			o = act[act.length-1] ;

			if ( a.equals2D( o ) ) {
				rec.add( new GeometryFactory().createLineString( act ) ) ;

				continue ;
			}

			if ( eps( a, o ) ) {
				act = FieldOfView.close( act ) ;
				rec.add( new GeometryFactory().createLineString( act ) ) ;

				// pragma
				combine++ ;
				continue ;
			}

			if ( elm != null ) {
				o = elm[elm.length-1] ;
				a = act[0] ;

				if ( eps( o, a ) ) {
					elm = FieldOfView.chain( elm, act ) ;

					a = elm[0] ;
					o = elm[elm.length-1] ;

					if ( eps( a, o ) ) {
						elm = FieldOfView.close( elm ) ;
						rec.add( new GeometryFactory().createLineString( elm ) ) ;

						elm = null ;
					}

					continue ;
				}

				rec.add( new GeometryFactory().createLineString( elm ) ) ;
			}

			elm = act ;
		}

		if ( elm != null ) {
			a = elm[0] ;
			o = elm[elm.length-1] ;

			if ( eps( a, o ) )
				elm = FieldOfView.close( elm ) ;
			rec.add( new GeometryFactory().createLineString( elm ) ) ;
		}

		return new GeometryFactory().createGeometryCollection( rec.toArray( new Geometry[0] ) ) ;
	}

	private boolean eps( Coordinate a, Coordinate b ) {
		return epsX( a, b ) && epsY( a, b );
	}

	private boolean epsX( Coordinate a, Coordinate b ) {
		return eps( a.x, b.x ) ;
	}

	private boolean epsY( Coordinate a, Coordinate b ) {
		return eps( a.y, b.y ) ;
	}

	private boolean eps( double a, double b ) {
		return eps>java.lang.Math.abs( a-b ) ;
	}
}
