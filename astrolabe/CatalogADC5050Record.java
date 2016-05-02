
package astrolabe;

import java.lang.reflect.Field;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

@SuppressWarnings("serial")
public class CatalogADC5050Record extends astrolabe.model.CatalogADC5050Record implements CatalogRecord {

	private final static String DEFAULT_TOKENPATTERN = ".+" ;

	private final static int CR_LENGTH = 198 ;

	public String HR         ; //  [1/9110]+ Harvard Revised Number = Bright Star Number
	public String Name       ; //  Name, generally Bayer and/or Flamsteed name
	public String flamsteed		;
	public String bayer			;
	public String bayerindex 	;
	public String constellation	;
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

	public CatalogADC5050Record( String data ) throws ParameterNotValidException {

		if ( data.length() != CR_LENGTH ) {
			throw new ParameterNotValidException(  Integer.toString( data.length() ) ) ;
		}

		HR         = data.substring( 0, 4 ).trim() ;
		Name       = data.substring( 4, 14 ) ;
		flamsteed		= Name.substring(0, 3).trim() ;
		bayer			= Name.substring(3, 6).trim() ;
		bayerindex		= Name.substring(6, 7).trim() ;
		constellation	= Name.substring(7, 10).trim() ;
		Name.trim() ;
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
		MessageCatalog m ;
		String key ;

		m = new MessageCatalog( ApplicationConstant.GC_APPLICATION ) ;

		key = m.message( ApplicationConstant.LK_ADC5050_HR ) ;
		AstrolabeRegistry.registerName( key, HR ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_NAME ) ;
		AstrolabeRegistry.registerName( key, Name ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_NAME_FLAMSTEED ) ;
		AstrolabeRegistry.registerName( key, flamsteed ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_NAME_BAYER ) ;
		bayer = Configuration.getValue(
				Configuration.getClassNode( this, null, ApplicationConstant.PN_GENERAL_REGISTRY ), bayer, bayer ) ;
		AstrolabeRegistry.registerName( key, bayer ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_NAME_BAYERINDEX ) ;
		AstrolabeRegistry.registerName( key, bayerindex ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_NAME_CONSTELLATION ) ;
		AstrolabeRegistry.registerName( key, constellation ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_DM ) ;
		AstrolabeRegistry.registerName( key, DM ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_HD ) ;
		AstrolabeRegistry.registerName( key, HD ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_SAO ) ;
		AstrolabeRegistry.registerName( key, SAO ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_FK5 ) ;
		AstrolabeRegistry.registerName( key, FK5 ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_IRFLAG ) ;
		AstrolabeRegistry.registerName( key, IRflag ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_R_IRFLAG ) ;
		AstrolabeRegistry.registerName( key, r_IRflag ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_MULTIPLE ) ;
		AstrolabeRegistry.registerName( key, Multiple ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_ADS ) ;
		AstrolabeRegistry.registerName( key, ADS ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_ADSCOMP ) ;
		AstrolabeRegistry.registerName( key, ADScomp ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_VARID ) ;
		AstrolabeRegistry.registerName( key, VarID ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_RAH1900 ) ;
		AstrolabeRegistry.registerName( key, RAh1900 ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_RAM1900 ) ;
		AstrolabeRegistry.registerName( key, RAm1900 ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_RAS1900 ) ;
		AstrolabeRegistry.registerName( key, RAs1900 ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_DE1900 ) ;
		AstrolabeRegistry.registerName( key, DE1900 ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_DED1900 ) ;
		AstrolabeRegistry.registerName( key, DEd1900 ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_DEM1900 ) ;
		AstrolabeRegistry.registerName( key, DEm1900 ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_DES1900 ) ;
		AstrolabeRegistry.registerName( key, DEs1900 ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_RAH ) ;
		AstrolabeRegistry.registerName( key, RAh ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_RAM ) ;
		AstrolabeRegistry.registerName( key, RAm ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_RAS ) ;
		AstrolabeRegistry.registerName( key, RAs ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_DE ) ;
		AstrolabeRegistry.registerName( key, DE ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_DED ) ;
		AstrolabeRegistry.registerName( key, DEd ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_DEM ) ;
		AstrolabeRegistry.registerName( key, DEm ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_DES ) ;
		AstrolabeRegistry.registerName( key, DEs ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_GLON ) ;
		AstrolabeRegistry.registerName( key, GLON ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_GLAT ) ;
		AstrolabeRegistry.registerName( key, GLAT ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_VMAG ) ;
		AstrolabeRegistry.registerName( key, Vmag ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_N_VMAG ) ;
		AstrolabeRegistry.registerName( key, n_Vmag ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_U_VMAG ) ;
		AstrolabeRegistry.registerName( key, u_Vmag ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_BV ) ;
		AstrolabeRegistry.registerName( key, BV ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_U_BV ) ;
		AstrolabeRegistry.registerName( key, u_BV ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_UB ) ;
		AstrolabeRegistry.registerName( key, UB ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_U_UB ) ;
		AstrolabeRegistry.registerName( key, u_UB ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_RI ) ;
		AstrolabeRegistry.registerName( key, RI ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_N_RI ) ;
		AstrolabeRegistry.registerName( key, n_RI ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_SPTYPE ) ;
		AstrolabeRegistry.registerName( key, SpType ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_N_SPTYPE ) ;
		AstrolabeRegistry.registerName( key, n_SpType ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_PMRA ) ;
		AstrolabeRegistry.registerName( key, pmRA ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_PMDE ) ;
		AstrolabeRegistry.registerName( key, pmDE ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_N_PARALLAX ) ;
		AstrolabeRegistry.registerName( key, n_Parallax ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_PARALLAX ) ;
		AstrolabeRegistry.registerName( key, Parallax ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_RADVEL ) ;
		AstrolabeRegistry.registerName( key, RadVel ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_N_RADVEL ) ;
		AstrolabeRegistry.registerName( key, n_RadVel ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_L_ROTVEL ) ;
		AstrolabeRegistry.registerName( key, l_RotVel ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_ROTVEL ) ;
		AstrolabeRegistry.registerName( key, RotVel ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_U_ROTVEL ) ;
		AstrolabeRegistry.registerName( key, u_RotVel ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_DMAG ) ;
		AstrolabeRegistry.registerName( key, Dmag ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_SEP ) ;
		AstrolabeRegistry.registerName( key, Sep ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_MULTID ) ;
		AstrolabeRegistry.registerName( key, MultID ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_MULTCNT ) ;
		AstrolabeRegistry.registerName( key, MultCnt ) ;
		key = m.message( ApplicationConstant.LK_ADC5050_NOTEFLAG ) ;
		AstrolabeRegistry.registerName( key, NoteFlag ) ;
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
