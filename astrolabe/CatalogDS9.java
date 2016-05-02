
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.xml.ValidationException;

import com.vividsolutions.jts.geom.Geometry;

@SuppressWarnings("serial")
public class CatalogDS9 extends astrolabe.model.CatalogDS9 implements Catalog {

	private final static Log log = LogFactory.getLog( CatalogDS9.class ) ;

	private class ContourLevel {
		public double level ;
		public List<List<CatalogDS9Record>> contour ;
	}

	private List<ContourLevel> catalog ;

	private Projector projector ;

	public CatalogDS9( Projector projector ) {
		this.projector = projector ;
	}

	public void register() {
		Geometry fov, fovu, fove ;

		if ( getFov() == null ) {
			fov = (Geometry) Registry.retrieve( ApplicationConstant.GC_FOVUNI ) ;
		} else {
			fovu = (Geometry) Registry.retrieve( ApplicationConstant.GC_FOVUNI ) ;
			fove = (Geometry) Registry.retrieve( getFov() ) ;
			fov = fovu.intersection( fove ) ;
		}
		Registry.register( ApplicationConstant.GC_FOVEFF, fov ) ;
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
				uri = new String[lev.length] ;
				for ( int u=0 ; u<uri.length ; u++ )
					uri[u] = String.format( getUrl(), u ) ;
			} else {
				lev = new double[] { Double.NEGATIVE_INFINITY } ;
				uri = new String[] { getUrl() } ;
			}

			for ( int u=0 ; u<uri.length ; u++ ) {
				contour = new ContourLevel() ;
				contour.level = lev[u] ;
				contour.contour = new java.util.Vector<List<CatalogDS9Record>>() ;

				reader = reader( uri[u] ) ;
				while ( ( element = record( reader ) ) != null ) {
					entry = new java.util.Vector<CatalogDS9Record>() ;
					contour.contour.add( entry ) ;

					for ( astrolabe.model.CatalogDS9Record select : getCatalogDS9Record() ) {
						select.setupCompanion( element ) ;
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
		List<ContourLevel> catalog ;
		Comparator<ContourLevel> comparator = new Comparator<ContourLevel>() {
			public int compare( ContourLevel a, ContourLevel b ) {
				return a.level<b.level?-1:
					a.level>b.level?1:
						0 ;
			}
		} ;
		astrolabe.model.Body body ;
		BodyDS9 bodyDS9 ;
		astrolabe.model.Position pm ;
		List<double[]> list ;
		double eq[] ;

		catalog = Arrays.asList( this.catalog
				.toArray( new ContourLevel[0] ) ) ;
		Collections.sort( catalog, comparator ) ;

		for ( ContourLevel contour : catalog ) {
			for ( List<CatalogDS9Record> entry : contour.contour ) {
				for ( CatalogDS9Record element : entry ) {
					body = new astrolabe.model.Body() ;
					body.setBodyAreal( new astrolabe.model.BodyAreal() ) ;
					if ( getName() == null )
						body.getBodyAreal().setName( ApplicationConstant.GC_NS_CAT ) ;
					else
						body.getBodyAreal().setName( ApplicationConstant.GC_NS_CAT+getName() ) ;
					ApplicationFactory.modelOf( body.getBodyAreal(), false ) ;
					body.getBodyAreal().setName( null ) ;

					body.getBodyAreal().setNature( element.getNature() ) ;

					body.getBodyAreal().setAnnotation( element.getAnnotation() ) ;

					body.getBodyAreal().setBodyArealTypeChoice( new astrolabe.model.BodyArealTypeChoice() ) ;

					list = element.list() ;
					for ( int p=0 ; p<list.size() ; p++ ) {
						eq = list.get( p ) ;
						pm = new astrolabe.model.Position() ;
						// astrolabe.model.SphericalType
						pm.setR( new astrolabe.model.R() ) ;
						pm.getR().setValue( 1 ) ;
						// astrolabe.model.AngleType
						pm.setPhi( new astrolabe.model.Phi() ) ;
						pm.getPhi().setRational( new astrolabe.model.Rational() ) ;
						pm.getPhi().getRational().setValue( eq[0] ) ;  
						// astrolabe.model.AngleType
						pm.setTheta( new astrolabe.model.Theta() ) ;
						pm.getTheta().setRational( new astrolabe.model.Rational() ) ;
						pm.getTheta().getRational().setValue( eq[1] ) ;  

						body.getBodyAreal().getBodyArealTypeChoice().addPosition( pm ) ;
					}

					try {
						body.validate() ;
					} catch ( ValidationException e ) {
						throw new RuntimeException( e.toString() ) ;
					}

					bodyDS9 = new BodyDS9( projector ) ;
					body.getBodyAreal().setupCompanion( bodyDS9 ) ;
					bodyDS9.register() ;

					ps.operator.gsave() ;

					bodyDS9.headPS( ps ) ;
					bodyDS9.emitPS( ps ) ;
					bodyDS9.tailPS( ps ) ;

					ps.operator.grestore() ;
				}
			}
		}
	}

	public void tailPS( ApplicationPostscriptStream ps ) {
	}

	public Reader reader( String uri ) throws URISyntaxException, MalformedURLException {
		URI cURI ;
		URL cURL ;
		File cFile ;
		InputStream cCon ;
		GZIPInputStream cGZ ;

		cURI = new URI( uri ) ;
		if ( cURI.isAbsolute() ) {
			cFile = new File( cURI ) ;	
		} else {
			cFile = new File( cURI.getPath() ) ;
		}
		cURL = cFile.toURL() ;

		try {
			cCon = cURL.openStream() ;

			cGZ = new GZIPInputStream( cCon ) ;
			return new InputStreamReader( cGZ ) ;
		} catch ( IOException egz ) {
			try {
				cCon = cURL.openStream() ;

				return new InputStreamReader( cCon ) ;
			} catch ( IOException eis ) {
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
