
package astrolabe;

@SuppressWarnings("serial")
public class Text extends astrolabe.model.Text {

	private final static double DEFAULT_PURPOSE = 3.8 ;

	public Text( Peer peer ) {
		peer.setupCompanion( this ) ;
	}

	public double size() {
		return Configuration.getValue(
				Configuration.getClassNode( this, getName(), ApplicationConstant.PN_TEXT_PURPOSE ),
				getPurpose(), DEFAULT_PURPOSE ) ;
	}
}
