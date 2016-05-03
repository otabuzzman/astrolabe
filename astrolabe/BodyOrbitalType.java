
package astrolabe;

import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;

@SuppressWarnings("serial")
abstract public class BodyOrbitalType extends astrolabe.model.BodyOrbitalType implements PostscriptEmitter, Baseline {

	// configuration key (CK_)
	private final static String CK_FADE				= "fade" ;

	private final static String CK_INTERVAL			= "interval" ;
	private static final String CK_DISTANCE			= "distance" ;
	private final static String CK_SEGMIN			= "segmin" ;

	private final static String CK_HALO				= "halo" ;
	private final static String CK_HALOMIN			= "halomin" ;
	private final static String CK_HALOMAX			= "halomax" ;

	private final static double DEFAULT_FADE		= 0 ;

	private final static double DEFAULT_INTERVAL	= 1 ;
	private static final double DEFAULT_DISTANCE	= 0 ;
	private final static int DEFAULT_SEGMIN			= 3 ;

	private final static double DEFAULT_HALO		= 4 ;
	private final static double DEFAULT_HALOMIN		= .08 ;
	private final static double DEFAULT_HALOMAX		= .4 ;

	private Projector projector ;

	private Epoch epoch ;
	private double interval ;

	public BodyOrbitalType( Converter converter, Projector projector ) {
		this.projector = projector ;

		epoch = (Epoch) Registry.retrieve( Epoch.class.getName() ) ;
		interval = Configuration.getValue( this, CK_INTERVAL, DEFAULT_INTERVAL ) ;
	}

	protected double getEpochAlpha() {
		if ( getEpoch() != null ) {
			epoch = new Epoch() ;
			getEpoch().copyValues( epoch ) ;
		} else if ( epoch == null )
			epoch = new Epoch() ;

		return epoch.alpha() ;
	}

	protected double getEpochOmega() {
		if ( getEpoch() != null ) {
			epoch = new Epoch() ;
			getEpoch().copyValues( epoch ) ;
		} else if ( epoch == null )
			epoch = new Epoch() ;

		return epoch.omega() ;
	}

	public Vector posVecOfScaleMarkVal( double jd ) {
		return new Vector( projector.project( jdToEquatorial( jd ), false ) ) ;
	}

	public double valOfScaleMarkN( int mark, double span ) {
		return new LinearScale( span, new double[] { getEpochAlpha(), getEpochOmega() } ).markN( mark ) ;
	}

	public Coordinate[] list( double jdA, double jdO ) {
		List<Coordinate> list ;
		double distance ;

		list = new java.util.Vector<Coordinate>() ;

		for ( double jd=jdA ; jd<jdO ; jd=jd+interval )
			list.add( posVecOfScaleMarkVal( jd ) ) ;
		list.add( posVecOfScaleMarkVal( jdO ) ) ;

		distance = Configuration.getValue( this, CK_DISTANCE, DEFAULT_DISTANCE ) ;
		if ( distance>0 && list.size()>2 )
			return DouglasPeuckerSimplifier.simplify( new GeometryFactory().createLineString( list.toArray( new com.vividsolutions.jts.geom.Coordinate[0] ) ), distance ).getCoordinates() ;
		return list.toArray( new Coordinate[0] ) ;
	}

	abstract public Coordinate jdToEquatorial( double jd ) ;

	public void headPS( ApplicationPostscriptStream ps ) {
		String gstate ;

		if ( ( gstate = Configuration.getValue( this, getNature(), null ) ) == null )
			return ;
		ps.script( gstate ) ;
	}

	public void emitPS( ApplicationPostscriptStream ps ) {
		Configuration conf ;
		int segmin ;
		FieldOfView fov ;
		Geometry gov, cut, tmp ;
		ChartPage page ;
		com.vividsolutions.jts.geom.Coordinate[] ccrc, ccut ;
		astrolabe.model.Annotation annotation ;
		PostscriptEmitter emitter ;

		conf = new Configuration( this ) ;
		segmin = conf.getValue( CK_SEGMIN, DEFAULT_SEGMIN ) ;

		fov = (FieldOfView) Registry.retrieve( FieldOfView.class.getName() ) ;
		if ( fov != null && fov.isClosed() )
			gov = fov.makeGeometry() ;
		else {
			page = (ChartPage) Registry.retrieve( ChartPage.class.getName() ) ;
			if ( page != null )
				gov = FieldOfView.makeGeometry( page.getViewRectangle(), true ) ;
			else
				gov = null ;
		}

		ccrc = list( getEpochAlpha(), getEpochOmega() ) ;

		if ( gov == null )
			cut = new GeometryFactory().createLineString( ccrc ) ;
		else {
			tmp = new GeometryFactory().createLineString( ccrc ) ;
			if ( ! tmp.isSimple() )
				return ;
			if ( ! gov.intersects( tmp ) )
				return ;
			cut = gov.intersection( tmp ) ;
		}

		for ( int i=0 ; cut.getNumGeometries()>i ; i++ ) {
			ccut = cut.getGeometryN( i ).getCoordinates() ;

			if ( segmin>ccut.length )
				continue ;

			ps.op( "gsave" ) ;

			ps.array( true ) ;
			for ( Coordinate xy : ccut ) {
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
			ps.push( 1 ) ;
			ps.op( "setlinecap" ) ;
			ps.push( conf.getValue( CK_FADE, DEFAULT_FADE ) ) ;
			ps.op( "currenthsbcolor" ) ;
			ps.op( "exch" ) ;
			ps.op( "pop" ) ;
			ps.op( "exch" ) ;
			ps.op( "pop" ) ;
			ps.op( "sub" ) ;
			ps.push( ccrc.length-1 ) ;
			ps.op( "div" ) ;
			ps.op( "hfade" ) ;
			ps.op( "grestore" ) ;

			if ( getAnnotation() != null ) {
				for ( int k=0 ; k<getAnnotationCount() ; k++ ) {
					annotation = getAnnotation( k ) ;

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

			ps.op( "grestore" ) ;
		}

		if ( getDialDay() != null ) {
			emitter = new DialDay( this ) ;
			getDialDay().copyValues( emitter ) ;

			ps.op( "gsave" ) ;

			emitter.headPS( ps ) ;
			emitter.emitPS( ps ) ;
			emitter.tailPS( ps ) ;

			ps.op( "grestore" ) ;
		}
	}

	public void tailPS( ApplicationPostscriptStream ps ) {
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
