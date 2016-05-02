
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
public class CatalogADC1239T extends astrolabe.model.CatalogADC1239T implements Catalog {

	private final static int C_CHUNK = 350+1/*0x0a*/ ;

	private final static Log log = LogFactory.getLog( CatalogADC1239T.class ) ;

	private Hashtable<String, CatalogADC1239TRecord> catalog ;

	private Projector projector ;

	public CatalogADC1239T( Projector projector ) {
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
	private Hashtable<String, CatalogADC1239TRecord> unsafecast( Object hashtable ) {
		return (Hashtable<String, CatalogADC1239TRecord>) hashtable ;
	}

	public void addAllCatalogRecord() {
		Reader reader ;
		CatalogADC1239TRecord record ;
		String key, ident ;

		key = getClass().getSimpleName()+":"+getName() ;
		catalog = unsafecast( Registry.retrieve( key ) ) ;
		if ( catalog == null ) {
			catalog = new Hashtable<String, CatalogADC1239TRecord>() ;
			Registry.register( key, catalog ) ;
		} else
			return ;

		try {
			reader = reader() ;

			while ( ( record = record( reader ) ) != null ) {
				try {
					record.inspect() ;
				} catch ( ParameterNotValidException e ) {
					String msg ;

					msg = MessageCatalog.message( ApplicationConstant.GC_APPLICATION, ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
					msg = MessageFormat.format( msg, new Object[] { e.getMessage(), "\""+record.TYC+"\"" } ) ;
					log.warn( msg ) ;

					continue ;
				}

				record.register() ;

				for ( astrolabe.model.CatalogADC1239TRecord select : getCatalogADC1239TRecord() ) {
					select.setupCompanion( record ) ;
					if ( Boolean.parseBoolean( record.getSelect() ) ) {
						ident = record.TYC
						.replaceAll( "[ ]+", "-" ) ;
						catalog.put( ident, record ) ;

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

	public CatalogRecord[] getCatalogRecord() {
		return catalog.values().toArray( new CatalogRecord[0] ) ;
	}

	public void headPS( AstrolabePostscriptStream ps ) {
	}

	public void emitPS( AstrolabePostscriptStream ps ) {
		Geometry fov ;
		double[] xy ;
		List<CatalogADC1239TRecord> catalog ;
		Comparator<CatalogADC1239TRecord> comparator = new Comparator<CatalogADC1239TRecord>() {
			public int compare( CatalogADC1239TRecord a, CatalogADC1239TRecord b ) {
				double amag, bmag ;

				amag = Double.valueOf( a.Vmag ).doubleValue() ;
				bmag = Double.valueOf( b.Vmag ).doubleValue() ;

				return amag<bmag?-1:
					amag>bmag?1:
						0 ;
			}
		} ;
		astrolabe.model.Body body ;
		BodyStellar bodyStellar ;
		astrolabe.model.Position pm ;
		CAA2DCoordinate cpm, ceq ;
		double epoch, ra, de, pmRA, pmDE ;

		fov = (Geometry) Registry.retrieve( ApplicationConstant.GC_FOVEFF ) ;

		epoch = ( (Double) Registry.retrieve( ApplicationConstant.GC_EPOCH ) ).doubleValue() ;

		catalog = Arrays.asList( this.catalog
				.values()
				.toArray( new CatalogADC1239TRecord[0] ) ) ;
		Collections.sort( catalog, comparator ) ;

		for ( CatalogADC1239TRecord record : catalog ) {
			pmRA = 0 ;
			if ( record.pmRA.length()>0 )
				pmRA = new Double( record.pmRA ).doubleValue() ;
			pmDE = 0 ;
			if ( record.pmDE.length()>0 )
				pmDE = new Double( record.pmDE ).doubleValue() ;
			cpm = CAAPrecession.AdjustPositionUsingUniformProperMotion(
					epoch-2451545., record.RA(), record.de(), pmRA/1000., pmDE/1000. ) ;
			ceq = CAAPrecession.PrecessEquatorial( cpm.X(), cpm.Y(), 2451545./*J2000*/, epoch ) ;
			ra = CAACoordinateTransformation.HoursToDegrees( ceq.X() ) ;
			de = ceq.Y() ;
			cpm.delete() ;
			ceq.delete() ;

			xy = projector.project( ra, de ) ;
			if ( ! fov.covers( new GeometryFactory().createPoint( new JTSCoordinate( xy ) ) ) )
				continue ;

			record.register() ;

			body = new astrolabe.model.Body() ;
			body.setBodyStellar( new astrolabe.model.BodyStellar() ) ;
			if ( getName() == null )
				body.getBodyStellar().setName( ApplicationConstant.GC_NS_CAT ) ;
			else
				body.getBodyStellar().setName( ApplicationConstant.GC_NS_CAT+getName() ) ;
			AstrolabeFactory.modelOf( body.getBodyStellar(), false ) ;
			body.getBodyStellar().setName( record.TYC.replaceAll( "[ ]+", "-" ) ) ;

			body.getBodyStellar().setScript( record.getScript() ) ;
			body.getBodyStellar().setAnnotation( record.getAnnotation() ) ;

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

			body.getBodyStellar().setPosition( pm ) ;

			try {
				body.validate() ;
			} catch ( ValidationException e ) {
				throw new RuntimeException( e.toString() ) ;
			}

			bodyStellar = new BodyStellar( projector ) ;
			body.getBodyStellar().setupCompanion( bodyStellar ) ;
			bodyStellar.register() ;

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
		URI cURI ;
		URL cURL ;
		File cFile ;
		InputStream cCon ;
		GZIPInputStream cGZ ;

		cURI = new URI( getUrl() ) ;
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

	public CatalogADC1239TRecord record( java.io.Reader catalog ) {
		CatalogADC1239TRecord r = null ;
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

	private CatalogADC1239TRecord record( String record ) {
		CatalogADC1239TRecord r = null ;

		try {
			r = new CatalogADC1239TRecord( record ) ;
		} catch ( ParameterNotValidException e ) {
			String msg ;

			msg = MessageCatalog.message( ApplicationConstant.GC_APPLICATION, ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
			msg = MessageFormat.format( msg, new Object[] { e.getMessage(), "\""+record+"\"" } ) ;
			log.warn( msg ) ;
		}

		return r ;
	}
}
