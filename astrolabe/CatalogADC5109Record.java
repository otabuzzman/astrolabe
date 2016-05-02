
package astrolabe;

import java.lang.reflect.Field;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

@SuppressWarnings("serial")
public class CatalogADC5109Record extends astrolabe.model.CatalogADC5109Record implements CatalogRecord {

	private final static String DEFAULT_TOKENPATTERN = ".+" ;

	private final static int CR_LENGTH = 521 ;

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

	public CatalogADC5109Record( String data ) throws ParameterNotValidException {

		if ( data.length() != CR_LENGTH ) {
			throw new ParameterNotValidException(  Integer.toString( data.length() ) ) ;
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

	public boolean isOK() {
		try {
			recognize() ;
		} catch ( ParameterNotValidException e ) {
			return false ;
		}

		return true ;
	}

	public void recognize() throws ParameterNotValidException {
		Preferences node ;
		Field token ;
		String value ;

		node = Configuration.getClassNode( this, null, null ) ;

		try {
			for ( String key : node.keys() ) {
				try {
					token = getClass().getDeclaredField( key ) ;
					value = (String) token.get( this ) ;
					if ( ! value.matches( node.get( key, DEFAULT_TOKENPATTERN ) ) )
						throw new ParameterNotValidException( key ) ;
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

	public void register() {
		MessageCatalog m ;
		String key ;

		m = new MessageCatalog( ApplicationConstant.GC_APPLICATION ) ;

		key = m.message( ApplicationConstant.LK_ADC5109_SKY2000 ) ;
		Registry.registerName( key, SKY2000 ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_ID ) ;
		Registry.registerName( key, ID ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_HD ) ;
		Registry.registerName( key, HD ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_M_HD ) ;
		Registry.registerName( key, m_HD ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_U_HD ) ;
		Registry.registerName( key, u_HD ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_SAO ) ;
		Registry.registerName( key, SAO ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_M_SAO ) ;
		Registry.registerName( key, m_SAO ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_DM ) ;
		Registry.registerName( key, DM ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_M_DM ) ;
		Registry.registerName( key, m_DM ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_U_DM ) ;
		Registry.registerName( key, u_DM ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_HR ) ;
		Registry.registerName( key, HR ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_WDS ) ;
		Registry.registerName( key, WDS ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_M_WDS ) ;
		Registry.registerName( key, m_WDS ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_U_WDS ) ;
		Registry.registerName( key, u_WDS ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_PPM ) ;
		Registry.registerName( key, PPM ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_U_PPM ) ;
		Registry.registerName( key, u_PPM ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_ID_MERG ) ;
		Registry.registerName( key, ID_merg ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_NAME ) ;
		Registry.registerName( key, Name ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_VNAME ) ;
		Registry.registerName( key, Vname ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_RAH ) ;
		Registry.registerName( key, RAh ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_RAM ) ;
		Registry.registerName( key, RAm ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_RAS ) ;
		Registry.registerName( key, RAs ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_DE ) ;
		Registry.registerName( key, DE ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_DED ) ;
		Registry.registerName( key, DEd ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_DEM ) ;
		Registry.registerName( key, DEm ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_DES ) ;
		Registry.registerName( key, DEs ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_E_POS ) ;
		Registry.registerName( key, e_pos ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_F_POS ) ;
		Registry.registerName( key, f_pos ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_R_POS ) ;
		Registry.registerName( key, r_pos ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_PMRA ) ;
		Registry.registerName( key, pmRA ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_PMDE ) ;
		Registry.registerName( key, pmDE ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_R_PM ) ;
		Registry.registerName( key, r_pm ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_RV ) ;
		Registry.registerName( key, RV ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_R_RV ) ;
		Registry.registerName( key, r_RV ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_PLX ) ;
		Registry.registerName( key, Plx ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_E_PLX ) ;
		Registry.registerName( key, e_Plx ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_R_PLX ) ;
		Registry.registerName( key, r_Plx ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_GCI_X ) ;
		Registry.registerName( key, GCI_X ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_GCI_Y ) ;
		Registry.registerName( key, GCI_Y ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_GCI_Z ) ;
		Registry.registerName( key, GCI_Z ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_GLON ) ;
		Registry.registerName( key, GLON ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_GLAT ) ;
		Registry.registerName( key, GLAT ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_VMAG ) ;
		Registry.registerName( key, Vmag ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_VDER ) ;
		Registry.registerName( key, Vder ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_E_VMAG ) ;
		Registry.registerName( key, e_Vmag ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_F_VMAG ) ;
		Registry.registerName( key, f_Vmag ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_R_VMAG ) ;
		Registry.registerName( key, r_Vmag ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_N_VMAG ) ;
		Registry.registerName( key, n_Vmag ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_BMAG ) ;
		Registry.registerName( key, Bmag ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_BV ) ;
		Registry.registerName( key, BV ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_E_BMAG ) ;
		Registry.registerName( key, e_Bmag ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_F_BMAG ) ;
		Registry.registerName( key, f_Bmag ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_R_BMAG ) ;
		Registry.registerName( key, r_Bmag ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_UMAG ) ;
		Registry.registerName( key, Umag ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_UB ) ;
		Registry.registerName( key, UB ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_E_UMAG ) ;
		Registry.registerName( key, e_Umag ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_N_UMAG ) ;
		Registry.registerName( key, n_Umag ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_R_UMAG ) ;
		Registry.registerName( key, r_Umag ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_PTV ) ;
		Registry.registerName( key, Ptv ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_R_PTV ) ;
		Registry.registerName( key, r_Ptv ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_PTG ) ;
		Registry.registerName( key, Ptg ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_R_PTG ) ;
		Registry.registerName( key, r_Ptg ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_SPMK ) ;
		Registry.registerName( key, SpMK ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_R_SPMK ) ;
		Registry.registerName( key, r_SpMK ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_SP ) ;
		Registry.registerName( key, Sp ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_R_SP ) ;
		Registry.registerName( key, r_Sp ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_SEP ) ;
		Registry.registerName( key, sep ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_DMAG ) ;
		Registry.registerName( key, Dmag ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_ORBPER ) ;
		Registry.registerName( key, orbPer ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_PA ) ;
		Registry.registerName( key, PA ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_DATE ) ;
		Registry.registerName( key, date ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_R_DUP ) ;
		Registry.registerName( key, r_Dup ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_N_DMAG ) ;
		Registry.registerName( key, n_Dmag ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_DIST1 ) ;
		Registry.registerName( key, dist1 ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_DIST2 ) ;
		Registry.registerName( key, dist2 ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_ID_A ) ;
		Registry.registerName( key, ID_A ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_ID_B ) ;
		Registry.registerName( key, ID_B ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_ID_C ) ;
		Registry.registerName( key, ID_C ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_MAGMAX ) ;
		Registry.registerName( key, magMax ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_MAGMIN ) ;
		Registry.registerName( key, magMin ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_VARAMP ) ;
		Registry.registerName( key, varAmp ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_N_VARAMP ) ;
		Registry.registerName( key, n_varAmp ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_VARPER ) ;
		Registry.registerName( key, varPer ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_VAREP ) ;
		Registry.registerName( key, varEp ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_VARTYP ) ;
		Registry.registerName( key, varTyp ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_R_VAR ) ;
		Registry.registerName( key, r_var ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_MAG1 ) ;
		Registry.registerName( key, mag1 ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_VMAG1 ) ;
		Registry.registerName( key, vmag1 ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_E_MAG1 ) ;
		Registry.registerName( key, e_mag1 ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_N_MAG1 ) ;
		Registry.registerName( key, n_mag1 ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_P_MAG1 ) ;
		Registry.registerName( key, p_mag1 ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_R_MAG1 ) ;
		Registry.registerName( key, r_mag1 ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_MAG2 ) ;
		Registry.registerName( key, mag2 ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_VMAG2 ) ;
		Registry.registerName( key, vmag2 ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_E_MAG2 ) ;
		Registry.registerName( key, e_mag2 ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_N_MAG2 ) ;
		Registry.registerName( key, n_mag2 ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_P_MAG2 ) ;
		Registry.registerName( key, p_mag2 ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_R_MAG2 ) ;
		Registry.registerName( key, r_mag2 ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_CI1 ) ;
		Registry.registerName( key, ci1 ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_F_MAG1 ) ;
		Registry.registerName( key, f_mag1 ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_F_MAG2 ) ;
		Registry.registerName( key, f_mag2 ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_MAG3 ) ;
		Registry.registerName( key, mag3 ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_VMAG3 ) ;
		Registry.registerName( key, vmag3 ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_E_MAG3 ) ;
		Registry.registerName( key, e_mag3 ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_N_MAG3 ) ;
		Registry.registerName( key, n_mag3 ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_P_MAG3 ) ;
		Registry.registerName( key, p_mag3 ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_R_MAG3 ) ;
		Registry.registerName( key, r_mag3 ) ;
		key = m.message( ApplicationConstant.LK_ADC5109_F_MAG3 ) ;
		Registry.registerName( key, f_mag3 ) ;
	}

	public double[] RA() {
		return new double[] { RAh()+RAm()/60.+RAs()/3600. } ;
	}

	public double[] de() {
		return new double[] { DEd()+DEm()/60.+DEs()/3600. } ;
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
}
