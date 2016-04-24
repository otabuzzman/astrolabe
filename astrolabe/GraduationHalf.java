
package astrolabe;

public class GraduationHalf extends GraduationSpan {

	public GraduationHalf( Vector origin, Vector tangent ) {
		super( origin, tangent ) ;

		double space, linelength, linewidth ;

		space = ApplicationHelper.getClassNode( this, null, null ).getDouble( ApplicationConstant.PK_GRADUATION_SPACE, .4 ) ;
		setSpace( space ) ;
		linelength = ApplicationHelper.getClassNode( this, null, null ).getDouble( ApplicationConstant.PK_GRADUATION_LINELENGTH, 3.6 ) ;
		setLinelength( linelength ) ;
		linewidth = ApplicationHelper.getClassNode( this, null, null ).getDouble( ApplicationConstant.PK_GRADUATION_LINEWIDTH, .01 ) ;
		setLinewidth( linewidth ) ;
	}
}
