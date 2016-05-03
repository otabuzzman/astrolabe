
package astrolabe;

import java.util.List;

import caa.CAADate;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;

@SuppressWarnings("serial")
abstract public class BodyOrbitalType extends astrolabe.model.BodyOrbitalType implements Cloneable, PostscriptEmitter, Baseline {

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

	private double epoch ;
	private double interval ;

	public BodyOrbitalType( Converter converter, Projector projector ) {
		Double Epoch ;

		this.projector = projector ;

		Epoch = (Double) Registry.retrieve( Epoch.class.getName() ) ;
		if ( Epoch == null )
			epoch = astrolabe.Epoch.defoult() ;
		epoch = Epoch.doubleValue() ;

		interval = Configuration.getValue( this, CK_INTERVAL, DEFAULT_INTERVAL ) ;
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

	public Coordinate[] list( double jdA, double jdO, double shift ) {
		List<Coordinate> listxy ;
		double distance ;


		listxy = new java.util.Vector<Coordinate>() ;

		for ( double jd=jdA ; jd<jdO ; jd=jd+interval )
			listxy.add( positionOfScaleMarkValue( jd, shift ) ) ;
		listxy.add( positionOfScaleMarkValue( jdO, shift ) ) ;

		distance = Configuration.getValue( this, CK_DISTANCE, DEFAULT_DISTANCE ) ;
		if ( distance>0 && listxy.size()>2 )
			return DouglasPeuckerSimplifier.simplify( new GeometryFactory().createLineString( listxy.toArray( new Coordinate[0] ) ), distance ).getCoordinates() ;
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
		int segmin ;
		BodyOrbitalType base ;
		astrolabe.model.JD jdA, jdO ;
		FieldOfView fov ;
		Geometry gov, cut, tmp ;
		ChartPage page ;
		Coordinate[] ccrc, ccut ;
		Coordinate c ;
		int j ;
		double epoch[] ;
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

		base = (BodyOrbitalType) clone() ;
		base.setEpoch( new astrolabe.model.Epoch() ) ;
		if ( getEpoch() == null ) {
			base.getEpoch().setJD( new astrolabe.model.JD() ) ;
			base.getEpoch().getJD().setValue( this.epoch ) ;
		} else {
			base.getEpoch().setCalendar( getEpoch().getCalendar() ) ;
			base.getEpoch().setJD( getEpoch().getJD() ) ;
		}

		base.getEpoch().setA( new astrolabe.model.A() ) ;
		base.getEpoch().getA().setJD( new astrolabe.model.JD() ) ;
		base.getEpoch().setO( new astrolabe.model.O() ) ;
		base.getEpoch().getO().setJD( new astrolabe.model.JD() ) ;

		jdA = base.getEpoch().getA().getJD() ;
		jdO = base.getEpoch().getO().getJD() ;

		epoch = epoch() ;
		ccrc = list( epoch[0], epoch[1], 0 ) ;

		if ( gov == null )
			cut = new GeometryFactory().createLineString( ccrc ) ;
		else {
			tmp = new GeometryFactory().createLineString( ccrc ) ;
			cut = gov.intersection( tmp ) ;
		}

		for ( int i=0 ; cut.getNumGeometries()>i ; i++ ) {
			ccut = cut.getGeometryN( i ).getCoordinates() ;

			if ( segmin>ccut.length )
				continue ;

			c = ccut[1] ;
			for ( j=0 ; ccrc.length>j ; j++ )
				if ( ccrc[j].compareTo( c ) == 0 )
					break ;
			jdA.setValue( epoch[0]+j*interval ) ;
			c = ccut[ccut.length-2] ;
			for ( j++ ; ccrc.length>j ; j++ )
				if ( ccrc[j].compareTo( c ) == 0 )
					break ;
			jdO.setValue( epoch[0]+j*interval ) ;

			ps.op( "gsave" ) ;

			ps.array( true ) ;
			for ( Coordinate xy : cut.getGeometryN( i ).getCoordinates() ) {
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

			if ( getDialDay() != null ) {
				PostscriptEmitter dial ;

				dial = new DialDay( base ) ;
				getDialDay().copyValues( dial ) ;

				ps.op( "gsave" ) ;

				dial.headPS( ps ) ;
				dial.emitPS( ps ) ;
				dial.tailPS( ps ) ;

				ps.op( "grestore" ) ;
			}

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
	}

	public Object clone() {
		try {
			return super.clone() ;
		} catch ( CloneNotSupportedException e ) {}
		return null ;
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
