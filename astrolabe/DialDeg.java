
package astrolabe;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

@SuppressWarnings("serial")
public class DialDeg extends astrolabe.model.DialDeg implements PostscriptEmitter {

	// configuration key (CK_), node (CN_)
	private final static String CK_FADE			= "fade" ;

	private final static double DEFAULT_FADE	= 0 ;

	private final static String CK_SUPERSCRIPT		= "superscript" ;

	private final static double DEFAULT_SUPERSCRIPT	= 3.2 ;

	private final static String CK_HALO			= "halo" ;
	private final static String CK_HALOMIN		= "halomin" ;
	private final static String CK_HALOMAX		= "halomax" ;

	private final static double DEFAULT_HALO	= 4 ;
	private final static double DEFAULT_HALOMIN	= .08 ;
	private final static double DEFAULT_HALOMAX	= .4 ;

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

	private class ScalebaseNone extends astrolabe.model.ScalebaseNone implements PostscriptEmitter {

		private final static double DEFAULT_SPACE		= 1 ;

		private double space = Double.NaN ;

		public void headPS(ApplicationPostscriptStream ps ) {
		}

		public void emitPS( ApplicationPostscriptStream ps ) {
		}

		public void tailPS( ApplicationPostscriptStream ps ) {
		}

		public void init() {
			if ( ! Double.isNaN( space ) )
				return ;

			space = Configuration.getValue( this, CK_SPACE, DEFAULT_SPACE ) ;
		}
	}

	private class ScalebaseLine extends astrolabe.model.ScalebaseLine implements PostscriptEmitter {

		private final static double DEFAULT_SPACE		= 1 ;
		private final static double DEFAULT_THICKNESS	= 1.2 ;

		private double space		= Double.NaN ;
		private double thickness	= Double.NaN ;

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

			if ( getBaseline() != null && ! updateBaselinePosNrm( bot, nrm ) )
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

		public void init() {
			if ( ! Double.isNaN( space ) )
				return ;

			space = Configuration.getValue( this, CK_SPACE, DEFAULT_SPACE ) ;
			thickness = Configuration.getValue( this, CK_THICKNESS, DEFAULT_THICKNESS ) ;
		}
	}

	private class ScalebaseRail extends astrolabe.model.ScalebaseRail implements PostscriptEmitter {

		private final static double DEFAULT_SPACE		= 1 ;
		private final static double DEFAULT_THICKNESS	= 1.2 ;
		private final static double DEFAULT_LINEWIDTH	= .01 ;

		private double space		= Double.NaN ;
		private double thickness	= Double.NaN ;
		private double linewidth	= Double.NaN ;

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

			baseattr = getBaseline() != null ;

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

		public void init() {
			if ( ! Double.isNaN( space ) )
				return ;

			space = Configuration.getValue( this, CK_SPACE, DEFAULT_SPACE ) ;
			thickness = Configuration.getValue( this, CK_THICKNESS, DEFAULT_THICKNESS ) ;
			linewidth = Configuration.getValue( this, CK_LINEWIDTH, DEFAULT_LINEWIDTH ) ;
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
	}

	public void emitPS( ApplicationPostscriptStream ps ) {
		Configuration conf ;
		double shift, superscript ;
		double span, a, o, v ;
		Vector pos, nrm, pt, t1 ;
		boolean baseattr ;
		astrolabe.model.ScalebaseType peer ;
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

		if ( ( peer = getDialTypeChoice().getScalebaseNone() ) != null ) {
			ScalebaseNone scalebase ;

			scalebase = new ScalebaseNone() ;
			peer.copyValues( scalebase ) ;
			scalebase.init() ;

			scalebase.headPS( ps ) ;
			scalebase.emitPS( ps ) ;
			scalebase.tailPS( ps ) ;

			shift = scalebase.space ;			
		} else if ( ( peer = getDialTypeChoice().getScalebaseLine() ) != null ) {
			ScalebaseLine scalebase ;

			scalebase = new ScalebaseLine() ;
			peer.copyValues( scalebase ) ;
			scalebase.init() ;

			scalebase.headPS( ps ) ;
			scalebase.emitPS( ps ) ;
			scalebase.tailPS( ps ) ;

			shift = scalebase.space+scalebase.thickness ;			
		} else { // getDialTypeChoice().getScalebaseRail() != null
			ScalebaseRail scalebase ;

			scalebase = new ScalebaseRail() ;
			peer = getDialTypeChoice().getScalebaseRail() ;
			peer.copyValues( scalebase ) ;
			scalebase.init() ;

			scalebase.headPS( ps ) ;
			scalebase.emitPS( ps ) ;
			scalebase.tailPS( ps ) ;

			shift = scalebase.space+scalebase.thickness ;			
		}


		baseattr = getBaseline() != null ;

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

			nrm.scale( shift ) ;
			if ( getReflect() )
				pos.sub( nrm ) ;
			else
				pos.add( nrm ) ;

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

		superscript = conf.getValue( CK_SUPERSCRIPT, DEFAULT_SUPERSCRIPT ) ;

		double t00 = superscript+shift ;
		Coordinate[] t01 = baseline.list( a, o ) ;
		astrolabe.Coordinate.parallelShift( t01, getReflect()?-t00:t00 ) ;
		ps.array( true ) ;
		for ( Coordinate xy : t01 ) {
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

		name = getBaseline() ;
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
