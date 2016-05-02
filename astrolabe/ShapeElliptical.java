
package astrolabe;

import java.util.List;

@SuppressWarnings("serial")
public class ShapeElliptical extends astrolabe.model.ShapeElliptical implements PostscriptEmitter {

	private final static double DEFAULT_INTERVAL = 10 ;

	private Projector projector ;

	public ShapeElliptical( Projector projector ) {
		this.projector = projector ;
	}

	public void headPS(AstrolabePostscriptStream ps) {
	}

	public void emitPS(AstrolabePostscriptStream ps) {
		ps.array( true ) ;
		for ( double[] xy : list() ) {
			ps.push( xy[0] ) ;
			ps.push( xy[1] ) ;
		}
		ps.array( false ) ;

		ps.operator.newpath() ;
		ps.push( ApplicationConstant.PS_PROLOG_GDRAW ) ;

		ps.operator.closepath() ;

		// halo stroke
		ps.operator.currentlinewidth() ;

		ps.operator.dup() ;
		ps.operator.div( 100 ) ;
		ps.push( (Double) ( Registry.retrieve( ApplicationConstant.PK_CHART_HALO ) ) ) ; 
		ps.operator.mul() ;
		ps.push( (Double) ( Registry.retrieve( ApplicationConstant.PK_CHART_HALOMIN ) ) ) ; 
		ps.push( ApplicationConstant.PS_PROLOG_MAX ) ;
		ps.push( (Double) ( Registry.retrieve( ApplicationConstant.PK_CHART_HALOMAX ) ) ) ; 
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
		List<double[]> list ;
		double d, a, p[] ;
		Vector vp, vd, va ;
		double pa, pas, pac, mrot[] ;
		double interval ;

		interval = Configuration.getValue(
				Configuration.getClassNode( this, null, null ),
				ApplicationConstant.PK_SHAPE_INTERVAL, DEFAULT_INTERVAL ) ;

		list = new java.util.Vector<double[]>() ;

		d = AstrolabeFactory.valueOf( this ) ;
		p = AstrolabeFactory.valueOf( getPosition() ) ;

		vp = new Vector( projector.project( p[1], p[2] ) ) ;
		vd = new Vector( projector.project( p[1]-d, p[2] ) ) ;

		pa = getPA() ;
		pas = Math.sin( pa ) ;
		pac = Math.cos( pa ) ;
		mrot = new double[] {
				pac,	-pas,	0,
				pas,	pac,	0,
				0,		0,		1 } ;

		va = new Vector() ;
		a = new Vector( vd ).sub( vp ).abs()/2 ;

		for ( double i=0 ; i<360 ; i=i+interval ) {
			va.set( -a*Math.sin( i ), a*Math.cos( i )*getProportion() ) ;
			va.apply( mrot ) ;
			va.add( vp ) ;

			list.add( new double[] { va.x, va.y } ) ;
		}

		return list ;
	}
}
