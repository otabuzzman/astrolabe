
package astrolabe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.exolab.castor.xml.ValidationException;

import caa.CAA2DCoordinate;
import caa.CAACoordinateTransformation;
import caa.CAAPrecession;

public class CatalogADC6049Record implements CatalogRecord {

	public final static int CR_LENGTH = 29 ;

	public List<String>	RAhr  ; // Right ascension in decimal hours (J2000)
	public List<String>	DEdeg ; // Declination in degrees (J2000)
	public String		con   ; // Constellation abbreviation
	public String		type  ; // [OI] Type of point (Original or Interpolated)

	public CatalogADC6049Record( String data ) throws ParameterNotValidException, NumberFormatException {
		BufferedReader b ;
		String l, lv[] ;

		RAhr = new java.util.Vector<String>() ;
		DEdeg = new java.util.Vector<String>() ;

		try {
			b = new BufferedReader( new StringReader( data ) ) ;

			while ( ( l = b.readLine() ) != null ) {
				if ( l.length() != CR_LENGTH ) {
					throw new ParameterNotValidException(  Integer.toString( l.length() ) ) ;
				}

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
		for ( int position=0 ; position<RAhr.size() ; position++ ) {
			RAhr( position ) ;
			DEdeg( position ) ; // continue new methods
		}
	}

	public astrolabe.model.Body toModel( double epoch ) throws ValidationException {
		astrolabe.model.Body model ;
		astrolabe.model.Position pm ;
		CAA2DCoordinate ceq ;

		model = new astrolabe.model.Body() ;
		model.setBodyAreal( new astrolabe.model.BodyAreal() ) ;
		model.getBodyAreal().setName( con ) ;
		model.getBodyAreal().setImportance( ApplicationConstant.AV_BODY_DIVIDING ) ;
		for ( int p=0 ; p<RAhr.size() ; p++ ) {
			ceq = CAAPrecession.PrecessEquatorial( RAhr( p ), DEdeg( p ), 2451545./*J2000*/, epoch ) ;
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

			model.getBodyAreal().addPosition( pm ) ;
			ceq.delete() ;
		}

		model.validate() ;

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

		return r ;
	}

	public String ident() {
		return con ;
	}

	public List<double[]> list( Projector projector ) {
		List<double[]> r = new java.util.Vector<double[]>() ;
		double ra, de, xy[] ;

		for ( int position=0 ; position<RAhr.size() ; position++ ) {
			ra = RAhr( position ) ;
			de = DEdeg( position ) ;
			xy = projector.project( CAACoordinateTransformation.HoursToDegrees( ra ), de ) ;
			r.add( xy ) ;
		}

		return r ;
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

	public double RAhr( int index ) {
		return new Double( RAhr.get( index ) ).doubleValue() ;		
	}

	public double DEdeg( int index ) {
		return new Double( DEdeg.get( index ) ).doubleValue() ;
	}
}
