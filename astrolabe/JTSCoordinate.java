
package astrolabe;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

@SuppressWarnings("serial")
public class JTSCoordinate extends Coordinate {

	private final static int C_EDGE_MAXY = 1 ; // normally top
	private final static int C_EDGE_MAXX = 2 ; // normally right
	private final static int C_EDGE_MINY = 3 ; // normally bottom
	private final static int C_EDGE_MINX = 4 ; // normally left
	private final static int C_EDGE_NONE = 0 ;

	public JTSCoordinate( double[] xy ) {
		super( xy[0], xy[1] ) ;
	}

	public int edge( Envelope box ) {
		double minx, miny, maxx, maxy, curx, cury ;

		minx = java.lang.Math.floor( box.getMinX() ) ;
		miny = java.lang.Math.floor( box.getMinY() ) ;
		maxx = java.lang.Math.floor( box.getMaxX() ) ;
		maxy = java.lang.Math.floor( box.getMaxY() ) ;

		curx = java.lang.Math.floor( x ) ;
		cury = java.lang.Math.floor( y ) ;

		return
		cury == maxy ? C_EDGE_MAXY :
			curx == maxx ? C_EDGE_MAXX :
				cury == miny ? C_EDGE_MINY :
					curx == minx ? C_EDGE_MINX :
						C_EDGE_NONE ;
	}
}
