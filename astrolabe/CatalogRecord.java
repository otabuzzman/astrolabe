
package astrolabe;

import java.util.List;

public interface CatalogRecord {
	public astrolabe.model.Body toModel( double epoch ) throws ParameterNotValidException ;
	public boolean matchAny( java.util.Set<String> set ) ;
	public boolean matchAll( java.util.Set<String> set ) ;
	public java.util.Set<String> matchSet( java.util.Set<String> set ) ;
	public java.util.Set<String> identSet() ;
	public String ident() ;
	public List<double[]> list( Projector projector ) ;
	public void register() ;
}
