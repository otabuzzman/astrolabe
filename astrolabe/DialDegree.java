
package astrolabe;

import java.util.Collections;
import java.util.List;
import java.util.prefs.Preferences;

import caa.CAACoordinateTransformation;

@SuppressWarnings("serial")
public class DialDegree extends astrolabe.model.DialDegree implements PostscriptEmitter {

	private class None implements PostscriptEmitter {

		private final static double DEFAULT_SPACE		= 1 ;
		private final static double DEFAULT_THICKNESS	= 0 ;

		private double space ;
		private double thickness ;

		public None() {
			Preferences node ;

			node = Configuration.getClassNode(
					getClass().getEnclosingClass(), getName(), ApplicationConstant.PN_DIAL_BASELINE+"/"+getBaseline() ) ;

			space = Configuration.getValue( node,
					ApplicationConstant.PK_DIAL_SPACE, None.DEFAULT_SPACE ) ;
			thickness = Configuration.getValue( node,
					ApplicationConstant.PK_DIAL_THICKNESS, None.DEFAULT_THICKNESS ) ;
		}

		public void headPS(AstrolabePostscriptStream ps) {
		}

		public void emitPS(AstrolabePostscriptStream ps) {
		}

		public void tailPS(AstrolabePostscriptStream ps) {
		}
	}

	private class Line implements PostscriptEmitter {

		private final static double DEFAULT_SPACE		= 1 ;
		private final static double DEFAULT_THICKNESS	= 1.2 ;

		private double space ;
		private double thickness ;

		public Line() {
			Preferences node ;

			node = Configuration.getClassNode(
					getClass().getEnclosingClass(), getName(), ApplicationConstant.PN_DIAL_BASELINE+"/"+getBaseline() ) ;
			space = Configuration.getValue( node,
					ApplicationConstant.PK_DIAL_SPACE, DEFAULT_SPACE ) ;
			thickness = Configuration.getValue( node,
					ApplicationConstant.PK_DIAL_THICKNESS, DEFAULT_THICKNESS ) ;
		}

		public void headPS(AstrolabePostscriptStream ps) {
			ps.operator.setlinewidth( thickness ) ;
		}

		public void emitPS(AstrolabePostscriptStream ps) {
			List<double[]> v ;
			double ma, mo, o, span ;
			int m ;

			v = new java.util.Vector<double[]>() ;

			span = getGraduationSpan().getValue()*unit ;
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
		}

		public void tailPS(AstrolabePostscriptStream ps) {
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
			Preferences node ;

			node = Configuration.getClassNode(
					getClass().getEnclosingClass(), getName(), ApplicationConstant.PN_DIAL_BASELINE+"/"+getBaseline() ) ;

			space = Configuration.getValue( node,
					ApplicationConstant.PK_DIAL_SPACE, DEFAULT_SPACE ) ;
			thickness = Configuration.getValue( node,
					ApplicationConstant.PK_DIAL_THICKNESS, DEFAULT_THICKNESS ) ;
			linewidth = Configuration.getValue( node,
					ApplicationConstant.PK_DIAL_LINEWIDTH, DEFAULT_LINEWIDTH ) ;

		}

		public void headPS(AstrolabePostscriptStream ps) {
			ps.operator.setlinewidth( linewidth ) ;
		}

		public void emitPS(AstrolabePostscriptStream ps) {
			List<double[]> vDFw = null, vDRv = null, rvDRv ;
			double ma, mo, o, s, span ;
			int m = 0 ;

			span = ( getGraduationSpan().getValue()/getGraduationSpan().getDivision() )*unit ;
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
					ps.push( ApplicationConstant.PS_PROLOG_GDRAW ) ;
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
					ps.push( ApplicationConstant.PS_PROLOG_GDRAW ) ;
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
					ps.push( ApplicationConstant.PS_PROLOG_GDRAW ) ;
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
			}
		}

		public void tailPS(AstrolabePostscriptStream ps) {
		}
	}

	private final static double DEFAULT_RISE			= 3.2 ;

	private Baseline	baseline ;
	private double		unit ;

	public DialDegree( Baseline baseline ) {
		this( baseline, 1 ) ;
	}

	public DialDegree( Baseline baseline, double unit ) {
		this.baseline	= baseline ;	
		this.unit		= unit ;
	}

	public void register( int index ) {
		double a ;
		String key ;
		MessageCatalog m ;

		a = baseline.scaleMarkNth( index, getGraduationSpan().getValue()*unit ) ;

		m = new MessageCatalog( ApplicationConstant.GC_APPLICATION ) ;

		key = m.message( ApplicationConstant.LK_DIAL_DEGREE ) ;
		AstrolabeRegistry.registerDMS( key, a ) ;
		key = m.message( ApplicationConstant.LK_DIAL_HOUR ) ;
		AstrolabeRegistry.registerHMS( key, a ) ;
		key = m.message( ApplicationConstant.LK_DIAL_AZIMUTHTIME ) ;
		AstrolabeRegistry.registerHMS( key, CAACoordinateTransformation.MapTo0To360Range( a+180/*12h*/ ) ) ;
	}

	public void headPS( AstrolabePostscriptStream ps ) {
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

	public void emitPS( AstrolabePostscriptStream ps ) {
		List<double[]> v ;
		double space, thickness ;
		double xy[], a, o, s, rise ;
		double mo, b[], t[] ;
		PostscriptEmitter g ;

		switch ( baseline() ) {
		case 0:
			None none = new None() ;
			space = none.space ;
			thickness = none.thickness ;

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

		a = baseline.scaleMarkNth( 0, 1 ) ;
		o = baseline.scaleMarkNth( -1, getGraduationSpan().getValue()*unit ) ;

		for ( int m=0 ; ; m++ ) {
			mo = baseline.scaleMarkNth( m, getGraduationSpan().getValue()*unit ) ;

			register( m ) ;

			b = baseline.project( mo, getReflect()?-( space+thickness ):space+thickness ) ;
			t = baseline.tangent( mo ) ;
			if ( getReflect() ) {
				t[0] = -t[0] ;
				t[1] = -t[1] ;
			}

			ps.operator.gsave() ;

			g = new GraduationSpan( b, t ) ;
			getGraduationSpan().setupCompanion( g ) ;
			if ( getGraduationHalf() != null ) {
				if ( isMultipleSpan( mo, getGraduationHalf().getValue() ) ) {
					g = new GraduationHalf( b, t ) ;
					getGraduationHalf().setupCompanion( g ) ;
				}
			}
			if ( getGraduationFull() != null ) {
				if ( isMultipleSpan( mo, getGraduationFull().getValue() ) ) {
					g = new GraduationFull( b, t ) ;
					getGraduationFull().setupCompanion( g ) ;
				}
			}
			g.headPS( ps ) ;
			g.emitPS( ps ) ;
			g.tailPS( ps ) ;

			ps.operator.grestore() ;

			if ( mo==o )
				break ;
		}

		rise = Configuration.getValue(
				Configuration.getClassNode( this, getName(), null ),
				ApplicationConstant.PK_DIAL_RISE, DEFAULT_RISE ) ;

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
		ps.push( ApplicationConstant.PS_PROLOG_GDRAW ) ;

		if ( getAnnotation() != null ) {
			PostscriptEmitter annotation ;

			for ( int i=0 ; i<getAnnotationCount() ; i++ ) {
				ps.operator.gsave() ;

				annotation = AstrolabeFactory.companionOf( getAnnotation( i ) ) ;
				annotation.headPS( ps ) ;
				annotation.emitPS( ps ) ;
				annotation.tailPS( ps ) ;

				ps.operator.grestore() ;
			}
		}
	}

	public void tailPS( AstrolabePostscriptStream ps ) {
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
		return Math.isLim0( mark-(int) ( mark/( span*unit ) )*span*unit ) ;
	}

	private int baseline() {
		if ( getBaseline().equals( ApplicationConstant.AV_DIAL_NONE ) )
			return 0 ;
		if ( getBaseline().equals( ApplicationConstant.AV_DIAL_LINE ) )
			return 0 ;
		return 2 ; // ApplicationConstant.AV_DIAL_RAIL
	}
}
