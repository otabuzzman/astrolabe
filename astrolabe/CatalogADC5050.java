
package astrolabe;

import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
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

	private HashSet<String> restrict ;

	private Hashtable<String, CatalogADC5050Record> catalogT ;
	private List<String> catalogL ;

	private astrolabe.model.CatalogADC5050 peer ;
	private Projector projector ;
	private double epoch ;

	public CatalogADC5050( Peer peer, Projector projector ) {
		super( peer, projector ) ;

		String[] rv ;

		this.peer = (astrolabe.model.CatalogADC5050) peer ;
		this.projector = projector ;

		this.epoch = ( (Double) AstrolabeRegistry.retrieve( ApplicationConstant.GC_EPOCHE ) ).doubleValue() ;

		restrict = new HashSet<String>() ;
		if ( ( (astrolabe.model.CatalogADC5050) peer ).getRestrict() != null ) {
			rv = ( (astrolabe.model.CatalogADC5050) peer ).getRestrict().split( "," ) ;
			for ( int v=0 ; v<rv.length ; v++ ) {
				restrict.add( rv[v] ) ;
			}
		}

		catalogT = new Hashtable<String, CatalogADC5050Record>() ;
		catalogL = new java.util.Vector<String>() ;
	}

	public void addAllCatalogRecord() {
		Reader catalogR ;
		CatalogADC5050Record record ;
		List<double[]> bodyL ;
		Geometry bodyG, fov ;
		String ident ;
		Comparator<String> c = new Comparator<String>() {
			public int compare( String a, String b ) {
				CatalogADC5050Record x, y ;

				x = catalogT.get( a ) ;
				y = catalogT.get( b ) ;

				return x.Vmag()<y.Vmag()?-1:
					x.Vmag()>y.Vmag()?1:
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

			ident = record.ident().get( 0 ) ;
			catalogT.put( ident, record ) ;
			catalogL.add( ident ) ;
		}

		try {
			catalogR.close() ;
		} catch (IOException e) {
			throw new RuntimeException( e.toString() ) ;
		}

		Collections.sort( catalogL, c ) ;
	}

	public void headPS( AstrolabePostscriptStream ps ) {
	}

	public void emitPS( AstrolabePostscriptStream ps ) {
		List<BodyAreal> sign ;
		astrolabe.model.BodyAreal bodySign ;
		BodyAreal bodyAreal ;
		HashSet<String> headline ;
		String[] sv, hv, bv ;
		CatalogADC5050Record recordS ;

		astrolabe.model.BodyStellar bodyModel ;
		BodyStellar bodyStellar ;
		astrolabe.model.Select[] select ;

		for ( int s=0 ; s<peer.getSignCount() ; s++ ) {
			try {
				sign = new java.util.Vector<BodyAreal>() ;

				headline = new HashSet<String>() ;
				if ( peer.getSign( s ).getHeadline() != null ) {
					hv = peer.getSign( s ).getHeadline().split( "," ) ;
					for ( int h=0 ; h<hv.length ; h++ ) {
						headline.add( hv[h] ) ;
					}
				}

				sv = peer.getSign( s ).getValue().split( "," ) ;
				for ( int b=0 ; b<sv.length ; b++ ) {
					bodySign = new astrolabe.model.BodyAreal() ;
					bodySign.setImportance( ApplicationConstant.AV_BODY_GRAPHICAL ) ;

					bv = sv[b].split( ":" ) ;
					for ( int p=0 ; p<bv.length ; p++ ) {
						if ( ( recordS = catalogT.get( bv[p] ) ) == null ) {
							String msg ;

							msg = MessageCatalog.message( ApplicationConstant.GC_APPLICATION, ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
							msg = MessageFormat.format( msg, new Object[] { "\""+bv[p]+"\"", "\""+sv[b]+"\"" } ) ;

							throw new ParameterNotValidException( msg ) ;
						}
						try {
							bodySign.addPosition( recordS.toModel( epoch ).getBodyStellar().getPosition() ) ;
						} catch ( ValidationException e ) {
							throw new RuntimeException( e.toString() ) ;
						}
					}

					try {
						bodySign.validate() ;
					} catch ( ValidationException e ) {
						throw new RuntimeException( e.toString() ) ;
					}

					bodyAreal = new BodyAreal( bodySign, projector ) ;

					if ( peer.getSign( s ).getAnnotationCount()>0 ) {
						if ( headline.size()==0 ) {
							if ( b==0 ) {
								bodyAreal.setAnnotation( peer.getSign( s ).getAnnotation() ) ;
							}
						} else {
							if ( headline.contains( new Integer( b+1 ).toString() ) ) {
								bodyAreal.setAnnotation( peer.getSign( s ).getAnnotation() ) ;
							}
						}
					}

					sign.add( bodyAreal ) ;
				}
				for ( int b=0 ; b<sign.size() ; b++ ) {
					bodyAreal = sign.get( b ) ;

					ps.operator.gsave() ;

					bodyAreal.headPS( ps ) ;
					bodyAreal.emitPS( ps ) ;
					bodyAreal.tailPS( ps ) ;

					ps.operator.grestore() ;
				}
			} catch ( ParameterNotValidException e ) {
				log.warn( e.toString() ) ;
			}
		}

		for ( CatalogADC5050Record recordB : catalogT.values() ) {
			recordB.register() ;

			try {
				bodyModel = recordB.toModel( epoch ).getBodyStellar() ;
			} catch ( ValidationException e ) {
				throw new RuntimeException( e.toString() ) ;
			}

			select = getSelect( recordB.ident() ) ;
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
		boolean ok = true ;

		try {
			r = new CatalogADC5050Record( record ) ;

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
