
package astrolabe;

import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

@SuppressWarnings("serial")
public class ListCutter extends java.util.Vector<Coordinate> implements PostscriptEmitter {

	// configuration key (CK_)
	private final static String CK_HALO			= "halo" ;
	private final static String CK_HALOMIN		= "halomin" ;
	private final static String CK_HALOMAX		= "halomax" ;

	private final static double DEFAULT_HALO	= 4 ;
	private final static double DEFAULT_HALOMIN	= .08 ;
	private final static double DEFAULT_HALOMAX	= .4 ;

	private Geometry fov ;

	public ListCutter( Geometry fov ) {
		this( null, fov ) ;
	}

	public ListCutter( Coordinate[] list, Geometry fov ) {
		if ( list != null )
			for ( Coordinate xy : list )
				add( xy ) ;

		this.fov = new GeometryFactory().createGeometry( fov ) ;
	}

	public void headPS( ApplicationPostscriptStream ps ) {
	}

	public void emitPS( ApplicationPostscriptStream ps ) {
		Configuration conf ;

		for ( Coordinate[] l : segmentsInterior() ) {
			ps.array( true ) ;
			for ( Coordinate xy : l ) {
				ps.push( xy.x ) ;
				ps.push( xy.y ) ;
			}
			ps.array( false ) ;

			ps.operator.newpath() ;
			ps.gdraw() ;

			// halo stroke
			ps.operator.currentlinewidth() ;

			ps.operator.dup() ;
			ps.operator.div( 100 ) ;
			conf = new Configuration( this ) ;
			ps.push( conf.getValue( CK_HALO, DEFAULT_HALO ) ) ; 
			ps.operator.mul() ;
			ps.push( conf.getValue( CK_HALOMIN, DEFAULT_HALOMIN ) ) ; 
			ps.max() ;
			ps.push( conf.getValue( CK_HALOMAX, DEFAULT_HALOMAX ) ) ; 
			ps.min() ;

			ps.operator.mul( 2 ) ;
			ps.operator.add() ;
			ps.operator.gsave() ;
			ps.operator.setlinewidth() ;
			ps.operator.setlinecap( 2 ) ;
			ps.operator.setgray( 1 ) ;
			ps.operator.stroke() ;
			ps.operator.grestore() ;

			ps.operator.gsave() ;
			ps.operator.stroke() ;
			ps.operator.grestore() ;
		}
	}

	public void tailPS( ApplicationPostscriptStream ps ) {
	}

	public List<Coordinate[]> segmentsIntersecting( boolean interior ) {
		List<Coordinate[]> l ;
		Coordinate[] s ;
		List<int[]> segmentsByIndex ;
		Coordinate[] segmentByValue ;
		Geometry gRaw, gCut ;
		Coordinate[] segmentByCoordinate ;
		Coordinate xyA, xyO ;
		int ia, io ;

		l = new java.util.Vector<Coordinate[]>() ;

		segmentsByIndex = new java.util.Vector<int[]>() ;
		if ( interior ) {
			segmentsInterior( segmentsByIndex ) ;
		} else {
			segmentsExterior( segmentsByIndex ) ;
		}
		for ( int[] segmentByIndex : segmentsByIndex ) {
			segmentByValue = subList( segmentByIndex[0], segmentByIndex[1]+1 ).toArray( new Coordinate[0] ) ;

			gRaw = new GeometryFactory().createLineString( segmentByValue ) ;
			gCut = gRaw.intersection( fov ) ;

			segmentByCoordinate = gCut.getCoordinates() ;
			xyA = new Coordinate(
					segmentByCoordinate[0].x,
					segmentByCoordinate[0].y ) ;
			xyO = new Coordinate(
					segmentByCoordinate[ segmentByCoordinate.length-1 ].x,
					segmentByCoordinate[ segmentByCoordinate.length-1 ].y ) ;

			ia = segmentByIndex[0]+1 ;
			io = segmentByIndex[1]-1 ;
			if ( io>=ia ) {
				s = new Coordinate[1+io-ia+2] ;
				for ( int c=0 ; c<1+io-ia ; c++ )
					s[c+1] = get( ia+c ) ;
			} else
				s = new Coordinate[2] ;

			s[0] = xyA ;
			s[s.length-1] = xyO ;

			l.add( s ) ;
		}

		return l ;
	}

	public List<Coordinate[]> segmentsInterior() {
		return segmentsInterior( null ) ;
	}

	public List<Coordinate[]> segmentsInterior( final List<int[]> index ) {
		return segments( index, true ) ;
	}

	public List<Coordinate[]> segmentsExterior() {
		return segmentsExterior( null ) ;
	}

	public List<Coordinate[]> segmentsExterior( final List<int[]> index ) {
		return segments( index, false ) ;
	}

	public List<Coordinate[]> segments( boolean interior ) {
		return segments( null, interior ) ;
	}

	public List<Coordinate[]> segments( final List<int[]> index, boolean interior ) {
		List<Coordinate[]> r = new java.util.Vector<Coordinate[]>() ;
		int ib, ie ;

		for ( ib = nearest( 0, interior ) ; ib>-1 ; ib = nearestFirst( ie, interior ) ) {
			ie = nearestFirst( ib, ! interior ) ;
			if ( 0>ie )
				ie = size()-1 ;
			if ( ib>0 )
				ib = ib-1 ;

			r.add( subList( ib, ie+1 ).toArray( new Coordinate[0] ) ) ;

			if ( index != null )
				index.add( new int[] { ib, ie } ) ;
		}

		return r ;
	}

	public int nearestFirstInterior( int index ) {
		return nearestFirst( index, true ) ;
	}

	public int nearestFirstExterior( int index ) {
		return nearestFirst( index, false ) ;
	}

	public int nearestFirst( int index, boolean interior ) {
		int i ;

		if ( check( index, interior ) ) {
			i = nearest( index, ! interior ) ;
			if ( 0>i )
				return -1 ;
			return nearest( i, interior ) ;
		} else
			return nearest( index, interior ) ;
	}

	public int nearest( int index, boolean interior ) {
		for ( int i=index ; i<size() ; i++ )
			if ( check( i, interior ) )
				return i ;

		return -1 ;
	}

	public boolean check( int index, boolean interior ) {
		return interior == fov.contains( new GeometryFactory().createPoint( get( index ) ) ) ;
	}
}
