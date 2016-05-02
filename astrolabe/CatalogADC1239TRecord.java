
package astrolabe;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.vividsolutions.jts.geom.Coordinate;

@SuppressWarnings("serial")
public class CatalogADC1239TRecord extends astrolabe.model.CatalogADC1239TRecord implements CatalogRecord {

	private final static String DEFAULT_TOKENPATTERN = ".+" ;

	private final static int CR_TOKEN = 58 ;

	private final static String QK_CATALOG		= "Catalog" ;
	private final static String QK_TYC			= "TYC" ;
	private final static String QK_PROXY		= "Proxy" ;
	private final static String QK_RAHMS		= "RAhms" ;
	private final static String QK_DEDMS		= "DEdms" ;
	private final static String QK_VMAG			= "Vmag" ;
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
	private final static String QK_NASTRO		= "Nastro" ;
	private final static String QK_F2			= "F2" ;
	private final static String QK_HIP			= "HIP" ;
	private final static String QK_BTMAG		= "BTmag" ;
	private final static String QK_E_BTMAG		= "e_BTmag" ;
	private final static String QK_VTMAG		= "VTmag" ;
	private final static String QK_E_VTMAG		= "e_VTmag" ;
	private final static String QK_R_BTMAG		= "r_BTmag" ;
	private final static String QK_BV			= "BV" ;
	private final static String QK_E_BV			= "e_BV" ;
	private final static String QK_Q			= "Q" ;
	private final static String QK_FS			= "Fs" ;
	private final static String QK_SOURCE		= "Source" ;
	private final static String QK_NPHOTO		= "Nphoto" ;
	private final static String QK_VTSCAT		= "VTscat" ;
	private final static String QK_VTMAX		= "VTmax" ;
	private final static String QK_VTMIN		= "VTmin" ;
	private final static String QK_VAR			= "Var" ;
	private final static String QK_VARFLAG		= "VarFlag" ;
	private final static String QK_MULTFLAG		= "MultFlag" ;
	private final static String QK_MOREPHOTO	= "morePhoto" ;
	private final static String QK_M_HIP		= "m_HIP" ;
	private final static String QK_PPM			= "PPM" ;
	private final static String QK_HD			= "HD" ;
	private final static String QK_BD			= "BD" ;
	private final static String QK_COD			= "CoD" ;
	private final static String QK_CPD			= "CPD" ;
	private final static String QK_REMARK		= "Remark" ;

	public String   Catalog   ; //  [T] Catalogue (T = Tycho)
	public String   TYC       ; // *TYC1-3 (TYC number)
	public String   Proxy     ; //  [HT]? Proximity flag
	public String   RAhms     ; //  Right ascension in h m s, (ICRS, Eq=J2000)
	public String   DEdms     ; //  Declination in deg ' ",  (ICRS, Eq=J2000)
	public String   Vmag      ; //  ? Magnitude in Johnson V

	public String r_Vmag      ; // *[BDTV] Source of magnitude
	public String   RAdeg     ; // *alpha, degrees (ICRS, Eq=J2000)
	public String   DEdeg     ; // *delta, degrees (ICRS, Eq=J2000)
	public String   AstroRef  ; // *[X]? Reference flag for astrometry
	public String   Plx       ; // *? Trigonometric parallax
	public String   pmRA      ; // *? Proper motion mu_alpha.cos(delta), ICRS
	public String   pmDE      ; // *? Proper motion mu_delta, ICRS
	public String e_RAdeg     ; // *? Standard error in RA*cos(delta)
	public String e_DEdeg     ; // *? Standard error in DE
	public String e_Plx       ; // *? Standard error in Plx
	public String e_pmRA      ; // *? Standard error in pmRA
	public String e_pmDE      ; // *? Standard error in pmDE
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
	public String   Nastro    ; //  ? Number of transits for astrometry
	public String   F2        ; // *? Goodness-of-fit parameter
	public String   HIP       ; //  ? Hipparcos HIP number
	public String   BTmag     ; //  ? Mean BT magnitude
	public String e_BTmag     ; //  ? Standard error in BTmag
	public String   VTmag     ; //  ? Mean VT magnitude
	public String e_VTmag     ; //  ? Standard error in VTmag
	public String r_BTmag     ; // *[DMNT] Source of photometry
	public String   BV        ; //  ? Johnson B-V colour
	public String e_BV        ; //  ? Standard error on B-V

	public String   Q         ; // *? Astrometric quality flag, Q
	public String   Fs        ; //  ? Signal-to-noise ratio of the star image
	public String   Source    ; // *[HPR] Source of astrometric data
	public String   Nphoto    ; //  ? Number of transits for photometry
	public String   VTscat    ; //  ? Estimate of VTmag scatter
	public String   VTmax     ; //  ? VTmag at maximum (15th percentile)
	public String   VTmin     ; //  ? VTmag at minimum (85th percentile)
	public String   Var       ; // *[GN]? Known variability from GCVS/NSV
	public String   VarFlag   ; // *[UVW]? Variability from Tycho
	public String   MultFlag  ; // *[DRSYZ]? Duplicity from Tycho
	public String   morePhoto ; //  [AB]  Epoch photometry in Annex A or B
	public String m_HIP       ; //  CCDM component identifier
	public String   PPM       ; // *[1/789676]? PPM and Supplement
	public String   HD        ; //  [1/359083]? HD cat. <III/135>
	public String   BD        ; //  Bonner DM <I/119>, <I/122>
	public String   CoD       ; //  Cordoba DM <I/114>
	public String   CPD       ; //  Cape Photographic DM <I/108>
	public String   Remark    ; // *[JKLM] Notes

	// message key (MK_)
	private final static String MK_ERECLEN = "ereclen" ;
	private final static String MK_ERECVAL = "erecval" ;

	public CatalogADC1239TRecord( String data ) throws ParameterNotValidException {
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
		TYC       = token[1].trim() ;
		Proxy     = token[2].trim() ;
		RAhms     = token[3].trim() ;
		DEdms     = token[4].trim() ;
		Vmag      = token[5].trim() ;

		r_Vmag    = token[7].trim() ;
		RAdeg     = token[8].trim() ;
		DEdeg     = token[9].trim() ;
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
		Nastro    = token[29].trim() ;
		F2        = token[30].trim() ;
		HIP       = token[31].trim() ;
		BTmag     = token[32].trim() ;
		e_BTmag   = token[33].trim() ;
		VTmag     = token[34].trim() ;
		e_VTmag   = token[35].trim() ;
		r_BTmag   = token[36].trim() ;
		BV        = token[37].trim() ;
		e_BV      = token[38].trim() ;

		Q         = token[40].trim() ;
		Fs        = token[41].trim() ;
		Source    = token[42].trim() ;
		Nphoto    = token[43].trim() ;
		VTscat    = token[44].trim() ;
		VTmax     = token[45].trim() ;
		VTmin     = token[46].trim() ;
		Var       = token[47].trim() ;
		VarFlag   = token[48].trim() ;
		MultFlag  = token[49].trim() ;
		morePhoto = token[50].trim() ;
		m_HIP     = token[51].trim() ;
		PPM       = token[52].trim() ;
		HD        = token[53].trim() ;
		BD        = token[54].trim() ;
		CoD       = token[55].trim() ;
		CPD       = token[56].trim() ;
		Remark    = token[57].trim() ;
	}

	public void register() {
		SubstituteCatalog cat ;
		String sub ;

		cat = new SubstituteCatalog( this ) ;

		sub = cat.substitute( QK_CATALOG, QK_CATALOG ) ;
		Registry.register( sub, Catalog ) ;
		sub = cat.substitute( QK_TYC, QK_TYC ) ;
		Registry.register( sub, TYC ) ;
		sub = cat.substitute( QK_PROXY, QK_PROXY ) ;
		Registry.register( sub, Proxy ) ;
		sub = cat.substitute( QK_RAHMS, QK_RAHMS ) ;
		Registry.register( sub, RAhms ) ;
		sub = cat.substitute( QK_DEDMS, QK_DEDMS ) ;
		Registry.register( sub, DEdms ) ;
		sub = cat.substitute( QK_VMAG, QK_VMAG ) ;
		Registry.register( sub, Vmag ) ;
		sub = cat.substitute( QK_R_VMAG, QK_R_VMAG ) ;
		Registry.register( sub, r_Vmag ) ;
		sub = cat.substitute( QK_RADEG, QK_RADEG ) ;
		Registry.register( sub, RAdeg ) ;
		sub = cat.substitute( QK_DEDEG, QK_DEDEG ) ;
		Registry.register( sub, DEdeg ) ;
		sub = cat.substitute( QK_ASTROREF, QK_ASTROREF ) ;
		Registry.register( sub, AstroRef ) ;
		sub = cat.substitute( QK_PLX, QK_PLX ) ;
		Registry.register( sub, Plx ) ;
		sub = cat.substitute( QK_PMRA, QK_PMRA ) ;
		Registry.register( sub, pmRA ) ;
		sub = cat.substitute( QK_PMDE, QK_PMDE ) ;
		Registry.register( sub, pmDE ) ;
		sub = cat.substitute( QK_E_RADEG, QK_E_RADEG ) ;
		Registry.register( sub, e_RAdeg ) ;
		sub = cat.substitute( QK_E_DEDEG, QK_E_DEDEG ) ;
		Registry.register( sub, e_DEdeg ) ;
		sub = cat.substitute( QK_E_PLX, QK_E_PLX ) ;
		Registry.register( sub, e_Plx ) ;
		sub = cat.substitute( QK_E_PMRA, QK_E_PMRA ) ;
		Registry.register( sub, e_pmRA ) ;
		sub = cat.substitute( QK_E_PMDE, QK_E_PMDE ) ;
		Registry.register( sub, e_pmDE ) ;
		sub = cat.substitute( QK_DERA, QK_DERA ) ;
		Registry.register( sub, DERA ) ;
		sub = cat.substitute( QK_PLXRA, QK_PLXRA ) ;
		Registry.register( sub, PlxRA ) ;
		sub = cat.substitute( QK_PLXDE, QK_PLXDE ) ;
		Registry.register( sub, PlxDE ) ;
		sub = cat.substitute( QK_PMRARA, QK_PMRARA ) ;
		Registry.register( sub, pmRARA ) ;
		sub = cat.substitute( QK_PMRADE, QK_PMRADE ) ;
		Registry.register( sub, pmRADE ) ;
		sub = cat.substitute( QK_PMRAPLX, QK_PMRAPLX ) ;
		Registry.register( sub, pmRAPlx ) ;
		sub = cat.substitute( QK_PMDERA, QK_PMDERA ) ;
		Registry.register( sub, pmDERA ) ;
		sub = cat.substitute( QK_PMDEDE, QK_PMDEDE ) ;
		Registry.register( sub, pmDEDE ) ;
		sub = cat.substitute( QK_PMDEPLX, QK_PMDEPLX ) ;
		Registry.register( sub, pmDEPlx ) ;
		sub = cat.substitute( QK_PMDEPMRA, QK_PMDEPMRA ) ;
		Registry.register( sub, pmDEpmRA ) ;
		sub = cat.substitute( QK_NASTRO, QK_NASTRO ) ;
		Registry.register( sub, Nastro ) ;
		sub = cat.substitute( QK_F2, QK_F2 ) ;
		Registry.register( sub, F2 ) ;
		sub = cat.substitute( QK_HIP, QK_HIP ) ;
		Registry.register( sub, HIP ) ;
		sub = cat.substitute( QK_BTMAG, QK_BTMAG ) ;
		Registry.register( sub, BTmag ) ;
		sub = cat.substitute( QK_E_BTMAG, QK_E_BTMAG ) ;
		Registry.register( sub, e_BTmag ) ;
		sub = cat.substitute( QK_VTMAG, QK_VTMAG ) ;
		Registry.register( sub, VTmag ) ;
		sub = cat.substitute( QK_E_VTMAG, QK_E_VTMAG ) ;
		Registry.register( sub, e_VTmag ) ;
		sub = cat.substitute( QK_R_BTMAG, QK_R_BTMAG ) ;
		Registry.register( sub, r_BTmag ) ;
		sub = cat.substitute( QK_BV, QK_BV ) ;
		Registry.register( sub, BV ) ;
		sub = cat.substitute( QK_E_BV, QK_E_BV ) ;
		Registry.register( sub, e_BV ) ;
		sub = cat.substitute( QK_Q, QK_Q ) ;
		Registry.register( sub, Q ) ;
		sub = cat.substitute( QK_FS, QK_FS ) ;
		Registry.register( sub, Fs ) ;
		sub = cat.substitute( QK_SOURCE, QK_SOURCE ) ;
		Registry.register( sub, Source ) ;
		sub = cat.substitute( QK_NPHOTO, QK_NPHOTO ) ;
		Registry.register( sub, Nphoto ) ;
		sub = cat.substitute( QK_VTSCAT, QK_VTSCAT ) ;
		Registry.register( sub, VTscat ) ;
		sub = cat.substitute( QK_VTMAX, QK_VTMAX ) ;
		Registry.register( sub, VTmax ) ;
		sub = cat.substitute( QK_VTMIN, QK_VTMIN ) ;
		Registry.register( sub, VTmin ) ;
		sub = cat.substitute( QK_VAR, QK_VAR ) ;
		Registry.register( sub, Var ) ;
		sub = cat.substitute( QK_VARFLAG, QK_VARFLAG ) ;
		Registry.register( sub, VarFlag ) ;
		sub = cat.substitute( QK_MULTFLAG, QK_MULTFLAG ) ;
		Registry.register( sub, MultFlag ) ;
		sub = cat.substitute( QK_MOREPHOTO, QK_MOREPHOTO ) ;
		Registry.register( sub, morePhoto ) ;
		sub = cat.substitute( QK_M_HIP, QK_M_HIP ) ;
		Registry.register( sub, m_HIP ) ;
		sub = cat.substitute( QK_PPM, QK_PPM ) ;
		Registry.register( sub, PPM ) ;
		sub = cat.substitute( QK_HD, QK_HD ) ;
		Registry.register( sub, HD ) ;
		sub = cat.substitute( QK_BD, QK_BD ) ;
		Registry.register( sub, BD ) ;
		sub = cat.substitute( QK_COD, QK_COD ) ;
		Registry.register( sub, CoD ) ;
		sub = cat.substitute( QK_CPD, QK_CPD ) ;
		Registry.register( sub, CPD ) ;
		sub = cat.substitute( QK_REMARK, QK_REMARK ) ;
		Registry.register( sub, Remark ) ;
	}

	public void degister() {
		SubstituteCatalog cat ;
		String sub ;

		cat = new SubstituteCatalog( this ) ;

		sub = cat.substitute( QK_CATALOG, QK_CATALOG ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_TYC, QK_TYC ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_PROXY, QK_PROXY ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_RAHMS, QK_RAHMS ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_DEDMS, QK_DEDMS ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_VMAG, QK_VMAG ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_R_VMAG, QK_R_VMAG ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_RADEG, QK_RADEG ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_DEDEG, QK_DEDEG ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_ASTROREF, QK_ASTROREF ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_PLX, QK_PLX ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_PMRA, QK_PMRA ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_PMDE, QK_PMDE ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_E_RADEG, QK_E_RADEG ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_E_DEDEG, QK_E_DEDEG ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_E_PLX, QK_E_PLX ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_E_PMRA, QK_E_PMRA ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_E_PMDE, QK_E_PMDE ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_DERA, QK_DERA ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_PLXRA, QK_PLXRA ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_PLXDE, QK_PLXDE ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_PMRARA, QK_PMRARA ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_PMRADE, QK_PMRADE ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_PMRAPLX, QK_PMRAPLX ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_PMDERA, QK_PMDERA ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_PMDEDE, QK_PMDEDE ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_PMDEPLX, QK_PMDEPLX ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_PMDEPMRA, QK_PMDEPMRA ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_NASTRO, QK_NASTRO ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_F2, QK_F2 ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_HIP, QK_HIP ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_BTMAG, QK_BTMAG ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_E_BTMAG, QK_E_BTMAG ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_VTMAG, QK_VTMAG ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_E_VTMAG, QK_E_VTMAG ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_R_BTMAG, QK_R_BTMAG ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_BV, QK_BV ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_E_BV, QK_E_BV ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_Q, QK_Q ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_FS, QK_FS ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_SOURCE, QK_SOURCE ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_NPHOTO, QK_NPHOTO ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_VTSCAT, QK_VTSCAT ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_VTMAX, QK_VTMAX ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_VTMIN, QK_VTMIN ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_VAR, QK_VAR ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_VARFLAG, QK_VARFLAG ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_MULTFLAG, QK_MULTFLAG ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_MOREPHOTO, QK_MOREPHOTO ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_M_HIP, QK_M_HIP ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_PPM, QK_PPM ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_HD, QK_HD ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_BD, QK_BD ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_COD, QK_COD ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_CPD, QK_CPD ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_REMARK, QK_REMARK ) ;
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

	public Coordinate[] list() {
		return new Coordinate[] { new Coordinate( RA(), de() ) } ;
	}
}
