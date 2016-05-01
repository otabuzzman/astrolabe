
package astrolabe;

import java.util.List;
import java.util.prefs.Preferences;

import org.exolab.castor.xml.ValidationException;

import astrolabe.model.AngleType;
import astrolabe.model.AnnotationType;
import astrolabe.model.BodyArealType;
import astrolabe.model.BodyStellarType;
import astrolabe.model.CalendarType;
import astrolabe.model.CartesianType;
import astrolabe.model.CircleType;
import astrolabe.model.DateType;
import astrolabe.model.DMSType;
import astrolabe.model.HMSType;
import astrolabe.model.HorizonType;
import astrolabe.model.RationalType;
import astrolabe.model.PolarType;
import astrolabe.model.SphericalType;
import astrolabe.model.TextType;
import astrolabe.model.TimeType;
import astrolabe.model.YMDType;

import caa.CAACoordinateTransformation;
import caa.CAADate;

public final class AstrolabeFactory {

	private AstrolabeFactory() {
	}

	public static Atlas companionOf( astrolabe.model.Atlas at ) {
		astrolabe.model.AtlasStereographic atS ;
		astrolabe.model.AtlasOrthographic atO ;
		astrolabe.model.AtlasEquidistant atE ;
		Atlas atlas ;

		if ( ( atS = at.getAtlasStereographic() ) != null ) {
			atlas = new AtlasStereographic( atS ) ;
		} else if ( ( atO = at.getAtlasOrthographic() ) != null ) {
			atlas = new AtlasOrthographic( atO ) ;
		} else if ( ( atE = at.getAtlasEquidistant() ) != null ) {
			atlas = new AtlasEquidistant( atE ) ;
		} else { // at.getAtlasGnomonic() != null
			atlas = new AtlasGnomonic( at.getAtlasGnomonic() ) ;
		}
		return atlas ;
	}

	public static PostscriptEmitter companionOf( astrolabe.model.Chart ch ) {
		astrolabe.model.ChartStereographic chS ;
		astrolabe.model.ChartOrthographic chO ;
		astrolabe.model.ChartEquidistant chE ;
		PostscriptEmitter chart ;

		if ( ( chS = ch.getChartStereographic() ) != null ) {
			chart = new ChartStereographic( chS ) ;
		} else if ( ( chO = ch.getChartOrthographic() ) != null ) {
			chart = new ChartOrthographic( chO ) ;
		} else if ( ( chE = ch.getChartEquidistant() ) != null ) {
			chart = new ChartEquidistant( chE ) ;
		} else { // ch.getChartGnomonic() != null
			chart = new ChartGnomonic( ch.getChartGnomonic() ) ;
		}
		return chart ;
	}

	public static PostscriptEmitter companionOf( astrolabe.model.Horizon ho, Projector p ) {
		astrolabe.model.HorizonLocal hoLo ;
		astrolabe.model.HorizonEquatorial hoEq ;
		astrolabe.model.HorizonEcliptical hoEc ;
		PostscriptEmitter horizon ;

		if ( ( hoLo = ho.getHorizonLocal() ) != null  ) {
			horizon = new HorizonLocal( hoLo, p ) ;
		} else if ( ( hoEq = ho.getHorizonEquatorial() ) != null  ) {
			horizon = new HorizonEquatorial( hoEq, p ) ;
		} else if ( ( hoEc = ho.getHorizonEcliptical() ) != null  ) {
			horizon = new HorizonEcliptical( hoEc, p ) ;
		} else { // ho.getHorizonGalactic() != null
			horizon = new HorizonGalactic( ho.getHorizonGalactic(), p ) ;
		}

		return horizon ;
	}

	public static PostscriptEmitter companionOf( astrolabe.model.Circle cl, Projector p ) {
		astrolabe.model.CircleParallel clP ;
		astrolabe.model.CircleMeridian clM ;
		astrolabe.model.CircleSouthernPolar clSP ;
		astrolabe.model.CircleNorthernPolar clNP ;
		astrolabe.model.CircleSouthernTropic clST ;
		PostscriptEmitter circle ;

		if ( ( clP = cl.getCircleParallel() ) != null ) {
			circle = new CircleParallel( clP, p ) ;
		} else if ( ( clM = cl.getCircleMeridian() ) != null ) {
			circle = new CircleMeridian( clM, p ) ;
		} else if ( ( clSP = cl.getCircleSouthernPolar() ) != null ) {
			circle = new CircleSouthernPolar( clSP, p ) ;
		} else if ( ( clNP = cl.getCircleNorthernPolar() ) != null ) {
			circle = new CircleNorthernPolar( clNP, p ) ;
		} else if ( ( clST = cl.getCircleSouthernTropic() ) != null ) {
			circle = new CircleSouthernTropic( clST, p ) ;
		} else { // cl.getCircleNorthernTropic() != null
			circle = new CircleNorthernTropic( cl.getCircleNorthernTropic(), p ) ;
		}

		return circle ;
	}

	public static PostscriptEmitter companionOf( astrolabe.model.Dial dl, Baseline baseline ) {
		astrolabe.model.DialDegree dlD ;
		PostscriptEmitter dial ;

		if ( ( dlD = dl.getDialDegree() ) != null ) {
			dial = new DialDegree( dlD, baseline ) ;
		} else { // dl.getDialHour() != null
			dial = new DialHour( dl.getDialHour(), baseline ) ;
		}

		return dial ;
	}

	public static PostscriptEmitter companionOf( astrolabe.model.Annotation an ) {
		astrolabe.model.AnnotationStraight anS ;
		PostscriptEmitter annotation ;

		if ( ( anS = an.getAnnotationStraight() ) != null ) {
			annotation = new AnnotationStraight( anS ) ;
		} else { // an.getAnnotationCurved() != null
			annotation = new AnnotationCurved( an.getAnnotationCurved() ) ;
		}

		return annotation ;
	}

	public static PostscriptEmitter companionOf( astrolabe.model.Body bd, Projector p ) {
		astrolabe.model.BodyStellar bdS ;
		astrolabe.model.BodyAreal bdA ;
		astrolabe.model.BodyPlanet bdP ;
		astrolabe.model.BodyMoon bdM ;
		astrolabe.model.BodySun bdH ;
		astrolabe.model.BodyElliptical bdE ;
		PostscriptEmitter body ;

		if ( ( bdS = bd.getBodyStellar() ) != null ) {
			body = new BodyStellar( bdS, p ) ;
		} else if ( ( bdA = bd.getBodyAreal() ) != null ) {
			body = new BodyAreal( bdA, p ) ;
		} else if ( ( bdP = bd.getBodyPlanet() ) != null ) {
			body = new BodyPlanet( bdP, p ) ;
		} else if ( ( bdM = bd.getBodyMoon() ) != null ) {
			body = new BodyMoon( bdM, p ) ;
		} else if ( ( bdH = bd.getBodySun() ) != null ) {
			body = new BodySun( bdH, p ) ;
		} else if ( ( bdE = bd.getBodyElliptical() ) != null ) {
			body = new BodyElliptical( bdE, p ) ;
		} else { // bd.getBodyParabolical() != null
			body = new BodyParabolical( bd.getBodyParabolical(), p ) ;
		}

		return body ;
	}

	public static Catalog companionOf( astrolabe.model.Catalog ct, Projector p ) {
		astrolabe.model.CatalogADC1239H ct1239h ;
		astrolabe.model.CatalogADC1239T ct1239t ;
		astrolabe.model.CatalogADC5050 ct5050 ;
		astrolabe.model.CatalogADC5109 ct5109 ;
		astrolabe.model.CatalogADC6049 ct6049 ;
		astrolabe.model.CatalogADC7118 ct7118 ;
		Catalog catalog ;

		if ( ( ct1239h = ct.getCatalogADC1239H() ) != null ) {
			catalog = new CatalogADC1239H( ct1239h, p ) ;
		} else if ( ( ct1239t = ct.getCatalogADC1239T() ) != null ) {
			catalog = new CatalogADC1239T( ct1239t, p ) ;
		} else if ( ( ct5050 = ct.getCatalogADC5050() ) != null ) {
			catalog = new CatalogADC5050( ct5050, p ) ;
		} else if ( ( ct5109 = ct.getCatalogADC5109() ) != null ) {
			catalog = new CatalogADC5109( ct5109, p ) ;
		} else if ( ( ct6049 = ct.getCatalogADC6049() ) != null ) {
			catalog = new CatalogADC6049( ct6049, p ) ;
		} else if ( ( ct7118 = ct.getCatalogADC7118() ) != null ) {
			catalog = new CatalogADC7118( ct7118, p ) ;
		} else { // // ct.getCatalogADC7237() != null
			catalog = new CatalogADC7237( ct.getCatalogADC7237(), p ) ;
		}

		return catalog ;
	}

	public static void modelOf( HorizonType horizon ) throws ValidationException {
		if ( ! modelOf( horizon, true ) ) {
			throw new ValidationException( horizon.getClass().getSimpleName() ) ;
		}
	}

	public static boolean modelOf( HorizonType horizon, boolean validate ) {
		Preferences node ;

		node = Configuration.getClassNode( horizon, horizon.getName(), null ) ;
		horizon.setupPeer( node ) ;

		if ( validate ) {
			try {
				horizon.validate() ;
			} catch ( ValidationException e ) {
				return false ;
			}
		}

		return true ;
	}

	public static void modelOf( CircleType circle ) throws ValidationException {
		if ( ! modelOf( circle, true ) ) {
			throw new ValidationException( circle.getClass().getSimpleName() ) ;
		}
	}

	public static boolean modelOf( CircleType circle, boolean validate ) {
		Preferences node ;

		node = Configuration.getClassNode( circle, circle.getName(), null ) ;
		circle.setupPeer( node ) ;

		if ( validate ) {
			try {
				circle.validate() ;
			} catch ( ValidationException e ) {
				return false ;
			}
		}

		return true ;
	}

	public static void modelOf( AnnotationType annotation ) throws ValidationException {
		if ( ! modelOf( annotation, true ) ) {
			throw new ValidationException( annotation.getClass().getSimpleName() ) ;
		}
	}

	public static boolean modelOf( AnnotationType annotation, boolean validate ) {
		Preferences node ;

		node = Configuration.getClassNode( annotation, annotation.getName(), null ) ;
		annotation.setupPeer( node ) ;

		if ( validate ) {
			try {
				annotation.validate() ;
			} catch ( ValidationException e ) {
				return false ;
			}
		}

		return true ;
	}

	public static void modelOf( TextType text ) throws ValidationException {
		if ( ! modelOf( text, true ) ) {
			throw new ValidationException( text.getClass().getSimpleName() ) ;
		}
	}

	public static boolean modelOf( TextType text, boolean validate ) {
		Preferences node ;

		node = Configuration.getClassNode( text, text.getName(), null ) ;
		text.setupPeer( node ) ;

		if ( validate ) {
			try {
				text.validate() ;
			} catch ( ValidationException e ) {
				return false ;
			}
		}

		return true ;
	}

	public static void modelOf( BodyStellarType body ) throws ValidationException {
		if ( ! modelOf( body, true ) ) {
			throw new ValidationException( body.getClass().getSimpleName() ) ;
		}
	}

	public static boolean modelOf( BodyStellarType body, boolean validate ) {
		Preferences node ;

		node = Configuration.getClassNode( body, body.getName(), null ) ;
		body.setupPeer( node ) ;

		if ( validate ) {
			try {
				body.validate() ;
			} catch ( ValidationException e ) {
				return false ;
			}
		}

		return true ;
	}

	public static void modelOf( BodyArealType body ) throws ValidationException {
		if ( ! modelOf( body, true ) ) {
			throw new ValidationException( body.getClass().getSimpleName() ) ;
		}
	}

	public static boolean modelOf( BodyArealType body, boolean validate ) {
		Preferences node ;

		node = Configuration.getClassNode( body, body.getName(), null ) ;
		body.setupPeer( node ) ;

		if ( validate ) {
			try {
				body.validate() ;
			} catch ( ValidationException e ) {
				return false ;
			}
		}

		return true ;
	}

	public static double valueOf( DateType date ) {
		double r ;

		if ( date.getJD() == null ) {
			r = valueOf( date.getCalendar() ) ;
		} else {
			r = valueOf( date.getJD() ) ;
		}

		return r ;
	}

	public static double valueOf( CalendarType calendar ) {
		double r ;
		CAADate d ;
		long[] c ;
		double t = 0 ;

		c = valueOf( calendar.getYMD() ) ;

		if ( calendar.getTime() != null ) {
			t = valueOf( calendar.getTime() ) ;
		}

		d = new CAADate( c[0], c[1], c[2]+t, true ) ;
		r = d.Julian() ;
		d.delete() ;

		return r ;
	}

	public static double valueOf( TimeType time ) {
		double r = 0 ;

		if ( time.getRational() == null ) {
			r = AstrolabeFactory.valueOf( time.getHMS() ) ;
		} else {
			r = AstrolabeFactory.valueOf( time.getRational() ) ;
		}

		return r ;
	}

	public static List<double[]> valueOf( SphericalType[] spherical ) {
		List<double[]> r = new java.util.Vector<double[]>() ;

		for ( int n=0 ; n<spherical.length ; n++ ) {
			r.add( valueOf( spherical[n] ) ) ;
		}

		return r ;
	}

	public static double[] valueOf( SphericalType spherical ) {
		double[] r = { 1, 0, 0 } ;

		if ( spherical.getR() != null ) {
			r[0] = valueOf( spherical.getR() ) ;
		}
		r[1] = valueOf( spherical.getPhi() ) ;
		r[2] = valueOf( spherical.getTheta() ) ;

		return r ;
	}

	public static double[] valueOf( PolarType polar ) {
		double[] r = { 1, 0 } ;

		if ( polar.getR() != null ) {
			r[0] = valueOf( polar.getR() ) ;
		}
		r[1] = valueOf( polar.getPhi() ) ;

		return r ;
	}

	public static double[] valueOf( CartesianType cartesian ) {
		return new double[] { cartesian.getX(), cartesian.getY(), cartesian.hasZ()?cartesian.getZ():0 } ;
	}

	public static double valueOf( AngleType angle ) {
		double r ;

		if ( angle.getRational() == null ) {
			if ( angle.getDMS() == null ) {
				r = CAACoordinateTransformation.HoursToDegrees( valueOf( angle.getHMS() ) ) ;
			} else {
				r = valueOf( angle.getDMS() ) ;
			}

		} else {
			r = valueOf( angle.getRational() ) ;
		}

		return r ;
	}

	public static long[] valueOf( YMDType ymd ) {
		return new long[] { ymd.getY(), ymd.getM(), ymd.getD() } ;
	}

	public static double valueOf( DMSType dms ) {
		return dms.getDeg()+dms.getMin()/60.+dms.getSec()/3600 ;
	}

	public static double valueOf( HMSType hms ) {
		return hms.getHrs()+hms.getMin()/60.+hms.getSec()/3600 ;
	}

	public static double valueOf( RationalType rational ) {
		return rational.getValue() ;
	} 
}
