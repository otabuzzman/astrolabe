
package astrolabe;

import java.util.List;

@SuppressWarnings("serial")
public class ShapeElliptical extends astrolabe.model.ShapeElliptical implements PostscriptEmitter {

	// configuration key (CK_)
	private final static String CK_INTERVAL			= "interval" ;

	private final static String CK_HALO				= "halo" ;
	private final static String CK_HALOMIN			= "halomin" ;
	private final static String CK_HALOMAX			= "halomax" ;


	private final static double DEFAULT_INTERVAL	= 10 ;

	private final static double DEFAULT_HALO		= 4 ;
	private final static double DEFAULT_HALOMIN		= .08 ;
	private final static double DEFAULT_HALOMAX		= .4 ;

	private Projector projector ;

	public ShapeElliptical( Projector projector ) {
		this.projector = projector ;
	}

	public void headPS(ApplicationPostscriptStream ps) {
	}

	public void emitPS(ApplicationPostscriptStream ps) {
		Configuration conf ;

		ps.array( true ) ;
		for ( double[] xy : list() ) {
			ps.push( xy[0] ) ;
			ps.push( xy[1] ) ;
		}
		ps.array( false ) ;

		ps.operator.newpath() ;
		ps.gdraw() ;

		ps.operator.closepath() ;

		// halo stroke
		ps.operator.currentlinewidth() ;

		ps.operator.dup() ;
		ps.operator.div( 100 ) ;
		conf = new Configuration( this ) ;
		ps.push( conf.getValue( CK_HALO, DEFAULT_HALO ) ) ; 
		ps.operator.mul() ;
		ps.push( conf.getValue( CK_HALOMIN, DEFAULT_HALOMIN ) ) ; 
		ps.max() ;
		ps.push( conf.getValue( CK_HALOMAX, DEFAULT_HALOMAX ) ) ; 
		ps.min() ;

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

	public void tailPS(ApplicationPostscriptStream ps) {
	}

	public List<double[]> list() {
		List<double[]> list ;
		double d, a, p[] ;
		Vector vp, vd, va ;
		double pa, pas, pac, mrot[] ;
		double interval ;

		interval = Configuration.getValue( this, CK_INTERVAL, DEFAULT_INTERVAL ) ;

		list = new java.util.Vector<double[]>() ;

		d = valueOf( this ) ;
		p = valueOf( getPosition() ) ;

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
