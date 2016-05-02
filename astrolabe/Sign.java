
package astrolabe;

import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.xml.ValidationException;

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
		astrolabe.model.Body body ;
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

				for ( String ident : segment.split( ":" ) ) {
					if ( ( record = catalog.getCatalogRecord( ident ) ) == null ) {
						String msg ;

						msg = MessageCatalog.message( ApplicationConstant.GC_APPLICATION, ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
						msg = MessageFormat.format( msg, new Object[] { "\""+ident+"\"", "\""+segment+"\"" } ) ;

						throw new ParameterNotValidException( msg ) ;
					}
					try {
						body = new astrolabe.model.Body() ;
						body.setBodyStellar( new astrolabe.model.BodyStellar() ) ;
						if ( getName() == null )
							body.getBodyStellar().setName( ApplicationConstant.GC_NS_SGN ) ;
						else
							body.getBodyStellar().setName( ApplicationConstant.GC_NS_SGN+getName() ) ;
						AstrolabeFactory.modelOf( body.getBodyStellar(), false ) ;

						body.getBodyStellar().setScript( new astrolabe.model.Script() ) ;
						body.getBodyStellar().getScript().setPurpose( ApplicationConstant.AV_SCRIPT_NONE ) ;
						body.getBodyStellar().getScript().setValue( "" ) ;

						record.toModel( body ) ;

						peer.addPosition( body.getBodyStellar().getPosition() ) ;
					} catch ( ValidationException e ) {
						throw new RuntimeException( e.toString() ) ;
					}
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
