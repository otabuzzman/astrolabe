
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
public class CatalogADC1239H extends CatalogType implements Catalog {

	private final static int C_CHUNK = 450+1/*0x0a*/ ;

	private final static Log log = LogFactory.getLog( CatalogADC1239H.class ) ;

	private astrolabe.model.Script script ;

	private Hashtable<String, CatalogRecord> catalogT ;
	private List<String> catalogL ;

	private Projector projector ;

	public CatalogADC1239H( Peer peer, Projector projector ) {
		super( peer, projector ) ;

		this.projector = projector ;

		script = ( (astrolabe.model.CatalogADC1239H) peer ).getScript() ;

		catalogT = new Hashtable<String, CatalogRecord>() ;
		catalogL = new java.util.Vector<String>() ;
	}

	public void addAllCatalogRecord() {
		Reader catalogR ;
		CatalogADC1239HRecord record ;
		List<double[]> bodyL ;
		Geometry bodyG, fov ;
		Comparator<String> c = new Comparator<String>() {
			public int compare( String a, String b ) {
				CatalogADC1239HRecord x, y ;
				double xmag, ymag ;

				x = (CatalogADC1239HRecord) catalogT.get( a ) ;
				y = (CatalogADC1239HRecord) catalogT.get( b ) ;

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
				msg = MessageFormat.format( msg, new Object[] { record.HIP+".Vmag 0", "" } ) ;
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

			catalogT.put( record.HIP, record ) ;
			catalogL.add( record.HIP ) ;
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
