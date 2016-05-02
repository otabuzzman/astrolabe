
package astrolabe;

import java.util.List;
import java.util.prefs.Preferences;

@SuppressWarnings("serial")
public class GraduationSpan extends astrolabe.model.GraduationSpan implements PostscriptEmitter {

	private final static double DEFAULT_SPACE		= .4 ;
	private final static double DEFAULT_LINELENGTH	= 2.8 ;
	private final static double DEFAULT_LINEWIDTH	= .01 ;

	private double space ;
	private double linelength ;
	private double linewidth ;

	private Vector tangent ;
	private Vector origin ;

	public GraduationSpan( double[] origin, double[] tangent ) {
		Preferences node ;

		this.origin = new Vector( origin[0], origin[1] ) ;
		this.tangent = new Vector( tangent[0], tangent[1] ) ;

		this.tangent.apply( new double[] { 0, -1, 0, 1, 0, 0, 0, 0, 1 } ) ; // rotate 90 degrees counter clockwise

		node = Configuration.getClassNode( this, null, null ) ;

		space = Configuration.getValue( node, ApplicationConstant.PK_GRADUATION_SPACE, DEFAULT_SPACE ) ;
		linelength = Configuration.getValue( node, ApplicationConstant.PK_GRADUATION_LINELENGTH, DEFAULT_LINELENGTH ) ;
		linewidth = Configuration.getValue( node, ApplicationConstant.PK_GRADUATION_LINEWIDTH, DEFAULT_LINEWIDTH ) ;
	}

	public void headPS( AstrolabePostscriptStream ps ) {
		ps.operator.setlinewidth( linewidth ) ;
	}

	public void emitPS( AstrolabePostscriptStream ps ) {
		List<double[]> v ;
		double[] xy ;

		v = list() ;

		ps.array( true ) ;
		for ( int n=0 ; n<v.size() ; n++ ) {
			xy = (double[]) v.get( n ) ;
			ps.push( xy[0] ) ;
			ps.push( xy[1] ) ;
		}
		ps.array( false ) ;

		ps.operator.newpath() ;
		ps.push( ApplicationConstant.PS_PROLOG_GDRAW ) ;

		// halo stroke
		ps.operator.currentlinewidth() ;

		ps.operator.dup() ;
		ps.operator.div( 100 ) ;
		ps.push( (Double) ( Registry.retrieve( ApplicationConstant.PK_CHART_HALO ) ) ) ; 
		ps.operator.mul() ;
		ps.push( (Double) ( Registry.retrieve( ApplicationConstant.PK_CHART_HALOMIN ) ) ) ; 
		ps.push( ApplicationConstant.PS_PROLOG_MAX ) ;
		ps.push( (Double) ( Registry.retrieve( ApplicationConstant.PK_CHART_HALOMAX ) ) ) ; 
		ps.push( ApplicationConstant.PS_PROLOG_MIN ) ;

		ps.operator.mul( 2 ) ;
		ps.operator.add() ;
		ps.operator.gsave() ;
		ps.operator.setlinewidth() ;
		ps.operator.setlinecap( 2 ) ;
		ps.operator.setgray( 1 ) ;
		ps.operator.stroke() ;
		ps.operator.grestore() ;

		ps.operator.gsave() ;
		ps.operator.stroke() ;
		ps.operator.grestore() ;

		ps.operator.rotate( Math.atan2( tangent.y, tangent.x )-90 ) ;

		if ( getAnnotationStraight() != null ) {
			AnnotationStraight annotation ;

			for ( int i=0 ; i<getAnnotationStraightCount() ; i++ ) {
				ps.operator.gsave() ;

				annotation = new AnnotationStraight() ;
				getAnnotationStraight( i ).setupCompanion( annotation ) ;
				annotation.register() ;

				annotation.headPS( ps ) ;
				annotation.emitPS( ps ) ;
				annotation.tailPS( ps ) ;

				ps.operator.grestore() ;
			}
		}
	}

	public void tailPS( AstrolabePostscriptStream ps ) {
	}

	private List<double[]> list() {
		List<double[]> list ;
		Vector a, b ;

		list = new java.util.Vector<double[]>() ;

		a = new Vector( tangent ) ;
		b = new Vector( tangent ) ;

		a.scale( space ) ;
		a.add( origin ) ;
		list.add( new double[] { a.x, a.y } ) ;

		b.scale( space+linelength ) ;
		b.add( origin ) ;
		list.add( new double[] { b.x, b.y } ) ;

		return list ;
	}

}
