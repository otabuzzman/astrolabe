
package astrolabe;

import org.exolab.castor.xml.ValidationException;

@SuppressWarnings("serial")
public class Frame extends astrolabe.model.Frame implements PostscriptEmitter {

	private final static String DEFAULT_ANCHOR = "0:0" ; // bottomleft

	private double[] origin = new double[2] ; // bottom left x/y
	private double[] extent = new double[2] ; // frame size x/y

	private ParserAttribute parser ;

	public Frame( Object peer ) throws ParameterNotValidException {
		Layout layout ; 
		double[] frame ;
		int number ;

		ApplicationHelper.setupCompanionFromPeer( this, peer ) ;
		try {
			validate() ;
		} catch ( ValidationException e ) {
			throw new ParameterNotValidException( e.toString() ) ;
		}

		parser = new ParserAttribute() ;

		layout = (Layout) Registry.retrieve( ApplicationConstant.GC_LAYOUT ) ;

		number = parser.intValue( getNumber() ) ;
		if ( number>0 ) {
			frame = layout.frame( number ) ;
		} else {
			throw new ParameterNotValidException() ;
		}

		origin[0] = frame[0] ;
		origin[1] = frame[1] ;
		extent[0] = frame[2]-frame[0] ;
		extent[1] = frame[3]-frame[1] ;
	}

	public void headPS( PostscriptStream ps ) {
	}

	public void emitPS( PostscriptStream ps ) {
		String anchor ;
		String[] xyRaw ;
		double[] xyVal ;

		anchor = parser.stringValue( getAnchor() ) ;
		xyRaw = ApplicationHelper.getPreferencesKV(
				ApplicationHelper.getClassNode( this, null, null ),
				anchor, DEFAULT_ANCHOR )
				.split( ":" ) ;

		xyVal = new double[2] ;
		xyVal[0] = origin[0]+extent[0]*new Double( xyRaw[0] ).doubleValue() ;
		xyVal[1] = origin[1]+extent[1]*new Double( xyRaw[1] ).doubleValue() ;

		ps.operator.moveto( xyVal ) ;
	}

	public void tailPS( PostscriptStream ps ) {
	}
}
