
package astrolabe;

import caa.CAACoordinateTransformation;

@SuppressWarnings("serial")
public class BodyAreal extends astrolabe.model.BodyAreal implements Body, CatalogRecord {

	private Projector projector ;

	private final static double DEFAULT_LINEWIDTH = 0.36 ;
	private final static double DEFAULT_LINEDASH = 0 ;

	private double linewidth ;
	private double linedash ;

	private java.util.Vector<double[]> outline ;

	public BodyAreal( Object peer, Projector projector ) throws ParameterNotValidException {
		PolygonSpherical polygon ;
		String key ;
		double rad1 ;

		ApplicationHelper.setupCompanionFromPeer( this, peer ) ;

		this.projector = projector ;

		linewidth = ApplicationHelper.getClassNode( this,
				getName(), getType() ).getDouble( ApplicationConstant.PK_BODY_LINEWIDTH, DEFAULT_LINEWIDTH ) ;
		linedash = ApplicationHelper.getClassNode( this,
				getName(), getType() ).getDouble( ApplicationConstant.PK_BODY_LINEDASH, DEFAULT_LINEDASH ) ;

		outline = AstrolabeFactory.valueOf( getPosition() ) ;
		polygon = new PolygonSpherical( outline ) ;
		try {
			key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_BODY_STERADIAN ) ;
			ApplicationHelper.registerDMS( key, polygon.area(), 2 ) ;
			rad1 = CAACoordinateTransformation.DegreesToRadians( 1 ) ;
			key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_BODY_SQUAREDEGREE ) ;
			ApplicationHelper.registerDMS( key, ( rad1*rad1 )*polygon.area(), 2 ) ;		
		} catch ( ParameterNotValidException e ) {}
	}

	public void headPS( PostscriptStream ps ) {
		ps.operator.setlinewidth( linewidth ) ;
		ps.operator.setdash( linedash ) ;
	}

	public void emitPS( PostscriptStream ps ) {
		double[] lo, xy = null ;
		java.util.Vector<double[]> outline ;
		PolygonPlane polygon ;
		Vector z, p ;
		double a ;

		outline = new java.util.Vector<double[]>() ;

		ps.operator.mark() ;

		for ( int n=this.outline.size() ; n>0 ; n-- ) {
			lo = this.outline.get( n-1 ) ;

			xy = projector.project( lo[1], lo[2] ) ;
			ps.push( xy[0] ) ;
			ps.push( xy[1] ) ;

			outline.add( xy ) ;
		}
		try {
			ps.custom( ApplicationConstant.PS_PROLOG_LISTREDUCE ) ;
			ps.custom( ApplicationConstant.PS_PROLOG_POLYLINE ) ;
		} catch ( ParameterNotValidException e ) {}
		// listreduce, polyline are considered well-defined

		polygon = new PolygonPlane( outline ) ;
		try {
			ps.push( polygon.sign()*linewidth/2 ) ;
			ps.push( true ) ; // parallel edges
			ps.custom( ApplicationConstant.PS_PROLOG_PATHSHIFT ) ;
			ps.operator.stroke() ;
		} catch ( ParameterNotValidException e ) {}
		// polyline is considered well-defined

		p = new Vector( xy[0], xy[1] ) ;
		xy = projector.project( 0, Math.rad90 ) ;
		z = new Vector( xy[0], xy[1] ) ; // zenit

		z.sub( p ) ;

		a = java.lang.Math.atan2( z.y, z.x )-Math.rad90 ;
		a = CAACoordinateTransformation.RadiansToDegrees( a ) ;

		ps.operator.rotate( a ) ;

		try {
			ApplicationHelper.emitPS( ps, getAnnotationStraight() ) ;
		} catch ( ParameterNotValidException e ) {} // optional
	}

	public void tailPS( PostscriptStream ps ) {
	}

	public java.util.Vector<double[]> list() {
		java.util.Vector<double[]> r = new java.util.Vector<double[]>() ;
		double[] lo, xy ;

		for ( int n=0 ; n<outline.size() ; n++ ) {
			lo = outline.get( n ) ;
			xy = projector.project( lo[1], lo[2] ) ;
			r.add( xy ) ;
		}

		return r ;
	}
}
