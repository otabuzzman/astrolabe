
package astrolabe;

import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.xml.ValidationException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

@SuppressWarnings("serial")
public class CatalogADC6049 extends CatalogType implements Catalog {

	private final static int C_CHUNK18 = 25+1/*0x0a*/ ;
	private final static int C_CHUNK20 = 29+1/*0x0a*/ ;

	private final static Log log = LogFactory.getLog( CatalogADC6049.class ) ;

	private HashSet<String> restrict ;

	private List<CatalogADC6049Record> catalogT ;

	private Projector projector ;
	private double epoch ;

	private String _m = new String() ;
	private int _ch = 0 ;
	private int _ca = 0 ;
	private int _co = 0 ;

	public CatalogADC6049( Peer peer, Projector projector ) {
		super( peer, projector ) ;

		String[] rv ;

		this.projector = projector ;

		this.epoch = ( (Double) AstrolabeRegistry.retrieve( ApplicationConstant.GC_EPOCHE ) ).doubleValue() ;

		restrict = new HashSet<String>() ;
		if ( ( (astrolabe.model.CatalogADC6049) peer ).getRestrict() != null ) {
			rv = ( (astrolabe.model.CatalogADC6049) peer ).getRestrict().split( "," ) ;
			for ( int v=0 ; v<rv.length ; v++ ) {
				restrict.add( rv[v] ) ;
			}
		}

		catalogT = new java.util.Vector<CatalogADC6049Record>() ;
	}

	public void addAllCatalogRecord() {
		Reader catalogR ;
		CatalogADC6049Record record ;
		List<double[]> bodyL ;
		Geometry bodyG, fov ;

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
	}

	public void headPS( AstrolabePostscriptStream ps ) {
	}

	public void emitPS( AstrolabePostscriptStream ps ) {
		astrolabe.model.BodyAreal bodyModel ;
		BodyAreal bodyAreal ;
		astrolabe.model.Select[] select ;

		for ( CatalogADC6049Record record : catalogT ) {
			record.register() ;

			try {
				bodyModel = record.toModel( epoch ).getBodyAreal() ;
			} catch ( ValidationException e ) {
				throw new RuntimeException( e.toString() ) ;
			}

			bodyModel.setAnnotation( getAnnotation() ) ;

			select = getSelect( record.ident() ) ;
			if ( select != null )
				bodyModel.setAnnotation( select[select.length-1].getAnnotation() ) ;

			bodyAreal = new BodyAreal( bodyModel, projector ) ;

			ps.operator.gsave() ;

			bodyAreal.headPS( ps ) ;
			bodyAreal.emitPS( ps ) ;
			bodyAreal.tailPS( ps ) ;

			ps.operator.grestore() ;
		}
	}

	public void tailPS( AstrolabePostscriptStream ps ) {
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

			if ( restrict.size()>0 ) {
				for ( String key : r.ident() )
					if ( restrict.contains( key ) )
						break ;
			}
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
