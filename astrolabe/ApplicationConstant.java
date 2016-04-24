
package astrolabe;

public final class ApplicationConstant {

	private ApplicationConstant() {
	}

	// general constants (GC_), patterns (GP_)
	public static final String GC_APPLICATION	= "astrolabe" ;
	public static final String GC_NATLIB_CAA	= "cygcaa-1.17" ;

	// locale keys (LK_), nodes (LN_), leafs (LL_), patterns (LP_)
	public static final String LK_ASTROLABE_EPOCH = "substitue.astrolabe.epoch" ;

	public static final String LK_HORIZON_LATITUDE			= "substitute.horizon.latitude" ;
	public static final String LK_HORIZON_LONGITUDE			= "substitute.horizon.longitude" ;
	public static final String LK_HORIZON_TIMELOCAL			= "substitute.horizon.timelocal" ;
	public static final String LK_HORIZON_TIMESIDEREAL		= "substitute.horizon.timesidereal" ;
	public static final String LK_HORIZON_ECLIPTICEPSILON	= "substitute.horizon.eclipticepsilon" ;

	public static final String LK_CIRCLE_AZIMUTH	= "substitute.circle.azimuth" ;
	public static final String LK_CIRCLE_ALTITUDE	= "substitute.circle.altitude" ;

	public static final String LK_BODY_AZIMUTH			= "substitute.body.azimuth" ;
	public static final String LK_BODY_ALTITUDE			= "substitute.body.altitude" ;
	public static final String LK_BODY_RIGHTASCENSION	= "substitute.body.rightascension" ;
	public static final String LK_BODY_DECLINATION		= "substitute.body.declination" ;
	public static final String LK_BODY_STERADIAN		= "substitute.body.steradian" ;
	public static final String LK_BODY_SQUAREDEGREE		= "substitute.body.squaredegree" ;

	public static final String LK_DIAL_DEGREE			= "substitute.dial.degree" ;
	public static final String LK_DIAL_HOUR				= "substitute.dial.hour" ;
	public static final String LK_DIAL_AZIMUTHTIME		= "substitute.dial.hour.azimuthtime" ;
	public static final String LK_DIAL_DAY				= "substitute.dial.day" ;
	public static final String LK_DIAL_NAMEOFDAY		= "substitute.dial.day.nameofday" ;
	public static final String LK_DIAL_NAMEOFDAYSHORT	= "substitute.dial.day.nameofdayshort" ;
	public static final String LK_DIAL_NAMEOFMONTH		= "substitute.dial.day.nameofmonth" ;
	public static final String LK_DIAL_NAMEOFMONTHSHORT	= "substitute.dial.day.nameofmonthshort" ;
	public static final String LK_DIAL_JULIANDAY		= "substitute.dial.day.julianday" ;
	public static final String LK_DIAL_EQUATIONOFTIME	= "substitute.dial.day.equationoftime" ;

	public static final String LK_ADC5050_FLAMSTEED		= "substitute.adc5050.flamsteed" ;
	public static final String LK_ADC5050_BAYER			= "substitute.adc5050.bayer" ;
	public static final String LK_ADC5050_BAYERINDEX	= "substitute.adc5050.bayerindex" ;
	public static final String LK_ADC5050_CONSTELLATION	= "substitute.adc5050.constellation" ;

	public static final String LK_ADC6049_CONSTELLATION	= "substitute.adc6049.constellation" ;
	public static final String LK_ADC6049_ABBREVIATION	= "substitute.adc6049.abbreviation" ;
	public static final String LK_ADC6049_NOMINATIVE	= "substitute.adc6049.nominative" ;
	public static final String LK_ADC6049_GENITIVE		= "substitute.adc6049.genitive" ;

	public static final String LN_ADC6049 = "adc6049" ;

	public static final String LP_SUBSTITUTE = "@\\{[\\p{Alnum}\\p{L}]*\\}@" ;

	public static final String LN_CALENDAR_LONG		= "calendar" ;
	public static final String LN_CALENDAR_SHORT	= "calendar.short" ;

	public static final String LL_JANUARY	= ".january" ;
	public static final String LL_FEBRUARY	= ".february" ;
	public static final String LL_MARCH		= ".march" ;
	public static final String LL_APRIL		= ".april" ;
	public static final String LL_MAY		= ".may" ;
	public static final String LL_JUNE		= ".june" ;
	public static final String LL_JULY		= ".july" ;
	public static final String LL_AUGUST	= ".august" ;
	public static final String LL_SEPTEMBER	= ".september" ;
	public static final String LL_OCTOBER	= ".october" ;
	public static final String LL_NOVEMBER	= ".november" ;
	public static final String LL_DECEMBER	= ".december" ;

	public static final String LL_MONDAY	= ".monday" ;
	public static final String LL_TUESDAY	= ".tuesday" ;
	public static final String LL_WEDNESDAY	= ".wednesday" ;
	public static final String LL_THURSDAY	= ".thursday" ;
	public static final String LL_FRIDAY	= ".friday" ;
	public static final String LL_SATURDAY	= ".saturday" ;
	public static final String LL_SUNDAY	= ".sunday" ;

	public static final String LK_YMD_NUMBEROFYEAR	= "substitute.indicator.ymd.numberofyear" ;
	public static final String LK_YMD_NUMBEROFMONTH	= "substitute.indicator.ymd.numberofmonth" ;
	public static final String LK_YMD_NUMBEROFDAY	= "substitute.indicator.ymd.numberofday" ;

	public static final String LK_HMS_HOURS		= "substitute.indicator.hms.hours" ;
	public static final String LK_HMS_MSPREFIX	= "substitute.indicator.hms.msprefix" ;

	public static final String LK_DMS_DEGREES	= "substitute.indicator.dms.degrees" ;
	public static final String LK_DMS_MSPREFIX	= "substitute.indicator.dms.msprefix" ;

	public static final String LK_MS_MINUTES	= "substitute.indicator.ms.minutes" ;
	public static final String LK_MS_SECONDS	= "substitute.indicator.ms.seconds" ;
	public static final String LK_MS_FRACTION	= "substitute.indicator.ms.fraction" ;

	// messages
	public static final String LK_MESSAGE_PARAMETERNOTAVLID = "message.parameternotvalid" ;

	// model values (AV_), patterns (AP_)
	public static final String AV_CHART_NORTHERN = "northern" ;
	public static final String AV_CHART_SOUTHERN = "southern" ;

	public static final String AV_CIRCLE_CHASING = "chasing" ;
	public static final String AV_CIRCLE_LEADING = "leading" ;

	public static final String AV_ANNOTATION_TOPLEFT		= "topleft" ;
	public static final String AV_ANNOTATION_TOPMIDDLE		= "topmiddle" ;
	public static final String AV_ANNOTATION_TOPRIGHT		= "topright" ;
	public static final String AV_ANNOTATION_MIDDLELEFT		= "middleleft" ;
	public static final String AV_ANNOTATION_MIDDLE			= "middle" ;
	public static final String AV_ANNOTATION_MIDDLERIGHT	= "middleright" ;
	public static final String AV_ANNOTATION_BOTTOMLEFT		= "bottomleft" ;
	public static final String AV_ANNOTATION_BOTTOMMIDDLE	= "bottommiddle" ;
	public static final String AV_ANNOTATION_BOTTOMRIGHT	= "bottomright" ;

	public static final String AV_BODY_CONSTELLATION = "constellation" ;

	// preferences keys (PK_), nodes (PN_)
	public static final String PK_CHART_UNIT		= "unit" ;
	public static final String PN_CHART_PAGESIZE	= "pagesize" ;

	public static final String PN_HORIZON_PRACTICALITY = "practicality" ;

	public static final String PK_CIRCLE_SEGMENT	= "segment" ;
	public static final String PN_CIRCLE_IMPORTANCE	= "importance" ;

	public static final String PN_DIAL_ANNOTATION	= "annotation" ;
	public static final String PK_DIAL_RISE			= "rise" ;
	public static final String PN_DIAL_BASELINE		= "baseline" ;
	public static final String PK_DIAL_SPACE		= "space" ;
	public static final String PK_DIAL_THICKNESS	= "thickness" ;
	public static final String PK_DIAL_LINEWIDTH	= "linewidth" ;

	public static final String PK_GRADUATION_SPACE		= "space" ;
	public static final String PK_GRADUATION_LINELENGTH	= "linelength" ;
	public static final String PK_GRADUATION_LINEWIDTH	= "linewidth" ;

	public static final String PK_ANNOTATION_SUBSCRIPTSHIFT		= "subscriptshift" ;
	public static final String PK_ANNOTATION_SUPERSCRIPTSHIFT	= "superscriptshift" ;
	public static final String PK_ANNOTATION_SUBSCRIPTSHRINK	= "subscriptshrink" ;
	public static final String PK_ANNOTATION_SUPERSCRIPTSHRINK	= "superscriptshrink" ;
	public static final String PK_ANNOTATION_MARGIN				= "margin" ;
	public static final String PK_ANNOTATION_RISE				= "rise" ;
	public static final String PN_ANNOTATION_PURPOSE			= "purpose" ;

	public static final String PK_BODY_SEGMENT		= "segment" ;
	public static final String PK_BODY_STRETCH		= "stretch" ;
	public static final String PK_BODY_LINEWIDTH	= "linewidth" ;
	public static final String PK_BODY_LINEDASH		= "linedash" ;

	public static final String PK_POSTSCRIPT_PRECISION	= "precision" ;
	public static final String PK_POSTSCRIPT_SCANLINE	= "scanline" ;
	public static final String PN_POSTSCRIPT_TYPE3		= "type3" ;
	public static final String PN_POSTSCRIPT_PROLOG		= "prolog" ;
	public static final String PN_POSTSCRIPT_UNICODE	= "unicode" ;
	public static final String PK_POSTSCRIPT_DEFAULT	= "default" ;

	public static final String PK_PRINTSTREAM_VIEWER	= "viewer" ;

	// postscript prolog definitions
	public static final String PS_PROLOG_ACOS			= "acos" ;
	public static final String PS_PROLOG_ASIN			= "asin" ;
	public static final String PS_PROLOG_LINE			= "line" ;
	public static final String PS_PROLOG_LIM0			= "lim0" ;
	public static final String PS_PROLOG_LISTREDUCE		= "listreduce" ;
	public static final String PS_PROLOG_MATROT90CC		= "matrot90cc" ;
	public static final String PS_PROLOG_MATROT90C		= "matrot90c" ;
	public static final String PS_PROLOG_MATROT180CC	= "matrot180cc" ;
	public static final String PS_PROLOG_MATROT180C		= "matrot180c" ;
	public static final String PS_PROLOG_MATROTARB		= "matrotarb" ;
	public static final String PS_PROLOG_MOD			= "mod" ;
	public static final String PS_PROLOG_PAGESIZE		= "pagesize" ;
	public static final String PS_PROLOG_PATHLENGTH		= "pathlength" ;
	public static final String PS_PROLOG_PATHREVERSE	= "pathreverse" ;
	public static final String PS_PROLOG_PATHSHIFT		= "pathshift" ;
	public static final String PS_PROLOG_PATHSHOW		= "pathshow" ;
	public static final String PS_PROLOG_POLYLINE		= "polyline" ;
	public static final String PS_PROLOG_SETENCODING	= "setencoding" ;
	public static final String PS_PROLOG_TRUNCATEF		= "truncatef" ;
	public static final String PS_PROLOG_VABS			= "vabs" ;
	public static final String PS_PROLOG_VADD			= "vadd" ;
	public static final String PS_PROLOG_VANGLE			= "vangle" ;
	public static final String PS_PROLOG_VAPPLY			= "vapply" ;
	public static final String PS_PROLOG_VDOT			= "vdot" ;
	public static final String PS_PROLOG_VMUL			= "vmul" ;
	public static final String PS_PROLOG_VSCALE			= "vscale" ;
	public static final String PS_PROLOG_VSUB			= "vsub" ;
}
