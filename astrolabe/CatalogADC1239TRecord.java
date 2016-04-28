
package astrolabe;

import java.util.HashSet;
import java.util.Set;

import org.exolab.castor.xml.ValidationException;

import caa.CAA2DCoordinate;
import caa.CAACoordinateTransformation;
import caa.CAAPrecession;

public class CatalogADC1239TRecord implements CatalogRecord {

	private final static String DEFAULT_STAR = "\uf811" ;

	public final static int CR_TOKEN = 58 ;

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
		if ( token.length != 58 ) {
			throw new ParameterNotValidException( new Integer( token.length ).toString() ) ;
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

		// validation
		try {
			TYC() ;
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
		astrolabe.model.Position pm ;
		double[] pv ;
		CAA2DCoordinate cpm, ceq ;

		model = new astrolabe.model.Body() ;
		model.setBodyStellar( new astrolabe.model.BodyStellar() ) ;
		model.getBodyStellar().setName( TYC() ) ;
		model.getBodyStellar().setGlyph( DEFAULT_STAR ) ;
		model.getBodyStellar().setType( "mag"+( (int) ( Vmag()+100.5 )-100 ) ) ;
		model.getBodyStellar().setTurn( 0 ) ;
		model.getBodyStellar().setSpin( 0 ) ;

		cpm = CAAPrecession.AdjustPositionUsingUniformProperMotion(
				epoch-2451545., RAhms(), DEdms(), pmRA()/1000., pmDE()/1000. ) ;
		ceq = CAAPrecession.PrecessEquatorial( cpm.X(), cpm.Y(), 2451545./*J2000*/, epoch ) ;
		pm = new astrolabe.model.Position() ;
		pv = new double[] { 1, CAACoordinateTransformation.HoursToDegrees( ceq.X() ), ceq.Y() } ;
		AstrolabeFactory.modelOf( pv, pm ) ;
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
		return TYC() ;
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

		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_CATALOG ) ;
		ApplicationHelper.registerName( key, Catalog ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_TYC ) ;
		ApplicationHelper.registerName( key, TYC ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_PROXY ) ;
		ApplicationHelper.registerName( key, Proxy ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_RAHMS ) ;
		ApplicationHelper.registerName( key, RAhms ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_DEDMS ) ;
		ApplicationHelper.registerName( key, DEdms ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_VMAG ) ;
		ApplicationHelper.registerName( key, Vmag ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_R_VMAG ) ;
		ApplicationHelper.registerName( key, r_Vmag ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_RADEG ) ;
		ApplicationHelper.registerName( key, RAdeg ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_DEDEG ) ;
		ApplicationHelper.registerName( key, DEdeg ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_ASTROREF ) ;
		ApplicationHelper.registerName( key, AstroRef ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_PLX ) ;
		ApplicationHelper.registerName( key, Plx ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_PMRA ) ;
		ApplicationHelper.registerName( key, pmRA ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_PMDE ) ;
		ApplicationHelper.registerName( key, pmDE ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_E_RADEG ) ;
		ApplicationHelper.registerName( key, e_RAdeg ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_E_DEDEG ) ;
		ApplicationHelper.registerName( key, e_DEdeg ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_E_PLX ) ;
		ApplicationHelper.registerName( key, e_Plx ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_E_PMRA ) ;
		ApplicationHelper.registerName( key, e_pmRA ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_E_PMDE ) ;
		ApplicationHelper.registerName( key, e_pmDE ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_DERA ) ;
		ApplicationHelper.registerName( key, DERA ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_PLXRA ) ;
		ApplicationHelper.registerName( key, PlxRA ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_PLXDE ) ;
		ApplicationHelper.registerName( key, PlxDE ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_PMRARA ) ;
		ApplicationHelper.registerName( key, pmRARA ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_PMRADE ) ;
		ApplicationHelper.registerName( key, pmRADE ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_PMRAPLX ) ;
		ApplicationHelper.registerName( key, pmRAPlx ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_PMDERA ) ;
		ApplicationHelper.registerName( key, pmDERA ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_PMDEDE ) ;
		ApplicationHelper.registerName( key, pmDEDE ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_PMDEPLX ) ;
		ApplicationHelper.registerName( key, pmDEPlx ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_PMDEPMRA ) ;
		ApplicationHelper.registerName( key, pmDEpmRA ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_NASTRO ) ;
		ApplicationHelper.registerName( key, Nastro ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_F2 ) ;
		ApplicationHelper.registerName( key, F2 ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_HIP ) ;
		ApplicationHelper.registerName( key, HIP ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_BTMAG ) ;
		ApplicationHelper.registerName( key, BTmag ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_E_BTMAG ) ;
		ApplicationHelper.registerName( key, e_BTmag ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_VTMAG ) ;
		ApplicationHelper.registerName( key, VTmag ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_E_VTMAG ) ;
		ApplicationHelper.registerName( key, e_VTmag ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_R_BTMAG ) ;
		ApplicationHelper.registerName( key, r_BTmag ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_BV ) ;
		ApplicationHelper.registerName( key, BV ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_E_BV ) ;
		ApplicationHelper.registerName( key, e_BV ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_Q ) ;
		ApplicationHelper.registerName( key, Q ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_FS ) ;
		ApplicationHelper.registerName( key, Fs ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_SOURCE ) ;
		ApplicationHelper.registerName( key, Source ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_NPHOTO ) ;
		ApplicationHelper.registerName( key, Nphoto ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_VTSCAT ) ;
		ApplicationHelper.registerName( key, VTscat ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_VTMAX ) ;
		ApplicationHelper.registerName( key, VTmax ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_VTMIN ) ;
		ApplicationHelper.registerName( key, VTmin ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_VAR ) ;
		ApplicationHelper.registerName( key, Var ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_VARFLAG ) ;
		ApplicationHelper.registerName( key, VarFlag ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_MULTFLAG ) ;
		ApplicationHelper.registerName( key, MultFlag ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_MOREPHOTO ) ;
		ApplicationHelper.registerName( key, morePhoto ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_M_HIP ) ;
		ApplicationHelper.registerName( key, m_HIP ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_PPM ) ;
		ApplicationHelper.registerName( key, PPM ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_HD ) ;
		ApplicationHelper.registerName( key, HD ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_BD ) ;
		ApplicationHelper.registerName( key, BD ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_COD ) ;
		ApplicationHelper.registerName( key, CoD ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_CPD ) ;
		ApplicationHelper.registerName( key, CPD ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC1239T_REMARK ) ;
		ApplicationHelper.registerName( key, Remark ) ;
	}

	public String TYC() {
		// <GSC region number 1-9537>-<number in region 1-12119>-<component number 1-4>
		return TYC.trim().replaceAll( "[ ]+", "-" ) ;
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
