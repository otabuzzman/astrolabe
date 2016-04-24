
package astrolabe;

public class GraduationHalf extends GraduationSpan {

	public GraduationHalf( Vector origin, Vector tangent ) throws ParameterNotValidException {
		super( origin, tangent ) ;

		space = getClassNode( null, null ).getDouble( "space", .4 ) ;
		linelength = getClassNode( null, null ).getDouble( "linelength", 3.6 ) ;
		linewidth = getClassNode( null, null ).getDouble( "linewidth", .01 ) ;
	}
}
