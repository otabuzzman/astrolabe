
package astrolabe;

public interface Projector {
	public double[] project( double[] ho ) ;
	public double[] project( double az, double al ) ;
	public double[] unproject( double[] xy ) ;
	public double[] unproject( double x, double y ) ;
	public double[] convert( double[] ho ) ;
	public double[] convert( double az, double al ) ;
	public double[] unconvert( double[] eq ) ;
	public double[] unconvert( double RA, double d ) ;
}
