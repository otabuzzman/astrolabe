
package astrolabe;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SuppressWarnings("serial")
public class CatalogADC1239H extends CatalogType {

	private final static int C_CHUNK = 450+1/*0x0a*/ ;

	private final static Log log = LogFactory.getLog( CatalogADC1239H.class ) ;

	private HashSet<String> restrict ;

	private astrolabe.model.CatalogADC1239H peer ;
	private Projector projector ;
	private double epoch ;

	private final static Comparator<CatalogRecord> comparator = new Comparator<CatalogRecord>() {

		public int compare( CatalogRecord a, CatalogRecord b ) {
			return ( (CatalogADC1239HRecord) a ).Vmag()<( (CatalogADC1239HRecord) b ).Vmag()?-1:
				( (CatalogADC1239HRecord) a ).Vmag()>( (CatalogADC1239HRecord) b ).Vmag()?1:0 ;
		}
	} ;

	public CatalogADC1239H( Object peer, Projector projector, double epoch ) throws ParameterNotValidException {
		super( peer, projector, epoch ) ;

		String[] rv ;

		this.peer = (astrolabe.model.CatalogADC1239H) peer ;
		this.projector = projector ;
		this.epoch = epoch ;

		restrict = new HashSet<String>() ;
		if ( ( (astrolabe.model.CatalogADC1239H) peer ).getRestrict() != null ) {
			rv = ( (astrolabe.model.CatalogADC1239H) peer ).getRestrict().split( "," ) ;
			for ( int v=0 ; v<rv.length ; v++ ) {
				restrict.add( rv[v] ) ;
			}
		}
	}

	public void emitPS( PostscriptStream ps ) {
		java.util.Vector<BodyAreal> sign ;
		astrolabe.model.BodyAreal model ;
		HashSet<String> headline ;
		String[] sv, hv, bv ;
		boolean ok = true ;
		CatalogRecord cr ;
		BodyAreal body ;

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
			notok:
				if ( ok ) {
					for ( int b=0 ; b<sv.length ; b++ ) {
						model = new astrolabe.model.BodyAreal() ;
						model.setType( ApplicationConstant.AV_BODY_SIGN ) ;

						bv = sv[b].split( ":" ) ;
						for ( int p=0 ; p<bv.length ; p++ ) {
							if ( ( cr = entry( bv[p] ) ) == null ) {
								ok = false ;
								break notok ;
							}
							try {
								model.addPosition( cr.toBody( epoch ).getBodyStellar().getPosition() ) ;
							} catch ( ParameterNotValidException e ) {
								throw new RuntimeException( e.toString() ) ;
							}
						}
						try {
							body = new BodyAreal( model, projector ) ;
						} catch ( ParameterNotValidException e ) {
							throw new RuntimeException( e.toString() ) ;
						}

						if ( peer.getSign( s ).getAnnotationCount()>0 ) {
							if ( headline.size()==0 ) {
								if ( b==0 ) {
									body.setAnnotation( peer.getSign( s ).getAnnotation() ) ;
								}
							} else {
								if ( headline.contains( new Integer( b+1 ).toString() ) ) {
									body.setAnnotation( peer.getSign( s ).getAnnotation() ) ;
								}
							}
						}

						sign.add( body ) ;
					}
					for ( int b=0 ; b<sign.size() ; b++ ) {
						body = sign.get( b ) ;

						ps.operator.gsave() ;

						body.headPS( ps ) ;
						body.emitPS( ps ) ;
						body.tailPS( ps ) ;

						ps.operator.grestore() ;
					}
				} else {
					ok = true ;
				}
		}

		super.emitPS( ps ) ;
	}

	public CatalogRecord record( java.io.Reader catalog ) {
		CatalogADC1239HRecord r = null ;
		char[] c ;
		String l ;

		c = new char[C_CHUNK] ;

		try {
			while ( catalog.read( c, 0, C_CHUNK ) == C_CHUNK ) {
				l = new String( c ) ;
				l = l.substring( 0, l.length()-1 ) ;

				try {
					r = new CatalogADC1239HRecord( l ) ;

					if ( r.matchAny( restrict ) ) {
						break ;
					} else {
						continue ;
					}
				} catch ( ParameterNotValidException e ) {
					String msg ;

					msg = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
					msg = MessageFormat.format( msg, new Object[] { "\""+l+"\"", "" } ) ;
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
