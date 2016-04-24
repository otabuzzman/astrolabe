
package astrolabe;

import caa.CAACoordinateTransformation;

public class DialDegrees extends Model implements Dial {

	private double segment ;

	private double rise ;

	private double space ;
	private double thickness ;
	private double linewidth ;

	private double unit ;
	private int division ;

	private boolean hasBaseNone ;
	private boolean hasBaseLine ;
	private boolean hasBaseRail ;

	private astrolabe.model.DialType dlT ;
	private Circle circle ;

	public DialDegrees( astrolabe.model.DialType dlT, Circle circle ) {
		String baseline ;
		double interval ;
		double rad1d ;

		this.dlT = dlT ;
		this.circle = circle ;

		segment = CAACoordinateTransformation.DegreesToRadians(
				getClassNode( null, null ).getDouble( "segment", 1 ) ) ;

		rise = getClassNode( null, "annotation" ).getDouble( "rise", 3.2 ) ;

		interval = dlT.getGraduationUnit().getInterval() ;

		rad1d = CAACoordinateTransformation.DegreesToRadians( 1 ) ;
		unit = rad1d*interval ;

		baseline = dlT.getBaseline() ;

		hasBaseNone = false ;
		hasBaseLine = false ;
		hasBaseRail = false ;

		if ( baseline.equals( "none") ) {
			division = 1 ;

			space = getClassNode( null, "baseline/none" ).getDouble( "space", .1 ) ;

			hasBaseNone = true ;
		} else if ( baseline.equals( "line" ) ) {
			division = 1 ;

			space = getClassNode( null, "baseline/line" ).getDouble( "space", 1 ) ;
			thickness = getClassNode( null, "baseline/line" ).getDouble( "thickness", .2 ) ;

			hasBaseLine = true ;
		} else if ( baseline.equals( "rail" ) ) {
			division = dlT.getGraduationUnit().getDivision() ;

			space = getClassNode( null, "baseline/rail" ).getDouble( "space", 1 ) ;
			thickness = getClassNode( null, "baseline/rail" ).getDouble( "thickness", 1.2 ) ;
			linewidth = getClassNode( null, "baseline/rail" ).getDouble( "linewidth", .01 ) ;

			hasBaseRail = true ;
		}
	}

	public void emitPS(PostscriptStream ps) throws InvalidUnicodeCharacterException {
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

		vV = circle.cartesianList( ( space+thickness )+rise, segment ) ;
		vD = CircleHelper.convertCartesianVectorToDouble( vV ) ;
		ps.polyline( vD ) ;
	}

	private void emitPSBaseNone( PostscriptStream ps ) throws InvalidUnicodeCharacterException {
		double subunit, aV, su ;
		double a, b ;
		double az, al ;
		int su0 ;
		astrolabe.Vector origin, tangent ;

		subunit = unit/division ;

		su = offsetBeginUnit( circle.getBegin(), unit ) ;
		su0 = (int) ( su/subunit ) ;

		a = circle.getAngle() ;
		b = circle.getBegin()+su ;

		for ( int nsu=0 ; b+nsu*subunit<circle.getEnd() ; nsu++ ) {
			aV = b+nsu*subunit ;

			az = circle.isMeridian()?a:aV ;
			al = circle.isMeridian()?aV:a ;

			if ( hasGraduationFull() && nsu%division == su0 &&
					(int) Math.remainder( CAACoordinateTransformation.RadiansToDegrees( aV )+.000000001,
							dlT.getGraduationFull().getInterval() ) == 0 ) {
				origin = circle.cartesian( az, al, space+thickness) ;
				tangent = circle.tangentVector( az, al ) ;

				ps.operator.gsave() ;

				new GraduationFull( origin, tangent ).emitPS( ps ) ;

				try {
					ReplacementHelper.registerDMS( "dial", aV, 2 ) ;
					ReplacementHelper.registerHMS( "dial", aV, 2 ) ;
					AnnotationHelper.emitPS( ps, dlT.getGraduationFull().getAnnotationStraight() ) ;
				} catch ( ParameterNotValidException e ) {}

				ps.operator.grestore() ;
			} else if ( hasGraduationHalf() && nsu%division == su0 &&
					(int) Math.remainder( CAACoordinateTransformation.RadiansToDegrees( aV )+.000000001,
							dlT.getGraduationHalf().getInterval() ) == 0 ) {
				origin = circle.cartesian( az, al, space+thickness) ;
				tangent = circle.tangentVector( az, al ) ;

				ps.operator.gsave() ;

				new GraduationHalf( origin, tangent ).emitPS( ps ) ;

				try {
					ReplacementHelper.registerDMS( "dial", aV, 2 ) ;
					ReplacementHelper.registerHMS( "dial", aV, 2 ) ;
					AnnotationHelper.emitPS( ps, dlT.getGraduationHalf().getAnnotationStraight() ) ;
				} catch ( ParameterNotValidException e ) {}

				ps.operator.grestore() ;
			} else if ( nsu%division == su0 ) {
				origin = circle.cartesian( az, al, space+thickness) ;
				tangent = circle.tangentVector( az, al ) ;

				ps.operator.gsave() ;

				new GraduationUnit( origin, tangent ).emitPS( ps ) ;

				try {
					ReplacementHelper.registerDMS( "dial", aV, 2 ) ;
					ReplacementHelper.registerHMS( "dial", aV, 2 ) ;
					AnnotationHelper.emitPS( ps, dlT.getGraduationUnit().getAnnotationStraight() ) ;
				} catch ( ParameterNotValidException e ) {}

				ps.operator.grestore() ;
			}
		}
	}

	private void emitPSBaseLine( PostscriptStream ps ) throws InvalidUnicodeCharacterException {
		double subunit, aV, su ;
		double r, d ;
		double a, b ;
		double az, al ;
		java.util.Vector<astrolabe.Vector> vV ;
		java.util.Vector<double[]> vD ;
		int su0 ;
		astrolabe.Vector origin, tangent ;

		subunit = unit/division ;

		su = offsetBeginUnit( circle.getBegin(), unit ) ;
		su0 = (int) ( su/subunit ) ;

		d = java.lang.Math.min( subunit, segment ) ;
		r = d/2 ;

		a = circle.getAngle() ;
		b = circle.getBegin()+su ;

		vV = new java.util.Vector<astrolabe.Vector>() ;

		az = circle.isMeridian()?a:b+su ;
		al = circle.isMeridian()?b+su:a ;		

		vV.add( circle.cartesian( az, al, space ) ) ;

		for ( int nsu=0 ; b+nsu*subunit<circle.getEnd() ; nsu++ ) {
			aV = b+nsu*subunit ;

			az = circle.isMeridian()?a:aV ;
			al = circle.isMeridian()?aV:a ;

			vV.add( circle.cartesian( az, al, space ) ) ;
			for ( int nssu=0 ; r+nssu*d<subunit ; nssu++ ) {
				az = circle.isMeridian()?a:aV+r+nssu*d ;
				al = circle.isMeridian()?aV+r+nssu*d:a ;

				vV.add( circle.cartesian( az, al, space ) ) ;
			}
			az = circle.isMeridian()?a:b+( nsu+1 )*subunit ;
			al = circle.isMeridian()?b+( nsu+1 )*subunit:a ;

			vV.add( circle.cartesian( az, al, space ) ) ;

			if ( hasGraduationFull() && nsu%division == su0 &&
					(int) Math.remainder( CAACoordinateTransformation.RadiansToDegrees( aV )+.000000001,
							dlT.getGraduationFull().getInterval() ) == 0 ) {
				origin = circle.cartesian( az, al, space+thickness) ;
				tangent = circle.tangentVector( az, al ) ;

				ps.operator.gsave() ;

				new GraduationFull( origin, tangent ).emitPS( ps ) ;

				try {
					ReplacementHelper.registerDMS( "dial", aV, 2 ) ;
					ReplacementHelper.registerHMS( "dial", aV, 2 ) ;
					AnnotationHelper.emitPS( ps, dlT.getGraduationFull().getAnnotationStraight() ) ;
				} catch ( ParameterNotValidException e ) {}

				ps.operator.grestore() ;
			} else if ( hasGraduationHalf() && nsu%division == su0 &&
					(int) Math.remainder( CAACoordinateTransformation.RadiansToDegrees( aV )+.000000001,
							dlT.getGraduationHalf().getInterval() ) == 0 ) {
				origin = circle.cartesian( az, al, space+thickness) ;
				tangent = circle.tangentVector( az, al ) ;

				ps.operator.gsave() ;

				new GraduationHalf( origin, tangent ).emitPS( ps ) ;

				try {
					ReplacementHelper.registerDMS( "dial", aV, 2 ) ;
					ReplacementHelper.registerHMS( "dial", aV, 2 ) ;
					AnnotationHelper.emitPS( ps, dlT.getGraduationHalf().getAnnotationStraight() ) ;
				} catch ( ParameterNotValidException e ) {}

				ps.operator.grestore() ;
			} else if ( nsu%division == su0 ) {
				origin = circle.cartesian( az, al, space+thickness) ;
				tangent = circle.tangentVector( az, al ) ;

				ps.operator.gsave() ;

				new GraduationUnit( origin, tangent ).emitPS( ps ) ;

				try {
					ReplacementHelper.registerDMS( "dial", aV, 2 ) ;
					ReplacementHelper.registerHMS( "dial", aV, 2 ) ;
					AnnotationHelper.emitPS( ps, dlT.getGraduationUnit().getAnnotationStraight() ) ;
				} catch ( ParameterNotValidException e ) {}

				ps.operator.grestore() ;
			}
		}

		vD = CircleHelper.convertCartesianVectorToDouble( vV ) ;
		ps.polyline( vD ) ;
		ps.operator.stroke() ;
	}

	private void emitPSBaseRail( PostscriptStream ps ) throws InvalidUnicodeCharacterException {
		double subunit, aV, su, ssu ;
		double r, d ;
		double a, b ;
		double az, al, s ;
		java.util.Vector<astrolabe.Vector> vV ;
		java.util.Vector<double[]> vD = null ;
		int su0, nsu, nssu ;
		astrolabe.Vector origin, tangent ;

		subunit = unit/division ;

		su = offsetBeginUnit( circle.getBegin(), unit ) ;
		su0 = (int) ( su/subunit ) ;
		ssu = Math.remainder( su, subunit ) ;

		d = java.lang.Math.min( subunit, segment ) ;
		r = d/2 ;

		a = circle.getAngle() ;
		b = circle.getBegin()+ssu ;

		for ( nsu=0 ; b+( nsu+1 )*subunit<circle.getEnd() ; nsu++ ) {
			vV = new java.util.Vector<astrolabe.Vector>() ;

			aV = b+nsu*subunit ;

			az = circle.isMeridian()?a:aV ;
			al = circle.isMeridian()?aV:a ;
			s = nsu%2==0?space:space+linewidth/2 ;

			vV.add( circle.cartesian( az, al, s ) ) ;
			for ( nssu=0 ; r+nssu*d<subunit ; nssu++ ) {
				az = circle.isMeridian()?a:aV+r+nssu*d ;
				al = circle.isMeridian()?aV+r+nssu*d:a ;

				vV.add( circle.cartesian( az, al, s ) ) ;
			}
			az = circle.isMeridian()?a:b+( nsu+1 )*subunit ;
			al = circle.isMeridian()?b+( nsu+1 )*subunit:a ;

			vV.add( circle.cartesian( az, al, s ) ) ;

			s = space+( nsu%2==0?thickness:thickness-linewidth/2 ) ;

			vV.add( circle.cartesian( az, al, s ) ) ;
			for ( nssu-- ; nssu>=0 ; nssu-- ) {
				az = circle.isMeridian()?a:aV+r+nssu*d ;
				al = circle.isMeridian()?aV+r+nssu*d:a ;

				vV.add( circle.cartesian( az, al, s ) ) ;
			}
			az = circle.isMeridian()?a:aV ;
			al = circle.isMeridian()?aV:a ;

			vV.add( circle.cartesian( az, al, s ) ) ;

			vD = CircleHelper.convertCartesianVectorToDouble( vV ) ;

			if ( nsu%2 == 0 ) { // subunit filled
				ps.polyline( vD ) ;
				ps.operator.closepath() ;
				ps.operator.fill() ;
			} else { // subunit unfilled
				ps.polyline( new java.util.Vector<double[]>( vD.subList( 0, vD.size()/2 ) ) ) ;
				ps.operator.stroke() ;
				ps.polyline( new java.util.Vector<double[]>( vD.subList( vD.size()/2, vD.size() ) ) ) ;
				ps.operator.stroke() ;
			}

			if ( hasGraduationFull() && nsu%division == su0 &&
					(int) Math.remainder( CAACoordinateTransformation.RadiansToDegrees( aV )+.000000001,
							dlT.getGraduationFull().getInterval() ) == 0 ) {
				origin = circle.cartesian( az, al, space+thickness) ;
				tangent = circle.tangentVector( az, al ) ;

				ps.operator.gsave() ;

				new GraduationFull( origin, tangent ).emitPS( ps ) ;

				try {
					ReplacementHelper.registerDMS( "dial", aV, 2 ) ;
					ReplacementHelper.registerHMS( "dial", aV, 2 ) ;
					AnnotationHelper.emitPS( ps, dlT.getGraduationFull().getAnnotationStraight() ) ;
				} catch ( ParameterNotValidException e ) {}

				ps.operator.grestore() ;
			} else if ( hasGraduationHalf() && nsu%division == su0 &&
					(int) Math.remainder( CAACoordinateTransformation.RadiansToDegrees( aV )+.000000001,
							dlT.getGraduationHalf().getInterval() ) == 0 ) {
				origin = circle.cartesian( az, al, space+thickness) ;
				tangent = circle.tangentVector( az, al ) ;

				ps.operator.gsave() ;

				new GraduationHalf( origin, tangent ).emitPS( ps ) ;

				try {
					ReplacementHelper.registerDMS( "dial", aV, 2 ) ;
					ReplacementHelper.registerHMS( "dial", aV, 2 ) ;
					AnnotationHelper.emitPS( ps, dlT.getGraduationHalf().getAnnotationStraight() ) ;
				} catch ( ParameterNotValidException e ) {}

				ps.operator.grestore() ;
			} else if ( nsu%division == su0 ) {
				origin = circle.cartesian( az, al, space+thickness) ;
				tangent = circle.tangentVector( az, al ) ;

				ps.operator.gsave() ;

				new GraduationUnit( origin, tangent ).emitPS( ps ) ;

				try {
					ReplacementHelper.registerDMS( "dial", aV, 2 ) ;
					ReplacementHelper.registerHMS( "dial", aV, 2 ) ;
					AnnotationHelper.emitPS( ps, dlT.getGraduationUnit().getAnnotationStraight() ) ;
				} catch ( ParameterNotValidException e ) {}

				ps.operator.grestore() ;
			}
		}

		if ( nsu%2 == 0 ) { // close unfilled subunit
			ps.line( new java.util.Vector<double[]>( vD.subList( vD.size()/2-1, vD.size()/2+1 ) ) ) ;
			ps.operator.stroke() ;
		}
	}

	private boolean hasGraduationHalf() {
		return dlT.getGraduationHalf()!=null?true:false ;
	}

	private boolean hasGraduationFull() {
		return dlT.getGraduationFull()!=null?true:false ;
	}

	private double offsetBeginUnit( double begin, double unit ) {
		double r ;

		r = java.lang.Math.abs( Math.remainder( begin, unit ) ) ;

		return begin>0?unit-r:r ;
	}
}
