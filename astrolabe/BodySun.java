
package astrolabe;

import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SuppressWarnings("serial")
public class BodySun extends BodyPlanet {

	private final static Log log = LogFactory.getLog( BodySun.class ) ;

	private Projector projector ;

	private Circle circle ;

	public BodySun( Object peer, double epoch, Projector projector ) throws ParameterNotValidException {
		super( peer, epoch, projector ) ;

		String circle ;

		this.projector = projector ;

		circle = ( (astrolabe.model.BodySun) peer ).getCircle() ;
		if ( circle != null ) {
			try {
				this.circle = (Circle) Registry.retrieve( circle ) ;
			} catch ( ParameterNotValidException e ) {
				String msg ;

				msg = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
				msg = MessageFormat.format( msg, new Object[] { null, "\""+circle+"\"" } ) ;
				log.warn( msg ) ;

				this.circle = null ;
			}
		}
	}

	public void emitPS( PostscriptStream ps ) {
		if ( circle==null ) {
			super.emitPS( ps ) ;
		} else {
			if ( getDialDay() != null ) {
				Dial dial ;

				ps.operator.gsave() ;
				try {
					dial = new DialDay( getDialDay(), this ) ;
					dial.headPS( ps ) ;
					dial.emitPS( ps ) ;
					dial.tailPS( ps ) ;
				} catch ( ParameterNotValidException e ) {} // DialDay validated in constructor

				ps.operator.grestore() ;
			}

			if ( getAnnotation() != null ) {
				try {
					ApplicationHelper.emitPS( ps, getAnnotation() ) ;
				} catch ( ParameterNotValidException e ) {
					throw new RuntimeException( e.toString() ) ;
				}
			}
		}
	}

	public double[] project( double jd, double shift ) {
		double[] r ;
		double a ;

		if ( circle==null ) {
			r = super.project( jd, shift ) ;
		} else {
			a = angle( jd ) ;
			r = circle.project( a, shift ) ;
		}

		return r ;
	}

	public double[] tangent( double jd ) {
		double[] r ;
		double a ;

		if ( circle==null ) {
			r = super.tangent( jd ) ;
		} else {
			a = angle( jd ) ;
			r = circle.tangent( a ) ;
		}

		return r ;
	}

	private double angle( double jd ) {
		double r ;
		double[] ec, eq ;

		ec = position( jd ) ;
		eq = projector.convert( ec ) ;
		r = circle.unconvert( eq ) ;

		return r ;
	}
}
