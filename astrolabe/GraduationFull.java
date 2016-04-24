
package astrolabe;

public class GraduationFull extends GraduationSpan {

	public GraduationFull( Vector origin, Vector tangent ) throws ParameterNotValidException {
		super( origin, tangent ) ;

		space = getClassNode( null, null ).getDouble( "space", .4 ) ;
		linelength = getClassNode( null, null ).getDouble( "linelength", 4.2 ) ;
		linewidth = getClassNode( null, null ).getDouble( "linewidth", .01 ) ;
	}
}
