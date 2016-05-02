
package astrolabe;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

@SuppressWarnings("serial")
public class CatalogADC1239HRecord extends astrolabe.model.CatalogADC1239HRecord implements CatalogRecord {

	private final static String DEFAULT_TOKENPATTERN = ".+" ;

	private final static int CR_TOKEN = 78 ;

	private final static String QK_CATALOG		= "Catalog" ;
	private final static String QK_HIP			= "HIP" ;
	private final static String QK_PROXY		= "Proxy" ;
	private final static String QK_RAHMS		= "RAhms" ;
	private final static String QK_DEDMS		= "DEdms" ;
	private final static String QK_VMAG			= "Vmag" ;
	private final static String QK_VARFLAG		= "VarFlag" ;
	private final static String QK_R_VMAG		= "r_Vmag" ;
	private final static String QK_RADEG		= "RAdeg" ;
	private final static String QK_DEDEG		= "DEdeg" ;
	private final static String QK_ASTROREF		= "AstroRef" ;
	private final static String QK_PLX			= "Plx" ;
	private final static String QK_PMRA			= "pmRA" ;
	private final static String QK_PMDE			= "pmDE" ;
	private final static String QK_E_RADEG		= "e_RAdeg" ;
	private final static String QK_E_DEDEG		= "e_DEdeg" ;
	private final static String QK_E_PLX		= "e_Plx" ;
	private final static String QK_E_PMRA		= "e_pmRA" ;
	private final static String QK_E_PMDE		= "e_pmDE" ;
	private final static String QK_DERA			= "DERA" ;
	private final static String QK_PLXRA		= "PlxRA" ;
	private final static String QK_PLXDE		= "PlxDE" ;
	private final static String QK_PMRARA		= "pmRARA" ;
	private final static String QK_PMRADE		= "pmRADE" ;
	private final static String QK_PMRAPLX		= "pmRAPlx" ;
	private final static String QK_PMDERA		= "pmDERA" ;
	private final static String QK_PMDEDE		= "pmDEDE" ;
	private final static String QK_PMDEPLX		= "pmDEPlx" ;
	private final static String QK_PMDEPMRA		= "pmDEpmRA" ;
	private final static String QK_F1			= "F1" ;
	private final static String QK_F2			= "F2" ;
	private final static String QK_BTMAG		= "BTmag" ;
	private final static String QK_E_BTMAG		= "e_BTmag" ;
	private final static String QK_VTMAG		= "VTmag" ;
	private final static String QK_E_VTMAG		= "e_VTmag" ;
	private final static String QK_M_BTMAG		= "m_BTmag" ;
	private final static String QK_BV			= "BV" ;
	private final static String QK_E_BV			= "e_BV" ;
	private final static String QK_R_BV			= "r_BV" ;
	private final static String QK_VI			= "VI" ;
	private final static String QK_E_VI			= "e_VI" ;
	private final static String QK_R_VI			= "r_VI" ;
	private final static String QK_COMBMAG		= "CombMag" ;
	private final static String QK_HPMAG		= "Hpmag" ;
	private final static String QK_E_HPMAG		= "e_Hpmag" ;
	private final static String QK_HPSCAT		= "Hpscat" ;
	private final static String QK_O_HPMAG		= "o_Hpmag" ;
	private final static String QK_M_HPMAG		= "m_Hpmag" ;
	private final static String QK_HPMAX		= "Hpmax" ;
	private final static String QK_HPMIN		= "HPmin" ;
	private final static String QK_PERIOD		= "Period" ;
	private final static String QK_HVARTYPE		= "HvarType" ;
	private final static String QK_MOREVAR		= "moreVar" ;
	private final static String QK_MOREPHOTO	= "morePhoto" ;
	private final static String QK_CCDM			= "CCDM" ;
	private final static String QK_N_CCDM		= "n_CCDM" ;
	private final static String QK_NSYS			= "Nsys" ;
	private final static String QK_NCOMP		= "Ncomp" ;
	private final static String QK_MULTFLAG		= "MultFlag" ;
	private final static String QK_SOURCE		= "Source" ;
	private final static String QK_QUAL			= "Qual" ;
	private final static String QK_M_HIP		= "m_HIP" ;
	private final static String QK_THETA		= "theta" ;
	private final static String QK_RHO			= "rho" ;
	private final static String QK_E_RHO		= "e_rho" ;
	private final static String QK_DHP			= "dHp" ;
	private final static String QK_E_DHP		= "e_dHp" ;
	private final static String QK_SURVEY		= "Survey" ;
	private final static String QK_CHART		= "Chart" ;
	private final static String QK_NOTES		= "Notes" ;
	private final static String QK_HD			= "HD" ;
	private final static String QK_BD			= "BD" ;
	private final static String QK_COD			= "CoD" ;
	private final static String QK_CPD			= "CPD" ;
	private final static String QK_VIRED		= "VIred" ;
	private final static String QK_SPTYPE		= "SpType" ;
	private final static String QK_R_SPTYPE		= "r_SpType" ;

	public String   Catalog   ; //  [H] Catalogue (H=Hipparcos)
	public String   HIP       ; //  Identifier (HIP number)
	public String   Proxy     ; //  [HT] Proximity flag
	public String   RAhms     ; // *Right ascension in h m s, ICRS (Eq=J2000)
	public String   DEdms     ; // *Declination in deg ' ", ICRS (Eq=J2000)
	public String   Vmag      ; //  ? Magnitude in Johnson V
	public String   VarFlag   ; // *[1,3]? Coarse variability flag
	public String r_Vmag      ; // *[GHT] Source of magnitude
	public String   RAdeg     ; // *? alpha, degrees (ICRS, Eq=J2000)
	public String   DEdeg     ; // *? delta, degrees (ICRS, Eq=J2000)
	public String   AstroRef  ; // *[*+A-Z] Reference flag for astrometry
	public String   Plx       ; //  ? Trigonometric parallax
	public String   pmRA      ; //  ? Proper motion mu_alpha.cos(delta), ICRS(H12)
	public String   pmDE      ; //  ? Proper motion mu_delta, ICRS
	public String e_RAdeg     ; //  ? Standard error in RA*cos(DEdeg)
	public String e_DEdeg     ; //  ? Standard error in DE
	public String e_Plx       ; //  ? Standard error in Plx
	public String e_pmRA      ; //  ? Standard error in pmRA
	public String e_pmDE      ; //  ? Standard error in pmDE
	public String   DERA      ; //  [-1/1]? Correlation, DE/RA*cos(delta)
	public String   PlxRA     ; //  [-1/1]? Correlation, Plx/RA*cos(delta)
	public String   PlxDE     ; //  [-1/1]? Correlation, Plx/DE
	public String   pmRARA    ; //  [-1/1]? Correlation, pmRA/RA*cos(delta)
	public String   pmRADE    ; //  [-1/1]? Correlation, pmRA/DE
	public String   pmRAPlx   ; //  [-1/1]? Correlation, pmRA/Plx
	public String   pmDERA    ; //  [-1/1]? Correlation, pmDE/RA*cos(delta)
	public String   pmDEDE    ; //  [-1/1]? Correlation, pmDE/DE
	public String   pmDEPlx   ; //  [-1/1]? Correlation, pmDE/Plx
	public String   pmDEpmRA  ; //  [-1/1]? Correlation, pmDE/pmRA
	public String   F1        ; //  ? Percentage of rejected data
	public String   F2        ; // *? Goodness-of-fit parameter

	public String   BTmag     ; //  ? Mean BT magnitude
	public String e_BTmag     ; //  ? Standard error on BTmag
	public String   VTmag     ; //  ? Mean VT magnitude
	public String e_VTmag     ; //  ? Standard error on VTmag
	public String m_BTmag     ; // *[A-Z*-] Reference flag for BT and VTmag
	public String   BV        ; //  ? Johnson B-V colour
	public String e_BV        ; //  ? Standard error on B-V
	public String r_BV        ; //  [GT] Source of B-V from Ground or Tycho
	public String   VI        ; //  ? Colour index in Cousins' system
	public String e_VI        ; //  ? Standard error on V-I
	public String r_VI        ; // *[A-T] Source of V-I
	public String   CombMag   ; //  [*] Flag for combined Vmag, B-V, V-I
	public String   Hpmag     ; // *? Median magnitude in Hipparcos system
	public String e_Hpmag     ; // *? Standard error on Hpmag
	public String   Hpscat    ; //  ? Scatter on Hpmag
	public String o_Hpmag     ; //  ? Number of observations for Hpmag
	public String m_Hpmag     ; // *[A-Z*-] Reference flag for Hpmag
	public String   Hpmax     ; //  ? Hpmag at maximum
	public String   HPmin     ; //  ? Hpmag at minimum
	public String   Period    ; //  ? Variability period
	public String   HvarType  ; // *[CDMPRU]? variability type
	public String   moreVar   ; // *[12] Additional data about variability
	public String   morePhoto ; //  [ABC] Light curve Annex
	public String   CCDM      ; //  CCDM identifier
	public String n_CCDM      ; // *[HIM] Historical status flag
	public String   Nsys      ; //  ? Number of entries with same CCDM
	public String   Ncomp     ; //  ? Number of components in this entry
	public String   MultFlag  ; // *[CGOVX] Double/Multiple Systems flag
	public String   Source    ; // *[PFILS] Astrometric source flag
	public String   Qual      ; // *[ABCDS] Solution quality
	public String m_HIP       ; //  Component identifiers
	public String   theta     ; //  ? Position angle between components
	public String   rho       ; //  ? Angular separation between components
	public String   e_rho     ; //  ? Standard error on rho
	public String   dHp       ; //  ? Magnitude difference of components
	public String e_dHp       ; //  ? Standard error on dHp
	public String   Survey    ; //  [S] Flag indicating a Survey Star
	public String   Chart     ; // *[DG] Identification Chart
	public String   Notes     ; // *[DGPWXYZ] Existence of notes
	public String   HD        ; //  [1/359083]? HD number <III/135>
	public String   BD        ; //  Bonner DM <I/119>, <I/122>
	public String   CoD       ; //  Cordoba Durchmusterung
	public String   CPD       ; //  Cape Photographic DM <I/108>
	public String   VIred     ; //  V-I used for reductions
	public String   SpType    ; //  Spectral type
	public String r_SpType    ; // *[1234GKSX]? Source of spectral type

	// message key (MK_)
	private final static String MK_ERECLEN = "ereclen" ;
	private final static String MK_ERECVAL = "erecval" ;

	public CatalogADC1239HRecord( String data ) throws ParameterNotValidException {
		String[] token ;
		MessageCatalog cat ;
		StringBuffer msg ;
		String fmt ;

		token = data.split( "\\|" ) ;
		if ( token.length != CR_TOKEN ) {
			cat = new MessageCatalog( this ) ;
			fmt = cat.message( MK_ERECLEN, null ) ;
			if ( fmt != null ) {
				msg = new StringBuffer() ;
				msg.append( MessageFormat.format( fmt, new Object[] { CR_TOKEN } ) ) ;
			} else
				msg = null ;

			throw new ParameterNotValidException( ParameterNotValidError.errmsg( token.length, msg.toString() ) ) ;
		}

		Catalog   = token[0].trim() ;
		HIP       = token[1].trim() ;
		Proxy     = token[2].trim() ;
		RAhms     = token[3].trim() ;
		DEdms     = token[4].trim() ;
		Vmag      = token[5].trim() ;
		VarFlag   = token[6].trim() ;
		r_Vmag    = token[7].trim() ;
		RAdeg     = token[8].trim() ;
		DEdeg     = token[8].trim() ;
		AstroRef  = token[10].trim() ;
		Plx       = token[11].trim() ;
		pmRA      = token[12].trim() ;
		pmDE      = token[13].trim() ;
		e_RAdeg   = token[14].trim() ;
		e_DEdeg   = token[15].trim() ;
		e_Plx     = token[16].trim() ;
		e_pmRA    = token[17].trim() ;
		e_pmDE    = token[18].trim() ;
		DERA      = token[19].trim() ;
		PlxRA     = token[20].trim() ;
		PlxDE     = token[21].trim() ;
		pmRARA    = token[22].trim() ;
		pmRADE    = token[23].trim() ;
		pmRAPlx   = token[24].trim() ;
		pmDERA    = token[25].trim() ;
		pmDEDE    = token[26].trim() ;
		pmDEPlx   = token[27].trim() ;
		pmDEpmRA  = token[28].trim() ;
		F1        = token[29].trim() ;
		F2        = token[30].trim() ;

		BTmag     = token[32].trim() ;
		e_BTmag   = token[33].trim() ;
		VTmag     = token[34].trim() ;
		e_VTmag   = token[35].trim() ;
		m_BTmag   = token[36].trim() ;
		BV        = token[37].trim() ;
		e_BV      = token[38].trim() ;
		r_BV      = token[39].trim() ;
		VI        = token[40].trim() ;
		e_VI      = token[41].trim() ;
		r_VI      = token[42].trim() ;
		CombMag   = token[43].trim() ;
		Hpmag     = token[44].trim() ;
		e_Hpmag   = token[45].trim() ;
		Hpscat    = token[46].trim() ;
		o_Hpmag   = token[47].trim() ;
		m_Hpmag   = token[48].trim() ;
		Hpmax     = token[49].trim() ;
		HPmin     = token[50].trim() ;
		Period    = token[51].trim() ;
		HvarType  = token[52].trim() ;
		moreVar   = token[53].trim() ;
		morePhoto = token[54].trim() ;
		CCDM      = token[55].trim() ;
		n_CCDM    = token[56].trim() ;
		Nsys      = token[57].trim() ;
		Ncomp     = token[58].trim() ;
		MultFlag  = token[59].trim() ;
		Source    = token[60].trim() ;
		Qual      = token[61].trim() ;
		m_HIP     = token[62].trim() ;
		theta     = token[63].trim() ;
		rho       = token[64].trim() ;
		e_rho     = token[65].trim() ;
		dHp       = token[66].trim() ;
		e_dHp     = token[67].trim() ;
		Survey    = token[68].trim() ;
		Chart     = token[69].trim() ;
		Notes     = token[70].trim() ;
		HD        = token[71].trim() ;
		BD        = token[72].trim() ;
		CoD       = token[73].trim() ;
		CPD       = token[74].trim() ;
		VIred     = token[75].trim() ;
		SpType    = token[76].trim() ;
		r_SpType  = token[77].trim() ;
	}

	public void register() {
		SubstituteCatalog cat ;
		String sub ;

		cat = new SubstituteCatalog( this ) ;

		sub = cat.substitute( QK_CATALOG, null ) ;
		Registry.register( sub, Catalog ) ;
		sub = cat.substitute( QK_HIP, null ) ;
		Registry.register( sub, HIP ) ;
		sub = cat.substitute( QK_PROXY, null ) ;
		Registry.register( sub, Proxy ) ;
		sub = cat.substitute( QK_RAHMS, null ) ;
		Registry.register( sub, RAhms ) ;
		sub = cat.substitute( QK_DEDMS, null ) ;
		Registry.register( sub, DEdms ) ;
		sub = cat.substitute( QK_VMAG, null ) ;
		Registry.register( sub, Vmag ) ;
		sub = cat.substitute( QK_VARFLAG, null ) ;
		Registry.register( sub, VarFlag ) ;
		sub = cat.substitute( QK_R_VMAG, null ) ;
		Registry.register( sub, r_Vmag ) ;
		sub = cat.substitute( QK_RADEG, null ) ;
		Registry.register( sub, RAdeg ) ;
		sub = cat.substitute( QK_DEDEG, null ) ;
		Registry.register( sub, DEdeg ) ;
		sub = cat.substitute( QK_ASTROREF, null ) ;
		Registry.register( sub, AstroRef ) ;
		sub = cat.substitute( QK_PLX, null ) ;
		Registry.register( sub, Plx ) ;
		sub = cat.substitute( QK_PMRA, null ) ;
		Registry.register( sub, pmRA ) ;
		sub = cat.substitute( QK_PMDE, null ) ;
		Registry.register( sub, pmDE ) ;
		sub = cat.substitute( QK_E_RADEG, null ) ;
		Registry.register( sub, e_RAdeg ) ;
		sub = cat.substitute( QK_E_DEDEG, null ) ;
		Registry.register( sub, e_DEdeg ) ;
		sub = cat.substitute( QK_E_PLX, null ) ;
		Registry.register( sub, e_Plx ) ;
		sub = cat.substitute( QK_E_PMRA, null ) ;
		Registry.register( sub, e_pmRA ) ;
		sub = cat.substitute( QK_E_PMDE, null ) ;
		Registry.register( sub, e_pmDE ) ;
		sub = cat.substitute( QK_DERA, null ) ;
		Registry.register( sub, DERA ) ;
		sub = cat.substitute( QK_PLXRA, null ) ;
		Registry.register( sub, PlxRA ) ;
		sub = cat.substitute( QK_PLXDE, null ) ;
		Registry.register( sub, PlxDE ) ;
		sub = cat.substitute( QK_PMRARA, null ) ;
		Registry.register( sub, pmRARA ) ;
		sub = cat.substitute( QK_PMRADE, null ) ;
		Registry.register( sub, pmRADE ) ;
		sub = cat.substitute( QK_PMRAPLX, null ) ;
		Registry.register( sub, pmRAPlx ) ;
		sub = cat.substitute( QK_PMDERA, null ) ;
		Registry.register( sub, pmDERA ) ;
		sub = cat.substitute( QK_PMDEDE, null ) ;
		Registry.register( sub, pmDEDE ) ;
		sub = cat.substitute( QK_PMDEPLX, null ) ;
		Registry.register( sub, pmDEPlx ) ;
		sub = cat.substitute( QK_PMDEPMRA, null ) ;
		Registry.register( sub, pmDEpmRA ) ;
		sub = cat.substitute( QK_F1, null ) ;
		Registry.register( sub, F1 ) ;
		sub = cat.substitute( QK_F2, null ) ;
		Registry.register( sub, F2 ) ;

		sub = cat.substitute( QK_BTMAG, null ) ;
		Registry.register( sub, BTmag ) ;
		sub = cat.substitute( QK_E_BTMAG, null ) ;
		Registry.register( sub, e_BTmag ) ;
		sub = cat.substitute( QK_VTMAG, null ) ;
		Registry.register( sub, VTmag ) ;
		sub = cat.substitute( QK_E_VTMAG, null ) ;
		Registry.register( sub, e_VTmag ) ;
		sub = cat.substitute( QK_M_BTMAG, null ) ;
		Registry.register( sub, m_BTmag ) ;
		sub = cat.substitute( QK_BV, null ) ;
		Registry.register( sub, BV ) ;
		sub = cat.substitute( QK_E_BV, null ) ;
		Registry.register( sub, e_BV ) ;
		sub = cat.substitute( QK_R_BV, null ) ;
		Registry.register( sub, r_BV ) ;
		sub = cat.substitute( QK_VI, null ) ;
		Registry.register( sub, VI ) ;
		sub = cat.substitute( QK_E_VI, null ) ;
		Registry.register( sub, e_VI ) ;
		sub = cat.substitute( QK_R_VI, null ) ;
		Registry.register( sub, r_VI ) ;
		sub = cat.substitute( QK_COMBMAG, null ) ;
		Registry.register( sub, CombMag ) ;
		sub = cat.substitute( QK_HPMAG, null ) ;
		Registry.register( sub, Hpmag ) ;
		sub = cat.substitute( QK_E_HPMAG, null ) ;
		Registry.register( sub, e_Hpmag ) ;
		sub = cat.substitute( QK_HPSCAT, null ) ;
		Registry.register( sub, Hpscat ) ;
		sub = cat.substitute( QK_O_HPMAG, null ) ;
		Registry.register( sub, o_Hpmag ) ;
		sub = cat.substitute( QK_M_HPMAG, null ) ;
		Registry.register( sub, m_Hpmag ) ;
		sub = cat.substitute( QK_HPMAX, null ) ;
		Registry.register( sub, Hpmax ) ;
		sub = cat.substitute( QK_HPMIN, null ) ;
		Registry.register( sub, HPmin ) ;
		sub = cat.substitute( QK_PERIOD, null ) ;
		Registry.register( sub, Period ) ;
		sub = cat.substitute( QK_HVARTYPE, null ) ;
		Registry.register( sub, HvarType ) ;
		sub = cat.substitute( QK_MOREVAR, null ) ;
		Registry.register( sub, moreVar ) ;
		sub = cat.substitute( QK_MOREPHOTO, null ) ;
		Registry.register( sub, morePhoto ) ;
		sub = cat.substitute( QK_CCDM, null ) ;
		Registry.register( sub, CCDM ) ;
		sub = cat.substitute( QK_N_CCDM, null ) ;
		Registry.register( sub, n_CCDM ) ;
		sub = cat.substitute( QK_NSYS, null ) ;
		Registry.register( sub, Nsys ) ;
		sub = cat.substitute( QK_NCOMP, null ) ;
		Registry.register( sub, Ncomp ) ;
		sub = cat.substitute( QK_MULTFLAG, null ) ;
		Registry.register( sub, MultFlag ) ;
		sub = cat.substitute( QK_SOURCE, null ) ;
		Registry.register( sub, Source ) ;
		sub = cat.substitute( QK_QUAL, null ) ;
		Registry.register( sub, Qual ) ;
		sub = cat.substitute( QK_M_HIP, null ) ;
		Registry.register( sub, m_HIP ) ;
		sub = cat.substitute( QK_THETA, null ) ;
		Registry.register( sub, theta ) ;
		sub = cat.substitute( QK_RHO, null ) ;
		Registry.register( sub, rho ) ;
		sub = cat.substitute( QK_E_RHO, null ) ;
		Registry.register( sub, e_rho ) ;
		sub = cat.substitute( QK_DHP, null ) ;
		Registry.register( sub, dHp ) ;
		sub = cat.substitute( QK_E_DHP, null ) ;
		Registry.register( sub, e_dHp ) ;
		sub = cat.substitute( QK_SURVEY, null ) ;
		Registry.register( sub, Survey ) ;
		sub = cat.substitute( QK_CHART, null ) ;
		Registry.register( sub, Chart ) ;
		sub = cat.substitute( QK_NOTES, null ) ;
		Registry.register( sub, Notes ) ;
		sub = cat.substitute( QK_HD, null ) ;
		Registry.register( sub, HD ) ;
		sub = cat.substitute( QK_BD, null ) ;
		Registry.register( sub, BD ) ;
		sub = cat.substitute( QK_COD, null ) ;
		Registry.register( sub, CoD ) ;
		sub = cat.substitute( QK_CPD, null ) ;
		Registry.register( sub, CPD ) ;
		sub = cat.substitute( QK_VIRED, null ) ;
		Registry.register( sub, VIred ) ;
		sub = cat.substitute( QK_SPTYPE, null ) ;
		Registry.register( sub, SpType ) ;
		sub = cat.substitute( QK_R_SPTYPE, null ) ;
		Registry.register( sub, r_SpType ) ;
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
		String rams[] ;
		double ra ;

		rams = RAhms.split( "\\p{Space}+" ) ;
		ra = new Double( rams[0] ).doubleValue()
		+new Double( rams[1] ).doubleValue()/60.
		+new Double( rams[2] ).doubleValue()/3600. ;

		return ra ;
	}

	public double de() {
		String dems[] ;
		double de ;

		dems = DEdms.split( "\\p{Space}+" ) ;
		de = new Double( dems[0] ).doubleValue()
		+new Double( dems[1] ).doubleValue()/60.
		+new Double( dems[2] ).doubleValue()/3600. ;

		return de ;
	}

	public List<double[]> list() {
		List<double[]> r = new java.util.Vector<double[]>() ;

		r.add( new double[] { RA(), de() } ) ;

		return r ;
	}
}
