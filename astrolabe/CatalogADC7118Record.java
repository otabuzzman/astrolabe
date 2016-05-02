
package astrolabe;

import java.lang.reflect.Field;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

@SuppressWarnings("serial")
public class CatalogADC7118Record extends astrolabe.model.CatalogADC7118Record implements CatalogRecord {

	private final static String DEFAULT_TOKENPATTERN = ".+" ;

	private final static int CR_LENGTH = 96 ;

	public String Name    ; //  NGC or IC designation (preceded by I)
	public String Type    ; // *Object classification
	public String RAh     ; //  Right Ascension 2000 (hours)
	public String RAm     ; //  Right Ascension 2000 (minutes)
	public String DE      ; //  Declination 2000 (sign)
	public String DEd     ; //  Declination 2000 (degrees)
	public String DEm     ; //  Declination 2000 (minutes)
	public String Source  ; // *Source of entry
	public String Const   ; //  Constellation
	public String l_size  ; //  [<] Limit on Size
	public String size    ; //  ? Largest dimension
	public String mag     ; //  ? Integrated magnitude, visual or photographic (see n_mag)
	public String n_mag   ; //  [p] 'p' if mag is photographic (blue)
	public String Desc    ; // *Description of the object

	public CatalogADC7118Record( String data ) throws ParameterNotValidException {
		if ( data.length() != CR_LENGTH ) {
			throw new ParameterNotValidException(  Integer.toString( data.length() ) ) ;
		}

		Name    = data.substring(0, 5 ).trim() ;
		Type    = data.substring(6, 9 ).trim() ;
		RAh     = data.substring(10, 12 ).trim() ;
		RAm     = data.substring(13, 17 ).trim() ;
		DE      = data.substring(19, 20 ).trim() ;
		DEd     = data.substring(20, 22 ).trim() ;
		DEm     = data.substring(23, 25 ).trim() ;
		Source  = data.substring(26, 27 ).trim() ;
		Const   = data.substring(29, 32 ).trim() ;
		l_size  = data.substring(32, 33 ).trim() ;
		size    = data.substring(33, 38 ).trim() ;
		mag     = data.substring(40, 44 ).trim() ;
		n_mag   = data.substring(44, 45 ).trim() ;
		Desc    = data.substring(46, 96 ).trim() ;
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

		key = m.message( ApplicationConstant.LK_ADC7118_NAME ) ;
		AstrolabeRegistry.registerName( key, Name ) ;
		key = m.message( ApplicationConstant.LK_ADC7118_TYPE ) ;
		AstrolabeRegistry.registerName( key, Type  ) ;
		key = m.message( ApplicationConstant.LK_ADC7118_RAH ) ;
		AstrolabeRegistry.registerName( key, RAh ) ;
		key = m.message( ApplicationConstant.LK_ADC7118_RAM ) ;
		AstrolabeRegistry.registerName( key, RAm ) ;
		key = m.message( ApplicationConstant.LK_ADC7118_DE ) ;
		AstrolabeRegistry.registerName( key, DE ) ;
		key = m.message( ApplicationConstant.LK_ADC7118_DED ) ;
		AstrolabeRegistry.registerName( key, DEd  ) ;
		key = m.message( ApplicationConstant.LK_ADC7118_DEM ) ;
		AstrolabeRegistry.registerName( key, DEm ) ;
		key = m.message( ApplicationConstant.LK_ADC7118_SOURCE ) ;
		AstrolabeRegistry.registerName( key, Source ) ;
		key = m.message( ApplicationConstant.LK_ADC7118_CONST ) ;
		AstrolabeRegistry.registerName( key, Const ) ;
		key = m.message( ApplicationConstant.LK_ADC7118_L_SIZE ) ;
		AstrolabeRegistry.registerName( key, l_size ) ;
		key = m.message( ApplicationConstant.LK_ADC7118_SIZE ) ;
		AstrolabeRegistry.registerName( key, size ) ;
		key = m.message( ApplicationConstant.LK_ADC7118_MAG ) ;
		AstrolabeRegistry.registerName( key, mag ) ;
		key = m.message( ApplicationConstant.LK_ADC7118_N_MAG ) ;
		AstrolabeRegistry.registerName( key, n_mag ) ;
		key = m.message( ApplicationConstant.LK_ADC7118_DESC ) ;
		AstrolabeRegistry.registerName( key, Desc ) ;
	}

	public double[] RA() {
		return new double[] { RAh()+RAm()/60. } ;
	}

	public double[] de() {
		return new double[] { DEd()+DEm()/60. } ;
	}

	private double RAh() {
		return new Double( RAh ).doubleValue() ;
	}

	private double RAm() {
		return new Double( RAm ).doubleValue() ;
	}

	private double DEd() {
		return new Double( DE+DEd ).doubleValue() ;
	}

	private double DEm() {
		return new Double( DE+DEm ).doubleValue() ;
	}
}
