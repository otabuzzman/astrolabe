
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

@SuppressWarnings("serial")
public class CatalogADC1239H extends astrolabe.model.CatalogADC1239H implements PostscriptEmitter {

	private final static int C_CHUNK = 450+1/*0x0a*/ ;

	private final static Log log = LogFactory.getLog( CatalogADC1239H.class ) ;

	private Hashtable<String, CatalogADC1239HRecord> catalog ;

	private Projector projector ;

	public CatalogADC1239H( Projector projector ) {
		this.projector = projector ;
	}

	@SuppressWarnings("unchecked")
	private Hashtable<String, CatalogADC1239HRecord> unsafecast( Object hashtable ) {
		return (Hashtable<String, CatalogADC1239HRecord>) hashtable ;
	}

	public void addAllCatalogRecord() {
		Reader reader ;
		CatalogADC1239HRecord record ;
		String key ;

		key = getClass().getSimpleName()+":"+getName() ;
		catalog = unsafecast( Registry.retrieve( key ) ) ;
		if ( catalog == null ) {
			catalog = new Hashtable<String, CatalogADC1239HRecord>() ;
			Registry.register( key, catalog ) ;
		} else
			return ;

		try {
			reader = reader() ;

			while ( ( record = record( reader ) ) != null ) {
				try {
					record.inspect() ;
				} catch ( ParameterNotValidException e ) {
					log.warn( ParameterNotValidError.errmsg( record.HIP, e.getMessage() ) ) ;

					continue ;
				}

				record.register() ;

				for ( astrolabe.model.CatalogADC1239HRecord select : getCatalogADC1239HRecord() ) {
					select.copyValues( record ) ;
					if ( Boolean.parseBoolean( record.getSelect() ) ) {
						catalog.put( record.HIP, record ) ;

						break ;
					}
				}

				record.degister() ;
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

	public void headPS( ApplicationPostscriptStream ps ) {
	}

	public void emitPS( ApplicationPostscriptStream ps ) {
		List<CatalogADC1239HRecord> catalog ;
		Comparator<CatalogADC1239HRecord> comparator = new Comparator<CatalogADC1239HRecord>() {
			public int compare( CatalogADC1239HRecord a, CatalogADC1239HRecord b ) {
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
		Double Epoch ;

		Epoch = (Double) Registry.retrieve( astrolabe.Epoch.RK_EPOCH ) ;
		if ( Epoch == null )
			epoch = astrolabe.Epoch.defoult() ;
		else
			epoch = Epoch.doubleValue() ;

		catalog = Arrays.asList( this.catalog
				.values()
				.toArray( new CatalogADC1239HRecord[0] ) ) ;
		Collections.sort( catalog, comparator ) ;

		for ( CatalogADC1239HRecord record : catalog ) {
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

			record.register() ;

			body = new astrolabe.model.Body() ;
			body.setBodyStellar( new astrolabe.model.BodyStellar() ) ;
			body.getBodyStellar().setName( record.HIP ) ;
			body.getBodyStellar().initValues() ;

			body.getBodyStellar().setScript( record.getScript() ) ;
			body.getBodyStellar().setAnnotation( record.getAnnotation() ) ;

			pm = new astrolabe.model.Position() ;
			// astrolabe.model.AngleType
			pm.setLon( new astrolabe.model.Lon() ) ;
			pm.getLon().setRational( new astrolabe.model.Rational() ) ;
			pm.getLon().getRational().setValue( ra ) ;  
			// astrolabe.model.AngleType
			pm.setLat( new astrolabe.model.Lat() ) ;
			pm.getLat().setRational( new astrolabe.model.Rational() ) ;
			pm.getLat().getRational().setValue( de ) ;  

			body.getBodyStellar().setPosition( pm ) ;

			try {
				body.validate() ;
			} catch ( ValidationException e ) {
				throw new RuntimeException( e.toString() ) ;
			}

			bodyStellar = new BodyStellar( projector ) ;
			body.getBodyStellar().copyValues( bodyStellar ) ;

			bodyStellar.register() ;
			ps.operator.gsave() ;

			bodyStellar.headPS( ps ) ;
			bodyStellar.emitPS( ps ) ;
			bodyStellar.tailPS( ps ) ;

			ps.operator.grestore() ;
			bodyStellar.degister() ;

			record.degister() ;
		}
	}

	public void tailPS( ApplicationPostscriptStream ps ) {
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

	public CatalogADC1239HRecord record( java.io.Reader catalog ) {
		CatalogADC1239HRecord r = null ;
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

	private CatalogADC1239HRecord record( String record ) {
		CatalogADC1239HRecord r = null ;

		try {
			r = new CatalogADC1239HRecord( record ) ;
		} catch ( ParameterNotValidException e ) {
			log.warn( ParameterNotValidError.errmsg( '"'+record+'"', e.getMessage() ) ) ;
		}

		return r ;
	}
}
