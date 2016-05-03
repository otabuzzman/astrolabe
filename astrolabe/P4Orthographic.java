
package astrolabe;

import com.vividsolutions.jts.geom.Coordinate;

public class P4Orthographic implements P4Projector {

	//P4Const
	private double lam0 ;
	private double phi1 ;
	private double sinphi1 ;
	private double cosphi1 ;	
	private double R ;

	private final static int M_NORTH	= 0 ;
	private final static int M_SOUTH	= 1 ;
	private final static int M_EQUATOR	= 2 ;
	private final static int M_OBLIQUE	= 3 ;

	private int mode ;

	public P4Orthographic() {
		init( 0, 90, 1, 1 ) ;
	}

	public void init( double lam0, double phi1, double R, double k0 ) {
		this.lam0 = lam0 ;
		this.phi1 = phi1 ;
		sinphi1 = Math.sin( this.phi1 ) ;
		cosphi1 = Math.cos( this.phi1 ) ;
		this.R = R ;

		if ( phi1 == 90 )
			mode = M_NORTH ;
		else if ( phi1 == -90 )
			mode = M_SOUTH ;
		else if ( phi1 == 0 )
			mode = M_EQUATOR ;
		else
			mode = M_OBLIQUE ;
	}

	public Coordinate forward( Coordinate lamphi ) {
		Coordinate xy = new Coordinate() ;
		double sinlamdif, coslamdif, sinphi, cosphi ;

		sinlamdif = Math.sin( lamphi.x-lam0 ) ;
		coslamdif = Math.cos( lamphi.x-lam0 ) ;
		sinphi = Math.sin( lamphi.y ) ;
		cosphi = Math.cos( lamphi.y ) ;

		xy.x = R*cosphi*sinlamdif ;

		switch ( mode ) {
		case M_NORTH:
			xy.y = -R*cosphi*coslamdif ;

			break ;
		case M_SOUTH:
			xy.y = R*cosphi*coslamdif ;

			break ;
		case M_EQUATOR:
			xy.y = R*sinphi ;

			break ;
		case M_OBLIQUE:			
			xy.y = R*( cosphi1*sinphi-sinphi1*cosphi*coslamdif ) ;

			break ;
		}

		return xy ;
	}

	public Coordinate inverse( Coordinate xy ) {
		Coordinate lamphi = new Coordinate() ;
		double p, c, sinc, cosc ;

		p = java.lang.Math.pow( xy.x*xy.x+xy.y*xy.y, .5 ) ;
		c = Math.asin( p/R ) ;

		sinc = Math.sin( c ) ;
		cosc = Math.cos( c ) ;

		lamphi.y = Math.asin( cosc*sinphi1+( xy.y*sinc*cosphi1/p ) ) ;

		switch ( mode ) {
		case M_NORTH:
			lamphi.x = lam0+Math.atan2(xy.x, -xy.y ) ;

			break ;
		case M_SOUTH:
			lamphi.x = lam0+Math.atan2(xy.x, xy.y ) ;

			break ;
		case M_EQUATOR:
		case M_OBLIQUE:
			lamphi.x = lam0+Math.atan2( xy.x*sinc, p*cosphi1*cosc-xy.y*sinphi1*sinc ) ;

			break ;
		}

		return lamphi ;
	}
}
