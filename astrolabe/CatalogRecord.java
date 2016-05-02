
package astrolabe;

import com.vividsolutions.jts.geom.Coordinate;

public interface CatalogRecord {
	public boolean isOK() ;
	public void inspect() throws ParameterNotValidException ;
	public double RA() ;
	public double de() ;
	public Coordinate[] list() ;
}
