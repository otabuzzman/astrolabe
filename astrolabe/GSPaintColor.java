package astrolabe;

public class GSPaintColor implements PostscriptEmitter {

	private final static double DEFAULT_PAINTCOLOR = 0 ;

	private double[] colorRGB = null ;
	private double colorGray = -1 ;

	public GSPaintColor( String paintcolor ) {
		String pv, pd[] ;

		pv = Configuration.getValue(
				Configuration.getClassNode( this, null, null ),
				paintcolor, paintcolor ) ;
		pd = pv.split( ":" ) ;

		try {
			if ( pd.length>1 ) {
				colorRGB = new double[] {new Double( pd[0] ).doubleValue(),
						new Double( pd[1] ).doubleValue(),
						new Double( pd[2] ).doubleValue() } ;
			} else {
				colorGray = new Double( pd[0] ).doubleValue() ;
			}
		} catch ( NumberFormatException e ) {
			colorGray = DEFAULT_PAINTCOLOR ;
		}
	}

	public void headPS( AstrolabePostscriptStream ps ) {
	}

	public void emitPS( AstrolabePostscriptStream ps ) {
		if ( colorRGB == null )
			ps.operator.setgray( colorGray ) ;
		else
			ps.operator.setrgbcolor( colorRGB[0], colorRGB[1], colorRGB[2] ) ;
	}

	public void tailPS( AstrolabePostscriptStream ps ) {
	}
}
