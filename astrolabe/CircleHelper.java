
package astrolabe;

public class CircleHelper extends Model {

	public static java.util.Vector<double[]> convertCartesianVectorToDouble( java.util.Vector<Vector> vector ) {
		java.util.Vector<double[]> r ;
		Vector v ;

		r = new java.util.Vector<double[]>() ;
		for ( int n=0 ; n<vector.size() ; n++ ) {
			v = vector.get( n ) ;
			r.add( new double[] { v.getX(), v.getY() } ) ;
		}

		return r ;
	}

	public static java.util.Vector<Vector> convertCartesianDoubleToVector( java.util.Vector<double[]> vector ) {
		java.util.Vector<Vector> r ;
		double[] v ;

		r = new java.util.Vector<Vector>() ;
		for ( int n=0 ; n<vector.size() ; n++ ) {
			v = vector.get( n ) ;
			r.add( new Vector( v[0], v[1] ) ) ;
		}

		return r ;
	}
}
