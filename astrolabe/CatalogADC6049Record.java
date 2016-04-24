
package astrolabe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import org.exolab.castor.xml.ValidationException;

import caa.CAACoordinateTransformation;

public class CatalogADC6049Record implements CatalogRecord {

	public java.util.Vector<String> RAhr  ; // Right ascension in decimal hours (J2000)
	public java.util.Vector<String> DEdeg ; // Declination in degrees (J2000)
	public String                   con   ; // Constellation abbreviation
	public String                   type  ; // [OI] Type of point (Original or Interpolated)

	public CatalogADC6049Record( String data ) throws ParameterNotValidException {
		BufferedReader b ;
		String l, lv[] ;

		RAhr = new java.util.Vector<String>() ;
		DEdeg = new java.util.Vector<String>() ;

		try {
			b = new BufferedReader( new StringReader( data ) ) ;

			while ( ( l = b.readLine() ) != null ) {
				lv = l.trim().split( "[ ]+" ) ;
				RAhr.add( lv[0] ) ;
				DEdeg.add( lv[1] ) ;
				con  = lv[2] ;
				type = lv[3] ;
			}
		} catch ( IOException e ) {
			throw new RuntimeException( e.toString() ) ;
		}

		// validation
		try {
			for ( int position=0 ; position<RAhr.size() ; position++ ) {
				RAhr( position ) ;
				DEdeg( position ) ;
			} // continue new methods
		} catch ( NumberFormatException e ) {
			throw new ParameterNotValidException( e.toString() ) ;
		}
	}

	public astrolabe.model.Body toBody() throws ParameterNotValidException {
		astrolabe.model.Body model ;

		model = new astrolabe.model.Body() ;
		model.setBodyAreal( new astrolabe.model.BodyAreal() ) ;
		model.getBodyAreal().setName( con ) ;
		model.getBodyAreal().setType( ApplicationConstant.AV_BODY_CONSTELLATION ) ;
		for ( int position=0 ; position<RAhr.size() ; position++ ) {
			model.getBodyAreal().addPosition( AstrolabeFactory.modelPosition(
					CAACoordinateTransformation.HoursToDegrees( RAhr( position ) ), DEdeg( position ) ) ) ;
		}
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

		for ( String k : new String[] { con } ) {
			if ( list.contains( k ) ) {
				r.add( k ) ;
			}
		}

		return r ;
	}

	public java.util.Vector<double[]> list( Projector projector ) {
		java.util.Vector<double[]> r = new java.util.Vector<double[]>() ;
		double ra, de, xy[] ;

		for ( int position=0 ; position<RAhr.size() ; position++ ) {
			ra = RAhr( position ) ;
			de = DEdeg( position ) ;
			xy = projector.project( CAACoordinateTransformation.HoursToRadians( ra ),
					CAACoordinateTransformation.DegreesToRadians( de ) ) ;
			r.add( xy ) ;
		}

		return r ;
	}

	public void register() {
		String k, p, v ;

		k = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC6049_CONSTELLATION ) ;
		p = MessageFormat.format( ApplicationConstant.LP_ADC6049_CONSTELLATION, new Object[] { con } ) ;
		v = ApplicationHelper.getLocalizedString( p ) ;
		ApplicationHelper.registerName( k, v ) ;

		k = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC6049_ABBREVIATION ) ;
		p = MessageFormat.format( ApplicationConstant.LP_ADC6049_ABBREVIATION, new Object[] { con } ) ;
		v = ApplicationHelper.getLocalizedString( p ) ;
		ApplicationHelper.registerName( k, v ) ;

		k = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC6049_NOMINATIVE ) ;
		p = MessageFormat.format( ApplicationConstant.LP_ADC6049_NOMINATIVE, new Object[] { con } ) ;
		v = ApplicationHelper.getLocalizedString( p ) ;
		ApplicationHelper.registerName( k, v ) ;

		k = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC6049_GENITIVE ) ;
		p = MessageFormat.format( ApplicationConstant.LP_ADC6049_GENITIVE, new Object[] { con } ) ;
		v = ApplicationHelper.getLocalizedString( p ) ;
		ApplicationHelper.registerName( k, v ) ;
	}

	public double RAhr( int index ) {
		return new Double( RAhr.get( index ) ).doubleValue() ;		
	}

	public double DEdeg( int index ) {
		return new Double( DEdeg.get( index ) ).doubleValue() ;
	}
}
