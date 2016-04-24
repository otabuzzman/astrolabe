
package astrolabe;

public final class ApplicationConstant {

	private ApplicationConstant() {
	}

	// general constants (GC_), patterns (GP_)
	public static final String GC_APPLICATION	= "astrolabe" ;
	public static final String GC_NATLIB_CAA	= "cygcaa-1.17" ;

	// locale substitute keys (LK_), nodes (LN_), leafs (LL_), patterns (LP_)
	public static final String LP_SUBSTITUTE = "@\\{[\\p{Alnum}\\p{L}]*\\}@" ;

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

	public static final String LK_ADC1239H_CATALOG		= "substitute.adc1239h.Catalog" ;
	public static final String LK_ADC1239H_HIP			= "substitute.adc1239h.HIP" ;
	public static final String LK_ADC1239H_PROXY		= "substitute.adc1239h.Proxy" ;
	public static final String LK_ADC1239H_RAHMS		= "substitute.adc1239h.RAhms" ;
	public static final String LK_ADC1239H_DEDMS		= "substitute.adc1239h.DEdms" ;
	public static final String LK_ADC1239H_VMAG			= "substitute.adc1239h.Vmag" ;
	public static final String LK_ADC1239H_VARFLAG		= "substitute.adc1239h.VarFlag" ;
	public static final String LK_ADC1239H_R_VMAG		= "substitute.adc1239h.r_Vmag" ;
	public static final String LK_ADC1239H_RADEG		= "substitute.adc1239h.RAdeg" ;
	public static final String LK_ADC1239H_DEDEG		= "substitute.adc1239h.DEdeg" ;
	public static final String LK_ADC1239H_ASTROREF		= "substitute.adc1239h.AstroRef" ;
	public static final String LK_ADC1239H_PLX			= "substitute.adc1239h.Plx" ;
	public static final String LK_ADC1239H_PMRA			= "substitute.adc1239h.pmRA" ;
	public static final String LK_ADC1239H_PMDE			= "substitute.adc1239h.pmDE" ;
	public static final String LK_ADC1239H_E_RADEG		= "substitute.adc1239h.e_RAdeg" ;
	public static final String LK_ADC1239H_E_DEDEG		= "substitute.adc1239h.e_DEdeg" ;
	public static final String LK_ADC1239H_E_PLX		= "substitute.adc1239h.e_Plx" ;
	public static final String LK_ADC1239H_E_PMRA		= "substitute.adc1239h.e_pmRA" ;
	public static final String LK_ADC1239H_E_PMDE		= "substitute.adc1239h.e_pmDE" ;
	public static final String LK_ADC1239H_DERA			= "substitute.adc1239h.DERA" ;
	public static final String LK_ADC1239H_PLXRA		= "substitute.adc1239h.PlxRA" ;
	public static final String LK_ADC1239H_PLXDE		= "substitute.adc1239h.PlxDE" ;
	public static final String LK_ADC1239H_PMRARA		= "substitute.adc1239h.pmRARA" ;
	public static final String LK_ADC1239H_PMRADE		= "substitute.adc1239h.pmRADE" ;
	public static final String LK_ADC1239H_PMRAPLX		= "substitute.adc1239h.pmRAPlx" ;
	public static final String LK_ADC1239H_PMDERA		= "substitute.adc1239h.pmDERA" ;
	public static final String LK_ADC1239H_PMDEDE		= "substitute.adc1239h.pmDEDE" ;
	public static final String LK_ADC1239H_PMDEPLX		= "substitute.adc1239h.pmDEPlx" ;
	public static final String LK_ADC1239H_PMDEPMRA		= "substitute.adc1239h.pmDEpmRA" ;
	public static final String LK_ADC1239H_F1			= "substitute.adc1239h.F1" ;
	public static final String LK_ADC1239H_F2			= "substitute.adc1239h.F2" ;
	public static final String LK_ADC1239H_BTMAG		= "substitute.adc1239h.BTmag" ;
	public static final String LK_ADC1239H_E_BTMAG		= "substitute.adc1239h.e_BTmag" ;
	public static final String LK_ADC1239H_VTMAG		= "substitute.adc1239h.VTmag" ;
	public static final String LK_ADC1239H_E_VTMAG		= "substitute.adc1239h.e_VTmag" ;
	public static final String LK_ADC1239H_M_BTMAG		= "substitute.adc1239h.m_BTmag" ;
	public static final String LK_ADC1239H_BV			= "substitute.adc1239h.BV" ;
	public static final String LK_ADC1239H_E_BV			= "substitute.adc1239h.e_BV" ;
	public static final String LK_ADC1239H_R_BV			= "substitute.adc1239h.r_BV" ;
	public static final String LK_ADC1239H_VI			= "substitute.adc1239h.VI" ;
	public static final String LK_ADC1239H_E_VI			= "substitute.adc1239h.e_VI" ;
	public static final String LK_ADC1239H_R_VI			= "substitute.adc1239h.r_VI" ;
	public static final String LK_ADC1239H_COMBMAG		= "substitute.adc1239h.CombMag" ;
	public static final String LK_ADC1239H_HPMAG		= "substitute.adc1239h.Hpmag" ;
	public static final String LK_ADC1239H_E_HPMAG		= "substitute.adc1239h.e_Hpmag" ;
	public static final String LK_ADC1239H_HPSCAT		= "substitute.adc1239h.Hpscat" ;
	public static final String LK_ADC1239H_O_HPMAG		= "substitute.adc1239h.o_Hpmag" ;
	public static final String LK_ADC1239H_M_HPMAG		= "substitute.adc1239h.m_Hpmag" ;
	public static final String LK_ADC1239H_HPMAX		= "substitute.adc1239h.Hpmax" ;
	public static final String LK_ADC1239H_HPMIN		= "substitute.adc1239h.HPmin" ;
	public static final String LK_ADC1239H_PERIOD		= "substitute.adc1239h.Period" ;
	public static final String LK_ADC1239H_HVARTYPE		= "substitute.adc1239h.HvarType" ;
	public static final String LK_ADC1239H_MOREVAR		= "substitute.adc1239h.moreVar" ;
	public static final String LK_ADC1239H_MOREPHOTO	= "substitute.adc1239h.morePhoto" ;
	public static final String LK_ADC1239H_CCDM			= "substitute.adc1239h.CCDM" ;
	public static final String LK_ADC1239H_N_CCDM		= "substitute.adc1239h.n_CCDM" ;
	public static final String LK_ADC1239H_NSYS			= "substitute.adc1239h.Nsys" ;
	public static final String LK_ADC1239H_NCOMP		= "substitute.adc1239h.Ncomp" ;
	public static final String LK_ADC1239H_MULTFLAG		= "substitute.adc1239h.MultFlag" ;
	public static final String LK_ADC1239H_SOURCE		= "substitute.adc1239h.Source" ;
	public static final String LK_ADC1239H_QUAL			= "substitute.adc1239h.Qual" ;
	public static final String LK_ADC1239H_M_HIP		= "substitute.adc1239h.m_HIP" ;
	public static final String LK_ADC1239H_THETA		= "substitute.adc1239h.theta" ;
	public static final String LK_ADC1239H_RHO			= "substitute.adc1239h.rho" ;
	public static final String LK_ADC1239H_E_RHO		= "substitute.adc1239h.e_rho" ;
	public static final String LK_ADC1239H_DHP			= "substitute.adc1239h.dHp" ;
	public static final String LK_ADC1239H_E_DHP		= "substitute.adc1239h.e_dHp" ;
	public static final String LK_ADC1239H_SURVEY		= "substitute.adc1239h.Survey" ;
	public static final String LK_ADC1239H_CHART		= "substitute.adc1239h.Chart" ;
	public static final String LK_ADC1239H_NOTES		= "substitute.adc1239h.Notes" ;
	public static final String LK_ADC1239H_HD			= "substitute.adc1239h.HD" ;
	public static final String LK_ADC1239H_BD			= "substitute.adc1239h.BD" ;
	public static final String LK_ADC1239H_COD			= "substitute.adc1239h.CoD" ;
	public static final String LK_ADC1239H_CPD			= "substitute.adc1239h.CPD" ;
	public static final String LK_ADC1239H_VIRED		= "substitute.adc1239h.VIred" ;
	public static final String LK_ADC1239H_SPTYPE		= "substitute.adc1239h.SpType" ;
	public static final String LK_ADC1239H_R_SPTYPE		= "substitute.adc1239h.r_SpType" ;

	public static final String LK_ADC1239T_CATALOG		= "substitute.adc1239t.Catalog" ;
	public static final String LK_ADC1239T_TYC			= "substitute.adc1239t.TYC" ;
	public static final String LK_ADC1239T_PROXY		= "substitute.adc1239t.Proxy" ;
	public static final String LK_ADC1239T_RAHMS		= "substitute.adc1239t.RAhms" ;
	public static final String LK_ADC1239T_DEDMS		= "substitute.adc1239t.DEdms" ;
	public static final String LK_ADC1239T_VMAG			= "substitute.adc1239t.Vmag" ;
	public static final String LK_ADC1239T_R_VMAG		= "substitute.adc1239t.r_Vmag" ;
	public static final String LK_ADC1239T_RADEG		= "substitute.adc1239t.RAdeg" ;
	public static final String LK_ADC1239T_DEDEG		= "substitute.adc1239t.DEdeg" ;
	public static final String LK_ADC1239T_ASTROREF		= "substitute.adc1239t.AstroRef" ;
	public static final String LK_ADC1239T_PLX			= "substitute.adc1239t.Plx" ;
	public static final String LK_ADC1239T_PMRA			= "substitute.adc1239t.pmRA" ;
	public static final String LK_ADC1239T_PMDE			= "substitute.adc1239t.pmDE" ;
	public static final String LK_ADC1239T_E_RADEG		= "substitute.adc1239t.e_RAdeg" ;
	public static final String LK_ADC1239T_E_DEDEG		= "substitute.adc1239t.e_DEdeg" ;
	public static final String LK_ADC1239T_E_PLX		= "substitute.adc1239t.e_Plx" ;
	public static final String LK_ADC1239T_E_PMRA		= "substitute.adc1239t.e_pmRA" ;
	public static final String LK_ADC1239T_E_PMDE		= "substitute.adc1239t.e_pmDE" ;
	public static final String LK_ADC1239T_DERA			= "substitute.adc1239t.DERA" ;
	public static final String LK_ADC1239T_PLXRA		= "substitute.adc1239t.PlxRA" ;
	public static final String LK_ADC1239T_PLXDE		= "substitute.adc1239t.PlxDE" ;
	public static final String LK_ADC1239T_PMRARA		= "substitute.adc1239t.pmRARA" ;
	public static final String LK_ADC1239T_PMRADE		= "substitute.adc1239t.pmRADE" ;
	public static final String LK_ADC1239T_PMRAPLX		= "substitute.adc1239t.pmRAPlx" ;
	public static final String LK_ADC1239T_PMDERA		= "substitute.adc1239t.pmDERA" ;
	public static final String LK_ADC1239T_PMDEDE		= "substitute.adc1239t.pmDEDE" ;
	public static final String LK_ADC1239T_PMDEPLX		= "substitute.adc1239t.pmDEPlx" ;
	public static final String LK_ADC1239T_PMDEPMRA		= "substitute.adc1239t.pmDEpmRA" ;
	public static final String LK_ADC1239T_NASTRO		= "substitute.adc1239t.Nastro" ;
	public static final String LK_ADC1239T_F2			= "substitute.adc1239t.F2" ;
	public static final String LK_ADC1239T_HIP			= "substitute.adc1239t.HIP" ;
	public static final String LK_ADC1239T_BTMAG		= "substitute.adc1239t.BTmag" ;
	public static final String LK_ADC1239T_E_BTMAG		= "substitute.adc1239t.e_BTmag" ;
	public static final String LK_ADC1239T_VTMAG		= "substitute.adc1239t.VTmag" ;
	public static final String LK_ADC1239T_E_VTMAG		= "substitute.adc1239t.e_VTmag" ;
	public static final String LK_ADC1239T_R_BTMAG		= "substitute.adc1239t.r_BTmag" ;
	public static final String LK_ADC1239T_BV			= "substitute.adc1239t.BV" ;
	public static final String LK_ADC1239T_E_BV			= "substitute.adc1239t.e_BV" ;
	public static final String LK_ADC1239T_Q			= "substitute.adc1239t.Q" ;
	public static final String LK_ADC1239T_FS			= "substitute.adc1239t.Fs" ;
	public static final String LK_ADC1239T_SOURCE		= "substitute.adc1239t.Source" ;
	public static final String LK_ADC1239T_NPHOTO		= "substitute.adc1239t.Nphoto" ;
	public static final String LK_ADC1239T_VTSCAT		= "substitute.adc1239t.VTscat" ;
	public static final String LK_ADC1239T_VTMAX		= "substitute.adc1239t.VTmax" ;
	public static final String LK_ADC1239T_VTMIN		= "substitute.adc1239t.VTmin" ;
	public static final String LK_ADC1239T_VAR			= "substitute.adc1239t.Var" ;
	public static final String LK_ADC1239T_VARFLAG		= "substitute.adc1239t.VarFlag" ;
	public static final String LK_ADC1239T_MULTFLAG		= "substitute.adc1239t.MultFlag" ;
	public static final String LK_ADC1239T_MOREPHOTO	= "substitute.adc1239t.morePhoto" ;
	public static final String LK_ADC1239T_M_HIP		= "substitute.adc1239t.m_HIP" ;
	public static final String LK_ADC1239T_PPM			= "substitute.adc1239t.PPM" ;
	public static final String LK_ADC1239T_HD			= "substitute.adc1239t.HD" ;
	public static final String LK_ADC1239T_BD			= "substitute.adc1239t.BD" ;
	public static final String LK_ADC1239T_COD			= "substitute.adc1239t.CoD" ;
	public static final String LK_ADC1239T_CPD			= "substitute.adc1239t.CPD" ;
	public static final String LK_ADC1239T_REMARK		= "substitute.adc1239t.Remark" ;

	public static final String LK_ADC5050_HR			= "substitute.adc5050.HR" ;
	public static final String LK_ADC5050_NAME			= "substitute.adc5050.Name" ;
	public static final String LK_ADC5050_DM			= "substitute.adc5050.DM" ;
	public static final String LK_ADC5050_HD			= "substitute.adc5050.HD" ;
	public static final String LK_ADC5050_SAO			= "substitute.adc5050.SAO" ;
	public static final String LK_ADC5050_FK5			= "substitute.adc5050.FK5" ;
	public static final String LK_ADC5050_IRFLAG		= "substitute.adc5050.IRflag" ;
	public static final String LK_ADC5050_R_IRFLAG		= "substitute.adc5050.r_IRflag" ;
	public static final String LK_ADC5050_MULTIPLE		= "substitute.adc5050.Multiple" ;
	public static final String LK_ADC5050_ADS			= "substitute.adc5050.ADS" ;
	public static final String LK_ADC5050_ADSCOMP		= "substitute.adc5050.ADScomp" ;
	public static final String LK_ADC5050_VARID			= "substitute.adc5050.VarID" ;
	public static final String LK_ADC5050_RAH1900		= "substitute.adc5050.RAh1900" ;
	public static final String LK_ADC5050_RAM1900		= "substitute.adc5050.RAm1900" ;
	public static final String LK_ADC5050_RAS1900		= "substitute.adc5050.RAs1900" ;
	public static final String LK_ADC5050_DE1900		= "substitute.adc5050.DE1900" ;
	public static final String LK_ADC5050_DED1900		= "substitute.adc5050.DEd1900" ;
	public static final String LK_ADC5050_DEM1900		= "substitute.adc5050.DEm1900" ;
	public static final String LK_ADC5050_DES1900		= "substitute.adc5050.DEs1900" ;
	public static final String LK_ADC5050_RAH			= "substitute.adc5050.RAh" ;
	public static final String LK_ADC5050_RAM			= "substitute.adc5050.RAm" ;
	public static final String LK_ADC5050_RAS			= "substitute.adc5050.RAs" ;
	public static final String LK_ADC5050_DE			= "substitute.adc5050.DE" ;
	public static final String LK_ADC5050_DED			= "substitute.adc5050.DEd" ;
	public static final String LK_ADC5050_DEM			= "substitute.adc5050.DEm" ;
	public static final String LK_ADC5050_DES			= "substitute.adc5050.DEs" ;
	public static final String LK_ADC5050_GLON			= "substitute.adc5050.GLON" ;
	public static final String LK_ADC5050_GLAT			= "substitute.adc5050.GLAT" ;
	public static final String LK_ADC5050_VMAG			= "substitute.adc5050.Vmag" ;
	public static final String LK_ADC5050_N_VMAG		= "substitute.adc5050.n_Vmag" ;
	public static final String LK_ADC5050_U_VMAG		= "substitute.adc5050.u_Vmag" ;
	public static final String LK_ADC5050_BV			= "substitute.adc5050.BV" ;
	public static final String LK_ADC5050_U_BV			= "substitute.adc5050.u_BV" ;
	public static final String LK_ADC5050_UB			= "substitute.adc5050.UB" ;
	public static final String LK_ADC5050_U_UB			= "substitute.adc5050.u_UB" ;
	public static final String LK_ADC5050_RI			= "substitute.adc5050.RI" ;
	public static final String LK_ADC5050_N_RI			= "substitute.adc5050.n_RI" ;
	public static final String LK_ADC5050_SPTYPE		= "substitute.adc5050.SpType" ;
	public static final String LK_ADC5050_N_SPTYPE		= "substitute.adc5050.n_SpType" ;
	public static final String LK_ADC5050_PMRA			= "substitute.adc5050.pmRA" ;
	public static final String LK_ADC5050_PMDE			= "substitute.adc5050.pmDE" ;
	public static final String LK_ADC5050_N_PARALLAX	= "substitute.adc5050.n_Parallax" ;
	public static final String LK_ADC5050_PARALLAX		= "substitute.adc5050.Parallax" ;
	public static final String LK_ADC5050_RADVEL		= "substitute.adc5050.RadVel" ;
	public static final String LK_ADC5050_N_RADVEL		= "substitute.adc5050.n_RadVel" ;
	public static final String LK_ADC5050_L_ROTVEL		= "substitute.adc5050.l_RotVel" ;
	public static final String LK_ADC5050_ROTVEL		= "substitute.adc5050.RotVel" ;
	public static final String LK_ADC5050_U_ROTVEL		= "substitute.adc5050.u_RotVel" ;
	public static final String LK_ADC5050_DMAG			= "substitute.adc5050.Dmag" ;
	public static final String LK_ADC5050_SEP			= "substitute.adc5050.Sep" ;
	public static final String LK_ADC5050_MULTID		= "substitute.adc5050.MultID" ;
	public static final String LK_ADC5050_MULTCNT		= "substitute.adc5050.MultCnt" ;
	public static final String LK_ADC5050_NOTEFLAG		= "substitute.adc5050.NoteFlag" ;

	public static final String LK_ADC6049_CONSTELLATION	= "substitute.adc6049.constellation" ;
	public static final String LK_ADC6049_ABBREVIATION	= "substitute.adc6049.latin.abbreviation" ;
	public static final String LK_ADC6049_NOMINATIVE	= "substitute.adc6049.latin.nominative" ;
	public static final String LK_ADC6049_GENITIVE		= "substitute.adc6049.latin.genitive" ;

	public static final String LK_ADC7118_NAME		= "substitute.adc7118.Name" ;
	public static final String LK_ADC7118_TYPE		= "substitute.adc7118.Type" ;
	public static final String LK_ADC7118_RAH		= "substitute.adc7118.RAh" ;
	public static final String LK_ADC7118_RAM		= "substitute.adc7118.RAm" ;
	public static final String LK_ADC7118_DE		= "substitute.adc7118.DE" ;
	public static final String LK_ADC7118_DED		= "substitute.adc7118.DEd" ;
	public static final String LK_ADC7118_DEM		= "substitute.adc7118.DEm" ;
	public static final String LK_ADC7118_SOURCE	= "substitute.adc7118.Source" ;
	public static final String LK_ADC7118_CONST		= "substitute.adc7118.Const" ;
	public static final String LK_ADC7118_L_SIZE	= "substitute.adc7118.l_size" ;
	public static final String LK_ADC7118_SIZE		= "substitute.adc7118.size" ;
	public static final String LK_ADC7118_MAG		= "substitute.adc7118.mag" ;
	public static final String LK_ADC7118_N_MAG		= "substitute.adc7118.n_mag" ;
	public static final String LK_ADC7118_DESC		= "substitute.adc7118.Desc" ;

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

	// locale message keys (LK_), nodes (LN_), leafs (LL_), patterns (LP_)
	public static final String LK_MESSAGE_PARAMETERNOTAVLID = "message.parameternotvalid" ;

	public static final String LK_CALENDAR_GREGORIAN_MONTH_JANUARY		= "message.calendar.gregorian.month.january" ;
	public static final String LK_CALENDAR_GREGORIAN_MONTH_FEBRUARY		= "message.calendar.gregorian.month.february" ;
	public static final String LK_CALENDAR_GREGORIAN_MONTH_MARCH		= "message.calendar.gregorian.month.march" ;
	public static final String LK_CALENDAR_GREGORIAN_MONTH_APRIL		= "message.calendar.gregorian.month.april" ;
	public static final String LK_CALENDAR_GREGORIAN_MONTH_MAY			= "message.calendar.gregorian.month.may" ;
	public static final String LK_CALENDAR_GREGORIAN_MONTH_JUNE			= "message.calendar.gregorian.month.june" ;
	public static final String LK_CALENDAR_GREGORIAN_MONTH_JULY			= "message.calendar.gregorian.month.july" ;
	public static final String LK_CALENDAR_GREGORIAN_MONTH_AUGUST		= "message.calendar.gregorian.month.august" ;
	public static final String LK_CALENDAR_GREGORIAN_MONTH_SEPTEMBER	= "message.calendar.gregorian.month.september" ;
	public static final String LK_CALENDAR_GREGORIAN_MONTH_OCTOBER		= "message.calendar.gregorian.month.october" ;
	public static final String LK_CALENDAR_GREGORIAN_MONTH_NOVEMBER		= "message.calendar.gregorian.month.november" ;
	public static final String LK_CALENDAR_GREGORIAN_MONTH_DECEMBER		= "message.calendar.gregorian.month.december" ;

	public static final String LK_CALENDAR_GREGORIAN_WEEKDAY_MONDAY		= "message.calendar.gregorian.weekday.monday" ;
	public static final String LK_CALENDAR_GREGORIAN_WEEKDAY_TUESDAY	= "message.calendar.gregorian.weekday.tuesday" ;
	public static final String LK_CALENDAR_GREGORIAN_WEEKDAY_WEDNESDAY	= "message.calendar.gregorian.weekday.wednesday" ;
	public static final String LK_CALENDAR_GREGORIAN_WEEKDAY_THURSDAY	= "message.calendar.gregorian.weekday.thursday" ;
	public static final String LK_CALENDAR_GREGORIAN_WEEKDAY_FRIDAY		= "message.calendar.gregorian.weekday.friday" ;
	public static final String LK_CALENDAR_GREGORIAN_WEEKDAY_SATURDAY	= "message.calendar.gregorian.weekday.saturday" ;
	public static final String LK_CALENDAR_GREGORIAN_WEEKDAY_SUNDAY		= "message.calendar.gregorian.weekday.sunday" ;

	public static final String LK_CALENDAR_GREGORIAN_MONTH_SHORT_JANUARY		= "message.calendar.gregorian.month.short.january" ;
	public static final String LK_CALENDAR_GREGORIAN_MONTH_SHORT_FEBRUARY		= "message.calendar.gregorian.month.short.february" ;
	public static final String LK_CALENDAR_GREGORIAN_MONTH_SHORT_MARCH			= "message.calendar.gregorian.month.short.march" ;
	public static final String LK_CALENDAR_GREGORIAN_MONTH_SHORT_APRIL			= "message.calendar.gregorian.month.short.april" ;
	public static final String LK_CALENDAR_GREGORIAN_MONTH_SHORT_MAY			= "message.calendar.gregorian.month.short.may" ;
	public static final String LK_CALENDAR_GREGORIAN_MONTH_SHORT_JUNE			= "message.calendar.gregorian.month.short.june" ;
	public static final String LK_CALENDAR_GREGORIAN_MONTH_SHORT_JULY			= "message.calendar.gregorian.month.short.july" ;
	public static final String LK_CALENDAR_GREGORIAN_MONTH_SHORT_AUGUST			= "message.calendar.gregorian.month.short.august" ;
	public static final String LK_CALENDAR_GREGORIAN_MONTH_SHORT_SEPTEMBER		= "message.calendar.gregorian.month.short.september" ;
	public static final String LK_CALENDAR_GREGORIAN_MONTH_SHORT_OCTOBER		= "message.calendar.gregorian.month.short.october" ;
	public static final String LK_CALENDAR_GREGORIAN_MONTH_SHORT_NOVEMBER		= "message.calendar.gregorian.month.short.november" ;
	public static final String LK_CALENDAR_GREGORIAN_MONTH_SHORT_DECEMBER		= "message.calendar.gregorian.month.short.december" ;

	public static final String LK_CALENDAR_GREGORIAN_WEEKDAY_SHORT_MONDAY		= "message.calendar.gregorian.weekday.short.monday" ;
	public static final String LK_CALENDAR_GREGORIAN_WEEKDAY_SHORT_TUESDAY		= "message.calendar.gregorian.weekday.short.tuesday" ;
	public static final String LK_CALENDAR_GREGORIAN_WEEKDAY_SHORT_WEDNESDAY	= "message.calendar.gregorian.weekday.short.wednesday" ;
	public static final String LK_CALENDAR_GREGORIAN_WEEKDAY_SHORT_THURSDAY		= "message.calendar.gregorian.weekday.short.thursday" ;
	public static final String LK_CALENDAR_GREGORIAN_WEEKDAY_SHORT_FRIDAY		= "message.calendar.gregorian.weekday.short.friday" ;
	public static final String LK_CALENDAR_GREGORIAN_WEEKDAY_SHORT_SATURDAY		= "message.calendar.gregorian.weekday.short.saturday" ;
	public static final String LK_CALENDAR_GREGORIAN_WEEKDAY_SHORT_SUNDAY		= "message.calendar.gregorian.weekday.short.sunday" ;

	// {0} value of con from catalog record
	public static final String LP_ADC6049_CONSTELLATION	= "message.adc6049.{0}.constellation" ;
	public static final String LP_ADC6049_ABBREVIATION	= "message.adc6049.{0}.latin.abbreviation" ;
	public static final String LP_ADC6049_NOMINATIVE	= "message.adc6049.{0}.latin.nominative" ;
	public static final String LP_ADC6049_GENITIVE		= "message.adc6049.{0}.latin.genitive" ;

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
