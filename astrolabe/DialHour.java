
package astrolabe;

public class DialHour extends DialDegree {

	public DialHour( astrolabe.model.DialType dlT, Circle circle ) {
		super( dlT, circle ) ;

		Span quantity ;

		quantity = new SpanHour( circle ) ;
		setQuantity( quantity ) ;
	}
}
