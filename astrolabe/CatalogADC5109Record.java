
package astrolabe;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

@SuppressWarnings("serial")
public class CatalogADC5109Record extends astrolabe.model.CatalogADC5109Record implements CatalogRecord {

	private final static String DEFAULT_TOKENPATTERN = ".+" ;

	private final static int CR_LENGTH = 521 ;

	private final static String QK_SKY2000	= "SKY2000" ;
	private final static String QK_ID		= "ID" ;
	private final static String QK_HD		= "HD" ;
	private final static String QK_M_HD		= "m_HD" ;
	private final static String QK_U_HD		= "u_HD" ;
	private final static String QK_SAO		= "SAO" ;
	private final static String QK_M_SAO	= "m_SAO" ;
	private final static String QK_DM		= "DM" ;
	private final static String QK_M_DM		= "m_DM" ;
	private final static String QK_U_DM		= "u_DM" ;
	private final static String QK_HR		= "HR" ;
	private final static String QK_WDS		= "WDS" ;
	private final static String QK_M_WDS	= "m_WDS" ;
	private final static String QK_U_WDS	= "u_WDS" ;
	private final static String QK_PPM		= "PPM" ;
	private final static String QK_U_PPM	= "u_PPM" ;
	private final static String QK_ID_MERG	= "ID_merg" ;
	private final static String QK_NAME		= "Name" ;
	private final static String QK_VNAME	= "Vname" ;
	private final static String QK_RAH		= "RAh" ;
	private final static String QK_RAM		= "RAm" ;
	private final static String QK_RAS		= "RAs" ;
	private final static String QK_DE		= "DE" ;
	private final static String QK_DED		= "DEd" ;
	private final static String QK_DEM		= "DEm" ;
	private final static String QK_DES		= "DEs" ;
	private final static String QK_E_POS	= "e_pos" ;
	private final static String QK_F_POS	= "f_pos" ;
	private final static String QK_R_POS	= "r_pos" ;
	private final static String QK_PMRA		= "pmRA" ;
	private final static String QK_PMDE		= "pmDE" ;
	private final static String QK_R_PM		= "r_pm" ;
	private final static String QK_RV		= "RV" ;
	private final static String QK_R_RV		= "r_RV" ;
	private final static String QK_PLX		= "Plx" ;
	private final static String QK_E_PLX	= "e_Plx" ;
	private final static String QK_R_PLX	= "r_Plx" ;
	private final static String QK_GCI_X	= "GCI_X" ;
	private final static String QK_GCI_Y	= "GCI_Y" ;
	private final static String QK_GCI_Z	= "GCI_Z" ;
	private final static String QK_GLON		= "GLON" ;
	private final static String QK_GLAT		= "GLAT" ;
	private final static String QK_VMAG		= "Vmag" ;
	private final static String QK_VDER		= "Vder" ;
	private final static String QK_E_VMAG	= "e_Vmag" ;
	private final static String QK_F_VMAG	= "f_Vmag" ;
	private final static String QK_R_VMAG	= "r_Vmag" ;
	private final static String QK_N_VMAG	= "n_Vmag" ;
	private final static String QK_BMAG		= "Bmag" ;
	private final static String QK_BV		= "BV" ;
	private final static String QK_E_BMAG	= "e_Bmag" ;
	private final static String QK_F_BMAG	= "f_Bmag" ;
	private final static String QK_R_BMAG	= "r_Bmag" ;
	private final static String QK_UMAG		= "Umag" ;
	private final static String QK_UB		= "UB" ;
	private final static String QK_E_UMAG	= "e_Umag" ;
	private final static String QK_N_UMAG	= "n_Umag" ;
	private final static String QK_R_UMAG	= "r_Umag" ;
	private final static String QK_PTV		= "Ptv" ;
	private final static String QK_R_PTV	= "r_Ptv" ;
	private final static String QK_PTG		= "Ptg" ;
	private final static String QK_R_PTG	= "r_Ptg" ;
	private final static String QK_SPMK		= "SpMK" ;
	private final static String QK_R_SPMK	= "r_SpMK" ;
	private final static String QK_SP		= "Sp" ;
	private final static String QK_R_SP		= "r_Sp" ;
	private final static String QK_SEP		= "sep" ;
	private final static String QK_DMAG		= "Dmag" ;
	private final static String QK_ORBPER	= "orbPer" ;
	private final static String QK_PA		= "PA" ;
	private final static String QK_DATE		= "date" ;
	private final static String QK_R_DUP	= "r_Dup" ;
	private final static String QK_N_DMAG	= "n_Dmag" ;
	private final static String QK_DIST1	= "dist1" ;
	private final static String QK_DIST2	= "dist2" ;
	private final static String QK_ID_A		= "ID_A" ;
	private final static String QK_ID_B		= "ID_B" ;
	private final static String QK_ID_C		= "ID_C" ;
	private final static String QK_MAGMAX	= "magMax" ;
	private final static String QK_MAGMIN	= "magMin" ;
	private final static String QK_VARAMP	= "varAmp" ;
	private final static String QK_N_VARAMP	= "n_varAmp" ;
	private final static String QK_VARPER	= "varPer" ;
	private final static String QK_VAREP	= "varEp" ;
	private final static String QK_VARTYP	= "varTyp" ;
	private final static String QK_R_VAR	= "r_var" ;
	private final static String QK_MAG1		= "mag1" ;
	private final static String QK_VMAG1	= "vmag1" ;
	private final static String QK_E_MAG1	= "e_mag1" ;
	private final static String QK_N_MAG1	= "n_mag1" ;
	private final static String QK_P_MAG1	= "p_mag1" ;
	private final static String QK_R_MAG1	= "r_mag1" ;
	private final static String QK_MAG2		= "mag2" ;
	private final static String QK_VMAG2	= "vmag2" ;
	private final static String QK_E_MAG2	= "e_mag2" ;
	private final static String QK_N_MAG2	= "n_mag2" ;
	private final static String QK_P_MAG2	= "p_mag2" ;
	private final static String QK_R_MAG2	= "r_mag2" ;
	private final static String QK_CI1		= "ci1" ;
	private final static String QK_F_MAG1	= "f_mag1" ;
	private final static String QK_F_MAG2	= "f_mag2" ;
	private final static String QK_MAG3		= "mag3" ;
	private final static String QK_VMAG3	= "vmag3" ;
	private final static String QK_E_MAG3	= "e_mag3" ;
	private final static String QK_N_MAG3	= "n_mag3" ;
	private final static String QK_P_MAG3	= "p_mag3" ;
	private final static String QK_R_MAG3	= "r_mag3" ;
	private final static String QK_F_MAG3	= "f_mag3" ;

	public String SKY2000	; // *Identifier based on J2000 position
	public String ID		; // Skymap number
	public String HD		; // ?Henry Draper <III/135> number
	public String m_HD		; // *[1239]? HD duplicity indication
	public String u_HD		; // HD identification uncertain
	public String SAO		; // ? SAO <I/131> number
	public String m_SAO		; // SAO component
	public String DM		; // Durchmusterung (BD <I/122>; SD <I/119>;
	// CD <I/114>; CP <I/108>) 
	public String m_DM		; // Durchmusterung supplement letter
	public String u_DM		; // [: ] DM identification uncertain
	public String HR		; // ?Harvard Revised <V/50> num. (=BS)
	public String WDS		; // Washington Double Stars <I/237> number
	public String m_WDS		; // WDS components
	public String u_WDS		; // [: ] WDS identification uncertain
	public String PPM		; // ?Position and Proper Motion number
	// (<I/146>, <I/193>, <I/208>) 
	public String u_PPM		; // [: ] PPM identification uncertain
	public String ID_merg	; // ?Skymap num. of last skymap entry merged
	// with this star 
	public String Name		; // Star name (or AGK3 number)
	public String Vname		; // Variable star name (or
	// doubtful variability) 
	public String RAh		; // Right ascension (J2000) hours
	public String RAm		; // Right ascension (J2000) minutes
	public String RAs		; // Right ascension (J2000) seconds
	public String DE		; // Declination sign
	public String DEd		; // Declination degrees (J2000)
	public String DEm		; // Declination minutes (J2000)
	public String DEs		; // Declination seconds (J2000)
	public String e_pos		; // Position uncertainty
	public String f_pos		; // [b] Blended position flag
	public String r_pos		; // Source of position
	public String pmRA		; // Proper motion in RA (J2000)
	public String pmDE		; // Proper motion in Dec (J2000)
	public String r_pm		; // ?Source of proper motion data
	public String RV		; // ?Radial velocity
	public String r_RV		; // *?Source of radial velocity data
	public String Plx		; // ?Trigonometric parallax
	public String e_Plx		; // ?Trigonometric parallax uncertainty
	public String r_Plx		; // ?Source of trigonometric parallax data
	public String GCI_X		; // *GCI unit vector in X (J2000)
	public String GCI_Y		; // *GCI unit vector in Y (J2000)
	public String GCI_Z		; // *GCI unit vector in Z (J2000)
	public String GLON		; // Galactic longitude (B1950)
	public String GLAT		; // Galactic latitude (B1950)
	public String Vmag		; // ?Observed visual magnitude (V or v)
	public String Vder		; // ?Derived visual magnitude
	public String e_Vmag	; // ?Derived v or observed visual magnitude
	// uncertainty 
	public String f_Vmag	; // [b] Blended visual magnitude flag
	public String r_Vmag	; // *?Source of visual magnitude
	public String n_Vmag	; // ?V magnitude derivation flag
	public String Bmag		; // ?B magnitude (observed)
	public String BV		; // ?B V color (observed)
	public String e_Bmag	; // ?B or (B V) magnitude uncertainty
	public String f_Bmag	; // [b] Blended b magnitude flag
	public String r_Bmag	; // ?Source of b magnitude
	public String Umag		; // ?U magnitude (observed)
	public String UB		; // ?U B color (observed)
	public String e_Umag	; // ?U or (U B) magnitude uncertainty
	public String n_Umag	; // Blended u magnitude flag
	public String r_Umag	; // *?Source of u magnitude
	public String Ptv		; // ?Photovisual magnitude (observed)
	public String r_Ptv		; // ?Source of ptv magnitudes
	public String Ptg		; // ?Photographic magnitude (observed)
	public String r_Ptg		; // ?Source of ptg magnitudes
	public String SpMK		; // Morgan Keenan (MK) spectral type
	public String r_SpMK	; // ?Source of MK spectral type data
	public String Sp		; // *One dimensional spectral class
	public String r_Sp		; // ?Source of one dimen. spectral class
	public String sep		; // ?Separation of brightest and second
	// brightest components 
	public String Dmag		; // ?Magnitude difference of the brightest
	// and second brightest components 
	public String orbPer	; // *?Orbital period
	public String PA		; // ?Position angle
	public String date		; // ?Year of observation (AD)
	public String r_Dup		; // ?Source of multiplicity data
	public String n_Dmag	; // Passband of multiple star mag. dif.
	public String dist1		; // ?Distance to nearest neighboring star in
	// the master catalog 
	public String dist2		; // ?Dist. to nearest neighboring master
	// cat. star no more than 2 mag. fainter 
	public String ID_A		; // ?Skymap number of primary component
	public String ID_B		; // ?Skymap number of second component
	public String ID_C		; // ?Skymap number of third component
	public String magMax	; // *?Maximum variable magnitude
	public String magMin	; // *?Minimum variable magnitude
	public String varAmp	; // ?Variability amplitude
	public String n_varAmp	; // Passband of variability amplitude
	public String varPer	; // ?Period of variability
	public String varEp		; // ?Epoch of variability (JD 2400000)
	public String varTyp	; // ?Type of variable star
	public String r_var		; // ?Source of variability data
	public String mag1		; // ?Passband #1 magnitude (observed)
	public String vmag1		; // ?v passband #1 color
	public String e_mag1	; // ?Passband #1 uncertainty in mag. or col.
	public String n_mag1	; // *[RJC] Passband #1 photometric system
	public String p_mag1	; // *[R] Passband #1
	public String r_mag1	; // *?Source of passband #1: mag. or color
	public String mag2		; // ?Passband #2 magnitude (observed)
	public String vmag2		; // ?v passband #2 color
	public String e_mag2	; // ?Passband #2 uncertainty in mag. or col.
	public String n_mag2	; // *[JEC] Passband #2 photometric system
	public String p_mag2	; // *[I] Passband #2
	public String r_mag2	; // *?Source of passband #2: mag. or color
	public String ci1		; // 2 ?Passband #1 passband #2 color
	public String f_mag1	; // [b] Blended passband #1 mag/color flag
	public String f_mag2	; // [b] Blended passband #2 mag/color flag
	public String mag3		; // *?Passband #3 magnitude (observed)
	public String vmag3		; // *?v passband #3 color
	public String e_mag3	; // *?Passband #3 uncertainty in mag. or col.
	public String n_mag3	; // *Passband #3 photometric system
	public String p_mag3	; // *[X] Passband #3
	public String r_mag3	; // *?Source of passband #3: mag. or color
	public String f_mag3	; // *[b] Blended passband #3 mag/color flag

	// message key (MK_)
	private final static String MK_ERECLEN = "ereclen" ;
	private final static String MK_ERECVAL = "erecval" ;

	public CatalogADC5109Record( String data ) throws ParameterNotValidException {
		MessageCatalog cat ;
		StringBuffer msg ;
		String fmt ;

		if ( data.length() != CR_LENGTH ) {
			cat = new MessageCatalog( this ) ;
			fmt = cat.message( MK_ERECLEN, null ) ;
			if ( fmt != null ) {
				msg = new StringBuffer() ;
				msg.append( MessageFormat.format( fmt, new Object[] { CR_LENGTH } ) ) ;
			} else
				msg = null ;

			throw new ParameterNotValidException( ParameterNotValidError.errmsg( data.length(), msg.toString() ) ) ;
		}

		SKY2000		= data.substring( 8, 27 ).trim() ;
		ID			= data.substring( 27, 35 ).trim() ;
		HD			= data.substring( 35, 41 ).trim() ;
		m_HD		= data.substring( 41, 42 ).trim() ;
		u_HD		= data.substring( 42, 43 ).trim() ;
		SAO			= data.substring( 43, 49 ).trim() ;
		m_SAO		= data.substring( 49, 50 ).trim() ;
		DM			= data.substring( 50, 61 ).trim() ;
		m_DM		= data.substring( 61, 62 ).trim() ;
		u_DM		= data.substring( 62, 63 ).trim() ;
		HR			= data.substring( 63, 67 ).trim() ;
		WDS			= data.substring( 67, 77 ).trim() ;
		m_WDS		= data.substring( 77, 82 ).trim() ;
		u_WDS		= data.substring( 82, 83 ).trim() ;
		PPM			= data.substring( 83, 89 ).trim() ;
		u_PPM		= data.substring( 89, 90 ).trim() ;
		ID_merg		= data.substring( 90, 98 ).trim() ;
		Name		= data.substring( 98, 108 ).trim() ;
		Vname		= data.substring( 108, 118 ).trim() ;
		RAh			= data.substring( 118, 120 ).trim() ;
		RAm			= data.substring( 120, 122 ).trim() ;
		RAs			= data.substring( 122, 129 ).trim() ;
		DE			= data.substring( 129, 130 ).trim() ;
		DEd			= data.substring( 130, 132 ).trim() ;
		DEm			= data.substring( 132, 134 ).trim() ;
		DEs			= data.substring( 134, 140 ).trim() ;
		e_pos		= data.substring( 140, 146 ).trim() ;
		f_pos		= data.substring( 146, 147 ).trim() ;
		r_pos		= data.substring( 147, 149 ).trim() ;
		pmRA		= data.substring( 149, 157 ).trim() ;
		pmDE		= data.substring( 157, 165 ).trim() ;
		r_pm		= data.substring( 165, 167 ).trim() ;
		RV			= data.substring( 167, 173 ).trim() ;
		r_RV		= data.substring( 173, 175 ).trim() ;
		Plx			= data.substring( 175, 183 ).trim() ;
		e_Plx		= data.substring( 183, 191 ).trim() ;
		r_Plx		= data.substring( 191, 193 ).trim() ;
		GCI_X		= data.substring( 193, 202 ).trim() ;
		GCI_Y		= data.substring( 202, 211 ).trim() ;
		GCI_Z		= data.substring( 211, 220 ).trim() ;
		GLON		= data.substring( 220, 226 ).trim() ;
		GLAT		= data.substring( 226, 232 ).trim() ;
		Vmag		= data.substring( 232, 238 ).trim() ;
		Vder		= data.substring( 238, 243 ).trim() ;
		e_Vmag		= data.substring( 243, 248 ).trim() ;
		f_Vmag		= data.substring( 248, 249 ).trim() ;
		r_Vmag		= data.substring( 249, 251 ).trim() ;
		n_Vmag		= data.substring( 251, 252 ).trim() ;
		Bmag		= data.substring( 252, 258 ).trim() ;
		BV			= data.substring( 258, 264 ).trim() ;
		e_Bmag		= data.substring( 264, 269 ).trim() ;
		f_Bmag		= data.substring( 269, 270 ).trim() ;
		r_Bmag		= data.substring( 270, 272 ).trim() ;
		Umag		= data.substring( 272, 278 ).trim() ;
		UB			= data.substring( 278, 284 ).trim() ;
		e_Umag		= data.substring( 284, 289 ).trim() ;
		n_Umag		= data.substring( 289, 290 ).trim() ;
		r_Umag		= data.substring( 290, 292 ).trim() ;
		Ptv			= data.substring( 292, 296 ).trim() ;
		r_Ptv		= data.substring( 296, 298 ).trim() ;
		Ptg			= data.substring( 298, 302 ).trim() ;
		r_Ptg		= data.substring( 302, 304 ).trim() ;
		SpMK		= data.substring( 304, 334 ).trim() ;
		r_SpMK		= data.substring( 334, 336 ).trim() ;
		Sp			= data.substring( 336, 339 ).trim() ;
		r_Sp		= data.substring( 339, 341 ).trim() ;
		sep			= data.substring( 341, 348 ).trim() ;
		Dmag		= data.substring( 348, 353 ).trim() ;
		orbPer		= data.substring( 353, 360 ).trim() ;
		PA			= data.substring( 360, 363 ).trim() ;
		date		= data.substring( 363, 370 ).trim() ;
		r_Dup		= data.substring( 370, 372 ).trim() ;
		n_Dmag		= data.substring( 372, 373 ).trim() ;
		dist1		= data.substring( 373, 380 ).trim() ;
		dist2		= data.substring( 380, 387 ).trim() ;
		ID_A		= data.substring( 387, 395 ).trim() ;
		ID_B		= data.substring( 395, 403 ).trim() ;
		ID_C		= data.substring( 403, 411 ).trim() ;
		magMax		= data.substring( 411, 416 ).trim() ;
		magMin		= data.substring( 416, 421 ).trim() ;
		varAmp		= data.substring( 421, 426 ).trim() ;
		n_varAmp	= data.substring( 426, 427 ).trim() ;
		varPer		= data.substring( 427, 435 ).trim() ;
		varEp		= data.substring( 435, 443 ).trim() ;
		varTyp		= data.substring( 443, 446 ).trim() ;
		r_var		= data.substring( 446, 448 ).trim() ;
		mag1		= data.substring( 448, 454 ).trim() ;
		vmag1		= data.substring( 454, 460 ).trim() ;
		e_mag1		= data.substring( 460, 465 ).trim() ;
		n_mag1		= data.substring( 465, 466 ).trim() ;
		p_mag1		= data.substring( 466, 467 ).trim() ;
		r_mag1		= data.substring( 467, 469 ).trim() ;
		mag2		= data.substring( 469, 475 ).trim() ;
		vmag2		= data.substring( 475, 481 ).trim() ;
		e_mag2		= data.substring( 481, 486 ).trim() ;
		n_mag2		= data.substring( 486, 487 ).trim() ;
		p_mag2		= data.substring( 487, 488 ).trim() ;
		r_mag2		= data.substring( 488, 490 ).trim() ;
		ci1			= data.substring( 490, 496 ).trim() ;
		f_mag1		= data.substring( 496, 497 ).trim() ;
		f_mag2		= data.substring( 497, 498 ).trim() ;
		mag3		= data.substring( 498, 504 ).trim() ;
		vmag3		= data.substring( 504, 510 ).trim() ;
		e_mag3		= data.substring( 510, 515 ).trim() ;
		n_mag3		= data.substring( 515, 516 ).trim() ;
		p_mag3		= data.substring( 516, 517 ).trim() ;
		r_mag3		= data.substring( 517, 519 ).trim() ;
		f_mag3		= data.substring( 519, 520 ).trim() ;
	}

	public void register() {
		SubstituteCatalog cat ;
		String sub ;

		cat = new SubstituteCatalog( this ) ;

		sub = cat.substitute( QK_SKY2000, null ) ;
		Registry.register( sub, SKY2000 ) ;
		sub = cat.substitute( QK_ID, null ) ;
		Registry.register( sub, ID ) ;
		sub = cat.substitute( QK_HD, null ) ;
		Registry.register( sub, HD ) ;
		sub = cat.substitute( QK_M_HD, null ) ;
		Registry.register( sub, m_HD ) ;
		sub = cat.substitute( QK_U_HD, null ) ;
		Registry.register( sub, u_HD ) ;
		sub = cat.substitute( QK_SAO, null ) ;
		Registry.register( sub, SAO ) ;
		sub = cat.substitute( QK_M_SAO, null ) ;
		Registry.register( sub, m_SAO ) ;
		sub = cat.substitute( QK_DM, null ) ;
		Registry.register( sub, DM ) ;
		sub = cat.substitute( QK_M_DM, null ) ;
		Registry.register( sub, m_DM ) ;
		sub = cat.substitute( QK_U_DM, null ) ;
		Registry.register( sub, u_DM ) ;
		sub = cat.substitute( QK_HR, null ) ;
		Registry.register( sub, HR ) ;
		sub = cat.substitute( QK_WDS, null ) ;
		Registry.register( sub, WDS ) ;
		sub = cat.substitute( QK_M_WDS, null ) ;
		Registry.register( sub, m_WDS ) ;
		sub = cat.substitute( QK_U_WDS, null ) ;
		Registry.register( sub, u_WDS ) ;
		sub = cat.substitute( QK_PPM, null ) ;
		Registry.register( sub, PPM ) ;
		sub = cat.substitute( QK_U_PPM, null ) ;
		Registry.register( sub, u_PPM ) ;
		sub = cat.substitute( QK_ID_MERG, null ) ;
		Registry.register( sub, ID_merg ) ;
		sub = cat.substitute( QK_NAME, null ) ;
		Registry.register( sub, Name ) ;
		sub = cat.substitute( QK_VNAME, null ) ;
		Registry.register( sub, Vname ) ;
		sub = cat.substitute( QK_RAH, null ) ;
		Registry.register( sub, RAh ) ;
		sub = cat.substitute( QK_RAM, null ) ;
		Registry.register( sub, RAm ) ;
		sub = cat.substitute( QK_RAS, null ) ;
		Registry.register( sub, RAs ) ;
		sub = cat.substitute( QK_DE, null ) ;
		Registry.register( sub, DE ) ;
		sub = cat.substitute( QK_DED, null ) ;
		Registry.register( sub, DEd ) ;
		sub = cat.substitute( QK_DEM, null ) ;
		Registry.register( sub, DEm ) ;
		sub = cat.substitute( QK_DES, null ) ;
		Registry.register( sub, DEs ) ;
		sub = cat.substitute( QK_E_POS, null ) ;
		Registry.register( sub, e_pos ) ;
		sub = cat.substitute( QK_F_POS, null ) ;
		Registry.register( sub, f_pos ) ;
		sub = cat.substitute( QK_R_POS, null ) ;
		Registry.register( sub, r_pos ) ;
		sub = cat.substitute( QK_PMRA, null ) ;
		Registry.register( sub, pmRA ) ;
		sub = cat.substitute( QK_PMDE, null ) ;
		Registry.register( sub, pmDE ) ;
		sub = cat.substitute( QK_R_PM, null ) ;
		Registry.register( sub, r_pm ) ;
		sub = cat.substitute( QK_RV, null ) ;
		Registry.register( sub, RV ) ;
		sub = cat.substitute( QK_R_RV, null ) ;
		Registry.register( sub, r_RV ) ;
		sub = cat.substitute( QK_PLX, null ) ;
		Registry.register( sub, Plx ) ;
		sub = cat.substitute( QK_E_PLX, null ) ;
		Registry.register( sub, e_Plx ) ;
		sub = cat.substitute( QK_R_PLX, null ) ;
		Registry.register( sub, r_Plx ) ;
		sub = cat.substitute( QK_GCI_X, null ) ;
		Registry.register( sub, GCI_X ) ;
		sub = cat.substitute( QK_GCI_Y, null ) ;
		Registry.register( sub, GCI_Y ) ;
		sub = cat.substitute( QK_GCI_Z, null ) ;
		Registry.register( sub, GCI_Z ) ;
		sub = cat.substitute( QK_GLON, null ) ;
		Registry.register( sub, GLON ) ;
		sub = cat.substitute( QK_GLAT, null ) ;
		Registry.register( sub, GLAT ) ;
		sub = cat.substitute( QK_VMAG, null ) ;
		Registry.register( sub, Vmag ) ;
		sub = cat.substitute( QK_VDER, null ) ;
		Registry.register( sub, Vder ) ;
		sub = cat.substitute( QK_E_VMAG, null ) ;
		Registry.register( sub, e_Vmag ) ;
		sub = cat.substitute( QK_F_VMAG, null ) ;
		Registry.register( sub, f_Vmag ) ;
		sub = cat.substitute( QK_R_VMAG, null ) ;
		Registry.register( sub, r_Vmag ) ;
		sub = cat.substitute( QK_N_VMAG, null ) ;
		Registry.register( sub, n_Vmag ) ;
		sub = cat.substitute( QK_BMAG, null ) ;
		Registry.register( sub, Bmag ) ;
		sub = cat.substitute( QK_BV, null ) ;
		Registry.register( sub, BV ) ;
		sub = cat.substitute( QK_E_BMAG, null ) ;
		Registry.register( sub, e_Bmag ) ;
		sub = cat.substitute( QK_F_BMAG, null ) ;
		Registry.register( sub, f_Bmag ) ;
		sub = cat.substitute( QK_R_BMAG, null ) ;
		Registry.register( sub, r_Bmag ) ;
		sub = cat.substitute( QK_UMAG, null ) ;
		Registry.register( sub, Umag ) ;
		sub = cat.substitute( QK_UB, null ) ;
		Registry.register( sub, UB ) ;
		sub = cat.substitute( QK_E_UMAG, null ) ;
		Registry.register( sub, e_Umag ) ;
		sub = cat.substitute( QK_N_UMAG, null ) ;
		Registry.register( sub, n_Umag ) ;
		sub = cat.substitute( QK_R_UMAG, null ) ;
		Registry.register( sub, r_Umag ) ;
		sub = cat.substitute( QK_PTV, null ) ;
		Registry.register( sub, Ptv ) ;
		sub = cat.substitute( QK_R_PTV, null ) ;
		Registry.register( sub, r_Ptv ) ;
		sub = cat.substitute( QK_PTG, null ) ;
		Registry.register( sub, Ptg ) ;
		sub = cat.substitute( QK_R_PTG, null ) ;
		Registry.register( sub, r_Ptg ) ;
		sub = cat.substitute( QK_SPMK, null ) ;
		Registry.register( sub, SpMK ) ;
		sub = cat.substitute( QK_R_SPMK, null ) ;
		Registry.register( sub, r_SpMK ) ;
		sub = cat.substitute( QK_SP, null ) ;
		Registry.register( sub, Sp ) ;
		sub = cat.substitute( QK_R_SP, null ) ;
		Registry.register( sub, r_Sp ) ;
		sub = cat.substitute( QK_SEP, null ) ;
		Registry.register( sub, sep ) ;
		sub = cat.substitute( QK_DMAG, null ) ;
		Registry.register( sub, Dmag ) ;
		sub = cat.substitute( QK_ORBPER, null ) ;
		Registry.register( sub, orbPer ) ;
		sub = cat.substitute( QK_PA, null ) ;
		Registry.register( sub, PA ) ;
		sub = cat.substitute( QK_DATE, null ) ;
		Registry.register( sub, date ) ;
		sub = cat.substitute( QK_R_DUP, null ) ;
		Registry.register( sub, r_Dup ) ;
		sub = cat.substitute( QK_N_DMAG, null ) ;
		Registry.register( sub, n_Dmag ) ;
		sub = cat.substitute( QK_DIST1, null ) ;
		Registry.register( sub, dist1 ) ;
		sub = cat.substitute( QK_DIST2, null ) ;
		Registry.register( sub, dist2 ) ;
		sub = cat.substitute( QK_ID_A, null ) ;
		Registry.register( sub, ID_A ) ;
		sub = cat.substitute( QK_ID_B, null ) ;
		Registry.register( sub, ID_B ) ;
		sub = cat.substitute( QK_ID_C, null ) ;
		Registry.register( sub, ID_C ) ;
		sub = cat.substitute( QK_MAGMAX, null ) ;
		Registry.register( sub, magMax ) ;
		sub = cat.substitute( QK_MAGMIN, null ) ;
		Registry.register( sub, magMin ) ;
		sub = cat.substitute( QK_VARAMP, null ) ;
		Registry.register( sub, varAmp ) ;
		sub = cat.substitute( QK_N_VARAMP, null ) ;
		Registry.register( sub, n_varAmp ) ;
		sub = cat.substitute( QK_VARPER, null ) ;
		Registry.register( sub, varPer ) ;
		sub = cat.substitute( QK_VAREP, null ) ;
		Registry.register( sub, varEp ) ;
		sub = cat.substitute( QK_VARTYP, null ) ;
		Registry.register( sub, varTyp ) ;
		sub = cat.substitute( QK_R_VAR, null ) ;
		Registry.register( sub, r_var ) ;
		sub = cat.substitute( QK_MAG1, null ) ;
		Registry.register( sub, mag1 ) ;
		sub = cat.substitute( QK_VMAG1, null ) ;
		Registry.register( sub, vmag1 ) ;
		sub = cat.substitute( QK_E_MAG1, null ) ;
		Registry.register( sub, e_mag1 ) ;
		sub = cat.substitute( QK_N_MAG1, null ) ;
		Registry.register( sub, n_mag1 ) ;
		sub = cat.substitute( QK_P_MAG1, null ) ;
		Registry.register( sub, p_mag1 ) ;
		sub = cat.substitute( QK_R_MAG1, null ) ;
		Registry.register( sub, r_mag1 ) ;
		sub = cat.substitute( QK_MAG2, null ) ;
		Registry.register( sub, mag2 ) ;
		sub = cat.substitute( QK_VMAG2, null ) ;
		Registry.register( sub, vmag2 ) ;
		sub = cat.substitute( QK_E_MAG2, null ) ;
		Registry.register( sub, e_mag2 ) ;
		sub = cat.substitute( QK_N_MAG2, null ) ;
		Registry.register( sub, n_mag2 ) ;
		sub = cat.substitute( QK_P_MAG2, null ) ;
		Registry.register( sub, p_mag2 ) ;
		sub = cat.substitute( QK_R_MAG2, null ) ;
		Registry.register( sub, r_mag2 ) ;
		sub = cat.substitute( QK_CI1, null ) ;
		Registry.register( sub, ci1 ) ;
		sub = cat.substitute( QK_F_MAG1, null ) ;
		Registry.register( sub, f_mag1 ) ;
		sub = cat.substitute( QK_F_MAG2, null ) ;
		Registry.register( sub, f_mag2 ) ;
		sub = cat.substitute( QK_MAG3, null ) ;
		Registry.register( sub, mag3 ) ;
		sub = cat.substitute( QK_VMAG3, null ) ;
		Registry.register( sub, vmag3 ) ;
		sub = cat.substitute( QK_E_MAG3, null ) ;
		Registry.register( sub, e_mag3 ) ;
		sub = cat.substitute( QK_N_MAG3, null ) ;
		Registry.register( sub, n_mag3 ) ;
		sub = cat.substitute( QK_P_MAG3, null ) ;
		Registry.register( sub, p_mag3 ) ;
		sub = cat.substitute( QK_R_MAG3, null ) ;
		Registry.register( sub, r_mag3 ) ;
		sub = cat.substitute( QK_F_MAG3, null ) ;
		Registry.register( sub, f_mag3 ) ;
	}

	public boolean isOK() {
		try {
			inspect() ;
		} catch ( ParameterNotValidException e ) {
			return false ;
		}

		return true ;
	}

	public void inspect() throws ParameterNotValidException {
		Preferences node ;
		Field token ;
		String name, value, pattern ;
		MessageCatalog cat ;
		StringBuffer msg ;
		String fmt ;

		try {
			name = this.getClass().getName().replaceAll( "\\.", "/" ) ;
			if ( ! Preferences.systemRoot().nodeExists( name ) )
				return ;
			node = Preferences.systemRoot().node( name ) ;

			for ( String key : node.keys() ) {
				try {
					token = getClass().getDeclaredField( key ) ;
					value = (String) token.get( this ) ;
					pattern = node.get( key, DEFAULT_TOKENPATTERN ) ;
					if ( ! value.matches( pattern ) ) {
						cat = new MessageCatalog( this ) ;
						fmt = cat.message( MK_ERECVAL, null ) ;
						if ( fmt != null ) {
							msg = new StringBuffer() ;
							msg.append( MessageFormat.format( fmt, new Object[] { value, pattern } ) ) ;
						} else
							msg = null ;

						throw new ParameterNotValidException( ParameterNotValidError.errmsg( key, msg.toString() ) ) ;
					}
				} catch ( NoSuchFieldException e ) {
					continue ;
				} catch ( IllegalAccessException e ) {
					throw new RuntimeException( e.toString() ) ;
				}
			}
		} catch ( BackingStoreException e ) {
			throw new RuntimeException( e.toString() ) ;
		}
	}

	public double RA() {
		return RAh()+RAm()/60.+RAs()/3600. ;
	}

	public double de() {
		return DEd()+DEm()/60.+DEs()/3600. ;
	}

	private double RAh() {
		return new Double( RAh ).doubleValue() ;
	}

	private double RAm() {
		return new Double( RAm ).doubleValue() ;
	}

	private double RAs() {
		return new Double( RAs ).doubleValue() ;
	}

	private double DEd() {
		return new Double( DE+DEd ).doubleValue() ;
	}

	private double DEm() {
		return new Double( DE+DEm ).doubleValue() ;
	}

	private double DEs() {
		return new Double( DE+DEs ).doubleValue() ;
	}

	public List<double[]> list() {
		List<double[]> r = new java.util.Vector<double[]>() ;

		r.add( new double[] { RA(), de() } ) ;

		return r ;
	}
}
