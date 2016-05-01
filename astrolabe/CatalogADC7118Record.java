
package astrolabe;

import java.util.List;

import org.exolab.castor.xml.ValidationException;

import caa.CAA2DCoordinate;
import caa.CAACoordinateTransformation;
import caa.CAAPrecession;

public class CatalogADC7118Record {

	private final static String DEFAULT_STAR = "\uf811" ;

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

	public CatalogADC7118Record( String data ) throws ParameterNotValidException, NumberFormatException {
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

		// validation
		RAh() ;
		RAm() ;
		DEd() ;
		DEm() ;
		mag() ; // continue new methods
	}

	public astrolabe.model.Body toModel( double epoch ) throws ValidationException {
		astrolabe.model.Body model ;
		astrolabe.model.Text tm ;
		astrolabe.model.Position pm ;
		CAA2DCoordinate ceq ;

		tm = new astrolabe.model.Text() ;
		tm.setPurpose( "mag"+( (int) ( mag()+100.5 )-100 ) ) ;
		tm.setValue( DEFAULT_STAR ) ;

		model = new astrolabe.model.Body() ;
		model.setBodyStellar( new astrolabe.model.BodyStellar() ) ;
		model.getBodyStellar().setName( Name ) ;
		model.getBodyStellar().setTurn( 0 ) ;
		model.getBodyStellar().setSpin( 0 ) ;
		model.getBodyStellar().setText( tm ) ;

		ceq = CAAPrecession.PrecessEquatorial( RAh()+RAm()/60., DEd()+DEm()/60., 2451545./*J2000*/, epoch ) ;
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
		ceq.delete() ;

		model.validate() ;

		return model ;
	}

	public List<double[]> list( Projector projector ) {
		List<double[]> r = new java.util.Vector<double[]>() ;
		double ra, de, xy[] ;

		ra = RAh()+RAm()/60. ;
		de = DEd()+DEm()/60. ;
		xy = projector.project( CAACoordinateTransformation.HoursToDegrees( ra ), de ) ;
		r.add( xy ) ;

		return r ;
	}

	public void register() {
		MessageCatalog m ;
		String key ;

		m = new MessageCatalog( ApplicationConstant.GC_APPLICATION ) ;

		key = m.message( ApplicationConstant.LK_ADC7118_NAME ) ;
		Registry.registerName( key, Name ) ;
		key = m.message( ApplicationConstant.LK_ADC7118_TYPE ) ;
		Registry.registerName( key, Type  ) ;
		key = m.message( ApplicationConstant.LK_ADC7118_RAH ) ;
		Registry.registerName( key, RAh ) ;
		key = m.message( ApplicationConstant.LK_ADC7118_RAM ) ;
		Registry.registerName( key, RAm ) ;
		key = m.message( ApplicationConstant.LK_ADC7118_DE ) ;
		Registry.registerName( key, DE ) ;
		key = m.message( ApplicationConstant.LK_ADC7118_DED ) ;
		Registry.registerName( key, DEd  ) ;
		key = m.message( ApplicationConstant.LK_ADC7118_DEM ) ;
		Registry.registerName( key, DEm ) ;
		key = m.message( ApplicationConstant.LK_ADC7118_SOURCE ) ;
		Registry.registerName( key, Source ) ;
		key = m.message( ApplicationConstant.LK_ADC7118_CONST ) ;
		Registry.registerName( key, Const ) ;
		key = m.message( ApplicationConstant.LK_ADC7118_L_SIZE ) ;
		Registry.registerName( key, l_size ) ;
		key = m.message( ApplicationConstant.LK_ADC7118_SIZE ) ;
		Registry.registerName( key, size ) ;
		key = m.message( ApplicationConstant.LK_ADC7118_MAG ) ;
		Registry.registerName( key, mag ) ;
		key = m.message( ApplicationConstant.LK_ADC7118_N_MAG ) ;
		Registry.registerName( key, n_mag ) ;
		key = m.message( ApplicationConstant.LK_ADC7118_DESC ) ;
		Registry.registerName( key, Desc ) ;
	}

	public List<String> ident() {
		List<String> r = new java.util.Vector<String>( 2 ) ;

		r.add( Name ) ;
		r.add( "mag"+( (int) ( mag()+100.5 )-100 ) ) ;

		return r ;
	}

	public double RAh() {
		return new Double( RAh ).doubleValue() ;
	}

	public double RAm() {
		return new Double( RAm ).doubleValue() ;
	}

	public double DEd() {
		return new Double( DE+DEd ).doubleValue() ;
	}

	public double DEm() {
		return new Double( DE+DEm ).doubleValue() ;
	}

	public double mag() {
		return new Double( mag ).doubleValue() ;
	}
}
