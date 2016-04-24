
package astrolabe;

public class GraduationFull extends GraduationSpan {

	public GraduationFull( Vector origin, Vector tangent ) {
		super( origin, tangent ) ;

		double space, linelength, linewidth ;

		space = ApplicationHelper.getClassNode( this, null, null ).getDouble( ApplicationConstant.PK_GRADUATION_SPACE, .4 ) ;
		setSpace( space ) ;
		linelength = ApplicationHelper.getClassNode( this, null, null ).getDouble( ApplicationConstant.PK_GRADUATION_LINELENGTH, 4.2 ) ;
		setLinelength( linelength ) ;
		linewidth = ApplicationHelper.getClassNode( this, null, null ).getDouble( ApplicationConstant.PK_GRADUATION_LINEWIDTH, .01 ) ;
		setLinewidth( linewidth ) ;
	}
}
