
package astrolabe;

import java.util.Collections;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

@SuppressWarnings("serial")
public class DialDegree extends astrolabe.model.DialDegree implements PostscriptEmitter {

	private final static String CK_FADE			= "fade" ;

	private final static double DEFAULT_FADE	= 0 ;

	private class None implements PostscriptEmitter {

		private final static double DEFAULT_SPACE		= 1 ;

		private double space = 0 ;

		public void headPS(ApplicationPostscriptStream ps ) {
			Configuration conf ;
			String qual ;

			conf = new Configuration( getClass().getEnclosingClass() ) ;
			qual = CN_BASELINE+"/"+getBaseline() ;

			space = conf.getValue(
					qual+"/"+CK_SPACE, DEFAULT_SPACE ) ;
		}

		public void emitPS( ApplicationPostscriptStream ps ) {
		}

		public void tailPS( ApplicationPostscriptStream ps ) {
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

		public void headPS( ApplicationPostscriptStream ps ) {
			ps.push( thickness ) ;
			ps.op( "setlinewidth" ) ;
		}

		public void emitPS( ApplicationPostscriptStream ps ) {
			Configuration conf ;
			List<Coordinate> v ;
			double ma, mo, o, span ;
			int m ;

			v = new java.util.Vector<Coordinate>() ;

			span = getSpan() ;
			o = baseline.valueOfScaleMarkN( -1, span ) ;

			for ( m=0 ; ; m++ ) {
				ma = baseline.valueOfScaleMarkN( m, span ) ;
				mo = baseline.valueOfScaleMarkN( m+1, span ) ;

				for ( Coordinate xy : baseline.list( ma, mo, getReflect()?-space:space ) )
					v.add( xy ) ;

				if ( mo==o )
					break ;
			}

			ps.array( true ) ;
			for ( Coordinate xy : v ) {
				ps.push( xy.x ) ;
				ps.push( xy.y ) ;
			}
			ps.array( false ) ;

			ps.op( "newpath" ) ;
			ps.op( "gdraw" ) ;

			// halo stroke
			ps.op( "currentlinewidth" ) ;

			ps.op( "dup" ) ;
			ps.push( 100 ) ;
			ps.op( "div" ) ;
			conf = new Configuration( this ) ;
			ps.push( conf.getValue( CK_HALO, DEFAULT_HALO ) ) ; 
			ps.op( "mul" ) ;
			ps.push( conf.getValue( CK_HALOMIN, DEFAULT_HALOMIN ) ) ; 
			ps.op( "max" ) ;
			ps.push( conf.getValue( CK_HALOMAX, DEFAULT_HALOMAX ) ) ; 
			ps.op( "min" ) ;

			ps.push( 2 ) ;
			ps.op( "mul" ) ;
			ps.op( "add" ) ;
			ps.op( "gsave" ) ;
			ps.op( "setlinewidth" ) ;
			ps.push( 2 ) ;
			ps.op( "setlinecap" ) ;
			ps.push( 1 ) ;
			ps.op( "setgray" ) ;
			ps.op( "stroke" ) ;
			ps.op( "grestore" ) ;

			ps.op( "gsave" ) ;
			ps.op( "stroke" ) ;
			ps.op( "grestore" ) ;
		}

		public void tailPS( ApplicationPostscriptStream ps ) {
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

		public void headPS( ApplicationPostscriptStream ps ) {
			ps.push( linewidth ) ;
			ps.op( "setlinewidth" ) ;
		}

		public void emitPS( ApplicationPostscriptStream ps ) {
			Configuration conf ;
			List<Coordinate> vDFw, vDRv, rvDRv ;
			double ma, mo, o, s, span ;
			int m = 0 ;

			span = getSpan()/getGraduationSpan().getDivision() ;
			o = baseline.valueOfScaleMarkN( -1, span ) ;

			for ( ; ; m++ ) {
				ma = baseline.valueOfScaleMarkN( m, span ) ;
				mo = baseline.valueOfScaleMarkN( m+1, span ) ;

				s = m%2==0?space:space+linewidth/2 ;
				s = getReflect()?-s:s ;			
				vDFw = new java.util.Vector<Coordinate>() ;
				for ( Coordinate xy : baseline.list( ma, mo, s ) )
					vDFw.add( xy ) ;
				ps.array( true ) ;
				for ( Coordinate xy : vDFw ) {
					ps.push( xy.x ) ;
					ps.push( xy.y ) ;
				}
				ps.array( false ) ;

				ps.op( "newpath" ) ;
				ps.op( "gdraw" ) ;

				// halo stroke
				ps.op( "currentlinewidth" ) ;

				ps.op( "dup" ) ;
				ps.push( 100 ) ;
				ps.op( "div" ) ;
				conf = new Configuration( this ) ;
				ps.push( conf.getValue( CK_HALO, DEFAULT_HALO ) ) ; 
				ps.op( "mul" ) ;
				ps.push( conf.getValue( CK_HALOMIN, DEFAULT_HALOMIN ) ) ; 
				ps.op( "max" ) ;
				ps.push( conf.getValue( CK_HALOMAX, DEFAULT_HALOMAX ) ) ; 
				ps.op( "min" ) ;

				ps.push( 2 ) ;
				ps.op( "mul" ) ;
				ps.op( "add" ) ;
				ps.op( "gsave" ) ;
				ps.op( "setlinewidth" ) ;
				ps.push( 1 ) ;
				ps.op( "setgray" ) ;
				ps.op( "stroke" ) ;
				ps.op( "grestore" ) ;

				s = space+( m%2==0?thickness:thickness-linewidth/2 ) ;
				s = getReflect()?-s:s ;
				vDRv = new java.util.Vector<Coordinate>() ;
				for ( Coordinate xy : baseline.list( ma, mo, s ) )
					vDRv.add( xy ) ;
				ps.array( true ) ;
				for ( Coordinate xy : vDRv ) {
					ps.push( xy.x ) ;
					ps.push( xy.y ) ;
				}
				ps.array( false ) ;

				ps.op( "newpath" ) ;
				ps.op( "gdraw" ) ;

				// halo stroke
				ps.op( "currentlinewidth" ) ;

				ps.op( "dup" ) ;
				ps.push( 100 ) ;
				ps.op( "div" ) ;
				conf = new Configuration( this ) ;
				ps.push( conf.getValue( CK_HALO, DEFAULT_HALO ) ) ; 
				ps.op( "mul" ) ;
				ps.push( conf.getValue( CK_HALOMIN, DEFAULT_HALOMIN ) ) ; 
				ps.op( "max" ) ;
				ps.push( conf.getValue( CK_HALOMAX, DEFAULT_HALOMAX ) ) ; 
				ps.op( "min" ) ;

				ps.push( 2 ) ;
				ps.op( "mul" ) ;
				ps.op( "add" ) ;
				ps.op( "gsave" ) ;
				ps.op( "setlinewidth" ) ;
				ps.push( 1 ) ;
				ps.op( "setgray" ) ;
				ps.op( "stroke" ) ;
				ps.op( "grestore" ) ;

				rvDRv = new java.util.Vector<Coordinate>( vDRv ) ;
				Collections.reverse( rvDRv ) ;
				vDFw.addAll( rvDRv ) ;

				if ( m%2 == 0 ) { // subunit filled
					ps.array( true ) ;
					for ( Coordinate xy : vDFw ) {
						ps.push( xy.x ) ;
						ps.push( xy.y ) ;
					}
					ps.array( false ) ;

					ps.op( "newpath" ) ;
					ps.op( "gdraw" ) ;
					ps.op( "closepath" ) ;
					ps.op( "fill" ) ;
				} else { // subunit unfilled
					java.util.Vector<Coordinate> fw, rv ;

					fw = new java.util.Vector<Coordinate>( vDFw.subList( 0, vDFw.size()/2 ) ) ;
					rv = new java.util.Vector<Coordinate>( vDFw.subList( vDFw.size()/2, vDFw.size() ) ) ;

					ps.array( true ) ;
					for ( Coordinate xy : fw ) {
						ps.push( xy.x ) ;
						ps.push( xy.y ) ;
					}
					ps.array( false ) ;

					ps.op( "newpath" ) ;
					ps.op( "gdraw" ) ;
					ps.op( "gsave" ) ;
					ps.op( "stroke" ) ;
					ps.op( "grestore" ) ;
					ps.array( true ) ;
					for ( Coordinate xy : rv ) {
						ps.push( xy.x ) ;
						ps.push( xy.y ) ;
					}
					ps.array( false ) ;

					ps.op( "newpath" ) ;
					ps.op( "gdraw" ) ;
					ps.op( "gsave" ) ;
					ps.op( "stroke" ) ;
					ps.op( "grestore" ) ;
				}

				if ( mo == o )
					break ;
			}
			if ( m%2 == 1 ) {// close unfilled subunit
				Coordinate xy ;
				int i ;

				ps.op( "newpath" ) ;
				i = vDFw.size()/2 ;
				xy = vDFw.get( i ) ;
				ps.push( xy.x ) ;
				ps.push( xy.y ) ;
				xy = vDFw.get( i-1 ) ;
				ps.push( xy.x ) ;
				ps.push( xy.y ) ;
				ps.op( "moveto" ) ;
				ps.op( "lineto" ) ;

				// halo stroke
				ps.op( "currentlinewidth" ) ;

				ps.op( "dup" ) ;
				ps.push( 100 ) ;
				ps.op( "div" ) ;
				conf = new Configuration( this ) ;
				ps.push( conf.getValue( CK_HALO, DEFAULT_HALO ) ) ; 
				ps.op( "mul" ) ;
				ps.push( conf.getValue( CK_HALOMIN, DEFAULT_HALOMIN ) ) ; 
				ps.op( "max" ) ;
				ps.push( conf.getValue( CK_HALOMAX, DEFAULT_HALOMAX ) ) ; 
				ps.op( "min" ) ;

				ps.push( 2 ) ;
				ps.op( "mul" ) ;
				ps.op( "add" ) ;
				ps.op( "gsave" ) ;
				ps.op( "setlinewidth" ) ;
				ps.push( 2 ) ;
				ps.op( "setlinecap" ) ;
				ps.push( 1 ) ;
				ps.op( "setgray" ) ;
				ps.op( "stroke" ) ;
				ps.op( "grestore" ) ;

				ps.op( "gsave" ) ;
				ps.op( "stroke" ) ;
				ps.op( "grestore" ) ;
			}
		}

		public void tailPS( ApplicationPostscriptStream ps ) {
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
		double space, thickness ;
		double a, o, c, s, rise ;
		double mV, span, half, full ;
		java.util.Vector<Double> mL ;
		Coordinate b, t ;
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
		a = c = baseline.valueOfScaleMarkN( 0, span ) ;
		o = baseline.valueOfScaleMarkN( -1, span ) ;

		mL = new java.util.Vector<Double>() ;		
		for ( int mN=0 ; o>c ; mN++ ) {
			c = baseline.valueOfScaleMarkN( mN, span ) ;
			mL.add( c ) ;
		}

		ps.push( Configuration.getValue( this, CK_FADE, DEFAULT_FADE ) ) ;
		ps.op( "currenthsbcolor" ) ;
		ps.op( "exch" ) ;
		ps.op( "pop" ) ;
		ps.op( "exch" ) ;
		ps.op( "pop" ) ;
		ps.op( "sub" ) ;
		ps.push( mL.size()-1 ) ;
		ps.op( "div" ) ;

		for ( int mN=0 ; mN<mL.size(); mN++ ) {
			mV = mL.get( mN ) ;

			b = baseline.positionOfScaleMarkValue( mV, getReflect()?-( space+thickness ):space+thickness ) ;
			t = baseline.directionOfScaleMarkValue( mV ) ;
			if ( getReflect() ) {
				t.x = -t.x ;
				t.y = -t.y ;
			}

			ps.op( "currenthsbcolor" ) ;
			ps.push( 3 ) ;
			ps.op( "index" ) ;
			ps.op( "add" ) ;
			ps.op( "sethsbcolor" ) ;

			register( mV ) ;
			ps.op( "gsave" ) ;

			emitter = new GraduationSpan( b, t ) ;
			getGraduationSpan().copyValues( emitter ) ;
			if ( ( half = getHalf() )>0 )
				if ( isMultipleSpan( mV, half ) ) {
					emitter = new GraduationHalf( b, t ) ;
					getGraduationHalf().copyValues( emitter ) ;
				}
			if ( ( full = getFull() )>0 )
				if ( isMultipleSpan( mV, full ) ) {
					emitter = new GraduationFull( b, t ) ;
					getGraduationFull().copyValues( emitter ) ;
				}
			emitter.headPS( ps ) ;
			emitter.emitPS( ps ) ;
			emitter.tailPS( ps ) ;

			ps.op( "grestore" ) ;
			degister() ;
		}

		ps.op( "pop" ) ;

		rise = Configuration.getValue( this, CK_RISE, DEFAULT_RISE ) ;

		s = ( ( space+thickness )+rise ) ;
		ps.array( true ) ;
		for ( Coordinate xy : baseline.list( a, o, getReflect()?-s:s ) ) {
			ps.push( xy.x ) ;
			ps.push( xy.y ) ;
		}
		ps.array( false ) ;

		ps.op( "newpath" ) ;
		ps.op( "gdraw" ) ;

		if ( getAnnotation() != null ) {
			for ( int i=0 ; i<getAnnotationCount() ; i++ ) {
				annotation = getAnnotation( i ) ;

				if ( annotation.getAnnotationStraight() != null ) {
					emitter = annotation( annotation.getAnnotationStraight() ) ;
				} else { // annotation.getAnnotationCurved() != null
					emitter = annotation( annotation.getAnnotationCurved() ) ;
				}

				ps.op( "gsave" ) ;

				emitter.headPS( ps ) ;
				emitter.emitPS( ps ) ;
				emitter.tailPS( ps ) ;

				ps.op( "grestore" ) ;
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
