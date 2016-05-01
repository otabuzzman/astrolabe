
package astrolabe;

import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.xml.ValidationException;

@SuppressWarnings("serial")
public class Sign extends astrolabe.model.Sign implements PostscriptEmitter {

	private final static String DEFAULT_IMPORTANCE = ApplicationConstant.AV_BODY_GRAPHICAL ;

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
		BodyAreal companion ;
		CatalogRecord record ;
		String importance ;

		try {
			for ( String body : getValue().split( "," ) ) {
				peer = new astrolabe.model.BodyAreal() ;

				for ( String ident : body.split( ":" ) ) {
					importance = Configuration.getValue(
							Configuration.getClassNode( this, ident, null ),
							ApplicationConstant.PK_SIGN_IMPORTANCE, DEFAULT_IMPORTANCE ) ;
					peer.setName( ident ) ;
					peer.setImportance( importance ) ;

					if ( ( record = catalog.getCatalogRecord( ident ) ) == null ) {
						String msg ;

						msg = MessageCatalog.message( ApplicationConstant.GC_APPLICATION, ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
						msg = MessageFormat.format( msg, new Object[] { "\""+ident+"\"", "\""+body+"\"" } ) ;

						throw new ParameterNotValidException( msg ) ;
					}
					try {
						peer.addPosition( record.toModel().getBodyStellar().getPosition() ) ;
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

				if ( getAnnotationCount()>0 )
					companion.setAnnotation( getAnnotation() ) ;

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
