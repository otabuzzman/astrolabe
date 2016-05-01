
package astrolabe;

public final class ApplicationConstant {

	private ApplicationConstant() {
	}

	// general constants (GC_), patterns (GP_)
	public final static String GC_APPLICATION	= "astrolabe" ;		// application name
	public final static String GC_NATLIB_CAA	= "cygcaa-1.17" ;	// native library
	public final static String GC_NS_FOVG		= "fovg:" ;			// name space, application generated
	public final static String GC_NS_FOVE		= "fove:" ;			// name space, application generated
	public final static String GC_NS_CUT		= "cut:" ;			// name space, application generated
	public final static String GC_NS_ATL		= "atl:" ;			// name space, application generated
	public final static String GC_EPOCHE		= "epoch" ;			// class global model variable
	public final static String GC_LAYOUT		= "layout" ;		// class global model variable

	// locale substitute keys (LK_), nodes (LN_), leafs (LL_), patterns (LP_)
	public final static String LP_SUBSTITUTE = "@\\{[\\p{Alnum}\\p{L}]*\\}@" ;

	public final static String LK_ASTROLABE_EPOCH = "substitue.astrolabe.epoch" ;

	public final static String LK_HORIZON_LATITUDE			= "substitute.horizon.latitude" ;
	public final static String LK_HORIZON_LONGITUDE			= "substitute.horizon.longitude" ;
	public final static String LK_HORIZON_TIMELOCAL			= "substitute.horizon.timelocal" ;
	public final static String LK_HORIZON_TIMESIDEREAL		= "substitute.horizon.timesidereal" ;
	public final static String LK_HORIZON_ECLIPTICEPSILON	= "substitute.horizon.eclipticepsilon" ;

	public final static String LK_CIRCLE_AZIMUTH	= "substitute.circle.azimuth" ;
	public final static String LK_CIRCLE_ALTITUDE	= "substitute.circle.altitude" ;

	public final static String LK_BODY_AZIMUTH			= "substitute.body.azimuth" ;
	public final static String LK_BODY_ALTITUDE			= "substitute.body.altitude" ;
	public final static String LK_BODY_RIGHTASCENSION	= "substitute.body.rightascension" ;
	public final static String LK_BODY_DECLINATION		= "substitute.body.declination" ;
	public final static String LK_BODY_STERADIAN		= "substitute.body.steradian" ;
	public final static String LK_BODY_SQUAREDEGREE		= "substitute.body.squaredegree" ;

	public final static String LK_DIAL_DEGREE			= "substitute.dial.degree" ;
	public final static String LK_DIAL_HOUR				= "substitute.dial.hour" ;
	public final static String LK_DIAL_AZIMUTHTIME		= "substitute.dial.hour.azimuthtime" ;
	public final static String LK_DIAL_DAY				= "substitute.dial.day" ;
	public final static String LK_DIAL_NAMEOFDAY		= "substitute.dial.day.nameofday" ;
	public final static String LK_DIAL_NAMEOFDAYSHORT	= "substitute.dial.day.nameofdayshort" ;
	public final static String LK_DIAL_NAMEOFMONTH		= "substitute.dial.day.nameofmonth" ;
	public final static String LK_DIAL_NAMEOFMONTHSHORT	= "substitute.dial.day.nameofmonthshort" ;
	public final static String LK_DIAL_JULIANDAY		= "substitute.dial.day.julianday" ;
	public final static String LK_DIAL_EQUATIONOFTIME	= "substitute.dial.day.equationoftime" ;

	public final static String LK_ADC1239H_CATALOG		= "substitute.adc1239h.Catalog" ;
	public final static String LK_ADC1239H_HIP			= "substitute.adc1239h.HIP" ;
	public final static String LK_ADC1239H_PROXY		= "substitute.adc1239h.Proxy" ;
	public final static String LK_ADC1239H_RAHMS		= "substitute.adc1239h.RAhms" ;
	public final static String LK_ADC1239H_DEDMS		= "substitute.adc1239h.DEdms" ;
	public final static String LK_ADC1239H_VMAG			= "substitute.adc1239h.Vmag" ;
	public final static String LK_ADC1239H_VARFLAG		= "substitute.adc1239h.VarFlag" ;
	public final static String LK_ADC1239H_R_VMAG		= "substitute.adc1239h.r_Vmag" ;
	public final static String LK_ADC1239H_RADEG		= "substitute.adc1239h.RAdeg" ;
	public final static String LK_ADC1239H_DEDEG		= "substitute.adc1239h.DEdeg" ;
	public final static String LK_ADC1239H_ASTROREF		= "substitute.adc1239h.AstroRef" ;
	public final static String LK_ADC1239H_PLX			= "substitute.adc1239h.Plx" ;
	public final static String LK_ADC1239H_PMRA			= "substitute.adc1239h.pmRA" ;
	public final static String LK_ADC1239H_PMDE			= "substitute.adc1239h.pmDE" ;
	public final static String LK_ADC1239H_E_RADEG		= "substitute.adc1239h.e_RAdeg" ;
	public final static String LK_ADC1239H_E_DEDEG		= "substitute.adc1239h.e_DEdeg" ;
	public final static String LK_ADC1239H_E_PLX		= "substitute.adc1239h.e_Plx" ;
	public final static String LK_ADC1239H_E_PMRA		= "substitute.adc1239h.e_pmRA" ;
	public final static String LK_ADC1239H_E_PMDE		= "substitute.adc1239h.e_pmDE" ;
	public final static String LK_ADC1239H_DERA			= "substitute.adc1239h.DERA" ;
	public final static String LK_ADC1239H_PLXRA		= "substitute.adc1239h.PlxRA" ;
	public final static String LK_ADC1239H_PLXDE		= "substitute.adc1239h.PlxDE" ;
	public final static String LK_ADC1239H_PMRARA		= "substitute.adc1239h.pmRARA" ;
	public final static String LK_ADC1239H_PMRADE		= "substitute.adc1239h.pmRADE" ;
	public final static String LK_ADC1239H_PMRAPLX		= "substitute.adc1239h.pmRAPlx" ;
	public final static String LK_ADC1239H_PMDERA		= "substitute.adc1239h.pmDERA" ;
	public final static String LK_ADC1239H_PMDEDE		= "substitute.adc1239h.pmDEDE" ;
	public final static String LK_ADC1239H_PMDEPLX		= "substitute.adc1239h.pmDEPlx" ;
	public final static String LK_ADC1239H_PMDEPMRA		= "substitute.adc1239h.pmDEpmRA" ;
	public final static String LK_ADC1239H_F1			= "substitute.adc1239h.F1" ;
	public final static String LK_ADC1239H_F2			= "substitute.adc1239h.F2" ;
	public final static String LK_ADC1239H_BTMAG		= "substitute.adc1239h.BTmag" ;
	public final static String LK_ADC1239H_E_BTMAG		= "substitute.adc1239h.e_BTmag" ;
	public final static String LK_ADC1239H_VTMAG		= "substitute.adc1239h.VTmag" ;
	public final static String LK_ADC1239H_E_VTMAG		= "substitute.adc1239h.e_VTmag" ;
	public final static String LK_ADC1239H_M_BTMAG		= "substitute.adc1239h.m_BTmag" ;
	public final static String LK_ADC1239H_BV			= "substitute.adc1239h.BV" ;
	public final static String LK_ADC1239H_E_BV			= "substitute.adc1239h.e_BV" ;
	public final static String LK_ADC1239H_R_BV			= "substitute.adc1239h.r_BV" ;
	public final static String LK_ADC1239H_VI			= "substitute.adc1239h.VI" ;
	public final static String LK_ADC1239H_E_VI			= "substitute.adc1239h.e_VI" ;
	public final static String LK_ADC1239H_R_VI			= "substitute.adc1239h.r_VI" ;
	public final static String LK_ADC1239H_COMBMAG		= "substitute.adc1239h.CombMag" ;
	public final static String LK_ADC1239H_HPMAG		= "substitute.adc1239h.Hpmag" ;
	public final static String LK_ADC1239H_E_HPMAG		= "substitute.adc1239h.e_Hpmag" ;
	public final static String LK_ADC1239H_HPSCAT		= "substitute.adc1239h.Hpscat" ;
	public final static String LK_ADC1239H_O_HPMAG		= "substitute.adc1239h.o_Hpmag" ;
	public final static String LK_ADC1239H_M_HPMAG		= "substitute.adc1239h.m_Hpmag" ;
	public final static String LK_ADC1239H_HPMAX		= "substitute.adc1239h.Hpmax" ;
	public final static String LK_ADC1239H_HPMIN		= "substitute.adc1239h.HPmin" ;
	public final static String LK_ADC1239H_PERIOD		= "substitute.adc1239h.Period" ;
	public final static String LK_ADC1239H_HVARTYPE		= "substitute.adc1239h.HvarType" ;
	public final static String LK_ADC1239H_MOREVAR		= "substitute.adc1239h.moreVar" ;
	public final static String LK_ADC1239H_MOREPHOTO	= "substitute.adc1239h.morePhoto" ;
	public final static String LK_ADC1239H_CCDM			= "substitute.adc1239h.CCDM" ;
	public final static String LK_ADC1239H_N_CCDM		= "substitute.adc1239h.n_CCDM" ;
	public final static String LK_ADC1239H_NSYS			= "substitute.adc1239h.Nsys" ;
	public final static String LK_ADC1239H_NCOMP		= "substitute.adc1239h.Ncomp" ;
	public final static String LK_ADC1239H_MULTFLAG		= "substitute.adc1239h.MultFlag" ;
	public final static String LK_ADC1239H_SOURCE		= "substitute.adc1239h.Source" ;
	public final static String LK_ADC1239H_QUAL			= "substitute.adc1239h.Qual" ;
	public final static String LK_ADC1239H_M_HIP		= "substitute.adc1239h.m_HIP" ;
	public final static String LK_ADC1239H_THETA		= "substitute.adc1239h.theta" ;
	public final static String LK_ADC1239H_RHO			= "substitute.adc1239h.rho" ;
	public final static String LK_ADC1239H_E_RHO		= "substitute.adc1239h.e_rho" ;
	public final static String LK_ADC1239H_DHP			= "substitute.adc1239h.dHp" ;
	public final static String LK_ADC1239H_E_DHP		= "substitute.adc1239h.e_dHp" ;
	public final static String LK_ADC1239H_SURVEY		= "substitute.adc1239h.Survey" ;
	public final static String LK_ADC1239H_CHART		= "substitute.adc1239h.Chart" ;
	public final static String LK_ADC1239H_NOTES		= "substitute.adc1239h.Notes" ;
	public final static String LK_ADC1239H_HD			= "substitute.adc1239h.HD" ;
	public final static String LK_ADC1239H_BD			= "substitute.adc1239h.BD" ;
	public final static String LK_ADC1239H_COD			= "substitute.adc1239h.CoD" ;
	public final static String LK_ADC1239H_CPD			= "substitute.adc1239h.CPD" ;
	public final static String LK_ADC1239H_VIRED		= "substitute.adc1239h.VIred" ;
	public final static String LK_ADC1239H_SPTYPE		= "substitute.adc1239h.SpType" ;
	public final static String LK_ADC1239H_R_SPTYPE		= "substitute.adc1239h.r_SpType" ;

	public final static String LK_ADC1239T_CATALOG		= "substitute.adc1239t.Catalog" ;
	public final static String LK_ADC1239T_TYC			= "substitute.adc1239t.TYC" ;
	public final static String LK_ADC1239T_PROXY		= "substitute.adc1239t.Proxy" ;
	public final static String LK_ADC1239T_RAHMS		= "substitute.adc1239t.RAhms" ;
	public final static String LK_ADC1239T_DEDMS		= "substitute.adc1239t.DEdms" ;
	public final static String LK_ADC1239T_VMAG			= "substitute.adc1239t.Vmag" ;
	public final static String LK_ADC1239T_R_VMAG		= "substitute.adc1239t.r_Vmag" ;
	public final static String LK_ADC1239T_RADEG		= "substitute.adc1239t.RAdeg" ;
	public final static String LK_ADC1239T_DEDEG		= "substitute.adc1239t.DEdeg" ;
	public final static String LK_ADC1239T_ASTROREF		= "substitute.adc1239t.AstroRef" ;
	public final static String LK_ADC1239T_PLX			= "substitute.adc1239t.Plx" ;
	public final static String LK_ADC1239T_PMRA			= "substitute.adc1239t.pmRA" ;
	public final static String LK_ADC1239T_PMDE			= "substitute.adc1239t.pmDE" ;
	public final static String LK_ADC1239T_E_RADEG		= "substitute.adc1239t.e_RAdeg" ;
	public final static String LK_ADC1239T_E_DEDEG		= "substitute.adc1239t.e_DEdeg" ;
	public final static String LK_ADC1239T_E_PLX		= "substitute.adc1239t.e_Plx" ;
	public final static String LK_ADC1239T_E_PMRA		= "substitute.adc1239t.e_pmRA" ;
	public final static String LK_ADC1239T_E_PMDE		= "substitute.adc1239t.e_pmDE" ;
	public final static String LK_ADC1239T_DERA			= "substitute.adc1239t.DERA" ;
	public final static String LK_ADC1239T_PLXRA		= "substitute.adc1239t.PlxRA" ;
	public final static String LK_ADC1239T_PLXDE		= "substitute.adc1239t.PlxDE" ;
	public final static String LK_ADC1239T_PMRARA		= "substitute.adc1239t.pmRARA" ;
	public final static String LK_ADC1239T_PMRADE		= "substitute.adc1239t.pmRADE" ;
	public final static String LK_ADC1239T_PMRAPLX		= "substitute.adc1239t.pmRAPlx" ;
	public final static String LK_ADC1239T_PMDERA		= "substitute.adc1239t.pmDERA" ;
	public final static String LK_ADC1239T_PMDEDE		= "substitute.adc1239t.pmDEDE" ;
	public final static String LK_ADC1239T_PMDEPLX		= "substitute.adc1239t.pmDEPlx" ;
	public final static String LK_ADC1239T_PMDEPMRA		= "substitute.adc1239t.pmDEpmRA" ;
	public final static String LK_ADC1239T_NASTRO		= "substitute.adc1239t.Nastro" ;
	public final static String LK_ADC1239T_F2			= "substitute.adc1239t.F2" ;
	public final static String LK_ADC1239T_HIP			= "substitute.adc1239t.HIP" ;
	public final static String LK_ADC1239T_BTMAG		= "substitute.adc1239t.BTmag" ;
	public final static String LK_ADC1239T_E_BTMAG		= "substitute.adc1239t.e_BTmag" ;
	public final static String LK_ADC1239T_VTMAG		= "substitute.adc1239t.VTmag" ;
	public final static String LK_ADC1239T_E_VTMAG		= "substitute.adc1239t.e_VTmag" ;
	public final static String LK_ADC1239T_R_BTMAG		= "substitute.adc1239t.r_BTmag" ;
	public final static String LK_ADC1239T_BV			= "substitute.adc1239t.BV" ;
	public final static String LK_ADC1239T_E_BV			= "substitute.adc1239t.e_BV" ;
	public final static String LK_ADC1239T_Q			= "substitute.adc1239t.Q" ;
	public final static String LK_ADC1239T_FS			= "substitute.adc1239t.Fs" ;
	public final static String LK_ADC1239T_SOURCE		= "substitute.adc1239t.Source" ;
	public final static String LK_ADC1239T_NPHOTO		= "substitute.adc1239t.Nphoto" ;
	public final static String LK_ADC1239T_VTSCAT		= "substitute.adc1239t.VTscat" ;
	public final static String LK_ADC1239T_VTMAX		= "substitute.adc1239t.VTmax" ;
	public final static String LK_ADC1239T_VTMIN		= "substitute.adc1239t.VTmin" ;
	public final static String LK_ADC1239T_VAR			= "substitute.adc1239t.Var" ;
	public final static String LK_ADC1239T_VARFLAG		= "substitute.adc1239t.VarFlag" ;
	public final static String LK_ADC1239T_MULTFLAG		= "substitute.adc1239t.MultFlag" ;
	public final static String LK_ADC1239T_MOREPHOTO	= "substitute.adc1239t.morePhoto" ;
	public final static String LK_ADC1239T_M_HIP		= "substitute.adc1239t.m_HIP" ;
	public final static String LK_ADC1239T_PPM			= "substitute.adc1239t.PPM" ;
	public final static String LK_ADC1239T_HD			= "substitute.adc1239t.HD" ;
	public final static String LK_ADC1239T_BD			= "substitute.adc1239t.BD" ;
	public final static String LK_ADC1239T_COD			= "substitute.adc1239t.CoD" ;
	public final static String LK_ADC1239T_CPD			= "substitute.adc1239t.CPD" ;
	public final static String LK_ADC1239T_REMARK		= "substitute.adc1239t.Remark" ;

	public final static String LK_ADC5050_HR			= "substitute.adc5050.HR" ;
	public final static String LK_ADC5050_NAME			= "substitute.adc5050.Name" ;
	public final static String LK_ADC5050_DM			= "substitute.adc5050.DM" ;
	public final static String LK_ADC5050_HD			= "substitute.adc5050.HD" ;
	public final static String LK_ADC5050_SAO			= "substitute.adc5050.SAO" ;
	public final static String LK_ADC5050_FK5			= "substitute.adc5050.FK5" ;
	public final static String LK_ADC5050_IRFLAG		= "substitute.adc5050.IRflag" ;
	public final static String LK_ADC5050_R_IRFLAG		= "substitute.adc5050.r_IRflag" ;
	public final static String LK_ADC5050_MULTIPLE		= "substitute.adc5050.Multiple" ;
	public final static String LK_ADC5050_ADS			= "substitute.adc5050.ADS" ;
	public final static String LK_ADC5050_ADSCOMP		= "substitute.adc5050.ADScomp" ;
	public final static String LK_ADC5050_VARID			= "substitute.adc5050.VarID" ;
	public final static String LK_ADC5050_RAH1900		= "substitute.adc5050.RAh1900" ;
	public final static String LK_ADC5050_RAM1900		= "substitute.adc5050.RAm1900" ;
	public final static String LK_ADC5050_RAS1900		= "substitute.adc5050.RAs1900" ;
	public final static String LK_ADC5050_DE1900		= "substitute.adc5050.DE1900" ;
	public final static String LK_ADC5050_DED1900		= "substitute.adc5050.DEd1900" ;
	public final static String LK_ADC5050_DEM1900		= "substitute.adc5050.DEm1900" ;
	public final static String LK_ADC5050_DES1900		= "substitute.adc5050.DEs1900" ;
	public final static String LK_ADC5050_RAH			= "substitute.adc5050.RAh" ;
	public final static String LK_ADC5050_RAM			= "substitute.adc5050.RAm" ;
	public final static String LK_ADC5050_RAS			= "substitute.adc5050.RAs" ;
	public final static String LK_ADC5050_DE			= "substitute.adc5050.DE" ;
	public final static String LK_ADC5050_DED			= "substitute.adc5050.DEd" ;
	public final static String LK_ADC5050_DEM			= "substitute.adc5050.DEm" ;
	public final static String LK_ADC5050_DES			= "substitute.adc5050.DEs" ;
	public final static String LK_ADC5050_GLON			= "substitute.adc5050.GLON" ;
	public final static String LK_ADC5050_GLAT			= "substitute.adc5050.GLAT" ;
	public final static String LK_ADC5050_VMAG			= "substitute.adc5050.Vmag" ;
	public final static String LK_ADC5050_N_VMAG		= "substitute.adc5050.n_Vmag" ;
	public final static String LK_ADC5050_U_VMAG		= "substitute.adc5050.u_Vmag" ;
	public final static String LK_ADC5050_BV			= "substitute.adc5050.BV" ;
	public final static String LK_ADC5050_U_BV			= "substitute.adc5050.u_BV" ;
	public final static String LK_ADC5050_UB			= "substitute.adc5050.UB" ;
	public final static String LK_ADC5050_U_UB			= "substitute.adc5050.u_UB" ;
	public final static String LK_ADC5050_RI			= "substitute.adc5050.RI" ;
	public final static String LK_ADC5050_N_RI			= "substitute.adc5050.n_RI" ;
	public final static String LK_ADC5050_SPTYPE		= "substitute.adc5050.SpType" ;
	public final static String LK_ADC5050_N_SPTYPE		= "substitute.adc5050.n_SpType" ;
	public final static String LK_ADC5050_PMRA			= "substitute.adc5050.pmRA" ;
	public final static String LK_ADC5050_PMDE			= "substitute.adc5050.pmDE" ;
	public final static String LK_ADC5050_N_PARALLAX	= "substitute.adc5050.n_Parallax" ;
	public final static String LK_ADC5050_PARALLAX		= "substitute.adc5050.Parallax" ;
	public final static String LK_ADC5050_RADVEL		= "substitute.adc5050.RadVel" ;
	public final static String LK_ADC5050_N_RADVEL		= "substitute.adc5050.n_RadVel" ;
	public final static String LK_ADC5050_L_ROTVEL		= "substitute.adc5050.l_RotVel" ;
	public final static String LK_ADC5050_ROTVEL		= "substitute.adc5050.RotVel" ;
	public final static String LK_ADC5050_U_ROTVEL		= "substitute.adc5050.u_RotVel" ;
	public final static String LK_ADC5050_DMAG			= "substitute.adc5050.Dmag" ;
	public final static String LK_ADC5050_SEP			= "substitute.adc5050.Sep" ;
	public final static String LK_ADC5050_MULTID		= "substitute.adc5050.MultID" ;
	public final static String LK_ADC5050_MULTCNT		= "substitute.adc5050.MultCnt" ;
	public final static String LK_ADC5050_NOTEFLAG		= "substitute.adc5050.NoteFlag" ;

	public final static String LK_ADC6049_CONSTELLATION	= "substitute.adc6049.constellation" ;
	public final static String LK_ADC6049_ABBREVIATION	= "substitute.adc6049.latin.abbreviation" ;
	public final static String LK_ADC6049_NOMINATIVE	= "substitute.adc6049.latin.nominative" ;
	public final static String LK_ADC6049_GENITIVE		= "substitute.adc6049.latin.genitive" ;

	public final static String LK_ADC7118_NAME		= "substitute.adc7118.Name" ;
	public final static String LK_ADC7118_TYPE		= "substitute.adc7118.Type" ;
	public final static String LK_ADC7118_RAH		= "substitute.adc7118.RAh" ;
	public final static String LK_ADC7118_RAM		= "substitute.adc7118.RAm" ;
	public final static String LK_ADC7118_DE		= "substitute.adc7118.DE" ;
	public final static String LK_ADC7118_DED		= "substitute.adc7118.DEd" ;
	public final static String LK_ADC7118_DEM		= "substitute.adc7118.DEm" ;
	public final static String LK_ADC7118_SOURCE	= "substitute.adc7118.Source" ;
	public final static String LK_ADC7118_CONST		= "substitute.adc7118.Const" ;
	public final static String LK_ADC7118_L_SIZE	= "substitute.adc7118.l_size" ;
	public final static String LK_ADC7118_SIZE		= "substitute.adc7118.size" ;
	public final static String LK_ADC7118_MAG		= "substitute.adc7118.mag" ;
	public final static String LK_ADC7118_N_MAG		= "substitute.adc7118.n_mag" ;
	public final static String LK_ADC7118_DESC		= "substitute.adc7118.Desc" ;

	public final static String LK_YMD_NUMBEROFYEAR	= "substitute.indicator.ymd.numberofyear" ;
	public final static String LK_YMD_NUMBEROFMONTH	= "substitute.indicator.ymd.numberofmonth" ;
	public final static String LK_YMD_NUMBEROFDAY	= "substitute.indicator.ymd.numberofday" ;

	public final static String LK_HMS_HOURS			= "substitute.indicator.hms.hours" ;
	public final static String LK_HMS_HOURMINUTES	= "substitute.indicator.hms.hourminutes" ;
	public final static String LK_HMS_HOURSECONDS	= "substitute.indicator.hms.hourseconds" ;
	public final static String LK_HMS_HOURFRACTION	= "substitute.indicator.hms.hourfraction" ;

	public final static String LK_DMS_DEGREES			= "substitute.indicator.dms.degrees" ;
	public final static String LK_DMS_DEGREEMINUTES		= "substitute.indicator.dms.degreeminutes" ;
	public final static String LK_DMS_DEGREESECONDS		= "substitute.indicator.dms.degreeseconds" ;
	public final static String LK_DMS_DEGREEFRACTION	= "substitute.indicator.dms.degreefraction" ;

	public final static String LK_SIG_MATH	= "substitute.indicator.sig.math" ;
	public final static String LK_SIG_BOTH	= "substitute.indicator.sig.both" ;

	// locale annotation keys (LK_)
	public final static String LK_TEXT_HMS_HOURS	= "annotation.text.hms.hours" ;
	public final static String LK_TEXT_HMS_MINUTES	= "annotation.text.hms.minutes" ;
	public final static String LK_TEXT_HMS_SECONDS	= "annotation.text.hms.seconds" ;

	public final static String LK_TEXT_DMS_DEGREES	= "annotation.text.dms.degrees" ;
	public final static String LK_TEXT_DMS_MINUTES	= "annotation.text.dms.minutes" ;
	public final static String LK_TEXT_DMS_SECONDS	= "annotation.text.dms.seconds" ;

	// locale message keys (LK_), nodes (LN_), leafs (LL_), patterns (LP_)
	public final static String LK_MESSAGE_PARAMETERNOTAVLID = "message.parameternotvalid" ;

	public final static String LK_CALENDAR_GREGORIAN_MONTH_JANUARY		= "message.calendar.gregorian.month.january" ;
	public final static String LK_CALENDAR_GREGORIAN_MONTH_FEBRUARY		= "message.calendar.gregorian.month.february" ;
	public final static String LK_CALENDAR_GREGORIAN_MONTH_MARCH		= "message.calendar.gregorian.month.march" ;
	public final static String LK_CALENDAR_GREGORIAN_MONTH_APRIL		= "message.calendar.gregorian.month.april" ;
	public final static String LK_CALENDAR_GREGORIAN_MONTH_MAY			= "message.calendar.gregorian.month.may" ;
	public final static String LK_CALENDAR_GREGORIAN_MONTH_JUNE			= "message.calendar.gregorian.month.june" ;
	public final static String LK_CALENDAR_GREGORIAN_MONTH_JULY			= "message.calendar.gregorian.month.july" ;
	public final static String LK_CALENDAR_GREGORIAN_MONTH_AUGUST		= "message.calendar.gregorian.month.august" ;
	public final static String LK_CALENDAR_GREGORIAN_MONTH_SEPTEMBER	= "message.calendar.gregorian.month.september" ;
	public final static String LK_CALENDAR_GREGORIAN_MONTH_OCTOBER		= "message.calendar.gregorian.month.october" ;
	public final static String LK_CALENDAR_GREGORIAN_MONTH_NOVEMBER		= "message.calendar.gregorian.month.november" ;
	public final static String LK_CALENDAR_GREGORIAN_MONTH_DECEMBER		= "message.calendar.gregorian.month.december" ;

	public final static String LK_CALENDAR_GREGORIAN_WEEKDAY_MONDAY		= "message.calendar.gregorian.weekday.monday" ;
	public final static String LK_CALENDAR_GREGORIAN_WEEKDAY_TUESDAY	= "message.calendar.gregorian.weekday.tuesday" ;
	public final static String LK_CALENDAR_GREGORIAN_WEEKDAY_WEDNESDAY	= "message.calendar.gregorian.weekday.wednesday" ;
	public final static String LK_CALENDAR_GREGORIAN_WEEKDAY_THURSDAY	= "message.calendar.gregorian.weekday.thursday" ;
	public final static String LK_CALENDAR_GREGORIAN_WEEKDAY_FRIDAY		= "message.calendar.gregorian.weekday.friday" ;
	public final static String LK_CALENDAR_GREGORIAN_WEEKDAY_SATURDAY	= "message.calendar.gregorian.weekday.saturday" ;
	public final static String LK_CALENDAR_GREGORIAN_WEEKDAY_SUNDAY		= "message.calendar.gregorian.weekday.sunday" ;

	public final static String LK_CALENDAR_GREGORIAN_MONTH_SHORT_JANUARY		= "message.calendar.gregorian.month.short.january" ;
	public final static String LK_CALENDAR_GREGORIAN_MONTH_SHORT_FEBRUARY		= "message.calendar.gregorian.month.short.february" ;
	public final static String LK_CALENDAR_GREGORIAN_MONTH_SHORT_MARCH			= "message.calendar.gregorian.month.short.march" ;
	public final static String LK_CALENDAR_GREGORIAN_MONTH_SHORT_APRIL			= "message.calendar.gregorian.month.short.april" ;
	public final static String LK_CALENDAR_GREGORIAN_MONTH_SHORT_MAY			= "message.calendar.gregorian.month.short.may" ;
	public final static String LK_CALENDAR_GREGORIAN_MONTH_SHORT_JUNE			= "message.calendar.gregorian.month.short.june" ;
	public final static String LK_CALENDAR_GREGORIAN_MONTH_SHORT_JULY			= "message.calendar.gregorian.month.short.july" ;
	public final static String LK_CALENDAR_GREGORIAN_MONTH_SHORT_AUGUST			= "message.calendar.gregorian.month.short.august" ;
	public final static String LK_CALENDAR_GREGORIAN_MONTH_SHORT_SEPTEMBER		= "message.calendar.gregorian.month.short.september" ;
	public final static String LK_CALENDAR_GREGORIAN_MONTH_SHORT_OCTOBER		= "message.calendar.gregorian.month.short.october" ;
	public final static String LK_CALENDAR_GREGORIAN_MONTH_SHORT_NOVEMBER		= "message.calendar.gregorian.month.short.november" ;
	public final static String LK_CALENDAR_GREGORIAN_MONTH_SHORT_DECEMBER		= "message.calendar.gregorian.month.short.december" ;

	public final static String LK_CALENDAR_GREGORIAN_WEEKDAY_SHORT_MONDAY		= "message.calendar.gregorian.weekday.short.monday" ;
	public final static String LK_CALENDAR_GREGORIAN_WEEKDAY_SHORT_TUESDAY		= "message.calendar.gregorian.weekday.short.tuesday" ;
	public final static String LK_CALENDAR_GREGORIAN_WEEKDAY_SHORT_WEDNESDAY	= "message.calendar.gregorian.weekday.short.wednesday" ;
	public final static String LK_CALENDAR_GREGORIAN_WEEKDAY_SHORT_THURSDAY		= "message.calendar.gregorian.weekday.short.thursday" ;
	public final static String LK_CALENDAR_GREGORIAN_WEEKDAY_SHORT_FRIDAY		= "message.calendar.gregorian.weekday.short.friday" ;
	public final static String LK_CALENDAR_GREGORIAN_WEEKDAY_SHORT_SATURDAY		= "message.calendar.gregorian.weekday.short.saturday" ;
	public final static String LK_CALENDAR_GREGORIAN_WEEKDAY_SHORT_SUNDAY		= "message.calendar.gregorian.weekday.short.sunday" ;

	// {0} value of con from catalog record
	public final static String LP_ADC6049_CONSTELLATION	= "message.adc6049.{0}.constellation" ;
	public final static String LP_ADC6049_ABBREVIATION	= "message.adc6049.{0}.latin.abbreviation" ;
	public final static String LP_ADC6049_NOMINATIVE	= "message.adc6049.{0}.latin.nominative" ;
	public final static String LP_ADC6049_GENITIVE		= "message.adc6049.{0}.latin.genitive" ;

	// attribute values (AV_), patterns (AP_)
	public final static String AV_CHART_NORTHERN = "northern" ;
	public final static String AV_CHART_SOUTHERN = "southern" ;

	public final static String AV_CIRCLE_CHASING = "chasing" ;
	public final static String AV_CIRCLE_LEADING = "leading" ;

	public final static String AV_ANNOTATION_TOPLEFT		= "topleft" ;
	public final static String AV_ANNOTATION_TOPMIDDLE		= "topmiddle" ;
	public final static String AV_ANNOTATION_TOPRIGHT		= "topright" ;
	public final static String AV_ANNOTATION_MIDDLELEFT		= "middleleft" ;
	public final static String AV_ANNOTATION_MIDDLE			= "middle" ;
	public final static String AV_ANNOTATION_MIDDLERIGHT	= "middleright" ;
	public final static String AV_ANNOTATION_BOTTOMLEFT		= "bottomleft" ;
	public final static String AV_ANNOTATION_BOTTOMMIDDLE	= "bottommiddle" ;
	public final static String AV_ANNOTATION_BOTTOMRIGHT	= "bottomright" ;

	public final static String AV_BODY_AREA = "area" ;
	public final static String AV_BODY_SIGN = "sign" ;

	// preferences keys (PK_), nodes (PN_)
	public final static String PN_GENERAL_PRACTICALITY	= "practicality" ;
	public final static String PN_GENERAL_IMPORTANCE	= "importance" ;

	public final static String PK_GENERAL_PRACTICALITY	= PN_GENERAL_PRACTICALITY ;
	public final static String PK_GENERAL_IMPORTANCE	= PN_GENERAL_IMPORTANCE ;
	public final static String PK_GENERAL_RISE			= "rise" ;
	public final static String PK_GENERAL_SPACE			= "space" ;
	public final static String PK_GENERAL_INTERVAL		= "interval" ;
	public final static String PK_GENERAL_LINEWIDTH		= "linewidth" ;
	public final static String PK_GENERAL_PRECISION		= "precision" ;

	public final static String PK_CHART_UNIT			= "unit" ;
	public final static String PK_CHART_HALO			= "halo" ;
	public final static String PK_CHART_HALOMIN			= "halomin" ;
	public final static String PK_CHART_HALOMAX			= "halomax" ;
	public final static String PK_CHART_PRACTICALITY	= PK_GENERAL_PRACTICALITY ;
	public final static String PK_CHART_IMPORTANCE		= PK_GENERAL_IMPORTANCE ;
	public final static String PN_CHART_PAGESIZE		= "pagesize" ;
	public final static String PN_CHART_LAYOUT			= "layout" ;

	public final static String PK_ATLAS_PRACTICALITY	= PK_GENERAL_PRACTICALITY ;
	public final static String PK_ATLAS_IMPORTANCE		= PK_GENERAL_IMPORTANCE ;
	public final static String PK_ATLAS_URLMODELMAP		= "urlModelMap" ;
	public final static String PK_ATLAS_OVERLAP			= "overlap" ;
	public final static String PK_ATLAS_LIMITDE			= "limitDe" ;
	public final static String PK_ATLAS_INTERVALUNITSH	= "intervalUnitsH" ;
	public final static String PK_ATLAS_INTERVALUNITSD	= "intervalUnitsD" ;

	public final static String PN_HORIZON_PRACTICALITY = PN_GENERAL_PRACTICALITY ;

	public final static String PK_CIRCLE_INTERVAL	= PK_GENERAL_INTERVAL ;
	public final static String PN_CIRCLE_IMPORTANCE	= PN_GENERAL_IMPORTANCE ;

	public final static String PN_DIAL_ANNOTATION	= "annotation" ;
	public final static String PK_DIAL_RISE			= PK_GENERAL_RISE ;
	public final static String PN_DIAL_BASELINE		= "baseline" ;
	public final static String PK_DIAL_SPACE		= PK_GENERAL_SPACE ;
	public final static String PK_DIAL_THICKNESS	= "thickness" ;
	public final static String PK_DIAL_LINEWIDTH	= PK_GENERAL_LINEWIDTH ;

	public final static String PK_GRADUATION_SPACE		= PK_GENERAL_SPACE ;
	public final static String PK_GRADUATION_LINELENGTH	= "linelength" ;
	public final static String PK_GRADUATION_LINEWIDTH	= PK_GENERAL_LINEWIDTH ;

	public final static String PK_ANNOTATION_SUBSCRIPTSHIFT		= "subscriptshift" ;
	public final static String PK_ANNOTATION_SUPERSCRIPTSHIFT	= "superscriptshift" ;
	public final static String PK_ANNOTATION_SUBSCRIPTSHRINK	= "subscriptshrink" ;
	public final static String PK_ANNOTATION_SUPERSCRIPTSHRINK	= "superscriptshrink" ;
	public final static String PK_ANNOTATION_MARGIN				= "margin" ;
	public final static String PK_ANNOTATION_RISE				= PK_GENERAL_RISE ;

	public final static String PN_TEXT_PURPOSE		= "purpose" ;

	public final static String PK_BODY_INTERVAL		= PK_GENERAL_INTERVAL ;
	public final static String PK_BODY_STRETCH		= "stretch" ;
	public final static String PK_BODY_IMPORTANCE	= PK_GENERAL_IMPORTANCE ;

	public final static String PK_POSTSCRIPT_PRECISION	= PK_GENERAL_PRECISION ;
	public final static String PK_POSTSCRIPT_SCANLINE	= "scanline" ;
	public final static String PN_POSTSCRIPT_TYPE3		= "type3" ;
	public final static String PN_POSTSCRIPT_PROLOG		= "prolog" ;
	public final static String PN_POSTSCRIPT_UNICODE	= "unicode" ;
	public final static String PK_POSTSCRIPT_DEFAULT	= "default" ;

	public final static String PK_PRINTSTREAM_VIEWER	= "viewer" ;

	public final static String PK_DMS_PRECISION = PK_GENERAL_PRECISION ;

	public final static String PK_RATIONAL_PRECISION = PK_GENERAL_PRECISION ;

	// postscript prolog definitions
	public final static String PS_PROLOG_LIM0			= "LIM0" ;
	public final static String PS_PROLOG_ACOS			= "acos" ;
	public final static String PS_PROLOG_ASIN			= "asin" ;
	public final static String PS_PROLOG_HALO			= "halo" ;
	public final static String PS_PROLOG_LINE			= "line" ;
	public final static String PS_PROLOG_LISTREDUCE		= "listreduce" ;
	public final static String PS_PROLOG_MATROT90CC		= "matrot90cc" ;
	public final static String PS_PROLOG_MATROT90C		= "matrot90c" ;
	public final static String PS_PROLOG_MATROT180CC	= "matrot180cc" ;
	public final static String PS_PROLOG_MATROT180C		= "matrot180c" ;
	public final static String PS_PROLOG_MATROTARB		= "matrotarb" ;
	public final static String PS_PROLOG_PAGESIZE		= "pagesize" ;
	public final static String PS_PROLOG_PATHLENGTH		= "pathlength" ;
	public final static String PS_PROLOG_PATHREVERSE	= "pathreverse" ;
	public final static String PS_PROLOG_PATHSHIFT		= "pathshift" ;
	public final static String PS_PROLOG_PATHSHOW		= "pathshow" ;
	public final static String PS_PROLOG_POLYLINE		= "polyline" ;
	public final static String PS_PROLOG_SETENCODING	= "setencoding" ;
	public final static String PS_PROLOG_TRUNCATEF		= "truncatef" ;
	public final static String PS_PROLOG_VABS			= "vabs" ;
	public final static String PS_PROLOG_VADD			= "vadd" ;
	public final static String PS_PROLOG_VANGLE			= "vangle" ;
	public final static String PS_PROLOG_VAPPLY			= "vapply" ;
	public final static String PS_PROLOG_VDOT			= "vdot" ;
	public final static String PS_PROLOG_VMUL			= "vmul" ;
	public final static String PS_PROLOG_VSCALE			= "vscale" ;
	public final static String PS_PROLOG_VSUB			= "vsub" ;
}
