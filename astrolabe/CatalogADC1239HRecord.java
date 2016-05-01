
package astrolabe;

import java.util.HashSet;
import java.util.Set;

import org.exolab.castor.xml.ValidationException;

import caa.CAA2DCoordinate;
import caa.CAACoordinateTransformation;
import caa.CAAPrecession;

public class CatalogADC1239HRecord implements CatalogRecord {

	private final static String DEFAULT_STAR = "\uf811" ;

	public final static int CR_TOKEN = 78 ;

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

	public CatalogADC1239HRecord( String data ) throws ParameterNotValidException {
		String[] token ;

		token = data.split( "\\|" ) ;
		if ( token.length != CR_TOKEN ) {
			throw new ParameterNotValidException( new Integer( token.length ).toString() ) ;
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
		try {
			RAhms() ;
			DEdms() ;
			Vmag() ; 
			pmRA() ;
			pmDE() ; // continue new methods
		} catch ( NumberFormatException e ) {
			throw new ParameterNotValidException( e.toString() ) ;
		}
	}

	public astrolabe.model.Body toModel( double epoch ) throws ParameterNotValidException {
		astrolabe.model.Body model ;
		astrolabe.model.Text tm ;
		astrolabe.model.Position pm ;
		CAA2DCoordinate cpm, ceq ;

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

		try {
			model.validate() ;
		} catch ( ValidationException e ) {
			throw new ParameterNotValidException( e.toString() ) ;
		}

		return model ;
	}

	public boolean matchAny( Set<String> list ) {
		return list.size()==0||matchSet( list ).size()>0 ;
	}

	public boolean matchAll( Set<String> list ) {
		return list.size()==matchSet( list ).size() ;
	}

	public Set<String> matchSet( Set<String> list ) {
		HashSet<String> r = new HashSet<String>() ;

		for ( String ident : identSet() ) {
			if ( list.contains( ident ) ) {
				r.add( ident ) ;
			}
		}

		return r ;
	}

	public Set<String> identSet() {
		HashSet<String> r = new HashSet<String>() ;

		r.add( ident() ) ;
		r.add( "mag"+( (int) ( Vmag()+100.5 )-100 ) ) ;

		return r ;
	}

	public String ident() {
		return HIP ;
	}

	public java.util.Vector<double[]> list( Projector projector ) {
		java.util.Vector<double[]> r = new java.util.Vector<double[]>() ;
		double[] xy ;

		xy = projector.project( CAACoordinateTransformation.HoursToRadians( RAhms() ),
				CAACoordinateTransformation.DegreesToRadians( DEdms() ) ) ;
		r.add( xy ) ;

		return r ;
	}

	public void register() {
		String key ;

		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_CATALOG ) ;
		ApplicationHelper.registerName( key, Catalog ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_HIP ) ;
		ApplicationHelper.registerName( key, HIP ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_PROXY ) ;
		ApplicationHelper.registerName( key, Proxy ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_RAHMS ) ;
		ApplicationHelper.registerName( key, RAhms ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_DEDMS ) ;
		ApplicationHelper.registerName( key, DEdms ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_VMAG ) ;
		ApplicationHelper.registerName( key, Vmag ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_VARFLAG ) ;
		ApplicationHelper.registerName( key, VarFlag ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_R_VMAG ) ;
		ApplicationHelper.registerName( key, r_Vmag ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_RADEG ) ;
		ApplicationHelper.registerName( key, RAdeg ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_DEDEG ) ;
		ApplicationHelper.registerName( key, DEdeg ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_ASTROREF ) ;
		ApplicationHelper.registerName( key, AstroRef ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_PLX ) ;
		ApplicationHelper.registerName( key, Plx ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_PMRA ) ;
		ApplicationHelper.registerName( key, pmRA ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_PMDE ) ;
		ApplicationHelper.registerName( key, pmDE ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_E_RADEG ) ;
		ApplicationHelper.registerName( key, e_RAdeg ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_E_DEDEG ) ;
		ApplicationHelper.registerName( key, e_DEdeg ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_E_PLX ) ;
		ApplicationHelper.registerName( key, e_Plx ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_E_PMRA ) ;
		ApplicationHelper.registerName( key, e_pmRA ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_E_PMDE ) ;
		ApplicationHelper.registerName( key, e_pmDE ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_DERA ) ;
		ApplicationHelper.registerName( key, DERA ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_PLXRA ) ;
		ApplicationHelper.registerName( key, PlxRA ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_PLXDE ) ;
		ApplicationHelper.registerName( key, PlxDE ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_PMRARA ) ;
		ApplicationHelper.registerName( key, pmRARA ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_PMRADE ) ;
		ApplicationHelper.registerName( key, pmRADE ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_PMRAPLX ) ;
		ApplicationHelper.registerName( key, pmRAPlx ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_PMDERA ) ;
		ApplicationHelper.registerName( key, pmDERA ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_PMDEDE ) ;
		ApplicationHelper.registerName( key, pmDEDE ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_PMDEPLX ) ;
		ApplicationHelper.registerName( key, pmDEPlx ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_PMDEPMRA ) ;
		ApplicationHelper.registerName( key, pmDEpmRA ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_F1 ) ;
		ApplicationHelper.registerName( key, F1 ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_F2 ) ;
		ApplicationHelper.registerName( key, F2 ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_BTMAG ) ;
		ApplicationHelper.registerName( key, BTmag ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_E_BTMAG ) ;
		ApplicationHelper.registerName( key, e_BTmag ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_VTMAG ) ;
		ApplicationHelper.registerName( key, VTmag ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_E_VTMAG ) ;
		ApplicationHelper.registerName( key, e_VTmag ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_M_BTMAG ) ;
		ApplicationHelper.registerName( key, m_BTmag ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_BV ) ;
		ApplicationHelper.registerName( key, BV ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_E_BV ) ;
		ApplicationHelper.registerName( key, e_BV ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_R_BV ) ;
		ApplicationHelper.registerName( key, r_BV ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_VI ) ;
		ApplicationHelper.registerName( key, VI ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_E_VI ) ;
		ApplicationHelper.registerName( key, e_VI ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_R_VI ) ;
		ApplicationHelper.registerName( key, r_VI ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_COMBMAG ) ;
		ApplicationHelper.registerName( key, CombMag ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_HPMAG ) ;
		ApplicationHelper.registerName( key, Hpmag ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_E_HPMAG ) ;
		ApplicationHelper.registerName( key, e_Hpmag ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_HPSCAT ) ;
		ApplicationHelper.registerName( key, Hpscat ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_O_HPMAG ) ;
		ApplicationHelper.registerName( key, o_Hpmag ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_M_HPMAG ) ;
		ApplicationHelper.registerName( key, m_Hpmag ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_HPMAX ) ;
		ApplicationHelper.registerName( key, Hpmax ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_HPMIN ) ;
		ApplicationHelper.registerName( key, HPmin ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_PERIOD ) ;
		ApplicationHelper.registerName( key, Period ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_HVARTYPE ) ;
		ApplicationHelper.registerName( key, HvarType ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_MOREVAR ) ;
		ApplicationHelper.registerName( key, moreVar ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_MOREPHOTO ) ;
		ApplicationHelper.registerName( key, morePhoto ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_CCDM ) ;
		ApplicationHelper.registerName( key, CCDM ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_N_CCDM ) ;
		ApplicationHelper.registerName( key, n_CCDM ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_NSYS ) ;
		ApplicationHelper.registerName( key, Nsys ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_NCOMP ) ;
		ApplicationHelper.registerName( key, Ncomp ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_MULTFLAG ) ;
		ApplicationHelper.registerName( key, MultFlag ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_SOURCE ) ;
		ApplicationHelper.registerName( key, Source ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_QUAL ) ;
		ApplicationHelper.registerName( key, Qual ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_M_HIP ) ;
		ApplicationHelper.registerName( key, m_HIP ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_THETA ) ;
		ApplicationHelper.registerName( key, theta ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_RHO ) ;
		ApplicationHelper.registerName( key, rho ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_E_RHO ) ;
		ApplicationHelper.registerName( key, e_rho ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_DHP ) ;
		ApplicationHelper.registerName( key, dHp ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_E_DHP ) ;
		ApplicationHelper.registerName( key, e_dHp ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_SURVEY ) ;
		ApplicationHelper.registerName( key, Survey ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_CHART ) ;
		ApplicationHelper.registerName( key, Chart ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_NOTES ) ;
		ApplicationHelper.registerName( key, Notes ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_HD ) ;
		ApplicationHelper.registerName( key, HD ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_BD ) ;
		ApplicationHelper.registerName( key, BD ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_COD ) ;
		ApplicationHelper.registerName( key, CoD ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_CPD ) ;
		ApplicationHelper.registerName( key, CPD ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_VIRED ) ;
		ApplicationHelper.registerName( key, VIred ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_SPTYPE ) ;
		ApplicationHelper.registerName( key, SpType ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239H_R_SPTYPE ) ;
		ApplicationHelper.registerName( key, r_SpType ) ;
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
