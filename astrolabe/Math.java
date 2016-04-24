
package astrolabe;

import caa.CAACoordinateTransformation;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public final class Math {

	public static final double lim0 = .000000000001 ;

	public static final double rad1 = CAACoordinateTransformation.DegreesToRadians( 1 ) ;
	public static final double rad1h = CAACoordinateTransformation.DegreesToRadians( 15 ) ;

	public static final double rad90 = CAACoordinateTransformation.DegreesToRadians( 90 ) ;
	public static final double rad180 = CAACoordinateTransformation.DegreesToRadians( 180 ) ;
	public static final double rad270 = CAACoordinateTransformation.DegreesToRadians( 270 ) ;
	public static final double rad360 = CAACoordinateTransformation.DegreesToRadians( 360 ) ;

	// approximate value of golden section
	public static final double goldensection = ( 1+java.lang.Math.sqrt( 5 ) )/2 ;

	private Math() {        
	} 

	public static double lawOfSine( double a, double b, double al ) {
		double sina, sinb ;		// edges
		double sinal, sinbe ;	// angles
		double r ;

		// sin(a):sin(b) = sin(al):sin(be)
		// sin(be) = sin(b)*sin(al):sin(a)
		sina = java.lang.Math.sin( a ) ;
		sinb = java.lang.Math.sin( b ) ;
		sinal = java.lang.Math.sin( al ) ;

		sinbe = sinb*sinal/sina ;

		r = java.lang.Math.asin( truncate ( sinbe ) ) ;

		return r ;
	} 

	public static double lawOfSineSolveEdge( double a, double al, double be ) {
		double r ;

		// sin(a):sin(b) = sin(al):sin(be)
		// sin(b) = sin(be)*sin(a):sin(al) 
		r = lawOfSine( al, be, a ) ;

		return r ;
	}

	public static double lawOfEdgeCosine( double b, double c, double al ) {
		double sinb, sinc, cosa, cosb, cosc ;	// edges
		double cosal ;							// angle
		double r ;

		// cos(a) = cos(b)*cos(c)+sin(b)*sin(c)*cos(al)
		sinb = java.lang.Math.sin( b ) ;
		sinc = java.lang.Math.sin( c ) ;
		cosb = java.lang.Math.cos( b ) ;
		cosc = java.lang.Math.cos( c ) ;
		cosal = java.lang.Math.cos( al ) ;

		cosa = cosb*cosc+sinb*sinc*cosal ;

		r = java.lang.Math.acos( truncate ( cosa ) ) ;

		return r ;
	}

	public static double lawOfEdgeCosineSolveAngle( double a, double b, double c ) {
		double sinb, sinc, cosa, cosb, cosc ;	// edges
		double cosal ;							// angle
		double r ;

		// cos(al) = ( cos(a)-cos(b)*cos(c) ):( sin(b)*sin(c) )
		sinb = java.lang.Math.sin( b ) ;
		sinc = java.lang.Math.sin( c ) ;
		cosa = java.lang.Math.cos( a ) ;
		cosb = java.lang.Math.cos( b ) ;
		cosc = java.lang.Math.cos( c ) ;

		cosal = ( cosa-cosb*cosc )/( sinb*sinc ) ;

		r = java.lang.Math.acos( truncate ( cosal ) ) ;

		return r ;
	}

	public static double lawOfAngleCosine( double a, double be, double ga ) {
		double sinbe, singa, cosal, cosbe, cosga ;	// angles
		double cosa ;								// edge
		double r ;

		// cos(al) = -cos(be)*cos(ga)+sin(be)*sin(ga)*cos(a)
		sinbe = java.lang.Math.sin( be ) ;
		singa = java.lang.Math.sin( ga ) ;
		cosbe = java.lang.Math.cos( be ) ;
		cosga = java.lang.Math.cos( ga ) ;
		cosa = java.lang.Math.cos( a ) ;

		cosal = -cosbe*cosga+sinbe*singa*cosa ;

		r = java.lang.Math.acos( truncate ( cosal ) ) ;

		return r ;
	} 

	public static double lawOfAngleCosineSolveEdge( double al, double be, double ga ) {
		double sinbe, singa, cosal, cosbe, cosga ;	// angles
		double cosa ;								// edge
		double r ;

		// cos(a) = ( cos(al)+cos(be)*cos(ga) )/( sin(be)*sin(ga) )
		sinbe = java.lang.Math.sin( be ) ;
		singa = java.lang.Math.sin( ga ) ;
		cosal = java.lang.Math.cos( al ) ;
		cosbe = java.lang.Math.cos( be ) ;
		cosga = java.lang.Math.cos( ga ) ;

		cosa = ( cosal+cosbe*cosga )/( sinbe*singa ) ;

		r = java.lang.Math.acos( truncate ( cosa ) ) ;

		return r ;
	}

	public static double lawOfHalfAngles( double al, double be, double ga ) {
		double cosoal, cosobe, cosoga ;
		double cota2 ;
		double o, coso, k ;
		double r ;

		// cot(a/2) = sqrt( cos(o-al)*cos(o-be)*cos(o-ga)/-cos(o) ):cos(o-al), o = ( al+be+ga ):2
		o = ( al+be+ga )/2 ;
		cosoal = java.lang.Math.cos( o-al ) ;
		cosobe = java.lang.Math.cos( o-be ) ;
		cosoga = java.lang.Math.cos( o-ga ) ;
		coso = java.lang.Math.cos( o ) ;

		k = java.lang.Math.sqrt( cosoal*cosobe*cosoga/-coso ) ;

		cota2 = k/cosoal ;

		r = java.lang.Math.atan2( 1, cota2 )*2 ;

		return r ;
	}

	public static double lawOfHalfEdges( double a, double b, double c ) {        
		double sinsa, sinsb, sinsc ;
		double s, sins, k ;
		double r ;

		// tan(a/2) = sqrt( sin(s-a)*sin(s-b)*sin(s-c):sin(s) ):sin(s-a), s = ( a+b+c ):2
		s = ( a+b+c )/2 ;
		sinsa = java.lang.Math.sin( s-a ) ;
		sinsb = java.lang.Math.sin( s-b ) ;
		sinsc = java.lang.Math.sin( s-c ) ;
		sins = java.lang.Math.sin( s ) ;

		k = java.lang.Math.sqrt( sinsa*sinsb*sinsc/sins ) ;

		r = java.lang.Math.atan2( k, sinsa )*2 ;

		return r ;
	}

	public static double[] solveTriangleEEE( double a, double b, double c ) {
		double r[] = new double[3] ;	// r[ al, be, ga]

		r[0] = lawOfEdgeCosineSolveAngle( a, b, c ) ;
		r[1] = lawOfEdgeCosineSolveAngle( b, c, a ) ;
		r[2] = lawOfEdgeCosineSolveAngle( c, a, b ) ;

		return r ;
	}

	public static double[] solveTriangleAAA( double al, double be, double ga ) {
		double r[] = new double[3] ;	// r[ a, b, c]

		r[0] = lawOfAngleCosineSolveEdge( al, be, ga ) ;
		r[1] = lawOfAngleCosineSolveEdge( be, ga, al ) ;
		r[2] = lawOfAngleCosineSolveEdge( ga, al, be ) ;

		return r ;
	}

	public static double[] solveTriangleEAE( double a, double b, double ga ) {
		double r[] = new double[3] ;	// r[ c, al, be]

		r[0] = lawOfEdgeCosine( a, b, ga ) ;
		r[1] = lawOfEdgeCosineSolveAngle( a, b, r[0] ) ;
		r[2] = lawOfEdgeCosineSolveAngle( b, r[0], a ) ;

		return r ;
	}

	public static double[] solveTriangleAEA( double a, double be, double ga ) {
		double r[] = new double[3] ;	// r[ b, c, al]

		r[2] = lawOfAngleCosine( a, be, ga ) ;
		r[0] = lawOfAngleCosineSolveEdge( be, ga, r[2] ) ;
		r[1] = lawOfAngleCosineSolveEdge( ga, r[2], be ) ;

		return r ;
	}

	public static double[] solveTriangleEEA( double a, double b, double al, boolean ambiguous ) {
		double r[] = new double[3] ;	// r[ c, be, ga]
		double c, be, ga ;
		double tanapb2, cosalpbe2, cosalmbe2 ;
		double cotal2, cosbpc2, cosbmc2 ;

		be = lawOfSine( a, b, al ) ;
		be = ambiguous?rad180-be:be ;

		// tan(al:2-be:2)*sin(a:2+b:2)=cot(ga:2)*sin(a:2-b:2)
		// tan(al:2+be:2)*cos(a:2+b:2)=cot(ga:2)*cos(a:2-b:2)
		// tan(a:2-b:2)*sin(al:2+be:2)=tan(c:2)*sin(al:2-be:2)
		// tan(a:2+b:2)*cos(al:2+be:2)=tan(c:2)*cos(al:2-be:2)
		// tan(be:2-ga:2)*sin(b:2+c:2)=cot(al:2)*sin(b:2-c:2)
		// tan(be:2+ga:2)*cos(b:2+c:2)=cot(al:2)*cos(b:2-c:2)
		// tan(b:2-c:2)*sin(be:2+ga:2)=tan(a:2)*sin(be:2-ga:2)
		// tan(b:2+c:2)*cos(be:2+ga:2)=tan(a:2)*cos(be:2-ga:2)
		// tan(ga:2-al:2)*sin(c:2+a:2)=cot(be:2)*sin(c:2-a:2)
		// tan(ga:2+al:2)*cos(c:2+a:2)=cot(be:2)*cos(c:2-a:2)
		// tan(c:2-a:2)*sin(ga:2+al:2)=tan(b:2)*sin(ga:2-al:2)
		// tan(c:2+a:2)*cos(ga:2+al:2)=tan(b:2)*cos(ga:2-al:2)

		// tan(c:2)=tan(a:2+b:2)*cos(al:2+be:2):cos(al:2-be:2)
		tanapb2 = java.lang.Math.tan( a/2+b/2 ) ;
		cosalpbe2 = java.lang.Math.cos( al/2+be/2 ) ;
		cosalmbe2 = java.lang.Math.cos( al/2-be/2 ) ;

		c = java.lang.Math.atan2( tanapb2*cosalpbe2, cosalmbe2 )*2 ;

		// tan(be+ga):2=cot(al):2*cos(b-c):2:cos(b+c):2
		cotal2 = cot( al/2 ) ;
		cosbpc2 = java.lang.Math.cos( b/2+c/2 ) ;
		cosbmc2 = java.lang.Math.cos( b/2-c/2 ) ;

		ga = java.lang.Math.atan2( cotal2*cosbmc2, cosbpc2 )*2-be ;

		r[0] = c ; 
		r[1] = be ; 
		r[2] = ga ; 

		return r ;
	}

	public static double[] solveTriangleAAE( double a, double al, double be, boolean ambiguous ) {
		double r[] = new double[3] ;	// r[ b, c, ga]
		double b, c, ga ;
		double tanapb2, cosalpbe2, cosalmbe2 ;
		double cotal2, cosbpc2, cosbmc2 ;

		b = lawOfSineSolveEdge( a, al, be ) ;
		b = ambiguous?rad180-b:b ;

		// tan(c:2)=tan(a:2+b:2)*cos(al:2+be:2):cos(al:2-be:2)
		tanapb2 = java.lang.Math.tan( a/2+b/2 ) ;
		cosalpbe2 = java.lang.Math.cos( al/2+be/2 ) ;
		cosalmbe2 = java.lang.Math.cos( al/2-be/2 ) ;

		c = java.lang.Math.atan2( tanapb2*cosalpbe2, cosalmbe2 )*2 ;

		// tan(be+ga):2=cot(al):2*cos(b-c):2:cos(b+c):2
		cotal2 = cot( al/2 ) ;
		cosbpc2 = java.lang.Math.cos( b/2+c/2 ) ;
		cosbmc2 = java.lang.Math.cos( b/2-c/2 ) ;

		ga = java.lang.Math.atan2( cotal2*cosbmc2, cosbpc2 )*2-be ;

		r[0] = b ; 
		r[1] = c ; 
		r[2] = ga ; 

		return r ;
	}

	public static double truncate( double v ) {
		double r, d ;

		d = 1/Math.lim0 ;
		r = (long) ( v*d )/d ;

		return r ;
	}

	public static double cot( double al ) {
		return 1/java.lang.Math.tan( al ) ;
	}

	public static boolean isLim0( double v ) {
		return java.lang.Math.abs( v )<lim0?true:false ;
	}

	public static boolean isNaN( double v ) {
		return Double.doubleToLongBits( v )==Double.doubleToLongBits( Double.NaN )?true:false ;
	}

	public static boolean polygonCoversPoint( java.util.Vector<double[]> polygon, double[] p ) {
		double[] xy ;
		Coordinate[] cl ;
		LinearRing gl ;
		Polygon gpn ;
		Point gpt ;

		// convert polygon into geometry
		cl = new Coordinate[polygon.size()+1] ;
		for ( int n=polygon.size() ; n>0 ; n-- ) {
			xy = polygon.get( n-1 ) ;
			cl[n-1] = new Coordinate( xy[0], xy[1] ) ;
		}
		cl[polygon.size()] = cl[0] ;
		gl = new GeometryFactory().createLinearRing( cl ) ;
		gpn = new GeometryFactory().createPolygon( gl, null ) ;

		// convert point into geometry
		gpt = new GeometryFactory().createPoint( new Coordinate( p[0], p[1] ) ) ;

		return gpn.covers( gpt ) ;
	}
}
