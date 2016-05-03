
package astrolabe;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

@SuppressWarnings("serial")
public class DialDeg extends astrolabe.model.DialDeg implements PostscriptEmitter {

	// configuration key (CK_), node (CN_)
	private final static String CK_FADE			= "fade" ;

	private final static double DEFAULT_FADE	= 0 ;

	private final static String CK_RISE			= "rise" ;

	private final static double DEFAULT_RISE	= 3.2 ;

	private final static String CK_HALO			= "halo" ;
	private final static String CK_HALOMIN		= "halomin" ;
	private final static String CK_HALOMAX		= "halomax" ;

	private final static double DEFAULT_HALO	= 4 ;
	private final static double DEFAULT_HALOMIN	= .08 ;
	private final static double DEFAULT_HALOMAX	= .4 ;

	private final static String CN_BASELINE		= "baseline" ;
	private final static String CK_SPACE		= "space" ;
	private final static String CK_THICKNESS	= "thickness" ;
	private final static String CK_LINEWIDTH	= "linewidth" ;

	// qualifier key (QK_)
	private final static String QK_ANGLE		= "angle" ;

	private Baseline baseline ;

	private Geometry fov ;
	private Geometry bas ;
	private Geometry seg ;

	private double halo ;
	private double halomin ;
	private double halomax ;

	private final static double[] m90 = new double[] { 0, -1, 0, 1, 0, 0, 0, 0, 1 } ;
	private final static double[] m90c = new double[] { 0, 1, 0, -1, 0, 0, 0, 0, 1 } ;

	// attribute value (AV_)
	private final static String AV_NONE = "none" ;
	private final static String AV_LINE = "line" ;
	private final static String AV_RAIL = "rail" ;

	private class None implements PostscriptEmitter {

		private final static double DEFAULT_SPACE		= 1 ;

		private double space ;

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
			Coordinate[] bot, nrm ;
			double span, a, o ;
			int j ;

			span = getScaleline()[0].getValue() ;
			a = baseline.valOfScaleMarkN( 0, span ) ;
			o = baseline.valOfScaleMarkN( -1, span ) ;

			bot = baseline.list( a, o ) ;
			nrm = astrolabe.Coordinate.cloneAll( bot ) ;
			astrolabe.Coordinate.parallelShift( nrm, getReflect()?-1:1 ) ;
			for ( j=0 ; nrm.length>j ; j++ )
				nrm[j].setCoordinate( new Vector( nrm[j] ).sub( bot[j] ) ) ;
			Coordinate t02 = baseline.posVecOfScaleMarkVal( o ) ;
			Coordinate t03 = baseline.posVecOfScaleMarkVal( o+1./getDivision() ) ;
			Vector t04 = new Vector( t03 ).sub( t02 ).scale( 1 ).apply( m90 ) ;
			nrm[j-1].setCoordinate( t04 ) ;

			if ( getCircle() != null && ! updateBaselinePosNrm( bot, nrm ) )
				return ;

			astrolabe.Coordinate.parallelShift( bot, getReflect()?-space:space ) ;

			ps.array( true ) ;
			for ( Coordinate xy : bot ) {
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
			ps.push( halo ) ; 
			ps.op( "mul" ) ;
			ps.push( halomin ) ; 
			ps.op( "max" ) ;
			ps.push( halomax ) ; 
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

			ps.op( "stroke" ) ;
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
			Coordinate[] bot, top, nrm ;
			double m90[], span, a, o, s ;
			boolean baseattr ;
			int n, j ;

			m90 = new double[] { 0, -1, 1, 0 } ;

			baseattr = getCircle() != null ;

			span = getScaleline()[0].getValue()/getDivision() ;
			a = baseline.valOfScaleMarkN( 0, span ) ;
			o = baseline.valOfScaleMarkN( -1, span ) ;
			n = (int) ( ( o-a )/span ) ;

			for ( int i=0 ; n>i ; i++ ) {
				a = baseline.valOfScaleMarkN( i, span ) ;
				o = baseline.valOfScaleMarkN( i+1, span ) ;

				bot = baseline.list( a, o ) ;

				nrm = astrolabe.Coordinate.cloneAll( bot ) ;
				astrolabe.Coordinate.parallelShift( nrm, getReflect()?-1:1 ) ;
				for ( j=0 ; nrm.length>j ; j++ )
					nrm[j].setCoordinate( new Vector( nrm[j] ).sub( bot[j] ) ) ;
				Coordinate t02 = baseline.posVecOfScaleMarkVal( o ) ;
				Coordinate t03 = baseline.posVecOfScaleMarkVal( o+1./getDivision() ) ;
				Vector t04 = new Vector( t03 ).sub( t02 ).scale( 1 ).apply( m90 ) ;
				nrm[j-1].setCoordinate( t04 ) ;

				if ( baseattr && ! updateBaselinePosNrm( bot, nrm ) )
					continue ;

				top = astrolabe.Coordinate.cloneAll( bot ) ;

				s = i%2==0?space:space+linewidth/2 ;
				s = getReflect()?-s:s ;			
				for ( int k=0 ; bot.length>k ; k++ )
					bot[k].setCoordinate( new Vector( bot[k] ).add( new Vector( nrm[k] ).scale( s ) ) ) ;

				s = space+( i%2==0?thickness:thickness-linewidth/2 ) ;
				s = getReflect()?-s:s ;
				for ( int k=0 ; bot.length>k ; k++ )
					top[k].setCoordinate( new Vector( top[k] ).add( new Vector( nrm[k] ).scale( s ) ) ) ;

				// remove when list() = Geometry
				Geometry t00 = new GeometryFactory().createLineString( bot ) ;
				Geometry t01 = new GeometryFactory().createLineString( top ) ;
				if ( fov != null && ! fov.contains( t00.union( t01 ) ) )
					continue ;

				ps.array( true ) ;
				for ( int k=0 ; bot.length>k ; k++ ) {
					ps.push( bot[k].x ) ;
					ps.push( bot[k].y ) ;
				}
				for ( int k=top.length ; k>0 ; k-- ) {
					ps.push( top[k-1].x ) ;
					ps.push( top[k-1].y ) ;
				}
				ps.array( false ) ;

				ps.op( "newpath" ) ;
				ps.op( "gdraw" ) ;

				// halo stroke
				ps.op( "currentlinewidth" ) ;

				ps.op( "dup" ) ;
				ps.push( 100 ) ;
				ps.op( "div" ) ;
				ps.push( halo ) ; 
				ps.op( "mul" ) ;
				ps.push( halomin ) ; 
				ps.op( "max" ) ;
				ps.push( halomax ) ; 
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

				ps.op( "closepath" ) ;
				if ( i%2 == 0 ) {
					ps.op( "fill" ) ;
				} else // subunit unfilled
					ps.op( "stroke" ) ;
			}
		}

		public void tailPS( ApplicationPostscriptStream ps ) {
		}
	}

	public DialDeg( Baseline baseline ) {
		this.baseline = baseline ;	
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
		Configuration conf ;
		double space, thickness, rise, shift ;
		double span, a, o, v, s ;
		Vector pos, nrm, pt, t1 ;
		boolean baseattr ;
		astrolabe.model.Annotation annotation ;
		PostscriptEmitter emitter ;
		Scaleline scaleline ;

		conf = new Configuration( this ) ;
		halo = conf.getValue( CK_HALO, DEFAULT_HALO ) ;
		halomin = conf.getValue( CK_HALOMIN, DEFAULT_HALOMIN ) ;
		halomax = conf.getValue( CK_HALOMAX, DEFAULT_HALOMAX ) ;

		bas = seg = makeBaselineGeometry() ;
		fov = findFieldOfView() ;
		if ( fov != null )
			seg = fov.intersection( bas ) ;

		if ( seg.getNumPoints() == 0 )
			return ; // bas not covered by fov

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

		baseattr = getCircle() != null ;

		span = getScaleline()[0].getValue() ;
		a = baseline.valOfScaleMarkN( 0, span ) ;
		o = baseline.valOfScaleMarkN( -1, span ) ;
		int n = (int) ( ( o-a )/span ) ;

		ps.push( conf.getValue( CK_FADE, DEFAULT_FADE ) ) ;
		ps.op( "currenthsbcolor" ) ;
		ps.op( "exch" ) ;
		ps.op( "pop" ) ;
		ps.op( "exch" ) ;
		ps.op( "pop" ) ;
		ps.op( "sub" ) ;
		ps.push( n++ ) ;
		ps.op( "div" ) ;

		for ( int m=0 ; n>m ; m++ ) {
			v = baseline.valOfScaleMarkN( m, span ) ;
			pos = baseline.posVecOfScaleMarkVal( v ) ;

			pt = baseline.posVecOfScaleMarkVal( v+1./getDivision() ) ;

			t1 = new Vector( pt ).sub( pos ) ;
			nrm = new Vector( t1 ).apply( m90 ).scale( 1 ) ;

			if ( baseattr && ! updateBaselinePosNrm( pos, nrm ) )
				continue ;

			if ( getReflect() )
				nrm.neg() ;

			s = space+thickness ;
			if ( s != 0 )
				pos.add( nrm.scale( s ) ) ;

			register( v ) ;
			ps.op( "gsave" ) ;

			ps.op( "currenthsbcolor" ) ;
			ps.push( 3 ) ;
			ps.op( "index" ) ;
			ps.op( "add" ) ;
			ps.op( "sethsbcolor" ) ;

			for ( int j=getScalelineCount()-1 ; j>-1 ; j-- ) {
				if ( ! isMultipleSpan( v, getScaleline()[j].getValue() ) )
					continue ;

				scaleline = new Scaleline( pos, nrm ) ;
				getScaleline()[j].copyValues( scaleline ) ;

				if ( fov != null && ! fov.contains( scaleline.list() ) )
					break ;

				scaleline.headPS( ps ) ;
				scaleline.emitPS( ps ) ;
				scaleline.tailPS( ps ) ;

				break ;
			}

			ps.op( "grestore" ) ;
			degister() ;
		}

		ps.op( "pop" ) ;

		rise = conf.getValue( CK_RISE, DEFAULT_RISE ) ;

		shift = ( ( space+thickness )+rise ) ;
		Coordinate[] t00 = baseline.list( a, o ) ;
		astrolabe.Coordinate.parallelShift( t00, getReflect()?-shift:shift ) ;
		ps.array( true ) ;
		for ( Coordinate xy : t00 ) {
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

	private Geometry findFieldOfView() {
		FieldOfView fov ;
		ChartPage page ;

		fov = (FieldOfView) Registry.retrieve( FieldOfView.class.getName() ) ;
		if ( fov != null && fov.isClosed() )
			return fov.makeGeometry() ;
		else {
			page = (ChartPage) Registry.retrieve( ChartPage.class.getName() ) ;
			if ( page != null )
				return FieldOfView.makeGeometry( page.getViewRectangle(), true ) ;
			return null ;
		}
	}

	private Geometry makeBaselineGeometry() {
		String name ;
		double a, o, s ;
		Baseline circle ;

		s = 1./( getDivision()*2 ) ;

		name = getCircle() ;
		if ( name == null ) {
			a = baseline.valOfScaleMarkN( 0, s ) ;
			o = baseline.valOfScaleMarkN( -1, s ) ;
			return new GeometryFactory().createLineString( baseline.list( a, o ) ) ;
		}

		circle = (Baseline) Registry.retrieve( name ) ;
		if ( circle != null ) {
			a = circle.valOfScaleMarkN( 0, s ) ;
			o = circle.valOfScaleMarkN( -1, s ) ;
			return new GeometryFactory().createLineString( circle.list( a, o ) ) ;
		}
		return null ;
	}

	private boolean updateBaselinePosNrm( Coordinate[] pos, Coordinate[] nrm ) {
		for ( int i=0 ; pos.length>i ; i++ )
			if ( ! updateBaselinePosNrm( pos[i], nrm[i] ) )
				return false ;
		return true ;
	}

	private boolean updateBaselinePosNrm( Coordinate pos, Coordinate nrm ) {
		Vector pt, t1, wp, w ;
		Geometry prb, x, d ;

		wp = new Vector( nrm ).scale( 1000000 ) ;
		w = new Vector( pos ).add( wp ) ;

		prb = new GeometryFactory().createLineString(
				new Coordinate[] { (Coordinate) pos.clone(), w } ) ;
		x = bas.intersection( prb ) ;

		if ( x.getNumPoints() == 0 )
			return false ;

		pos.setCoordinate( x.getGeometryN( 0 ).getCoordinates()[0] ) ;

		d = bas.difference( prb ) ;

		Coordinate[] t00 = d.getGeometryN( 0 ).getCoordinates() ;
		if ( pos.equals2D( t00[0] ) )
			pt = new Vector( t00[1] ) ;
		else
			pt = new Vector( t00[t00.length-2] ) ;

		t1 = new Vector( pt ).sub( pos ) ;
		nrm.setCoordinate( new Vector( t1 ).apply( m90c ).scale( 1 ) ) ;

		return true ;
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
