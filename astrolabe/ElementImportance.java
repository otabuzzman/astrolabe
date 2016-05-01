
package astrolabe;

public class ElementImportance implements PostscriptEmitter {

	private final static String DEFAULT_IMPORTANCE = ".1:0" ;

	private double linewidth ;
	private double[] linedash ;

	public ElementImportance( String importance ) {
		String iv, id[] ;

		iv = Configuration.getValue(
				Configuration.getClassNode( this, null, null ),
				importance, DEFAULT_IMPORTANCE ) ;
		id = iv.split( ":" ) ;

		linewidth = new Double( id[0] ).doubleValue() ;

		if ( id.length>2 ) {
			linedash = new double[] {
					new Double( id[1] ).doubleValue(),
					new Double( id[1] ).doubleValue()*new Double( id[2] ).doubleValue() } ;
		} else {
			linedash = new double[] {
					new Double( id[1] ).doubleValue() } ;
		}
	}

	public void headPS( PostscriptStream ps ) {
	}

	public void emitPS( PostscriptStream ps ) {
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

	public void tailPS( PostscriptStream ps ) {
	}
}
