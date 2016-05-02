
package astrolabe;

import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

@SuppressWarnings("serial")
public class ListCutter extends java.util.Vector<double[]> implements PostscriptEmitter {

	private Geometry fov ;

	public ListCutter( Geometry fov ) {
		this( null, fov ) ;
	}

	public ListCutter( List<double[]> list, Geometry fov ) {
		if ( list != null )
			for ( double[] xy : list )
				add( xy ) ;

		this.fov = new GeometryFactory().createGeometry( fov ) ;
	}

	public void headPS( AstrolabePostscriptStream ps ) {
	}

	public void emitPS( AstrolabePostscriptStream ps ) {
		double[] xy ;

		for ( List<double[]> l : segmentsInterior() ) {
			ps.array( true ) ;
			for ( int n=0 ; n<l.size() ; n++ ) {
				xy = (double[]) l.get( n ) ;
				ps.push( xy[0] ) ;
				ps.push( xy[1] ) ;
			}
			ps.array( false ) ;

			ps.operator.newpath() ;
			ps.push( ApplicationConstant.PS_PROLOG_GDRAW ) ;

			// halo stroke
			ps.operator.currentlinewidth() ;

			ps.operator.dup() ;
			ps.operator.div( 100 ) ;
			ps.push( (Double) ( Registry.retrieve( ApplicationConstant.PK_CHART_HALO ) ) ) ; 
			ps.operator.mul() ;
			ps.push( (Double) ( Registry.retrieve( ApplicationConstant.PK_CHART_HALOMIN ) ) ) ; 
			ps.push( ApplicationConstant.PS_PROLOG_MAX ) ;
			ps.push( (Double) ( Registry.retrieve( ApplicationConstant.PK_CHART_HALOMAX ) ) ) ; 
			ps.push( ApplicationConstant.PS_PROLOG_MIN ) ;

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

	public void tailPS( AstrolabePostscriptStream ps ) {
	}

	public List<List<double[]>> segmentsIntersecting( boolean interior ) {
		List<List<double[]>> l ;
		List<double[]> s ;
		List<int[]> segmentsByIndex ;
		List<double[]> segmentByValue ;
		Geometry gRaw, gCut ;
		Coordinate[] segmentByCoordinate ;
		double[] xyA, xyO ;
		int ia, io ;

		l = new java.util.Vector<List<double[]>>() ;

		segmentsByIndex = new java.util.Vector<int[]>() ;
		if ( interior ) {
			segmentsInterior( segmentsByIndex ) ;
		} else {
			segmentsExterior( segmentsByIndex ) ;
		}
		for ( int[] segmentByIndex : segmentsByIndex ) {
			segmentByValue = subList( segmentByIndex[0], segmentByIndex[1]+1 ) ;

			gRaw = new GeometryFactory().createLineString( new JTSCoordinateArraySequence( segmentByValue ) ) ;
			gCut = gRaw.intersection( fov ) ;

			segmentByCoordinate = gCut.getCoordinates() ;
			xyA = new double[] {
					segmentByCoordinate[0].x,
					segmentByCoordinate[0].y } ;
			xyO = new double[] {
					segmentByCoordinate[ segmentByCoordinate.length-1 ].x,
					segmentByCoordinate[ segmentByCoordinate.length-1 ].y } ;

			s = new java.util.Vector<double[]>() ;
			s.add( xyA ) ;

			ia = segmentByIndex[0]+1 ;
			io = segmentByIndex[1]-1 ;
			if ( io>=ia )
				for ( double[] xy : subList( ia, io+1 ) ) {
					s.add( xy ) ;
				}

			s.add( xyO ) ;

			l.add( s ) ;
		}

		return l ;
	}

	public List<List<double[]>> segmentsInterior() {
		return segmentsInterior( null ) ;
	}

	public List<List<double[]>> segmentsInterior( List<int[]> index ) {
		return segments( index, true ) ;
	}

	public List<List<double[]>> segmentsExterior() {
		return segmentsExterior( null ) ;
	}

	public List<List<double[]>> segmentsExterior( List<int[]> index ) {
		return segments( index, false ) ;
	}

	public List<List<double[]>> segments( boolean interior ) {
		return segments( null, interior ) ;
	}

	public List<List<double[]>> segments( List<int[]> index, boolean interior ) {
		List<List<double[]>> r = new java.util.Vector<List<double[]>>() ;
		int ib, ie ;

		for ( ib = nearest( 0, interior ) ; ib>-1 ; ib = nearestFirst( ie, interior ) ) {
			ie = nearestFirst( ib, ! interior ) ;
			if ( 0>ie )
				ie = size()-1 ;
			if ( ib>0 )
				ib = ib-1 ;

			r.add( subList( ib, ie+1 ) ) ;

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
		return interior==fov.contains( new GeometryFactory().createPoint( new JTSCoordinate( get( index ) ) ) ) ;
	}
}
