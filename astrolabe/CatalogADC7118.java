
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

import com.vividsolutions.jts.geom.Coordinate;

import caa.CAA2DCoordinate;
import caa.CAACoordinateTransformation;
import caa.CAAPrecession;

@SuppressWarnings("serial")
public class CatalogADC7118 extends astrolabe.model.CatalogADC7118 implements PostscriptEmitter {

	// configuration key (CK_)
	private final static String CK_THRESHOLDSCALE		= "thresholdscale" ;

	private final static double DEFAULT_THRESHOLDSCALE	= 5.2 ;

	private final static int C_CHUNK = 96+1/*0x0a*/ ;

	private final static Log log = LogFactory.getLog( CatalogADC7118.class ) ;

	private Hashtable<String, CatalogADC7118Record> catalog ;

	private Converter converter ;
	private Projector projector ;

	public CatalogADC7118( Converter converter, Projector projector ) {
		this.converter = converter ;
		this.projector = projector ;
	}

	@SuppressWarnings("unchecked")
	private Hashtable<String, CatalogADC7118Record> unsafecast( Object hashtable ) {
		return (Hashtable<String, CatalogADC7118Record>) hashtable ;
	}

	public void addAllCatalogRecord() {
		Reader reader ;
		CatalogADC7118Record record ;
		String key ;

		key = getClass().getSimpleName()+":"+getName() ;
		catalog = unsafecast( Registry.retrieve( key ) ) ;
		if ( catalog == null ) {
			catalog = new Hashtable<String, CatalogADC7118Record>() ;
			Registry.register( key, catalog ) ;
		} else
			return ;

		try {
			reader = reader() ;

			while ( ( record = record( reader ) ) != null ) {
				try {
					record.inspect() ;
				} catch ( ParameterNotValidException e ) {
					log.warn( ParameterNotValidError.errmsg( record.Name, e.getMessage() ) ) ;

					continue ;
				}

				record.register() ;

				for ( astrolabe.model.CatalogADC7118Record select : getCatalogADC7118Record() ) {
					select.copyValues( record ) ;
					if ( Boolean.parseBoolean( record.getSelect() ) ) {
						catalog.put( record.Name, record ) ;

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
		double threshold, d, s ;
		Coordinate p, a ;
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
		BodyStellar star ;
		BodyAreal area ;
		astrolabe.model.ShapeElliptical shapeElliptical ;
		astrolabe.model.Position position ;
		CAA2DCoordinate ceq ;
		double epoch, ra, de ;
		Double Epoch ;

		Epoch = (Double) Registry.retrieve( Epoch.class.getName() ) ;
		if ( Epoch == null )
			epoch = astrolabe.Epoch.defoult() ;
		else
			epoch = Epoch.doubleValue() ;

		threshold = Configuration.getValue( this, CK_THRESHOLDSCALE, DEFAULT_THRESHOLDSCALE ) ;

		catalog = Arrays.asList( this.catalog
				.values()
				.toArray( new CatalogADC7118Record[0] ) ) ;
		Collections.sort( catalog, comparator ) ;

		for ( CatalogADC7118Record record : catalog ) {
			ceq = CAAPrecession.PrecessEquatorial( record.RA(), record.de(), 2451545./*J2000*/, epoch ) ;
			ra = CAACoordinateTransformation.HoursToDegrees( ceq.X() ) ;
			de = ceq.Y() ;
			ceq.delete() ;


			record.register() ;

			position = new astrolabe.model.Position() ;
			// astrolabe.model.AngleType
			position.setLon( new astrolabe.model.Lon() ) ;
			position.getLon().setRational( new astrolabe.model.Rational() ) ;
			position.getLon().getRational().setValue( ra ) ;  
			// astrolabe.model.AngleType
			position.setLat( new astrolabe.model.Lat() ) ;
			position.getLat().setRational( new astrolabe.model.Rational() ) ;
			position.getLat().getRational().setValue( de ) ;  

			d = 0 ;
			s = 0 ;
			if ( record.size.length()>0 ) {
				d = Double.valueOf( record.size )/60. ;
				p = projector.project( converter.convert( new Coordinate( ra, de ), false ), false ) ;
				vp = new Vector( p ) ;
				a = projector.project( converter.convert( new Coordinate( ra+d, de ), false ), false ) ;
				va = new Vector( a ) ;
				s = va.sub( vp ).abs() ;
			}

			try {
				ps.operator.gsave() ;

				if ( s>threshold ) {
					area = new BodyAreal( converter, projector ) ;
					area.setName( record.Name ) ;

					area.setAnnotation( record.getAnnotation() ) ;

					area.setBodyArealTypeChoice( new astrolabe.model.BodyArealTypeChoice() ) ;
					shapeElliptical = new astrolabe.model.ShapeElliptical() ;
					area.getBodyArealTypeChoice().setShapeElliptical( shapeElliptical ) ;

					shapeElliptical.setProportion( 1 ) ;
					shapeElliptical.setPA( 0 ) ;
					shapeElliptical.setRational( new astrolabe.model.Rational() ) ;
					shapeElliptical.getRational().setValue( d ) ;
					shapeElliptical.setPosition( position ) ;

					area.validate() ;

					ps.script( Configuration.getValue( this, record.Type, "" ) ) ;

					area.headPS( ps ) ;
					area.emitPS( ps ) ;
					area.tailPS( ps ) ;
				} else {
					star = new BodyStellar( converter, projector ) ;
					star.setName( record.Name ) ;
					star.initValues() ;

					star.setScript( record.getScript() ) ;
					star.setAnnotation( record.getAnnotation() ) ;

					star.setPosition( position ) ;

					star.validate() ;

					star.headPS( ps ) ;
					star.emitPS( ps ) ;
					star.tailPS( ps ) ;
				}

				ps.operator.grestore() ;
			} catch ( ValidationException e ) {
				throw new RuntimeException( e.toString() ) ;
			}

			record.degister() ;
		}
	}

	public void tailPS( ApplicationPostscriptStream ps ) {
	}

	public Reader reader() throws URISyntaxException, MalformedURLException {
		URI uri ;
		URL url ;
		File file ;
		InputStream in ;
		GZIPInputStream gz ;

		uri = new URI( getUrl() ) ;
		if ( uri.isAbsolute() ) {
			file = new File( uri ) ;	
		} else {
			file = new File( uri.getPath() ) ;
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
			log.warn( ParameterNotValidError.errmsg( '"'+record+'"', e.getMessage() ) ) ;
		}

		return r ;
	}
}
