
package astrolabe;

public interface Quantity {
	public void setSpan( double span ) ;
	public double span0Distance() ;
	public double spanNDistance( int n ) throws ParameterNotValidException ;
	public boolean isSpanModN( double span, int n ) ;
	public void register( String key, int n ) ;
	public Object clone() ;
}
