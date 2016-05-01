package astrolabe;

public class ElementPracticality implements PostscriptEmitter {

	private final static String DEFAULT_PRACTICALITY = "0" ;

	private double[] colorRGB = null ;
	private double colorGray = -1 ;

	public ElementPracticality( String practicality ) {
		String pv, pd[] ;

		pv = Configuration.getValue(
				Configuration.getClassNode( this, null, null ),
				practicality, DEFAULT_PRACTICALITY ) ;
		pd = pv.split( ":" ) ;

		if ( pd.length>1 ) {
			colorRGB = new double[] {new Double( pd[0] ).doubleValue(),
					new Double( pd[1] ).doubleValue(),
					new Double( pd[2] ).doubleValue() } ;
		} else { // setgray
			colorGray = new Double( pd[0] ).doubleValue() ;
		}
	}

	public void headPS( AstrolabePostscriptStream ps ) {
	}

	public void emitPS( AstrolabePostscriptStream ps ) {
		if ( colorRGB == null )
			ps.operator.setgray( colorGray ) ;
		else
			ps.operator.setrgbcolor( colorRGB[0], colorRGB[0], colorRGB[0] ) ;
	}

	public void tailPS( AstrolabePostscriptStream ps ) {
	}
}
