
package astrolabe;

@SuppressWarnings("serial")
public class BodyAreal extends astrolabe.model.BodyAreal implements Body {

	private Projector projector ;

	private final static double DEFAULT_LINEWIDTH = 0.36 ;
	private final static double DEFAULT_LINEDASH = 0 ;

	private java.util.Vector<double[]> outline ;

	private double linewidth ;
	private double linedash ;

	public BodyAreal( Object peer, Projector projector ) throws ParameterNotValidException {
		double[] lo ;

		ApplicationHelper.setupCompanionFromPeer( this, peer ) ;

		this.projector = projector ;

		outline = new java.util.Vector<double[]>() ;
		for ( int pn=0 ; pn<getPositionCount() ; pn++ ) {
			lo = AstrolabeFactory.valueOf( getPosition( pn ) ) ;
			outline.add( new double[] { lo[1], lo[2] } ) ; // lo[0] is r
		}

		linewidth = ApplicationHelper.getClassNode( this,
				getName(), getType() ).getDouble( ApplicationConstant.PK_BODY_LINEWIDTH, DEFAULT_LINEWIDTH ) ;
		linedash = ApplicationHelper.getClassNode( this,
				getName(), getType() ).getDouble( ApplicationConstant.PK_BODY_LINEDASH, DEFAULT_LINEDASH ) ;
	}

	public void headPS( PostscriptStream ps ) {
		ps.operator.setlinewidth( linewidth ) ;
		ps.operator.setdash( linedash ) ;
	}

	public void emitPS( PostscriptStream ps ) {
		double[] ho, xy ;
		java.util.Vector<double[]> pn ;
		double[] pt ;
		Vector a, b ;
		double s ;

		pn = new java.util.Vector<double[]>() ;

		ps.operator.mark() ;
		for ( int n=outline.size() ; n>0 ; n-- ) {
			ho = (double[]) outline.get( n-1 ) ;
			xy = projector.project( ho[0], ho[1] ) ;
			ps.push( xy[0] ) ;
			ps.push( xy[1] ) ;

			pn.add( xy ) ;
		}
		try {
			ps.custom( ApplicationConstant.PS_PROLOG_POLYLINE ) ;
		} catch ( ParameterNotValidException e ) {} // polyline is considered well-defined
		ps.operator.stroke() ;

		a = new Vector( pn.get( 0 ) ) ;
		b = new Vector( pn.get( 1 ) ) ;
		b.sub( a ) ;
		b.size( .001 ) ;
		b.rotate( Math.rad1 ) ;
		a.add( b ) ;
		pt = new double[] { a.getX(), a.getY() } ;

		s = 2 ;
		try {
			ps.operator.mark() ;
			ps.push( Math.isPointInsidePolygon( pn , pt )?-s:s ) ; // shift value
			ps.push( true ) ; // parallel edges
			ps.custom( ApplicationConstant.PS_PROLOG_PATHSHIFT ) ;
			ps.operator.stroke() ;
		} catch ( ParameterNotValidException e ) {} // polyline is considered well-defined

		try {
			ApplicationHelper.emitPS( ps, getAnnotationCurved() ) ;
		} catch ( ParameterNotValidException e ) {} // optional
	}

	public void tailPS( PostscriptStream ps ) {
	}
}
