
package astrolabe;

import java.util.Hashtable;

import com.vividsolutions.jts.geom.Coordinate;

@SuppressWarnings("serial")
public class ChartPseudoCylindrical extends ChartType {

	// configuration key (CK_)
	public final static String CK_DEFOBLIQUELON			= "defobliquelon" ;
	public final static String CK_DEFOBLIQUELAT			= "defobliquelat" ;

	public final static double DEFAULT_DEFOBLIQUELON	= 0 ;
	public final static double DEFAULT_DEFOBLIQUELAT	= 0 ;

	// attribute value (AV_)
	private final static String AV_MOLLWEIDE = "mollweide" ;

	private final static Hashtable<String, P4Projector> projection = new Hashtable<String, P4Projector>() ;

	private P4Projector projector ;

	public ChartPseudoCylindrical( astrolabe.model.ChartPseudoCylindrical peer ) {
		double lam0, phi1, R ;

		peer.copyValues( this ) ;

		if ( getOblique() == null ) {
			lam0 = Configuration.getValue( this, CK_DEFOBLIQUELON, DEFAULT_DEFOBLIQUELON ) ;
			phi1 = Configuration.getValue( this, CK_DEFOBLIQUELAT, DEFAULT_DEFOBLIQUELAT ) ;
		} else {
			lam0 = valueOf( getOblique().getLon() ) ;
			phi1 = valueOf( getOblique().getLat() ) ;
		}
		R = scale() ;

		projector = projection.get( peer.getProjection() ) ;
		projector.init( lam0, phi1, R, 1 ) ;
	}

	public Coordinate project( Coordinate coordinate, boolean inverse ) {
		Coordinate c = new Coordinate( coordinate ) ;

		if ( inverse )
			return projector.inverse( c ) ;
		else {
			while ( c.x>180 )
				c.x = c.x-360 ;

			return projector.forward( c ) ;
		}
	}

	static {
		projection.put( AV_MOLLWEIDE, new P4Mollweide() ) ;
	}
}
