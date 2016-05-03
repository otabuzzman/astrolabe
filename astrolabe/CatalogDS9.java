
package astrolabe;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.CoordinateSequenceFilter;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

@SuppressWarnings("serial")
public class CatalogDS9 extends astrolabe.model.CatalogDS9 implements PostscriptEmitter {

	// attribute value (AV_)
	private final static String AV_LINE = "line" ;

	private final static Log log = LogFactory.getLog( CatalogDS9.class ) ;

	private class CoordinateToCartesianFilter implements CoordinateSequenceFilter {

		private Converter converter ;
		private Projector projector ;

		public CoordinateToCartesianFilter( Projector projector, Converter converter ) {
			this.converter = converter ;
			this.projector = projector ;
		}

		public void filter( CoordinateSequence seq, int i ) {
			Coordinate xy ;

			xy = projector.project( converter.convert( seq.getCoordinate( i ), false ), false ) ;

			seq.setOrdinate( i, 0, xy.x ) ;
			seq.setOrdinate( i, 1, xy.y ) ;
		}

		public boolean isDone() {
			return false ;
		}

		public boolean isGeometryChanged() {
			return true ;
		}
	}

	private CoordinateToCartesianFilter coordinateToCartesianFilter ;

	public CatalogDS9( Converter converter, Projector projector ) {
		coordinateToCartesianFilter = new CoordinateToCartesianFilter( projector, converter ) ;
	}

	public void headPS( ApplicationPostscriptStream ps ) {
	}

	public void emitPS( ApplicationPostscriptStream ps ) {
		String uri ;
		Reader reader ;
		CatalogDS9Record record ;
		astrolabe.model.CatalogDS9Record select ;
		boolean line, draw ;
		FieldOfView fov ;
		Geometry gov ;
		ChartPage page ;
		Geometry rec, cmb ;
		LineString elm ;
		Geometry tmp, mem ;

		try {
			for ( int i=0 ; getCatalogDS9RecordCount()>i ; i++ ) {
				ps.op( "gsave" ) ;

				uri = String.format( getUrl(), i ) ;
				reader = reader( uri ) ;
				record = record( reader ) ;
				reader.close() ;

				select = getCatalogDS9Record( i ) ;
				select.copyValues( record ) ;

				if ( ! Boolean.parseBoolean( record.getSelect() ) )
					continue ;
				line = record.getContour().equals( AV_LINE ) ;

				fov = (FieldOfView) Registry.retrieve( FieldOfView.class.getName() ) ;
				if ( fov != null && fov.isClosed() )
					gov = fov.makeGeometry() ;
				else {
					page = (ChartPage) Registry.retrieve( ChartPage.class.getName() ) ;
					if ( page != null )
						gov = FieldOfView.makeGeometry( page.getViewRectangle(), true ) ;
					else
						gov = null ;
				}

				draw = false ;
				cmb = null ;
				mem = null ;

				rec = record.list() ;
				rec.apply( coordinateToCartesianFilter ) ;

				for ( int j=0 ; rec.getNumGeometries()>j ; j++ ) {
					elm = (LineString) rec.getGeometryN( j ) ;

					if ( gov == null ) {
						draw = gdraw( ps, elm ) ;
						continue ;
					}

					if ( ! elm.isClosed() )
						continue ;
					if ( ! elm.intersects( gov ) )
						continue ;

					if ( gov.contains( elm ) ) {
						draw = gdraw( ps, elm ) ;
						continue ;
					}

					if ( line ) {
						tmp = gov.intersection( elm ) ;
						draw = gdraw( ps, tmp ) ;
						continue ;
					}

					// pragma
					tmp = new GeometryFactory().createPolygon( elm.getCoordinates() ) ;
					if ( record.combine == 0 ) {
						draw = gdraw( ps, gov.intersection( tmp ) ) ;
						continue ;
					}
					if ( cmb == null ) {
						cmb = gov.intersection( tmp ) ;
						mem = tmp ;
					} else {
						if ( cmb.intersects( tmp ) )
							cmb = cmb.difference( tmp ) ;
						else {
							cmb = gov.difference( mem ) ;
							cmb = cmb.difference( tmp ) ;
						}
					}
				}

				if ( cmb != null )
					draw = gdraw( ps, cmb ) ;

				if ( draw && gov == null )
					ps.script( Configuration.getValue( record, AV_LINE, "" ) ) ;
				else
					ps.script( Configuration.getValue( record, record.getContour(), "" ) ) ;

				ps.op( "grestore" ) ;
			}
		} catch ( MalformedURLException e ) {
			throw new RuntimeException( e.toString() ) ;
		} catch ( URISyntaxException e ) {
			throw new RuntimeException( e.toString() ) ;
		} catch ( IOException e ) {
			throw new RuntimeException( e.toString() ) ;
		}
	}

	static private boolean gdraw( ApplicationPostscriptStream ps, Geometry geometry ) {
		Geometry g ;
		int n ;

		if ( geometry.getNumPoints() == 0 )
			return false ;

		n = geometry.getNumGeometries() ;

		for ( int i=0 ; n>i ; i++ ) {
			g = geometry.getGeometryN( i ) ;

			ps.array( true ) ;
			for ( Coordinate c : g.getCoordinates() ) {
				ps.push( c.x ) ;
				ps.push( c.y ) ;
			}
			ps.array( false ) ;

			ps.op( "gdraw" ) ;
		}

		return true ;
	}

	public void tailPS( ApplicationPostscriptStream ps ) {
	}

	public Reader reader( String uri ) throws URISyntaxException, MalformedURLException {
		URI src ;
		URL url ;
		File file ;
		InputStream in ;
		GZIPInputStream gz ;

		src = new URI( uri ) ;
		if ( src.isAbsolute() ) {
			file = new File( src ) ;	
		} else {
			file = new File( src.getPath() ) ;
		}
		url = file.toURL() ;

		try {
			in = url.openStream() ;

			gz = new GZIPInputStream( in ) ;
			return new InputStreamReader( gz ) ;
		} catch ( IOException egz ) {
			try {
				in = url.openStream() ;

				return new InputStreamReader( in ) ;
			} catch ( IOException ein ) {
				throw new RuntimeException ( egz.toString() ) ;
			}
		}
	}

	public CatalogDS9Record record( java.io.Reader catalog ) throws IOException {
		StringBuilder b ;
		char[] c ;

		b = new StringBuilder() ;
		c = new char[1] ;

		while ( catalog.read( c, 0, 1 )>-1 )
			b.append( c ) ;

		return record( b.substring( 0 ) ) ;
	}

	private CatalogDS9Record record( String record ) {
		CatalogDS9Record r = null ;

		try {
			r = new CatalogDS9Record( record ) ;
		} catch ( ParameterNotValidException e ) {
			log.warn( ParameterNotValidError.errmsg( '"'+record+'"', e.getMessage() ) ) ;
		}

		return r ;
	}
}
