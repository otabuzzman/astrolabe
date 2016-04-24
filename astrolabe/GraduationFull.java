
package astrolabe;

public class GraduationFull extends GraduationUnit {

	public GraduationFull( Vector origin, Vector tangent ) {
		super( origin, tangent ) ;

		space = getClassNode( null, null ).getDouble( "space", .4 ) ;
		linelength = getClassNode( null, null ).getDouble( "linelength", 4.2 ) ;
		linewidth = getClassNode( null, null ).getDouble( "linewidth", .01 ) ;
	}
}
