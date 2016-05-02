
package astrolabe;

public interface CatalogRecord {
	public boolean isOK() ;
	public void recognize() throws ParameterNotValidException ;
	public double[] RA() ;
	public double[] de() ;
}
