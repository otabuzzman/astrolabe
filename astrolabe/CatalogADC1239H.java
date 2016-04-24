
package astrolabe;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SuppressWarnings("serial")
public class CatalogADC1239H extends CatalogType {

	private static final int C_CHUNK = 450+1/*0x0a*/ ;

	private final static Log log = LogFactory.getLog( CatalogADC1239H.class ) ;

	private HashSet<String> restrict ;

	public CatalogADC1239H( Object peer, Projector projector ) throws ParameterNotValidException {
		super( peer, projector ) ;

		String[] rv ;

		restrict = new HashSet<String>() ;
		if ( ( (astrolabe.model.CatalogADC1239H) peer ).getRestrict() != null ) {
			rv = ( (astrolabe.model.CatalogADC1239H) peer ).getRestrict().split( "," ) ;
			for ( int v=0 ; v<rv.length ; v++ ) {
				restrict.add( rv[v] ) ;
			}
		}
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
}
