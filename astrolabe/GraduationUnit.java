
package astrolabe;

import java.util.Vector;

import caa.CAACoordinateTransformation;

public class GraduationUnit extends Model implements Graduation {

	protected double space ;
	protected double linelength ;
	protected double linewidth ;

	private astrolabe.Vector origin ;
	private astrolabe.Vector tangent ;

	public GraduationUnit( astrolabe.Vector origin, astrolabe.Vector tangent ) {
		this.origin = (astrolabe.Vector) origin.clone() ;
		this.tangent = (astrolabe.Vector) tangent.clone() ;

		space = getClassNode( null, null ).getDouble( "space", .4 ) ;
		linelength = getClassNode( null, null ).getDouble( "linelength", 2.8 ) ;
		linewidth = getClassNode( null, null ).getDouble( "linewidth", .01 ) ;
	}

	public Vector<astrolabe.Vector> cartesianList() {
		double rad90 ;
		astrolabe.Vector a, b ;
		Vector<astrolabe.Vector> r ;

		rad90 = CAACoordinateTransformation.DegreesToRadians( 90 ) ;

		a = (astrolabe.Vector) tangent.clone() ;
		b = (astrolabe.Vector) tangent.clone() ;
		r = new Vector<astrolabe.Vector>() ;

		a.rotate( rad90 ) ;
		a.size( space ) ;
		a.add( origin ) ;
		r.add( a ) ;

		b.rotate( rad90 ) ;
		b.size( space+linelength ) ;
		b.add( origin ) ;
		r.add( b ) ;

		return r ;
	}

	public void initPS( PostscriptStream ps ) {
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
		degA = CAACoordinateTransformation.RadiansToDegrees( radA ) ;

		ps.operator.rotate( degA ) ;
	}
}
