
package astrolabe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;

@SuppressWarnings("serial")
public class CatalogADC6049Record extends astrolabe.model.CatalogADC6049Record implements CatalogRecord {

	// configuration key (CK_)
	private static final String CK_DISTANCE			= "distance" ;

	private static final double DEFAULT_DISTANCE	= 0 ;

	// qualifier key (QK_)
	private final static String QK_CON				= "con" ;

	// redirection key (RK_)
	private final static String RK_CONSTELLATION	= "constellation" ;
	private final static String RK_ABBREVIATION		= "abbreviation" ;
	private final static String RK_NOMINATIVE		= "nominative" ;
	private final static String RK_GENITIVE			= "genitive" ;

	private final static String DEFAULT_TOKENPATTERN = ".+" ;

	private final static int CR_LENGTH18 = 25 ;
	private final static int CR_LENGTH20 = 29 ;

	private int _le = 0 ;

	public List<String>	RAh		; // Right ascension in hours
	public List<String>	DEd		; // Declination in degrees
	public String		con		; // Constellation abbreviation
	public String		type	; // [OI] Type of point (Original or Interpolated)

	// message key (MK_)
	private final static String MK_ERECLEN = "ereclen" ;
	private final static String MK_ERECFMT = "erecfmt" ;
	private final static String MK_ERECVAL = "erecval" ;

	public CatalogADC6049Record( String data ) throws ParameterNotValidException {
		BufferedReader b ;
		String l, lv[] = null ;
		String ra, pra ;
		String de, pde ;
		MessageCatalog cat ;
		StringBuffer msg ;
		String fmt ;

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
					cat = new MessageCatalog( this ) ;
					fmt = cat.message( MK_ERECLEN, null ) ;
					if ( fmt != null ) {
						msg = new StringBuffer() ;
						msg.append( MessageFormat.format( fmt, new Object[] { _le } ) ) ;
					} else
						msg = null ;

					throw new ParameterNotValidException( ParameterNotValidError.errmsg( l, msg.toString() ) ) ;
				}

				lv = l.trim().split( "[ ]+" ) ;
				if ( lv.length != 4 ) {
					msg = new StringBuffer() ;
					msg.append( MessageCatalog.message( this, MK_ERECFMT, null ) ) ;

					throw new ParameterNotValidException( ParameterNotValidError.errmsg( data.length(), msg.toString() ) ) ;
				}
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
		java.util.Vector<String> list ;

		if ( value instanceof List )
			return (List<String>) value ;

		list = new java.util.Vector<String>() ;
		list.add( (String) value ) ;

		return list ;
	}

	public void register() {
		SubstituteCatalog scat ;
		MessageCatalog mcat ;
		String sub, val ;

		scat = new SubstituteCatalog( this ) ;
		mcat = new MessageCatalog( this ) ;

		sub = scat.substitute( QK_CON, QK_CON ) ;
		Registry.register( sub, con ) ;

		sub = scat.substitute( RK_CONSTELLATION, RK_CONSTELLATION ) ;
		val = mcat.message( con+'.'+RK_CONSTELLATION, null ) ;
		Registry.register( sub, val ) ;

		sub = scat.substitute( RK_ABBREVIATION, RK_ABBREVIATION ) ;
		val = mcat.message( con+'.'+RK_ABBREVIATION, null ) ;
		Registry.register( sub, val ) ;

		sub = scat.substitute( RK_NOMINATIVE, RK_NOMINATIVE ) ;
		val = mcat.message( con+'.'+RK_NOMINATIVE, null ) ;
		Registry.register( sub, val ) ;

		sub = scat.substitute( RK_GENITIVE, RK_GENITIVE ) ;
		val = mcat.message( con+'.'+RK_GENITIVE, null ) ;
		Registry.register( sub, val ) ;
	}

	public void degister() {
		SubstituteCatalog scat ;
		String sub ;

		scat = new SubstituteCatalog( this ) ;

		sub = scat.substitute( QK_CON, QK_CON ) ;
		Registry.degister( sub ) ;

		sub = scat.substitute( RK_CONSTELLATION, RK_CONSTELLATION ) ;
		Registry.degister( sub ) ;

		sub = scat.substitute( RK_ABBREVIATION, RK_ABBREVIATION ) ;
		Registry.degister( sub ) ;

		sub = scat.substitute( RK_NOMINATIVE, RK_NOMINATIVE ) ;
		Registry.degister( sub ) ;

		sub = scat.substitute( RK_GENITIVE, RK_GENITIVE ) ;
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
		Object value ;
		String name, pattern ;
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
					value = token.get( this ) ;
					pattern = node.get( key, DEFAULT_TOKENPATTERN ) ;
					for ( String v : unsafecast( value ) )
						if ( ! v.matches( pattern ) ) {
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
		return Double.POSITIVE_INFINITY ;		
	}

	public double de() {
		return Double.POSITIVE_INFINITY ;		
	}

	public Geometry list() {
		Coordinate[] lst ;
		LineString rec ;
		String RA, de ;
		double dist ;

		lst = new Coordinate[ RAh.size() ] ;

		for ( int i=0 ; i<RAh.size() ; i++ ) {
			RA = RAh.get( i ) ;
			de = DEd.get( i ) ;

			lst[i] = new Coordinate(
					Double.valueOf( RA ),
					Double.valueOf( de ) ) ;
		}

		rec = new GeometryFactory().createLineString( lst ) ;

		dist = Configuration.getValue( this, CK_DISTANCE, DEFAULT_DISTANCE ) ;
		if ( dist>0 && lst.length>2 )
			return DouglasPeuckerSimplifier.simplify( rec, dist ) ;
		else
			return rec ;
	}
}
