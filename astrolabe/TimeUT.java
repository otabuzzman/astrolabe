
package astrolabe;

public class TimeUT extends Model implements Time {

	private double time ;

	public TimeUT( double time ) {
		this.time = time ;
	}

	public double getTimeUT() {
		return time ;
	}
}
