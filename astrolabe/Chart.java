
package astrolabe;

public interface Chart {
	public void emitPS( PostscriptStream ps ) throws ParameterNotValidException ;
	public Vector project( double[] eq ) ;
	public Vector project( double RA, double d ) ;
	public double[] unproject( Vector xy ) ;
	public double[] unproject( double x, double y ) ;
	public boolean isNorthern();
	public boolean isSouthern();
	public void rollup( PostscriptStream ps ) ;
}
