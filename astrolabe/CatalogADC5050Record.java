
package astrolabe;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

@SuppressWarnings("serial")
public class CatalogADC5050Record extends astrolabe.model.CatalogADC5050Record implements CatalogRecord {

	private final static String DEFAULT_TOKENPATTERN = ".+" ;

	private final static int CR_LENGTH = 198 ;

	private class Name {

		private final static String QK_FLAMSTEED		= "flamsteed" ;
		private final static String QK_BAYER			= "bayer" ;
		private final static String QK_BAYERINDEX		= "bayerindex" ;
		private final static String QK_CONSTELLATION	= "constellation" ;	

		private String flamsteed		;
		private String bayer			;
		private String bayerindex 		;
		private String constellation	;

		public Name( String value ) {
			String bayer ;

			flamsteed		= value.substring(0, 3).trim() ;
			bayer			= value.substring(3, 6).trim() ;
			this.bayer = Configuration.getValue( getClass().getEnclosingClass(),
					bayer, bayer ) ;
			bayerindex		= value.substring(6, 7).trim() ;
			constellation	= value.substring(7, 10).trim() ;
		}

		public void register() {
			SubstituteCatalog cat ;
			String sub ;

			cat = new SubstituteCatalog( this ) ;

			sub = cat.substitute( QK_FLAMSTEED, QK_FLAMSTEED ) ;
			Registry.register( sub, flamsteed ) ;
			sub = cat.substitute( QK_BAYER, QK_BAYER ) ;
			Registry.register( sub, bayer ) ;
			sub = cat.substitute( QK_BAYERINDEX, QK_BAYERINDEX ) ;
			Registry.register( sub, bayerindex) ;
			sub = cat.substitute( QK_CONSTELLATION, QK_CONSTELLATION ) ;
			Registry.register( sub, constellation ) ;
		}

		public void degister() {
			SubstituteCatalog cat ;
			String sub ;

			cat = new SubstituteCatalog( this ) ;

			sub = cat.substitute( QK_FLAMSTEED, QK_FLAMSTEED ) ;
			Registry.degister( sub ) ;
			sub = cat.substitute( QK_BAYER, QK_BAYER ) ;
			Registry.degister( sub ) ;
			sub = cat.substitute( QK_BAYERINDEX, QK_BAYERINDEX ) ;
			Registry.degister( sub ) ;
			sub = cat.substitute( QK_CONSTELLATION, QK_CONSTELLATION ) ;
			Registry.degister( sub ) ;
		}
	}

	private final static String QK_HR			= "HR" ;
	private final static String QK_NAME			= "Name" ;
	private final static String QK_DM			= "DM" ;
	private final static String QK_HD			= "HD" ;
	private final static String QK_SAO			= "SAO" ;
	private final static String QK_FK5			= "FK5" ;
	private final static String QK_IRFLAG		= "IRflag" ;
	private final static String QK_R_IRFLAG		= "r_IRflag" ;
	private final static String QK_MULTIPLE		= "Multiple" ;
	private final static String QK_ADS			= "ADS" ;
	private final static String QK_ADSCOMP		= "ADScomp" ;
	private final static String QK_VARID		= "VarID" ;
	private final static String QK_RAH1900		= "RAh1900" ;
	private final static String QK_RAM1900		= "RAm1900" ;
	private final static String QK_RAS1900		= "RAs1900" ;
	private final static String QK_DE1900		= "DE1900" ;
	private final static String QK_DED1900		= "DEd1900" ;
	private final static String QK_DEM1900		= "DEm1900" ;
	private final static String QK_DES1900		= "DEs1900" ;
	private final static String QK_RAH			= "RAh" ;
	private final static String QK_RAM			= "RAm" ;
	private final static String QK_RAS			= "RAs" ;
	private final static String QK_DE			= "DE" ;
	private final static String QK_DED			= "DEd" ;
	private final static String QK_DEM			= "DEm" ;
	private final static String QK_DES			= "DEs" ;
	private final static String QK_GLON			= "GLON" ;
	private final static String QK_GLAT			= "GLAT" ;
	private final static String QK_VMAG			= "Vmag" ;
	private final static String QK_N_VMAG		= "n_Vmag" ;
	private final static String QK_U_VMAG		= "u_Vmag" ;
	private final static String QK_BV			= "BV" ;
	private final static String QK_U_BV			= "u_BV" ;
	private final static String QK_UB			= "UB" ;
	private final static String QK_U_UB			= "u_UB" ;
	private final static String QK_RI			= "RI" ;
	private final static String QK_N_RI			= "n_RI" ;
	private final static String QK_SPTYPE		= "SpType" ;
	private final static String QK_N_SPTYPE		= "n_SpType" ;
	private final static String QK_PMRA			= "pmRA" ;
	private final static String QK_PMDE			= "pmDE" ;
	private final static String QK_N_PARALLAX	= "n_Parallax" ;
	private final static String QK_PARALLAX		= "Parallax" ;
	private final static String QK_RADVEL		= "RadVel" ;
	private final static String QK_N_RADVEL		= "n_RadVel" ;
	private final static String QK_L_ROTVEL		= "l_RotVel" ;
	private final static String QK_ROTVEL		= "RotVel" ;
	private final static String QK_U_ROTVEL		= "u_RotVel" ;
	private final static String QK_DMAG			= "Dmag" ;
	private final static String QK_SEP			= "Sep" ;
	private final static String QK_MULTID		= "MultID" ;
	private final static String QK_MULTCNT		= "MultCnt" ;
	private final static String QK_NOTEFLAG		= "NoteFlag" ;

	public String HR         ; //  [1/9110]+ Harvard Revised Number = Bright Star Number
	public String Name       ; //  Name, generally Bayer and/or Flamsteed name
	public String DM         ; //  Durchmusterung Identification (zone in bytes 17-19)
	public String HD         ; //  [1/225300]? Henry Draper Catalog Number
	public String SAO        ; //  [1/258997]? SAO Catalog Number
	public String FK5        ; //  ? FK5 star Number
	public String IRflag     ; //  [I] I if infrared source
	public String r_IRflag   ; // *[ ':] Coded reference for infrared source
	public String Multiple   ; // *[AWDIRS] Double or multiple-star code
	public String ADS        ; //  Aitken's Double Star Catalog (ADS) designation
	public String ADScomp    ; //  ADS number components
	public String VarID      ; //  Variable star identification
	public String RAh1900    ; //  ?Hours RA, equinox B1900, epoch 1900.0 (1)
	public String RAm1900    ; //  ?Minutes RA, equinox B1900, epoch 1900.0 (1)
	public String RAs1900    ; //  ?Seconds RA, equinox B1900, epoch 1900.0 (1)
	public String DE1900     ; //  ?Sign Dec, equinox B1900, epoch 1900.0 (1)
	public String DEd1900    ; //  ?Degrees Dec, equinox B1900, epoch 1900.0 (1)
	public String DEm1900    ; //  ?Minutes Dec, equinox B1900, epoch 1900.0 (1)
	public String DEs1900    ; //  ?Seconds Dec, equinox B1900, epoch 1900.0 (1)
	public String RAh        ; //  ?Hours RA, equinox J2000, epoch 2000.0 (1)
	public String RAm        ; //  ?Minutes RA, equinox J2000, epoch 2000.0 (1)
	public String RAs        ; //  ?Seconds RA, equinox J2000, epoch 2000.0 (1)
	public String DE         ; //  ?Sign Dec, equinox J2000, epoch 2000.0 (1)
	public String DEd        ; //  ?Degrees Dec, equinox J2000, epoch 2000.0 (1)
	public String DEm        ; //  ?Minutes Dec, equinox J2000, epoch 2000.0 (1)
	public String DEs        ; //  ?Seconds Dec, equinox J2000, epoch 2000.0 (1)
	public String GLON       ; //  ?Galactic longitude (1)
	public String GLAT       ; //  ?Galactic latitude (1)
	public String Vmag       ; //  ?Visual magnitude (1)
	public String n_Vmag     ; // *[ HR] Visual magnitude code
	public String u_Vmag     ; //  [ :?] Uncertainty flag on V
	public String BV         ; //  ? B-V color in the UBV system
	public String u_BV       ; //  [ :?] Uncertainty flag on B-V
	public String UB         ; //  ? U-B color in the UBV system
	public String u_UB       ; //  [ :?] Uncertainty flag on U-B
	public String RI         ; //  ? R-I   in system specified by n_R-I
	public String n_RI       ; //  [CE:?D] Code for R-I system (Cousin, Eggen)
	public String SpType     ; //  Spectral type
	public String n_SpType   ; //  [evt] Spectral type code
	public String pmRA       ; //  ?Annual proper motion in RA J2000, FK5 system
	public String pmDE       ; //  ?Annual proper motion in Dec J2000, FK5 system
	public String n_Parallax ; //  [D] D indicates a dynamical parallax, otherwise a trigonometric parallax otherwise a trigonometric parallax
	public String Parallax   ; //  ? Trigonometric parallax (unless n_Parallax)
	public String RadVel     ; //  ? Heliocentric Radial Velocity
	public String n_RadVel   ; // *[V?SB123O ] Radial velocity comments
	public String l_RotVel   ; //  [<=> ] Rotational velocity limit characters
	public String RotVel     ; //  ? Rotational velocity, v sin i
	public String u_RotVel   ; //  [ :v] uncertainty and variability flag on RotVel
	public String Dmag       ; //  ? Magnitude difference of double, or brightest multiple
	public String Sep        ; //  ? Separation of components in Dmag if occultation binary.
	public String MultID     ; //  Identifications of components in Dmag
	public String MultCnt    ; //  ? Number of components assigned to a multiple
	public String NoteFlag   ; //  [*] a star indicates that there is a note (file notes.dat)

	// message key (MK_)
	private final static String MK_ERECLEN = "ereclen" ;
	private final static String MK_ERECVAL = "erecval" ;

	public CatalogADC5050Record( String data ) throws ParameterNotValidException {
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

		HR         = data.substring( 0, 4 ).trim() ;
		Name       = data.substring( 4, 14 ) ;
		DM         = data.substring( 14, 25 ).trim() ;
		HD         = data.substring( 25, 31 ).trim() ;
		SAO        = data.substring( 31, 37 ).trim() ;
		FK5        = data.substring( 37, 41 ).trim() ;
		IRflag     = data.substring( 41, 42 ).trim() ;
		r_IRflag   = data.substring( 42, 43 ).trim() ;
		Multiple   = data.substring( 43, 44 ).trim() ;
		ADS        = data.substring( 44, 49 ).trim() ;
		ADScomp    = data.substring( 49, 51 ).trim() ;
		VarID      = data.substring( 51, 60 ).trim() ;
		RAh1900    = data.substring( 60, 62 ).trim() ;
		RAm1900    = data.substring( 62, 64 ).trim() ;
		RAs1900    = data.substring( 64, 68 ).trim() ;
		DE1900     = data.substring( 68, 69 ).trim() ;
		DEd1900    = data.substring( 69, 71 ).trim() ;
		DEm1900    = data.substring( 71, 73 ).trim() ;
		DEs1900    = data.substring( 73, 75 ).trim() ;
		RAh        = data.substring( 75, 77 ).trim() ;
		RAm        = data.substring( 77, 79 ).trim() ;
		RAs        = data.substring( 79, 83 ).trim() ;
		DE         = data.substring( 83, 84 ).trim() ;
		DEd        = data.substring( 84, 86 ).trim() ;
		DEm        = data.substring( 86, 88 ).trim() ;
		DEs        = data.substring( 88, 90 ).trim() ;
		GLON       = data.substring( 90, 96 ).trim() ;
		GLAT       = data.substring( 96, 102 ).trim() ;
		Vmag       = data.substring( 102, 107 ).trim() ;
		n_Vmag     = data.substring( 107, 108 ).trim() ;
		u_Vmag     = data.substring( 108, 109 ).trim() ;
		BV         = data.substring( 109, 114 ).trim() ;
		u_BV       = data.substring( 114, 115 ).trim() ;
		UB         = data.substring( 115, 120 ).trim() ;
		u_UB       = data.substring( 120, 121 ).trim() ;
		RI         = data.substring( 121, 126 ).trim() ;
		n_RI       = data.substring( 126, 127 ).trim() ;
		SpType     = data.substring( 127, 147 ).trim() ;
		n_SpType   = data.substring( 147, 148 ).trim() ;
		pmRA       = data.substring( 148, 154 ).trim() ;
		pmDE       = data.substring( 154, 160 ).trim() ;
		n_Parallax = data.substring( 160, 161 ).trim() ;
		Parallax   = data.substring( 161, 166 ).trim() ;
		RadVel     = data.substring( 166, 170 ).trim() ;
		n_RadVel   = data.substring( 170, 174 ).trim() ;
		l_RotVel   = data.substring( 174, 176 ).trim() ;
		RotVel     = data.substring( 176, 179 ).trim() ;
		u_RotVel   = data.substring( 179, 180 ).trim() ;
		Dmag       = data.substring( 180, 184 ).trim() ;
		Sep        = data.substring( 184, 190 ).trim() ;
		MultID     = data.substring( 190, 194 ).trim() ;
		MultCnt    = data.substring( 194, 196 ).trim() ;
		NoteFlag   = data.substring( 196, 197 ).trim() ;
	}

	public void register() {
		SubstituteCatalog cat ;
		String sub ;

		cat = new SubstituteCatalog( this ) ;

		sub = cat.substitute( QK_HR, QK_HR ) ;
		Registry.register( sub, HR ) ;
		new Name( Name ).register() ;
		sub = cat.substitute( QK_NAME, QK_NAME ) ;
		Registry.register( sub, Name.trim() ) ;
		sub = cat.substitute( QK_DM, QK_DM ) ;
		Registry.register( sub, DM ) ;
		sub = cat.substitute( QK_HD, QK_HD ) ;
		Registry.register( sub, HD ) ;
		sub = cat.substitute( QK_SAO, QK_SAO ) ;
		Registry.register( sub, SAO ) ;
		sub = cat.substitute( QK_FK5, QK_FK5 ) ;
		Registry.register( sub, FK5 ) ;
		sub = cat.substitute( QK_IRFLAG, QK_IRFLAG ) ;
		Registry.register( sub, IRflag ) ;
		sub = cat.substitute( QK_R_IRFLAG, QK_R_IRFLAG ) ;
		Registry.register( sub, r_IRflag ) ;
		sub = cat.substitute( QK_MULTIPLE, QK_MULTIPLE ) ;
		Registry.register( sub, Multiple ) ;
		sub = cat.substitute( QK_ADS, QK_ADS ) ;
		Registry.register( sub, ADS ) ;
		sub = cat.substitute( QK_ADSCOMP, QK_ADSCOMP ) ;
		Registry.register( sub, ADScomp ) ;
		sub = cat.substitute( QK_VARID, QK_VARID ) ;
		Registry.register( sub, VarID ) ;
		sub = cat.substitute( QK_RAH1900, QK_RAH1900 ) ;
		Registry.register( sub, RAh1900 ) ;
		sub = cat.substitute( QK_RAM1900, QK_RAM1900 ) ;
		Registry.register( sub, RAm1900 ) ;
		sub = cat.substitute( QK_RAS1900, QK_RAS1900 ) ;
		Registry.register( sub, RAs1900 ) ;
		sub = cat.substitute( QK_DE1900, QK_DE1900 ) ;
		Registry.register( sub, DE1900 ) ;
		sub = cat.substitute( QK_DED1900, QK_DED1900 ) ;
		Registry.register( sub, DEd1900 ) ;
		sub = cat.substitute( QK_DEM1900, QK_DEM1900 ) ;
		Registry.register( sub, DEm1900 ) ;
		sub = cat.substitute( QK_DES1900, QK_DES1900 ) ;
		Registry.register( sub, DEs1900 ) ;
		sub = cat.substitute( QK_RAH, QK_RAH ) ;
		Registry.register( sub, RAh ) ;
		sub = cat.substitute( QK_RAM, QK_RAM ) ;
		Registry.register( sub, RAm ) ;
		sub = cat.substitute( QK_RAS, QK_RAS ) ;
		Registry.register( sub, RAs ) ;
		sub = cat.substitute( QK_DE, QK_DE ) ;
		Registry.register( sub, DE ) ;
		sub = cat.substitute( QK_DED, QK_DED ) ;
		Registry.register( sub, DEd ) ;
		sub = cat.substitute( QK_DEM, QK_DEM ) ;
		Registry.register( sub, DEm ) ;
		sub = cat.substitute( QK_DES, QK_DES ) ;
		Registry.register( sub, DEs ) ;
		sub = cat.substitute( QK_GLON, QK_GLON ) ;
		Registry.register( sub, GLON ) ;
		sub = cat.substitute( QK_GLAT, QK_GLAT ) ;
		Registry.register( sub, GLAT ) ;
		sub = cat.substitute( QK_VMAG, QK_VMAG ) ;
		Registry.register( sub, Vmag ) ;
		sub = cat.substitute( QK_N_VMAG, QK_N_VMAG ) ;
		Registry.register( sub, n_Vmag ) ;
		sub = cat.substitute( QK_U_VMAG, QK_U_VMAG ) ;
		Registry.register( sub, u_Vmag ) ;
		sub = cat.substitute( QK_BV, QK_BV ) ;
		Registry.register( sub, BV ) ;
		sub = cat.substitute( QK_U_BV, QK_U_BV ) ;
		Registry.register( sub, u_BV ) ;
		sub = cat.substitute( QK_UB, QK_UB ) ;
		Registry.register( sub, UB ) ;
		sub = cat.substitute( QK_U_UB, QK_U_UB ) ;
		Registry.register( sub, u_UB ) ;
		sub = cat.substitute( QK_RI, QK_RI ) ;
		Registry.register( sub, RI ) ;
		sub = cat.substitute( QK_N_RI, QK_N_RI ) ;
		Registry.register( sub, n_RI ) ;
		sub = cat.substitute( QK_SPTYPE, QK_SPTYPE ) ;
		Registry.register( sub, SpType ) ;
		sub = cat.substitute( QK_N_SPTYPE, QK_N_SPTYPE ) ;
		Registry.register( sub, n_SpType ) ;
		sub = cat.substitute( QK_PMRA, QK_PMRA ) ;
		Registry.register( sub, pmRA ) ;
		sub = cat.substitute( QK_PMDE, QK_PMDE ) ;
		Registry.register( sub, pmDE ) ;
		sub = cat.substitute( QK_N_PARALLAX, QK_N_PARALLAX ) ;
		Registry.register( sub, n_Parallax ) ;
		sub = cat.substitute( QK_PARALLAX, QK_PARALLAX ) ;
		Registry.register( sub, Parallax ) ;
		sub = cat.substitute( QK_RADVEL, QK_RADVEL ) ;
		Registry.register( sub, RadVel ) ;
		sub = cat.substitute( QK_N_RADVEL, QK_N_RADVEL ) ;
		Registry.register( sub, n_RadVel ) ;
		sub = cat.substitute( QK_L_ROTVEL, QK_L_ROTVEL ) ;
		Registry.register( sub, l_RotVel ) ;
		sub = cat.substitute( QK_ROTVEL, QK_ROTVEL ) ;
		Registry.register( sub, RotVel ) ;
		sub = cat.substitute( QK_U_ROTVEL, QK_U_ROTVEL ) ;
		Registry.register( sub, u_RotVel ) ;
		sub = cat.substitute( QK_DMAG, QK_DMAG ) ;
		Registry.register( sub, Dmag ) ;
		sub = cat.substitute( QK_SEP, QK_SEP ) ;
		Registry.register( sub, Sep ) ;
		sub = cat.substitute( QK_MULTID, QK_MULTID ) ;
		Registry.register( sub, MultID ) ;
		sub = cat.substitute( QK_MULTCNT, QK_MULTCNT ) ;
		Registry.register( sub, MultCnt ) ;
		sub = cat.substitute( QK_NOTEFLAG, QK_NOTEFLAG ) ;
		Registry.register( sub, NoteFlag ) ;
	}

	public void degister() {
		SubstituteCatalog cat ;
		String sub ;

		cat = new SubstituteCatalog( this ) ;

		sub = cat.substitute( QK_HR, QK_HR ) ;
		Registry.degister( sub ) ;
		new Name( Name ).degister() ;
		sub = cat.substitute( QK_NAME, QK_NAME ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_DM, QK_DM ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_HD, QK_HD ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_SAO, QK_SAO ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_FK5, QK_FK5 ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_IRFLAG, QK_IRFLAG ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_R_IRFLAG, QK_R_IRFLAG ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_MULTIPLE, QK_MULTIPLE ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_ADS, QK_ADS ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_ADSCOMP, QK_ADSCOMP ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_VARID, QK_VARID ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_RAH1900, QK_RAH1900 ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_RAM1900, QK_RAM1900 ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_RAS1900, QK_RAS1900 ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_DE1900, QK_DE1900 ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_DED1900, QK_DED1900 ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_DEM1900, QK_DEM1900 ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_DES1900, QK_DES1900 ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_RAH, QK_RAH ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_RAM, QK_RAM ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_RAS, QK_RAS ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_DE, QK_DE ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_DED, QK_DED ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_DEM, QK_DEM ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_DES, QK_DES ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_GLON, QK_GLON ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_GLAT, QK_GLAT ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_VMAG, QK_VMAG ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_N_VMAG, QK_N_VMAG ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_U_VMAG, QK_U_VMAG ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_BV, QK_BV ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_U_BV, QK_U_BV ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_UB, QK_UB ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_U_UB, QK_U_UB ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_RI, QK_RI ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_N_RI, QK_N_RI ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_SPTYPE, QK_SPTYPE ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_N_SPTYPE, QK_N_SPTYPE ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_PMRA, QK_PMRA ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_PMDE, QK_PMDE ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_N_PARALLAX, QK_N_PARALLAX ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_PARALLAX, QK_PARALLAX ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_RADVEL, QK_RADVEL ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_N_RADVEL, QK_N_RADVEL ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_L_ROTVEL, QK_L_ROTVEL ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_ROTVEL, QK_ROTVEL ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_U_ROTVEL, QK_U_ROTVEL ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_DMAG, QK_DMAG ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_SEP, QK_SEP ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_MULTID, QK_MULTID ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_MULTCNT, QK_MULTCNT ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_NOTEFLAG, QK_NOTEFLAG ) ;
		Registry.degister( sub ) ;
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
