
package astrolabe;

import java.util.Vector;

import caa.CAACoordinateTransformation;

public class GraduationSpan implements Graduation {

	private double space ;
	private double linelength ;
	private double linewidth ;

	private astrolabe.Vector tangent ;
	private astrolabe.Vector origin ;

	public GraduationSpan( astrolabe.Vector origin, astrolabe.Vector tangent ) {
		this.origin = origin ;
		this.tangent = tangent ;

		space = ApplicationHelper.getClassNode( this, null, null ).getDouble( ApplicationConstant.PK_GRADUATION_SPACE, .4 ) ;
		linelength = ApplicationHelper.getClassNode( this, null, null ).getDouble( ApplicationConstant.PK_GRADUATION_LINELENGTH, 2.8 ) ;
		linewidth = ApplicationHelper.getClassNode( this, null, null ).getDouble( ApplicationConstant.PK_GRADUATION_LINEWIDTH, .01 ) ;
	}

	private Vector<astrolabe.Vector> cartesianList() {
		astrolabe.Vector a, b ;
		Vector<astrolabe.Vector> r ;

		a = (astrolabe.Vector) tangent.copy() ;
		b = (astrolabe.Vector) tangent.copy() ;
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
		d = ApplicationHelper.convertCartesianVectorToDouble( v ) ;

		ps.polyline( d ) ;
		ps.operator.stroke() ;

		radA = java.lang.Math.atan2( tangent.getY(), tangent.getX() ) ;
		degA = CAACoordinateTransformation.RadiansToDegrees( radA )-90 ;

		ps.operator.rotate( degA ) ;
	}

	public void setSpace( double space ) {
		this.space = space ;
	}

	public void setLinelength( double linelength ) {
		this.linelength = linelength ;
	}

	public void setLinewidth( double linewidth ) {
		this.linewidth = linewidth ;
	}
}
