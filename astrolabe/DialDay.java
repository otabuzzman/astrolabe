
package astrolabe;

public class DialDay extends DialDegree {

	public DialDay( astrolabe.model.DialType dlT, Circle circle ) throws ParameterNotValidException {
		super( dlT, circle ) ;

		Sun sun ;
		Span quantity ;
		astrolabe.model.DialDay ddT ;

		ddT = (astrolabe.model.DialDay) dlT ;

		sun = new Sun() ;
		if ( ddT.getSun().equals( ApplicationConstant.AV_DIAL_APPARENT ) ) {
			sun.setPositionApparent() ;
		} else if ( ddT.getSun().equals( ApplicationConstant.AV_DIAL_MEAN ) ) {
			sun.setPositionMean() ;
		} else { // ApplicationConstant.AV_DIAL_TRUE
			sun.setPositionTrue() ;
		}

		quantity = new SpanDay( circle, sun ) ;
		setQuantity( quantity ) ;
	}
}
