
package astrolabe;

import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SuppressWarnings("serial")
public class Script extends astrolabe.model.Script {

	private final static Log log = LogFactory.getLog( Script.class ) ;

	public Script( Peer peer ) {
		peer.setupCompanion( this ) ;
	}

	public double size() {
		double r, size ;

		size = Configuration.getValue(
				Configuration.getClassNode( this, getName(), ApplicationConstant.PN_TEXT_PURPOSE ),
				getPurpose(), 0 ) ;
		if ( size==0 ) {
			try {
				size = Double.valueOf( getPurpose() ) ;
			} catch ( NumberFormatException e ) {
				String msg ;

				msg = MessageCatalog.message( ApplicationConstant.GC_APPLICATION, ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
				msg = MessageFormat.format( msg, new Object[] { getPurpose(), "\""+getValue()+"\"" } ) ;
				log.warn( msg ) ;
			}
		}

		r = size ;

		return r ;
	}
}
