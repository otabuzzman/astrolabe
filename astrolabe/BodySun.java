
package astrolabe;

@SuppressWarnings("serial")
public class BodySun extends BodyPlanet {

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
			} catch ( ParameterNotValidException e ) {}
		}
	}

	public void emitPS( PostscriptStream ps ) {
		if ( circle==null ) {
			super.emitPS( ps ) ;
		} else {
			if ( getDialDay() != null ) {
				Dial dial ;

				ps.operator.gsave() ;

				dial = new DialDay( getDialDay(), this ) ;
				dial.headPS( ps ) ;
				dial.emitPS( ps ) ;
				dial.tailPS( ps ) ;

				ps.operator.grestore() ;
			}

			try {
				ApplicationHelper.emitPS( ps, getAnnotation() ) ;
			} catch ( ParameterNotValidException e ) {} // optional
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
