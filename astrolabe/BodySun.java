
package astrolabe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.List;
import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.xml.ValidationException;

import com.vividsolutions.jts.geom.Geometry;

import caa.CAACoordinateTransformation;
import caa.CAADate;
import caa.CAASun;

@SuppressWarnings("serial")
public class BodySun extends astrolabe.model.BodySun implements PostscriptEmitter, Baseline {

	private final static Log log = LogFactory.getLog( BodySun.class ) ;

	private final static double DEFAULT_INTERVAL = 1 ;
	private final static double DEFAULT_STRETCH = 0 ;

	private double jdAy ;
	private double jdOy ;

	private Projector projector ;

	private double interval ;
	private double stretch ;

	private Method eclipticLongitude ;
	private Method eclipticLatitude ;

	private Baseline circle ;

	public BodySun( Peer peer, Projector projector ) {
		String circle ;
		Preferences node ; 
		CAADate date ;
		double epochG, epochL, epochA, epochO ;
		long y ;

		peer.setupCompanion( this ) ;

		date = new CAADate() ;

		epochG = ( (Double) AstrolabeRegistry.retrieve( ApplicationConstant.GC_EPOCH ) ).doubleValue() ;

		date.Set( epochG, true ) ;
		y = date.Year() ;
		date.Set( y, 1, 1, 0, 0, 0, true ) ;
		jdAy = date.Julian() ;
		date.Set( y, 12, 31, 0, 0, 0, true ) ;
		jdOy = date.Julian() ;

		if ( getEpoch() != null ) {
			epochL = AstrolabeFactory.valueOf( getEpoch() ) ;
			date.Set( epochL, true ) ;
			y = date.Year() ;
			date.Set( y, 1, 1, 0, 0, 0, true ) ;
			jdAy = date.Julian() ;
			date.Set( y, 12, 31, 0, 0, 0, true ) ;
			jdOy = date.Julian() ;

			date.Set( epochL, true ) ;

			if ( getEpoch().getA() != null ) {
				epochA = AstrolabeFactory.valueOf( getEpoch().getA() ) ;
				jdAy = epochA ;
				jdOy = date.Julian() ;
			}

			if ( getEpoch().getO() != null ) {
				epochO = AstrolabeFactory.valueOf( getEpoch().getO() ) ;
				jdOy = epochO ;
				if ( getEpoch().getA() == null )
					jdAy = date.Julian() ;
			}
		}

		date.delete() ;

		this.projector = projector ;

		node = Configuration.getClassNode( this, getName(), getType() ) ;

		interval = Configuration.getValue( node, ApplicationConstant.PK_BODY_INTERVAL, DEFAULT_INTERVAL ) ;
		if ( getStretch() ) {
			stretch = Configuration.getValue( node, ApplicationConstant.PK_BODY_STRETCH, DEFAULT_STRETCH ) ;
		} else {
			stretch = 0 ;
		}

		try {
			eclipticLongitude = getClass().getMethod( getType()+"EclipticLongitude", new Class[] { double.class } ) ;
			eclipticLatitude = getClass().getMethod( getType()+"EclipticLatitude", new Class[] { double.class } ) ;
		} catch ( NoSuchMethodException e ) {
			throw new RuntimeException( e.toString() ) ;
		}

		circle = ( (astrolabe.model.BodySun) peer ).getCircle() ;
		if ( circle != null ) {
			this.circle = (Baseline) Registry.retrieve( circle ) ;
			if ( this.circle == null ) {
				String msg ;

				msg = MessageCatalog.message( ApplicationConstant.GC_APPLICATION, ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
				msg = MessageFormat.format( msg, new Object[] { "\""+circle+"\"", null } ) ;
				log.warn( msg ) ;
			}
		}
	}

	public void headPS( AstrolabePostscriptStream ps ) {
		ElementImportance importance ;

		importance = new ElementImportance( getImportance() ) ;
		importance.headPS( ps ) ;
		importance.emitPS( ps ) ;
		importance.tailPS( ps ) ;
	}

	public void emitPS( AstrolabePostscriptStream ps ) {
		emitPS( ps, true ) ;
	}

	public void emitPS( AstrolabePostscriptStream ps, boolean cut ) {
		ListCutter cutter ;
		Geometry fov ;
		astrolabe.model.BodySun peer ;
		BodySun body ;
		List<int[]> idlist ;
		List<Double> jdlist ;
		double jdAe, jdOe ;
		List<double[]> l ;
		double[] xy ;

		if ( cut ) {
			fov = (Geometry) Registry.retrieve( ApplicationConstant.GC_FOVEFF ) ;
			if ( fov == null ) {
				fov = (Geometry) AstrolabeRegistry.retrieve( ApplicationConstant.GC_FOVUNI ) ;
			}

			jdlist = new java.util.Vector<Double>() ;
			cutter = new ListCutter( list( jdlist ), fov ) ;

			idlist = new java.util.Vector<int[]>() ;
			cutter.segmentsInterior( idlist ) ;
			for ( int[] jdid : idlist ) {
				jdAe = jdlist.get( jdid[0] ) ;
				jdOe = jdlist.get( jdid[1] ) ;

				peer = new astrolabe.model.BodySun() ;
				peer.setName( ApplicationConstant.GC_NS_CUT+getName() ) ;

				peer.setStretch( getStretch() ) ;
				peer.setType( getType() ) ;
				peer.setImportance( getImportance() ) ;
				peer.setCircle( getCircle() ) ;

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

				body = new BodySun( peer, projector ) ;

				ps.operator.gsave();

				body.headPS( ps ) ;
				body.emitPS( ps, false ) ;
				body.tailPS( ps ) ;

				ps.operator.grestore() ;
			}
		} else {
			if ( circle==null ) {
				l = list( jdAy, jdOy, 0 ) ;
				ps.operator.mark() ;
				for ( int n=l.size() ; n>0 ; n-- ) {
					xy = (double[]) l.get( n-1 ) ;
					ps.push( xy[0] ) ;
					ps.push( xy[1] ) ;
				}

				ps.custom( ApplicationConstant.PS_CUSTOM_POLYLINE ) ;

				// halo stroke
				ps.operator.currentlinewidth() ;
				ps.operator.dup();
				ps.push( (Double) ( AstrolabeRegistry.retrieve( ApplicationConstant.PK_CHART_HALOMAX ) ) ) ; 
				ps.push( (Double) ( AstrolabeRegistry.retrieve( ApplicationConstant.PK_CHART_HALOMIN ) ) ) ; 
				ps.push( (Double) ( AstrolabeRegistry.retrieve( ApplicationConstant.PK_CHART_HALO ) ) ) ; 
				ps.custom( ApplicationConstant.PS_CUSTOM_HALO ) ;
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

					ps.operator.gsave() ;

					dial = new DialDay( getDialDay(), this ) ;
					dial.headPS( ps ) ;
					dial.emitPS( ps ) ;
					dial.tailPS( ps ) ;

					ps.operator.grestore() ;
				}
			} else {
				if ( getDialDay() != null ) {
					PostscriptEmitter dial ;

					ps.operator.gsave() ;
					dial = new DialDay( getDialDay(), this ) ;
					dial.headPS( ps ) ;
					dial.emitPS( ps ) ;
					dial.tailPS( ps ) ;

					ps.operator.grestore() ;
				}
			}

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
	}

	public void tailPS( AstrolabePostscriptStream ps ) {
	}

	public double[] project( double jd ) {
		return project( jd, 0 ) ;
	}

	public double[] project( double jd, double shift ) {
		double[] r, ec, xy ;
		double a ;
		Vector v, t ;

		if ( circle==null ) {
			ec = convert( jd ) ;
			xy = projector.project( ec ) ;
			v = new Vector( xy[0], xy[1] ) ;

			if ( shift != 0 ) {
				xy = tangent( jd ) ;
				t = new Vector( xy[0], xy[1] ) ;
				t.apply( new double[] { 0, -1, 0, 1, 0, 0, 0, 0, 1 } ) ; // rotate 90 degrees counter clockwise
				t.scale( shift ) ;
				v.add( t ) ;
			}

			r = new double[] { v.x, v.y } ;
		} else {
			a = angle( jd ) ;
			r = circle.project( a, shift ) ;
		}

		return r ;
	}

	public double[] tangent( double jd ) {
		double[] r ;
		double l, b, a ;

		if ( circle==null ) {
			l = 0 ;
			b = 0 ;

			try {
				l = (Double) eclipticLongitude.invoke( null, new Object[] { new Double( jd ) } ) ;
				b = (Double) eclipticLatitude.invoke( null, new Object[] { new Double( jd ) } ) ;
			} catch ( InvocationTargetException e ) {
				throw new RuntimeException( e.toString() ) ;
			} catch ( IllegalAccessException e ) {
				throw new RuntimeException( e.toString() ) ;
			}

			r = new double[] { l, b+( jd-jdAy )*90/90*stretch } ;
		} else {
			a = angle( jd ) ;
			r = circle.tangent( a ) ;
		}

		return r ;
	}

	private double angle( double jd ) {
		double r ;
		double[] ec, eq ;

		ec = convert( jd ) ;
		eq = projector.convert( ec ) ;
		r = circle.unconvert( eq ) ;

		return r ;
	}

	public double[] convert( double jd ) {
		double[] r = new double[2] ;
		double l, b ;

		l = 0 ;
		b = 0 ;

		try {
			l = (Double) eclipticLongitude.invoke( null, new Object[] { new Double( jd ) } ) ;
			b = (Double) eclipticLatitude.invoke( null, new Object[] { new Double( jd ) } ) ;
		} catch ( InvocationTargetException e ) {
			throw new RuntimeException( e.toString() ) ;
		} catch ( IllegalAccessException e ) {
			throw new RuntimeException( e.toString() ) ;
		}

		r[0] = l ;
		r[1] = b+( jd-jdAy )*90/90*stretch ;

		return r ;
	}

	public double unconvert( double[] eq ) {
		return Double.NaN ;
	}

	public List<double[]> list( List<Double> list ) {
		return list( list, jdAy, jdOy, 0 ) ;
	}

	public List<double[]> list( List<Double> list, double shift ) {
		return list( list, jdAy, jdOy, shift ) ;
	}

	public List<double[]> list( List<Double> list, double jdA, double jdO, double shift ) {
		List<double[]> r = new java.util.Vector<double[]>() ;
		double g ;

		r.add( project( jdA, shift ) ) ;
		if ( list != null ) {
			list.add( jdA ) ;
		}

		g = mapIndexToRange( jdA, jdO ) ;
		for ( double jd=jdA+g ; jd<jdO ; jd=jd+interval ) {
			r.add( project( jd, shift ) ) ;
			if ( list != null ) {
				list.add( jd ) ;
			}
		}

		r.add( project( jdO, shift ) ) ;
		if ( list != null ) {
			list.add( jdO ) ;
		}

		return r ;
	}

	public List<double[]> list() {
		return list( null, jdAy, jdOy, 0 ) ;
	}

	public List<double[]> list( double shift ) {
		return list( null, jdAy, jdOy, shift ) ;
	}

	public List<double[]> list( double jdA, double jdO, double shift ) {
		return list( null, jdA, jdO, shift ) ;
	}

	public boolean probe( double jd ) {
		return jd>=jdAy&&jd<=jdOy ;
	}

	public double mapIndexToScale( int index ) {
		return mapIndexToScale( index, interval, jdAy, jdOy ) ;
	}

	public double mapIndexToScale( double span ) {
		return mapIndexToScale( 0, span, jdAy, jdOy ) ;
	}

	public double mapIndexToScale( int index, double span ) {
		return mapIndexToScale( index, span, jdAy, jdOy ) ;
	}

	private static double mapIndexToScale( int index, double span, double jdA, double jdO ) {
		return index<0?jdO:jdA+index*span ;
	}

	public double mapIndexToRange() {
		return BodyPlanet.gap( 0, interval, jdAy , jdOy ) ;
	}

	public double mapIndexToRange( double jdA, double jdO ) {
		return BodyPlanet.gap( 0, interval, jdA , jdO ) ;
	}

	public double mapIndexToRange( int index, double jdA, double jdO ) {
		return BodyPlanet.gap( index, interval, jdA , jdO ) ;
	}

	public static double meanEclipticLongitude( double JD ) {
		double rho, rho2, rho3, rho4, rho5 ;

		rho = ( JD-2451545 )/365250 ;
		rho2 = rho*rho ;
		rho3 = rho2*rho ;
		rho4 = rho3*rho ;
		rho5 = rho4*rho ;

		return CAACoordinateTransformation.MapTo0To360Range(
				280.4664567+360007.6982779*rho+0.03032028*rho2+rho3/49931-rho4/15300-rho5/2000000 ) ;
	}

	public static double meanEclipticLatitude( double JD ) {
		return 0 ;
	}

	public static double trueEclipticLongitude( double JD ) {
		return CAASun.GeometricEclipticLongitude( JD ) ;
	}

	public static double trueEclipticLatitude( double JD ) {
		return CAASun.GeometricEclipticLatitude( JD ) ;
	}
}
