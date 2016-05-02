
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
import java.util.Hashtable;
import java.util.zip.GZIPInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.xml.ValidationException;

import com.vividsolutions.jts.geom.Geometry;

@SuppressWarnings("serial")
public class CatalogADC6049 extends astrolabe.model.CatalogADC6049 implements Catalog {

	private final static int C_CHUNK18 = 25+1/*0x0a*/ ;
	private final static int C_CHUNK20 = 29+1/*0x0a*/ ;

	private final static Log log = LogFactory.getLog( CatalogADC6049.class ) ;

	private Hashtable<String, CatalogADC6049Record> catalog = new Hashtable<String, CatalogADC6049Record>() ;

	private Projector projector ;

	private String _m = new String() ;
	private int _ch = 0 ;
	private int _ca = 0 ;
	private int _co = 0 ;

	public CatalogADC6049( Peer peer, Projector projector ) {
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
		CatalogADC6049Record record ;

		try {
			reader = reader() ;

			while ( ( record = record( reader ) ) != null ) {
				try {
					record.validate() ;
				} catch ( ParameterNotValidException e ) {
					String msg ;

					msg = MessageCatalog.message( ApplicationConstant.GC_APPLICATION, ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
					msg = MessageFormat.format( msg, new Object[] { e.getMessage(), record.con } ) ;
					log.warn( msg ) ;

					continue ;
				}

				catalog.put( record.con, record ) ;
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
		ParserAttribute parser ;
		astrolabe.model.Body body ;
		BodyAreal bodyAreal ;

		parser = (ParserAttribute) Registry.retrieve( ApplicationConstant.GC_PARSER ) ;

		for ( CatalogRecord record : catalog.values() ) {
			record.register() ;

			if ( getRestrict() != null )
				if ( ! parser.booleanValue( getRestrict().getValue() ) )
					continue ;

			body = new astrolabe.model.Body() ;
			body.setBodyAreal( new astrolabe.model.BodyAreal() ) ;
			if ( getName() == null )
				body.getBodyAreal().setName( ApplicationConstant.GC_NS_CAT ) ;
			else
				body.getBodyAreal().setName( ApplicationConstant.GC_NS_CAT+getName() ) ;
			AstrolabeFactory.modelOf( body.getBodyAreal(), false ) ;

			body.getBodyAreal().setAnnotation( getAnnotation() ) ;

			for ( astrolabe.model.Select select : getSelect() ) {
				if ( ! parser.booleanValue( select.getValue() ) )
					continue ;
				body.getBodyStellar().setAnnotation( select.getAnnotation() ) ;
				break ;
			}

			try {
				record.toModel( body ) ;
			} catch ( ValidationException e ) {
				throw new RuntimeException( e.toString() ) ;
			}

			bodyAreal = new BodyAreal( body.getBodyAreal(), projector ) ;

			ps.operator.gsave() ;

			bodyAreal.headPS( ps ) ;
			bodyAreal.emitPS( ps ) ;
			bodyAreal.tailPS( ps ) ;

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

	public CatalogADC6049Record record( java.io.Reader catalog ) {
		CatalogADC6049Record r = null ;
		char[] cl ;
		int cn ;
		String rl, rb ;
		String rc, mc ;

		try {
			if ( _m.length()==0 ) {
				cl = new char[C_CHUNK18] ;
				cn = catalog.read( cl, 0, C_CHUNK18 ) ;
				if ( cn == -1 )
					return r ;

				if ( cl[C_CHUNK18-1] == '\n' ) {
					rb = new String( cl ) ;

					_ch = C_CHUNK18 ;
					_ca = 20 ;
					_co = 24 ;
				} else {
					rb = new String( cl ) ;
					cl = new char[C_CHUNK20-C_CHUNK18] ;
					cn = catalog.read( cl, 0, C_CHUNK20-C_CHUNK18 ) ;
					rb = rb+new String( cl ) ;

					_ch = C_CHUNK20 ;
					_ca = 23 ;
					_co = 27 ;
				}

				_m = new String( rb ) ;
			} else {
				rb = new String( _m ) ;
			}

			cl = new char[_ch] ;

			while ( ( cn = catalog.read( cl, 0, _ch ) )>-1 ) {
				rl = new String( cl ) ;

				rc = rl.substring( _ca, _co ).trim() ;
				mc = _m.substring( _ca, _co ).trim() ;
				if ( rc.equals( mc ) ) {
					rb = rb+rl ;

					continue ;
				} else {
					_m = rl ;

					if ( ( r = record( rb ) ) != null )
						break ;

					rb = rl ;
				}
			}
			if ( cn == -1 ){
				r = record( rb ) ;

				_m = new String() ;
			}
		} catch ( IOException e ) {
			throw new RuntimeException( e.toString() ) ;
		}

		return r ;
	}

	private CatalogADC6049Record record( String record ) {
		CatalogADC6049Record r = null ;

		try {
			r = new CatalogADC6049Record( record ) ;
		} catch ( ParameterNotValidException e ) {
			String msg ;

			msg = MessageCatalog.message( ApplicationConstant.GC_APPLICATION, ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
			msg = MessageFormat.format( msg, new Object[] { e.getMessage(), "\""+record+"\"" } ) ;
			log.warn( msg ) ;
		}

		return r ;
	}
}
