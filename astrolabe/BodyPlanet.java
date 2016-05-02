
package astrolabe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.exolab.castor.xml.ValidationException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

import caa.CAADate;

@SuppressWarnings("serial")
public class BodyPlanet extends astrolabe.model.BodyPlanet implements PostscriptEmitter, Baseline {

	// configuration key (CK_)
	private final static String CK_INTERVAL			= "interval" ;
	private final static String CK_STRETCH			= "stretch" ;

	private final static String CK_HALO				= "halo" ;
	private final static String CK_HALOMIN			= "halomin" ;
	private final static String CK_HALOMAX			= "halomax" ;

	private final static double DEFAULT_INTERVAL	= 1 ;
	private final static double DEFAULT_STRETCH		= 0 ;

	private final static double DEFAULT_HALO		= 4 ;
	private final static double DEFAULT_HALOMIN		= .08 ;
	private final static double DEFAULT_HALOMAX		= .4 ;

	private Projector projector ;

	public BodyPlanet( Projector projector ) {
		this.projector = projector ;
	}

	public double[] epoch() {
		Double Epochgc ;
		double epochgc, epochlo ;
		double jdAy, jdOy ;
		CAADate epoch ;
		long year ;

		Epochgc = (Double) Registry.retrieve( Epoch.RK_EPOCH ) ;
		if ( Epochgc == null )
			epochgc = Epoch.defoult() ;
		else
			epochgc = Epochgc.doubleValue() ;

		epoch = new CAADate() ;
		epoch.Set( epochgc, true ) ;

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

	public void headPS( ApplicationPostscriptStream ps ) {
		String gstate ;

		gstate = Configuration.getValue( this, getNature(), null ) ;	

		if ( gstate == null || gstate.length() == 0 )
			return ;

		for ( String token : gstate.trim().split( "\\p{Space}+" ) )
			ps.push( token ) ;
	}

	public void emitPS( ApplicationPostscriptStream ps ) {
		emitPS( ps, true ) ;
	}

	public void emitPS( ApplicationPostscriptStream ps, boolean cut ) {
		Configuration conf ;
		ListCutter cutter ;
		Geometry fov ;
		ChartPage page ;
		astrolabe.model.BodyPlanet peer ;
		BodyPlanet body ;
		List<int[]> listid ;
		List<Double> listjd ;
		double jdAe, jdOe ;
		Coordinate[] list ;
		double[] epoch ;
		astrolabe.model.Annotation annotation ;
		PostscriptEmitter emitter ;

		epoch = epoch() ;

		if ( cut ) {
			fov = (Geometry) Registry.retrieve( FOV.RK_FOV ) ;
			if ( fov == null ) {
				page = (ChartPage) Registry.retrieve( ChartPage.RK_CHARTPAGE ) ;
				if ( page != null )
					fov = page.getViewGeometry() ;
			}

			listjd = new java.util.Vector<Double>() ;
			listid = new java.util.Vector<int[]>() ;
			list = list( listjd, epoch[0], epoch[1], 0 ) ;

			if ( fov == null ) {
				listid.add( new int[] { 0, list.length-1 } ) ;
			} else {
				cutter = new ListCutter( list, fov ) ;
				cutter.segmentsInterior( listid ) ;
			}

			for ( int[] jdid : listid ) {
				jdAe = listjd.get( jdid[0] ) ;
				jdOe = listjd.get( jdid[1] ) ;

				peer = new astrolabe.model.BodyPlanet() ;
				peer.setName( getName() ) ;

				peer.setStretch( getStretch() ) ;
				peer.setNature( getNature() ) ;
				peer.setType( getType() ) ;

				peer.setAnnotation( getAnnotation() ) ;

				peer.setEpoch( new astrolabe.model.Epoch() ) ;
				peer.getEpoch().setA( new astrolabe.model.A() ) ;
				peer.getEpoch().getA().setJD( new astrolabe.model.JD() ) ;
				peer.getEpoch().setJD( new astrolabe.model.JD() ) ;

				peer.getEpoch().getA().getJD().setValue( jdAe ) ;
				peer.getEpoch().getJD().setValue( jdOe ) ;

				peer.setDialDay( getDialDay() ) ;

				try {
					peer.validate() ;
				} catch ( ValidationException e ) {
					throw new RuntimeException( e.toString() ) ;
				}

				body = new BodyPlanet( projector ) ;
				peer.copyValues( body ) ;

				ps.operator.gsave();

				body.headPS( ps ) ;
				body.emitPS( ps, false ) ;
				body.tailPS( ps ) ;

				ps.operator.grestore() ;
			}
		} else {
			list = list( null, epoch[0], epoch[1], 0 ) ;
			ps.array( true ) ;
			for ( Coordinate xy : list ) {
				ps.push( xy.x ) ;
				ps.push( xy.y ) ;
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

			if ( getDialDay() != null ) {
				PostscriptEmitter dial ;

				dial = new DialDay( this ) ;
				getDialDay().copyValues( dial ) ;

				ps.operator.gsave() ;

				dial.headPS( ps ) ;
				dial.emitPS( ps ) ;
				dial.tailPS( ps ) ;

				ps.operator.grestore() ;
			}

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
	}

	public void tailPS( ApplicationPostscriptStream ps ) {
	}

	public Coordinate project( double jd, double shift ) {
		Coordinate ec, xy ;
		Vector v, t ;

		ec = convert( jd ) ;
		xy = projector.project( ec, false ) ;
		v = new Vector( xy  ) ;

		if ( shift != 0 ) {
			xy = tangent( jd ) ;
			t = new Vector( xy ) ;
			t.apply( new double[] { 0, -1, 0, 1, 0, 0, 0, 0, 1 } ) ; // rotate 90 degrees counter clockwise
			t.scale( shift ) ;
			v.add( t ) ;
		}

		return new Coordinate( v.x, v.y ) ;
	}

	public Coordinate tangent( double jd ) {
		Coordinate ec, xy ;
		Vector v, t ;

		ec = convert( jd+1./86400 ) ;
		xy = projector.project( ec, false ) ;
		v = new Vector( xy ) ;
		ec = convert( jd ) ;
		xy = projector.project( ec, false ) ;
		t = new Vector( xy ) ;

		v.sub( t ) ;

		return new Coordinate( v.x, v.y ) ;
	}

	public double scaleMarkNth( int mark, double span ) {
		return new LinearScale( span, epoch() ).markN( mark ) ;
	}

	public Coordinate[] list( final List<Double> listjd, double jdA, double jdO, double shift ) {
		List<Coordinate> listxy ;
		double interval ;
		double d, e, g ;

		interval = Configuration.getValue( this, CK_INTERVAL, DEFAULT_INTERVAL ) ;

		listxy = new java.util.Vector<Coordinate>() ;

		listxy.add( project( jdA, shift ) ) ;
		if ( listjd != null )
			listjd.add( jdA ) ;

		d = jdO-jdA ;
		e = d-(int) ( d/interval )*interval ;
		g = ( Math.isLim0( e )?interval:e )/2 ;

		for ( double jd=jdA+g ; jd<jdO ; jd=jd+interval ) {
			listxy.add( project( jd, shift ) ) ;
			if ( listjd != null )
				listjd.add( jd ) ;
		}

		listxy.add( project( jdO, shift ) ) ;
		if ( listjd != null )
			listjd.add( jdO ) ;

		return listxy.toArray( new Coordinate[0] ) ;
	}

	public Coordinate convert( double jd ) {
		double l, b ;
		Class<?> c ;
		double stretch ;
		Method eclipticLongitude ;
		Method eclipticLatitude ;
	
		l = 0 ;
		b = 0 ;
	
		if ( getStretch() )
			stretch = Configuration.getValue( this, CK_STRETCH, DEFAULT_STRETCH ) ;
		else
			stretch = 0 ;
	
		try {
			c = Class.forName( "caa.CAA"+getType().substring( 0, 1 ).toUpperCase()+getType().substring( 1 ) ) ;
	
			eclipticLongitude = c.getMethod( "EclipticLongitude", new Class[] { double.class } ) ;
			eclipticLatitude = c.getMethod( "EclipticLatitude", new Class[] { double.class } ) ;
	
			l = (Double) eclipticLongitude.invoke( null, new Object[] { new Double( jd ) } ) ;
			b = (Double) eclipticLatitude.invoke( null, new Object[] { new Double( jd ) } ) ;
		} catch ( ClassNotFoundException e ) {
			throw new RuntimeException( e.toString() ) ;
		} catch ( NoSuchMethodException e ) {
			throw new RuntimeException( e.toString() ) ;
		} catch ( InvocationTargetException e ) {
			throw new RuntimeException( e.toString() ) ;
		} catch ( IllegalAccessException e ) {
			throw new RuntimeException( e.toString() ) ;
		}
	
		return new Coordinate( l, b+( jd-epoch()[0] )*90/90*stretch  ) ;
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
