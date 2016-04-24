
package astrolabe;

import caa.CAACoordinateTransformation;

@SuppressWarnings("serial")
public class CircleParallel extends astrolabe.model.CircleParallel implements Circle {

	private final static double DEFAULT_SEGMENT = 1 ;
	private final static double DEFAULT_IMPORTANCE = .1 ;

	private Projector projector ;

	private double segment ;
	private double linewidth ;

	private double al ;

	private double begin ;
	private double end ;

	public CircleParallel() {
	}

	public CircleParallel( Object peer, Projector projector ) throws ParameterNotValidException {
		setup( peer, projector ) ;
	}

	public void setup( Object peer, Projector projector ) throws ParameterNotValidException {
		String key ;

		ApplicationHelper.setupCompanionFromPeer( this, peer ) ;

		this.projector = projector ;

		segment = CAACoordinateTransformation.DegreesToRadians(
				ApplicationHelper.getClassNode( this,
						getName(), null ).getDouble( ApplicationConstant.PK_CIRCLE_SEGMENT, DEFAULT_SEGMENT ) ) ;
		linewidth = ApplicationHelper.getClassNode( this,
				getName(), ApplicationConstant.PN_CIRCLE_IMPORTANCE ).getDouble( getImportance(), DEFAULT_IMPORTANCE ) ;

		try {
			al = AstrolabeFactory.valueOf( getAngle() ) ;
			key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_CIRCLE_ALTITUDE ) ;
			ApplicationHelper.registerDMS( key, al, 2 ) ;
		} catch ( ParameterNotValidException e ) {}
		try {
			begin = AstrolabeFactory.valueOf( getBegin().getImmediate() ) ;
		} catch ( ParameterNotValidException e ) {
			Registry registry ;
			Circle circle ;
			boolean leading ;

			registry = new Registry() ;
			circle = (Circle) registry.retrieve( getBegin().getReference().getCircle() ) ;

			leading = getBegin().getReference().getNode().equals( ApplicationConstant.AV_CIRCLE_LEADING ) ;

			begin = circle.isParallel()?
					intersect( (CircleParallel) circle, leading ):
						intersect( (CircleMeridian) circle, leading ) ;
		}
		try {
			end = AstrolabeFactory.valueOf( getEnd().getImmediate() ) ;
		} catch ( ParameterNotValidException e ) {
			Registry registry ;
			Circle circle ;
			boolean leading ;

			registry = new Registry() ;
			circle = (Circle) registry.retrieve( getEnd().getReference().getCircle() ) ;

			leading = getEnd().getReference().getNode().equals( ApplicationConstant.AV_CIRCLE_LEADING ) ;

			end = circle.isParallel()?
					intersect( (CircleParallel) circle, leading ):
						intersect( (CircleMeridian) circle, leading ) ;
		}
	}

	public double[] project( double az ) {
		return project( az, 0 ) ;
	}

	public double[] project( double az, double shift ) {
		double[] r = new double[2] ;
		Vector v, t ;

		v = new Vector( projector.project( az, al ) ) ;

		if ( shift != 0 ) {
			t = new Vector( tangent( az ) ) ;
			v.add( t.rotate( Math.rad90 ).size( shift ) ) ;
		}

		r[0] = v.getX() ;
		r[1] = v.getY() ;

		return r ;
	}

	public double[] tangent( double az ) {
		double[] r = new double[2] ;
		Vector a, b ;
		double d ;

		d = CAACoordinateTransformation.DegreesToRadians( 10./3600 ) ;

		a = new Vector( projector.project( az+d, al ) ) ;
		b = new Vector( projector.project( az, al ) ) ;

		a.sub( b ) ;

		r[0] = a.getX() ;
		r[1] = a.getY() ;

		return r ;
	}

	public java.util.Vector<double[]> list() {
		return list( begin, end, 0 ) ;
	}

	public java.util.Vector<double[]> list( double shift ) {
		return list( begin, end, shift ) ;
	}

	public java.util.Vector<double[]> list( double begin, double end, double shift ) {
		java.util.Vector<double[]> r = new java.util.Vector<double[]>() ;
		double g ;

		r.add( project( begin, shift ) ) ;

		g = mapIndexToAngleOfRange( begin, end ) ;
		for ( double az=begin+g ; begin>end?az-Math.rad360<end:az<end ; az=az+segment ) {
			r.add( project( az, shift ) ) ;
		}

		r.add( project( end, shift ) ) ;

		return r ;
	}

	public void headPS( PostscriptStream ps ) {
		ps.operator.setlinewidth( linewidth ) ;
	}

	public void emitPS( PostscriptStream ps ) {
		java.util.Vector<double[]> v ;
		double[] xy ;

		v = list() ;
		ps.operator.mark() ;
		for ( int n=v.size() ; n>0 ; n-- ) {
			xy = (double[]) v.get( n-1 ) ;
			ps.push( xy[0] ) ;
			ps.push( xy[1] ) ;
		}
		try {
			ps.custom( ApplicationConstant.PS_PROLOG_POLYLINE ) ;
		} catch ( ParameterNotValidException e ) {} // polyline is considered well-defined
		ps.operator.stroke() ;

		try {
			ApplicationHelper.emitPS( ps, getAnnotation() ) ;
		} catch ( ParameterNotValidException e ) {} // optional
	}

	public void tailPS( PostscriptStream ps ) {
	}

	public static double[] intersection( double rdB, double gnB, double blA, double blB, double blGa ) throws ParameterNotValidException {
		double blC, blAl, blBe ;
		double rdGa[], rdDe ;
		double gnGa[], gnDe ;
		double[] rdaz, gnaz ;
		double[] r ;

		rdGa = new double[2] ;
		gnGa = new double[2] ;
		rdaz = new double[2] ;
		gnaz = new double[2] ;

		r = new double[4] ;

		// i. rdST<gnST && abs( gnST-rdST )>180 is equivalent to rdST>gnST && abs ( gnST-rdST )<180
		// ii. rdST>gnST && abs( gnST-rdST )>180 is equivalent to rdST<gnST && abs ( gnST-rdST )<180
		if ( Math.rad180<blGa ) {			// case i.
			blGa = -( 2*Math.rad180-blGa ) ;
		} else if ( -Math.rad180>blGa ) {	// case ii.
			blGa = blGa+2*Math.rad180 ;
		}

		if ( blA==0 ) {	// rdLa==90
			blC = blB ;

			// intersection test
			if ( blGa<0?!( gnB>( rdB-blC ) ):!( rdB>( gnB-blC ) ) ) {
				throw new ParameterNotValidException() ;
			}

			rdDe = Math.rad180-Math.lawOfEdgeCosineSolveAngle( gnB, rdB, blC) ;
			gnDe = Math.lawOfEdgeCosineSolveAngle( rdB, blC, gnB) ;

			rdGa[0] = -( rdDe+blGa ) ;
			rdGa[1] = rdDe-blGa ;
			gnGa[0] = -gnDe ;
			gnGa[1] = gnDe ;

			rdaz[0] = Math.rad180+rdGa[0] ;
			rdaz[1] = Math.rad180+rdGa[1] ;
			gnaz[0] = ApplicationHelper.mapTo0To360Range( Math.rad180+gnGa[0] ) ;
			gnaz[1] = ApplicationHelper.mapTo0To360Range( Math.rad180+gnGa[1] ) ;
		} else if ( blB==0 ) {	// gnLa==90
			blC = blA ;

			// intersection test
			if ( blGa<0?!( gnB>( rdB-blC ) ):!( rdB>( gnB-blC ) ) ) {
				throw new ParameterNotValidException() ;
			}

			rdDe = Math.lawOfEdgeCosineSolveAngle( gnB, rdB, blC) ;
			gnDe = Math.rad180-Math.lawOfEdgeCosineSolveAngle( rdB, blC, gnB) ;

			rdGa[0] = -rdDe ;
			rdGa[1] = rdDe ;
			gnGa[0] = -( gnDe-blGa ) ;
			gnGa[1] = gnDe+blGa ;

			rdaz[0] = Math.rad180+rdGa[0] ;
			rdaz[1] = Math.rad180+rdGa[1] ;
			gnaz[0] = ApplicationHelper.mapTo0To360Range( Math.rad180+gnGa[0] ) ;
			gnaz[1] = ApplicationHelper.mapTo0To360Range( Math.rad180+gnGa[1] ) ;
		} else if ( java.lang.Math.sin( blGa )==0 ) {	/* rdST==gnST,
															rdST==gnST+180 */
			blC = java.lang.Math.abs( blA-blB*java.lang.Math.cos( blGa ) ) ;

			// intersection test
			if ( blGa<0?!( gnB>( rdB-blC ) ):!( rdB>( gnB-blC ) ) ) {
				throw new ParameterNotValidException() ;
			}

			if ( blC>0 ) {
				rdDe = Math.rad180-Math.lawOfEdgeCosineSolveAngle( gnB, rdB, blC) ;
				gnDe = Math.lawOfEdgeCosineSolveAngle( rdB, blC, gnB) ;
			} else {
				rdDe = Math.lawOfEdgeCosineSolveAngle( gnB, rdB, blC) ;
				gnDe = Math.rad180-Math.lawOfEdgeCosineSolveAngle( rdB, blC, gnB) ;
			}

			rdGa[0] = -rdDe ;
			rdGa[1] =  rdDe ;
			gnGa[0] = -gnDe ;
			gnGa[1] = gnDe ;

			rdaz[0] = Math.rad180+rdGa[0] ;
			rdaz[1] = Math.rad180+rdGa[1] ;
			gnaz[0] = Math.rad180+gnGa[0] ;
			gnaz[1] = Math.rad180+gnGa[1] ;

		} else {	/* rdST>gnST && abs( gnST-rdST )>180,
						rdST>gnST && abs( gnST-rdST )<180,
						rdST>gnST && abs( gnST-rdST )<90,
						rdST<gnST && abs( gnST-rdST )>180,
						rdST<gnST && abs( gnST-rdST )<180,
						rdST<gnST && abs( gnST-rdST )<90 */
			blC = Math.lawOfEdgeCosine( blA, blB, blGa ) ;
			blAl = Math.lawOfEdgeCosineSolveAngle( blA, blB, blC) ;
			blBe = Math.lawOfEdgeCosineSolveAngle( blB, blC, blA) ;

			// intersection test
			if ( blGa<0?!( gnB>( rdB-blC ) ):!( rdB>( gnB-blC ) ) ) {
				throw new ParameterNotValidException() ;
			}

			rdDe = Math.lawOfEdgeCosineSolveAngle( gnB, rdB, blC) ;
			gnDe = Math.lawOfEdgeCosineSolveAngle( rdB, blC, gnB) ;

			if ( rdDe<( Math.rad180-blBe ) ) {	// aph	
				gnDe = Math.rad180-gnDe ;

				if ( blGa<0 ) {
					rdGa[0] = blBe+rdDe ;
					rdGa[1] = blBe-rdDe ;
					gnGa[0] = Math.rad180-blAl+gnDe ;
					gnGa[1] = Math.rad180-blAl-gnDe ;

					rdaz[0] = Math.rad180-rdGa[0] ;
					rdaz[1] = Math.rad180-rdGa[1] ;
					gnaz[0] = ApplicationHelper.mapTo0To360Range( Math.rad180-gnGa[0] ) ;
					gnaz[1] = ApplicationHelper.mapTo0To360Range( Math.rad180-gnGa[1] ) ;
				} else {
					rdGa[0] = blBe-rdDe ;
					rdGa[1] = blBe+rdDe ;
					gnGa[0] = Math.rad180-blAl-gnDe ;
					gnGa[1] = Math.rad180-blAl+gnDe ;

					rdaz[0] = Math.rad180+rdGa[0] ;
					rdaz[1] = Math.rad180+rdGa[1] ;
					gnaz[0] = ApplicationHelper.mapTo0To360Range( Math.rad180+gnGa[0] ) ;
					gnaz[1] = ApplicationHelper.mapTo0To360Range( Math.rad180+gnGa[1] ) ;
				}
			} else {						// per
				rdDe = Math.rad180-rdDe ;

				if ( blGa<0 ) {
					rdGa[0] = Math.rad180-blBe+rdDe ;
					rdGa[1] = Math.rad180-blBe-rdDe ;
					gnGa[0] = blAl+gnDe ;
					gnGa[1] = blAl-gnDe ;

					rdaz[0] = Math.rad180+rdGa[0] ;
					rdaz[1] = Math.rad180+rdGa[1] ;
					gnaz[0] = Math.rad180+gnGa[0] ;
					gnaz[1] = Math.rad180+gnGa[1] ;
				} else {
					rdGa[0] = Math.rad180-blBe-rdDe ;
					rdGa[1] = Math.rad180-blBe+rdDe ;
					gnGa[0] = blAl-gnDe ;
					gnGa[1] = blAl+gnDe ;

					rdaz[0] = Math.rad180-rdGa[0] ;
					rdaz[1] = Math.rad180-rdGa[1] ;
					gnaz[0] = Math.rad180-gnGa[0] ;
					gnaz[1] = Math.rad180-gnGa[1] ;
				}
			}
		}

		r[0] = rdaz[0] ;
		r[1] = rdaz[1] ;
		r[2] = gnaz[0] ;
		r[3] = gnaz[1] ;

		return r ;
	}

	public double intersect( CircleParallel gn, boolean leading ) throws ParameterNotValidException {
		double blA, blB, blGa, rdB, gnB ;
		double[] rdeqA, rdeqO, rdhoA, rdhoO, gneq, gnho ;
		double rdhoC[], rdST, rdLa, gnhoC[], gnST, gnLa ;
		double[] inaz ;
		double gnal ;

		rdhoC = zenit() ;
		rdST = rdhoC[0] ;
		rdLa = rdhoC[1] ;
		gnhoC = gn.zenit() ;
		gnST = gnhoC[0] ;
		gnLa = gnhoC[1] ;

		rdB = Math.rad90-al ;
		gnB = Math.rad90-gn.al ;
		blA = Math.rad90-rdLa ;//horizon.getLa() ;
		blB = Math.rad90-gnLa ;//gn.dotDot().getLa() ;
		blGa = gnST-rdST ;//gn.dotDot().getST()-horizon.getST() ;

		inaz = CircleParallel.intersection( rdB, gnB, blA, blB, blGa ) ;

		// unconvert local rd into actual
		rdeqA = ApplicationHelper.horizontal2Equatorial( inaz[0], al, rdLa/*horizon.getLa()*/ ) ;
		rdeqA[0] = rdST-rdeqA[0] ;//horizon.getST()-rdeqA[0] ;
		rdhoA = projector.unconvert( rdeqA ) ;

		rdeqO = ApplicationHelper.horizontal2Equatorial( inaz[1], al, rdLa/*horizon.getLa()*/ ) ;
		rdeqO[0] = rdST-rdeqO[0] ;//horizon.getST()-rdeqO[0] ;
		rdhoO = projector.unconvert( rdeqO ) ;

		// sort associated values
		if ( rdhoA[0]>rdhoO[0] ) {
			double v ;

			v = rdhoA[0] ;
			rdhoA[0] = rdhoO[0] ;
			rdhoO[0] = v ;

			v = inaz[2] ;
			inaz[2] = inaz[3] ;
			inaz[3] = v ;
		}

		// unconvert local gn into actual
		gnal = AstrolabeFactory.valueOf( gn.getAngle() ) ;
		gneq = ApplicationHelper.horizontal2Equatorial( leading?inaz[3]:inaz[2], gnal, gnLa/*gn.dotDot().getLa()*/ ) ;
		gneq[0] = gnST-gneq[0] ;//gn.dotDot().getST()-gneq[0] ;
		gnho = gn.projector.unconvert( gneq ) ;//dotDot().unconvert( gneq ) ;

		if ( ! gn.probe( gnho[0] ) ) {
			throw new ParameterNotValidException() ;
		}

		return leading?rdhoO[0]:rdhoA[0] ;
	}

	public double intersect( CircleMeridian gn, boolean leading ) throws ParameterNotValidException {
		double blA, blB, blGa, rdB ;
		double[] rdeqA, rdeqO, rdhoA, rdhoO ;
		double rdhoC[], rdST, rdLa ;
		double inaz[], gnal ;

		rdhoC = zenit() ;
		rdST = rdhoC[0] ;
		rdLa = rdhoC[1] ;

		rdB = Math.rad90-al ;
		blA = Math.rad90-rdLa ;//horizon.getLa() ;
		blB = Math.rad90-gn.transformParallelLa() ;
		blGa = gn.transformParallelST()-rdST ;//horizon.getST() ;

		inaz = CircleParallel.intersection( rdB, Math.rad90, blA, blB, blGa ) ;

		// unconvert local rd into actual
		rdeqA = ApplicationHelper.horizontal2Equatorial( inaz[0], al, rdLa/*horizon.getLa()*/ ) ;
		rdeqA[0] = rdST-rdeqA[0] ;//horizon.getST()-rdeqA[0] ;
		rdhoA = projector.unconvert( rdeqA ) ;

		rdeqO = ApplicationHelper.horizontal2Equatorial( inaz[1], al, rdLa/*horizon.getLa()*/ ) ;
		rdeqO[0] = rdST-rdeqO[0] ;//horizon.getST()-rdeqO[0] ;
		rdhoO = projector.unconvert( rdeqO ) ;

		// sort associated values
		if ( rdhoA[0]>rdhoO[0] ) {
			double v ;

			v = rdhoA[0] ;
			rdhoA[0] = rdhoO[0] ;
			rdhoO[0] = v ;

			v = inaz[2] ;
			inaz[2] = inaz[3] ;
			inaz[3] = v ;
		}

		gnal = leading?gn.transformMeridianAl( inaz[3] ):gn.transformMeridianAl( inaz[2] ) ;

		if ( ! gn.probe( gnal ) ) {
			throw new ParameterNotValidException() ;
		}

		return leading?rdhoO[0]:rdhoA[0] ;
	}

	public boolean probe( double az ) {
		return probe( az, begin, end ) ;
	}

	public static boolean probe( double angle, double begin, double end ) {
		boolean r ;
		double b, e ;
		double a ;

		b = ApplicationHelper.mapTo0To360Range( begin ) ;
		e = ApplicationHelper.mapTo0To360Range( end ) ;
		a = ApplicationHelper.mapTo0To360Range( angle ) ;

		r = b>e?a>=b||a<=e:a>=b&&a<=e ;

		return r ;
	}

	public double mapIndexToAngleOfScale( int index ) {
		return mapIndexToAngleOfScale( index, segment, begin, end ) ;
	}

	public double mapIndexToAngleOfScale( double span ) {
		return mapIndexToAngleOfScale( 0, span, begin, end ) ;
	}

	public double mapIndexToAngleOfScale( int index, double span ) {
		return mapIndexToAngleOfScale( index, span, begin, end ) ;
	}

	public static double mapIndexToAngleOfScale( int index, double span, double begin, double end ) {
		double r ;
		double s ;

		if ( index<0 ) { // last
			s = java.lang.Math.abs( end-(int) ( end/span )*span ) ;
			if ( end>0 ) {
				if ( end>begin ) {
					r = end-s ;
				} else { // end<begin
					r = end+span-s ;
				}
			} else { // end<0
				if ( end>begin ) {
					r = end-span+s ;
				} else { // end<begin
					r = end+s ;
				}
			}
		} else {
			s = java.lang.Math.abs( begin-(int) ( begin/span )*span ) ;
			if ( begin>0 ) {
				if ( begin<end ) {
					r = begin+span-s+index*span ;
				} else { // begin>end
					r = begin-s-index*span ;
				}
			} else { // begin<0
				if ( begin<end ) {
					r = begin+s+index*span ;
				} else { // begin>end
					r = begin-span+s-index*span ;
				}
			}
		}

		return r ;
	}

	public double[] zenit() {
		return projector.convert( 0, Math.rad90 ) ;
	}

	public boolean isParallel() {
		return true ;
	}

	public boolean isMeridian() {
		return false ;
	}

	public double mapIndexToAngleOfRange() {
		return gap( 0, segment, begin , end ) ;
	}

	public double mapIndexToAngleOfRange( double begin, double end ) {
		return gap( 0, segment, begin , end ) ;
	}

	public double mapIndexToAngleOfRange( int index, double begin, double end ) {
		return gap( index, segment, begin , end ) ;
	}

	public static double gap( int index, double segment, double begin, double end ) {
		double r ;
		double b, e ;
		double d, g ;

		b = ApplicationHelper.mapTo0To360Range( begin ) ;
		e = ApplicationHelper.mapTo0To360Range( end ) ;		

		d = b>e?Math.rad360+e-b:e-b ;
		g = d-(int) ( d/segment )*segment ;

		r = ( ( Math.isLim0( g )?segment:g )/2 )+index*segment ;

		return r ;
	}
}
