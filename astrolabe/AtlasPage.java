
package astrolabe;

@SuppressWarnings("serial")
public class AtlasPage extends astrolabe.model.AtlasPage implements PostscriptEmitter {

	public void headPS( ApplicationPostscriptStream ps ) {
	}

	public void emitPS( ApplicationPostscriptStream ps ) {
		ps.array( true ) ;
		ps.push( getP0x() ) ;
		ps.push( getP0y() ) ;
		ps.push( getP1x() ) ;
		ps.push( getP1y() ) ;
		ps.push( getP2x() ) ;
		ps.push( getP2y() ) ;
		ps.push( getP3x() ) ;
		ps.push( getP3y() ) ;
		ps.array( false ) ;

		ps.op( "newpath" ) ;
		ps.op( "gdraw" ) ;

		ps.op( "closepath" ) ;
		ps.op( "stroke" ) ;
	}

	public void tailPS( ApplicationPostscriptStream ps ) {
	}
}
