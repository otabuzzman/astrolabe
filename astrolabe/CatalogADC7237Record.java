
package astrolabe;

import java.lang.reflect.Field;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

@SuppressWarnings("serial")
public class CatalogADC7237Record extends astrolabe.model.CatalogADC7237Record implements CatalogRecord {

	private final static String DEFAULT_TOKENPATTERN = ".+" ;

	private final static int CR_LENGTH = 521 ;

	public String PGC	; // [1/3099300] PGC number
	public String RAh	; // Right ascension (J2000)
	public String RAm	; // Right ascension (J2000)
	public String RAs	; // Right ascension (J2000)
	public String DE	; // Declination sign (J2000)
	public String DEd	; // Declination (J2000)
	public String DEm	; // Declination (J2000)
	public String DEs	; // Declination (J2000)
	public String OType	; // [GM ] Object type (1)
	public String MType	; // Provisional morphological type from LEDA
	// according to the RC2 code. 
	public String logD25	; // ?=9.99 Apparent diameter (2)
	public String e_logD25	; // ?=9.99 Actual error of logD25
	public String logR25	; // ?=9.99 Axis ratio in log scale
	// (log of major axis to minor axis) 
	public String e_logR25	; // ?=9.99 Actual error on logR25
	public String PA	; // ?=999. Adopted 1950-position angle (3)
	public String e_PA	; // ?=999. rms uncertainty on PA
	public String o_ANames	; // Number of alternate names.
	public String ANames	; // Alternate names (4)

	public CatalogADC7237Record( String data ) throws ParameterNotValidException {

		if ( data.length() != CR_LENGTH ) {
			throw new ParameterNotValidException(  Integer.toString( data.length() ) ) ;
		}

		PGC			= data.substring( 3, 10 ).trim() ;
		RAh			= data.substring( 12, 14 ).trim() ;
		RAm			= data.substring( 14, 16 ).trim() ;
		RAs			= data.substring( 16, 20 ).trim() ;
		DE			= data.substring( 20, 21 ).trim() ;
		DEd			= data.substring( 21, 23 ).trim() ;
		DEm			= data.substring( 23, 25 ).trim() ;
		DEs			= data.substring( 25, 27 ).trim() ;
		OType		= data.substring( 28, 30 ).trim() ;
		MType		= data.substring( 31, 36 ).trim() ;
		logD25		= data.substring( 36, 41 ).trim() ;
		e_logD25	= data.substring( 44, 48 ).trim() ;
		logR25		= data.substring( 50, 54 ).trim() ;
		e_logR25	= data.substring( 57, 61 ).trim() ;
		PA			= data.substring( 63, 67 ).trim() ;
		e_PA		= data.substring( 70, 74 ).trim() ;
		o_ANames	= data.substring( 75, 77 ).trim() ;
		ANames		= data.substring( 78, 341 ).trim() ;
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

		key = m.message( ApplicationConstant.LK_ADC7237_PGC ) ;
		AstrolabeRegistry.registerName( key, PGC ) ;
		key = m.message( ApplicationConstant.LK_ADC7237_RAH ) ;
		AstrolabeRegistry.registerName( key, RAh ) ;
		key = m.message( ApplicationConstant.LK_ADC7237_RAM ) ;
		AstrolabeRegistry.registerName( key, RAm ) ;
		key = m.message( ApplicationConstant.LK_ADC7237_RAS ) ;
		AstrolabeRegistry.registerName( key, RAs ) ;
		key = m.message( ApplicationConstant.LK_ADC7237_DE ) ;
		AstrolabeRegistry.registerName( key, DE ) ;
		key = m.message( ApplicationConstant.LK_ADC7237_DED ) ;
		AstrolabeRegistry.registerName( key, DEd ) ;
		key = m.message( ApplicationConstant.LK_ADC7237_DEM ) ;
		AstrolabeRegistry.registerName( key, DEm ) ;
		key = m.message( ApplicationConstant.LK_ADC7237_DES ) ;
		AstrolabeRegistry.registerName( key, DEs ) ;
		key = m.message( ApplicationConstant.LK_ADC7237_OTYPE ) ;
		AstrolabeRegistry.registerName( key, OType ) ;
		key = m.message( ApplicationConstant.LK_ADC7237_MTYPE ) ;
		AstrolabeRegistry.registerName( key, MType ) ;
		key = m.message( ApplicationConstant.LK_ADC7237_LOGD25 ) ;
		AstrolabeRegistry.registerName( key, logD25 ) ;
		key = m.message( ApplicationConstant.LK_ADC7237_E_LOGD25 ) ;
		AstrolabeRegistry.registerName( key, e_logD25 ) ;
		key = m.message( ApplicationConstant.LK_ADC7237_LOGR25 ) ;
		AstrolabeRegistry.registerName( key, logR25 ) ;
		key = m.message( ApplicationConstant.LK_ADC7237_E_LOGR25 ) ;
		AstrolabeRegistry.registerName( key, e_logR25 ) ;
		key = m.message( ApplicationConstant.LK_ADC7237_PA ) ;
		AstrolabeRegistry.registerName( key, PA ) ;
		key = m.message( ApplicationConstant.LK_ADC7237_E_PA ) ;
		AstrolabeRegistry.registerName( key, e_PA ) ;
		key = m.message( ApplicationConstant.LK_ADC7237_O_ANAMES ) ;
		AstrolabeRegistry.registerName( key, o_ANames ) ;
		key = m.message( ApplicationConstant.LK_ADC7237_ANAMES ) ;
		AstrolabeRegistry.registerName( key, ANames ) ;
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
