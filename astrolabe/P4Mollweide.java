
package astrolabe;

import com.vividsolutions.jts.geom.Coordinate;

public class P4Mollweide implements P4Projector {

	//P4Const
	private double lam0 ;
	private double R ;

	private final static double V_CON = 1e-7 ;

	private final static double radperdeg = java.lang.Math.PI/180. ;
	private final static double degperrad = 180./java.lang.Math.PI ;

	public P4Mollweide() {
		init( 0, 0, 1, 1 ) ;
	}

	public void init(double lam0, double phi1, double R, double k0) {
		this.lam0 = lam0 ;
		this.R = R ;
	}

	public Coordinate forward( Coordinate lamphi ) {
		Coordinate xy = new Coordinate() ;
		double tht2 = lamphi.y, dtht2 = 0, sintht2, costht2 ;
		double sinphi, tht, sintht, costht ;

		sinphi = Math.sin( lamphi.y ) ;

		do {
			tht2 = tht2+dtht2 ;

			sintht2 = Math.sin( tht2 ) ;
			costht2 = Math.cos( tht2 ) ;

			dtht2 = -( tht2*radperdeg+sintht2-java.lang.Math.PI*sinphi )/( 1+costht2 )*degperrad ;
		} while ( java.lang.Math.abs( dtht2 )>V_CON ) ;

		tht = tht2*.5 ;
		sintht = Math.sin( tht ) ;
		costht = Math.cos( tht ) ;

		xy.x = ( java.lang.Math.pow( 8, .5 )/java.lang.Math.PI )*R*( lamphi.x-lam0 )*costht*radperdeg ;
		xy.y = java.lang.Math.pow( 2, .5 )*R*sintht ;

		return xy ;
	}

	public Coordinate inverse( Coordinate xy ) {
		Coordinate lamphi = new Coordinate() ;
		double tht, sin2tht, costht ;

		tht = Math.asin( xy.y/( java.lang.Math.pow( 2, .5 )*R ) ) ;

		sin2tht = Math.sin( 2*tht ) ;
		lamphi.y = Math.asin( ( 2*tht*radperdeg+sin2tht )/java.lang.Math.PI ) ;

		if ( java.lang.Math.abs( lamphi.y ) == 90 )
			lamphi.x = lam0 ;
		else {
			costht = Math.cos( tht ) ;
			lamphi.x = lam0+( java.lang.Math.PI*xy.x/( java.lang.Math.pow( 8, .5 )*R*costht ) )*degperrad ;
		}

		return lamphi ;
	}
}
