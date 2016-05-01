
package astrolabe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.prefs.Preferences;

import org.exolab.castor.xml.ValidationException;

import com.vividsolutions.jts.geom.Geometry;

import caa.CAADate;

@SuppressWarnings("serial")
public class BodyPlanet extends astrolabe.model.BodyPlanet implements PostscriptEmitter, Baseline {

	private final static double DEFAULT_INTERVAL = 1 ;
	private final static double DEFAULT_STRETCH = 0 ;

	private double jdAy ;
	private double jdOy ;

	private Projector projector ;

	private double interval ;
	private double stretch ;

	private Method eclipticLongitude ;
	private Method eclipticLatitude ;

	public BodyPlanet( Peer peer, Projector projector ) throws ParameterNotValidException {
		Preferences node ; 
		CAADate date ;
		double epochG, epochL, epochA, epochO ;
		long y ;

		peer.setupCompanion( this ) ;
		try {
			validate() ;
		} catch ( ValidationException e ) {
			throw new ParameterNotValidException( e.toString() ) ;
		}

		date = new CAADate() ;

		epochG = ( (Double) Registry.retrieve( ApplicationConstant.GC_EPOCHE ) ).doubleValue() ;
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

		if ( ! ( jdAy<jdOy ) ) {
			String msg ;

			msg = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
			msg = MessageFormat.format( msg, new Object[] { "jdOy>jdAy", "" } ) ;

			throw new ParameterNotValidException( msg ) ;
		}

		this.projector = projector ;

		node = ApplicationHelper.getClassNode( this, getName(), getType() ) ;

		interval = ApplicationHelper.getPreferencesKV( node, ApplicationConstant.PK_BODY_INTERVAL, DEFAULT_INTERVAL ) ;
		if ( getStretch() ) {
			stretch = ApplicationHelper.getPreferencesKV( node, ApplicationConstant.PK_BODY_STRETCH, DEFAULT_STRETCH ) ;
		} else {
			stretch = 0 ;
		}

		try {
			Class<?> c ;

			c = Class.forName( "caa.CAA"+getType().substring( 0, 1 ).toUpperCase()+getType().substring( 1 ) ) ;

			eclipticLongitude = c.getMethod( "EclipticLongitude", new Class[] { double.class } ) ;
			eclipticLatitude = c.getMethod( "EclipticLatitude", new Class[] { double.class } ) ;
		} catch ( ClassNotFoundException e ) {
			throw new RuntimeException( e.toString() ) ;
		} catch ( NoSuchMethodException e ) {
			throw new RuntimeException( e.toString() ) ;
		}
	}

	public void headPS( PostscriptStream ps ) {
		ElementImportance importance ;

		importance = new ElementImportance( getImportance() ) ;
		importance.headPS( ps ) ;
		importance.emitPS( ps ) ;
		importance.tailPS( ps ) ;
	}

	public void emitPS( PostscriptStream ps ) {
		emitPS( ps, true ) ;
	}

	public void emitPS( PostscriptStream ps, boolean cut ) {
		ListCutter cutter ;
		Geometry fov ;
		astrolabe.model.BodyPlanet peer ;
		BodyPlanet body ;
		java.util.Vector<int[]> idlist ;
		java.util.Vector<Double> jdlist ;
		double jdAe, jdOe ;
		java.util.Vector<double[]> l ;
		double[] xy ;

		if ( cut ) {
			try {
				fov = (Geometry) Registry.retrieve( ApplicationConstant.GC_FOVEFF ) ;
			} catch ( ParameterNotValidException ee ) {
				try {
					fov = (Geometry) Registry.retrieve( ApplicationConstant.GC_FOVUNI ) ;
				} catch ( ParameterNotValidException eu ) {
					throw new RuntimeException( eu.toString() ) ;
				}
			}

			jdlist = new java.util.Vector<Double>() ;
			cutter = new ListCutter( list( jdlist ), fov ) ;

			idlist = new java.util.Vector<int[]>() ;
			cutter.segmentsInterior( idlist ) ;
			for ( int[] jdid : idlist ) {
				jdAe = jdlist.get( jdid[0] ) ;
				jdOe = jdlist.get( jdid[1] ) ;

				peer = new astrolabe.model.BodyPlanet() ;
				peer.setEpoch( new astrolabe.model.Epoch() ) ;
				peer.getEpoch().setA( new astrolabe.model.A() ) ;
				peer.getEpoch().getA().setJD( new astrolabe.model.JD() ) ;
				peer.getEpoch().setJD( new astrolabe.model.JD() ) ;

				peer.getEpoch().getA().getJD().setValue( jdAe ) ;
				peer.getEpoch().getJD().setValue( jdOe ) ;

				if ( getName() != null ) {
					peer.setName( ApplicationConstant.GC_NS_CUT+getName() ) ;
				}

				peer.setStretch( getStretch() ) ;
				peer.setType( getType() ) ;

				peer.setDialDay( getDialDay() ) ;
				peer.setAnnotation( getAnnotation() ) ;

				try {
					body = new BodyPlanet( peer, projector ) ;

					ps.operator.gsave();

					body.headPS( ps ) ;
					body.emitPS( ps, false ) ;
					body.tailPS( ps ) ;

					ps.operator.grestore() ;
				} catch ( ParameterNotValidException e ) {}
			}
		} else {
			l = list( jdAy, jdOy, 0 ) ;
			ps.operator.mark() ;
			for ( int n=l.size() ; n>0 ; n-- ) {
				xy = l.get( n-1 ) ;
				ps.push( xy[0] ) ;
				ps.push( xy[1] ) ;
			}
			try {
				ps.custom( ApplicationConstant.PS_PROLOG_POLYLINE ) ;

				// halo stroke
				ps.operator.currentlinewidth() ;
				ps.operator.dup();
				ps.push( (Double) ( Registry.retrieve( ApplicationConstant.PK_CHART_HALOMAX ) ) ) ; 
				ps.push( (Double) ( Registry.retrieve( ApplicationConstant.PK_CHART_HALOMIN ) ) ) ; 
				ps.push( (Double) ( Registry.retrieve( ApplicationConstant.PK_CHART_HALO ) ) ) ; 
				ps.custom( ApplicationConstant.PS_PROLOG_HALO ) ;
				ps.operator.mul( 2 ) ;
				ps.operator.add() ;
				ps.operator.gsave() ;
				ps.operator.setlinewidth() ;
				ps.operator.setlinecap( 2 ) ;
				ps.operator.setgray( 1 ) ;
				ps.operator.stroke() ;
				ps.operator.grestore() ;

			} catch ( ParameterNotValidException e ) {
				throw new RuntimeException( e.toString() ) ;
			}
			ps.operator.gsave() ;
			ps.operator.stroke() ;
			ps.operator.grestore() ;

			if ( getDialDay() != null ) {
				PostscriptEmitter dial ;

				ps.operator.gsave() ;

				try {
					dial = new DialDay( getDialDay(), this ) ;
					dial.headPS( ps ) ;
					dial.emitPS( ps ) ;
					dial.tailPS( ps ) ;
				} catch ( ParameterNotValidException e ) {} // DialDay validated in constructor

				ps.operator.grestore() ;
			}

			if ( getAnnotation() != null ) {
				try {
					ApplicationHelper.emitPS( ps, getAnnotation() ) ;
				} catch ( ParameterNotValidException e ) {
					throw new RuntimeException( e.toString() ) ;
				}
			}
		}
	}

	public void tailPS( PostscriptStream ps ) {
	}

	public double[] project( double jd ) {
		return project( jd, 0 ) ;
	}

	public double[] project( double jd, double shift ) {
		double[] ec, xy ;
		Vector v, t ;

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

		return new double[] { v.x, v.y } ;
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

	public double[] tangent( double jd ) {
		double[] ec, xy ;
		Vector v, t ;

		ec = convert( jd+1./86400 ) ;
		xy = projector.project( ec ) ;
		v = new Vector( xy[0], xy[1] ) ;
		ec = convert( jd ) ;
		xy = projector.project( ec ) ;
		t = new Vector( xy[0], xy[1] ) ;

		v.sub( t ) ;

		return new double[] { v.x, v.y } ;
	}

	public java.util.Vector<double[]> list( java.util.Vector<Double> list ) {
		return list( list, jdAy, jdOy, 0 ) ;
	}

	public java.util.Vector<double[]> list( java.util.Vector<Double> list, double shift ) {
		return list( list, jdAy, jdOy, shift ) ;
	}

	public java.util.Vector<double[]> list( java.util.Vector<Double> list, double jdA, double jdO, double shift ) {
		java.util.Vector<double[]> r = new java.util.Vector<double[]>() ;
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

	public java.util.Vector<double[]> list() {
		return list( null, jdAy, jdOy, 0 ) ;
	}

	public java.util.Vector<double[]> list( double shift ) {
		return list( null, jdAy, jdOy, shift ) ;
	}

	public java.util.Vector<double[]> list( double jdA, double jdO, double shift ) {
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
		return gap( 0, interval, jdAy , jdOy ) ;
	}

	public double mapIndexToRange( double jdA, double jdO ) {
		return gap( 0, interval, jdA , jdO ) ;
	}

	public double mapIndexToRange( int index, double jdA, double jdO ) {
		return gap( index, interval, jdA , jdO ) ;
	}

	public static double gap( int index, double interval, double jdA, double jdO ) {
		double r ;
		double d, g ;

		d = jdO-jdA ;
		g = d-(int) ( d/interval )*interval ;

		r = ( ( Math.isLim0( g )?interval:g )/2 )+index*interval ;

		return r ;
	}
}
