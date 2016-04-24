
package astrolabe;

import java.util.Vector;

import caa.CAACoordinateTransformation;

public class CircleParallel extends Model implements Circle {

	private Chart chart ;
	protected Horizon horizon ;

	private double segment ;
	private double linewidth ;

	protected double al ;

	private boolean ascending ;

	private double begin ;
	private double end ;

	public CircleParallel( astrolabe.model.CircleType clT, Chart chart, Horizon horizon ) throws ParameterNotValidException, ParameterNotPlausibleException {

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

			registry = new Registry() ;
			circle = (Circle) registry.retrieve( Model.condense( clT.getBegin().getReference() ) ) ;

			if ( circle.isParallel() ) {
				try {
					ascending = clT.getBegin().getReference().getNode().equals( "ascending" ) ? true : /*"descending"*/ false ;
				} catch ( NullPointerException n ) {
					throw new ParameterNotPlausibleException() ;
				}
			}

			begin = intersectionAngle( circle ) ;
		}
		try {
			end = Model.condense( clT.getEnd().getImmediate() ) ;
		} catch ( ParameterNotValidException e ) {
			Registry registry ;
			Circle circle ;

			registry = new Registry() ;
			circle = (Circle) registry.retrieve( Model.condense( clT.getEnd().getReference() ) ) ;

			if ( circle.isParallel() ) {
				try {
					ascending = clT.getEnd().getReference().getNode().equals( "ascending" ) ? true : /*"descending"*/ false ;
				} catch ( NullPointerException n ) {
					throw new ParameterNotPlausibleException() ;
				}
			}

			end = intersectionAngle( circle ) ;
		}
	}

	public astrolabe.Vector cartesian( double az, double shift ) throws ParameterNotValidException {
		astrolabe.Vector r ;
		double rad90 ;

		if ( begin>end?( az<begin&&az>end ):( az<begin||az>end )  ) {
			throw new ParameterNotValidException() ;
		}

		rad90 = CAACoordinateTransformation.DegreesToRadians( 90 ) ;

		r = chart.project( horizon.convert( az, al ) ) ;
		if ( shift != 0 ) {
			r.add( tangentVector( az ).rotate( shift<0?-rad90:rad90 ).size( java.lang.Math.abs( shift ) ) ) ;
		}

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

		if ( begin<this.begin || end>this.end ) {
			throw new ParameterNotValidException() ;
		}

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

	private double intersectionAngle( Circle gn ) {
		double r ;

		if ( gn.isMeridian() ) {
			r = intersectMeridian( gn ) ;
		} else { // gn.isParallel()
			r = intersectParallel( gn ) ;
		}

		return r ;
	}

	private double intersectParallel( Circle gn ) {
		double blA, blB, blC, blAl, blGa ;
		double rdC, rdBe ;
		double gnC, gnGa ;
		double rad90, rad180 ;
		double[] rEq, rHo ;

		rad90 = CAACoordinateTransformation.DegreesToRadians( 90 ) ;
		rad180 = CAACoordinateTransformation.DegreesToRadians( 180 ) ;

		blA = rad90-( (CircleParallel) gn ).horizon.getLa() ;
		blB = rad90-horizon.getLa() ;
		blGa = ( (CircleParallel) gn ).horizon.getST()-horizon.getST() ;
		blC = astrolabe.Math.LawOfEdgeCosine( blA, blB, blGa ) ;
		blAl = astrolabe.Math.LawOfEdgeCosineForAlpha( blA, blB, blC) ;

		rdC = rad90-al ;

		gnC = rad90-( (CircleParallel) gn ).al ;
		gnGa = astrolabe.Math.LawOfEdgeCosineForAlpha( gnC, rdC, blC ) ;

		if ( ascending ) {
			rdBe = horizon.isLocal()?rad180+( blAl-gnGa ):rad180-( blAl-gnGa ) ;
		} else { // descending
			rdBe = horizon.isLocal()?rad180+( blAl+gnGa ):rad180-( blAl+gnGa ) ;
		}

		// unconvert local red az into actual
		rEq = CAACoordinateTransformation.Horizontal2Equatorial(
				CAACoordinateTransformation.RadiansToDegrees( rdBe ),
				CAACoordinateTransformation.RadiansToDegrees( al ),
				CAACoordinateTransformation.RadiansToDegrees( horizon.getLa() ) ) ;
		rEq[0] = horizon.getST()-CAACoordinateTransformation.HoursToRadians( rEq[0] ) ;
		rEq[1] = CAACoordinateTransformation.DegreesToRadians( rEq[1] ) ;
		rHo = horizon.unconvert( rEq ) ;

		return rHo[0] ;
	}

	private double intersectMeridian( Circle gn ) {
		double blA, blB, blC, blAl, blBe, blGa ;
		double rdC, rdBe, rdGa ;
		double gnC, gnBe, gnGa ;
		double[] gnEq, gnHo ;
		double rad90, rad180 ;
		double[] rEq, rHo ;

		rad90 = CAACoordinateTransformation.DegreesToRadians( 90 ) ;
		rad180 = CAACoordinateTransformation.DegreesToRadians( 180 ) ;

		blA = rad90-( (CircleMeridian) gn ).horizon.getLa() ;
		blB = rad90-horizon.getLa() ;
		blGa = ( (CircleMeridian) gn ).horizon.getST()-horizon.getST() ;
		blC = astrolabe.Math.LawOfEdgeCosine( blA, blB, blGa ) ;
		blAl = astrolabe.Math.LawOfEdgeCosineForAlpha( blA, blB, blC) ;
		blBe = astrolabe.Math.LawOfEdgeCosineForAlpha( blB, blA, blC) ;

		// convert actual green az into local
		gnEq = horizon.convert( ( (CircleMeridian) gn ).az, 0 ) ;
		gnHo = CAACoordinateTransformation.Equatorial2Horizontal(
				CAACoordinateTransformation.RadiansToHours( horizon.getST()-gnEq[0] ),
				CAACoordinateTransformation.RadiansToDegrees( gnEq[1] ),
				CAACoordinateTransformation.RadiansToDegrees( horizon.getLa() ) ) ;
		gnBe = rad180-CAACoordinateTransformation.DegreesToRadians( gnHo[0] ) ;

		rdC = rad90-al ;
		rdGa = gnBe-blBe ;

		gnC = astrolabe.Math.MethodOfAuxAngle( rdC, blC, rdGa ) ;
		gnGa = astrolabe.Math.LawOfEdgeCosineForAlpha( gnC, rdC, blC ) ;

		rdBe = rad180+( java.lang.Math.cos( ( (CircleMeridian) gn ).az )<0?blAl-gnGa:blAl+gnGa ) ;

		// unconvert local red az into actual
		rEq = CAACoordinateTransformation.Horizontal2Equatorial(
				CAACoordinateTransformation.RadiansToDegrees( rdBe ),
				CAACoordinateTransformation.RadiansToDegrees( al ),
				CAACoordinateTransformation.RadiansToDegrees( horizon.getLa() ) ) ;
		rEq[0] = horizon.getST()-CAACoordinateTransformation.HoursToRadians( rEq[0] ) ;
		rEq[1] = CAACoordinateTransformation.DegreesToRadians( rEq[1] ) ;
		rHo = horizon.unconvert( rEq ) ;

		return rHo[0] ;
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
