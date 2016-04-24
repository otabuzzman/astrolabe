
package astrolabe;

@SuppressWarnings("serial")
public class BodyAreal extends astrolabe.model.BodyAreal implements Body {

	private Projector projector ;

	private final static double DEFAULT_LINEWIDTH = 0.36 ;
	private final static double DEFAULT_LINEDASH = 0 ;

	private double linewidth ;
	private double linedash ;

	public BodyAreal( Object peer, Projector projector ) throws ParameterNotValidException {
		ApplicationHelper.setupCompanionFromPeer( this, peer ) ;

		this.projector = projector ;

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
		double[] lo, xy ;
		java.util.Vector<double[]> p ;
		Vector k, l ;

		p = new java.util.Vector<double[]>() ;

		try {
			ps.operator.mark() ;
			for ( int n=getPositionCount() ; n>0 ; n-- ) {
				lo = AstrolabeFactory.valueOf( getPosition( n-1 ) ) ;
				xy = projector.project( lo[1], lo[2] ) ;

				p.add( xy ) ;

				ps.push( xy[0] ) ;
				ps.push( xy[1] ) ;
			}
			ps.custom( ApplicationConstant.PS_PROLOG_POLYLINE ) ;
		} catch ( ParameterNotValidException e ) {}
		// polyline is considered well-defined,
		// valuOf should not fail in for clause

		k = new Vector( p.get( 0 ) ) ;
		l = new Vector( p.get( 1 ) ) ;
		l.sub( k ) ;
		l.rotate( Math.rad1 ) ;
		k.add( l ) ;

		try {
			ps.operator.mark() ;
			ps.push( Math.polygonCoversPoint( p , k.get() )?-linewidth/2:linewidth/2 ) ;
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
