
package astrolabe;

import java.util.List;

import org.exolab.castor.xml.ValidationException;

import caa.CAACoordinateTransformation;

public class CatalogADC7237Record implements CatalogRecord {

	private final static int CR_LENGTH = 343 ;

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

	public CatalogADC7237Record( String data ) throws ParameterNotValidException, NumberFormatException {

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

		// validation
		RAh() ;
		RAm() ;
		RAs() ;
		DEd() ;
		DEm() ;
		DEs() ;
		logD25() ; // continue new methods
	}

	public void toModel( astrolabe.model.Body body ) throws ValidationException {
		astrolabe.model.Position pm ;
		double ra, de ;

		body.getBodyStellar().setName( PGC ) ;

		ra = RAh()+RAm()/60.+RAs()/3600. ;
		de = DEd()+DEm()/60.+DEs()/3600. ;
		pm = new astrolabe.model.Position() ;
		// astrolabe.model.SphericalType
		pm.setR( new astrolabe.model.R() ) ;
		pm.getR().setValue( 1 ) ;
		// astrolabe.model.AngleType
		pm.setPhi( new astrolabe.model.Phi() ) ;
		pm.getPhi().setRational( new astrolabe.model.Rational() ) ;
		pm.getPhi().getRational().setValue( CAACoordinateTransformation.HoursToDegrees( ra ) ) ;  
		// astrolabe.model.AngleType
		pm.setTheta( new astrolabe.model.Theta() ) ;
		pm.getTheta().setRational( new astrolabe.model.Rational() ) ;
		pm.getTheta().getRational().setValue( de ) ;  

		body.getBodyStellar().setPosition( pm ) ;

		body.validate() ;
	}

	public List<double[]> list( Projector projector ) {
		List<double[]> r = new java.util.Vector<double[]>() ;
		double ra, de, xy[] ;

		ra = RAh()+RAm()/60.+RAs()/3600. ;
		de = DEd()+DEm()/60.+DEs()/3600. ;
		xy = projector.project( CAACoordinateTransformation.HoursToDegrees( ra ), de ) ;
		r.add( xy ) ;

		return r ;
	}

	public void register() {
		MessageCatalog m ;
		String key ;

		m = new MessageCatalog( ApplicationConstant.GC_APPLICATION ) ;

		key = m.message( ApplicationConstant.LK_ADC7237_PGC ) ;
		Registry.registerName( key, PGC ) ;
		key = m.message( ApplicationConstant.LK_ADC7237_RAH ) ;
		Registry.registerName( key, RAh ) ;
		key = m.message( ApplicationConstant.LK_ADC7237_RAM ) ;
		Registry.registerName( key, RAm ) ;
		key = m.message( ApplicationConstant.LK_ADC7237_RAS ) ;
		Registry.registerName( key, RAs ) ;
		key = m.message( ApplicationConstant.LK_ADC7237_DE ) ;
		Registry.registerName( key, DE ) ;
		key = m.message( ApplicationConstant.LK_ADC7237_DED ) ;
		Registry.registerName( key, DEd ) ;
		key = m.message( ApplicationConstant.LK_ADC7237_DEM ) ;
		Registry.registerName( key, DEm ) ;
		key = m.message( ApplicationConstant.LK_ADC7237_DES ) ;
		Registry.registerName( key, DEs ) ;
		key = m.message( ApplicationConstant.LK_ADC7237_OTYPE ) ;
		Registry.registerName( key, OType ) ;
		key = m.message( ApplicationConstant.LK_ADC7237_MTYPE ) ;
		Registry.registerName( key, MType ) ;
		key = m.message( ApplicationConstant.LK_ADC7237_LOGD25 ) ;
		Registry.registerName( key, logD25 ) ;
		key = m.message( ApplicationConstant.LK_ADC7237_E_LOGD25 ) ;
		Registry.registerName( key, e_logD25 ) ;
		key = m.message( ApplicationConstant.LK_ADC7237_LOGR25 ) ;
		Registry.registerName( key, logR25 ) ;
		key = m.message( ApplicationConstant.LK_ADC7237_E_LOGR25 ) ;
		Registry.registerName( key, e_logR25 ) ;
		key = m.message( ApplicationConstant.LK_ADC7237_PA ) ;
		Registry.registerName( key, PA ) ;
		key = m.message( ApplicationConstant.LK_ADC7237_E_PA ) ;
		Registry.registerName( key, e_PA ) ;
		key = m.message( ApplicationConstant.LK_ADC7237_O_ANAMES ) ;
		Registry.registerName( key, o_ANames ) ;
		key = m.message( ApplicationConstant.LK_ADC7237_ANAMES ) ;
		Registry.registerName( key, ANames ) ;
	}

	public List<String> ident() {
		List<String> r = new java.util.Vector<String>( 2 ) ;

		r.add( PGC ) ;

		return r ;
	}

	public double RAh() {
		return new Double( RAh ).doubleValue() ;
	}

	public double RAm() {
		return new Double( RAm ).doubleValue() ;
	}

	public double RAs() {
		return new Double( RAs ).doubleValue() ;
	}

	public double DEd() {
		return new Double( DE+DEd ).doubleValue() ;
	}

	public double DEm() {
		return new Double( DE+DEm ).doubleValue() ;
	}

	public double DEs() {
		return new Double( DE+DEs ).doubleValue() ;
	}

	public double logD25() {
		return new Double( logD25 ).doubleValue() ;
	}
}
