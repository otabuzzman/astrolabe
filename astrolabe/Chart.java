
package astrolabe;

public interface Chart {
	public void initPS( PostscriptStream ps ) ;
	public Vector project( double[] eq ) ;
	public Vector project( double RA, double d ) ;
	public double[] unproject( Vector xy ) ;
	public double[] unproject( double x, double y ) ;
	public boolean isNorthern();
	public boolean isSouthern();
	public void rollup() ;
}
