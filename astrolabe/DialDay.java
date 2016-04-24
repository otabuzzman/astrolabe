
package astrolabe;

public class DialDay extends DialDegree {

	public DialDay( astrolabe.model.DialType dlT, Circle circle ) throws ParameterNotValidException {
		super( dlT, circle ) ;

		Sun sun ;
		Quantity quantity ;

		sun = AstrolabeFactory.createSun( ( (astrolabe.model.DialDay) dlT ).getSun() ) ;
		quantity = new QuantityDay( circle, sun ) ;
		setQuantity( quantity ) ;
	}
}
