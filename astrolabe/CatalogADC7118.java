
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

import caa.CAA2DCoordinate;
import caa.CAACoordinateTransformation;
import caa.CAAPrecession;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

@SuppressWarnings("serial")
public class CatalogADC7118 extends astrolabe.model.CatalogADC7118 implements Catalog {

	private final static double DEFAULT_THRESHOLDSCALE = 5.2 ;

	private final static int C_CHUNK = 96+1/*0x0a*/ ;

	private final static Log log = LogFactory.getLog( CatalogADC7118.class ) ;

	private Hashtable<String, CatalogADC7118Record> catalog ;

	private Projector projector ;

	public CatalogADC7118( Peer peer, Projector projector ) {
		Geometry fov, fovu, fove ;
		String key ;

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

		key = getClass().getSimpleName()+":"+getName() ;
		catalog = unsafecast( Registry.retrieve( key ) ) ;
		if ( catalog == null ) {
			catalog = new Hashtable<String, CatalogADC7118Record>() ;
			Registry.register( key, catalog ) ;
		}
	}

	@SuppressWarnings("unchecked")
	private Hashtable<String, CatalogADC7118Record> unsafecast( Object value ) {
		return (Hashtable<String, CatalogADC7118Record>) value ;
	}

	public void addAllCatalogRecord() {
		Reader reader ;
		CatalogADC7118Record record ;

		if ( catalog.size()>0 )
			return ;

		try {
			reader = reader() ;

			while ( ( record = record( reader ) ) != null ) {
				try {
					record.recognize() ;
				} catch ( ParameterNotValidException e ) {
					String msg ;

					msg = MessageCatalog.message( ApplicationConstant.GC_APPLICATION, ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
					msg = MessageFormat.format( msg, new Object[] { e.getMessage(), record.Name } ) ;
					log.warn( msg ) ;

					continue ;
				}

				record.register() ;

				for ( astrolabe.model.CatalogADC7118Record select : getCatalogADC7118Record() ) {
					select.setupCompanion( record ) ;
					if ( Boolean.parseBoolean( record.getSelect() ) ) {
						catalog.put( record.Name, record ) ;

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
		double[] p, a ;
		Vector vp, va ;
		List<CatalogADC7118Record> catalog ;
		Comparator<CatalogADC7118Record> comparator = new Comparator<CatalogADC7118Record>() {
			public int compare( CatalogADC7118Record a, CatalogADC7118Record b ) {
				double amag, bmag ;

				amag = Double.valueOf( a.mag ).doubleValue() ;
				bmag = Double.valueOf( b.mag ).doubleValue() ;

				return amag<bmag?-1:
					amag>bmag?1:
						0 ;
			}
		} ;
		astrolabe.model.Body body ;
		astrolabe.model.OutlineElliptical outlineElliptical ;
		PostscriptEmitter pe ;
		astrolabe.model.Position pm ;
		CAA2DCoordinate ceq ;
		double epoch, ra, de ;

		fov = (Geometry) Registry.retrieve( ApplicationConstant.GC_FOVEFF ) ;

		epoch = ( (Double) AstrolabeRegistry.retrieve( ApplicationConstant.GC_EPOCH ) ).doubleValue() ;

		threshold = Configuration.getValue( Configuration.getClassNode( this, getName(), null ),
				ApplicationConstant.PK_CATALOG_THRESHOLDSCALE, DEFAULT_THRESHOLDSCALE ) ;

		catalog = Arrays.asList( this.catalog
				.values()
				.toArray( new CatalogADC7118Record[0] ) ) ;
		Collections.sort( catalog, comparator ) ;

		for ( CatalogADC7118Record record : catalog ) {
			ceq = CAAPrecession.PrecessEquatorial( record.RA()[0], record.de()[0], 2451545./*J2000*/, epoch ) ;
			ra = CAACoordinateTransformation.HoursToDegrees( ceq.X() ) ;
			de = ceq.Y() ;
			ceq.delete() ;

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
			if ( record.size.length()>0 ) {
				d = Double.valueOf( record.size )/60. ;
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
				body.getBodyAreal().setName( record.Name ) ;

				body.getBodyAreal().setNature( record.getNature() ) ;

				body.getBodyAreal().setAnnotation( record.getAnnotation() ) ;

				body.getBodyAreal().setBodyArealTypeChoice( new astrolabe.model.BodyArealTypeChoice() ) ;
				outlineElliptical = new astrolabe.model.OutlineElliptical() ;
				body.getBodyAreal().getBodyArealTypeChoice().setOutlineElliptical( outlineElliptical ) ;

				outlineElliptical.setProportion( 1 ) ;
				outlineElliptical.setPA( 0 ) ;
				outlineElliptical.setRational( new astrolabe.model.Rational() ) ;
				outlineElliptical.getRational().setValue( d ) ;
				outlineElliptical.setPosition( pm ) ;
			} else {
				body.setBodyStellar( new astrolabe.model.BodyStellar() ) ;
				if ( getName() == null )
					body.getBodyStellar().setName( ApplicationConstant.GC_NS_CAT ) ;
				else
					body.getBodyStellar().setName( ApplicationConstant.GC_NS_CAT+getName() ) ;
				AstrolabeFactory.modelOf( body.getBodyStellar(), false ) ;
				body.getBodyStellar().setName( record.Name ) ;

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

	public CatalogADC7118Record record( java.io.Reader catalog ) {
		CatalogADC7118Record r = null ;
		char[] cl ;
		String rl ;

		cl = new char[C_CHUNK] ;

		try {
			while ( catalog.read( cl, 0, C_CHUNK )>-1 ) {
				rl = new String( cl ) ;
				rl = rl.substring( 0, rl.length()-1 ) ;

				if ( ( r = record( rl ) ) != null )
					break ;
			}
		} catch ( IOException e ) {
			throw new RuntimeException( e.toString() ) ;
		}

		return r ;
	}

	private CatalogADC7118Record record( String record ) {
		CatalogADC7118Record r = null ;

		try {
			r = new CatalogADC7118Record( record ) ;
		} catch ( ParameterNotValidException e ) {
			String msg ;

			msg = MessageCatalog.message( ApplicationConstant.GC_APPLICATION, ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
			msg = MessageFormat.format( msg, new Object[] { e.getMessage(), "\""+record+"\"" } ) ;
			log.warn( msg ) ;
		}

		return r ;
	}
}
