
package astrolabe;

import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.xml.ValidationException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

@SuppressWarnings("serial")
public class CatalogADC7118 extends CatalogType implements Catalog {

	private final static int C_CHUNK = 96+1/*0x0a*/ ;

	private final static Log log = LogFactory.getLog( CatalogADC7118.class ) ;

	private HashSet<String> restrict ;

	private List<CatalogADC7118Record> catalogT ;

	private Projector projector ;
	private double epoch ;

	public CatalogADC7118( Peer peer, Projector projector ) {
		super( peer, projector ) ;

		String[] rv ;

		this.projector = projector ;

		this.epoch = ( (Double) AstrolabeRegistry.retrieve( ApplicationConstant.GC_EPOCHE ) ).doubleValue() ;

		restrict = new HashSet<String>() ;
		if ( ( (astrolabe.model.CatalogADC7118) peer ).getRestrict() != null ) {
			rv = ( (astrolabe.model.CatalogADC7118) peer ).getRestrict().split( "," ) ;
			for ( int v=0 ; v<rv.length ; v++ ) {
				restrict.add( rv[v] ) ;
			}
		}

		catalogT = new java.util.Vector<CatalogADC7118Record>() ;
	}

	public void addAllCatalogRecord() {
		Reader catalogR ;
		CatalogADC7118Record record ;
		List<double[]> bodyL ;
		Geometry bodyG, fov ;
		Comparator<CatalogADC7118Record> c = new Comparator<CatalogADC7118Record>() {
			public int compare( CatalogADC7118Record a, CatalogADC7118Record b ) {
				return a.mag()<b.mag()?-1:
					a.mag()>b.mag()?1:
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

			catalogT.add( record ) ;
		}

		try {
			catalogR.close() ;
		} catch (IOException e) {
			throw new RuntimeException( e.toString() ) ;
		}

		Collections.sort( catalogT, c ) ;
	}

	public void headPS( AstrolabePostscriptStream ps ) {
	}

	public void emitPS( AstrolabePostscriptStream ps ) {
		astrolabe.model.BodyStellar bodyModel ;
		BodyStellar bodyStellar ;
		astrolabe.model.Select[] select ;

		for ( CatalogADC7118Record record : catalogT ) {
			record.register() ;

			try {
				bodyModel = record.toModel( epoch ).getBodyStellar() ;
			} catch ( ValidationException e ) {
				throw new RuntimeException( e.toString() ) ;
			}

			select = getSelect( record.ident() ) ;
			if ( select != null )
				bodyModel.setAnnotation( select[select.length-1].getAnnotation() ) ;

			bodyStellar = new BodyStellar( bodyModel, projector ) ;

			ps.operator.gsave() ;

			bodyStellar.headPS( ps ) ;
			bodyStellar.emitPS( ps ) ;
			bodyStellar.tailPS( ps ) ;

			ps.operator.grestore() ;
		}
	}

	public void tailPS( AstrolabePostscriptStream ps ) {
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
		boolean ok = true ;

		try {
			r = new CatalogADC7118Record( record ) ;

			if ( restrict.size()>0 )
				for ( String key : r.ident() )
					if ( ok = restrict.contains( key ) )
						break ;
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

		return ok?r:null ;
	}
}
