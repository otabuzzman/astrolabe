
package astrolabe;

import java.util.List;

import org.exolab.castor.xml.ValidationException;

import caa.CAA2DCoordinate;
import caa.CAACoordinateTransformation;
import caa.CAAPrecession;

public class CatalogADC1239HRecord implements CatalogRecord {

	private final static String DEFAULT_STAR = "\uf811" ;

	private final static int CR_TOKEN = 78 ;

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

	public CatalogADC1239HRecord( String data ) throws ParameterNotValidException, NumberFormatException {
		String[] token ;

		token = data.split( "\\|" ) ;
		if ( token.length != CR_TOKEN ) {
			throw new ParameterNotValidException( Integer.toString( token.length ) ) ;
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

		// validation
		RAhms() ;
		DEdms() ;
		Vmag() ; 
		pmRA() ;
		pmDE() ; // continue new methods
	}

	public astrolabe.model.Body toModel() throws ValidationException {
		astrolabe.model.Body model ;
		astrolabe.model.Text tm ;
		astrolabe.model.Position pm ;
		CAA2DCoordinate cpm, ceq ;
		double epoch ;

		epoch = ( (Double) AstrolabeRegistry.retrieve( ApplicationConstant.GC_EPOCHE ) ).doubleValue() ;

		tm = new astrolabe.model.Text() ;
		tm.setPurpose( "mag"+( (int) ( Vmag()+100.5 )-100 ) ) ;
		tm.setValue( DEFAULT_STAR ) ;

		model = new astrolabe.model.Body() ;
		model.setBodyStellar( new astrolabe.model.BodyStellar() ) ;
		model.getBodyStellar().setName( HIP ) ;
		model.getBodyStellar().setTurn( 0 ) ;
		model.getBodyStellar().setSpin( 0 ) ;
		model.getBodyStellar().setText( tm ) ;

		cpm = CAAPrecession.AdjustPositionUsingUniformProperMotion(
				epoch-2451545., RAhms(), DEdms(), pmRA()/1000., pmDE()/1000. ) ;
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

		model.getBodyStellar().setPosition( pm ) ;
		cpm.delete() ;
		ceq.delete() ;

		model.validate() ;

		return model ;
	}

	public List<double[]> list( Projector projector ) {
		List<double[]> r = new java.util.Vector<double[]>() ;
		double[] xy ;

		xy = projector.project( CAACoordinateTransformation.HoursToDegrees( RAhms() ), DEdms() ) ;
		r.add( xy ) ;

		return r ;
	}

	public void register() {
		MessageCatalog m ;
		String key ;

		m = new MessageCatalog( ApplicationConstant.GC_APPLICATION ) ;

		key = m.message( ApplicationConstant.LK_ADC1239H_CATALOG ) ;
		Registry.registerName( key, Catalog ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_HIP ) ;
		Registry.registerName( key, HIP ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_PROXY ) ;
		Registry.registerName( key, Proxy ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_RAHMS ) ;
		Registry.registerName( key, RAhms ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_DEDMS ) ;
		Registry.registerName( key, DEdms ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_VMAG ) ;
		Registry.registerName( key, Vmag ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_VARFLAG ) ;
		Registry.registerName( key, VarFlag ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_R_VMAG ) ;
		Registry.registerName( key, r_Vmag ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_RADEG ) ;
		Registry.registerName( key, RAdeg ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_DEDEG ) ;
		Registry.registerName( key, DEdeg ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_ASTROREF ) ;
		Registry.registerName( key, AstroRef ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_PLX ) ;
		Registry.registerName( key, Plx ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_PMRA ) ;
		Registry.registerName( key, pmRA ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_PMDE ) ;
		Registry.registerName( key, pmDE ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_E_RADEG ) ;
		Registry.registerName( key, e_RAdeg ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_E_DEDEG ) ;
		Registry.registerName( key, e_DEdeg ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_E_PLX ) ;
		Registry.registerName( key, e_Plx ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_E_PMRA ) ;
		Registry.registerName( key, e_pmRA ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_E_PMDE ) ;
		Registry.registerName( key, e_pmDE ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_DERA ) ;
		Registry.registerName( key, DERA ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_PLXRA ) ;
		Registry.registerName( key, PlxRA ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_PLXDE ) ;
		Registry.registerName( key, PlxDE ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_PMRARA ) ;
		Registry.registerName( key, pmRARA ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_PMRADE ) ;
		Registry.registerName( key, pmRADE ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_PMRAPLX ) ;
		Registry.registerName( key, pmRAPlx ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_PMDERA ) ;
		Registry.registerName( key, pmDERA ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_PMDEDE ) ;
		Registry.registerName( key, pmDEDE ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_PMDEPLX ) ;
		Registry.registerName( key, pmDEPlx ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_PMDEPMRA ) ;
		Registry.registerName( key, pmDEpmRA ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_F1 ) ;
		Registry.registerName( key, F1 ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_F2 ) ;
		Registry.registerName( key, F2 ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_BTMAG ) ;
		Registry.registerName( key, BTmag ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_E_BTMAG ) ;
		Registry.registerName( key, e_BTmag ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_VTMAG ) ;
		Registry.registerName( key, VTmag ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_E_VTMAG ) ;
		Registry.registerName( key, e_VTmag ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_M_BTMAG ) ;
		Registry.registerName( key, m_BTmag ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_BV ) ;
		Registry.registerName( key, BV ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_E_BV ) ;
		Registry.registerName( key, e_BV ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_R_BV ) ;
		Registry.registerName( key, r_BV ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_VI ) ;
		Registry.registerName( key, VI ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_E_VI ) ;
		Registry.registerName( key, e_VI ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_R_VI ) ;
		Registry.registerName( key, r_VI ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_COMBMAG ) ;
		Registry.registerName( key, CombMag ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_HPMAG ) ;
		Registry.registerName( key, Hpmag ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_E_HPMAG ) ;
		Registry.registerName( key, e_Hpmag ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_HPSCAT ) ;
		Registry.registerName( key, Hpscat ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_O_HPMAG ) ;
		Registry.registerName( key, o_Hpmag ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_M_HPMAG ) ;
		Registry.registerName( key, m_Hpmag ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_HPMAX ) ;
		Registry.registerName( key, Hpmax ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_HPMIN ) ;
		Registry.registerName( key, HPmin ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_PERIOD ) ;
		Registry.registerName( key, Period ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_HVARTYPE ) ;
		Registry.registerName( key, HvarType ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_MOREVAR ) ;
		Registry.registerName( key, moreVar ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_MOREPHOTO ) ;
		Registry.registerName( key, morePhoto ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_CCDM ) ;
		Registry.registerName( key, CCDM ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_N_CCDM ) ;
		Registry.registerName( key, n_CCDM ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_NSYS ) ;
		Registry.registerName( key, Nsys ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_NCOMP ) ;
		Registry.registerName( key, Ncomp ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_MULTFLAG ) ;
		Registry.registerName( key, MultFlag ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_SOURCE ) ;
		Registry.registerName( key, Source ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_QUAL ) ;
		Registry.registerName( key, Qual ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_M_HIP ) ;
		Registry.registerName( key, m_HIP ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_THETA ) ;
		Registry.registerName( key, theta ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_RHO ) ;
		Registry.registerName( key, rho ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_E_RHO ) ;
		Registry.registerName( key, e_rho ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_DHP ) ;
		Registry.registerName( key, dHp ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_E_DHP ) ;
		Registry.registerName( key, e_dHp ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_SURVEY ) ;
		Registry.registerName( key, Survey ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_CHART ) ;
		Registry.registerName( key, Chart ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_NOTES ) ;
		Registry.registerName( key, Notes ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_HD ) ;
		Registry.registerName( key, HD ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_BD ) ;
		Registry.registerName( key, BD ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_COD ) ;
		Registry.registerName( key, CoD ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_CPD ) ;
		Registry.registerName( key, CPD ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_VIRED ) ;
		Registry.registerName( key, VIred ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_SPTYPE ) ;
		Registry.registerName( key, SpType ) ;
		key = m.message( ApplicationConstant.LK_ADC1239H_R_SPTYPE ) ;
		Registry.registerName( key, r_SpType ) ;
	}

	public List<String> ident() {
		List<String> r = new java.util.Vector<String>( 2 ) ;

		r.add( HIP ) ;
		r.add( "mag"+( (int) ( Vmag()+100.5 )-100 ) ) ;

		return r ;
	}

	public double RAhms() {
		String rams[] ;
		double ra ;

		rams = RAhms.split( " " ) ;
		ra = new Double( rams[0] ).doubleValue()
		+new Double( rams[1] ).doubleValue()/60.
		+new Double( rams[2] ).doubleValue()/3600. ;

		return ra ;
	}

	public double DEdms() {
		String dems[] ;
		double de ;

		dems = DEdms.split( " " ) ;
		de = new Double( dems[0] ).doubleValue()
		+new Double( dems[1] ).doubleValue()/60.
		+new Double( dems[2] ).doubleValue()/3600. ;

		return de ;
	}

	public double Vmag() {
		return new Double( Vmag ).doubleValue() ;
	}

	public double pmRA() {
		return new Double( pmRA ).doubleValue() ;
	}

	public double pmDE() {
		return new Double( pmDE ).doubleValue() ;
	}
}
