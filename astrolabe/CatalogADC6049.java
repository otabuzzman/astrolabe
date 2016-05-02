
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
public class CatalogADC6049 extends astrolabe.model.CatalogADC6049 implements PostscriptEmitter {

	private final static int C_CHUNK18 = 25+1/*0x0a*/ ;
	private final static int C_CHUNK20 = 29+1/*0x0a*/ ;

	private final static Log log = LogFactory.getLog( CatalogADC6049.class ) ;

	private Hashtable<String, CatalogADC6049Record> catalog ;

	private Projector projector ;

	private String _m = new String() ;
	private int _ch = 0 ;
	private int _ca = 0 ;
	private int _co = 0 ;

	public CatalogADC6049( Projector projector ) {
		this.projector = projector ;
	}

	@SuppressWarnings("unchecked")
	private Hashtable<String, CatalogADC6049Record> unsafecast( Object hashtable ) {
		return (Hashtable<String, CatalogADC6049Record>) hashtable ;
	}

	public void addAllCatalogRecord() {
		Reader reader ;
		CatalogADC6049Record record ;
		String key ;

		key = getClass().getSimpleName()+":"+getName() ;
		catalog = unsafecast( Registry.retrieve( key ) ) ;
		if ( catalog == null ) {
			catalog = new Hashtable<String, CatalogADC6049Record>() ;
			Registry.register( key, catalog ) ;
		} else
			return ;

		try {
			reader = reader() ;

			while ( ( record = record( reader ) ) != null ) {
				try {
					record.inspect() ;
				} catch ( ParameterNotValidException e ) {
					log.warn( ParameterNotValidError.errmsg( record.con, e.getMessage() ) ) ;

					continue ;
				}

				record.register() ;

				for ( astrolabe.model.CatalogADC6049Record select : getCatalogADC6049Record() ) {
					select.copyValues( record ) ;
					if ( Boolean.parseBoolean( record.getSelect() ) ) {
						catalog.put( record.con, record ) ;

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
		astrolabe.model.Body body ;
		BodyAreal bodyAreal ;
		astrolabe.model.Position pm ;
		CAA2DCoordinate ceq ;
		List<double[]> list ;
		double epoch, eq[] ;
		Double Epoch ;

		Epoch = (Double) Registry.retrieve( astrolabe.Epoch.RK_EPOCH ) ;
		if ( Epoch == null )
			epoch = astrolabe.Epoch.defoult() ;
		else
			epoch = Epoch.doubleValue() ;

		for ( CatalogADC6049Record record : catalog.values() ) {
			record.register() ;

			body = new astrolabe.model.Body() ;
			body.setBodyAreal( new astrolabe.model.BodyAreal() ) ;
			body.getBodyAreal().setName( record.con ) ;
			body.getBodyAreal().initValues() ;

			body.getBodyAreal().setNature( record.getNature() ) ;

			body.getBodyAreal().setAnnotation( record.getAnnotation() ) ;

			body.getBodyAreal().setBodyArealTypeChoice( new astrolabe.model.BodyArealTypeChoice() ) ;

			list = record.list() ;
			for ( int p=0 ; p<list.size() ; p++ ) {
				eq = list.get( p ) ;
				ceq = CAAPrecession.PrecessEquatorial( eq[0], eq[1], 2451545./*J2000*/, epoch ) ;
				pm = new astrolabe.model.Position() ;
				// astrolabe.model.SphericalType
				pm.setDistance( new astrolabe.model.Distance() ) ;
				pm.getDistance().setValue( 1 ) ;
				// astrolabe.model.AngleType
				pm.setDeviation( new astrolabe.model.Deviation() ) ;
				pm.getDeviation().setRational( new astrolabe.model.Rational() ) ;
				pm.getDeviation().getRational().setValue( CAACoordinateTransformation.HoursToDegrees( ceq.X() ) ) ;  
				// astrolabe.model.AngleType
				pm.setElevation( new astrolabe.model.Elevation() ) ;
				pm.getElevation().setRational( new astrolabe.model.Rational() ) ;
				pm.getElevation().getRational().setValue( ceq.Y() ) ;  

				body.getBodyAreal().getBodyArealTypeChoice().addPosition( pm ) ;
				ceq.delete() ;
			}

			try {
				body.validate() ;
			} catch ( ValidationException e ) {
				throw new RuntimeException( e.toString() ) ;
			}

			bodyAreal = new BodyAreal( projector ) ;
			body.getBodyAreal().copyValues( bodyAreal ) ;

			bodyAreal.register() ;
			ps.operator.gsave() ;

			bodyAreal.headPS( ps ) ;
			bodyAreal.emitPS( ps ) ;
			bodyAreal.tailPS( ps ) ;

			ps.operator.grestore() ;
			bodyAreal.degister() ;

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
			log.warn( ParameterNotValidError.errmsg( '"'+record+'"', e.getMessage() ) ) ;
		}

		return r ;
	}
}
