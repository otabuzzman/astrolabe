
package astrolabe;

import java.io.IOException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.WKTReader;

public class FieldOfView implements PostscriptEmitter {

	// configuration key (CK_)
	private final static String CK_VIEWER = "viewer" ;

	private java.util.Vector<Coordinate> fov = new java.util.Vector<Coordinate>() ;

	public FieldOfView( Coordinate[] list ) {
		if ( list == null || list.length == 1 )
			return ;
		set( list ) ;
	}

	public boolean add( Coordinate[] list ) {
		Geometry A, B, XAB, XBA ;
		Coordinate[] lnStrA0, lnStrA1, lnStrA2, lnStrB1 ;
		Coordinate[] crds0, crds1 ;
		Geometry X, P ; ;
		boolean ac, bc ;

		if ( list == null )
			return false ;
		if ( list.length+fov.size() == 1 )
			return false ;

		B = new GeometryFactory().createLineString( list ) ;
		if ( ! B.isSimple() )
			return false ;

		if ( fov.size() == 0 ) {
			set( list ) ;

			return true ;
		}

		A = new GeometryFactory().createLineString( get() ) ;

		if ( ! A.intersects( B ) )
			return false ;

		ac = isClosed() ;
		bc = list[0].equals2D( list[list.length-1] ) ;

		if ( ! ( ac || bc ) ) { // A open, B open
			XAB = A.difference( B ) ;
			XBA = B.difference( A ) ;

			switch ( XAB.getNumGeometries() ) {
			case 2: // 1 node
				set( chain(
						XAB.getGeometryN( 0 ).getCoordinates(),
						XBA.getGeometryN( 1 ).getCoordinates() ) ) ;

				break ;
			case 3: // 2 nodes
				lnStrA1 = XAB.getGeometryN( 1 ).getCoordinates() ;
				lnStrB1 = XBA.getGeometryN( 1 ).getCoordinates() ;

				// end of LineString A connected with start of B (regular)
				if ( lnStrA1[0].equals2D( lnStrB1[lnStrB1.length-1] ) )
					set( chain( lnStrA1, lnStrB1 ) ) ;
				// start of LineString A connected with start of B (irregular)
				else
					set( chain( lnStrA1, XBA.getGeometryN( 1 ).reverse().getCoordinates() ) ) ;

				break ;
			default: // 2+ nodes
				return false ;
			}
		} else if ( ac && bc ) { // A closed, B closed
			A = makeGeometry( get() ) ;
			B = makeGeometry( list ) ;

			XAB = A.intersection( B ) ;
			set( XAB.getCoordinates() ) ;
		} else { // A closed, B open or vice versa
			if ( bc ) {
				XAB = A ;
				A = B ;
				B = XAB ;
			}

			XAB = A.difference( B ) ;
			XBA = B.difference( A ) ;

			if ( XAB.getNumGeometries()>3 )
				return false ;

			lnStrA0 = XAB.getGeometryN( 0 ).getCoordinates() ;
			lnStrA1 = XAB.getGeometryN( 1 ).getCoordinates() ;
			lnStrA2 = XAB.getGeometryN( 2 ).getCoordinates() ;
			lnStrB1 = XBA.getGeometryN( 1 ).getCoordinates() ;
			if ( lnStrA0[lnStrA0.length-1].equals2D( lnStrB1[0] ) ) {
				crds0 = chain( lnStrA0, lnStrB1, lnStrA2 ) ;
				crds1 = chain( lnStrA1, XBA.getGeometryN( 1 ).reverse().getCoordinates() ) ;
			} else {
				crds0 = chain( lnStrA0, XBA.getGeometryN( 1 ).reverse().getCoordinates(), lnStrA2 ) ;
				crds1 = chain( lnStrA1, lnStrB1 ) ;
			}

			X = new GeometryFactory().createPolygon( crds0 ) ;
			P = pragmaGetSidePoint( lnStrB1[0], lnStrB1[1], true ) ;
			if ( X.contains( P ) )
				set( crds0 ) ;
			else
				set( crds1 ) ;

		}

		return true ;
	}

	private void set( Coordinate[] list ) {
		fov.clear() ;

		for ( Coordinate c : list )
			fov.add( c ) ;
	}

	static protected Coordinate[] chain( Coordinate[] a, Coordinate[]... o ) {
		java.util.Vector<Coordinate> list = new java.util.Vector<Coordinate>() ;
		int skip ;

		for ( Coordinate c : a )
			list.add( c ) ;

		for ( Coordinate[] l : o ) {
			if ( l == null || 2>l.length )
				continue ;
			if ( list.lastElement().equals2D( l[0] ) )
				skip = 1 ;
			else
				skip = 0 ;
			for ( int i=0+skip ; l.length>i ; i++ )
				list.add( l[i] ) ;
		}

		return list.toArray( new Coordinate[0] );
	}

	static protected Coordinate[] close( Coordinate[] c ) {
		Coordinate[] n = new Coordinate[c.length+1] ;

		n[c.length] = (Coordinate) c[0].clone() ;
		for ( int i=0 ; c.length>i ; i++ )
			n[i] = c[i] ;

		return n ;
	}

	private Point pragmaGetSidePoint( Coordinate a, Coordinate o, boolean left ) {
		Vector m, n, s, t ;
		double l ;

		m = new Vector( a ) ;
		n = new Vector( o ) ;
		s = new Vector( n.sub( m ) ) ;
		l = s.abs() ;
		s.scale( l*.5 ) ;
		t = new Vector( s ) ;
		if ( left )
			t.apply( new double[] { 0, -1, 0, 1, 0, 0, 0, 0, 1 } ) ;
		else
			t.apply( new double[] { 0, 1, 0, -1, 0, 0, 0, 0, 1 } ) ;
		t.scale( l*.01 ) ;

		return new GeometryFactory().createPoint( m.add( s ).add( t ) ) ;
	}

	public Coordinate[] get() {
		return fov.toArray( new Coordinate[0] ) ;
	}

	public Geometry makeGeometry() {
		if ( isClosed() )
			return new GeometryFactory().createPolygon( fov.toArray( new Coordinate[0] ) ) ;
		return null ;
	}

	static public Geometry makeGeometry( Coordinate[] list ) {
		return makeGeometry( list, false ) ;
	}

	static public Geometry makeGeometry( Coordinate[] list, boolean close ) {
		if ( close ) {
			return new GeometryFactory().createPolygon( close( list ) ) ;
		} else
			return new GeometryFactory().createPolygon( list ) ;
	}

	public boolean isClosed() {
		return fov.get( 0 ).equals2D( fov.get( fov.size()-1 ) ) ;
	}

	public void headPS( ApplicationPostscriptStream ps ) {
	}

	public void emitPS( ApplicationPostscriptStream ps ) {
		ps.array( true ) ;
		for ( Coordinate xy : fov ) {
			ps.push( xy.x ) ;
			ps.push( xy.y ) ;
		}
		ps.array( false ) ;

		ps.op( "newpath" ) ;
		ps.op( "gdraw" ) ;

		ps.op( "stroke" ) ;
	}

	public void tailPS( ApplicationPostscriptStream ps ) {
	}

	public static void main( String[] argv ) {
		String viewerDecl ;
		Process viewerProc ;
		TeeOutputStream out ;
		ApplicationPostscriptStream ps ;
		WKTReader R ;
		Geometry A, B ;
		FieldOfView fovA, fovB ;

		if ( argv.length%2 == 1 )
			System.exit( 1 ) ;

		Configuration.init() ;

		viewerDecl = Configuration.getValue( FieldOfView.class, CK_VIEWER, null ) ;
		out =  new TeeOutputStream( System.out ) ;

		if ( viewerDecl == null ) {
			viewerProc = null ;
		} else {
			try {
				viewerProc = Runtime.getRuntime().exec( viewerDecl.trim().split( "\\p{Space}+" ) ) ;

				viewerProc.getInputStream().close() ;
				viewerProc.getErrorStream().close() ;

				out.add( viewerProc.getOutputStream() ) ;
			} catch ( IOException e ) {
				viewerDecl = null ;
				viewerProc = null ;
			}
		}

		ps = new ApplicationPostscriptStream( out ) ;
		ps.emitDSCHeader() ;
		ps.emitDSCProlog() ;

		try {
			R = new WKTReader() ;

			for ( int i=0 ; argv.length>i ; i++ ) {
				A = R.read( argv[i] ) ;
				fovA = new FieldOfView( A.getCoordinates() ) ;
				B = R.read( argv[++i] ) ;
				fovB = new FieldOfView( B.getCoordinates() ) ;

				ps.op( "gsave" ) ;
				ps.push( 1 ) ;
				ps.push( 0 ) ;
				ps.push( 0 ) ;
				ps.op( "setrgbcolor" ) ;
				fovA.emitPS( ps ) ;
				ps.op( "grestore" ) ;

				ps.op( "gsave" ) ;
				ps.push( 0 ) ;
				ps.push( 1 ) ;
				ps.push( 0 ) ;
				ps.op( "setrgbcolor" ) ;
				fovB.emitPS( ps ) ;
				ps.op( "grestore" ) ;

				fovA.add( B.getCoordinates() ) ;
				fovA.emitPS( ps ) ;

				ps.op( "showpage" ) ;
			}

			ps.emitDSCTrailer() ;

			if ( viewerDecl != null )
				viewerProc.waitFor() ;
		} catch ( Exception e ) {
			e.printStackTrace() ;
			System.exit( 1 ) ;
		}
	}
}
