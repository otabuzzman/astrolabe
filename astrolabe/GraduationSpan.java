
package astrolabe;

import java.util.List;
import java.util.prefs.Preferences;

import org.exolab.castor.xml.ValidationException;

@SuppressWarnings("serial")
public class GraduationSpan extends astrolabe.model.GraduationSpan implements PostscriptEmitter {

	private final static double DEFAULT_SPACE = .4 ;
	private final static double DEFAULT_LINELENGTH = 2.8 ;
	private final static double DEFAULT_LINEWIDTH = .01 ;

	private double space ;
	private double linelength ;
	private double linewidth ;

	private Vector tangent ;
	private Vector origin ;

	public GraduationSpan( Peer peer, double[] origin, double[] tangent ) throws ParameterNotValidException {
		Preferences node ;

		peer.setupCompanion( this ) ;
		try {
			validate() ;
		} catch ( ValidationException e ) {
			throw new ParameterNotValidException( e.toString() ) ;
		}

		node = Configuration.getClassNode( this, null, null ) ;

		this.origin = new Vector( origin[0], origin[1] ) ;
		this.tangent = new Vector( tangent[0], tangent[1] ) ;

		this.tangent.apply( new double[] { 0, -1, 0, 1, 0, 0, 0, 0, 1 } ) ; // rotate 90 degrees counter clockwise

		space = Configuration.getValue( node, ApplicationConstant.PK_GRADUATION_SPACE, DEFAULT_SPACE ) ;
		linelength = Configuration.getValue( node, ApplicationConstant.PK_GRADUATION_LINELENGTH, DEFAULT_LINELENGTH ) ;
		linewidth = Configuration.getValue( node, ApplicationConstant.PK_GRADUATION_LINEWIDTH, DEFAULT_LINEWIDTH ) ;
	}

	public void headPS( PostscriptStream ps ) {
		ps.operator.setlinewidth( linewidth ) ;
	}

	public void emitPS( PostscriptStream ps ) {
		List<double[]> v ;
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

			// halo stroke
			ps.operator.currentlinewidth() ;
			ps.operator.dup();
			ps.push( (Double) ( Registry.retrieve( ApplicationConstant.PK_CHART_HALOMAX ) ) ) ; 
			ps.push( (Double) ( Registry.retrieve( ApplicationConstant.PK_CHART_HALOMIN ) ) ) ; 
			ps.push( (Double) ( Registry.retrieve( ApplicationConstant.PK_CHART_HALO ) ) ) ; 
			ps.custom( ApplicationConstant.PS_PROLOG_HALO ) ;
			ps.operator.mul( 2 ) ;
			ps.operator.add() ;
			ps.operator.gsave() ;
			ps.operator.setlinewidth() ;
			ps.operator.setlinecap( 2 ) ;
			ps.operator.setgray( 1 ) ;
			ps.operator.stroke() ;
			ps.operator.grestore() ;
		} catch ( ParameterNotValidException e ) {
			throw new RuntimeException( e.toString() ) ;
		}
		ps.operator.gsave() ;
		ps.operator.stroke() ;
		ps.operator.grestore() ;

		ps.operator.rotate( Math.atan2( tangent.y, tangent.x )-90 ) ;

		if ( getAnnotationStraight() != null ) {
			try {
				PostscriptEmitter annotation ;

				for ( int i=0 ; i<getAnnotationStraightCount() ; i++ ) {
					ps.operator.gsave() ;

					annotation = new AnnotationStraight( getAnnotationStraight( i ) ) ;
					annotation.headPS( ps ) ;
					annotation.emitPS( ps ) ;
					annotation.tailPS( ps ) ;

					ps.operator.grestore() ;
				}
			} catch ( ParameterNotValidException e ) {
				throw new RuntimeException( e.toString() ) ;
			}
		}
	}

	public void tailPS( PostscriptStream ps ) {
	}

	private List<double[]> list() {
		List<double[]> r = new java.util.Vector<double[]>() ;
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
