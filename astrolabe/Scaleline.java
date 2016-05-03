
package astrolabe;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

@SuppressWarnings("serial")
public class Scaleline extends astrolabe.model.Scaleline implements PostscriptEmitter {

	// configuration key (CK_)
	private final static String CK_HALO				= "halo" ;
	private final static String CK_HALOMIN			= "halomin" ;
	private final static String CK_HALOMAX			= "halomax" ;

	private final static double DEFAULT_HALO		= 4 ;
	private final static double DEFAULT_HALOMIN		= .08 ;
	private final static double DEFAULT_HALOMAX		= .4 ;

	private final static String CK_SPACE			= "space" ;
	private final static String CK_LINELENGTH		= "linelength" ;
	private final static String CK_LINEWIDTH		= "linewidth" ;

	private final static double DEFAULT_SPACE		= .4 ;
	private final static double DEFAULT_LINELENGTH	= 2.8 ;
	private final static double DEFAULT_LINEWIDTH	= .01 ;

	private Vector pos ;
	private Vector nrm ;

	private Configuration conf ;

	private double gap ;
	private double len ;

	public Scaleline( Vector pos, Vector nrm ) {
		this.pos = new Vector( pos ) ;
		this.nrm = new Vector( nrm ) ;
	}

	public void headPS( ApplicationPostscriptStream ps ) {
		double linewidth ;

		linewidth = Configuration.getValue( this, CK_LINEWIDTH, DEFAULT_LINEWIDTH ) ;
		ps.push( linewidth ) ;
		ps.op( "setlinewidth" ) ;
	}

	public void emitPS( ApplicationPostscriptStream ps ) {
		ps.push( pos.x ) ;
		ps.push( pos.y ) ;
		ps.op( "translate" ) ;

		ps.push( nrm.y ) ;
		ps.push( nrm.x ) ;
		ps.op( "atan" ) ;
		ps.op( "rotate" ) ;

		ps.push( gap ) ;
		ps.push( 0 ) ;
		ps.op( "translate" ) ;

		ps.array( true ) ;
		ps.push( 0 ) ;
		ps.push( 0 ) ;
		ps.push( len ) ;
		ps.push( 0 ) ;
		ps.array( false ) ;

		ps.op( "newpath" ) ;
		ps.op( "gdraw" ) ;

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
		ps.push( 1 ) ;
		ps.op( "setgray" ) ;
		ps.op( "stroke" ) ;
		ps.op( "grestore" ) ;

		ps.op( "gsave" ) ;
		ps.op( "stroke" ) ;
		ps.op( "grestore" ) ;

		ps.push( -90 ) ;
		ps.op( "rotate" ) ;

		if ( getAnnotationStraight() != null ) {
			AnnotationStraight annotation ;

			for ( int i=0 ; i<getAnnotationStraightCount() ; i++ ) {
				ps.op( "gsave" ) ;

				annotation = new AnnotationStraight() ;
				getAnnotationStraight( i ).copyValues( annotation ) ;

				annotation.headPS( ps ) ;
				annotation.emitPS( ps ) ;
				annotation.tailPS( ps ) ;

				ps.op( "grestore" ) ;
			}
		}
	}

	public void tailPS( ApplicationPostscriptStream ps ) {
	}

	public Geometry list() {
		Vector a, b ;

		if ( conf == null )
			conf = new Configuration( this ) ;

		gap = conf.getValue( CK_SPACE, DEFAULT_SPACE ) ;
		len = conf.getValue( CK_LINELENGTH, DEFAULT_LINELENGTH ) ;

		a = new Vector( pos ) ;		
		a.add( new Vector( nrm ).scale( gap ) ) ;

		b = new Vector( a ) ;
		b.add( new Vector( nrm ).scale( len ) ) ;

		return new GeometryFactory().createLineString( new Coordinate[] { a, b } ) ;
	}
}
