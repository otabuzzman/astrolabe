
package astrolabe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.prefs.Preferences;

import org.exolab.castor.xml.ValidationException;

import com.vividsolutions.jts.geom.Geometry;

import caa.CAACoordinateTransformation;
import caa.CAADate;
import caa.CAASun;

@SuppressWarnings("serial")
public class BodySun extends astrolabe.model.BodySun implements PostscriptEmitter, Baseline {

	private final static double DEFAULT_INTERVAL	= 1 ;
	private final static double DEFAULT_STRETCH		= 0 ;

	private Projector projector ;

	public BodySun( Projector projector ) {
		this.projector = projector ;
	}

	public double[] epoch() {
		double epochgc, epochlo ;
		double jdAy, jdOy ;
		CAADate epoch ;
		long year ;

		epochgc = ( (Double) Registry.retrieve( ApplicationConstant.GC_EPOCH ) ).doubleValue() ;

		epoch = new CAADate() ;
		epoch.Set( epochgc, true ) ;

		year = epoch.Year() ;
		epoch.Set( year, 1, 1, 0, 0, 0, true ) ;
		jdAy = epoch.Julian() ;
		epoch.Set( year, 12, 31, 0, 0, 0, true ) ;
		jdOy = epoch.Julian() ;

		if ( getEpoch() != null ) {
			epochlo = AstrolabeFactory.valueOf( getEpoch() ) ;
			epoch.Set( epochlo, true ) ;

			year = epoch.Year() ;
			epoch.Set( year, 1, 1, 0, 0, 0, true ) ;
			jdAy = epoch.Julian() ;
			epoch.Set( year, 12, 31, 0, 0, 0, true ) ;
			jdOy = epoch.Julian() ;

			epoch.Set( epochlo, true ) ;
			if ( getEpoch().getA() != null ) {
				jdAy = AstrolabeFactory.valueOf( getEpoch().getA() ) ;
				jdOy = epoch.Julian() ;
			}
			if ( getEpoch().getO() != null ) {
				if ( getEpoch().getA() == null )
					jdAy = epoch.Julian() ;
				jdOy = AstrolabeFactory.valueOf( getEpoch().getO() ) ;
			}
		}

		epoch.delete() ;

		return new double[] { jdAy, jdOy } ;
	}

	public void headPS( AstrolabePostscriptStream ps ) {
		GSPaintStroke nature ;

		nature = new GSPaintStroke( getNature(), getName() ) ;

		nature.headPS( ps ) ;
		nature.emitPS( ps ) ;
		nature.tailPS( ps ) ;
	}

	public void emitPS( AstrolabePostscriptStream ps ) {
		emitPS( ps, true ) ;
	}

	public void emitPS( AstrolabePostscriptStream ps, boolean cut ) {
		ListCutter cutter ;
		Geometry fov ;
		astrolabe.model.BodySun peer ;
		BodySun body ;
		List<int[]> listid ;
		List<Double> listjd ;
		double jdAe, jdOe ;
		Baseline circle ;
		List<double[]> l ;
		double[] epoch, xy ;

		epoch = epoch() ;

		if ( cut ) {
			fov = (Geometry) Registry.retrieve( ApplicationConstant.GC_FOVEFF ) ;
			if ( fov == null ) {
				fov = (Geometry) Registry.retrieve( ApplicationConstant.GC_FOVUNI ) ;
			}

			listjd = new java.util.Vector<Double>() ;
			cutter = new ListCutter( list( listjd, epoch[0], epoch[1], 0 ), fov ) ;

			listid = new java.util.Vector<int[]>() ;
			cutter.segmentsInterior( listid ) ;
			for ( int[] jdid : listid ) {
				jdAe = listjd.get( jdid[0] ) ;
				jdOe = listjd.get( jdid[1] ) ;

				peer = new astrolabe.model.BodySun() ;
				if ( getName() == null )
					peer.setName( ApplicationConstant.GC_NS_CUT ) ;
				else
					peer.setName( ApplicationConstant.GC_NS_CUT+getName() ) ;

				peer.setStretch( getStretch() ) ;
				peer.setType( getType() ) ;
				peer.setNature( getNature() ) ;
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

				body = new BodySun( projector ) ;
				peer.setupCompanion( body ) ;

				ps.operator.gsave();

				body.headPS( ps ) ;
				body.emitPS( ps, false ) ;
				body.tailPS( ps ) ;

				ps.operator.grestore() ;
			}
		} else {
			circle = (Baseline) Registry.retrieve( getCircle() ) ;

			if ( circle==null ) {
				l = list( null, epoch[0], epoch[1], 0 ) ;
				ps.array( true ) ;
				for ( int n=0 ; n<l.size() ; n++ ) {
					xy = (double[]) l.get( n ) ;
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

				if ( getDialDay() != null ) {
					PostscriptEmitter dial ;

					dial = new DialDay( this ) ;
					getDialDay().setupCompanion( dial ) ;

					ps.operator.gsave() ;

					dial.headPS( ps ) ;
					dial.emitPS( ps ) ;
					dial.tailPS( ps ) ;

					ps.operator.grestore() ;
				}
			} else {
				if ( getDialDay() != null ) {
					PostscriptEmitter dial ;

					dial = new DialDay( this ) ;
					getDialDay().setupCompanion( dial ) ;

					ps.operator.gsave() ;

					dial.headPS( ps ) ;
					dial.emitPS( ps ) ;
					dial.tailPS( ps ) ;

					ps.operator.grestore() ;
				}
			}

			if ( getAnnotation() != null ) {
				PostscriptEmitter annotation ;

				for ( int i=0 ; i<getAnnotationCount() ; i++ ) {
					annotation = AstrolabeFactory.companionOf( getAnnotation( i ) ) ;

					ps.operator.gsave() ;

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

	public double[] project( double jd, double shift ) {
		double[] r, ec, xy ;
		Baseline circle ;
		double a ;
		Vector v, t ;

		circle = (Baseline) Registry.retrieve( getCircle() ) ;

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

	public double[] convert( double jd ) {
		double[] r = new double[2] ;
		double l, b ;
		double stretch ;
		Preferences node ;
		Method eclipticLongitude ;
		Method eclipticLatitude ;

		l = 0 ;
		b = 0 ;

		node = Configuration.getClassNode( this, getName(), getType() ) ;

		if ( getStretch() ) {
			stretch = Configuration.getValue( node,
					ApplicationConstant.PK_BODY_STRETCH, DEFAULT_STRETCH ) ;
		} else {
			stretch = 0 ;
		}

		try {
			eclipticLongitude = getClass().getMethod( getType()+"EclipticLongitude", new Class[] { double.class } ) ;
			eclipticLatitude = getClass().getMethod( getType()+"EclipticLatitude", new Class[] { double.class } ) ;

			l = (Double) eclipticLongitude.invoke( null, new Object[] { new Double( jd ) } ) ;
			b = (Double) eclipticLatitude.invoke( null, new Object[] { new Double( jd ) } ) ;
		} catch ( NoSuchMethodException e ) {
			throw new RuntimeException( e.toString() ) ;
		} catch ( InvocationTargetException e ) {
			throw new RuntimeException( e.toString() ) ;
		} catch ( IllegalAccessException e ) {
			throw new RuntimeException( e.toString() ) ;
		}

		r[0] = l ;
		r[1] = b+( jd-epoch()[0] )*stretch ;

		return r ;
	}

	public double unconvert( double[] eq ) {
		return Double.NaN ;
	}

	public double[] tangent( double jd ) {
		double[] r ;
		Baseline circle ;
		double stretch ;
		double l, b, a ;
		Preferences node ;
		Method eclipticLongitude ;
		Method eclipticLatitude ;

		circle = (Baseline) Registry.retrieve( getCircle() ) ;

		if ( circle==null ) {
			l = 0 ;
			b = 0 ;

			node = Configuration.getClassNode( this, getName(), getType() ) ;

			if ( getStretch() ) {
				stretch = Configuration.getValue( node,
						ApplicationConstant.PK_BODY_STRETCH, DEFAULT_STRETCH ) ;
			} else {
				stretch = 0 ;
			}

			try {
				eclipticLongitude = getClass().getMethod( getType()+"EclipticLongitude", new Class[] { double.class } ) ;
				eclipticLatitude = getClass().getMethod( getType()+"EclipticLatitude", new Class[] { double.class } ) ;

				l = (Double) eclipticLongitude.invoke( null, new Object[] { new Double( jd ) } ) ;
				b = (Double) eclipticLatitude.invoke( null, new Object[] { new Double( jd ) } ) ;
			} catch ( NoSuchMethodException e ) {
				throw new RuntimeException( e.toString() ) ;
			} catch ( InvocationTargetException e ) {
				throw new RuntimeException( e.toString() ) ;
			} catch ( IllegalAccessException e ) {
				throw new RuntimeException( e.toString() ) ;
			}

			r = new double[] { l, b+( jd-epoch()[0] )*stretch } ;
		} else {
			a = angle( jd ) ;
			r = circle.tangent( a ) ;
		}

		return r ;
	}

	private double angle( double jd ) {
		double r ;
		Baseline circle ;
		double[] ec, eq ;

		circle = (Baseline) Registry.retrieve( getCircle() ) ;

		ec = convert( jd ) ;
		eq = projector.convert( ec ) ;
		r = circle.unconvert( eq ) ;

		return r ;
	}

	public List<double[]> list( List<Double> listjd, double jdA, double jdO, double shift ) {
		List<double[]> listxy ;
		double interval ;
		double d, e, g ;

		interval = Configuration.getValue(
				Configuration.getClassNode( this, getName(), getType() ),
				ApplicationConstant.PK_BODY_INTERVAL, DEFAULT_INTERVAL ) ;

		listxy = new java.util.Vector<double[]>() ;

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

		return listxy ;
	}

	public double scaleMarkNth( int mark, double span ) {
		return new LinearScale( span, epoch() ).markN( mark ) ;
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
