
package astrolabe;

import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

@SuppressWarnings("serial")
public class JTSCoordinateArraySequence extends CoordinateArraySequence {

	public JTSCoordinateArraySequence( java.util.List<double[]> coordinates ) {
		super( coordinates.size() ) ;

		double[] xy ;

		for ( int i=0 ; i<coordinates.size() ; i++ ) {
			xy = coordinates.get( i ) ;
			setOrdinate( i, 0, xy[0] ) ;
			setOrdinate( i, 1, xy[1] ) ;
		}
	}
}
