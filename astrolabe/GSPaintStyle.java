
package astrolabe;

public class GSPaintStyle implements PostscriptEmitter {

	private final static double DEFAULT_LINEWIDTH = .1 ;
	private final static double DEFAULT_LINEDASH = 0 ;

	private double linewidth ;
	private double[] linedash ;

	public GSPaintStyle( String paintstyle ) {
		String iv, id[] ;

		iv = Configuration.getValue(
				Configuration.getClassNode( this, null, null ),
				paintstyle, paintstyle ) ;
		id = iv.split( ":" ) ;

		try {
			linewidth = new Double( id[0] ).doubleValue() ;

			if ( id.length>2 ) {
				linedash = new double[] {
						new Double( id[1] ).doubleValue(),
						new Double( id[1] ).doubleValue()*new Double( id[2] ).doubleValue() } ;
			} else {
				linedash = new double[] {
						new Double( id[1] ).doubleValue() } ;
			}
		} catch ( NumberFormatException e ) {
			linewidth = DEFAULT_LINEWIDTH ;
			linedash = new double[] { DEFAULT_LINEDASH } ;
		}
	}

	public void headPS( AstrolabePostscriptStream ps ) {
	}

	public void emitPS( AstrolabePostscriptStream ps ) {
		ps.operator.setlinewidth( linewidth ) ;

		if ( linedash.length>1 ) {
			ps.array( true ) ;
			ps.push( linedash[0] ) ;
			ps.push( linedash[1] ) ;
			ps.array( false ) ;
			ps.push( 0 ) ;
			ps.operator.setdash() ;
		} else {
			ps.operator.setdash( linedash[0] ) ;
		}
	}

	public void tailPS( AstrolabePostscriptStream ps ) {
	}
}
