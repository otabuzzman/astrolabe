
package astrolabe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

@SuppressWarnings("serial")
public class CatalogADC6049Record extends astrolabe.model.CatalogADC6049Record implements CatalogRecord {

	private final static String DEFAULT_TOKENPATTERN = ".+" ;

	private final static int CR_LENGTH18 = 25 ;
	private final static int CR_LENGTH20 = 29 ;

	private int _le = 0 ;

	public List<String>	RAh		; // Right ascension in hours
	public List<String>	DEd		; // Declination in degrees
	public String		con		; // Constellation abbreviation
	public String		type	; // [OI] Type of point (Original or Interpolated)

	public CatalogADC6049Record( String data ) throws ParameterNotValidException {
		BufferedReader b ;
		String l, lv[] = null ;
		String ra, pra ;
		String de, pde ;

		RAh = new java.util.Vector<String>() ;
		DEd = new java.util.Vector<String>() ;

		if ( data.charAt( CR_LENGTH18 ) == '\n' )
			_le = CR_LENGTH18 ;
		else
			_le = CR_LENGTH20 ;

		try {
			b = new BufferedReader( new StringReader( data ) ) ;

			while ( ( l = b.readLine() ) != null ) {
				if ( l.length() != _le ) {
					throw new ParameterNotValidException( l ) ;
				}

				lv = l.trim().split( "[ ]+" ) ;
				if ( lv.length != 4 )
					throw new ParameterNotValidException( l ) ;
				RAh.add( lv[0] ) ;
				DEd.add( lv[1] ) ;
			}
			con  = lv[2] ;
			type = lv[3] ;
		} catch ( IOException e ) {
			throw new RuntimeException( e.toString() ) ;
		}

		pra = RAh.get( RAh.size()-1 ) ;
		pde = DEd.get( DEd.size()-1 ) ;
		for ( int position=0 ; position<RAh.size() ; position++ ) {
			ra = RAh.get( position ) ;
			de = DEd.get( position ) ;
			if ( ra.equals( pra ) && de.equals( pde ) ) {
				RAh.remove( position ) ;
				DEd.remove( position ) ;
			}
			pra = ra ;
			pde = de ;
		}
	}

	@SuppressWarnings("unchecked")
	private List<String> unsafecast( Object value ) {
		return (List<String>) value ;
	}

	public void register() {
		MessageCatalog m ;
		String k, p, v ;

		m = new MessageCatalog( ApplicationConstant.GC_APPLICATION ) ;

		k = m.message( ApplicationConstant.LK_ADC6049_CONSTELLATION ) ;
		p = MessageFormat.format( ApplicationConstant.LP_ADC6049_CONSTELLATION, new Object[] { con } ) ;
		v = m.message( p ) ;
		AstrolabeRegistry.registerName( k, v ) ;

		k = m.message( ApplicationConstant.LK_ADC6049_ABBREVIATION ) ;
		p = MessageFormat.format( ApplicationConstant.LP_ADC6049_ABBREVIATION, new Object[] { con } ) ;
		v = m.message( p ) ;
		AstrolabeRegistry.registerName( k, v ) ;

		k = m.message( ApplicationConstant.LK_ADC6049_NOMINATIVE ) ;
		p = MessageFormat.format( ApplicationConstant.LP_ADC6049_NOMINATIVE, new Object[] { con } ) ;
		v = m.message( p ) ;
		AstrolabeRegistry.registerName( k, v ) ;

		k = m.message( ApplicationConstant.LK_ADC6049_GENITIVE ) ;
		p = MessageFormat.format( ApplicationConstant.LP_ADC6049_GENITIVE, new Object[] { con } ) ;
		v = m.message( p ) ;
		AstrolabeRegistry.registerName( k, v ) ;
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
		Object value ;
		String pattern ;

		node = Configuration.getClassNode( this, null, null ) ;

		try {
			for ( String key : node.keys() ) {
				try {
					token = getClass().getDeclaredField( key ) ;
					value = token.get( this ) ;
					pattern = node.get( key, DEFAULT_TOKENPATTERN ) ;
					for ( String v : unsafecast( value ) )
						if ( ! v.matches( pattern ) )
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
		return new Double( RAh.get( 0 ) ).doubleValue() ;		
	}

	public double de() {
		return new Double( DEd.get( 0 ) ).doubleValue() ;
	}

	public List<double[]> list() {
		List<double[]> r = new java.util.Vector<double[]>() ;
		String RA, de ;

		for ( int i=0 ; i<RAh.size() ; i++ ) {
			RA = RAh.get( i ) ;
			de = DEd.get( i ) ;

			r.add( new double[] {
					Double.valueOf( RA ),
					Double.valueOf( de ) } ) ;
		}

		return r ;
	}
}
