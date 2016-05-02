
package astrolabe;

import caa.CAACoordinateTransformation;

@SuppressWarnings("serial")
public class DialHour extends DialDegree {

	// qualifier key (QK_)
	private final static String QK_ANGLE = "angle" ;

	private Baseline baseline ;
	private double unit ;

	public DialHour( Baseline baseline ) {
		super( baseline, CAACoordinateTransformation.HoursToDegrees( 1 ) ) ;

		this.baseline = baseline ;	
		this.unit = CAACoordinateTransformation.HoursToDegrees( 1 ) ;
	}

	public void register( int index ) {
		double a ;
		DMS dms ;

		a = baseline.scaleMarkNth( index, getGraduationSpan().getValue()*unit ) ;

		dms = new DMS( a/15 ) ;
		dms.register( this, QK_ANGLE ) ;
	}
}
