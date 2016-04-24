
package astrolabe;

public interface Circle {
	public void initPS( PostscriptStream ps ) ;
	public double tangentAngle( double[] ho ) ;
	public double tangentAngle( double az, double al ) ;
	public Vector tangentVector( double[] ho ) ;
	public Vector tangentVector( double az, double al ) ;
	public Vector cartesian( double[] ho, double shift ) ;
	public Vector cartesian( double az, double al, double shift ) ;
	public java.util.Vector<Vector> cartesianList() ;
	public java.util.Vector<Vector> cartesianList( double shift, double division ) ;
	public boolean isParallel() ;
	public boolean isMeridian() ;
	public double getAngle() ;
	public double getBegin();
	public double getEnd();
}
