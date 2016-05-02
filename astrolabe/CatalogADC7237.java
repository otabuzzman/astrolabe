
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

import caa.CAACoordinateTransformation;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

@SuppressWarnings("serial")
public class CatalogADC7237 extends astrolabe.model.CatalogADC7237 implements Catalog {

	private final static double DEFAULT_THRESHOLDSCALE = 5.2 ;

	private final static int C_CHUNK = 520+1/*0x0a*/ ;

	private final static Log log = LogFactory.getLog( CatalogADC7237.class ) ;

	private Hashtable<String, CatalogADC7237Record> catalog ;

	private Projector projector ;

	public CatalogADC7237( Projector projector ) {
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
	private Hashtable<String, CatalogADC7237Record> unsafecast( Object hashtable ) {
		return (Hashtable<String, CatalogADC7237Record>) hashtable ;
	}

	public void addAllCatalogRecord() {
		Reader reader ;
		CatalogADC7237Record record ;
		String key ;

		key = getClass().getSimpleName()+":"+getName() ;
		catalog = unsafecast( Registry.retrieve( key ) ) ;
		if ( catalog == null ) {
			catalog = new Hashtable<String, CatalogADC7237Record>() ;
			Registry.register( key, catalog ) ;
		} else
			return ;

		try {
			reader = reader() ;

			while ( ( record = record( reader ) ) != null ) {
				try {
					record.recognize() ;
				} catch ( ParameterNotValidException e ) {
					String msg ;

					msg = MessageCatalog.message( ApplicationConstant.GC_APPLICATION, ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
					msg = MessageFormat.format( msg, new Object[] { e.getMessage(), record.PGC } ) ;
					log.warn( msg ) ;

					continue ;
				}

				record.register() ;

				for ( astrolabe.model.CatalogADC7237Record select : getCatalogADC7237Record() ) {
					select.setupCompanion( record ) ;
					if ( Boolean.parseBoolean( record.getSelect() ) ) {
						catalog.put( record.PGC, record ) ;

						break ;
					}
				}
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

	public void delAllCatalogRecord() {
		catalog.clear() ;
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
		double threshold, d, s ;
		double d25, r25, pa ;
		double[] p, a ;
		Vector vp, va ;
		List<CatalogADC7237Record> catalog ;
		Comparator<CatalogADC7237Record> comparator = new Comparator<CatalogADC7237Record>() {
			public int compare( CatalogADC7237Record a, CatalogADC7237Record b ) {
				double alogD25, blogD25 ;

				alogD25 = Double.valueOf( a.logD25 ).doubleValue() ;
				blogD25 = Double.valueOf( b.logD25 ).doubleValue() ;

				return alogD25>blogD25?-1:
					alogD25<blogD25?1:
						0 ;
			}
		} ;
		astrolabe.model.Body body ;
		astrolabe.model.ShapeElliptical shapeElliptical ;
		PostscriptEmitter pe ;
		astrolabe.model.Position pm ;
		double ra, de ;

		fov = (Geometry) Registry.retrieve( ApplicationConstant.GC_FOVEFF ) ;

		threshold = Configuration.getValue(
				Configuration.getClassNode( this, getName(), null ),
				ApplicationConstant.PK_CATALOG_THRESHOLDSCALE, DEFAULT_THRESHOLDSCALE ) ;

		catalog = Arrays.asList( this.catalog
				.values()
				.toArray( new CatalogADC7237Record[0] ) ) ;
		Collections.sort( catalog, comparator ) ;

		for ( CatalogADC7237Record record : catalog ) {
			ra = CAACoordinateTransformation.HoursToDegrees( record.RA()[0] ) ;
			de = record.de()[0] ;
			p = projector.project( ra, de ) ;
			if ( ! fov.covers( new GeometryFactory().createPoint( new JTSCoordinate( p ) ) ) )
				continue ;

			record.register() ;
			body = new astrolabe.model.Body() ;

			pm = new astrolabe.model.Position() ;
			// astrolabe.model.SphericalType
			pm.setR( new astrolabe.model.R() ) ;
			pm.getR().setValue( 1 ) ;
			// astrolabe.model.AngleType
			pm.setPhi( new astrolabe.model.Phi() ) ;
			pm.getPhi().setRational( new astrolabe.model.Rational() ) ;
			pm.getPhi().getRational().setValue( ra ) ;  
			// astrolabe.model.AngleType
			pm.setTheta( new astrolabe.model.Theta() ) ;
			pm.getTheta().setRational( new astrolabe.model.Rational() ) ;
			pm.getTheta().getRational().setValue( de ) ;  

			d = 0 ;
			s = 0 ;
			d25 = Double.valueOf( record.logD25 ) ;
			if ( d25<9.99 ) {
				d = java.lang.Math.pow( 10, d25 )*.1/60. ;
				a = projector.project( ra+d, de ) ;
				vp = new Vector( p ) ;
				va = new Vector( a ) ;
				s = va.sub( vp ).abs() ;
			}

			if ( s>threshold ) {
				body.setBodyAreal( new astrolabe.model.BodyAreal() ) ;
				if ( getName() == null )
					body.getBodyAreal().setName( ApplicationConstant.GC_NS_CAT ) ;
				else
					body.getBodyAreal().setName( ApplicationConstant.GC_NS_CAT+getName() ) ;
				AstrolabeFactory.modelOf( body.getBodyAreal(), false ) ;
				body.getBodyAreal().setName( record.PGC ) ;

				body.getBodyAreal().setNature( record.getNature() ) ;

				body.getBodyAreal().setAnnotation( record.getAnnotation() ) ;

				body.getBodyAreal().setBodyArealTypeChoice( new astrolabe.model.BodyArealTypeChoice() ) ;
				shapeElliptical = new astrolabe.model.ShapeElliptical() ;
				body.getBodyAreal().getBodyArealTypeChoice().setShapeElliptical( shapeElliptical ) ;

				r25 = Double.valueOf( record.logR25 ) ;
				shapeElliptical.setProportion( r25<9.99?1/java.lang.Math.pow( 10, r25 ):1 ) ;
				pa = Double.valueOf( record.PA ) ;
				shapeElliptical.setPA( pa<999?pa:0 ) ;
				shapeElliptical.setRational( new astrolabe.model.Rational() ) ;
				shapeElliptical.getRational().setValue( d ) ;
				shapeElliptical.setPosition( pm ) ;
			} else {
				body.setBodyStellar( new astrolabe.model.BodyStellar() ) ;
				if ( getName() == null )
					body.getBodyStellar().setName( ApplicationConstant.GC_NS_CAT ) ;
				else
					body.getBodyStellar().setName( ApplicationConstant.GC_NS_CAT+getName() ) ;
				AstrolabeFactory.modelOf( body.getBodyStellar(), false ) ;
				body.getBodyStellar().setName( record.PGC ) ;

				body.getBodyStellar().setScript( record.getScript() ) ;
				body.getBodyStellar().setAnnotation( record.getAnnotation() ) ;

				body.getBodyStellar().setPosition( pm ) ;
			}

			try {
				body.validate() ;
			} catch ( ValidationException e ) {
				throw new RuntimeException( e.toString() ) ;
			}

			pe = AstrolabeFactory.companionOf( body , projector ) ;

			ps.operator.gsave() ;

			pe.headPS( ps ) ;
			pe.emitPS( ps ) ;
			pe.tailPS( ps ) ;

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
