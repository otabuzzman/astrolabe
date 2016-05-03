
package astrolabe;

import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;

import caa.CAA2DCoordinate;
import caa.CAACoordinateTransformation;

@SuppressWarnings("serial")
public class CircleParallel extends astrolabe.model.CircleParallel implements Cloneable, PostscriptEmitter, Baseline, Converter {

	// qualifier key (QK_)
	private final static String QK_ALTITUDE			= "altitude" ;
	private final static String QK_TERMINUS			= "terminus" ;

	private final static Log log = LogFactory.getLog( CircleParallel.class ) ;

	// attribute value (AV_)
	@SuppressWarnings("unused")
	private final static String AV_CHASING			= "chasing" ;
	private final static String AV_LEADING			= "leading" ;

	// configuration key (CK_)
	private final static String CK_INTERVAL			= "interval" ;
	private static final String CK_DISTANCE			= "distance" ;
	private final static String CK_SEGMIN			= "segmin" ;

	private final static String CK_HALO				= "halo" ;
	private final static String CK_HALOMIN			= "halomin" ;
	private final static String CK_HALOMAX			= "halomax" ;

	private final static double DEFAULT_INTERVAL	= 1 ;
	private static final double DEFAULT_DISTANCE	= 0 ;
	private final static int DEFAULT_SEGMIN			= 3 ;

	private final static double DEFAULT_HALO		= 4 ;
	private final static double DEFAULT_HALOMIN		= .08 ;
	private final static double DEFAULT_HALOMAX		= .4 ;

	// message key (MK_)
	private final static String MK_EINTSEC			= "eintsec" ;

	private Converter converter ;
	private Projector projector ;

	public CircleParallel( Converter converter, Projector projector ) {
		this.converter = converter ;
		this.projector = projector ;
	}

	public double begin() {
		double r ;

		if ( getBegin().getAngle() == null ) {
			Object circle ;
			boolean leading ;

			leading = getBegin().getReference().getNode().equals( AV_LEADING ) ;
			try {
				circle = Registry.retrieve( getBegin().getReference().getCircle() ) ;
				r = circle instanceof CircleParallel?
						intersect( (CircleParallel) circle, leading ):
							intersect( (CircleMeridian) circle, leading ) ;
			} catch ( ParameterNotValidException e ) {
				log.warn( e.getMessage() ) ;

				r = 0 ;
			}
		} else {
			r = valueOf( getBegin().getAngle() ) ;
		}

		return CAACoordinateTransformation.MapTo0To360Range( r ) ;
	}

	public double end() {
		double r ;

		if ( getEnd().getAngle() == null ) {
			Object circle ;
			boolean leading ;

			leading = getEnd().getReference().getNode().equals( AV_LEADING ) ;
			try {
				circle = Registry.retrieve( getEnd().getReference().getCircle() ) ;
				r = circle instanceof CircleParallel?
						intersect( (CircleParallel) circle, leading ):
							intersect( (CircleMeridian) circle, leading ) ;
			} catch ( ParameterNotValidException e ) {
				log.warn( e.getMessage() ) ;

				r = 360 ;
			}
		} else {
			r = valueOf( getEnd().getAngle() ) ;
		}
		r = CAACoordinateTransformation.MapTo0To360Range( r ) ;

		return r ;
	}

	public void register() {
		astrolabe.Coordinate currentpoint ;
		double al ;
		DMS dms ;

		currentpoint = new astrolabe.Coordinate( positionOfScaleMarkValue( end(), 0 ) ) ;
		currentpoint.register( this, QK_TERMINUS ) ;

		al = valueOf( getAngle() ) ;
		dms = new DMS( al ) ;
		dms.register( this, QK_ALTITUDE ) ;
	}

	public void degister() {
		astrolabe.Coordinate.degister( this, QK_TERMINUS ) ;

		DMS.degister( this, QK_ALTITUDE ) ;
	}

	public void headPS( ApplicationPostscriptStream ps ) {
		String gstate ;

		if ( ( gstate = Configuration.getValue( this, getImportance(), null ) ) == null )
			return ;
		ps.script( gstate ) ;
	}

	public void emitPS( ApplicationPostscriptStream ps ) {
		Configuration conf ;
		int segmin ;
		CircleParallel base ;
		astrolabe.model.Rational angA, angO ;
		Coordinate[] ccrc, ccut ;
		ChartPage page ;
		Geometry fov, cut, tmp ;
		astrolabe.model.Annotation annotation ;
		astrolabe.model.Dial dial ;
		PostscriptEmitter emitter ;

		conf = new Configuration( this ) ;
		segmin = conf.getValue( CK_SEGMIN, DEFAULT_SEGMIN ) ;

		page = (ChartPage) Registry.retrieve( ChartPage.class.getName() ) ;
		if ( page != null )
			fov = FieldOfView.makeGeometry( page.getViewRectangle(), true ) ;
		else
			fov = null ;

		ccrc = list( begin(), end(), 0 ) ;

		if ( fov == null )
			cut = new GeometryFactory().createLineString( ccrc ) ;
		else {
			tmp = new GeometryFactory().createLineString( ccrc ) ;
			cut = fov.intersection( tmp ) ;
		}

		base = (CircleParallel) clone() ;
		base.setBegin( new astrolabe.model.Begin() ) ;
		base.getBegin().setAngle( new astrolabe.model.Angle() ) ;
		base.getBegin().getAngle().setRational( new astrolabe.model.Rational() ) ;
		base.setEnd( new astrolabe.model.End() ) ;
		base.getEnd().setAngle( new astrolabe.model.Angle() ) ;
		base.getEnd().getAngle().setRational( new astrolabe.model.Rational() ) ;

		angA = base.getBegin().getAngle().getRational() ;
		angO = base.getEnd().getAngle().getRational() ;

		for ( int i=0 ; cut.getNumGeometries()>i ; i++ ) {
			ccut = cut.getGeometryN( i ).getCoordinates() ;

			if ( segmin>ccut.length )
				continue ;

			angA.setValue( converter.convert( projector.project( ccut[0], true ), true ).x ) ;
			angO.setValue( converter.convert( projector.project( ccut[ccut.length-1], true ), true ).x ) ;

			ps.op( "gsave" ) ;

			ps.array( true ) ;
			for ( Coordinate xy : ccut ) {
				ps.push( xy.x ) ;
				ps.push( xy.y ) ;
			}
			ps.array( false ) ;

			ps.op( "newpath" ) ;
			ps.op( "gdraw" ) ;

			// halo stroke
			ps.op( "currentlinewidth" ) ;

			ps.op( "dup" ) ;
			ps.push( 100 ) ;
			ps.op( "div" ) ;
			ps.push( conf.getValue( CK_HALO, DEFAULT_HALO ) ) ; 
			ps.op( "mul" ) ;
			ps.push( conf.getValue( CK_HALOMIN, DEFAULT_HALOMIN ) ) ; 
			ps.op( "max" ) ;
			ps.push( conf.getValue( CK_HALOMAX, DEFAULT_HALOMAX ) ) ; 
			ps.op( "min" ) ;

			ps.push( 2 ) ;
			ps.op( "mul" ) ;
			ps.op( "add" ) ;
			ps.op( "gsave" ) ;
			ps.op( "setlinewidth" ) ;
			ps.push( 2 ) ;
			ps.op( "setlinecap" ) ;
			ps.push( 1 ) ;
			ps.op( "setgray" ) ;
			ps.op( "stroke" ) ;
			ps.op( "grestore" ) ;

			ps.op( "gsave" ) ;
			ps.op( "stroke" ) ;
			ps.op( "grestore" ) ;

			if ( getDial() != null ) {
				dial = getDial() ;

				if ( dial.getDialDegree() != null ) {
					emitter = dial( dial.getDialDegree(), base ) ;
				} else { // dial.getDialHour() != null
					emitter = dial( dial.getDialHour(), base ) ;
				}

				ps.op( "gsave" ) ;

				emitter.headPS( ps ) ;
				emitter.emitPS( ps ) ;
				emitter.tailPS( ps ) ;

				ps.op( "grestore" ) ;
			}

			if ( getAnnotation() != null ) {
				for ( int j=0 ; j<getAnnotationCount() ; j++ ) {
					annotation = getAnnotation( j ) ;

					if ( annotation.getAnnotationStraight() != null ) {
						emitter = annotation( annotation.getAnnotationStraight() ) ;
					} else { // annotation.getAnnotationCurved() != null
						emitter = annotation( annotation.getAnnotationCurved() ) ;
					}

					ps.op( "gsave" ) ;

					emitter.headPS( ps ) ;
					emitter.emitPS( ps ) ;
					emitter.tailPS( ps ) ;

					ps.op( "grestore" ) ;
				}
			}

			ps.op( "grestore" ) ;
		}
	}

	public Object clone() {
		try {
			return super.clone() ;
		} catch ( CloneNotSupportedException e ) {}
		return null ;
	}

	public void tailPS( ApplicationPostscriptStream ps ) {
	}

	public Coordinate positionOfScaleMarkValue( double az, double shift ) {
		Coordinate xy ;
		double al ;
		Vector v, t ;

		al = valueOf( getAngle() ) ;	
		xy = projector.project( converter.convert( new Coordinate( az, al ), false ), false ) ;
		v = new Vector( xy ) ;

		if ( shift != 0 ) {
			xy = directionOfScaleMarkValue( az ) ;
			t = new Vector( xy ) ;
			t.apply( new double[] { 0, -1, 0, 1, 0, 0, 0, 0, 1 } ) ;
			t.scale( shift ) ;
			v.add( t ) ;
		}

		return new Coordinate( v.x, v.y ) ;
	}

	public Coordinate directionOfScaleMarkValue( double az ) {
		Vector a, b ;
		double al ;
		Coordinate xy ;

		al = valueOf( getAngle() ) ;
		xy = projector.project( converter.convert( new Coordinate( az+10./3600, al ), false ), false ) ;
		a = new Vector( xy ) ;
		xy = projector.project( converter.convert( new Coordinate( az, al ), false ), false ) ;
		b = new Vector( xy ) ;

		a.sub( b ) ;

		return new Coordinate( a.x, a.y ) ;
	}

	public double valueOfScaleMarkN( int mark, double span ) {
		return new Wheel360Scale( span, new double[] { begin(), end() } ).markN( mark ) ;
	}

	public Coordinate[] list( double begin, double end, double shift ) {
		List<Coordinate> list ;
		double interval, distance ;

		interval = Configuration.getValue( this, CK_INTERVAL, DEFAULT_INTERVAL ) ;

		list = new java.util.Vector<Coordinate>() ;

		for ( double az=begin ; begin>end?az<360+end:az<end ; az=az+interval )
			list.add( positionOfScaleMarkValue( CAACoordinateTransformation.MapTo0To360Range( az ), shift ) ) ;
		list.add( positionOfScaleMarkValue( end, shift ) ) ;

		distance = Configuration.getValue( this, CK_DISTANCE, DEFAULT_DISTANCE ) ;
		if ( distance>0 && list.size()>2 )
			return DouglasPeuckerSimplifier.simplify( new GeometryFactory().createLineString( list.toArray( new Coordinate[0] ) ), distance ).getCoordinates() ;
		else
			return list.toArray( new Coordinate[0] ) ;
	}

	public Coordinate convert( Coordinate local, boolean inverse ) {
		if ( inverse )
			return converter.convert( local, true ) ;
		return converter.convert( new Coordinate( local.x, valueOf( getAngle() ) ), false ) ;
	}

	public static double[] intersection( double rdB, double gnB, double blA, double blB, double blGa ) {
		double blC, blAl, blBe ;
		double rdGa[], rdDe ;
		double gnGa[], gnDe ;
		double[] rdaz, gnaz ;
		double[] inaz ;

		rdGa = new double[2] ;
		gnGa = new double[2] ;
		rdaz = new double[2] ;
		gnaz = new double[2] ;

		inaz = new double[] { 0, 0, 0, 0 } ;

		// i. rdST<gnST && abs( gnST-rdST )>180 is equivalent to rdST>gnST && abs ( gnST-rdST )<180
		// ii. rdST>gnST && abs( gnST-rdST )>180 is equivalent to rdST<gnST && abs ( gnST-rdST )<180
		if ( 180<blGa ) {			// case i.
			blGa = -( 2*180-blGa ) ;
		} else if ( -180>blGa ) {	// case ii.
			blGa = blGa+2*180 ;
		}

		if ( blA==0 ) {	// rdLa==90
			blC = blB ;

			// intersection test
			if ( blGa<0?!( gnB>( rdB-blC ) ):!( rdB>( gnB-blC ) ) )
				return inaz ;

			rdDe = 180-Math.lawOfEdgeCosineSolveAngle( gnB, rdB, blC) ;
			gnDe = Math.lawOfEdgeCosineSolveAngle( rdB, blC, gnB) ;

			rdGa[0] = -( rdDe+blGa ) ;
			rdGa[1] = rdDe-blGa ;
			gnGa[0] = -gnDe ;
			gnGa[1] = gnDe ;

			rdaz[0] = 180+rdGa[0] ;
			rdaz[1] = 180+rdGa[1] ;
			gnaz[0] = CAACoordinateTransformation.MapTo0To360Range( 180+gnGa[0] ) ;
			gnaz[1] = CAACoordinateTransformation.MapTo0To360Range( 180+gnGa[1] ) ;
		} else if ( blB==0 ) {	// gnLa==90
			blC = blA ;

			// intersection test
			if ( blGa<0?!( gnB>( rdB-blC ) ):!( rdB>( gnB-blC ) ) )
				return inaz ;

			rdDe = Math.lawOfEdgeCosineSolveAngle( gnB, rdB, blC) ;
			gnDe = 180-Math.lawOfEdgeCosineSolveAngle( rdB, blC, gnB) ;

			rdGa[0] = -rdDe ;
			rdGa[1] = rdDe ;
			gnGa[0] = -( gnDe-blGa ) ;
			gnGa[1] = gnDe+blGa ;

			rdaz[0] = 180+rdGa[0] ;
			rdaz[1] = 180+rdGa[1] ;
			gnaz[0] = CAACoordinateTransformation.MapTo0To360Range( 180+gnGa[0] ) ;
			gnaz[1] = CAACoordinateTransformation.MapTo0To360Range( 180+gnGa[1] ) ;
		} else if ( Math.sin( blGa )==0 ) {	/* rdST==gnST, rdST==gnST+180 */
			blC = java.lang.Math.abs( blA-blB*Math.cos( blGa ) ) ;

			// intersection test
			if ( blGa<0?!( gnB>( rdB-blC ) ):!( rdB>( gnB-blC ) ) )
				return inaz ;

			if ( blC>0 ) {
				rdDe = 180-Math.lawOfEdgeCosineSolveAngle( gnB, rdB, blC) ;
				gnDe = Math.lawOfEdgeCosineSolveAngle( rdB, blC, gnB) ;
			} else {
				rdDe = Math.lawOfEdgeCosineSolveAngle( gnB, rdB, blC) ;
				gnDe = 180-Math.lawOfEdgeCosineSolveAngle( rdB, blC, gnB) ;
			}

			rdGa[0] = -rdDe ;
			rdGa[1] =  rdDe ;
			gnGa[0] = -gnDe ;
			gnGa[1] = gnDe ;

			rdaz[0] = 180+rdGa[0] ;
			rdaz[1] = 180+rdGa[1] ;
			gnaz[0] = 180+gnGa[0] ;
			gnaz[1] = 180+gnGa[1] ;

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
			if ( blGa<0?!( gnB>( rdB-blC ) ):!( rdB>( gnB-blC ) ) )
				return inaz ;

			rdDe = Math.lawOfEdgeCosineSolveAngle( gnB, rdB, blC) ;
			gnDe = Math.lawOfEdgeCosineSolveAngle( rdB, blC, gnB) ;

			if ( rdDe<( 180-blBe ) ) { // aph	
				gnDe = 180-gnDe ;

				if ( blGa<0 ) {
					rdGa[0] = blBe+rdDe ;
					rdGa[1] = blBe-rdDe ;
					gnGa[0] = 180-blAl+gnDe ;
					gnGa[1] = 180-blAl-gnDe ;

					rdaz[0] = 180-rdGa[0] ;
					rdaz[1] = 180-rdGa[1] ;
					gnaz[0] = CAACoordinateTransformation.MapTo0To360Range( 180-gnGa[0] ) ;
					gnaz[1] = CAACoordinateTransformation.MapTo0To360Range( 180-gnGa[1] ) ;
				} else {
					rdGa[0] = blBe-rdDe ;
					rdGa[1] = blBe+rdDe ;
					gnGa[0] = 180-blAl-gnDe ;
					gnGa[1] = 180-blAl+gnDe ;

					rdaz[0] = 180+rdGa[0] ;
					rdaz[1] = 180+rdGa[1] ;
					gnaz[0] = CAACoordinateTransformation.MapTo0To360Range( 180+gnGa[0] ) ;
					gnaz[1] = CAACoordinateTransformation.MapTo0To360Range( 180+gnGa[1] ) ;
				}
			} else { // per
				rdDe = 180-rdDe ;

				if ( blGa<0 ) {
					rdGa[0] = 180-blBe+rdDe ;
					rdGa[1] = 180-blBe-rdDe ;
					gnGa[0] = blAl+gnDe ;
					gnGa[1] = blAl-gnDe ;

					rdaz[0] = 180+rdGa[0] ;
					rdaz[1] = 180+rdGa[1] ;
					gnaz[0] = 180+gnGa[0] ;
					gnaz[1] = 180+gnGa[1] ;
				} else {
					rdGa[0] = 180-blBe-rdDe ;
					rdGa[1] = 180-blBe+rdDe ;
					gnGa[0] = blAl-gnDe ;
					gnGa[1] = blAl+gnDe ;

					rdaz[0] = 180-rdGa[0] ;
					rdaz[1] = 180-rdGa[1] ;
					gnaz[0] = 180-gnGa[0] ;
					gnaz[1] = 180-gnGa[1] ;
				}
			}
		}

		inaz[0] = rdaz[0] ;
		inaz[1] = rdaz[1] ;
		inaz[2] = gnaz[0] ;
		inaz[3] = gnaz[1] ;

		return inaz ;
	}

	public double intersect( CircleParallel gn, boolean leading ) throws ParameterNotValidException {
		CAA2DCoordinate cA, cO, c ;
		double blA, blB, blGa, rdB, gnB ;
		double rdST, rdLa, gnST, gnLa ;
		Coordinate rdhoC, gnhoC, rdeqA, rdeqO, rdhoA, rdhoO ;
		Coordinate gneq, gnho ;
		double inaz[], gnaz, al, gnal ;
		double gnb, gne ;
		MessageCatalog cat ;
		StringBuffer msg ;
		String fmt ;

		al = valueOf( getAngle() ) ;
		gnal = valueOf( gn.getAngle() ) ;

		rdhoC = zenit() ;
		rdST = rdhoC.x ;
		rdLa = rdhoC.y ;
		gnhoC = gn.zenit() ;
		gnST = gnhoC.x ;
		gnLa = gnhoC.y ;

		rdB = 90-al ;
		gnB = 90-gnal ;
		blA = 90-rdLa ;
		blB = 90-gnLa ;
		blGa = gnST-rdST ;

		inaz = CircleParallel.intersection( rdB, gnB, blA, blB, blGa ) ;

		// unconvert local rd into actual
		cA = CAACoordinateTransformation.Horizontal2Equatorial( inaz[0], al, rdLa/*horizon.getLa()*/ ) ;
		rdeqA = new Coordinate( rdST-CAACoordinateTransformation.HoursToDegrees( cA.X() ), cA.Y() ) ;
		rdhoA = converter.convert( rdeqA, true ) ;

		cO = CAACoordinateTransformation.Horizontal2Equatorial( inaz[1], al, rdLa ) ;
		rdeqO = new Coordinate( rdST-CAACoordinateTransformation.HoursToDegrees( cO.X() ), cO.Y() ) ;
		rdhoO = converter.convert( rdeqO, true ) ;

		// sort associated values
		if ( rdhoA.x>rdhoO.x ) {
			double v ;

			v = rdhoA.x ;
			rdhoA.x = rdhoO.x ;
			rdhoO.x = v ;

			v = inaz[2] ;
			inaz[2] = inaz[3] ;
			inaz[3] = v ;
		}

		// unconvert local gn into actual
		c = CAACoordinateTransformation.Horizontal2Equatorial( leading?inaz[3]:inaz[2], gnal, gnLa ) ;
		gneq = new Coordinate( gnST-CAACoordinateTransformation.HoursToDegrees( c.X() ), c.Y() ) ;
		gnho = gn.converter.convert( gneq, true ) ;

		gnb = gn.begin() ;
		gne = gn.end() ;

		gnaz = CAACoordinateTransformation.MapTo0To360Range( gnho.x ) ;
		if ( !( gnb>gne ? gnaz>=gnb || gnaz<=gne : gnaz>=gnb && gnaz<=gne ) ) {
			cat = new MessageCatalog( this ) ;
			fmt = cat.message( MK_EINTSEC, null ) ;
			if ( fmt != null ) {
				msg = new StringBuffer() ;
				msg.append( MessageFormat.format( fmt, new Object[] { gn.getClass().getSimpleName()+'.'+'<'+gn.getName()+'>' } ) ) ;
			} else
				msg = null ;

			throw new ParameterNotValidException( ParameterNotValidError.errmsg( getClass().getSimpleName()+'.'+'<'+getName()+'>', msg.toString() ) ) ;
		}

		return leading?rdhoO.x:rdhoA.x ;
	}

	public double intersect( CircleMeridian gn, boolean leading ) throws ParameterNotValidException {
		CAA2DCoordinate cA, cO ;
		double blA, blB, blGa, rdB ;
		double rdST, rdLa ;
		Coordinate rdhoC, rdeqA, rdeqO, rdhoA, rdhoO ;
		double inaz[], al, gnal ;
		double gnb, gne ;
		MessageCatalog cat ;
		StringBuffer msg ;
		String fmt ;

		al = valueOf( getAngle() ) ;

		rdhoC = zenit() ;
		rdST = rdhoC.x ;
		rdLa = rdhoC.y ;

		rdB = 90-al ;
		blA = 90-rdLa ;
		blB = 90-gn.transformParallelLa() ;
		blGa = gn.transformParallelST()-rdST ;

		inaz = CircleParallel.intersection( rdB, 90, blA, blB, blGa ) ;

		// unconvert local rd into actual
		cA = CAACoordinateTransformation.Horizontal2Equatorial( inaz[0], al, rdLa ) ;
		rdeqA = new Coordinate( rdST-CAACoordinateTransformation.HoursToDegrees( cA.X() ), cA.Y() ) ;
		rdhoA = converter.convert( rdeqA, true ) ;

		cO = CAACoordinateTransformation.Horizontal2Equatorial( inaz[1], al, rdLa ) ;
		rdeqO = new Coordinate( rdST-CAACoordinateTransformation.HoursToDegrees( cO.X() ), cO.Y() ) ;
		rdhoO = converter.convert( rdeqO, true ) ;

		// sort associated values
		if ( rdhoA.x>rdhoO.x ) {
			double v ;

			v = rdhoA.x ;
			rdhoA.x = rdhoO.x ;
			rdhoO.x = v ;

			v = inaz[2] ;
			inaz[2] = inaz[3] ;
			inaz[3] = v ;
		}

		gnal = leading?gn.transformMeridianAl( inaz[3] ):gn.transformMeridianAl( inaz[2] ) ;

		gnb = gn.begin() ;
		gne = gn.end() ;

		if ( gnb>gne ? gnal>=gne && gnal<=gnb : gnal>=gnb && gnal<=gne ) {
			cat = new MessageCatalog( this ) ;
			fmt = cat.message( MK_EINTSEC, null ) ;
			if ( fmt != null ) {
				msg = new StringBuffer() ;
				msg.append( MessageFormat.format( fmt, new Object[] { gn.getClass().getSimpleName()+'.'+'<'+gn.getName()+'>' } ) ) ;
			} else
				msg = null ;

			throw new ParameterNotValidException( ParameterNotValidError.errmsg( getClass().getSimpleName()+'.'+'<'+getName()+'>', msg.toString() ) ) ;
		}

		return leading?rdhoO.x:rdhoA.x ;
	}

	public Coordinate zenit() {
		return converter.convert( new Coordinate( 0, 90 ), false ) ;
	}

	private PostscriptEmitter annotation( astrolabe.model.AnnotationStraight peer ) {
		AnnotationStraight annotation ;

		annotation = new AnnotationStraight() ;
		peer.copyValues( annotation ) ;

		return annotation ;
	}

	private PostscriptEmitter annotation( astrolabe.model.AnnotationCurved peer ) {
		AnnotationCurved annotation ;

		annotation = new AnnotationCurved() ;
		peer.copyValues( annotation ) ;

		return annotation ;
	}

	private PostscriptEmitter dial( astrolabe.model.DialDegree peer, Baseline base ) {
		DialDegree dial ;

		dial = new DialDegree( base ) ;
		peer.copyValues( dial ) ;

		return dial ;
	}

	private PostscriptEmitter dial( astrolabe.model.DialHour peer, Baseline base ) {
		DialHour dial ;

		dial = new DialHour( base ) ;
		peer.copyValues( dial ) ;

		return dial ;
	}
}
