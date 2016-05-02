
package astrolabe;

import java.lang.reflect.Field;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.exolab.castor.xml.ValidationException;

import caa.CAA2DCoordinate;
import caa.CAACoordinateTransformation;
import caa.CAAPrecession;

public class CatalogADC1239TRecord implements CatalogRecord {

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

	public boolean isValid() {
		try {
			validate() ;
		} catch ( ParameterNotValidException e ) {
			return false ;
		}

		return true ;
	}

	public void validate() throws ParameterNotValidException {
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

		key = m.message( ApplicationConstant.LK_ADC1239T_CATALOG ) ;
		Registry.registerName( key, Catalog ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_TYC ) ;
		Registry.registerName( key, TYC ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_PROXY ) ;
		Registry.registerName( key, Proxy ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_RAHMS ) ;
		Registry.registerName( key, RAhms ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_DEDMS ) ;
		Registry.registerName( key, DEdms ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_VMAG ) ;
		Registry.registerName( key, Vmag ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_R_VMAG ) ;
		Registry.registerName( key, r_Vmag ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_RADEG ) ;
		Registry.registerName( key, RAdeg ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_DEDEG ) ;
		Registry.registerName( key, DEdeg ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_ASTROREF ) ;
		Registry.registerName( key, AstroRef ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_PLX ) ;
		Registry.registerName( key, Plx ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_PMRA ) ;
		Registry.registerName( key, pmRA ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_PMDE ) ;
		Registry.registerName( key, pmDE ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_E_RADEG ) ;
		Registry.registerName( key, e_RAdeg ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_E_DEDEG ) ;
		Registry.registerName( key, e_DEdeg ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_E_PLX ) ;
		Registry.registerName( key, e_Plx ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_E_PMRA ) ;
		Registry.registerName( key, e_pmRA ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_E_PMDE ) ;
		Registry.registerName( key, e_pmDE ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_DERA ) ;
		Registry.registerName( key, DERA ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_PLXRA ) ;
		Registry.registerName( key, PlxRA ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_PLXDE ) ;
		Registry.registerName( key, PlxDE ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_PMRARA ) ;
		Registry.registerName( key, pmRARA ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_PMRADE ) ;
		Registry.registerName( key, pmRADE ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_PMRAPLX ) ;
		Registry.registerName( key, pmRAPlx ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_PMDERA ) ;
		Registry.registerName( key, pmDERA ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_PMDEDE ) ;
		Registry.registerName( key, pmDEDE ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_PMDEPLX ) ;
		Registry.registerName( key, pmDEPlx ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_PMDEPMRA ) ;
		Registry.registerName( key, pmDEpmRA ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_NASTRO ) ;
		Registry.registerName( key, Nastro ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_F2 ) ;
		Registry.registerName( key, F2 ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_HIP ) ;
		Registry.registerName( key, HIP ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_BTMAG ) ;
		Registry.registerName( key, BTmag ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_E_BTMAG ) ;
		Registry.registerName( key, e_BTmag ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_VTMAG ) ;
		Registry.registerName( key, VTmag ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_E_VTMAG ) ;
		Registry.registerName( key, e_VTmag ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_R_BTMAG ) ;
		Registry.registerName( key, r_BTmag ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_BV ) ;
		Registry.registerName( key, BV ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_E_BV ) ;
		Registry.registerName( key, e_BV ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_Q ) ;
		Registry.registerName( key, Q ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_FS ) ;
		Registry.registerName( key, Fs ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_SOURCE ) ;
		Registry.registerName( key, Source ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_NPHOTO ) ;
		Registry.registerName( key, Nphoto ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_VTSCAT ) ;
		Registry.registerName( key, VTscat ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_VTMAX ) ;
		Registry.registerName( key, VTmax ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_VTMIN ) ;
		Registry.registerName( key, VTmin ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_VAR ) ;
		Registry.registerName( key, Var ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_VARFLAG ) ;
		Registry.registerName( key, VarFlag ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_MULTFLAG ) ;
		Registry.registerName( key, MultFlag ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_MOREPHOTO ) ;
		Registry.registerName( key, morePhoto ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_M_HIP ) ;
		Registry.registerName( key, m_HIP ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_PPM ) ;
		Registry.registerName( key, PPM ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_HD ) ;
		Registry.registerName( key, HD ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_BD ) ;
		Registry.registerName( key, BD ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_COD ) ;
		Registry.registerName( key, CoD ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_CPD ) ;
		Registry.registerName( key, CPD ) ;
		key = m.message( ApplicationConstant.LK_ADC1239T_REMARK ) ;
		Registry.registerName( key, Remark ) ;
	}

	public void toModel( astrolabe.model.Body body ) throws ValidationException {
		astrolabe.model.Position pm ;
		CAA2DCoordinate cpm, ceq ;
		double epoch, pmRA, pmDE ;

		epoch = ( (Double) AstrolabeRegistry.retrieve( ApplicationConstant.GC_EPOCH ) ).doubleValue() ;

		body.getBodyStellar().setName( TYC.replaceAll( "[ ]+", "-" ) ) ;

		pmRA = 0 ;
		if ( this.pmRA.length()>0 )
			pmRA = new Double( this.pmRA ).doubleValue() ;
		pmDE = 0 ;
		if ( this.pmDE.length()>0 )
			pmDE = new Double( this.pmDE ).doubleValue() ;
		cpm = CAAPrecession.AdjustPositionUsingUniformProperMotion(
				epoch-2451545., RAhms(), DEdms(), pmRA/1000., pmDE/1000. ) ;
		ceq = CAAPrecession.PrecessEquatorial( cpm.X(), cpm.Y(), 2451545./*J2000*/, epoch ) ;
		pm = new astrolabe.model.Position() ;
		// astrolabe.model.SphericalType
		pm.setR( new astrolabe.model.R() ) ;
		pm.getR().setValue( 1 ) ;
		// astrolabe.model.AngleType
		pm.setPhi( new astrolabe.model.Phi() ) ;
		pm.getPhi().setRational( new astrolabe.model.Rational() ) ;
		pm.getPhi().getRational().setValue( CAACoordinateTransformation.HoursToDegrees( ceq.X() ) ) ;  
		// astrolabe.model.AngleType
		pm.setTheta( new astrolabe.model.Theta() ) ;
		pm.getTheta().setRational( new astrolabe.model.Rational() ) ;
		pm.getTheta().getRational().setValue( ceq.Y() ) ;  

		body.getBodyStellar().setPosition( pm ) ;
		cpm.delete() ;
		ceq.delete() ;

		body.validate() ;
	}

	public double[] RA() {
		return new double[] { RAhms() } ;
	}

	public double[] de() {
		return new double[] { DEdms() } ;
	}

	private double RAhms() {
		String rams[] ;
		double ra ;

		rams = RAhms.split( "\\p{Space}+" ) ;
		ra = new Double( rams[0] ).doubleValue()
		+new Double( rams[1] ).doubleValue()/60.
		+new Double( rams[2] ).doubleValue()/3600. ;

		return ra ;
	}

	private double DEdms() {
		String dems[] ;
		double de ;

		dems = DEdms.split( "\\p{Space}+" ) ;
		de = new Double( dems[0] ).doubleValue()
		+new Double( dems[1] ).doubleValue()/60.
		+new Double( dems[2] ).doubleValue()/3600. ;

		return de ;
	}
}
