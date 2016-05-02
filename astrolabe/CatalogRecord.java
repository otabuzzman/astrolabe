
package astrolabe;

import java.util.List;

public interface CatalogRecord {
	public boolean isOK() ;
	public void inspect() throws ParameterNotValidException ;
	public double RA() ;
	public double de() ;
	public List<double[]> list() ;
}
