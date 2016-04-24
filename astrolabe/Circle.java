
package astrolabe;

public interface Circle {
	public void initPS( PostscriptStream ps ) ;
	public double tangentAngle( double distance ) throws ParameterNotValidException ;
	public Vector tangentVector( double distance ) throws ParameterNotValidException ;
	public Vector cartesian( double distance, double shift ) throws ParameterNotValidException ;
	public java.util.Vector<Vector> cartesianList() throws ParameterNotValidException ;
	public java.util.Vector<Vector> cartesianList( double shift ) throws ParameterNotValidException ;
	public java.util.Vector<Vector> cartesianList( double begin, double end, double shift ) throws ParameterNotValidException ;
	public boolean isParallel() ;
	public boolean isMeridian() ;
	public double span0Distance( double span ) ;
	public double spanNDistance( double span, int n ) throws ParameterNotValidException ;
}
