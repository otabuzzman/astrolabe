
package astrolabe;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SuppressWarnings("serial")
public class CatalogADC6049 extends CatalogType implements PostscriptEmitter {

	private final static int C_CHUNK = 29+1/*0x0a*/ ;

	private final static Log log = LogFactory.getLog( CatalogADC6049.class ) ;

	private HashSet<String> restrict ;

	private Projector projector ;
	private double epoch ;

	private String memory ;

	public CatalogADC6049( Peer peer, Projector projector ) throws ParameterNotValidException {
		super( peer, projector ) ;

		String[] rv ;
		double epoch ;

		this.projector = projector ;

		epoch = ( (Double) Registry.retrieve( ApplicationConstant.GC_EPOCHE ) ).doubleValue() ;
		this.epoch = epoch ;

		restrict = new HashSet<String>() ;
		if ( ( (astrolabe.model.CatalogADC6049) peer ).getRestrict() != null ) {
			rv = ( (astrolabe.model.CatalogADC6049) peer ).getRestrict().split( "," ) ;
			for ( int v=0 ; v<rv.length ; v++ ) {
				restrict.add( rv[v] ) ;
			}
		}

		memory = new String() ;
	}

	public void headPS( PostscriptStream ps ) {
	}

	public void emitPS( PostscriptStream ps ) {
		Hashtable<String, CatalogRecord> catalog ;
		astrolabe.model.Annotation[] annotation ;
		astrolabe.model.BodyAreal bodyModel ;
		BodyAreal bodyAreal ;

		try {
			catalog = read() ;
		} catch ( ParameterNotValidException e ) {
			throw new RuntimeException( e.toString() ) ;
		}

		for ( CatalogRecord record : arrange( catalog ) ) {
			try {
				bodyModel = record.toModel( epoch ).getBodyAreal() ;

				annotation = annotation( record ) ;
				if ( annotation != null ) {
					bodyModel.setAnnotation( annotation ) ;

					record.register() ;
				}

				bodyAreal = new BodyAreal( bodyModel, projector ) ;
			} catch ( ParameterNotValidException e ) {
				throw new RuntimeException( e.toString() ) ;
			}

			ps.operator.gsave() ;

			bodyAreal.headPS( ps ) ;
			bodyAreal.emitPS( ps ) ;
			bodyAreal.tailPS( ps ) ;

			ps.operator.grestore() ;
		}
	}

	public void tailPS( PostscriptStream ps ) {
	}

	public CatalogRecord record( java.io.Reader catalog ) {
		CatalogADC6049Record r = null ;
		char[] c ;
		String rl/*record line*/, rb/*record buffer*/, rc/*record constellation*/, mc/*memory constellation*/ ;

		c = new char[C_CHUNK] ;
		if ( memory.length()==0 ) {
			rb = new String() ;
		} else {
			rb = new String( memory ) ;
		}

		try {
			while ( catalog.read( c, 0, C_CHUNK ) == C_CHUNK ) {
				rl = new String( c ) ;
				if ( memory.length()==0 ) {
					memory = rl ;
				}
				rc = rl.substring( 23, 27 ).trim() ;
				mc = memory.substring( 23, 27 ).trim() ;
				if ( rc.equals( mc ) ) {
					rb = rb+rl ;

					continue ;
				} else {
					memory = rl ;

					try {
						r = new CatalogADC6049Record( rb ) ;

						if ( r.matchAny( restrict ) ) {
							break ;
						} else {
							rb = rl ;

							continue ;
						}
					} catch ( ParameterNotValidException e ) {
						String msg ;

						msg = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
						msg = MessageFormat.format( msg, new Object[] { "\""+rb+"\"", "" } ) ;
						log.warn( msg ) ;

						rb = rl ;

						continue ;
					}
				}
			}
		} catch ( IOException e ) {
			throw new RuntimeException( e.toString() ) ;
		}

		return r ;
	}

	public CatalogRecord[] arrange( Hashtable<String, CatalogRecord> catalog ) {
		CatalogRecord[] r = new CatalogRecord[catalog.size()] ;

		return catalog.values().toArray( r ) ;
	}
}
