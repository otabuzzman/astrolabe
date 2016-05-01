
package astrolabe;

import org.exolab.castor.xml.ValidationException;

@SuppressWarnings("serial")
public class Text extends astrolabe.model.Text {

	private final static double DEFAULT_PURPOSE = 3.8 ;

	private ParserAttribute parser ;

	public Text( Object peer ) throws ParameterNotValidException {
		ApplicationHelper.setupCompanionFromPeer( this, peer ) ;
		try {
			validate() ;
		} catch ( ValidationException e ) {
			throw new ParameterNotValidException( e.toString() ) ;
		}

		parser = new ParserAttribute() ;
	}

	public double purpose() {
		String purpose ;
		double value ;

		purpose = parser.stringValue( getPurpose() ) ;

		value = ApplicationHelper.getPreferencesKV(
				ApplicationHelper.getClassNode( this, getName(), ApplicationConstant.PN_TEXT_PURPOSE ),
				purpose, DEFAULT_PURPOSE ) ;

		return value ;
	}

	public String value() {
		return parser.stringValue( getValue() ) ;
	}
}
