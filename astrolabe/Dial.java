
package astrolabe;

import caa.CAACoordinateTransformation;

public class Dial extends Model {

	private double rise ;

	private double space ;
	private double thickness ;
	private double linewidth ;

	private double span ;
	private int division ;

	private boolean reflect ;

	private boolean hasBaseNone ;
	private boolean hasBaseLine ;
	private boolean hasBaseRail ;

	private astrolabe.model.DialType dlT ;
	private Circle circle ;
	private Quantity quantity ;

	public Dial( astrolabe.model.DialType dlT, Circle circle, Quantity quantity ) {
		String baseline ;

		this.dlT = dlT ;
		this.circle = circle ;
		this.quantity = quantity ;

		span = dlT.getGraduationSpan().getSpan() ;
		quantity.setSpan( span ) ;

		reflect = dlT.getReflect() ;
		baseline = dlT.getBaseline() ;

		hasBaseNone = false ;
		hasBaseLine = false ;
		hasBaseRail = false ;

		if ( baseline.equals( "none") ) {		
			space = getClassNode( null, "baseline/none" ).getDouble( "space", .1 ) ;
			thickness = 0 ;

			hasBaseNone = true ;
		} else if ( baseline.equals( "line" ) ) {
			space = getClassNode( null, "baseline/line" ).getDouble( "space", 1 ) ;
			thickness = getClassNode( null, "baseline/line" ).getDouble( "thickness", .2 ) ;

			hasBaseLine = true ;
		} else if ( baseline.equals( "rail" ) ) {
			division = dlT.getGraduationSpan().getDivision() ;

			space = getClassNode( null, "baseline/rail" ).getDouble( "space", 1 ) ;
			thickness = getClassNode( null, "baseline/rail" ).getDouble( "thickness", 1.2 ) ;
			linewidth = getClassNode( null, "baseline/rail" ).getDouble( "linewidth", .01 ) ;

			hasBaseRail = true ;
		}

		rise = getClassNode( null, "annotation" ).getDouble( "rise", 3.2 ) ;
	}

	public void emitPS(PostscriptStream ps) throws ParameterNotValidException {
		java.util.Vector<astrolabe.Vector> vV ;
		java.util.Vector<double[]> vD ;

		if ( hasBaseNone ) {
			emitPSBaseNone( ps ) ;
		} else if ( hasBaseLine ) {
			ps.operator.setlinewidth( thickness ) ;
			emitPSBaseLine( ps ) ;
		} else if ( hasBaseRail ) {
			ps.operator.setlinewidth( linewidth ) ;
			emitPSBaseRail( ps ) ;
		}

		vV = circle.cartesianList( reflect?-( ( space+thickness )+rise ):( space+thickness )+rise ) ;
		vD = CircleHelper.convertCartesianVectorToDouble( vV ) ;
		ps.polyline( vD ) ;
	}

	private void emitPSBaseNone( PostscriptStream ps ) throws ParameterNotValidException {
		graduation( ps ) ;
	}

	private void emitPSBaseLine( PostscriptStream ps ) throws ParameterNotValidException {
		java.util.Vector<astrolabe.Vector> vV ;
		java.util.Vector<double[]> vD ;
		int ns = 0 ;

		vV = new java.util.Vector<astrolabe.Vector>() ;

		try { // baseline
			for ( ; ; ns++ ) {
				vV.addAll( circle.cartesianList( quantity.spanNDistance( ns ),
						quantity.spanNDistance( ns+1 ), reflect?-space:space ) ) ;
			}
		} catch ( ParameterNotValidException e ) {
			vD = CircleHelper.convertCartesianVectorToDouble( vV ) ;
			ps.polyline( vD ) ;
			ps.operator.stroke() ;
		}

		graduation( ps ) ;
	}

	private void emitPSBaseRail( PostscriptStream ps ) throws ParameterNotValidException {
		double s ;
		int nss = 0 ;
		java.util.Vector<astrolabe.Vector> vVFw, vVRv ;
		java.util.Vector<double[]> vDFw = null ;
		Quantity q ;

		q = (Quantity) quantity.clone() ;
		q.setSpan( span/division ) ;

		try { // baseline
			for ( ; ; nss++ ) {
				s = nss%2==0?space:space+linewidth/2 ;
				s = reflect?-s:s ;
				vVFw = circle.cartesianList( q.spanNDistance( nss ), q.spanNDistance( nss+1 ), s ) ;

				s = space+( nss%2==0?thickness:thickness-linewidth/2 ) ;
				s = reflect?-s:s ;
				vVRv = circle.cartesianList( q.spanNDistance( nss ), q.spanNDistance( nss+1 ), s ) ;

				vVRv = CircleHelper.reverseVector( vVRv ) ;
				vVFw.addAll( vVRv ) ;

				vDFw = CircleHelper.convertCartesianVectorToDouble( vVFw ) ;

				if ( nss%2 == 0 ) { // subunit filled
					ps.polyline( vDFw ) ;
					ps.operator.closepath() ;
					ps.operator.fill() ;
				} else { // subunit unfilled
					ps.polyline( new java.util.Vector<double[]>( vDFw.subList( 0, vDFw.size()/2 ) ) ) ;
					ps.operator.stroke() ;
					ps.polyline( new java.util.Vector<double[]>( vDFw.subList( vDFw.size()/2, vDFw.size() ) ) ) ;
					ps.operator.stroke() ;
				}
			}
		} catch ( ParameterNotValidException e ) {
			if ( nss%2 == 0 ) { // close unfilled subunit
				ps.line( new java.util.Vector<double[]>( vDFw.subList( vDFw.size()/2-1, vDFw.size()/2+1 ) ) ) ;
				ps.operator.stroke() ;
			}
		}

		graduation( ps ) ;
	}

	private void graduation( PostscriptStream ps ) {
		double d ;
		double rad90 ;
		Vector origin, tangent ;
		Graduation g ;
		astrolabe.model.AnnotationStraight[] a ;

		rad90 = CAACoordinateTransformation.DegreesToRadians( 90 ) ;

		try { // graduation
			for ( int ns=1 ; ; ns++ ) {
				d = quantity.spanNDistance( ns ) ;

				origin = circle.cartesian( d, reflect?-( space+thickness ):space+thickness ) ;
				tangent = circle.tangentVector( d ).rotate( reflect?-rad90:rad90 ) ;

				a = dlT.getGraduationSpan().getAnnotationStraight() ;
				g = new GraduationSpan( origin, tangent ) ;

				try { // half
					if ( quantity.isSpanModN( dlT.getGraduationHalf().getSpan(), ns ) ) {
						a = dlT.getGraduationHalf().getAnnotationStraight() ;
						g = new GraduationHalf( origin, tangent ) ;
					}
				} catch ( NullPointerException e ) {}

				try { // full
					if ( quantity.isSpanModN( dlT.getGraduationFull().getSpan(), ns ) ) {
						a = dlT.getGraduationFull().getAnnotationStraight() ;
						g = new GraduationFull( origin, tangent ) ;
					}
				} catch ( NullPointerException e ) {}

				ps.operator.gsave() ;

				g.emitPS( ps ) ;

				quantity.register( "dial", ns ) ;
				AnnotationHelper.emitPS( ps, a ) ;

				ps.operator.grestore() ;
			}
		} catch ( ParameterNotValidException e ) {}
	}
}
