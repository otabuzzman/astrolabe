
package astrolabe;

public interface Span {
	public void set( double span ) ;
	public double distance0() ;
	public double distanceN( int n ) throws ParameterNotValidException ;
	public boolean isGraduationModN( double graduation, int n ) ;
	public void register( int n ) ;
}
