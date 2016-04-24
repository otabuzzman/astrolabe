
package astrolabe;

import java.util.Vector;

import caa.CAACoordinateTransformation;

public class GraduationSpan extends Model implements Graduation {

	protected double space ;
	protected double linelength ;
	protected double linewidth ;

	private astrolabe.Vector tangent ;
	private astrolabe.Vector origin ;

	public GraduationSpan( astrolabe.Vector origin, astrolabe.Vector tangent ) throws ParameterNotValidException {
		this.origin = origin ;
		this.tangent = tangent ;

		space = getClassNode( null, null ).getDouble( "space", .4 ) ;
		linelength = getClassNode( null, null ).getDouble( "linelength", 2.8 ) ;
		linewidth = getClassNode( null, null ).getDouble( "linewidth", .01 ) ;
	}

	private Vector<astrolabe.Vector> cartesianList() {
		astrolabe.Vector a, b ;
		Vector<astrolabe.Vector> r ;

		a = (astrolabe.Vector) tangent.clone() ;
		b = (astrolabe.Vector) tangent.clone() ;
		r = new Vector<astrolabe.Vector>() ;

		a.size( space ) ;
		a.add( origin ) ;
		r.add( a ) ;

		b.size( space+linelength ) ;
		b.add( origin ) ;
		r.add( b ) ;

		return r ;
	}

	private void initPS( PostscriptStream ps ) {
		ps.operator.setlinewidth( linewidth ) ;
	}

	public void emitPS( PostscriptStream ps ) {
		java.util.Vector<astrolabe.Vector> v ;
		java.util.Vector<double[]> d ;
		double radA, degA ;

		initPS( ps ) ;

		v = cartesianList() ;
		d = CircleHelper.convertCartesianVectorToDouble( v ) ;

		ps.polyline( d ) ;
		ps.operator.stroke() ;

		radA = java.lang.Math.atan2( tangent.getY(), tangent.getX() ) ;
		degA = CAACoordinateTransformation.RadiansToDegrees( radA )-90 ;

		ps.operator.rotate( degA ) ;
	}
}
