
package astrolabe;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

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
			cat = new MessageCatalog( this ) ;
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

		cat = new SubstituteCatalog( this ) ;

		sub = cat.substitute( QK_NAME, QK_NAME ) ;
		Registry.register( sub, Name ) ;
		sub = cat.substitute( QK_TYPE, QK_TYPE ) ;
		Registry.register( sub, Type  ) ;
		sub = cat.substitute( QK_RAH, QK_RAH ) ;
		Registry.register( sub, RAh ) ;
		sub = cat.substitute( QK_RAM, QK_RAM ) ;
		Registry.register( sub, RAm ) ;
		sub = cat.substitute( QK_DE, QK_DE ) ;
		Registry.register( sub, DE ) ;
		sub = cat.substitute( QK_DED, QK_DED ) ;
		Registry.register( sub, DEd  ) ;
		sub = cat.substitute( QK_DEM, QK_DEM ) ;
		Registry.register( sub, DEm ) ;
		sub = cat.substitute( QK_SOURCE, QK_SOURCE ) ;
		Registry.register( sub, Source ) ;
		sub = cat.substitute( QK_CONST, QK_CONST ) ;
		Registry.register( sub, Const ) ;
		sub = cat.substitute( QK_L_SIZE, QK_L_SIZE ) ;
		Registry.register( sub, l_size ) ;
		sub = cat.substitute( QK_SIZE, QK_SIZE ) ;
		Registry.register( sub, size ) ;
		sub = cat.substitute( QK_MAG, QK_MAG ) ;
		Registry.register( sub, mag ) ;
		sub = cat.substitute( QK_N_MAG, QK_N_MAG ) ;
		Registry.register( sub, n_mag ) ;
		sub = cat.substitute( QK_DESC, QK_DESC ) ;
		Registry.register( sub, Desc ) ;
	}

	public void degister() {
		SubstituteCatalog cat ;
		String sub ;

		cat = new SubstituteCatalog( this ) ;

		sub = cat.substitute( QK_NAME, QK_NAME ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_TYPE, QK_TYPE ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_RAH, QK_RAH ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_RAM, QK_RAM ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_DE, QK_DE ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_DED, QK_DED ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_DEM, QK_DEM ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_SOURCE, QK_SOURCE ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_CONST, QK_CONST ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_L_SIZE, QK_L_SIZE ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_SIZE, QK_SIZE ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_MAG, QK_MAG ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_N_MAG, QK_N_MAG ) ;
		Registry.degister( sub ) ;
		sub = cat.substitute( QK_DESC, QK_DESC ) ;
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
			if ( ! Preferences.userRoot().nodeExists( name ) )
				return ;
			node = Preferences.userRoot().node( name ) ;

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

	public Geometry list() {
		return new GeometryFactory().createPoint( new Coordinate( RA(), de() ) ) ;
	}
}
