
package astrolabe;

import java.util.HashSet;
import java.util.Set;

import org.exolab.castor.xml.ValidationException;

import caa.CAA2DCoordinate;
import caa.CAACoordinateTransformation;
import caa.CAAPrecession;

public class CatalogADC7118Record implements CatalogRecord {

	private final static String DEFAULT_STAR = "\uf811" ;

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
		try {
			RAh() ;
			RAm() ;
			DEd() ;
			DEm() ;
			mag() ; // continue new methods
		} catch ( NumberFormatException e ) {
			throw new ParameterNotValidException( e.toString() ) ;
		}
	}

	public astrolabe.model.Body toModel( double epoch ) throws ParameterNotValidException {
		astrolabe.model.Body model ;
		astrolabe.model.Position pm ;
		double[] pv ;
		CAA2DCoordinate ceq ;

		model = new astrolabe.model.Body() ;
		model.setBodyStellar( new astrolabe.model.BodyStellar() ) ;
		model.getBodyStellar().setName( Name ) ;
		model.getBodyStellar().setGlyph( DEFAULT_STAR ) ;
		model.getBodyStellar().setType( "mag"+( (int) ( mag()+100.5 )-100 ) ) ;
		model.getBodyStellar().setTurn( 0 ) ;
		model.getBodyStellar().setSpin( 0 ) ;

		ceq = CAAPrecession.PrecessEquatorial( RAh()+RAm()/60., DEd()+DEm()/60., 2451545./*J2000*/, epoch ) ;
		pm = new astrolabe.model.Position() ;
		pv = new double[] { 1, CAACoordinateTransformation.HoursToDegrees( ceq.X() ), ceq.Y() } ;
		AstrolabeFactory.modelOf( pv, pm ) ;
		model.getBodyStellar().setPosition( pm ) ;
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
		r.add( "mag"+( (int) ( mag()+100.5 )-100 ) ) ;

		return r ;
	}

	public String ident() {
		return Name ;
	}

	public java.util.Vector<double[]> list( Projector projector ) {
		java.util.Vector<double[]> r = new java.util.Vector<double[]>() ;
		double ra, de, xy[] ;

		ra = RAh()+RAm()/60. ;
		de = DEd()+DEm()/60. ;
		xy = projector.project( CAACoordinateTransformation.HoursToRadians( ra ),
				CAACoordinateTransformation.DegreesToRadians( de ) ) ;
		r.add( xy ) ;

		return r ;
	}

	public void register() {
		String key ;

		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC7118_NAME ) ;
		ApplicationHelper.registerName( key, Name ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC7118_TYPE ) ;
		ApplicationHelper.registerName( key, Type  ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC7118_RAH ) ;
		ApplicationHelper.registerName( key, RAh ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC7118_RAM ) ;
		ApplicationHelper.registerName( key, RAm ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC7118_DE ) ;
		ApplicationHelper.registerName( key, DE ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC7118_DED ) ;
		ApplicationHelper.registerName( key, DEd  ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC7118_DEM ) ;
		ApplicationHelper.registerName( key, DEm ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC7118_SOURCE ) ;
		ApplicationHelper.registerName( key, Source ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC7118_CONST ) ;
		ApplicationHelper.registerName( key, Const ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC7118_L_SIZE ) ;
		ApplicationHelper.registerName( key, l_size ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC7118_SIZE ) ;
		ApplicationHelper.registerName( key, size ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC7118_MAG ) ;
		ApplicationHelper.registerName( key, mag ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC7118_N_MAG ) ;
		ApplicationHelper.registerName( key, n_mag ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC7118_DESC ) ;
		ApplicationHelper.registerName( key, Desc ) ;
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
