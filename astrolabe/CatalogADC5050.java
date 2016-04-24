
package astrolabe;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SuppressWarnings("serial")
public class CatalogADC5050 extends CatalogType {

	private static final int C_CHUNK = 197+1/*0x0a*/ ;

	private final static Log log = LogFactory.getLog( CatalogADC5050.class ) ;

	private HashSet<String> restrict ;

	public CatalogADC5050( Object peer, Projector projector ) throws ParameterNotValidException {
		super( peer, projector ) ;

		String[] rv ;

		restrict = new HashSet<String>() ;
		if ( ( (astrolabe.model.CatalogADC5050) peer ).getRestrict() != null ) {
			rv = ( (astrolabe.model.CatalogADC5050) peer ).getRestrict().split( "," ) ;
			for ( int v=0 ; v<rv.length ; v++ ) {
				restrict.add( rv[v] ) ;
			}
		}
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
