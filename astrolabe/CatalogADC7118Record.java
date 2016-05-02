
package astrolabe;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

@SuppressWarnings("serial")
public class CatalogADC7118Record extends astrolabe.model.CatalogADC7118Record implements CatalogRecord {

	private final static String DEFAULT_TOKENPATTERN = ".+" ;

	private final static int CR_LENGTH = 96 ;

	private final static String QK_NAME		= "Name" ;
	private final static String QK_TYPE		= "Type" ;
	private final static String QK_RAH		= "RAh" ;
	private final static String QK_RAM		= "RAm" ;
	private final static String QK_DE		= "DE" ;
	private final static String QK_DED		= "DEd" ;
	private final static String QK_DEM		= "DEm" ;
	private final static String QK_SOURCE	= "Source" ;
	private final static String QK_CONST	= "Const" ;
	private final static String QK_L_SIZE	= "l_size" ;
	private final static String QK_SIZE		= "size" ;
	private final static String QK_MAG		= "mag" ;
	private final static String QK_N_MAG	= "n_mag" ;
	private final static String QK_DESC		= "Desc" ;

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

	// message key (MK_)
	private final static String MK_ERECLEN = "ereclen" ;
	private final static String MK_ERECVAL = "erecval" ;

	public CatalogADC7118Record( String data ) throws ParameterNotValidException {
		MessageCatalog cat ;
		StringBuffer msg ;
		String fmt ;

		if ( data.length() != CR_LENGTH ) {
			cat = new MessageCatalog( ApplicationConstant.GC_APPLICATION, this ) ;
			fmt = cat.message( MK_ERECLEN, null ) ;
			if ( fmt != null ) {
				msg = new StringBuffer() ;
				msg.append( MessageFormat.format( fmt, new Object[] { CR_LENGTH } ) ) ;
			} else
				msg = null ;

			throw new ParameterNotValidException( ParameterNotValidError.errmsg( data.length(), msg.toString() ) ) ;
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

	public void register() {
		SubstituteCatalog cat ;
		String sub ;

		cat = new SubstituteCatalog( ApplicationConstant.GC_APPLICATION, this ) ;

		sub = cat.substitute( QK_NAME, null ) ;
		Registry.register( sub, Name ) ;
		sub = cat.substitute( QK_TYPE, null ) ;
		Registry.register( sub, Type  ) ;
		sub = cat.substitute( QK_RAH, null ) ;
		Registry.register( sub, RAh ) ;
		sub = cat.substitute( QK_RAM, null ) ;
		Registry.register( sub, RAm ) ;
		sub = cat.substitute( QK_DE, null ) ;
		Registry.register( sub, DE ) ;
		sub = cat.substitute( QK_DED, null ) ;
		Registry.register( sub, DEd  ) ;
		sub = cat.substitute( QK_DEM, null ) ;
		Registry.register( sub, DEm ) ;
		sub = cat.substitute( QK_SOURCE, null ) ;
		Registry.register( sub, Source ) ;
		sub = cat.substitute( QK_CONST, null ) ;
		Registry.register( sub, Const ) ;
		sub = cat.substitute( QK_L_SIZE, null ) ;
		Registry.register( sub, l_size ) ;
		sub = cat.substitute( QK_SIZE, null ) ;
		Registry.register( sub, size ) ;
		sub = cat.substitute( QK_MAG, null ) ;
		Registry.register( sub, mag ) ;
		sub = cat.substitute( QK_N_MAG, null ) ;
		Registry.register( sub, n_mag ) ;
		sub = cat.substitute( QK_DESC, null ) ;
		Registry.register( sub, Desc ) ;
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
						cat = new MessageCatalog( ApplicationConstant.GC_APPLICATION, this ) ;
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
		return RAh()+RAm()/60. ;
	}

	public double de() {
		return DEd()+DEm()/60. ;
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

	public List<double[]> list() {
		List<double[]> r = new java.util.Vector<double[]>() ;

		r.add( new double[] { RA(), de() } ) ;

		return r ;
	}
}
