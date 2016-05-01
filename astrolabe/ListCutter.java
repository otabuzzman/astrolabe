
package astrolabe;

import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

@SuppressWarnings("serial")
public class ListCutter extends java.util.Vector<double[]> implements PostscriptEmitter {

	private Geometry fov ;

	public ListCutter( List<double[]> list, Geometry fov ) {
		for ( double[] xy : list ) {
			add( xy ) ;
		}

		this.fov = new GeometryFactory().createGeometry( fov ) ;
	}

	public void headPS( PostscriptStream ps ) {
	}

	public void emitPS( PostscriptStream ps ) {
		double[] xy ;

		for ( List<double[]> l : segmentsInterior() ) {
			ps.operator.mark() ;
			for ( int n=l.size() ; n>0 ; n-- ) {
				xy = (double[]) l.get( n-1 ) ;
				ps.push( xy[0] ) ;
				ps.push( xy[1] ) ;
			}
			try {
				ps.custom( ApplicationConstant.PS_PROLOG_POLYLINE ) ;

				// halo stroke
				ps.operator.currentlinewidth() ;
				ps.operator.dup();
				ps.push( (Double) ( Registry.retrieve( ApplicationConstant.PK_CHART_HALOMAX ) ) ) ; 
				ps.push( (Double) ( Registry.retrieve( ApplicationConstant.PK_CHART_HALOMIN ) ) ) ; 
				ps.push( (Double) ( Registry.retrieve( ApplicationConstant.PK_CHART_HALO ) ) ) ; 
				ps.custom( ApplicationConstant.PS_PROLOG_HALO ) ;
				ps.operator.mul( 2 ) ;
				ps.operator.add() ;
				ps.operator.gsave() ;
				ps.operator.setlinewidth() ;
				ps.operator.setlinecap( 2 ) ;
				ps.operator.setgray( 1 ) ;
				ps.operator.stroke() ;
				ps.operator.grestore() ;
			} catch ( ParameterNotValidException e ) {
				throw new RuntimeException( e.toString() ) ;
			}
			ps.operator.gsave() ;
			ps.operator.stroke() ;
			ps.operator.grestore() ;
		}
	}

	public void tailPS( PostscriptStream ps ) {
	}

	public java.util.Vector<List<double[]>> segmentsIntersecting( boolean interior ) {
		java.util.Vector<List<double[]>> l ;
		java.util.Vector<double[]> s ;
		java.util.Vector<int[]> segmentsByIndex ;
		List<double[]> segmentByXY ;
		Geometry gRaw, gCut ;
		Coordinate[] segmentByCoordinate ;
		double[] xyA, xyO ;

		l = new java.util.Vector<List<double[]>>() ;

		segmentsByIndex = new java.util.Vector<int[]>() ;
		if ( interior ) {
			segmentsInterior( segmentsByIndex ) ;
		} else {
			segmentsExterior( segmentsByIndex ) ;
		}
		for ( int[] segmentByIndex : segmentsByIndex ) {
			segmentByXY = subList( segmentByIndex[0]>0?segmentByIndex[0]-1:0,
					segmentByIndex[1]<size()-1?segmentByIndex[1]+2:size() ) ;

			gRaw = new GeometryFactory().createLineString( new JTSCoordinateArraySequence( segmentByXY ) ) ;
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
			for ( double[] xy : segmentByXY ) {
				s.add( xy ) ;
			}
			s.add( xyO ) ;

			l.add( s ) ;
		}

		return l ;
	}

	public java.util.Vector<List<double[]>> segmentsInterior() {
		return segmentsInterior( null ) ;
	}

	public java.util.Vector<List<double[]>> segmentsInterior( java.util.Vector<int[]> index ) {
		return segments( index, true ) ;
	}

	public java.util.Vector<List<double[]>> segmentsExterior() {
		return segmentsExterior( null ) ;
	}

	public java.util.Vector<List<double[]>> segmentsExterior( java.util.Vector<int[]> index ) {
		return segments( index, false ) ;
	}

	public java.util.Vector<List<double[]>> segments( boolean interior ) {
		return segments( null, interior ) ;
	}

	public java.util.Vector<List<double[]>> segments( java.util.Vector<int[]> index, boolean interior ) {
		java.util.Vector<List<double[]>> r = new java.util.Vector<List<double[]>>() ;
		int ib, ie ;

		for ( ib = nearest( 0, interior ) ; ib>-1&&ib<size()-1 ; ib = nearestFirst( ib+1, interior ) ) {
			ie = nearestFirst( ib, ! interior ) ;
			if ( ie<0 ) {
				ie = size() ;
			}

			r.add( subList( ib, ie ) ) ;

			if ( index != null ) {
				index.add( new int[] { ib, ie-1 } ) ;
			}
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
		int r = -1 ;
		int i ;

		if ( check( index, interior ) ) {
			i = nearest( index, ! interior ) ;
			if ( i>-1 ) {
				r = nearest( i, interior ) ;
			}
		} else {
			r = nearest( index, interior ) ;
		}

		return r ;
	}

	public int nearest( int index, boolean interior ) {
		int r = -1 ;

		for ( int i=index ; i<size() ; i++ ) {
			if ( check( i, interior ) ) {
				r = i ;

				break ;
			}
		}

		return r ;
	}

	public boolean check( int index, boolean interior ) {
		return interior==fov.contains( new GeometryFactory().createPoint( new JTSCoordinate( get( index ) ) ) ) ;
	}
}
