
package astrolabe;

import org.exolab.castor.xml.ValidationException;

public interface CatalogRecord {
	public boolean isValid() ;
	public void validate() throws ParameterNotValidException ;
	public void toModel( astrolabe.model.Body body ) throws ValidationException ;
	public void register() ;
	public double[] RA() ;
	public double[] de() ;
}
