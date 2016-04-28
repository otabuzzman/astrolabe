
package astrolabe;

public interface CatalogRecord {
	public astrolabe.model.Body toModel( double epoch ) throws ParameterNotValidException ;
	public boolean matchAny( java.util.Set<String> set ) ;
	public boolean matchAll( java.util.Set<String> set ) ;
	public java.util.Set<String> matchSet( java.util.Set<String> set ) ;
	public java.util.Set<String> identSet() ;
	public String ident() ;
	public java.util.Vector<double[]> list( Projector projector ) ;
	public void register() ;
}
