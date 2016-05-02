
package astrolabe;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("serial")
public class DialDegree extends astrolabe.model.DialDegree implements PostscriptEmitter {

	private class None implements PostscriptEmitter {

		private final static double DEFAULT_SPACE		= 1 ;

		private double space = 0 ;

		public void headPS(ApplicationPostscriptStream ps) {
			Configuration conf ;
			String qual ;

			conf = new Configuration( getClass().getEnclosingClass() ) ;
			qual = CN_BASELINE+"/"+getBaseline() ;

			space = conf.getValue(
					qual+"/"+CK_SPACE, DEFAULT_SPACE ) ;
		}

		public void emitPS(ApplicationPostscriptStream ps) {
		}

		public void tailPS(ApplicationPostscriptStream ps) {
		}
	}

	private class Line implements PostscriptEmitter {

		private final static double DEFAULT_SPACE		= 1 ;
		private final static double DEFAULT_THICKNESS	= 1.2 ;

		private double space ;
		private double thickness ;

		public Line() {
			Configuration conf ;
			String qual ;

			conf = new Configuration( getClass().getEnclosingClass() ) ;
			qual = CN_BASELINE+"/"+getBaseline() ;

			space = conf.getValue(
					qual+"/"+CK_SPACE, DEFAULT_SPACE ) ;
			thickness = conf.getValue(
					qual+"/"+CK_THICKNESS, DEFAULT_THICKNESS ) ;
		}

		public void headPS(ApplicationPostscriptStream ps) {
			ps.operator.setlinewidth( thickness ) ;
		}

		public void emitPS(ApplicationPostscriptStream ps) {
			Configuration conf ;
			List<double[]> v ;
			double ma, mo, o, span ;
			int m ;

			v = new java.util.Vector<double[]>() ;

			span = getSpan() ;
			o = baseline.scaleMarkNth( -1, span ) ;

			for ( m=0 ; ; m++ ) {
				ma = baseline.scaleMarkNth( m, span ) ;
				mo = baseline.scaleMarkNth( m+1, span ) ;

				v.addAll( baseline.list( null, ma, mo, getReflect()?-space:space ) ) ;

				if ( mo==o )
					break ;
			}
			double[] xy ;

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
		}

		public void tailPS(ApplicationPostscriptStream ps) {
		}
	}

	private class Rail implements PostscriptEmitter {

		private final static double DEFAULT_SPACE		= 1 ;
		private final static double DEFAULT_THICKNESS	= 1.2 ;
		private final static double DEFAULT_LINEWIDTH	= .01 ;

		private double space ;
		private double thickness ;
		private double linewidth ;

		public Rail() {
			Configuration conf ;
			String qual ;

			conf = new Configuration( getClass().getEnclosingClass() ) ;
			qual = CN_BASELINE+"/"+getBaseline() ;

			space = conf.getValue(
					qual+"/"+CK_SPACE, DEFAULT_SPACE ) ;
			thickness = conf.getValue(
					qual+"/"+CK_THICKNESS, DEFAULT_THICKNESS ) ;
			linewidth = conf.getValue(
					qual+"/"+CK_LINEWIDTH, DEFAULT_LINEWIDTH ) ;

		}

		public void headPS(ApplicationPostscriptStream ps) {
			ps.operator.setlinewidth( linewidth ) ;
		}

		public void emitPS(ApplicationPostscriptStream ps) {
			Configuration conf ;
			List<double[]> vDFw = null, vDRv = null, rvDRv ;
			double ma, mo, o, s, span ;
			int m = 0 ;

			span = getSpan()/getGraduationSpan().getDivision() ;
			o = baseline.scaleMarkNth( -1, span ) ;

			for ( ; ; m++ ) {
				ma = baseline.scaleMarkNth( m, span ) ;
				mo = baseline.scaleMarkNth( m+1, span ) ;

				s = m%2==0?space:space+linewidth/2 ;
				s = getReflect()?-s:s ;			
				vDFw = baseline.list( null, ma, mo, s ) ;
				ps.array( true ) ;
				for ( int n=0 ; n<vDFw.size() ; n++ ) {
					double[] xy = (double[]) vDFw.get( n ) ;
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
				ps.operator.setgray( 1 ) ;
				ps.operator.stroke() ;
				ps.operator.grestore() ;

				s = space+( m%2==0?thickness:thickness-linewidth/2 ) ;
				s = getReflect()?-s:s ;
				vDRv = baseline.list( null, ma, mo, s ) ;
				ps.array( true ) ;
				for ( int n=0 ; n<vDRv.size() ; n++ ) {
					double[] xy = (double[]) vDRv.get( n ) ;
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
				ps.operator.setgray( 1 ) ;
				ps.operator.stroke() ;
				ps.operator.grestore() ;

				rvDRv = new java.util.Vector<double[]>( vDRv ) ;
				Collections.reverse( rvDRv ) ;
				vDFw.addAll( rvDRv ) ;

				if ( m%2 == 0 ) { // subunit filled
					double[] xy ;

					ps.array( true ) ;
					for ( int n=0 ; n<vDFw.size() ; n++ ) {
						xy = (double[]) vDFw.get( n ) ;
						ps.push( xy[0] ) ;
						ps.push( xy[1] ) ;
					}
					ps.array( false ) ;

					ps.operator.newpath() ;
					ps.gdraw() ;
					ps.operator.closepath() ;
					ps.operator.fill() ;
				} else { // subunit unfilled
					java.util.Vector<double[]> fw, rv ;
					double[] xy ;

					fw = new java.util.Vector<double[]>( vDFw.subList( 0, vDFw.size()/2 ) ) ;
					rv = new java.util.Vector<double[]>( vDFw.subList( vDFw.size()/2, vDFw.size() ) ) ;

					ps.array( true ) ;
					for ( int n=0 ; n<fw.size() ; n++ ) {
						xy = (double[]) fw.get( n ) ;
						ps.push( xy[0] ) ;
						ps.push( xy[1] ) ;
					}
					ps.array( false ) ;

					ps.operator.newpath() ;
					ps.gdraw() ;
					ps.operator.gsave() ;
					ps.operator.stroke() ;
					ps.operator.grestore() ;
					ps.array( true ) ;
					for ( int n=0 ; n<rv.size() ; n++ ) {
						xy = (double[]) rv.get( n ) ;
						ps.push( xy[0] ) ;
						ps.push( xy[1] ) ;
					}
					ps.array( false ) ;

					ps.operator.newpath() ;
					ps.gdraw() ;
					ps.operator.gsave() ;
					ps.operator.stroke() ;
					ps.operator.grestore() ;
				}

				if ( mo==o )
					break ;
			}
			if ( m%2 == 1 ) {// close unfilled subunit
				List<double[]> vector ;
				double[] xy ;

				vector = new java.util.Vector<double[]>( vDFw.subList( vDFw.size()/2-1, vDFw.size()/2+1 ) ) ;

				ps.operator.newpath() ;
				xy = (double[]) vector.get( 1 ) ;
				ps.push( xy[0] ) ;
				ps.push( xy[1] ) ;
				xy = (double[]) vector.get( 0 ) ;
				ps.push( xy[0] ) ;
				ps.push( xy[1] ) ;
				ps.operator.moveto() ;
				ps.operator.lineto() ;

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
			}
		}

		public void tailPS(ApplicationPostscriptStream ps) {
		}
	}

	// qualifier key (QK_)
	private final static String QK_ANGLE = "angle" ;

	// configuration key (CK_), node (CN_)
	private final static String CK_RISE			= "rise" ;
	private final static String CN_BASELINE		= "baseline" ;
	private final static String CK_SPACE		= "space" ;
	private final static String CK_THICKNESS	= "thickness" ;
	private final static String CK_LINEWIDTH	= "linewidth" ;

	private final static String CK_HALO			= "halo" ;
	private final static String CK_HALOMIN		= "halomin" ;
	private final static String CK_HALOMAX		= "halomax" ;

	private final static double DEFAULT_RISE	= 3.2 ;

	private final static double DEFAULT_HALO	= 4 ;
	private final static double DEFAULT_HALOMIN	= .08 ;
	private final static double DEFAULT_HALOMAX	= .4 ;

	private Baseline baseline ;
	//	private double unit ;

	// attribute value (AV_)
	private final static String AV_NONE = "none" ;
	private final static String AV_LINE = "line" ;
	private final static String AV_RAIL = "rail" ;

	public DialDegree( Baseline baseline ) {
		this.baseline = baseline ;	
	}

	public double getSpan() {
		return getGraduationSpan().getValue() ;
	}

	public double getHalf() {
		return getGraduationHalf() == null ? 0 : getGraduationHalf().getValue() ;
	}

	public double getFull() {
		return getGraduationFull() == null ? 0 : getGraduationFull().getValue() ;
	}

	protected void register( double angle ) {
		new DMS( angle ).register( this, QK_ANGLE ) ;
	}

	protected void degister() {
		DMS.degister( this, QK_ANGLE ) ;
	}

	public void headPS( ApplicationPostscriptStream ps ) {
		PostscriptEmitter pse ;

		switch ( baseline() ) {
		case 0:
			pse = new None() ;
			break ;
		case 1:
			pse = new Line() ;
			break ;
		default:
		case 2:
			pse = new Rail() ;
		}
		pse.headPS( ps ) ;
	}

	public void emitPS( ApplicationPostscriptStream ps ) {
		List<double[]> v ;
		double space, thickness ;
		double xy[], a, o, s, rise ;
		double mo, b[], t[], span, half, full ;
		astrolabe.model.Annotation annotation ;
		PostscriptEmitter emitter ;

		switch ( baseline() ) {
		case 0:
			None none = new None() ;
			space = none.space ;
			thickness = 0 ;

			none.emitPS( ps ) ;
			break ;
		case 1:
			Line line = new Line() ;
			space = line.space ;
			thickness = line.thickness ;

			line.emitPS( ps ) ;
			break ;
		default:
		case 2:
			Rail rail = new Rail() ;
			space = rail.space ;
			thickness = rail.thickness ;

			rail.emitPS( ps ) ;
			break ;
		}

		span = getSpan() ;
		a = baseline.scaleMarkNth( 0, span ) ;
		o = baseline.scaleMarkNth( -1, span ) ;

		for ( int m=0 ; ; m++ ) {
			mo = baseline.scaleMarkNth( m, span ) ;

			b = baseline.project( mo, getReflect()?-( space+thickness ):space+thickness ) ;
			t = baseline.tangent( mo ) ;
			if ( getReflect() ) {
				t[0] = -t[0] ;
				t[1] = -t[1] ;
			}

			register( mo ) ;
			ps.operator.gsave() ;

			emitter = new GraduationSpan( b, t ) ;
			getGraduationSpan().copyValues( emitter ) ;
			if ( ( half = getHalf() )>0 )
				if ( isMultipleSpan( mo, half ) ) {
					emitter = new GraduationHalf( b, t ) ;
					getGraduationHalf().copyValues( emitter ) ;
				}
			if ( ( full = getFull() )>0 )
				if ( isMultipleSpan( mo, full ) ) {
					emitter = new GraduationFull( b, t ) ;
					getGraduationFull().copyValues( emitter ) ;
				}
			emitter.headPS( ps ) ;
			emitter.emitPS( ps ) ;
			emitter.tailPS( ps ) ;

			ps.operator.grestore() ;
			degister() ;

			if ( mo==o )
				break ;
		}

		rise = Configuration.getValue( this, CK_RISE, DEFAULT_RISE ) ;

		s = ( ( space+thickness )+rise ) ;
		v = baseline.list( null, a, o, getReflect()?-s:s ) ;
		ps.array( true ) ;
		for ( int n=0 ; n<v.size() ; n++ ) {
			xy = (double[]) v.get( n ) ;
			ps.push( xy[0] ) ;
			ps.push( xy[1] ) ;
		}
		ps.array( false ) ;

		ps.operator.newpath() ;
		ps.gdraw() ;

		if ( getAnnotation() != null ) {
			for ( int i=0 ; i<getAnnotationCount() ; i++ ) {
				annotation = getAnnotation( i ) ;

				if ( annotation.getAnnotationStraight() != null ) {
					emitter = annotation( annotation.getAnnotationStraight() ) ;
				} else { // annotation.getAnnotationCurved() != null
					emitter = annotation( annotation.getAnnotationCurved() ) ;
				}

				ps.operator.gsave() ;

				emitter.headPS( ps ) ;
				emitter.emitPS( ps ) ;
				emitter.tailPS( ps ) ;

				ps.operator.grestore() ;
			}
		}
	}

	public void tailPS( ApplicationPostscriptStream ps ) {
		PostscriptEmitter pse ;

		switch ( baseline() ) {
		case 0:
			pse = new None() ;
			break ;
		case 1:
			pse = new Line() ;
			break ;
		default:
		case 2:
			pse = new Rail() ;
		}
		pse.tailPS( ps ) ;
	}

	public boolean isMultipleSpan( double mark, double span ) {
		return Math.isLim0( mark-(int) ( mark/span )*span ) ;
	}

	private int baseline() {
		if ( getBaseline().equals( AV_NONE ) )
			return 0 ;
		if ( getBaseline().equals( AV_LINE ) )
			return 1 ;
		if ( getBaseline().equals( AV_RAIL ) )
			return 2 ;
		return -1 ;
	}

	private PostscriptEmitter annotation( astrolabe.model.AnnotationStraight peer ) {
		AnnotationStraight annotation ;

		annotation = new AnnotationStraight() ;
		peer.copyValues( annotation ) ;

		return annotation ;
	}

	private PostscriptEmitter annotation( astrolabe.model.AnnotationCurved peer ) {
		AnnotationCurved annotation ;

		annotation = new AnnotationCurved() ;
		peer.copyValues( annotation ) ;

		return annotation ;
	}
}
