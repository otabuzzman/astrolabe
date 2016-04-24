
package astrolabe;

public interface Horizon {
	public void initPS( PostscriptStream ps ) ;
	public double[] convert( double[] ho) ;
	public double[] convert( double phi, double theta ) ;
	public double[] unconvert( double[] eq ) ;
	public double[] unconvert( double RA, double d ) ;
	public double getLa() ;
	public double getST() ;
	public boolean isEcliptical() ;
	public boolean isEquatorial() ;
	public boolean isGalactic() ;
	public boolean isLocal() ;
}
