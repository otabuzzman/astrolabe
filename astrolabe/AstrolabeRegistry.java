
package astrolabe;

import java.text.MessageFormat;

import com.vividsolutions.jts.geom.Geometry;

import caa.CAACoordinateTransformation;
import caa.CAADate;

public class AstrolabeRegistry extends Registry {

	public static Object retrieve( String key ) {
		Object r ;

		r = Registry.retrieve( key ) ;

		if ( r == null ) {
			String msg ;

			msg = MessageCatalog.message( ApplicationConstant.GC_APPLICATION, ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
			msg = MessageFormat.format( msg, new Object[] { "\""+key+"\"", "" } ) ;
		}

		return r ;
	}

	public static void registerYMD( String key, CAADate date ) {
		String ind ;
		MessageCatalog m ;

		m = new MessageCatalog( ApplicationConstant.GC_APPLICATION ) ;

		ind = m.message( ApplicationConstant.LK_INDICATOR_YMD_NUMBEROFYEAR ) ;
		registerNumber( key+ind, date.Year() ) ;
		ind = m.message( ApplicationConstant.LK_INDICATOR_YMD_NUMBEROFMONTH ) ;
		registerNumber( key+ind, date.Month() ) ;
		ind = m.message( ApplicationConstant.LK_INDICTAOR_YMD_NUMBEROFDAY ) ;
		registerNumber( key+ind, date.Day() ) ;
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
		registerNumber( key+ind, hDMS.getDeg() ) ;
		ind = m.message( ApplicationConstant.LK_INDICTAOR_HMS_HOURMINUTES ) ;
		registerNumber( key+ind, hDMS.getMin() ) ;
		ind = m.message( ApplicationConstant.LK_INDICTAOR_HMS_HOURSECONDS ) ;
		registerNumber( key+ind, (int) hDMS.getSec() ) ;
		ind = m.message( ApplicationConstant.LK_INDICTAOR_HMS_HOURFRACTION ) ;
		registerNumber( key+ind,
				(int) ( hDMS.getSec()-(int) hDMS.getSec() )*java.lang.Math.pow( 10, hDMS.precision() ) ) ;

		ind = m.message( ApplicationConstant.LK_INDICTAOR_SIG_MATH ) ;
		registerName( key+ind, hDMS.getNeg()?"-":"" ) ;
		ind = m.message( ApplicationConstant.LK_INDICTAOR_SIG_BOTH ) ;
		registerName( key+ind, hDMS.getNeg()?"-":"+" ) ;
	}

	public static void registerDMS( String key, double dms ) {
		DMS dDMS ;
		String ind ;
		MessageCatalog m ;

		dDMS = new DMS( dms ) ;

		m = new MessageCatalog( ApplicationConstant.GC_APPLICATION ) ;

		ind = m.message( ApplicationConstant.LK_INDICTAOR_DMS_DEGREES ) ;
		registerNumber( key+ind, dDMS.getDeg() ) ;
		ind = m.message( ApplicationConstant.LK_INDICTAOR_DMS_DEGREEMINUTES ) ;
		registerNumber( key+ind, dDMS.getMin() ) ;
		ind = m.message( ApplicationConstant.LK_INDICTAOR_DMS_DEGREESECONDS ) ;
		registerNumber( key+ind, (int) dDMS.getSec() ) ;
		ind = m.message( ApplicationConstant.LK_INDICTAOR_DMS_DEGREEFRACTION ) ;
		registerNumber( key+ind,
				(int) ( dDMS.getSec()-(int) dDMS.getSec() )*java.lang.Math.pow( 10, dDMS.precision() ) ) ;

		ind = m.message( ApplicationConstant.LK_INDICTAOR_SIG_MATH ) ;
		registerName( key+ind, dDMS.getNeg()?"-":"" ) ;
		ind = m.message( ApplicationConstant.LK_INDICTAOR_SIG_BOTH ) ;
		registerName( key+ind, dDMS.getNeg()?"-":"+" ) ;
	}

	public static void registerJTSCoordinate( String key, JTSCoordinate jtscoordinate ) {
		String ind ;
		MessageCatalog m ;
		Geometry fov ;

		m = new MessageCatalog( ApplicationConstant.GC_APPLICATION ) ;

		fov = (Geometry) AstrolabeRegistry.retrieve( ApplicationConstant.GC_FOVUNI ) ;

		ind = m.message( ApplicationConstant.LK_INDICTAOR_JTSCOORDINATE_QUADRANT ) ;
		registerNumber( key+ind,
				( jtscoordinate.x >= 0 && jtscoordinate.y >= 0 ) ? 1 :
					( jtscoordinate.x < 0 && jtscoordinate.y >= 0 ) ? 2 :
						( jtscoordinate.x < 0 && jtscoordinate.y < 0 ) ? 3 : 4 ) ;
		ind = m.message( ApplicationConstant.LK_INDICTAOR_JTSCOORDINATE_BOUNDARY ) ;
		registerNumber( key+ind, jtscoordinate.boundary( fov.getEnvelopeInternal() ) ) ;
	}
}
