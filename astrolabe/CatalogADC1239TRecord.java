
package astrolabe;

import java.lang.reflect.Field;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

@SuppressWarnings("serial")
public class CatalogADC1239TRecord extends astrolabe.model.CatalogADC1239TRecord implements CatalogRecord {

	private final static String DEFAULT_TOKENPATTERN = ".+" ;

	private final static int CR_TOKEN = 58 ;

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

	public CatalogADC1239TRecord( String data ) throws ParameterNotValidException {
		String[] token ;

		token = data.split( "\\|" ) ;
		if ( token.length != CR_TOKEN ) {
			throw new ParameterNotValidException( Integer.toString( token.length ) ) ;
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
		MessageCatalog m ;
		String key ;

		m = new MessageCatalog( ApplicationConstant.GC_APPLICATION ) ;

		key = m.message( ApplicationConstant.LK_ADC1239T_CATALOG ) ;
		AstrolabeRegistry.registerName( key, Catalog ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_TYC ) ;
		AstrolabeRegistry.registerName( key, TYC ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_PROXY ) ;
		AstrolabeRegistry.registerName( key, Proxy ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_RAHMS ) ;
		AstrolabeRegistry.registerName( key, RAhms ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_DEDMS ) ;
		AstrolabeRegistry.registerName( key, DEdms ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_VMAG ) ;
		AstrolabeRegistry.registerName( key, Vmag ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_R_VMAG ) ;
		AstrolabeRegistry.registerName( key, r_Vmag ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_RADEG ) ;
		AstrolabeRegistry.registerName( key, RAdeg ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_DEDEG ) ;
		AstrolabeRegistry.registerName( key, DEdeg ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_ASTROREF ) ;
		AstrolabeRegistry.registerName( key, AstroRef ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_PLX ) ;
		AstrolabeRegistry.registerName( key, Plx ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_PMRA ) ;
		AstrolabeRegistry.registerName( key, pmRA ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_PMDE ) ;
		AstrolabeRegistry.registerName( key, pmDE ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_E_RADEG ) ;
		AstrolabeRegistry.registerName( key, e_RAdeg ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_E_DEDEG ) ;
		AstrolabeRegistry.registerName( key, e_DEdeg ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_E_PLX ) ;
		AstrolabeRegistry.registerName( key, e_Plx ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_E_PMRA ) ;
		AstrolabeRegistry.registerName( key, e_pmRA ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_E_PMDE ) ;
		AstrolabeRegistry.registerName( key, e_pmDE ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_DERA ) ;
		AstrolabeRegistry.registerName( key, DERA ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_PLXRA ) ;
		AstrolabeRegistry.registerName( key, PlxRA ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_PLXDE ) ;
		AstrolabeRegistry.registerName( key, PlxDE ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_PMRARA ) ;
		AstrolabeRegistry.registerName( key, pmRARA ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_PMRADE ) ;
		AstrolabeRegistry.registerName( key, pmRADE ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_PMRAPLX ) ;
		AstrolabeRegistry.registerName( key, pmRAPlx ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_PMDERA ) ;
		AstrolabeRegistry.registerName( key, pmDERA ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_PMDEDE ) ;
		AstrolabeRegistry.registerName( key, pmDEDE ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_PMDEPLX ) ;
		AstrolabeRegistry.registerName( key, pmDEPlx ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_PMDEPMRA ) ;
		AstrolabeRegistry.registerName( key, pmDEpmRA ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_NASTRO ) ;
		AstrolabeRegistry.registerName( key, Nastro ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_F2 ) ;
		AstrolabeRegistry.registerName( key, F2 ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_HIP ) ;
		AstrolabeRegistry.registerName( key, HIP ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_BTMAG ) ;
		AstrolabeRegistry.registerName( key, BTmag ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_E_BTMAG ) ;
		AstrolabeRegistry.registerName( key, e_BTmag ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_VTMAG ) ;
		AstrolabeRegistry.registerName( key, VTmag ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_E_VTMAG ) ;
		AstrolabeRegistry.registerName( key, e_VTmag ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_R_BTMAG ) ;
		AstrolabeRegistry.registerName( key, r_BTmag ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_BV ) ;
		AstrolabeRegistry.registerName( key, BV ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_E_BV ) ;
		AstrolabeRegistry.registerName( key, e_BV ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_Q ) ;
		AstrolabeRegistry.registerName( key, Q ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_FS ) ;
		AstrolabeRegistry.registerName( key, Fs ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_SOURCE ) ;
		AstrolabeRegistry.registerName( key, Source ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_NPHOTO ) ;
		AstrolabeRegistry.registerName( key, Nphoto ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_VTSCAT ) ;
		AstrolabeRegistry.registerName( key, VTscat ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_VTMAX ) ;
		AstrolabeRegistry.registerName( key, VTmax ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_VTMIN ) ;
		AstrolabeRegistry.registerName( key, VTmin ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_VAR ) ;
		AstrolabeRegistry.registerName( key, Var ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_VARFLAG ) ;
		AstrolabeRegistry.registerName( key, VarFlag ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_MULTFLAG ) ;
		AstrolabeRegistry.registerName( key, MultFlag ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_MOREPHOTO ) ;
		AstrolabeRegistry.registerName( key, morePhoto ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_M_HIP ) ;
		AstrolabeRegistry.registerName( key, m_HIP ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_PPM ) ;
		AstrolabeRegistry.registerName( key, PPM ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_HD ) ;
		AstrolabeRegistry.registerName( key, HD ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_BD ) ;
		AstrolabeRegistry.registerName( key, BD ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_COD ) ;
		AstrolabeRegistry.registerName( key, CoD ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_CPD ) ;
		AstrolabeRegistry.registerName( key, CPD ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_REMARK ) ;
		AstrolabeRegistry.registerName( key, Remark ) ;
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
