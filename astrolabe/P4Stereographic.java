
package astrolabe;

import com.vividsolutions.jts.geom.Coordinate;

public class P4Stereographic implements P4Projector {

	//P4Const
	private double lam0 ;
	private double phi1 ;
	private double sinphi1 ;
	private double cosphi1 ;	
	private double R ;
	private double k0 ;

	private final static int M_NORTH	= 0 ;
	private final static int M_SOUTH	= 1 ;
	private final static int M_EQUATOR	= 2 ;
	private final static int M_OBLIQUE	= 3 ;

	private int mode ;

	public P4Stereographic() {
		init( 0, 90, 1, 1 ) ;
	}

	public void init( double lam0, double phi1, double R, double k0 ) {
		this.lam0 = lam0 ;
		this.phi1 = phi1 ;
		sinphi1 = Math.sin( this.phi1 ) ;
		cosphi1 = Math.cos( this.phi1 ) ;
		this.R = R ;
		this.k0 = k0 ;

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
		double sinlamdif, coslamdif, sinphi, cosphi, k, t ;

		sinlamdif = Math.sin( lamphi.x-lam0 ) ;
		coslamdif = Math.cos( lamphi.x-lam0 ) ;
		sinphi = Math.sin( lamphi.y ) ;
		cosphi = Math.cos( lamphi.y ) ;

		switch ( mode ) {
		case M_NORTH:
			t = Math.tan( 45-lamphi.y/2 ) ;
			xy.x = 2*R*k0*t*sinlamdif ;
			xy.y = -2*R*k0*t*coslamdif ;

			break ;
		case M_SOUTH:
			t = Math.tan( 45+lamphi.y/2 ) ;
			xy.x = 2*R*k0*t*sinlamdif ;
			xy.y = 2*R*k0*t*coslamdif ;

			break ;
		case M_EQUATOR:
			k = 2*k0/( 1+cosphi*coslamdif ) ;
			xy.x = R*k*cosphi*sinlamdif ;
			xy.y = R*k*sinphi ;

			break ;
		case M_OBLIQUE:			
			k = 2*k0/( 1+sinphi1*sinphi+cosphi1*cosphi*coslamdif ) ;
			xy.x = R*k*cosphi*sinlamdif ;
			xy.y = R*k*( cosphi1*sinphi-sinphi1*cosphi*coslamdif ) ;

			break ;
		}

		return xy ;
	}

	public Coordinate inverse( Coordinate xy ) {
		Coordinate lamphi = new Coordinate() ;
		double p, c, sinc, cosc ;

		p = java.lang.Math.pow( xy.x*xy.x+xy.y*xy.y, .5 ) ;
		c = 2*Math.atan2( p, 2*R*k0 ) ;

		sinc = Math.sin( c ) ;
		cosc = Math.cos( c ) ;

		lamphi.y = Math.asin( cosc*sinphi1+( xy.y*sinc*cosphi1/p ) ) ;

		switch ( mode ) {
		case M_NORTH:
			lamphi.x = lam0+Math.atan2( xy.x, -xy.y ) ;

			break ;
		case M_SOUTH:
			lamphi.x = lam0+Math.atan2( xy.x, xy.y ) ;

			break ;
		case M_EQUATOR:
		case M_OBLIQUE:			
			lamphi.x = lam0+Math.atan2( xy.x*sinc, p*cosphi1*cosc-xy.y*sinphi1*sinc ) ;

			break ;
		}

		return lamphi ;
	}
}
