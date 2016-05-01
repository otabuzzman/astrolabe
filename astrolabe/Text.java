
package astrolabe;

import org.exolab.castor.xml.ValidationException;

@SuppressWarnings("serial")
public class Text extends astrolabe.model.Text {

	private final static double DEFAULT_PURPOSE = 3.8 ;

	public Text( Object peer ) throws ParameterNotValidException {
		ApplicationHelper.setupCompanionFromPeer( this, peer ) ;
		try {
			validate() ;
		} catch ( ValidationException e ) {
			throw new ParameterNotValidException( e.toString() ) ;
		}
	}

	public double size() {
		double size ;

		size = ApplicationHelper.getPreferencesKV(
				ApplicationHelper.getClassNode( this, getPurpose(), ApplicationConstant.PN_TEXT_PURPOSE ),
				getPurpose(), DEFAULT_PURPOSE ) ;

		return size ;
	}
}
