
package astrolabe;

import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.xml.ValidationException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

@SuppressWarnings("serial")
public class CatalogADC5050 extends CatalogType implements Catalog {

	private final static int C_CHUNK = 197+1/*0x0a*/ ;

	private final static Log log = LogFactory.getLog( CatalogADC5050.class ) ;

	private astrolabe.model.Script script ;

	private Hashtable<String, CatalogRecord> catalogT ;
	private List<String> catalogL ;

	private Projector projector ;

	public CatalogADC5050( Peer peer, Projector projector ) {
		super( peer, projector ) ;

		this.projector = projector ;

		script = ( (astrolabe.model.CatalogADC5050) peer ).getScript() ;

		catalogT = new Hashtable<String, CatalogRecord>() ;
		catalogL = new java.util.Vector<String>() ;
	}

	public void addAllCatalogRecord() {
		Reader catalogR ;
		CatalogADC5050Record record ;
		List<double[]> bodyL ;
		Geometry bodyG, fov ;
		Comparator<String> c = new Comparator<String>() {
			public int compare( String a, String b ) {
				CatalogADC5050Record x, y ;
				double xmag, ymag ;

				x = (CatalogADC5050Record) catalogT.get( a ) ;
				y = (CatalogADC5050Record) catalogT.get( b ) ;

				xmag = Double.valueOf( x.Vmag ).doubleValue() ;
				ymag = Double.valueOf( y.Vmag ).doubleValue() ;

				return xmag<ymag?-1:
					xmag>ymag?1:
						0 ;
			}
		} ;

		fov = (Geometry) Registry.retrieve( ApplicationConstant.GC_FOVEFF ) ;

		try {
			catalogR = reader() ;
		} catch ( URISyntaxException e ) {
			throw new RuntimeException( e.toString() ) ;
		} catch ( MalformedURLException e ) {
			throw new RuntimeException( e.toString() ) ;
		}

		while ( ( record = record( catalogR ) ) != null ) {
			if ( record.Vmag.length() == 0 ) {
				String msg ;

				msg = MessageCatalog.message( ApplicationConstant.GC_APPLICATION, ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
				msg = MessageFormat.format( msg, new Object[] { record.HR+".Vmag 0", "" } ) ;
				log.warn( msg ) ;

				continue ;
			}

			bodyL = record.list( projector ) ;

			if ( bodyL.size() == 1 ) {
				if ( ! fov.covers( new GeometryFactory().createPoint(
						new JTSCoordinate( bodyL.get( 0 ) ) ) ) )
					continue ;
			} else {
				bodyL.add( bodyL.get( 0 ) ) ;
				bodyG = new GeometryFactory().createPolygon(
						new GeometryFactory().createLinearRing(
								new JTSCoordinateArraySequence( bodyL ) ), null ) ;

				if ( ! ( fov.covers( bodyG ) || fov.overlaps( bodyG ) ) )
					continue ;
			}

			catalogT.put( record.HR, record ) ;
			catalogL.add( record.HR ) ;
		}

		try {
			catalogR.close() ;
		} catch (IOException e) {
			throw new RuntimeException( e.toString() ) ;
		}

		Collections.sort( catalogL, c ) ;
	}

	public CatalogRecord getCatalogRecord( String ident ) {
		return catalogT.get( ident ) ;
	}

	public CatalogRecord[] getCatalogRecord() {
		return catalogT.values().toArray( new CatalogRecord[0] ) ;
	}

	public void headPS( AstrolabePostscriptStream ps ) {
	}

	public void emitPS( AstrolabePostscriptStream ps ) {
		ParserAttribute parser ;
		astrolabe.model.Body body ;
		BodyStellar bodyStellar ;

		parser = (ParserAttribute) Registry.retrieve( ApplicationConstant.GC_PARSER ) ;

		for ( CatalogRecord record : catalogT.values() ) {
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

			body.getBodyStellar().setScript( script ) ;
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

	public CatalogADC5050Record record( java.io.Reader catalog ) {
		CatalogADC5050Record r = null ;
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

	private CatalogADC5050Record record( String record ) {
		CatalogADC5050Record r = null ;

		try {
			r = new CatalogADC5050Record( record ) ;
		} catch ( ParameterNotValidException e ) {
			String msg ;

			msg = MessageCatalog.message( ApplicationConstant.GC_APPLICATION, ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
			msg = MessageFormat.format( msg, new Object[] { e.getMessage(), "\""+record+"\"" } ) ;
			log.warn( msg ) ;
		} catch ( NumberFormatException e ) {
			String msg ;

			msg = MessageCatalog.message( ApplicationConstant.GC_APPLICATION, ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
			msg = MessageFormat.format( msg, new Object[] { "("+e.getMessage()+")", "\""+record+"\"" } ) ;
			log.warn( msg ) ;
		}

		return r ;
	}
}
