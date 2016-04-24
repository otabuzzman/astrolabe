
package astrolabe;

public interface Circle {
	public void initPS( PostscriptStream ps ) ;
	public double tangentAngle( double distance ) throws ParameterNotValidException ;
	public Vector tangentVector( double distance ) throws ParameterNotValidException ;
	public Vector cartesian( double distance, double shift ) throws ParameterNotValidException ;
	public Vector cartesianA( double shift ) ;
	public Vector cartesianO( double shift ) ;
	public Horizon getHo() ;
	public java.util.Vector<Vector> cartesianList() throws ParameterNotValidException ;
	public java.util.Vector<Vector> cartesianList( double shift ) throws ParameterNotValidException ;
	public java.util.Vector<Vector> cartesianList( double begin, double end, double shift ) throws ParameterNotValidException ;
	public boolean isParallel() ;
	public boolean isMeridian() ;
	public boolean isClosed() ;
	public boolean examine( double v ) ;
	public double intersect( CircleParallel circle, boolean leading ) throws ParameterNotValidException ;
	public double intersect( CircleMeridian circle, boolean leading ) throws ParameterNotValidException ;
	public double span0Distance( double span ) ;
	public double spanNDistance( double span, int n ) throws ParameterNotValidException ;
}
