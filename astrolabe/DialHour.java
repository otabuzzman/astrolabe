
package astrolabe;

@SuppressWarnings("serial")
public class DialHour extends DialDegree {

	// qualifier key (QK_)
	private final static String QK_ANGLE = "angle" ;

	public DialHour( Baseline baseline ) {
		super( baseline ) ;
	}

	public double getSpan() {
		return super.getSpan()*15 ;
	}

	public double getHalf() {
		return super.getHalf()*15 ;
	}

	public double getFull() {
		return super.getFull()*15 ;
	}

	protected void register( double angle ) {
		new DMS( angle/15 ).register( this, QK_ANGLE ) ;
	}
}
