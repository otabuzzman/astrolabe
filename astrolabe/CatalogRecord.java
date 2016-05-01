
package astrolabe;

import java.util.List;

import org.exolab.castor.xml.ValidationException;

public interface CatalogRecord {
	public abstract List<double[]> list( Projector projector ) ;
	public abstract astrolabe.model.Body toModel() throws ValidationException ;
	public abstract void register() ;
	public abstract List<String> ident() ;
}
