
package astrolabe;

import java.util.List;

@SuppressWarnings("serial")
public class GraduationSpan extends astrolabe.model.GraduationSpan implements PostscriptEmitter {

	// configuration key (CK_)
	private final static String CK_SPACE			= "space" ;
	private final static String CK_LINELENGTH		= "linelength" ;
	private final static String CK_LINEWIDTH		= "linewidth" ;

	private final static String CK_HALO				= "halo" ;
	private final static String CK_HALOMIN			= "halomin" ;
	private final static String CK_HALOMAX			= "halomax" ;

	private final static double DEFAULT_SPACE		= .4 ;
	private final static double DEFAULT_LINELENGTH	= 2.8 ;
	private final static double DEFAULT_LINEWIDTH	= .01 ;

	private final static double DEFAULT_HALO		= 4 ;
	private final static double DEFAULT_HALOMIN		= .08 ;
	private final static double DEFAULT_HALOMAX		= .4 ;

	private double space ;
	private double linelength ;
	private double linewidth ;

	private Vector tangent ;
	private Vector origin ;

	public GraduationSpan( double[] origin, double[] tangent ) {
		Configuration conf ;

		this.origin = new Vector( origin[0], origin[1] ) ;
		this.tangent = new Vector( tangent[0], tangent[1] ) ;

		this.tangent.apply( new double[] { 0, -1, 0, 1, 0, 0, 0, 0, 1 } ) ; // rotate 90 degrees counter clockwise

		conf = new Configuration( this ) ;

		space = conf.getValue( CK_SPACE, DEFAULT_SPACE ) ;
		linelength = conf.getValue( CK_LINELENGTH, DEFAULT_LINELENGTH ) ;
		linewidth = conf.getValue( CK_LINEWIDTH, DEFAULT_LINEWIDTH ) ;
	}

	public void headPS( ApplicationPostscriptStream ps ) {
		ps.operator.setlinewidth( linewidth ) ;
	}

	public void emitPS( ApplicationPostscriptStream ps ) {
		Configuration conf ;
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
		ps.gdraw() ;

		// halo stroke
		ps.operator.currentlinewidth() ;

		ps.operator.dup() ;
		ps.operator.div( 100 ) ;
		conf = new Configuration( this ) ;
		ps.push( conf.getValue( CK_HALO, DEFAULT_HALO ) ) ; 
		ps.operator.mul() ;
		ps.push( conf.getValue( CK_HALOMIN, DEFAULT_HALOMIN ) ) ; 
		ps.max() ;
		ps.push( conf.getValue( CK_HALOMAX, DEFAULT_HALOMAX ) ) ; 
		ps.min() ;

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

	public void tailPS( ApplicationPostscriptStream ps ) {
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
