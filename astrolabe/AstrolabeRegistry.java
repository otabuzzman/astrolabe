
package astrolabe;

import com.vividsolutions.jts.geom.Geometry;

import caa.CAACoordinateTransformation;
import caa.CAADate;

public class AstrolabeRegistry extends Registry {

	public static void registerYMD( String key, CAADate date ) {
		String ind ;
		MessageCatalog m ;

		m = new MessageCatalog( ApplicationConstant.GC_APPLICATION ) ;

		ind = m.message( ApplicationConstant.LK_INDICATOR_YMD_NUMBEROFYEAR ) ;
		AstrolabeRegistry.registerNumber( key+ind, date.Year() ) ;
		ind = m.message( ApplicationConstant.LK_INDICATOR_YMD_NUMBEROFMONTH ) ;
		AstrolabeRegistry.registerNumber( key+ind, date.Month() ) ;
		ind = m.message( ApplicationConstant.LK_INDICTAOR_YMD_NUMBEROFDAY ) ;
		AstrolabeRegistry.registerNumber( key+ind, date.Day() ) ;
	}

	public static void registerHMS( String key, double hms ) {
		double h ;
		DMS hDMS ;
		String ind ;
		MessageCatalog m ;

		h = CAACoordinateTransformation.DegreesToHours( hms ) ;
		hDMS = new DMS( h ) ;

		m = new MessageCatalog( ApplicationConstant.GC_APPLICATION ) ;

		ind = m.message( ApplicationConstant.LK_INDICTAOR_HMS_HOURS ) ;
		AstrolabeRegistry.registerNumber( key+ind, hDMS.getDeg() ) ;
		ind = m.message( ApplicationConstant.LK_INDICTAOR_HMS_HOURMINUTES ) ;
		AstrolabeRegistry.registerNumber( key+ind, hDMS.getMin() ) ;
		ind = m.message( ApplicationConstant.LK_INDICTAOR_HMS_HOURSECONDS ) ;
		AstrolabeRegistry.registerNumber( key+ind, (int) hDMS.getSec() ) ;
		ind = m.message( ApplicationConstant.LK_INDICTAOR_HMS_HOURFRACTION ) ;
		AstrolabeRegistry.registerNumber( key+ind,
				(int) ( hDMS.getSec()-(int) hDMS.getSec() )*java.lang.Math.pow( 10, hDMS.precision() ) ) ;

		ind = m.message( ApplicationConstant.LK_INDICTAOR_SIG_MATH ) ;
		AstrolabeRegistry.registerName( key+ind, hDMS.getNeg()?"-":"" ) ;
		ind = m.message( ApplicationConstant.LK_INDICTAOR_SIG_BOTH ) ;
		AstrolabeRegistry.registerName( key+ind, hDMS.getNeg()?"-":"+" ) ;
	}

	public static void registerDMS( String key, double dms ) {
		DMS dDMS ;
		String ind ;
		MessageCatalog m ;

		dDMS = new DMS( dms ) ;

		m = new MessageCatalog( ApplicationConstant.GC_APPLICATION ) ;

		ind = m.message( ApplicationConstant.LK_INDICTAOR_DMS_DEGREES ) ;
		AstrolabeRegistry.registerNumber( key+ind, dDMS.getDeg() ) ;
		ind = m.message( ApplicationConstant.LK_INDICTAOR_DMS_DEGREEMINUTES ) ;
		AstrolabeRegistry.registerNumber( key+ind, dDMS.getMin() ) ;
		ind = m.message( ApplicationConstant.LK_INDICTAOR_DMS_DEGREESECONDS ) ;
		AstrolabeRegistry.registerNumber( key+ind, (int) dDMS.getSec() ) ;
		ind = m.message( ApplicationConstant.LK_INDICTAOR_DMS_DEGREEFRACTION ) ;
		AstrolabeRegistry.registerNumber( key+ind,
				(int) ( dDMS.getSec()-(int) dDMS.getSec() )*java.lang.Math.pow( 10, dDMS.precision() ) ) ;

		ind = m.message( ApplicationConstant.LK_INDICTAOR_SIG_MATH ) ;
		AstrolabeRegistry.registerName( key+ind, dDMS.getNeg()?"-":"" ) ;
		ind = m.message( ApplicationConstant.LK_INDICTAOR_SIG_BOTH ) ;
		AstrolabeRegistry.registerName( key+ind, dDMS.getNeg()?"-":"+" ) ;
	}

	public static void registerJTSCoordinate( String key, JTSCoordinate jtscoordinate ) {
		String ind ;
		MessageCatalog m ;
		Geometry fov ;

		m = new MessageCatalog( ApplicationConstant.GC_APPLICATION ) ;

		fov = (Geometry) Registry.retrieve( ApplicationConstant.GC_FOVUNI ) ;

		ind = m.message( ApplicationConstant.LK_INDICTAOR_JTSCOORDINATE_QUADRANT ) ;
		AstrolabeRegistry.registerNumber( key+ind,
				( jtscoordinate.x >= 0 && jtscoordinate.y >= 0 ) ? 1 :
					( jtscoordinate.x < 0 && jtscoordinate.y >= 0 ) ? 2 :
						( jtscoordinate.x < 0 && jtscoordinate.y < 0 ) ? 3 : 4 ) ;
		ind = m.message( ApplicationConstant.LK_INDICTAOR_JTSCOORDINATE_BOUNDARY ) ;
		AstrolabeRegistry.registerNumber( key+ind, jtscoordinate.boundary( fov.getEnvelopeInternal() ) ) ;
	}

	public static void registerNumber( String key, double value, int precision ) {
		double p, v ;

		p = java.lang.Math.pow( 10, precision ) ;
		v = (long) ( ( value*p+.5 ) )/p ;

		registerNumber( key, v ) ;
	}

	public static void registerNumber( String key, double value ) {
		Registry.register( key, (Object) new Double( value ) ) ;
	}

	public static void registerNumber( String key, long value ) {
		Registry.register( key, (Object) new Long( value ) ) ;
	}

	public static void registerName( String key, String value ) {
		Registry.register( key, new String( value ) ) ;
	}
}
