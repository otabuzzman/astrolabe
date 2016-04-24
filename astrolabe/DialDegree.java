
package astrolabe;

import caa.CAACoordinateTransformation;

public class DialDegree implements Dial {

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

	private Span quantity ;

	public DialDegree( astrolabe.model.DialType dlT, Circle circle ) {
		String baseline, node ;

		this.dlT = dlT ;
		this.circle = circle ;

		quantity = new SpanDegree( circle ) ;

		span = dlT.getGraduationSpan().getSpan() ;

		reflect = dlT.getReflect() ;
		baseline = dlT.getBaseline() ;

		hasBaseNone = false ;
		hasBaseLine = false ;
		hasBaseRail = false ;

		if ( baseline.equals( ApplicationConstant.AV_DIAL_NONE) ) {
			node = ApplicationConstant.PN_DIAL_BASELINE+"/"+ApplicationConstant.AV_DIAL_NONE ;
			space = ApplicationHelper.getClassNode( this, dlT.getName(), node ).getDouble( ApplicationConstant.PK_DIAL_SPACE, .1 ) ;
			thickness = 0 ;

			hasBaseNone = true ;
		} else if ( baseline.equals( ApplicationConstant.AV_DIAL_LINE ) ) {
			node = ApplicationConstant.PN_DIAL_BASELINE+"/"+ApplicationConstant.AV_DIAL_LINE ;
			space = ApplicationHelper.getClassNode( this, dlT.getName(), node ).getDouble( ApplicationConstant.PK_DIAL_SPACE, 1 ) ;
			thickness = ApplicationHelper.getClassNode( this, dlT.getName(), node ).getDouble( ApplicationConstant.PK_DIAL_THICKNESS, .2 ) ;

			hasBaseLine = true ;
		} else if ( baseline.equals( ApplicationConstant.AV_DIAL_RAIL ) ) {
			division = dlT.getGraduationSpan().getDivision() ;

			node = ApplicationConstant.PN_DIAL_BASELINE+"/"+ApplicationConstant.AV_DIAL_RAIL ;
			space = ApplicationHelper.getClassNode( this, dlT.getName(), node ).getDouble( ApplicationConstant.PK_DIAL_SPACE, 1 ) ;
			thickness = ApplicationHelper.getClassNode( this, dlT.getName(), node ).getDouble( ApplicationConstant.PK_DIAL_THICKNESS, 1.2 ) ;
			linewidth = ApplicationHelper.getClassNode( this, dlT.getName(), node ).getDouble( ApplicationConstant.PK_DIAL_LINEWIDTH, .01 ) ;

			hasBaseRail = true ;
		}

		rise = ApplicationHelper.getClassNode( this, dlT.getName(), ApplicationConstant.PN_DIAL_ANNOTATION ).getDouble( ApplicationConstant.PK_DIAL_RISE, 3.2 ) ;
	}

	public void emitPS( PostscriptStream ps ) throws ParameterNotValidException {
		java.util.Vector<astrolabe.Vector> vV ;
		java.util.Vector<double[]> vD ;
		double[] xy ;

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
		vD = ApplicationHelper.convertCartesianVectorToDouble( vV ) ;
		ps.operator.mark() ;
		for ( int c=vD.size() ; c>0 ; c-- ) {
			xy = (double[]) vD.get( c-1 ) ;
			ps.push( xy[0] ) ;
			ps.push( xy[1] ) ;
		}
		ps.custom( ApplicationConstant.PS_PROLOG_POLYLINE ) ;
	}

	private void emitPSBaseNone( PostscriptStream ps ) throws ParameterNotValidException {
		graduation( ps ) ;
	}

	private void emitPSBaseLine( PostscriptStream ps ) throws ParameterNotValidException {
		double b, e ;
		int ns = 0 ;
		java.util.Vector<astrolabe.Vector> vV ;
		java.util.Vector<double[]> vD ;

		vV = new java.util.Vector<astrolabe.Vector>() ;
		quantity.set( span ) ;

		try { // baseline
			for ( ; ; ns++ ) {
				b = quantity.distanceN( ns ) ;
				e = quantity.distanceN( ns+1 ) ;

				// in case that quantity handles dates this happens on turn of the year
				if ( e<b ) {
					continue ;
				}

				vV.addAll( circle.cartesianList( b, e, reflect?-space:space ) ) ;
			}
		} catch ( ParameterNotValidException ePNV ) {
			double[] xy ;

			vD = ApplicationHelper.convertCartesianVectorToDouble( vV ) ;
			ps.operator.mark() ;
			for ( int c=vD.size() ; c>0 ; c-- ) {
				xy = (double[]) vD.get( c-1 ) ;
				ps.push( xy[0] ) ;
				ps.push( xy[1] ) ;
			}
			ps.custom( ApplicationConstant.PS_PROLOG_POLYLINE ) ;
			ps.operator.stroke() ;
		}

		graduation( ps ) ;
	}

	private void emitPSBaseRail( PostscriptStream ps ) throws ParameterNotValidException {
		double b, e, s ;
		int nss = 0 ;
		java.util.Vector<astrolabe.Vector> vVFw, vVRv ;
		java.util.Vector<double[]> vDFw = null ;

		quantity.set( span/division ) ;

		try { // baseline
			for ( ; ; nss++ ) {
				b = quantity.distanceN( nss ) ;
				e = quantity.distanceN( nss+1 ) ;

				// in case that quantity handles dates this happens on turn of the year
				if ( e<b ) {
					continue ;
				}

				s = nss%2==0?space:space+linewidth/2 ;
				s = reflect?-s:s ;			
				vVFw = circle.cartesianList( b, e, s ) ;

				s = space+( nss%2==0?thickness:thickness-linewidth/2 ) ;
				s = reflect?-s:s ;
				vVRv = circle.cartesianList( b, e, s ) ;

				vVRv = ApplicationHelper.reverseVector( vVRv ) ;
				vVFw.addAll( vVRv ) ;

				vDFw = ApplicationHelper.convertCartesianVectorToDouble( vVFw ) ;

				if ( nss%2 == 0 ) { // subunit filled
					double[] xy ;

					ps.operator.mark() ;
					for ( int c=vDFw.size() ; c>0 ; c-- ) {
						xy = (double[]) vDFw.get( c-1 ) ;
						ps.push( xy[0] ) ;
						ps.push( xy[1] ) ;
					}
					ps.custom( ApplicationConstant.PS_PROLOG_POLYLINE ) ;
					ps.operator.closepath() ;
					ps.operator.fill() ;
				} else { // subunit unfilled
					java.util.Vector<double[]> fw, rv ;
					double[] xy ;

					fw = new java.util.Vector<double[]>( vDFw.subList( 0, vDFw.size()/2 ) ) ;
					rv = new java.util.Vector<double[]>( vDFw.subList( vDFw.size()/2, vDFw.size() ) ) ;

					ps.operator.mark() ;
					for ( int c=fw.size() ; c>0 ; c-- ) {
						xy = (double[]) fw.get( c-1 ) ;
						ps.push( xy[0] ) ;
						ps.push( xy[1] ) ;
					}
					ps.custom( ApplicationConstant.PS_PROLOG_POLYLINE ) ;
					ps.operator.stroke() ;
					ps.operator.mark() ;
					for ( int c=rv.size() ; c>0 ; c-- ) {
						xy = (double[]) rv.get( c-1 ) ;
						ps.push( xy[0] ) ;
						ps.push( xy[1] ) ;
					}
					ps.custom( ApplicationConstant.PS_PROLOG_POLYLINE ) ;
					ps.operator.stroke() ;
				}
			}
		} catch ( ParameterNotValidException ePNV ) {
			if ( nss%2 == 0 ) { // close unfilled subunit
				java.util.Vector<double[]> vector ;
				double[] xy ;

				vector = new java.util.Vector<double[]>( vDFw.subList( vDFw.size()/2-1, vDFw.size()/2+1 ) ) ;

				xy = (double[]) vector.get( 1 ) ;
				ps.push( xy[0] ) ;
				ps.push( xy[1] ) ;
				xy = (double[]) vector.get( 0 ) ;
				ps.push( xy[0] ) ;
				ps.push( xy[1] ) ;
				ps.custom( ApplicationConstant.PS_PROLOG_LINE ) ;
				ps.operator.stroke() ;
			}
		}

		graduation( ps ) ;
	}

	private void graduation( PostscriptStream ps ) throws ParameterNotValidException {
		double d ;
		double rad90 ;
		int ns ;
		Vector origin, tangent ;
		Graduation g ;
		astrolabe.model.GraduationType gdT ;

		rad90 = CAACoordinateTransformation.DegreesToRadians( 90 ) ;

		origin = null ;
		tangent = null ;

		quantity.set( span ) ;

		// Closed circle and dial starting at 0
		ns = circle.isClosed()&&Math.isE0( quantity.distance0() )?1:0 ;

		for ( ; ; ns++ ) {
			try {
				d = quantity.distanceN( ns ) ;
			} catch ( ParameterNotValidException e ) {
				break ;
			}

			try {
				origin = circle.cartesian( d, reflect?-( space+thickness ):space+thickness ) ;
				tangent = circle.tangentVector( d ).rotate( reflect?-rad90:rad90 ) ;
			} catch ( ParameterNotValidException e ) {}

			gdT = dlT.getGraduationSpan() ;
			g = new GraduationSpan( origin, tangent ) ;

			try { // half
				if ( quantity.isGraduationModN( dlT.getGraduationHalf().getSpan(), ns ) ) {
					gdT = dlT.getGraduationHalf() ;
					g = new GraduationHalf( origin, tangent ) ;
				}
			} catch ( NullPointerException e ) {}

			try { // full
				if ( quantity.isGraduationModN( dlT.getGraduationFull().getSpan(), ns ) ) {
					gdT = dlT.getGraduationFull() ;
					g = new GraduationFull( origin, tangent ) ;
				}
			} catch ( NullPointerException e ) {}

			ps.operator.gsave() ;

			g.emitPS( ps ) ;

			quantity.register( ns ) ;

			for ( int an=0 ; an<gdT.getAnnotationStraightCount() ; an++ ) {
				astrolabe.model.AnnotationType anT ;
				Annotation annotation ;

				ps.operator.gsave() ;

				anT = gdT.getAnnotationStraight( an ) ;
				annotation = new AnnotationStraight( anT ) ;
				annotation.emitPS( ps ) ;

				ps.operator.grestore() ;
			}

			ps.operator.grestore() ;
		}
	}

	public void setQuantity( Span quantity ) {
		this.quantity = quantity ;
	}

	public Circle dotDot() {
		return circle ;
	}
}
