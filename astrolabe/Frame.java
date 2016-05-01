
package astrolabe;

import org.exolab.castor.xml.ValidationException;

@SuppressWarnings("serial")
public class Frame extends astrolabe.model.Frame implements PostscriptEmitter {

	private final static String DEFAULT_ANCHOR = "0:0" ; // bottomleft

	private double[] origin = new double[2] ; // bottom left x/y
	private double[] extent = new double[2] ; // frame size x/y

	public Frame( Object peer, Layout layout ) throws ParameterNotValidException {
		double[] frame ;

		ApplicationHelper.setupCompanionFromPeer( this, peer ) ;
		try {
			validate() ;
		} catch ( ValidationException e ) {
			throw new ParameterNotValidException( e.toString() ) ;
		}

		frame = layout.frame( getNumber() ) ;

		origin[0] = frame[0] ;
		origin[1] = frame[1] ;
		extent[0] = frame[2]-frame[0] ;
		extent[1] = frame[3]-frame[1] ;
	}

	public void headPS( PostscriptStream ps ) {
	}

	public void emitPS( PostscriptStream ps ) {
		String[] xyRaw ;
		double[] xyVal ;

		xyRaw = ApplicationHelper.getPreferencesKV(
				ApplicationHelper.getClassNode( this, null, null ),
				getAnchor(), DEFAULT_ANCHOR )
				.split( ":" ) ;

		xyVal = new double[2] ;
		xyVal[0] = origin[0]+extent[0]*new Double( xyRaw[0] ).doubleValue() ;
		xyVal[1] = origin[1]+extent[1]*new Double( xyRaw[1] ).doubleValue() ;

		ps.operator.moveto( xyVal ) ;
	}

	public void tailPS( PostscriptStream ps ) {
	}
}
