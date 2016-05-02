
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
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.xml.ValidationException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

@SuppressWarnings("serial")
public class CatalogADC7237 extends astrolabe.model.CatalogADC7237 implements Catalog {

	private final static int C_CHUNK = 520+1/*0x0a*/ ;

	private final static Log log = LogFactory.getLog( CatalogADC7237.class ) ;

	private Hashtable<String, CatalogADC7237Record> catalog = new Hashtable<String, CatalogADC7237Record>() ;

	private Projector projector ;

	public CatalogADC7237( Peer peer, Projector projector ) {
		Geometry fov, fovu, fove ;

		peer.setupCompanion( this ) ;

		this.projector = projector ;

		if ( getFov() == null ) {
			fov = (Geometry) AstrolabeRegistry.retrieve( ApplicationConstant.GC_FOVUNI ) ;
		} else {
			fovu = (Geometry) AstrolabeRegistry.retrieve( ApplicationConstant.GC_FOVUNI ) ;
			fove = (Geometry) AstrolabeRegistry.retrieve( getFov() ) ;
			fov = fovu.intersection( fove ) ;
		}
		Registry.register( ApplicationConstant.GC_FOVEFF, fov ) ;
	}

	public void addAllCatalogRecord() {
		Reader reader ;
		CatalogADC7237Record record ;

		try {
			reader = reader() ;

			while ( ( record = record( reader ) ) != null ) {
				try {
					record.validate() ;
				} catch ( ParameterNotValidException e ) {
					String msg ;

					msg = MessageCatalog.message( ApplicationConstant.GC_APPLICATION, ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
					msg = MessageFormat.format( msg, new Object[] { e.getMessage(), record.PGC } ) ;
					log.warn( msg ) ;

					continue ;
				}

				catalog.put( record.PGC, record ) ;
			}

			reader.close() ;
		} catch ( URISyntaxException e ) {
			throw new RuntimeException( e.toString() ) ;
		} catch ( MalformedURLException e ) {
			throw new RuntimeException( e.toString() ) ;
		} catch ( IOException e ) {
			throw new RuntimeException( e.toString() ) ;
		}
	}

	public CatalogRecord getCatalogRecord( String ident ) {
		return catalog.get( ident ) ;
	}

	public CatalogRecord[] getCatalogRecord() {
		return catalog.values().toArray( new CatalogRecord[0] ) ;
	}

	public void headPS( AstrolabePostscriptStream ps ) {
	}

	public void emitPS( AstrolabePostscriptStream ps ) {
		Geometry fov ;
		double[] xy ;
		ParserAttribute parser ;
		List<CatalogADC7237Record> catalog ;
		Comparator<CatalogADC7237Record> comparator = new Comparator<CatalogADC7237Record>() {
			public int compare( CatalogADC7237Record a, CatalogADC7237Record b ) {
				double xlogD25, ylogD25 ;

				xlogD25 = Double.valueOf( a.logD25 ).doubleValue() ;
				ylogD25 = Double.valueOf( b.logD25 ).doubleValue() ;

				return xlogD25>ylogD25?-1:
					xlogD25<ylogD25?1:
						0 ;
			}
		} ;
		astrolabe.model.Body body ;
		BodyStellar bodyStellar ;

		fov = (Geometry) Registry.retrieve( ApplicationConstant.GC_FOVEFF ) ;

		parser = (ParserAttribute) Registry.retrieve( ApplicationConstant.GC_PARSER ) ;

		catalog = Arrays.asList( this.catalog
				.values()
				.toArray( new CatalogADC7237Record[0] ) ) ;
		Collections.sort( catalog, comparator ) ;

		for ( CatalogRecord record : catalog ) {
			xy = projector.project( record.RA()[0], record.de()[0] ) ;
			if ( ! fov.covers( new GeometryFactory().createPoint( new JTSCoordinate( xy ) ) ) )
				continue ;

			record.register() ;

			if ( getRestrict() != null )
				if ( ! parser.booleanValue( getRestrict().getValue() ) )
					continue ;

			body = new astrolabe.model.Body() ;
			body.setBodyStellar( new astrolabe.model.BodyStellar() ) ;
			if ( getName() == null )
				body.getBodyStellar().setName( ApplicationConstant.GC_NS_CAT ) ;
			else
				body.getBodyStellar().setName( ApplicationConstant.GC_NS_CAT+getName() ) ;
			AstrolabeFactory.modelOf( body.getBodyStellar(), false ) ;

			body.getBodyStellar().setScript( getScript() ) ;
			body.getBodyStellar().setAnnotation( getAnnotation() ) ;

			for ( astrolabe.model.Select select : getSelect() ) {
				if ( ! parser.booleanValue( select.getValue() ) )
					continue ;
				body.getBodyStellar().setAnnotation( select.getAnnotation() ) ;
				if ( select.getScript() != null )
					body.getBodyStellar().setScript( select.getScript() ) ;
				break ;
			}

			try {
				record.toModel( body ) ;
			} catch ( ValidationException e ) {
				throw new RuntimeException( e.toString() ) ;
			}

			bodyStellar = new BodyStellar( body.getBodyStellar(), projector ) ;

			ps.operator.gsave() ;

			bodyStellar.headPS( ps ) ;
			bodyStellar.emitPS( ps ) ;
			bodyStellar.tailPS( ps ) ;

			ps.operator.grestore() ;
		}
	}

	public void tailPS( AstrolabePostscriptStream ps ) {
	}

	public Reader reader() throws URISyntaxException, MalformedURLException {
		InputStreamReader r ;
		URI cURI ;
		URL cURL ;
		File cFile ;
		InputStream cIS ;
		GZIPInputStream cF ;

		cURI = new URI( getUrl() ) ;
		if ( cURI.isAbsolute() ) {
			cFile = new File( cURI ) ;	
		} else {
			cFile = new File( cURI.getPath() ) ;
		}
		cURL = cFile.toURL() ;

		try {
			cIS = cURL.openStream() ;
		} catch ( IOException e ) {
			throw new RuntimeException ( e.toString() ) ;
		}

		try {
			cF = new GZIPInputStream( cIS ) ;
			r = new InputStreamReader( cF ) ;
		} catch ( IOException e ) {
			r = new InputStreamReader( cIS ) ;
		}

		return r ;
	}

	public CatalogADC7237Record record( java.io.Reader catalog ) {
		CatalogADC7237Record r = null ;
		char[] cl ;
		int o ;
		String rl ;

		cl = new char[C_CHUNK] ;
		o = 0 ;

		try {
			while ( catalog.read( cl, o++, 1 ) == 1 ) {
				if ( cl[o-1] == '\n' ) {
					if ( o<C_CHUNK ) {
						for ( o-- ; o<C_CHUNK ; o++ ) {
							cl[o] = ' ' ;
						}
						cl[o-1] = '\n' ;
					}
					rl = new String( cl ) ;
					o = 0 ;
					if ( ( r = record( rl ) ) != null )
						break ;
				}
			}
		} catch ( IOException e ) {
			throw new RuntimeException( e.toString() ) ;
		}

		return r ;
	}

	private CatalogADC7237Record record( String record ) {
		CatalogADC7237Record r = null ;

		try {
			r = new CatalogADC7237Record( record ) ;
		} catch ( ParameterNotValidException e ) {
			String msg ;

			msg = MessageCatalog.message( ApplicationConstant.GC_APPLICATION, ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
			msg = MessageFormat.format( msg, new Object[] { e.getMessage(), "\""+record+"\"" } ) ;
			log.warn( msg ) ;
		}

		return r ;
	}
}
