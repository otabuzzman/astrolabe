
package astrolabe;

import caa.CAACoordinateTransformation;

public final class Math {

	public final static double lim0 = .000000000001 ;

	// approximate value of golden section
	public final static double goldensection = ( 1+java.lang.Math.sqrt( 5 ) )/2 ;

	private Math() {        
	} 

	public static double lawOfSine( double a, double b, double al ) {
		double sina, sinb ;		// edges
		double sinal, sinbe ;	// angles
		double r ;

		// sin(a):sin(b) = sin(al):sin(be)
		// sin(be) = sin(b)*sin(al):sin(a)
		sina = sin( a ) ;
		sinb = sin( b ) ;
		sinal = sin( al ) ;

		sinbe = sinb*sinal/sina ;

		r = asin( truncate ( sinbe ) ) ;

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
		sinb = sin( b ) ;
		sinc = sin( c ) ;
		cosb = cos( b ) ;
		cosc = cos( c ) ;
		cosal = cos( al ) ;

		cosa = cosb*cosc+sinb*sinc*cosal ;

		r = acos( truncate ( cosa ) ) ;

		return r ;
	}

	public static double lawOfEdgeCosineSolveAngle( double a, double b, double c ) {
		double sinb, sinc, cosa, cosb, cosc ;	// edges
		double cosal ;							// angle
		double r ;

		// cos(al) = ( cos(a)-cos(b)*cos(c) ):( sin(b)*sin(c) )
		sinb = sin( b ) ;
		sinc = sin( c ) ;
		cosa = cos( a ) ;
		cosb = cos( b ) ;
		cosc = cos( c ) ;

		cosal = ( cosa-cosb*cosc )/( sinb*sinc ) ;

		r = acos( truncate ( cosal ) ) ;

		return r ;
	}

	public static double lawOfAngleCosine( double a, double be, double ga ) {
		double sinbe, singa, cosal, cosbe, cosga ;	// angles
		double cosa ;								// edge
		double r ;

		// cos(al) = -cos(be)*cos(ga)+sin(be)*sin(ga)*cos(a)
		sinbe = sin( be ) ;
		singa = sin( ga ) ;
		cosbe = cos( be ) ;
		cosga = cos( ga ) ;
		cosa = cos( a ) ;

		cosal = -cosbe*cosga+sinbe*singa*cosa ;

		r = acos( truncate ( cosal ) ) ;

		return r ;
	} 

	public static double lawOfAngleCosineSolveEdge( double al, double be, double ga ) {
		double sinbe, singa, cosal, cosbe, cosga ;	// angles
		double cosa ;								// edge
		double r ;

		// cos(a) = ( cos(al)+cos(be)*cos(ga) )/( sin(be)*sin(ga) )
		sinbe = sin( be ) ;
		singa = sin( ga ) ;
		cosal = cos( al ) ;
		cosbe = cos( be ) ;
		cosga = cos( ga ) ;

		cosa = ( cosal+cosbe*cosga )/( sinbe*singa ) ;

		r = acos( truncate ( cosa ) ) ;

		return r ;
	}

	public static double lawOfHalfAngles( double al, double be, double ga ) {
		double cosoal, cosobe, cosoga ;
		double cota2 ;
		double o, coso, k ;
		double r ;

		// cot(a/2) = sqrt( cos(o-al)*cos(o-be)*cos(o-ga)/-cos(o) ):cos(o-al), o = ( al+be+ga ):2
		o = ( al+be+ga )/2 ;
		cosoal = cos( o-al ) ;
		cosobe = cos( o-be ) ;
		cosoga = cos( o-ga ) ;
		coso = cos( o ) ;

		k = java.lang.Math.sqrt( cosoal*cosobe*cosoga/-coso ) ;

		cota2 = k/cosoal ;

		r = atan2( 1, cota2 )*2 ;

		return r ;
	}

	public static double lawOfHalfEdges( double a, double b, double c ) {        
		double sinsa, sinsb, sinsc ;
		double s, sins, k ;
		double r ;

		// tan(a/2) = sqrt( sin(s-a)*sin(s-b)*sin(s-c):sin(s) ):sin(s-a), s = ( a+b+c ):2
		s = ( a+b+c )/2 ;
		sinsa = sin( s-a ) ;
		sinsb = sin( s-b ) ;
		sinsc = sin( s-c ) ;
		sins = sin( s ) ;

		k = java.lang.Math.sqrt( sinsa*sinsb*sinsc/sins ) ;

		r = atan2( k, sinsa )*2 ;

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
		be = ambiguous?180-be:be ;

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
		tanapb2 = tan( a/2+b/2 ) ;
		cosalpbe2 = cos( al/2+be/2 ) ;
		cosalmbe2 = cos( al/2-be/2 ) ;

		c = Math.atan2( tanapb2*cosalpbe2, cosalmbe2 )*2 ;

		// tan(be+ga):2=cot(al):2*cos(b-c):2:cos(b+c):2
		cotal2 = cot( al/2 ) ;
		cosbpc2 = cos( b/2+c/2 ) ;
		cosbmc2 = cos( b/2-c/2 ) ;

		ga = atan2( cotal2*cosbmc2, cosbpc2 )*2-be ;

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
		b = ambiguous?180-b:b ;

		// tan(c:2)=tan(a:2+b:2)*cos(al:2+be:2):cos(al:2-be:2)
		tanapb2 = tan( a/2+b/2 ) ;
		cosalpbe2 = cos( al/2+be/2 ) ;
		cosalmbe2 = cos( al/2-be/2 ) ;

		c = Math.atan2( tanapb2*cosalpbe2, cosalmbe2 )*2 ;

		// tan(be+ga):2=cot(al):2*cos(b-c):2:cos(b+c):2
		cotal2 = cot( al/2 ) ;
		cosbpc2 = cos( b/2+c/2 ) ;
		cosbmc2 = cos( b/2-c/2 ) ;

		ga = Math.atan2( cotal2*cosbmc2, cosbpc2 )*2-be ;

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
		return 1/tan( al ) ;
	}

	public static boolean isLim0( double v ) {
		return java.lang.Math.abs( v )<lim0?true:false ;
	}

	public static boolean isNaN( double v ) {
		return Double.doubleToLongBits( v )==Double.doubleToLongBits( Double.NaN )?true:false ;
	}

	// degree versions of java.lang.Math trigonometric methods
	public static double sin( double a ) {
		return java.lang.Math.sin( CAACoordinateTransformation.DegreesToRadians( a ) ) ;
	}

	public static double cos( double a ) {
		return java.lang.Math.cos( CAACoordinateTransformation.DegreesToRadians( a ) ) ;
	}

	public static double tan( double a ) {
		return java.lang.Math.tan( CAACoordinateTransformation.DegreesToRadians( a ) ) ;
	}

	public static double asin( double a ) {
		return CAACoordinateTransformation.RadiansToDegrees( java.lang.Math.asin( a ) ) ;
	}

	public static double acos( double a ) {
		return CAACoordinateTransformation.RadiansToDegrees( java.lang.Math.acos( a ) ) ;
	}

	public static double atan( double a ) {
		return CAACoordinateTransformation.RadiansToDegrees( java.lang.Math.atan( a ) ) ;
	}

	public static double atan2( double y, double x ) {
		return CAACoordinateTransformation.RadiansToDegrees( java.lang.Math.atan2( y, x ) ) ;
	}
}
