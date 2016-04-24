
package astrolabe;

@SuppressWarnings("serial")
public class DialHour extends DialDegree {

	public DialHour( Object peer, Circle circle ) {
		super( peer, circle, Math.rad1h ) ;
	}
}
