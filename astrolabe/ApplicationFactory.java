
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

public final class ApplicationFactory {

	// configuration key (CK_), node (CN_)
	private final static String CN_DEFAULT = "default" ;

	private ApplicationFactory() {
	}

	public static PostscriptEmitter companionOf( astrolabe.model.Chart chart ) {
		ChartStereographic cCS ;
		ChartOrthographic cCO ;
		ChartEquidistant cCE ;
		ChartGnomonic cCG ;
		PostscriptEmitter pse ;

		if ( chart.getChartStereographic() != null ) {
			cCS = new ChartStereographic() ;
			chart.getChartStereographic().setupCompanion( cCS ) ;

			pse = cCS ;
		} else if ( chart.getChartOrthographic() != null ) {
			cCO = new ChartOrthographic() ;
			chart.getChartStereographic().setupCompanion( cCO ) ;

			pse = cCO ;
		} else if (  chart.getChartEquidistant() != null ) {
			cCE = new ChartEquidistant() ;
			chart.getChartStereographic().setupCompanion( cCE ) ;

			pse = cCE ;
		} else { // chart.getChartGnomonic() != null
			cCG = new ChartGnomonic() ;
			chart.getChartStereographic().setupCompanion( cCG ) ;

			pse = cCG ;
		}

		return pse ;
	}

	public static PostscriptEmitter companionOf( astrolabe.model.Horizon horizon, Projector projector ) {
		HorizonLocal hLo ;
		HorizonEquatorial hEq ;
		HorizonEcliptical hEc ;
		HorizonGalactic hGa ;
		PostscriptEmitter pse ;

		if ( horizon.getHorizonLocal() != null  ) {
			hLo = new HorizonLocal( projector ) ;
			horizon.getHorizonLocal().setupCompanion( hLo ) ;
			hLo.register() ;

			pse = hLo ;
		} else if ( horizon.getHorizonEquatorial() != null  ) {
			hEq = new HorizonEquatorial( projector ) ;
			horizon.getHorizonEquatorial().setupCompanion( hEq ) ;

			pse = hEq ;
		} else if ( horizon.getHorizonEcliptical() != null  ) {
			hEc = new HorizonEcliptical( projector ) ;
			horizon.getHorizonEcliptical().setupCompanion( hEc ) ;
			hEc.register() ;

			pse = hEc ;
		} else { // horizon.getHorizonGalactic() != null
			hGa = new HorizonGalactic( projector ) ;
			horizon.getHorizonGalactic().setupCompanion( hGa ) ;
			hGa.register() ;

			pse = hGa ;
		}

		return pse ;
	}

	public static PostscriptEmitter companionOf( astrolabe.model.Circle circle, Projector projector ) {
		CircleParallel cPl ;
		CircleMeridian cMn ;
		CircleSouthernPolar cSP ;
		CircleNorthernPolar cNP ;
		CircleSouthernTropic cST ;
		CircleNorthernTropic cNT ;
		PostscriptEmitter pse ;

		if ( circle.getCircleParallel() != null ) {
			cPl = new CircleParallel( projector ) ;
			circle.getCircleParallel().setupCompanion( cPl ) ;
			cPl.register() ;

			pse = cPl ;
		} else if ( circle.getCircleMeridian() != null ) {
			cMn = new CircleMeridian( projector ) ;
			circle.getCircleMeridian().setupCompanion( cMn ) ;
			cMn.register() ;

			pse = cMn ;
		} else if ( circle.getCircleSouthernPolar() != null ) {
			cSP = new CircleSouthernPolar( projector ) ;
			circle.getCircleSouthernPolar().setupCompanion( cSP ) ;
			cSP.register() ;

			pse = cSP ;
		} else if ( circle.getCircleNorthernPolar() != null ) {
			cNP = new CircleNorthernPolar( projector ) ;
			circle.getCircleNorthernPolar().setupCompanion( cNP ) ;
			cNP.register() ;

			pse = cNP ;
		} else if (  circle.getCircleSouthernTropic() != null ) {
			cST = new CircleSouthernTropic( projector ) ;
			circle.getCircleSouthernTropic().setupCompanion( cST ) ;
			cST.register() ;

			pse = cST ;
		} else { // circle.getCircleNorthernTropic() != null
			cNT = new CircleNorthernTropic( projector ) ;
			circle.getCircleNorthernTropic().setupCompanion( cNT ) ;
			cNT.register() ;

			pse = cNT ;
		}

		return pse ;
	}

	public static PostscriptEmitter companionOf( astrolabe.model.Dial dial, Baseline baseline ) {
		DialDegree dD ;
		DialHour dH ;
		PostscriptEmitter pse ;

		if ( dial.getDialDegree() != null ) {
			dD = new DialDegree( baseline ) ;
			dial.getDialDegree().setupCompanion( dD ) ;

			pse = dD ;
		} else { // dial.getDialHour() != null
			dH = new DialHour( baseline ) ;
			dial.getDialHour().setupCompanion( dH ) ;

			pse = dH ;
		}

		return pse ;
	}

	public static PostscriptEmitter companionOf( astrolabe.model.Annotation annotation ) {
		AnnotationStraight aS ;
		AnnotationCurved aC ;
		PostscriptEmitter pse ;

		if ( annotation.getAnnotationStraight() != null ) {
			aS = new AnnotationStraight() ;
			annotation.getAnnotationStraight().setupCompanion( aS ) ;
			aS.register() ;

			pse = aS ;
		} else { // annotation.getAnnotationCurved() != null
			aC = new AnnotationCurved() ;
			annotation.getAnnotationCurved().setupCompanion( aC ) ;
			aC.register() ;

			pse = aC ;
		}

		return pse ;
	}

	public static PostscriptEmitter companionOf( astrolabe.model.Body body, Projector projector ) {
		BodyStellar cBSr ;
		BodyAreal cBAl ;
		BodyPlanet cBPt ;
		BodyMoon cBMn ;
		BodySun cBSn ;
		BodyElliptical cBEl ;
		BodyParabolical cBPl ;
		PostscriptEmitter pse ;

		if ( body.getBodyStellar() != null ) {
			cBSr = new BodyStellar( projector ) ;
			body.getBodyStellar().setupCompanion( cBSr ) ;
			cBSr.register() ;

			pse = cBSr ;
		} else if ( body.getBodyAreal() != null ) {
			cBAl = new BodyAreal( projector ) ;
			body.getBodyAreal().setupCompanion( cBAl ) ;
			cBAl.register() ;

			pse = cBAl ;
		} else if ( body.getBodyPlanet() != null ) {
			cBPt = new BodyPlanet( projector ) ;
			body.getBodyPlanet().setupCompanion( cBPt ) ;

			pse = cBPt ;
		} else if ( body.getBodyMoon() != null ) {
			cBMn = new BodyMoon( projector ) ;
			body.getBodyMoon().setupCompanion( cBMn ) ;

			pse = cBMn ;
		} else if ( body.getBodySun() != null ) {
			cBSn = new BodySun( projector ) ;
			body.getBodySun().setupCompanion( cBSn ) ;

			pse = cBSn ;
		} else if ( body.getBodyElliptical() != null ) {
			cBEl = new BodyElliptical( projector ) ;
			body.getBodyElliptical().setupCompanion( cBEl ) ;

			pse = cBEl ;
		} else { // body.getBodyParabolical() != null
			cBPl = new BodyParabolical( projector ) ;
			body.getBodyParabolical().setupCompanion( cBPl ) ;

			pse = cBPl ;
		}

		return pse ;
	}

	public static Catalog companionOf( astrolabe.model.Catalog catalog, Projector projector ) {
		CatalogADC1239H c1239h ;
		CatalogADC1239T c1239t ;
		CatalogADC5050 c5050 ;
		CatalogADC5109 c5109 ;
		CatalogADC6049 c6049 ;
		CatalogADC7118 c7118 ;
		CatalogADC7237 c7237 ;
		CatalogDS9 cDS9 ;

		if ( catalog.getCatalogADC1239H() != null ) {
			c1239h = new CatalogADC1239H( projector ) ;
			catalog.getCatalogADC1239H().setupCompanion( c1239h ) ;
			c1239h.register() ;

			return c1239h ;
		} else if (  catalog.getCatalogADC1239T() != null ) {
			c1239t = new CatalogADC1239T( projector ) ;
			catalog.getCatalogADC1239T().setupCompanion( c1239t ) ;
			c1239t.register() ;

			return c1239t ;
		} else if ( catalog.getCatalogADC5050() != null ) {
			c5050 = new CatalogADC5050( projector ) ;
			catalog.getCatalogADC5050().setupCompanion( c5050 ) ;
			c5050.register() ;

			return c5050 ;
		} else if (  catalog.getCatalogADC5109() != null ) {
			c5109 = new CatalogADC5109( projector ) ;
			catalog.getCatalogADC5109().setupCompanion( c5109 ) ;
			c5109.register() ;

			return c5109 ;
		} else if ( catalog.getCatalogADC6049() != null ) {
			c6049 = new CatalogADC6049( projector ) ;
			catalog.getCatalogADC6049().setupCompanion( c6049 ) ;
			c6049.register() ;

			return c6049 ;
		} else if ( catalog.getCatalogADC7118() != null ) {
			c7118 = new CatalogADC7118( projector ) ;
			catalog.getCatalogADC7118().setupCompanion( c7118 ) ;
			c7118.register() ;

			return c7118 ;
		} else if ( catalog.getCatalogADC7237() != null ) {
			c7237 = new CatalogADC7237( projector ) ;
			catalog.getCatalogADC7237().setupCompanion( c7237 ) ;
			c7237.register() ;

			return c7237 ;
		} else { // catalog.getCatalogDS9() != null
			cDS9 = new CatalogDS9( projector ) ;
			catalog.getCatalogDS9().setupCompanion( cDS9 ) ;
			cDS9.register() ;

			return cDS9 ;
		}
	}

	public static void modelOf( HorizonType horizon ) throws ValidationException {
		if ( ! modelOf( horizon, true ) ) {
			throw new ValidationException( horizon.getClass().getSimpleName() ) ;
		}
	}

	public static boolean modelOf( HorizonType horizon, boolean validate ) {
		Preferences node ;

		node = Configuration.getNode( horizon, CN_DEFAULT ) ;
		if ( node == null )
			return false ;

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

		node = Configuration.getNode( circle, CN_DEFAULT ) ;
		if ( node == null )
			return false ;

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

		node = Configuration.getNode( annotation, CN_DEFAULT ) ;
		if ( node == null )
			return false ;

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

		node = Configuration.getNode( text, CN_DEFAULT ) ;
		if ( node == null )
			return false ;

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

		node = Configuration.getNode( body, CN_DEFAULT ) ;
		if ( node == null )
			return false ;

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

		node = Configuration.getNode( body, CN_DEFAULT ) ;
		if ( node == null )
			return false ;

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
			r = ApplicationFactory.valueOf( time.getHMS() ) ;
		} else {
			r = ApplicationFactory.valueOf( time.getRational() ) ;
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
		double r ;

		r = dms.getDeg()+dms.getMin()/60.+dms.getSec()/3600 ;

		return dms.getNeg()?-r:r ;
	}

	public static double valueOf( HMSType hms ) {
		double r ;

		r = hms.getHrs()+hms.getMin()/60.+hms.getSec()/3600 ;

		return hms.getNeg()?-r:r ;
	}

	public static double valueOf( RationalType rational ) {
		return rational.getValue() ;
	} 
}
