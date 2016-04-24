
package astrolabe;

@SuppressWarnings("serial")
public class DialHour extends DialDegree {

	public DialHour( astrolabe.model.DialHour peer, Circle circle ) {
		setup( peer, circle, Math.rad1h ) ;
	}
}
