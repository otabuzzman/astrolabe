
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

	public int quadrant() {
		return
		( x >= 0 && y >= 0 ) ? 1 :
			( x < 0 && y >= 0 ) ? 2 :
				( x < 0 && y < 0 ) ? 3 : 4 ;
	}

	public int boundary( Envelope box ) {
		double minx, miny, maxx, maxy, curx, cury ;

		minx = java.lang.Math.floor( box.getMinX()*1000. ) ;
		miny = java.lang.Math.floor( box.getMinY()*1000. ) ;
		maxx = java.lang.Math.floor( box.getMaxX()*1000. ) ;
		maxy = java.lang.Math.floor( box.getMaxY()*1000. ) ;

		curx = java.lang.Math.floor( x*1000. ) ;
		cury = java.lang.Math.floor( y*1000. ) ;

		return
		cury == maxy ? 1 :
			curx == maxx ? 2 :
				cury == miny ? 3 :
					curx == minx ? 4 : 0 ;
	}
}
