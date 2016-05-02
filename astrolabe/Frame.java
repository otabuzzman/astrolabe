
package astrolabe;

@SuppressWarnings("serial")
public class Frame extends astrolabe.model.Frame implements PostscriptEmitter {

	private final static String DEFAULT_ANCHOR	= "0:0" ; // bottomleft

	private double originx ;
	private double originy ;
	private double extentx ;
	private double extenty ;

	public Frame( double[] dimension ) {
		originx = dimension[0] ;
		originy = dimension[1] ;
		extentx = dimension[2]-dimension[0] ;
		extenty = dimension[3]-dimension[1] ;
	}

	public void headPS( AstrolabePostscriptStream ps ) {
	}

	public void emitPS( AstrolabePostscriptStream ps ) {
		String[] xyRaw ;
		double[] xyVal ;

		xyRaw = Configuration.getValue(
				Configuration.getClassNode( this, null, null ),
				getAnchor(), DEFAULT_ANCHOR )
				.split( ":" ) ;

		xyVal = new double[2] ;
		xyVal[0] = originx+extentx*new Double( xyRaw[0] ).doubleValue() ;
		xyVal[1] = originy+extenty*new Double( xyRaw[1] ).doubleValue() ;

		ps.operator.moveto( xyVal ) ;
	}

	public void tailPS( AstrolabePostscriptStream ps ) {
	}
}
