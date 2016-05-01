
package astrolabe;

import java.io.OutputStream;
import java.text.MessageFormat;

public class AstrolabePostscriptStream extends PostscriptStream {

	public AstrolabePostscriptStream( OutputStream out ) {
		super( out ) ;
	}

	public void push( String string ) {
		try {
			super.push( string ) ;
		} catch ( ParameterNotValidException e ) {
			String msg ;

			msg = MessageCatalog.message( ApplicationConstant.GC_APPLICATION, ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
			msg = MessageFormat.format( msg, new Object[] { e.toString(), "" } ) ;

			throw new RuntimeException( msg ) ;
		}
	} 

	public void push( String[] string ) {        
		try {
			super.push( string ) ;
		} catch ( ParameterNotValidException e ) {
			String msg ;

			msg = MessageCatalog.message( ApplicationConstant.GC_APPLICATION, ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
			msg = MessageFormat.format( msg, new Object[] { e.toString(), "" } ) ;

			throw new RuntimeException( msg ) ;
		}
	}

	public void custom( String def ) {
		try {
			super.custom( def ) ;
		} catch ( ParameterNotValidException e ) {
			String msg ;

			msg = MessageCatalog.message( ApplicationConstant.GC_APPLICATION, ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
			msg = MessageFormat.format( msg, new Object[] { e.toString(), "" } ) ;

			throw new RuntimeException( msg ) ;
		}
	}
}
