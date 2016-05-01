
package astrolabe;

import com.vividsolutions.jts.geom.Coordinate;

@SuppressWarnings("serial")
public class JTSCoordinate extends Coordinate {

	public JTSCoordinate( double[] xy ) {
		super( xy[0], xy[1] ) ;
	}

	public JTSCoordinate( Coordinate xy ) {
		super( xy ) ;
	}

	public String quadrant() {
		return
		( x >= 0 && y >= 0 ) ? "I" :
			( x < 0 && y >= 0 ) ? "II" :
				( x < 0 && y < 0 ) ? "III" : "IV" ;
	}

	public String quadrant( JTSCoordinate botleft, JTSCoordinate topright ) {
		double minx, miny, maxx, maxy, curx, cury ;

		minx = java.lang.Math.floor( botleft.x*1000. ) ;
		miny = java.lang.Math.floor( botleft.y*1000. ) ;
		maxx = java.lang.Math.floor( topright.x*1000. ) ;
		maxy = java.lang.Math.floor( topright.y*1000. ) ;

		curx = java.lang.Math.floor( x*1000. ) ;
		cury = java.lang.Math.floor( y*1000. ) ;

		return quadrant()+(
				cury == maxy ? "t" :
					curx == maxx ? "r" :
						cury == miny ? "b" :
							curx == minx ? "l" : "" ) ;
	}
}
