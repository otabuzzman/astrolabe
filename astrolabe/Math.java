
package astrolabe;

public final class Math {

	private Math() {        
	} 

	public static double LawOfAngleSine( double b, double alpha, double beta ) {        
		return java.lang.Math.asin( java.lang.Math.sin( b )/java.lang.Math.sin( beta )*java.lang.Math.sin( alpha ) ) ;
	} 

	public static double LawOfAngleCosine( double beta, double gamma, double a ) {        
		return java.lang.Math.acos( -java.lang.Math.cos( beta )*java.lang.Math.cos( gamma )+java.lang.Math.sin( beta )*java.lang.Math.sin( gamma )*java.lang.Math.cos( a ) ) ;
	} 

	public static double LawOfAngleCosineForA( double alpha, double beta, double gamma ) {        
		return java.lang.Math.acos( ( java.lang.Math.cos( alpha )+java.lang.Math.cos( beta )*java.lang.Math.cos( gamma ) )/( java.lang.Math.sin( beta )*java.lang.Math.sin( gamma ) ) ) ;
	} 

	public static double LawOfAngleNepper( double alpha, double beta, double a ) {        
		double b = LawOfAngleSine( a, beta, alpha ) ;
		double q = java.lang.Math.cos( alpha )*java.lang.Math.tan( b ) ;
		double p = java.lang.Math.cos( beta )*java.lang.Math.tan( a ) ;
		return java.lang.Math.asin( LawOfEdgeSine( alpha, java.lang.Math.atan( q )+java.lang.Math.atan( p ), a ) ) ;
	} 

	public static double LawOfEdgeSine( double beta, double a, double b ) {        
		return java.lang.Math.asin( java.lang.Math.sin( beta )/java.lang.Math.sin( b )*java.lang.Math.sin( a ) ) ;
	} 

	public static double LawOfEdgeCosine( double b, double c, double alpha ) {        
		return java.lang.Math.acos( java.lang.Math.cos( b )*java.lang.Math.cos( c )+java.lang.Math.sin( b )*java.lang.Math.sin( c )*java.lang.Math.cos( alpha ) ) ;
	} 

	public static double LawOfEdgeCosineForAlpha( double a, double b, double c ) {        
		return java.lang.Math.acos( ( java.lang.Math.cos( a )-java.lang.Math.cos( b )*java.lang.Math.cos( c ) )/( java.lang.Math.sin( b )*java.lang.Math.sin( c ) ) ) ;
	} 

	public static double LawOfEdgeNepper( double a, double b, double alpha ) {        
		double beta = LawOfEdgeSine( alpha, b, a ) ;
		double q = java.lang.Math.cos( alpha )*java.lang.Math.tan( b ) ;
		double p = java.lang.Math.cos( beta )*java.lang.Math.tan( a ) ;
		return java.lang.Math.atan( q )+java.lang.Math.atan( p ) ;
	} 

	public static double LawOfHalfAngles( double alpha, double beta, double gamma ) {        
		double o = ( alpha+beta+gamma )/2 ;
		double k = java.lang.Math.sqrt( java.lang.Math.cos( o-alpha )*java.lang.Math.cos( o-beta )*java.lang.Math.cos( o-gamma )/-java.lang.Math.cos( o ) ) ;
		return java.lang.Math.atan2( 1, k/java.lang.Math.cos( o-alpha ) )*2 ;
	} 

	public static double LawOfHalfEdges( double a, double b, double c ) {        
		double s = ( a+b+c )/2 ;
		double k = java.lang.Math.sqrt( java.lang.Math.sin( s-a )*java.lang.Math.sin( s-b )*java.lang.Math.sin( s-c )/java.lang.Math.sin( s ) ) ;
		return java.lang.Math.atan2( k, java.lang.Math.sin( s-a ) )*2 ;
	} 

	public static double MethodOfAuxAngle( double a, double b, double alpha ) {
		double q = java.lang.Math.atan( java.lang.Math.cos( alpha )/cot( b ) ) ;
		return java.lang.Math.acos( java.lang.Math.cos( a )*java.lang.Math.cos( q )/java.lang.Math.cos( b ) )+q ;
	}

	public static double remainder( double a, double b ) {
		return a-(int) (a/b)*b ;
	}

	public static double cot( double alpha ) {
		return 1/java.lang.Math.tan( alpha ) ;
	}
}
