
package astrolabe;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.xml.ValidationException;

import com.vividsolutions.jts.geom.Coordinate;

@SuppressWarnings("serial")
public class CatalogDS9 extends astrolabe.model.CatalogDS9 implements PostscriptEmitter {

	private final static Log log = LogFactory.getLog( CatalogDS9.class ) ;

	private class ContourLevel {
		public String ident ;
		public double level ;
		public List<List<CatalogDS9Record>> contour ;
	}

	private List<ContourLevel> catalog ;

	private Converter converter ;
	private Projector projector ;

	public CatalogDS9( Converter converter, Projector projector ) {
		this.converter = converter ;
		this.projector = projector ;
	}

	@SuppressWarnings("unchecked")
	private List<ContourLevel> unsafecast( Object hashtable ) {
		return (List<ContourLevel>) hashtable ;
	}

	public void addAllCatalogRecord() {
		ContourLevel contour ;
		List<CatalogDS9Record> entry ;
		CatalogDS9Record element ;
		String key, uri[] ;
		Reader reader ;
		double[] lev ;

		key = getClass().getSimpleName()+":"+getName() ;
		catalog = unsafecast( Registry.retrieve( key ) ) ;
		if ( catalog == null ) {
			catalog = new java.util.Vector<ContourLevel>() ;
			Registry.register( key, catalog ) ;
		} else
			return ;

		try {
			if ( getLevel() != null ) {
				reader = reader( getLevel().getUrl() ) ;
				lev = level( reader ) ;
				Arrays.sort( lev ) ;
				uri = new String[lev.length] ;
				for ( int u=0 ; u<uri.length ; u++ )
					uri[u] = String.format( getUrl(), u ) ;
			} else {
				lev = new double[] { Double.NEGATIVE_INFINITY } ;
				uri = new String[] { getUrl() } ;
			}

			for ( int u=0 ; u<uri.length ; u++ ) {
				contour = new ContourLevel() ;
				contour.ident = URLEncoder.encode( uri[u], "UTF-8" ) ;
				contour.level = lev[u] ;
				contour.contour = new java.util.Vector<List<CatalogDS9Record>>() ;

				reader = reader( uri[u] ) ;
				while ( ( element = record( reader ) ) != null ) {
					entry = new java.util.Vector<CatalogDS9Record>() ;
					contour.contour.add( entry ) ;

					for ( astrolabe.model.CatalogDS9Record select : getCatalogDS9Record() ) {
						select.copyValues( element ) ;
						if ( Boolean.parseBoolean( element.getSelect() ) ) {
							entry.add( element ) ;

							break ;
						}
					}
				}
				reader.close() ;

				catalog.add( contour ) ;
			}
		} catch ( URISyntaxException e ) {
			throw new RuntimeException( e.toString() ) ;
		} catch ( MalformedURLException e ) {
			throw new RuntimeException( e.toString() ) ;
		} catch ( IOException e ) {
			throw new RuntimeException( e.toString() ) ;
		}
	}

	public void delAllCatalogRecord() {
		catalog.clear() ;
	}

	public CatalogRecord[] getCatalogRecord() {
		return catalog.toArray( new CatalogRecord[0] ) ;
	}

	public void headPS( ApplicationPostscriptStream ps ) {
	}

	public void emitPS( ApplicationPostscriptStream ps ) {
		astrolabe.model.Body body ;
		BodyAreal bodyDS9 ;
		astrolabe.model.Position pm ;

		for ( ContourLevel contour : catalog ) {

			ps.script( Configuration.getValue( this, contour.ident, "" ) ) ;

			for ( List<CatalogDS9Record> entry : contour.contour ) {
				for ( CatalogDS9Record element : entry ) {
					body = new astrolabe.model.Body() ;
					body.setBodyAreal( new astrolabe.model.BodyAreal() ) ;

					body.getBodyAreal().setAnnotation( element.getAnnotation() ) ;

					body.getBodyAreal().setBodyArealTypeChoice( new astrolabe.model.BodyArealTypeChoice() ) ;

					for ( Coordinate eq : element.list() ) {
						pm = new astrolabe.model.Position() ;
						// astrolabe.model.AngleType
						pm.setLon( new astrolabe.model.Lon() ) ;
						pm.getLon().setRational( new astrolabe.model.Rational() ) ;
						pm.getLon().getRational().setValue( eq.x ) ;  
						// astrolabe.model.AngleType
						pm.setLat( new astrolabe.model.Lat() ) ;
						pm.getLat().setRational( new astrolabe.model.Rational() ) ;
						pm.getLat().getRational().setValue( eq.y ) ;  

						body.getBodyAreal().getBodyArealTypeChoice().addPosition( pm ) ;
					}

					try {
						body.validate() ;
					} catch ( ValidationException e ) {
						throw new RuntimeException( e.toString() ) ;
					}

					bodyDS9 = new BodyAreal( converter, projector ) ;
					body.getBodyAreal().copyValues( bodyDS9 ) ;

					bodyDS9.register() ;
					ps.op( "gsave" ) ;

					bodyDS9.headPS( ps ) ;
					bodyDS9.emitPS( ps ) ;
					bodyDS9.tailPS( ps ) ;

					ps.op( "grestore" ) ;
					bodyDS9.degister() ;
				}
			}
		}
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
		boolean m ;

		b = new StringBuilder() ;
		c = new char[1] ;
		m = false ;

		while ( catalog.read( c, 0, 1 )>-1 ) {
			b.append( c ) ;
			if ( c[0] == '\n' ) {
				if ( m )
					return record( b.substring( 0, b.length()-1 ) ) ;
				m = true ;
			} else
				m = false ;
		}

		return null ;
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

	private double[] level( java.io.Reader catalog ) throws IOException {
		List<String> input = new java.util.Vector<String>() ;
		BufferedReader b ;
		String entry ;
		double[] r ;

		b = new BufferedReader( catalog ) ;
		while ( ( entry = b.readLine() ) != null )
			input.add( entry ) ;

		r = new double[ input.size() ] ;
		for ( int e=0 ; e<input.size() ; e++ )
			r[e] = Double.valueOf( input.get( e ) ) ;

		return r ;
	}
}
