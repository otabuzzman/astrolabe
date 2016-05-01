
package astrolabe;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.xml.ValidationException;

@SuppressWarnings("serial")
public class CatalogADC5050 extends CatalogType implements PostscriptEmitter {

	private final static int C_CHUNK = 197+1/*0x0a*/ ;

	private final static Log log = LogFactory.getLog( CatalogADC5050.class ) ;

	private HashSet<String> restrict ;

	private astrolabe.model.CatalogADC5050 peer ;
	private Projector projector ;
	private double epoch ;

	private final static Comparator<CatalogRecord> comparator = new Comparator<CatalogRecord>() {

		public int compare( CatalogRecord a, CatalogRecord b ) {
			return ( (CatalogADC5050Record) a ).Vmag()<( (CatalogADC5050Record) b ).Vmag()?-1:
				( (CatalogADC5050Record) a ).Vmag()>( (CatalogADC5050Record) b ).Vmag()?1:0 ;
		}
	} ;

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
	}

	public void headPS( AstrolabePostscriptStream ps ) {
	}

	public void emitPS( AstrolabePostscriptStream ps ) {
		List<BodyAreal> sign ;
		astrolabe.model.BodyAreal bodySign ;
		HashSet<String> headline ;
		String[] sv, hv, bv ;
		Hashtable<String, CatalogRecord> catalog ;
		CatalogRecord cr ;
		BodyAreal bodyAreal ;
		astrolabe.model.Annotation[] annotation ;
		astrolabe.model.BodyStellar bodyModel ;
		BodyStellar bodyStellar ;

		try {
			catalog = read() ;
		} catch ( URISyntaxException e ) {
			throw new RuntimeException( e.toString() ) ;
		} catch ( MalformedURLException e ) {
			throw new RuntimeException( e.toString() ) ;
		}

		for ( int s=0 ; s<peer.getSignCount() ; s++ ) {
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
					if ( ( cr = catalog.get( bv[p] ) ) == null ) {
						break ; // element missing in catalog
					}
					try {
						bodySign.addPosition( cr.toModel( epoch ).getBodyStellar().getPosition() ) ;
					} catch ( ValidationException e ) {
						throw new RuntimeException( e.toString() ) ;
					}
				}

				if ( bodySign.getPositionCount()<bv.length ) { // element(s) missing in catalog
					break ;
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
		}

		for ( CatalogRecord record : arrange( catalog ) ) {
			annotation = annotation( record ) ;

			try {
				bodyModel = record.toModel( epoch ).getBodyStellar() ;
				if ( annotation != null ) {
					bodyModel.setAnnotation( annotation( record ) ) ;

					record.register() ;
				}

				bodyStellar = new BodyStellar( bodyModel, projector ) ;
			} catch ( ValidationException e ) {
				throw new RuntimeException( e.toString() ) ;
			}

			ps.operator.gsave() ;

			bodyStellar.headPS( ps ) ;
			bodyStellar.emitPS( ps ) ;
			bodyStellar.tailPS( ps ) ;

			ps.operator.grestore() ;
		}
	}

	public void tailPS( AstrolabePostscriptStream ps ) {
	}

	public CatalogRecord record( java.io.Reader catalog ) {
		CatalogADC5050Record r = null ;
		String l ;
		char[] c ;
		int o ;

		c = new char[C_CHUNK] ;
		o = 0 ;

		try {
			while ( catalog.read( c, o++, 1 ) == 1 ) {
				if ( c[o-1] == '\n' ) {
					if ( o<C_CHUNK ) {
						for ( o-- ; o<C_CHUNK ; o++ ) {
							c[o] = ' ' ;
						}
						c[o-1] = '\n' ;
					}
					l = new String( c ) ;
					o = 0 ;
				} else {
					continue ;
				}

				try {
					r = new CatalogADC5050Record( l ) ;

					if ( r.matchAny( restrict ) ) {
						break ;
					} else {
						continue ;
					}
				} catch ( ParameterNotValidException e ) {
					String msg ;

					msg = MessageCatalog.message( ApplicationConstant.GC_APPLICATION, ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
					msg = MessageFormat.format( msg, new Object[] { e.getMessage(), "\""+l+"\"" } ) ;
					log.warn( msg ) ;

					continue ;
				} catch ( NumberFormatException e ) {
					String msg ;

					msg = MessageCatalog.message( ApplicationConstant.GC_APPLICATION, ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
					msg = MessageFormat.format( msg, new Object[] { "("+e.getMessage()+")", "\""+l+"\"" } ) ;
					log.warn( msg ) ;

					continue ;
				}
			}
		} catch ( IOException e ) {
			throw new RuntimeException( e.toString() ) ;
		}

		return r ;
	}

	public CatalogRecord[] arrange( Hashtable<String, CatalogRecord> catalog ) {
		CatalogRecord[] r ;
		List<CatalogRecord> l ;

		l = new ArrayList<CatalogRecord>( catalog.values() ) ;
		Collections.sort( l, comparator ) ;

		r = new CatalogRecord[l.size()] ;

		return l.toArray( r ) ;
	}
}
