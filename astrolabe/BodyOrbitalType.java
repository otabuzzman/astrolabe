
package astrolabe;

import java.util.List;

import caa.CAADate;

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
	private final static String CK_HALO				= "halo" ;
	private final static String CK_HALOMIN			= "halomin" ;
	private final static String CK_HALOMAX			= "halomax" ;

	private final static double DEFAULT_FADE		= 0 ;

	private final static double DEFAULT_INTERVAL	= 1 ;
	private static final double DEFAULT_DISTANCE	= 0 ;
	private final static double DEFAULT_HALO		= 4 ;
	private final static double DEFAULT_HALOMIN		= .08 ;
	private final static double DEFAULT_HALOMAX		= .4 ;

	private Projector projector ;

	private double epoch ;

	public BodyOrbitalType( Converter converter, Projector projector ) {
		Double Epoch ;

		this.projector = projector ;

		Epoch = (Double) Registry.retrieve( Epoch.class.getName() ) ;
		if ( Epoch == null )
			epoch = astrolabe.Epoch.defoult() ;
		epoch = Epoch.doubleValue() ;
	}

	public double[] epoch() {
		double epochlo ;
		double jdAy, jdOy ;
		CAADate epoch ;
		long year ;

		epoch = new CAADate() ;
		epoch.Set( this.epoch, true ) ;

		year = epoch.Year() ;
		epoch.Set( year, 1, 1, 0, 0, 0, true ) ;
		jdAy = epoch.Julian() ;
		epoch.Set( year, 12, 31, 0, 0, 0, true ) ;
		jdOy = epoch.Julian() ;

		if ( getEpoch() != null ) {
			epochlo = valueOf( getEpoch() ) ;
			epoch.Set( epochlo, true ) ;

			year = epoch.Year() ;
			epoch.Set( year, 1, 1, 0, 0, 0, true ) ;
			jdAy = epoch.Julian() ;
			epoch.Set( year, 12, 31, 0, 0, 0, true ) ;
			jdOy = epoch.Julian() ;

			epoch.Set( epochlo, true ) ;
			if ( getEpoch().getA() != null ) {
				jdAy = valueOf( getEpoch().getA() ) ;
				jdOy = epoch.Julian() ;
			}
			if ( getEpoch().getO() != null ) {
				if ( getEpoch().getA() == null )
					jdAy = epoch.Julian() ;
				jdOy = valueOf( getEpoch().getO() ) ;
			}
		}

		epoch.delete() ;

		return new double[] { jdAy, jdOy } ;
	}

	public Coordinate positionOfScaleMarkValue( double jd, double shift ) {
		Coordinate eq, xy ;
		Vector v, t ;

		eq = jdToEquatorial( jd ) ;
		xy = projector.project( eq, false ) ;
		v = new Vector( xy ) ;

		if ( shift != 0 ) {
			xy = directionOfScaleMarkValue( jd ) ;
			t = new Vector( xy ) ;
			t.apply( new double[] { 0, -1, 0, 1, 0, 0, 0, 0, 1 } ) ; // rotate 90 degrees counter clockwise
			t.scale( shift ) ;
			v.add( t ) ;
		}

		return new Coordinate( v.x, v.y ) ;
	}

	public Coordinate directionOfScaleMarkValue( double jd ) {
		Coordinate eq, xy ;
		Vector v, t ;

		eq = jdToEquatorial( jd+1./86400 ) ;
		xy = projector.project( eq, false ) ;
		v = new Vector( xy ) ;
		eq = jdToEquatorial( jd ) ;
		xy = projector.project( eq, false ) ;
		t = new Vector( xy ) ;

		v.sub( t ) ;

		return new Coordinate( v.x, v.y ) ;
	}

	public double valueOfScaleMarkN( int mark, double span ) {
		return new LinearScale( span, epoch() ).markN( mark ) ;
	}

	public Coordinate[] list( final List<Double> listjd, double jdA, double jdO, double shift ) {
		List<Coordinate> listxy ;
		double interval ;
		double d, e, g, dist ;

		interval = Configuration.getValue( this, CK_INTERVAL, DEFAULT_INTERVAL ) ;

		listxy = new java.util.Vector<Coordinate>() ;

		listxy.add( positionOfScaleMarkValue( jdA, shift ) ) ;
		if ( listjd != null )
			listjd.add( jdA ) ;

		d = jdO-jdA ;
		e = d-(int) ( d/interval )*interval ;
		g = ( Math.isLim0( e )?interval:e )/2 ;

		for ( double jd=jdA+g ; jd<jdO ; jd=jd+interval ) {
			listxy.add( positionOfScaleMarkValue( jd, shift ) ) ;
			if ( listjd != null )
				listjd.add( jd ) ;
		}

		listxy.add( positionOfScaleMarkValue( jdO, shift ) ) ;
		if ( listjd != null )
			listjd.add( jdO ) ;

		dist = Configuration.getValue( this, CK_DISTANCE, DEFAULT_DISTANCE ) ;
		if ( dist>0 && listxy.size()>2 )
			return DouglasPeuckerSimplifier.simplify( new GeometryFactory().createLineString( listxy.toArray( new Coordinate[0] ) ), dist ).getCoordinates() ;
		else
			return listxy.toArray( new Coordinate[0] ) ;
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
		ListCutter cutter ;
		FieldOfView fov ;
		Geometry gov ;
		ChartPage page ;
		List<int[]> listid ;
		List<Double> listjd ;
		Coordinate[] list ;
		double epoch[], jdAe, jdOe ;
		astrolabe.model.Annotation annotation ;
		PostscriptEmitter emitter ;

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

		epoch = epoch() ;
		listjd = new java.util.Vector<Double>() ;
		listid = new java.util.Vector<int[]>() ;
		list = list( listjd, epoch[0], epoch[1], 0 ) ;

		if ( gov == null ) {
			listid.add( new int[] { 0, list.length-1 } ) ;
		} else {
			cutter = new ListCutter( list, gov ) ;
			cutter.segmentsInterior( listid ) ;
		}

		for ( int[] jdid : listid ) {
			jdAe = listjd.get( jdid[0] ) ;
			jdOe = listjd.get( jdid[1] ) ;

			ps.op( "gsave" ) ;

			list = list( null, jdAe, jdOe, 0 ) ;
			ps.array( true ) ;
			for ( Coordinate xy : list ) {
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
			ps.push( 1 ) ;
			ps.op( "setlinecap" ) ;
			ps.push( conf.getValue( CK_FADE, DEFAULT_FADE ) ) ;
			ps.op( "currenthsbcolor" ) ;
			ps.op( "exch" ) ;
			ps.op( "pop" ) ;
			ps.op( "exch" ) ;
			ps.op( "pop" ) ;
			ps.op( "sub" ) ;
			ps.push( list.length-1 ) ;
			ps.op( "div" ) ;
			ps.op( "hfade" ) ;
			ps.op( "grestore" ) ;

			if ( getDialDay() != null ) {
				PostscriptEmitter dial ;

				dial = new DialDay( this ) ;
				getDialDay().copyValues( dial ) ;

				ps.op( "gsave" ) ;

				dial.headPS( ps ) ;
				dial.emitPS( ps ) ;
				dial.tailPS( ps ) ;

				ps.op( "grestore" ) ;
			}

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
