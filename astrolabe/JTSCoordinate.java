
package astrolabe;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

@SuppressWarnings("serial")
public class JTSCoordinate extends Coordinate {

	public JTSCoordinate( double[] xy ) {
		super( xy[0], xy[1] ) ;
	}

	public JTSCoordinate( Coordinate xy ) {
		super( xy ) ;
	}

	public int boundary( Envelope box ) {
		double minx, miny, maxx, maxy, curx, cury ;

		minx = normalize( box.getMinX() ) ;
		miny = normalize( box.getMinY() ) ;
		maxx = normalize( box.getMaxX() ) ;
		maxy = normalize( box.getMaxY() ) ;

		curx = normalize( x ) ;
		cury = normalize( y ) ;

		return
		cury == maxy ? 1 :
			curx == maxx ? 2 :
				cury == miny ? 3 :
					curx == minx ? 4 : 0 ;
	}

	private static double normalize( double value ) {
		return java.lang.Math.floor( value+.5 ) ;
	}
}
