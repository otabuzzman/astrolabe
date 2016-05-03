
package astrolabe;

import com.vividsolutions.jts.geom.Coordinate;

public interface P4Projector {
	public void init( double lam0, double phi1, double R, double k0 ) ;
	public Coordinate forward( Coordinate lamphi ) ;
	public Coordinate inverse( Coordinate xy ) ;
}
