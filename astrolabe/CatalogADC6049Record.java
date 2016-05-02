
package astrolabe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.exolab.castor.xml.ValidationException;

import caa.CAA2DCoordinate;
import caa.CAACoordinateTransformation;
import caa.CAAPrecession;

public class CatalogADC6049Record implements CatalogRecord {

	private final static String DEFAULT_TOKENPATTERN = ".+" ;

	private final static int CR_LENGTH18 = 25 ;
	private final static int CR_LENGTH20 = 29 ;

	private int _le = 0 ;

	public List<String>	RAh  ; // Right ascension in decimal hours (J2000)
	public List<String>	DEd ; // Declination in degrees (J2000)
	public String		con   ; // Constellation abbreviation
	public String		type  ; // [OI] Type of point (Original or Interpolated)

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
					throw new ParameterNotValidException(  Integer.toString( l.length() ) ) ;
				}

				lv = l.trim().split( "[ ]+" ) ;
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

	public boolean isValid() {
		try {
			validate() ;
		} catch ( ParameterNotValidException e ) {
			return false ;
		}

		return true ;
	}

	public void validate() throws ParameterNotValidException {
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
					for ( String v : (List<String>) value )
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

	public void register() {
		MessageCatalog m ;
		String k, p, v ;

		m = new MessageCatalog( ApplicationConstant.GC_APPLICATION ) ;

		k = m.message( ApplicationConstant.LK_ADC6049_CONSTELLATION ) ;
		p = MessageFormat.format( ApplicationConstant.LP_ADC6049_CONSTELLATION, new Object[] { con } ) ;
		v = m.message( p ) ;
		Registry.registerName( k, v ) ;

		k = m.message( ApplicationConstant.LK_ADC6049_ABBREVIATION ) ;
		p = MessageFormat.format( ApplicationConstant.LP_ADC6049_ABBREVIATION, new Object[] { con } ) ;
		v = m.message( p ) ;
		Registry.registerName( k, v ) ;

		k = m.message( ApplicationConstant.LK_ADC6049_NOMINATIVE ) ;
		p = MessageFormat.format( ApplicationConstant.LP_ADC6049_NOMINATIVE, new Object[] { con } ) ;
		v = m.message( p ) ;
		Registry.registerName( k, v ) ;

		k = m.message( ApplicationConstant.LK_ADC6049_GENITIVE ) ;
		p = MessageFormat.format( ApplicationConstant.LP_ADC6049_GENITIVE, new Object[] { con } ) ;
		v = m.message( p ) ;
		Registry.registerName( k, v ) ;
	}

	public void toModel( astrolabe.model.Body body ) throws ValidationException {
		astrolabe.model.Position pm ;
		CAA2DCoordinate ceq ;
		double epoch ;

		epoch = ( (Double) AstrolabeRegistry.retrieve( ApplicationConstant.GC_EPOCH ) ).doubleValue() ;

		body.getBodyAreal().setName( con ) ;

		for ( int p=0 ; p<RAh.size() ; p++ ) {
			ceq = CAAPrecession.PrecessEquatorial( RAh( p ), DEd( p ), 2451545./*J2000*/, epoch ) ;
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

			body.getBodyAreal().addPosition( pm ) ;
			ceq.delete() ;
		}

		body.validate() ;
	}

	public double[] RA() {
		double[] ra = new double[RAh.size()] ;

		for ( int i=0 ; i<RAh.size() ; i++ )
			ra[i] = RAh( i ) ;

		return ra ;
	}

	public double[] de() {
		double[] r = new double[DEd.size()] ;

		for ( int i=0 ; i<DEd.size() ; i++ )
			r[i] = DEd( i ) ;

		return r ;
	}

	private double RAh( int index ) {
		return new Double( RAh.get( index ) ).doubleValue() ;		
	}

	private double DEd( int index ) {
		return new Double( DEd.get( index ) ).doubleValue() ;
	}
}
