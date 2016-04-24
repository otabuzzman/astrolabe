
package astrolabe;

import java.util.Vector;

import caa.CAACoordinateTransformation;

public class CircleParallel extends Model implements Circle {

	private Chart chart ;
	protected Horizon horizon ;

	private double segment ;
	private double linewidth ;

	protected double al ;

	private double begin ;
	private double end ;

	public CircleParallel( astrolabe.model.CircleType clT, Chart chart, Horizon horizon ) throws ParameterNotValidException {

		segment = caa.CAACoordinateTransformation.DegreesToRadians(
				getClassNode( clT.getName(), null ).getDouble( "segment", 1 ) ) ;
		linewidth = getClassNode( clT.getName(), "importance" ).getDouble( clT.getImportance(), .1 ) ;

		this.chart = chart ;
		this.horizon = horizon ;

		try {
			al = Model.condense( clT.getAngle() ) ;
			ReplacementHelper.registerDMS( "circle", al, 2 ) ;
		} catch ( ParameterNotValidException e ) {}
		try {
			begin = Model.condense( clT.getBegin().getImmediate() ) ;
		} catch ( ParameterNotValidException e ) {
			Registry registry ;
			Circle circle ;
			boolean leading ;

			registry = new Registry() ;
			circle = (Circle) registry.retrieve( clT.getBegin().getReference().getCircle() ) ;

			leading = clT.getBegin().getReference().getNode().equals( "leading" ) ;

			begin = circle.isParallel()?
					intersect( (CircleParallel) circle, leading ):
						intersect( (CircleMeridian) circle, leading ) ;
		}
		try {
			end = Model.condense( clT.getEnd().getImmediate() ) ;
		} catch ( ParameterNotValidException e ) {
			Registry registry ;
			Circle circle ;
			boolean leading ;

			registry = new Registry() ;
			circle = (Circle) registry.retrieve( clT.getEnd().getReference().getCircle() ) ;

			leading = clT.getEnd().getReference().getNode().equals( "leading" ) ;

			end = circle.isParallel()?
					intersect( (CircleParallel) circle, leading ):
						intersect( (CircleMeridian) circle, leading ) ;
		}
	}

	public astrolabe.Vector cartesian( double az, double shift ) throws ParameterNotValidException {
		astrolabe.Vector r ;
		double rad90 ;

		if ( ! examine( az ) ) {
			throw new ParameterNotValidException() ;
		}

		rad90 = CAACoordinateTransformation.DegreesToRadians( 90 ) ;

		r = chart.project( horizon.convert( az, al ) ) ;
		if ( shift != 0 ) {
			r.add( tangentVector( az ).rotate( shift<0?-rad90:rad90 ).size( java.lang.Math.abs( shift ) ) ) ;
		}

		return r ;
	}

	public astrolabe.Vector cartesianA( double shift ) {
		astrolabe.Vector r = null ;

		try {
			r = cartesian( begin, shift ) ;
		} catch ( ParameterNotValidException e ) {}

		return r ;
	}

	public astrolabe.Vector cartesianO( double shift ) {
		astrolabe.Vector r = null ;

		try {
			r = cartesian( end, shift ) ;
		} catch ( ParameterNotValidException e ) {}

		return r ;
	}

	public Vector<astrolabe.Vector> cartesianList() throws ParameterNotValidException {
		return cartesianList( begin, end, 0 ) ;
	}

	public Vector<astrolabe.Vector> cartesianList( double shift ) throws ParameterNotValidException {
		return cartesianList( begin, end, shift ) ;
	}

	public Vector<astrolabe.Vector> cartesianList( double begin, double end, double shift ) throws ParameterNotValidException {
		Vector<astrolabe.Vector> r ;
		astrolabe.Vector a ;
		double rad360 ;
		double s, az ;

		r = new Vector<astrolabe.Vector>() ;

		rad360 = CAACoordinateTransformation.DegreesToRadians( 360 ) ;

		a = cartesian( begin, shift ) ;
		r.add( a ) ;

		s = java.lang.Math.abs( astrolabe.Math.remainder( begin, segment ) ) ;
		for ( az=begin+( s>0?s/2:segment/2 ) ; ( begin>end?az-rad360:az )<end ; az=az+segment ) {
			a = cartesian( az, shift ) ;
			r.add( a ) ;
		}

		a = cartesian( end, shift ) ;
		r.add( a ) ;

		return r ;
	}

	public double tangentAngle( double az ) throws ParameterNotValidException {
		astrolabe.Vector t ;
		double r ;

		t = tangentVector( az ) ;
		r = java.lang.Math.atan2( t.getY(), t.getX() ) ;

		return r ;
	}

	public astrolabe.Vector tangentVector( double az ) throws ParameterNotValidException {
		astrolabe.Vector r ;
		double d ;

		d = CAACoordinateTransformation.DegreesToRadians( 10./3600 ) ;
		r = chart.project( horizon.convert( az+d, al ) ).sub( chart.project( horizon.convert( az, al ) ) ) ;

		return r ;
	}

	public void initPS( PostscriptStream ps ) {
		ps.operator.setlinewidth( linewidth ) ;
	}

	public boolean isParallel() {
		return true ;
	}

	public boolean isMeridian() {
		return false ;
	}

	public boolean isClosed() {
		double b, e ;

		b = CAAHelper.MapTo0To360Range( begin ) ;
		e = CAAHelper.MapTo0To360Range( end ) ;

		return e==b||e>b ;
	}

	public boolean examine( double az ) {
		double b, e ;
		double v ;

		b = CAAHelper.MapTo0To360Range( begin ) ;
		e = CAAHelper.MapTo0To360Range( end ) ;
		v = CAAHelper.MapTo0To360Range( az ) ;

		return b>e?v>=b||v<=e:v>=b&&v<=e ;
	}

	protected static double[] intersection( double rdB, double gnB, double blA, double blB, double blGa ) throws ParameterNotValidException {
		double blC, blAl, blBe ;
		double rdGa[], rdDe ;
		double gnGa[], gnDe ;
		double[] rdaz, gnaz ;
		double rad180 ;
		double[] r ;

		rdGa = new double[2] ;
		gnGa = new double[2] ;
		rdaz = new double[2] ;
		gnaz = new double[2] ;

		r = new double[4] ;

		rad180 = CAACoordinateTransformation.DegreesToRadians( 180 ) ;

		// i. rdST<gnST && abs( gnST-rdST )>180 is equivalent to rdST>gnST && abs ( gnST-rdST )<180
		// ii. rdST>gnST && abs( gnST-rdST )>180 is equivalent to rdST<gnST && abs ( gnST-rdST )<180
		if ( rad180<blGa ) {			// case i.
			blGa = -( 2*rad180-blGa ) ;
		} else if ( -rad180>blGa ) {	// case ii.
			blGa = blGa+2*rad180 ;
		}

		if ( blA==0 ) {	// rdLa==90
			blC = blB ;

			// intersection test
			if ( blGa<0?!( gnB>( rdB-blC ) ):!( rdB>( gnB-blC ) ) ) {
				throw new ParameterNotValidException() ;
			}

			rdDe = rad180-astrolabe.Math.LawOfEdgeCosineSolveAngle( gnB, rdB, blC) ;
			gnDe = astrolabe.Math.LawOfEdgeCosineSolveAngle( rdB, blC, gnB) ;

			rdGa[0] = -( rdDe+blGa ) ;
			rdGa[1] = rdDe-blGa ;
			gnGa[0] = -gnDe ;
			gnGa[1] = gnDe ;

			rdaz[0] = rad180+rdGa[0] ;
			rdaz[1] = rad180+rdGa[1] ;
			gnaz[0] = CAAHelper.MapTo0To360Range( rad180+gnGa[0] ) ;
			gnaz[1] = CAAHelper.MapTo0To360Range( rad180+gnGa[1] ) ;
		} else if ( blB==0 ) {	// gnLa==90
			blC = blA ;

			// intersection test
			if ( blGa<0?!( gnB>( rdB-blC ) ):!( rdB>( gnB-blC ) ) ) {
				throw new ParameterNotValidException() ;
			}

			rdDe = astrolabe.Math.LawOfEdgeCosineSolveAngle( gnB, rdB, blC) ;
			gnDe = rad180-astrolabe.Math.LawOfEdgeCosineSolveAngle( rdB, blC, gnB) ;

			rdGa[0] = -rdDe ;
			rdGa[1] = rdDe ;
			gnGa[0] = -( gnDe-blGa ) ;
			gnGa[1] = gnDe+blGa ;

			rdaz[0] = rad180+rdGa[0] ;
			rdaz[1] = rad180+rdGa[1] ;
			gnaz[0] = CAAHelper.MapTo0To360Range( rad180+gnGa[0] ) ;
			gnaz[1] = CAAHelper.MapTo0To360Range( rad180+gnGa[1] ) ;
		} else if ( java.lang.Math.sin( blGa )==0 ) {	/* rdST==gnST,
															rdST==gnST+180 */
			blC = java.lang.Math.abs( blA-blB*java.lang.Math.cos( blGa ) ) ;

			// intersection test
			if ( blGa<0?!( gnB>( rdB-blC ) ):!( rdB>( gnB-blC ) ) ) {
				throw new ParameterNotValidException() ;
			}

			if ( blC>0 ) {
				rdDe = rad180-astrolabe.Math.LawOfEdgeCosineSolveAngle( gnB, rdB, blC) ;
				gnDe = astrolabe.Math.LawOfEdgeCosineSolveAngle( rdB, blC, gnB) ;
			} else {
				rdDe = astrolabe.Math.LawOfEdgeCosineSolveAngle( gnB, rdB, blC) ;
				gnDe = rad180-astrolabe.Math.LawOfEdgeCosineSolveAngle( rdB, blC, gnB) ;
			}

			rdGa[0] = -rdDe ;
			rdGa[1] =  rdDe ;
			gnGa[0] = -gnDe ;
			gnGa[1] = gnDe ;

			rdaz[0] = rad180+rdGa[0] ;
			rdaz[1] = rad180+rdGa[1] ;
			gnaz[0] = rad180+gnGa[0] ;
			gnaz[1] = rad180+gnGa[1] ;

		} else {	/* rdST>gnST && abs( gnST-rdST )>180,
						rdST>gnST && abs( gnST-rdST )<180,
						rdST>gnST && abs( gnST-rdST )<90,
						rdST<gnST && abs( gnST-rdST )>180,
						rdST<gnST && abs( gnST-rdST )<180,
						rdST<gnST && abs( gnST-rdST )<90 */
			blC = astrolabe.Math.LawOfEdgeCosine( blA, blB, blGa ) ;
			blAl = astrolabe.Math.LawOfEdgeCosineSolveAngle( blA, blB, blC) ;
			blBe = astrolabe.Math.LawOfEdgeCosineSolveAngle( blB, blC, blA) ;

			// intersection test
			if ( blGa<0?!( gnB>( rdB-blC ) ):!( rdB>( gnB-blC ) ) ) {
				throw new ParameterNotValidException() ;
			}

			rdDe = astrolabe.Math.LawOfEdgeCosineSolveAngle( gnB, rdB, blC) ;
			gnDe = astrolabe.Math.LawOfEdgeCosineSolveAngle( rdB, blC, gnB) ;

			if ( rdDe<( rad180-blBe ) ) {	// aph	
				gnDe = rad180-gnDe ;

				if ( blGa<0 ) {
					rdGa[0] = blBe+rdDe ;
					rdGa[1] = blBe-rdDe ;
					gnGa[0] = rad180-blAl+gnDe ;
					gnGa[1] = rad180-blAl-gnDe ;

					rdaz[0] = rad180-rdGa[0] ;
					rdaz[1] = rad180-rdGa[1] ;
					gnaz[0] = CAAHelper.MapTo0To360Range( rad180-gnGa[0] ) ;
					gnaz[1] = CAAHelper.MapTo0To360Range( rad180-gnGa[1] ) ;
				} else {
					rdGa[0] = blBe-rdDe ;
					rdGa[1] = blBe+rdDe ;
					gnGa[0] = rad180-blAl-gnDe ;
					gnGa[1] = rad180-blAl+gnDe ;

					rdaz[0] = rad180+rdGa[0] ;
					rdaz[1] = rad180+rdGa[1] ;
					gnaz[0] = CAAHelper.MapTo0To360Range( rad180+gnGa[0] ) ;
					gnaz[1] = CAAHelper.MapTo0To360Range( rad180+gnGa[1] ) ;
				}
			} else {						// per
				rdDe = rad180-rdDe ;

				if ( blGa<0 ) {
					rdGa[0] = rad180-blBe+rdDe ;
					rdGa[1] = rad180-blBe-rdDe ;
					gnGa[0] = blAl+gnDe ;
					gnGa[1] = blAl-gnDe ;

					rdaz[0] = rad180+rdGa[0] ;
					rdaz[1] = rad180+rdGa[1] ;
					gnaz[0] = rad180+gnGa[0] ;
					gnaz[1] = rad180+gnGa[1] ;
				} else {
					rdGa[0] = rad180-blBe-rdDe ;
					rdGa[1] = rad180-blBe+rdDe ;
					gnGa[0] = blAl-gnDe ;
					gnGa[1] = blAl+gnDe ;

					rdaz[0] = rad180-rdGa[0] ;
					rdaz[1] = rad180-rdGa[1] ;
					gnaz[0] = rad180-gnGa[0] ;
					gnaz[1] = rad180-gnGa[1] ;
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
		double rad90 ;
		double[] inaz ;

		rad90 = CAACoordinateTransformation.DegreesToRadians( 90 ) ;

		rdB = rad90-al ;
		gnB = rad90-gn.al ;
		blA = rad90-horizon.getLa() ;
		blB = rad90-gn.horizon.getLa() ;
		blGa = gn.horizon.getST()-horizon.getST() ;

		inaz = CircleParallel.intersection( rdB, gnB, blA, blB, blGa ) ;

		// unconvert local rd into actual
		rdeqA = CAAHelper.Horizontal2Equatorial( inaz[0], al, horizon.getLa() ) ;
		rdeqA[0] = horizon.getST()-rdeqA[0] ;
		rdhoA = horizon.unconvert( rdeqA ) ;

		rdeqO = CAAHelper.Horizontal2Equatorial( inaz[1], al, horizon.getLa() ) ;
		rdeqO[0] = horizon.getST()-rdeqO[0] ;
		rdhoO = horizon.unconvert( rdeqO ) ;

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
		gneq = CAAHelper.Horizontal2Equatorial( leading?inaz[3]:inaz[2], gn.al, gn.horizon.getLa() ) ;
		gneq[0] = gn.horizon.getST()-gneq[0] ;
		gnho = gn.horizon.unconvert( gneq ) ;

		if ( ! gn.examine( gnho[0] ) ) {
			throw new ParameterNotValidException() ;
		}

		return leading?rdhoO[0]:rdhoA[0] ;
	}

	public double intersect( CircleMeridian gn, boolean leading ) throws ParameterNotValidException {
		double blA, blB, blGa, rdB ;
		double[] rdeqA, rdeqO, rdhoA, rdhoO ;
		double rad90 ;
		double inaz[], gnal ;

		rad90 = CAACoordinateTransformation.DegreesToRadians( 90 ) ;

		rdB = rad90-al ;
		blA = rad90-horizon.getLa() ;
		blB = rad90-gn.transformParallelLa() ;
		blGa = gn.transformParallelST()-horizon.getST() ;

		inaz = CircleParallel.intersection( rdB, rad90, blA, blB, blGa ) ;

		// unconvert local rd into actual
		rdeqA = CAAHelper.Horizontal2Equatorial( inaz[0], al, horizon.getLa() ) ;
		rdeqA[0] = horizon.getST()-rdeqA[0] ;
		rdhoA = horizon.unconvert( rdeqA ) ;

		rdeqO = CAAHelper.Horizontal2Equatorial( inaz[1], al, horizon.getLa() ) ;
		rdeqO[0] = horizon.getST()-rdeqO[0] ;
		rdhoO = horizon.unconvert( rdeqO ) ;

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

		if ( ! gn.examine( gnal ) ) {
			throw new ParameterNotValidException() ;
		}

		return leading?rdhoO[0]:rdhoA[0] ;
	}

	public double span0Distance( double span ) {
		double r ;

		try {
			r =  spanNDistance( span, 0 ) ;
		} catch ( ParameterNotValidException e ) {
			r = 0 ;
		}

		return r ;
	}

	public double spanNDistance( double span, int n ) throws ParameterNotValidException {
		double s, r ;

		s = java.lang.Math.abs( Math.remainder( begin, span ) ) ;
		r = begin+( begin>0?span-s:s )+n*span ;

		if ( r>end ) {
			throw new ParameterNotValidException() ;
		}

		return r ;
	}
}
