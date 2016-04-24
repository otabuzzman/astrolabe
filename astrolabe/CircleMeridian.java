
package astrolabe;

import java.util.Vector;

import caa.CAACoordinateTransformation;

public class CircleMeridian implements Circle {

	private Horizon horizon ;

	private double segment ;
	private double linewidth ;

	private double az ;

	private double begin ;
	private double end ;

	public CircleMeridian( astrolabe.model.CircleType clT, Horizon horizon ) throws ParameterNotValidException {
		String key ;

		segment = caa.CAACoordinateTransformation.DegreesToRadians(
				ApplicationHelper.getClassNode( this, clT.getName(), null ).getDouble( ApplicationConstant.PK_CIRCLE_SEGMENT, 1 ) ) ;
		linewidth = ApplicationHelper.getClassNode( this, clT.getName(), ApplicationConstant.PN_CIRCLE_IMPORTANCE ).getDouble( clT.getImportance(), .1 ) ;

		this.horizon = horizon ;

		try {
			az = AstrolabeFactory.valueOf( clT.getAngle() ) ;
			key = Astrolabe.getLocalizedString( ApplicationConstant.LK_CIRCLE_AZIMUTH ) ;
			ApplicationHelper.registerDMS( key, az, 2 ) ;
		} catch ( ParameterNotValidException e ) {}
		try {
			begin = AstrolabeFactory.valueOf( clT.getBegin().getImmediate() ) ;
		} catch ( ParameterNotValidException e ) {
			Registry registry ;
			Circle circle ;
			boolean leading ;

			registry = new Registry() ;
			circle = (Circle) registry.retrieve( clT.getBegin().getReference().getCircle() ) ;

			leading = clT.getBegin().getReference().getNode().equals( ApplicationConstant.AV_CIRCLE_LEADING ) ;

			begin = circle.isParallel()?
					intersect( (CircleParallel) circle, leading ):
						intersect( (CircleMeridian) circle, leading ) ;
		}
		try {
			end = AstrolabeFactory.valueOf( clT.getEnd().getImmediate() ) ;
		} catch ( ParameterNotValidException e ) {
			Registry registry ;
			Circle circle ;
			boolean leading ;

			registry = new Registry() ;
			circle = (Circle) registry.retrieve( clT.getEnd().getReference().getCircle() ) ;

			leading = clT.getBegin().getReference().getNode().equals( ApplicationConstant.AV_CIRCLE_LEADING ) ;

			end = circle.isParallel()?
					intersect( (CircleParallel) circle, leading ):
						intersect( (CircleMeridian) circle, leading ) ;
		}
	}

	public astrolabe.Vector cartesian( double al, double shift ) throws ParameterNotValidException {
		astrolabe.Vector r ;
		double rad90 ;

		if ( ! examine( al ) ) {
			throw new ParameterNotValidException() ;
		}

		rad90 = CAACoordinateTransformation.DegreesToRadians( 90 ) ;

		r = horizon.dotDot()/*Chart*/.project( horizon.convert( az, al ) ) ;
		if ( shift != 0 ) {
			r.add( tangentVector( al ).rotate( shift<0?-rad90:rad90 ).size( java.lang.Math.abs( shift ) ) ) ;
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
		double s, al ;

		r = new Vector<astrolabe.Vector>() ;

		a = cartesian( begin, shift ) ;
		r.add( a ) ;

		s = java.lang.Math.abs( astrolabe.Math.remainder( begin, segment ) ) ;
		for ( al=begin+( s>0?s/2:segment/2 ) ; al<end ; al=al+segment ) {
			a = cartesian( al, shift ) ;
			r.add( a ) ;
		}

		a = cartesian( end, shift ) ;
		r.add( a ) ;

		return r ;
	}

	public double tangentAngle( double al ) throws ParameterNotValidException {
		astrolabe.Vector t ;
		double r ;

		t = tangentVector( al ) ;
		r = java.lang.Math.atan2( t.getY(), t.getX() ) ;

		return r ;
	}

	public astrolabe.Vector tangentVector( double al ) throws ParameterNotValidException {
		astrolabe.Vector r ;
		double d ;

		d = CAACoordinateTransformation.DegreesToRadians( 10./3600 ) ;
		r = horizon.dotDot()/*Chart*/.project( horizon.convert( az, al+d ) ).sub( horizon.dotDot()/*Chart*/.project( horizon.convert( az, al ) ) ) ;

		return r ;
	}

	public void initPS( PostscriptStream ps ) {
		ps.operator.setlinewidth( linewidth ) ;
	}

	public boolean isParallel() {
		return false ;
	}

	public boolean isMeridian() {
		return true ;
	}

	public boolean isClosed() {
		double b, e ;

		b = ApplicationHelper.MapTo0To360Range( begin ) ;
		e = ApplicationHelper.MapTo0To360Range( end ) ;

		return e==b||e>b ;
	}

	public boolean examine( double al ) {
		double b, e ;
		double v ;

		b = ApplicationHelper.MapTo0To360Range( begin ) ;
		e = ApplicationHelper.MapTo0To360Range( end ) ;
		v = ApplicationHelper.MapTo0To360Range( al ) ;

		return b>e?v>=b||v<=e:v>=b&&v<=e ;
	}

	private double[] transform() {
		double[] r = new double[3] ;
		double blB, vlA, vlD, vlAl, vlDe ;
		double gneq[], gnho[], gnaz ;
		double rad90, rad180 ;

		rad90 = CAACoordinateTransformation.DegreesToRadians( 90 ) ;
		rad180 = CAACoordinateTransformation.DegreesToRadians( 180 ) ;

		blB = rad90-horizon.getLa() ;
		if ( blB==0 ) {	// prevent infinity from tan
			blB = Math.e0 ;
		}

		// convert actual gn into local
		gneq = horizon.convert( this.az, 0 ) ;
		gnho = ApplicationHelper.Equatorial2Horizontal( horizon.getST()-gneq[0], gneq[1], horizon.getLa() ) ;
		gnaz = gnho[0] ;
		if ( gnaz==0 ) {	// prevent infinity from tan
			gnaz = Math.e0 ;
		}


		if ( java.lang.Math.tan( gnaz )>0 ) {	// QI, QIII
			vlAl = ApplicationHelper.MapTo0To90Range( gnaz ) ;

			// Rule of Napier : cos(90-a) = sin(al)*sin(c)
			vlA = java.lang.Math.acos( java.lang.Math.sin( vlAl )*java.lang.Math.sin( blB ) ) ;
			// Rule of Napier : cos(be) = cot(90-a)*cot(c)
			vlDe = java.lang.Math.acos( Math.truncate( Math.cot( vlA )*Math.cot( blB ) ) ) ;

			r[1] = horizon.getST()-rad180+vlDe ;
		} else {							// QII, QIV
			vlAl = rad90-ApplicationHelper.MapTo0To90Range( gnaz ) ;

			vlA = java.lang.Math.acos( java.lang.Math.sin( vlAl )*java.lang.Math.sin( blB ) ) ;
			vlDe = java.lang.Math.acos( Math.truncate( Math.cot( vlA )*Math.cot( blB ) ) ) ;

			r[1] = horizon.getST()+rad180-vlDe ;
		}

		r[0] = rad90-vlA ;

		// Rule of Napier : cos(c) = sin(90-a)*sin(90-b)
		// sin(90-b) = cos(c):sin(90-a)
		vlD = rad90-java.lang.Math.asin( Math.truncate( java.lang.Math.cos( blB )/java.lang.Math.sin( vlA ) ) ) ;

		r[2] = vlD ;

		return r ;
	}

	public double transformParallelLa() {
		return transform()[0] ;
	}

	public double transformParallelST() {
		return transform()[1] ;
	}

	public double transformMeridianAl( double vlaz ) {
		double gneq[], gnho[], gnaz, gnal, vlD ;
		double rad90, rad180, rad270, rad360 ;

		rad90 = CAACoordinateTransformation.DegreesToRadians( 90 ) ;
		rad180 = CAACoordinateTransformation.DegreesToRadians( 180 ) ;
		rad270 = CAACoordinateTransformation.DegreesToRadians( 270 ) ;
		rad360 = CAACoordinateTransformation.DegreesToRadians( 360 ) ;

		// convert actual gn into local
		gneq = horizon.convert( this.az, 0 ) ;
		gnho = ApplicationHelper.Equatorial2Horizontal( horizon.getST()-gneq[0], gneq[1], horizon.getLa() ) ;
		gnaz = gnho[0] ;
		if ( gnaz==0 ) {	// prevent infinity from tan
			gnaz = Math.e0 ;
		}

		// convert vl az into gn
		vlD = transform()[2] ;
		gnal = java.lang.Math.tan( gnaz )>0?vlaz-vlD:vlaz+vlD ;
		// convert gn az into al
		gnal = gnaz>rad180?gnal-rad90:-( gnal-rad270 ) ;
		// map range from -180 to 180
		gnal = gnal>rad180?gnal-rad360:gnal ;

		return gnal ;
	}

	public double intersect( CircleParallel gn, boolean leading ) throws ParameterNotValidException {
		double blA, blB, blGa, gnB ;
		double[] gneqA, gneqO, gnhoA, gnhoO ;
		double rad90 ;
		double inaz[], gnaz ;

		rad90 = CAACoordinateTransformation.DegreesToRadians( 90 ) ;

		gnB = rad90-gn.getAl() ;
		blA = rad90-transformParallelLa() ;
		blB = rad90-gn.dotDot().getLa() ;
		blGa = gn.dotDot().getST()-transformParallelST() ;

		inaz = CircleParallel.intersection( rad90, gnB, blA, blB, blGa ) ;

		// convert rd az into al
		inaz[0] = transformMeridianAl( inaz[0] ) ;
		inaz[1] = transformMeridianAl( inaz[1] ) ;

		// unconvert local gn into actual
		gneqA = ApplicationHelper.Horizontal2Equatorial( inaz[2], gn.getAl(), gn.dotDot().getLa() ) ;
		gneqA[0] = horizon.getST()-gneqA[0] ;
		gnhoA = horizon.unconvert( gneqA ) ;

		gneqO = ApplicationHelper.Horizontal2Equatorial( inaz[3], gn.getAl(), gn.dotDot().getLa() ) ;
		gneqO[0] = horizon.getST()-gneqO[0] ;
		gnhoO = horizon.unconvert( gneqO ) ;

		// sort associated values
		if ( inaz[0]>inaz[1] ) {
			double v ;

			v = inaz[0] ;
			inaz[0] = inaz[1] ;
			inaz[1] = v ;

			v = gnhoA[0] ;
			gnhoA[0] = gnhoO[0] ;
			gnhoO[0] = v ;
		}

		gnaz = leading?gnhoO[0]:gnhoA[0] ;

		if ( ! gn.examine( gnaz ) ) {
			throw new ParameterNotValidException() ;
		}

		return leading?inaz[1]:inaz[0] ;
	}

	public double intersect( CircleMeridian gn, boolean leading ) throws ParameterNotValidException {
		double blA, blB, blGa ;
		double rad90 ;
		double inaz[], gnal ;

		rad90 = CAACoordinateTransformation.DegreesToRadians( 90 ) ;

		blA = rad90-transformParallelLa() ;
		blB = rad90-gn.transformParallelLa() ;
		blGa = gn.transformParallelST()-transformParallelST() ;

		inaz = CircleParallel.intersection( rad90, rad90, blA, blB, blGa ) ;

		// convert az into al
		inaz[0] = transformMeridianAl( inaz[0] ) ;
		inaz[1] = transformMeridianAl( inaz[1] ) ;
		inaz[2] = gn.transformMeridianAl( inaz[2] ) ;
		inaz[3] = gn.transformMeridianAl( inaz[3] ) ;

		// sort associated values
		if ( inaz[0]>inaz[1] ) {
			double v ;

			v = inaz[0] ;
			inaz[0] = inaz[1] ;
			inaz[1] = v ;

			v = inaz[2] ;
			inaz[2] = inaz[3] ;
			inaz[3] = v ;
		}

		gnal = leading?inaz[3]:inaz[2] ;

		if ( ! gn.examine( gnal ) ) {
			throw new ParameterNotValidException() ;
		}

		return leading?inaz[1]:inaz[0] ;
	}

	public double distance0( double span ) {
		double r ;

		try {
			r =  distanceN( span, 0 ) ;
		} catch ( ParameterNotValidException e ) {
			r = 0 ;
		}

		return r ;
	}

	public double distanceN( double span, int n ) throws ParameterNotValidException {
		double s, r ;

		s = java.lang.Math.abs( Math.remainder( begin, span ) ) ;
		r = begin+( begin>0?span-s:s )+n*span ;

		if ( r>end ) {
			throw new ParameterNotValidException() ;
		}

		return r ;
	}

	public double getAz() {
		return az ;
	}

	public void setAz( double az ) {
		this.az = az ;
	}

	public Horizon dotDot() {
		return horizon ;
	}
}
