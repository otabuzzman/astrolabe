
package astrolabe;

public class DialHour extends DialDegree {

	public DialHour( astrolabe.model.DialType dlT, Circle circle ) {
		super( dlT, circle ) ;

		Quantity quantity ;

		quantity = new QuantityHour( circle ) ;
		setQuantity( quantity ) ;
	}
}
