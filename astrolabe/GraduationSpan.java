
package astrolabe;

import caa.CAACoordinateTransformation;

@SuppressWarnings("serial")
public class GraduationSpan extends astrolabe.model.GraduationSpan implements Graduation {

	private final static double DEFAULT_SPACE = .4 ;
	private final static double DEFAULT_LINELENGTH = 2.8 ;
	private final static double DEFAULT_LINEWIDTH = .01 ;

	private double space ;
	private double linelength ;
	private double linewidth ;

	private Vector tangent ;
	private Vector origin ;

	protected GraduationSpan() {
	}

	public GraduationSpan( astrolabe.model.GraduationSpan peer, double[] origin, double[] tangent ) {
		setup( peer, origin, tangent ) ;
	}

	public void setup( Object peer, double[] origin, double[] tangent ) {
		ApplicationHelper.setupCompanionFromPeer( this, peer ) ;

		this.origin = new Vector( origin ) ;
		this.tangent = new Vector( tangent ) ;

		this.tangent.rotate( Math.rad90 ) ;

		space = ApplicationHelper.getClassNode( this,
				null, null ).getDouble( ApplicationConstant.PK_GRADUATION_SPACE, DEFAULT_SPACE ) ;
		linelength = ApplicationHelper.getClassNode( this,
				null, null ).getDouble( ApplicationConstant.PK_GRADUATION_LINELENGTH, DEFAULT_LINELENGTH ) ;
		linewidth = ApplicationHelper.getClassNode( this,
				null, null ).getDouble( ApplicationConstant.PK_GRADUATION_LINEWIDTH, DEFAULT_LINEWIDTH ) ;
	}

	public void headPS( PostscriptStream ps ) {
		ps.operator.setlinewidth( linewidth ) ;
	}

	public void emitPS( PostscriptStream ps ) throws ParameterNotValidException {
		java.util.Vector<double[]> v ;
		double rad, deg ;
		double[] xy ;

		v = list() ;

		ps.operator.mark() ;
		for ( int c=v.size() ; c>0 ; c-- ) {
			xy = (double[]) v.get( c-1 ) ;
			ps.push( xy[0] ) ;
			ps.push( xy[1] ) ;
		}
		ps.custom( ApplicationConstant.PS_PROLOG_POLYLINE ) ;
		ps.operator.stroke() ;

		rad = java.lang.Math.atan2( tangent.getY(), tangent.getX() ) ;
		deg = CAACoordinateTransformation.RadiansToDegrees( rad )-90 ;

		ps.operator.rotate( deg ) ;

		try {
			ApplicationHelper.emitPS( ps, getAnnotationStraight() ) ;
		} catch ( ParameterNotValidException e ) {}
	}

	public void tailPS( PostscriptStream ps ) {
	}

	private java.util.Vector<double[]> list() {
		java.util.Vector<double[]> r = new java.util.Vector<double[]>() ;
		Vector a, b ;

		a = (Vector) tangent.copy() ;
		b = (Vector) tangent.copy() ;

		a.size( space ) ;
		a.add( origin ) ;
		r.add( new double[] { a.getX(), a.getY() } ) ;

		b.size( space+linelength ) ;
		b.add( origin ) ;
		r.add( new double[] { b.getX(), b.getY() } ) ;

		return r ;
	}

}
