
package astrolabe;

import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;

@SuppressWarnings("serial")
public class ShapeElliptical extends astrolabe.model.ShapeElliptical implements PostscriptEmitter {

	// configuration key (CK_)
	private final static String CK_INTERVAL			= "interval" ;
	private static final String CK_DISTANCE			= "distance" ;

	private final static String CK_HALO				= "halo" ;
	private final static String CK_HALOMIN			= "halomin" ;
	private final static String CK_HALOMAX			= "halomax" ;

	private final static double DEFAULT_INTERVAL	= 10 ;
	private static final double DEFAULT_DISTANCE	= 0 ;

	private final static double DEFAULT_HALO		= 4 ;
	private final static double DEFAULT_HALOMIN		= .08 ;
	private final static double DEFAULT_HALOMAX		= .4 ;

	private Converter converter ;
	private Projector projector ;

	public ShapeElliptical( Converter converter, Projector projector ) {
		this.converter = converter ;
		this.projector = projector ;
	}

	public void headPS( ApplicationPostscriptStream ps ) {
	}

	public void emitPS( ApplicationPostscriptStream ps ) {
		Configuration conf ;

		ps.array( true ) ;
		for ( Coordinate xy : list() ) {
			ps.push( xy.x ) ;
			ps.push( xy.y ) ;
		}
		ps.array( false ) ;

		ps.op( "newpath" ) ;
		ps.op( "gdraw" ) ;

		ps.op( "closepath" ) ;

		// halo stroke
		ps.op( "currentlinewidth" ) ;

		ps.op( "dup" ) ;
		ps.push( 100 ) ;
		ps.op( "div" ) ;
		conf = new Configuration( this ) ;
		ps.push( conf.getValue( CK_HALO, DEFAULT_HALO ) ) ; 
		ps.op( "mul" ) ;
		ps.push( conf.getValue( CK_HALOMIN, DEFAULT_HALOMIN ) ) ; 
		ps.op( "max" ) ;
		ps.push( conf.getValue( CK_HALOMAX, DEFAULT_HALOMAX ) ) ; 
		ps.op( "min" ) ;

		ps.push( 2 ) ;
		ps.op( "mul" ) ;
		ps.op( "add" ) ;
		ps.op( "gsave" ) ;
		ps.op( "setlinewidth" ) ;
		ps.push( 2 ) ;
		ps.op( "setlinecap" ) ;
		ps.array( true ) ;
		ps.array( false ) ;
		ps.push( 0 ) ;
		ps.op( "setdash" ) ;
		ps.push( 1 ) ;
		ps.op( "setgray" ) ;
		ps.op( "stroke" ) ;
		ps.op( "grestore" ) ;

		ps.op( "gsave" ) ;
		ps.op( "stroke" ) ;
		ps.op( "grestore" ) ;
	}

	public void tailPS( ApplicationPostscriptStream ps ) {
	}

	public Coordinate[] list() {
		List<Coordinate> list ;
		double interval, distance, d, a ;
		double pa, pas, pac, mrot[] ;
		Coordinate p ;
		Vector vp, vd, va ;

		interval = Configuration.getValue( this, CK_INTERVAL, DEFAULT_INTERVAL ) ;

		list = new java.util.Vector<Coordinate>() ;

		d = valueOf( this ) ;
		p = valueOf( getPosition() ) ;

		vp = new Vector( projector.project( converter.convert( p, false ), false ) ) ;

		p.x = p.x-d ;
		vd = new Vector( projector.project( converter.convert( p, false ), false ) ) ;

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
			va.x = -a*Math.sin( i ) ;
			va.y = a*Math.cos( i )*getProportion() ;
			va.apply( mrot )
			.add( vp ) ;

			list.add( new Coordinate( va.x, va.y ) ) ;
		}
		list.add( list.get( 0 ) ) ;

		distance = Configuration.getValue( this, CK_DISTANCE, DEFAULT_DISTANCE ) ;
		if ( distance>0 )
			return DouglasPeuckerSimplifier.simplify( new GeometryFactory().createLineString( list.toArray( new Coordinate[0] ) ), distance ).getCoordinates() ;
		return list.toArray( new Coordinate[0] ) ;
	}
}
