
package astrolabe;

import java.util.List;

@SuppressWarnings("serial")
public class OutlineElliptical extends astrolabe.model.OutlineElliptical implements PostscriptEmitter {

	private final static double DEFAULT_INTERVAL = 10 ;

	private Projector projector ;

	public OutlineElliptical( Peer peer, Projector projector ) {
		peer.setupCompanion( this ) ;

		this.projector = projector ;
	}

	public void headPS(AstrolabePostscriptStream ps) {
	}

	public void emitPS(AstrolabePostscriptStream ps) {
		ps.array( true ) ;
		for ( double [] xy : list() ) {
			ps.push( xy[0] ) ;
			ps.push( xy[1] ) ;
		}
		ps.array( false ) ;

		ps.operator.newpath() ;
		ps.push( ApplicationConstant.PS_PROLOG_GDRAW ) ;

		// halo stroke
		ps.operator.currentlinewidth() ;

		ps.operator.dup() ;
		ps.operator.div( 100 ) ;
		ps.push( (Double) ( AstrolabeRegistry.retrieve( ApplicationConstant.PK_CHART_HALO ) ) ) ; 
		ps.operator.mul() ;
		ps.push( (Double) ( AstrolabeRegistry.retrieve( ApplicationConstant.PK_CHART_HALOMIN ) ) ) ; 
		ps.push( ApplicationConstant.PS_PROLOG_MAX ) ;
		ps.push( (Double) ( AstrolabeRegistry.retrieve( ApplicationConstant.PK_CHART_HALOMAX ) ) ) ; 
		ps.push( ApplicationConstant.PS_PROLOG_MIN ) ;

		ps.operator.mul( 2 ) ;
		ps.operator.add() ;
		ps.operator.gsave() ;
		ps.operator.setlinewidth() ;
		ps.operator.setlinecap( 2 ) ;
		ps.operator.setdash( 0 ) ;
		ps.operator.setgray( 1 ) ;
		ps.operator.stroke() ;
		ps.operator.grestore() ;

		ps.operator.gsave() ;
		ps.operator.stroke() ;
		ps.operator.grestore() ;
	}

	public void tailPS(AstrolabePostscriptStream ps) {
	}

	public List<double[]> list() {
		List<double[]> r = new java.util.Vector<double[]>() ;
		double d, d2, p[] ;
		Vector vp, va, vd2, vh ;
		double pa, pas, pac, mrot[] ;
		double phi, phis, phic ;
		double interval ;

		interval = Configuration.getValue(
				Configuration.getClassNode( this, null, null ),
				ApplicationConstant.PK_CIRCLE_INTERVAL, DEFAULT_INTERVAL ) ;

		d = AstrolabeFactory.valueOf( this ) ;
		p = AstrolabeFactory.valueOf( getPosition() ) ;

		vp = new Vector( projector.project( p[1], p[2] ) ) ;
		va = new Vector( projector.project( p[1]-d/2, p[2] ) ) ;
		vd2 = new Vector( va ).sub( vp ) ;
		d2 = vd2.abs() ;

		vh = new Vector( vp ) ;
		vh.scale( vd2.abs() ) ;
		vh.neg() ;

		pa = getPA() ;
		pas = Math.sin( pa ) ;
		pac = Math.cos( pa ) ;
		mrot = new double[] {
				pac,	pas,	0,
				-pas,	pac,	0,
				0,		0,		1 } ;
		vh.apply( mrot ) ;

		phi = java.lang.Math.atan2( vh.y, vh.x ) ;
		phis = java.lang.Math.sin( phi ) ;
		phic = java.lang.Math.cos( phi ) ;
		mrot[0] = phic ;
		mrot[1] = -phis ;
		mrot[3] = phis ;
		mrot[4] = phic ;

		vh.add( vp ) ;

		r.add( new double[] { vh.x, vh.y } ) ;
		for ( double i=interval ; i<360 ; i=i+interval ) {
			vh.set( d2*Math.cos( i ), d2*Math.sin( i )*getProportion() ) ;
			vh.apply( mrot ) ;
			vh.add( vp ) ;

			r.add( new double[] { vh.x, vh.y } ) ;
		}

		return r ;
	}
}
