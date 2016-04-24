
package astrolabe;

import org.exolab.castor.xml.ValidationException;

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

	public GraduationSpan( Object peer, double[] origin, double[] tangent ) throws ParameterNotValidException {
		ApplicationHelper.setupCompanionFromPeer( this, peer ) ;
		try {
			validate() ;
		} catch ( ValidationException e ) {
			throw new ParameterNotValidException( e.toString() ) ;
		}

		this.origin = new Vector( origin[0], origin[1] ) ;
		this.tangent = new Vector( tangent[0], tangent[1] ) ;

		this.tangent.apply( new double[] { 0, -1, 0, 1, 0, 0, 0, 0, 1 } ) ; // rotate 90 degrees counter clockwise

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

	public void emitPS( PostscriptStream ps ) {
		java.util.Vector<double[]> v ;
		double rad, deg ;
		double[] xy ;

		v = list() ;

		ps.operator.mark() ;
		for ( int n=v.size() ; n>0 ; n-- ) {
			xy = (double[]) v.get( n-1 ) ;
			ps.push( xy[0] ) ;
			ps.push( xy[1] ) ;
		}
		try {
			ps.custom( ApplicationConstant.PS_PROLOG_POLYLINE ) ;

			ps.operator.gsave() ;
			ps.operator.setlinecap( 2 ) ;
			ps.custom( ApplicationConstant.PS_PROLOG_HALOSTROKE ) ;
			ps.operator.grestore() ;
		} catch ( ParameterNotValidException e ) {
			throw new RuntimeException( e.toString() ) ;
		}
		ps.operator.gsave() ;
		ps.operator.stroke() ;
		ps.operator.grestore() ;

		rad = java.lang.Math.atan2( tangent.y, tangent.x ) ;
		deg = CAACoordinateTransformation.RadiansToDegrees( rad )-90 ;

		ps.operator.rotate( deg ) ;

		if ( getAnnotationStraight() != null ) {
			try {
				ApplicationHelper.emitPS( ps, getAnnotationStraight() ) ;
			} catch ( ParameterNotValidException e ) {
				throw new RuntimeException( e.toString() ) ;
			}
		}
	}

	public void tailPS( PostscriptStream ps ) {
	}

	private java.util.Vector<double[]> list() {
		java.util.Vector<double[]> r = new java.util.Vector<double[]>() ;
		Vector a, b ;

		a = new Vector( tangent ) ;
		b = new Vector( tangent ) ;

		a.scale( space ) ;
		a.add( origin ) ;
		r.add( new double[] { a.x, a.y } ) ;

		b.scale( space+linelength ) ;
		b.add( origin ) ;
		r.add( new double[] { b.x, b.y } ) ;

		return r ;
	}

}
