
package astrolabe;

import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.xml.ValidationException;

import caa.CAACoordinateTransformation;

@SuppressWarnings("serial")
public class Sign extends astrolabe.model.Sign implements PostscriptEmitter {

	private final static Log log = LogFactory.getLog( Sign.class ) ;

	private Projector projector ;
	private Catalog catalog ;

	public Sign( Peer peer, Projector projector, Catalog catalog ) {
		peer.setupCompanion( this ) ;

		this.projector = projector ;
		this.catalog = catalog ;
	}

	public void headPS( AstrolabePostscriptStream ps ) {
	}

	public void emitPS( AstrolabePostscriptStream ps ) {
		astrolabe.model.BodyAreal peer ;
		astrolabe.model.Position pm ;
		BodyAreal companion ;
		CatalogRecord record ;

		try {
			for ( String segment : getValue().split( "," ) ) {
				peer = new astrolabe.model.BodyAreal() ;
				if ( getName() == null )
					peer.setName( ApplicationConstant.GC_NS_SGN ) ;
				else
					peer.setName( ApplicationConstant.GC_NS_SGN+getName() ) ;
				AstrolabeFactory.modelOf( peer, false ) ;

				peer.setBodyArealTypeChoice( new astrolabe.model.BodyArealTypeChoice() ) ;

				for ( String ident : segment.split( ":" ) ) {
					if ( ( record = catalog.getCatalogRecord( ident ) ) == null ) {
						String msg ;

						msg = MessageCatalog.message( ApplicationConstant.GC_APPLICATION, ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
						msg = MessageFormat.format( msg, new Object[] { "\""+ident+"\"", "\""+segment+"\"" } ) ;

						throw new ParameterNotValidException( msg ) ;
					}

					pm = new astrolabe.model.Position() ;
					// astrolabe.model.SphericalType
					pm.setR( new astrolabe.model.R() ) ;
					pm.getR().setValue( 1 ) ;
					// astrolabe.model.AngleType
					pm.setPhi( new astrolabe.model.Phi() ) ;
					pm.getPhi().setRational( new astrolabe.model.Rational() ) ;
					pm.getPhi().getRational().setValue( CAACoordinateTransformation.HoursToDegrees( record.RA()[0] ) ) ;  
					// astrolabe.model.AngleType
					pm.setTheta( new astrolabe.model.Theta() ) ;
					pm.getTheta().setRational( new astrolabe.model.Rational() ) ;
					pm.getTheta().getRational().setValue( record.de()[0] ) ;  
				}

				try {
					peer.validate() ;
				} catch ( ValidationException e ) {
					throw new RuntimeException( e.toString() ) ;
				}

				companion = new BodyAreal( peer, projector ) ;
				if ( getAnnotation() != null )
					companion.addAnnotation( getAnnotation() ) ;

				ps.operator.gsave() ;

				companion.headPS( ps ) ;
				companion.emitPS( ps ) ;
				companion.tailPS( ps ) ;

				ps.operator.grestore() ;
			}
		} catch ( ParameterNotValidException e ) {
			log.warn( e.toString() ) ;
		}
	}

	public void tailPS( AstrolabePostscriptStream ps ) {
	}

}
