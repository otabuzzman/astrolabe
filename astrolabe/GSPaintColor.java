package astrolabe;

public class GSPaintColor implements PostscriptEmitter {

	private final static double DEFAULT_PAINTCOLOR = 0 ;

	private double[] colorRGB	= null ;
	private double colorGray	= DEFAULT_PAINTCOLOR ;

	public GSPaintColor( String param ) {
		String pv, pd[] ;

		pv = Configuration.getValue( this, param, null ) ;
		if ( pv == null )
			pd = param.split( ":" ) ;
		else
			pd = pv.split( ":" ) ;

		try {
			if ( pd.length>1 ) {
				colorRGB = new double[] {new Double( pd[0] ).doubleValue(),
						new Double( pd[1] ).doubleValue(),
						new Double( pd[2] ).doubleValue() } ;
			} else {
				colorGray = new Double( pd[0] ).doubleValue() ;
			}
		} catch ( NumberFormatException e ) {}
	}

	public void headPS( ApplicationPostscriptStream ps ) {
	}

	public void emitPS( ApplicationPostscriptStream ps ) {
		if ( colorRGB == null )
			ps.operator.setgray( colorGray ) ;
		else
			ps.operator.setrgbcolor( colorRGB[0], colorRGB[1], colorRGB[2] ) ;
	}

	public void tailPS( ApplicationPostscriptStream ps ) {
	}
}
