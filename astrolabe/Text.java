
package astrolabe;

import org.exolab.castor.xml.ValidationException;

@SuppressWarnings("serial")
public class Text extends astrolabe.model.Text {

	private final static double DEFAULT_PURPOSE = 3.8 ;

	public Text( Peer peer ) throws ParameterNotValidException {
		peer.setupCompanion( this ) ;
		try {
			validate() ;
		} catch ( ValidationException e ) {
			throw new ParameterNotValidException( e.toString() ) ;
		}
	}

	public double size() {
		return ApplicationHelper.getPreferencesKV(
				ApplicationHelper.getClassNode( this, getName(), ApplicationConstant.PN_TEXT_PURPOSE ),
				getPurpose(), DEFAULT_PURPOSE ) ;
	}
}
