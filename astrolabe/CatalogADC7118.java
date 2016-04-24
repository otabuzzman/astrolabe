
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
public class CatalogADC7118 extends CatalogType {

	private final static int C_CHUNK = 96+1/*0x0a*/ ;

	private final static Log log = LogFactory.getLog( CatalogADC7118.class ) ;

	private HashSet<String> restrict ;

	private final static Comparator<CatalogRecord> comparator = new Comparator<CatalogRecord>() {

		public int compare( CatalogRecord a, CatalogRecord b ) {
			return ( (CatalogADC7118Record) a ).mag()<( (CatalogADC7118Record) b ).mag()?-1:
				( (CatalogADC7118Record) a ).mag()>( (CatalogADC7118Record) b ).mag()?1:0 ;
		}
	} ;

	public CatalogADC7118( Object peer, Projector projector, double epoch ) throws ParameterNotValidException {
		super( peer, projector, epoch ) ;

		String[] rv ;

		restrict = new HashSet<String>() ;
		if ( ( (astrolabe.model.CatalogADC7118) peer ).getRestrict() != null ) {
			rv = ( (astrolabe.model.CatalogADC7118) peer ).getRestrict().split( "," ) ;
			for ( int v=0 ; v<rv.length ; v++ ) {
				restrict.add( rv[v] ) ;
			}
		}
	}

	public CatalogRecord record( java.io.Reader catalog ) {
		CatalogADC7118Record r = null ;
		char[] c ;
		String l ;

		c = new char[C_CHUNK] ;

		try {
			while ( catalog.read( c, 0, C_CHUNK ) == C_CHUNK ) {
				l = new String( c ) ;
				l = l.substring( 0, l.length()-1 ) ;

				try {
					r = new CatalogADC7118Record( l ) ;

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
