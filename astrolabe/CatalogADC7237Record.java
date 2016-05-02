
package astrolabe;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

@SuppressWarnings("serial")
public class CatalogADC7237Record extends astrolabe.model.CatalogADC7237Record implements CatalogRecord {

	private final static String DEFAULT_TOKENPATTERN = ".+" ;

	private final static int CR_LENGTH = 521 ;

	private final static String QK_PGC		= "PGC" ;
	private final static String QK_RAH		= "RAh" ;
	private final static String QK_RAM		= "RAm" ;
	private final static String QK_RAS		= "RAs" ;
	private final static String QK_DE		= "DE" ;
	private final static String QK_DED		= "DEd" ;
	private final static String QK_DEM		= "DEm" ;
	private final static String QK_DES		= "DEs" ;
	private final static String QK_OTYPE	= "OType" ;
	private final static String QK_MTYPE	= "MType" ;
	private final static String QK_LOGD25	= "logD25" ;
	private final static String QK_E_LOGD25	= "e_logD25" ;
	private final static String QK_LOGR25	= "logR25" ;
	private final static String QK_E_LOGR25	= "e_logR25" ;
	private final static String QK_PA		= "PA" ;
	private final static String QK_E_PA		= "e_PA" ;
	private final static String QK_O_ANAMES	= "o_ANames" ;
	private final static String QK_ANAMES	= "ANames" ;

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

	// message key (MK_)
	private final static String MK_ERECLEN = "ereclen" ;
	private final static String MK_ERECVAL = "erecval" ;

	public CatalogADC7237Record( String data ) throws ParameterNotValidException {
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

	public void register() {
		SubstituteCatalog cat ;
		String sub ;

		cat = new SubstituteCatalog( this ) ;

		sub = cat.substitute( QK_PGC, null ) ;
		Registry.register( sub, PGC ) ;
		sub = cat.substitute( QK_RAH, null ) ;
		Registry.register( sub, RAh ) ;
		sub = cat.substitute( QK_RAM, null ) ;
		Registry.register( sub, RAm ) ;
		sub = cat.substitute( QK_RAS, null ) ;
		Registry.register( sub, RAs ) ;
		sub = cat.substitute( QK_DE, null ) ;
		Registry.register( sub, DE ) ;
		sub = cat.substitute( QK_DED, null ) ;
		Registry.register( sub, DEd ) ;
		sub = cat.substitute( QK_DEM, null ) ;
		Registry.register( sub, DEm ) ;
		sub = cat.substitute( QK_DES, null ) ;
		Registry.register( sub, DEs ) ;
		sub = cat.substitute( QK_OTYPE, null ) ;
		Registry.register( sub, OType ) ;
		sub = cat.substitute( QK_MTYPE, null ) ;
		Registry.register( sub, MType ) ;
		sub = cat.substitute( QK_LOGD25, null ) ;
		Registry.register( sub, logD25 ) ;
		sub = cat.substitute( QK_E_LOGD25, null ) ;
		Registry.register( sub, e_logD25 ) ;
		sub = cat.substitute( QK_LOGR25, null ) ;
		Registry.register( sub, logR25 ) ;
		sub = cat.substitute( QK_E_LOGR25, null ) ;
		Registry.register( sub, e_logR25 ) ;
		sub = cat.substitute( QK_PA, null ) ;
		Registry.register( sub, PA ) ;
		sub = cat.substitute( QK_E_PA, null ) ;
		Registry.register( sub, e_PA ) ;
		sub = cat.substitute( QK_O_ANAMES, null ) ;
		Registry.register( sub, o_ANames ) ;
		sub = cat.substitute( QK_ANAMES, null ) ;
		Registry.register( sub, ANames ) ;
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
		return RAh()+RAm()/60.+RAs()/3600. ;
	}

	public double de() {
		return DEd()+DEm()/60.+DEs()/3600. ;
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

	public List<double[]> list() {
		List<double[]> r = new java.util.Vector<double[]>() ;

		r.add( new double[] { RA(), de() } ) ;

		return r ;
	}
}
