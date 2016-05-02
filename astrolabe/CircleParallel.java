
package astrolabe;

import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.xml.ValidationException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

import caa.CAA2DCoordinate;
import caa.CAACoordinateTransformation;

@SuppressWarnings("serial")
public class CircleParallel extends astrolabe.model.CircleParallel implements PostscriptEmitter, Baseline {

	// qualifier key (QK_)
	private final static String QK_ALTITUDE			= "altitude" ;
	private final static String QK_TERMINUS			= "terminius" ;

	private final static Log log = LogFactory.getLog( CircleParallel.class ) ;

	// attribute value (AV_)
	@SuppressWarnings("unused")
	private final static String AV_CHASING			= "chasing" ;
	private final static String AV_LEADING			= "leading" ;

	// configuration key (CK_)
	private final static String CK_INTERVAL			= "interval" ;

	private final static String CK_HALO				= "halo" ;
	private final static String CK_HALOMIN			= "halomin" ;
	private final static String CK_HALOMAX			= "halomax" ;

	private final static double DEFAULT_INTERVAL	= 1 ;

	private final static double DEFAULT_HALO		= 4 ;
	private final static double DEFAULT_HALOMIN		= .08 ;
	private final static double DEFAULT_HALOMAX		= .4 ;

	// message key (MK_)
	private final static String MK_EINTSEC			= "eintsec" ;

	private Projector projector ;

	public CircleParallel( Projector projector ) {
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
		Point point ;
		double al ;
		DMS dms ;

		point = new Point( convert( end() ) ) ;
		point.register( this, QK_TERMINUS ) ;

		al = valueOf( getAngle() ) ;
		dms = new DMS( al ) ;
		dms.register( this, QK_ALTITUDE ) ;
	}

	public void degister() {
		Point.degister( this, QK_TERMINUS ) ;

		DMS.degister( this, QK_ALTITUDE ) ;
	}

	public void headPS( ApplicationPostscriptStream ps ) {
		String gstate ;

		gstate = Configuration.getValue( this, getImportance(), null ) ;	

		if ( gstate == null || gstate.length() == 0 )
			return ;

		for ( String token : gstate.trim().split( "\\p{Space}+" ) )
			ps.push( token ) ;
	}

	public void emitPS( ApplicationPostscriptStream ps ) {
		emitPS( ps, true ) ;
	}

	private void emitPS( ApplicationPostscriptStream ps, boolean cut ) {
		Configuration conf ;
		ListCutter cutter ;
		List<List<double[]>> segment ;
		List<double[]> list ;
		ChartPage page ;
		Geometry fov ;
		astrolabe.model.CircleParallel peer ;
		CircleParallel circle ;
		double[] lob, loe, xy ;
		astrolabe.model.Annotation annotation ;
		astrolabe.model.Dial dial ;
		PostscriptEmitter emitter ;

		if ( cut ) {
			list = list( null, begin(), end(), 0 ) ;
			page = (ChartPage) Registry.retrieve( ChartPage.RK_CHARTPAGE ) ;

			if ( page == null ) {
				emitPS( ps, false ) ;

				return ;
			}

			fov = page.getViewGeometry() ;
			cutter = new ListCutter( list, fov ) ;
			segment = cutter.segmentsIntersecting( true ) ;

			for ( List<double[]> s : segment ) {
				lob = projector.unproject( s.get( 0 ) ) ;
				xy = s.get( s.size()-1 ) ;
				loe = projector.unproject( xy ) ; 

				try {
					peer = new astrolabe.model.CircleParallel() ;
					peer.setName( getName() ) ;

					peer.setImportance( getImportance() ) ;

					// astrolabe.model.AngleType
					peer.setAngle( new astrolabe.model.Angle() ) ;
					peer.getAngle().setRational( new astrolabe.model.Rational() ) ;
					peer.getAngle().getRational().setValue( valueOf( getAngle() ) ) ;

					peer.setBegin( new astrolabe.model.Begin() ) ;
					// astrolabe.model.AngleType
					peer.getBegin().setAngle( new astrolabe.model.Angle() ) ;
					peer.getBegin().getAngle().setRational( new astrolabe.model.Rational() ) ;
					peer.getBegin().getAngle().getRational().setValue( lob[0] ) ;
					peer.setEnd( new astrolabe.model.End() ) ;
					// astrolabe.model.AngleType
					peer.getEnd().setAngle( new astrolabe.model.Angle() ) ;
					peer.getEnd().getAngle().setRational( new astrolabe.model.Rational() ) ;
					peer.getEnd().getAngle().getRational().setValue( loe[0] ) ;
					if ( getName() != null ) {
					}

					peer.setDial( getDial() ) ;
					peer.setAnnotation( getAnnotation() ) ;

					peer.validate() ;
				} catch ( ValidationException e ) {
					throw new RuntimeException( e.toString() ) ;
				}

				circle = new CircleParallel( projector ) ;
				peer.copyValues( circle ) ;

				circle.register() ;
				ps.operator.gsave() ;

				circle.headPS( ps ) ;
				circle.emitPS( ps, false ) ;
				circle.tailPS( ps ) ;

				ps.operator.grestore() ;
				circle.degister() ;
			}
		} else {
			list = list( null, begin(), end(), 0 ) ;
			ps.array( true ) ;
			for ( int n=0 ; n<list.size() ; n++ ) {
				xy = list.get( n ) ;
				ps.push( xy[0] ) ;
				ps.push( xy[1] ) ;
			}
			ps.array( false ) ;

			ps.operator.newpath() ;
			ps.gdraw() ;

			// halo stroke
			ps.operator.currentlinewidth() ;

			ps.operator.dup() ;
			ps.operator.div( 100 ) ;
			conf = new Configuration( this ) ;
			ps.push( conf.getValue( CK_HALO, DEFAULT_HALO ) ) ; 
			ps.operator.mul() ;
			ps.push( conf.getValue( CK_HALOMIN, DEFAULT_HALOMIN ) ) ; 
			ps.max() ;
			ps.push( conf.getValue( CK_HALOMAX, DEFAULT_HALOMAX ) ) ; 
			ps.min() ;

			ps.operator.mul( 2 ) ;
			ps.operator.add() ;
			ps.operator.gsave() ;
			ps.operator.setlinewidth() ;
			ps.operator.setlinecap( 2 ) ;
			ps.operator.setgray( 1 ) ;
			ps.operator.stroke() ;
			ps.operator.grestore() ;

			ps.operator.gsave() ;
			ps.operator.stroke() ;
			ps.operator.grestore() ;

			if ( getDial() != null ) {
				dial = getDial() ;

				if ( dial.getDialDegree() != null ) {
					emitter = dial( dial.getDialDegree() ) ;
				} else { // dial.getDialHour() != null
					emitter = dial( dial.getDialHour() ) ;
				}

				ps.operator.gsave() ;

				emitter.headPS( ps ) ;
				emitter.emitPS( ps ) ;
				emitter.tailPS( ps ) ;

				ps.operator.grestore() ;
			}

			if ( getAnnotation() != null ) {
				for ( int i=0 ; i<getAnnotationCount() ; i++ ) {
					annotation = getAnnotation( i ) ;

					if ( annotation.getAnnotationStraight() != null ) {
						emitter = annotation( annotation.getAnnotationStraight() ) ;
					} else { // annotation.getAnnotationCurved() != null
						emitter = annotation( annotation.getAnnotationCurved() ) ;
					}

					ps.operator.gsave() ;

					emitter.headPS( ps ) ;
					emitter.emitPS( ps ) ;
					emitter.tailPS( ps ) ;

					ps.operator.grestore() ;
				}
			}
		}
	}

	public void tailPS( ApplicationPostscriptStream ps ) {
	}

	public double[] project( double az, double shift ) {
		double xy[], al ;
		Vector v, t ;

		al = valueOf( getAngle() ) ;	
		xy = projector.project( az, al ) ;
		v = new Vector( xy[0], xy[1] ) ;

		if ( shift != 0 ) {
			xy = tangent( az ) ;
			t = new Vector( xy[0], xy[1] ) ;
			t.apply( new double[] { 0, -1, 0, 1, 0, 0, 0, 0, 1 } ) ;
			t.scale( shift ) ;
			v.add( t ) ;
		}

		return new double[] { v.x, v.y } ;
	}

	public double[] convert( double angle ) {
		return projector.convert( angle, valueOf( getAngle() ) ) ;
	}

	public double unconvert( double[] eq ) {
		return projector.unconvert( eq )[0] ;
	}

	public double[] tangent( double az ) {
		Vector a, b ;
		double xy[], al ;

		al = valueOf( getAngle() ) ;
		xy = projector.project( az+10./3600, al ) ;
		a = new Vector( xy[0], xy[1] ) ;
		xy = projector.project( az, al ) ;
		b = new Vector( xy[0], xy[1] ) ;

		a.sub( b ) ;

		return new double[] { a.x, a.y } ;
	}

	public List<double[]> list( List<Double> lista, double begin, double end, double shift ) {
		List<double[]> listxy ;
		double interval ;

		interval = Configuration.getValue( this, CK_INTERVAL, DEFAULT_INTERVAL ) ;

		listxy = new java.util.Vector<double[]>() ;

		for ( double az=begin ; begin>end?az<360+end:az<end ; az=az+interval ) {
			listxy.add( project( CAACoordinateTransformation.MapTo0To360Range( az ), shift ) ) ;
			if ( lista != null )
				lista.add( az ) ;
		}

		listxy.add( project( end, shift ) ) ;
		if ( lista != null )
			lista.add( end ) ;

		return listxy ;
	}

	public LineString getCircleGeometry() {
		List<double[]> list;
		LineString line ;

		list = list( null, begin(), end(), 0 ) ;
		line = new GeometryFactory().createLineString( new JTSCoordinateArraySequence( list ) ) ;

		return line ;
	}

	public double scaleMarkNth( int mark, double span ) {
		return new Wheel360Scale( span, new double[] { begin(), end() } ).markN( mark ) ;
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
		double[] rdeqA = new double[2], rdeqO = new double[2], rdhoA, rdhoO, gneq = new double[2], gnho ;
		double rdhoC[], rdST, rdLa, gnhoC[], gnST, gnLa ;
		double inaz[], gnaz, al, gnal ;
		double gnb, gne ;
		MessageCatalog cat ;
		StringBuffer msg ;
		String fmt ;

		al = valueOf( getAngle() ) ;
		gnal = valueOf( gn.getAngle() ) ;

		rdhoC = zenit() ;
		rdST = rdhoC[0] ;
		rdLa = rdhoC[1] ;
		gnhoC = gn.zenit() ;
		gnST = gnhoC[0] ;
		gnLa = gnhoC[1] ;

		rdB = 90-al ;
		gnB = 90-gnal ;
		blA = 90-rdLa ;
		blB = 90-gnLa ;
		blGa = gnST-rdST ;

		inaz = CircleParallel.intersection( rdB, gnB, blA, blB, blGa ) ;

		// unconvert local rd into actual
		cA = CAACoordinateTransformation.Horizontal2Equatorial( inaz[0], al, rdLa/*horizon.getLa()*/ ) ;
		rdeqA[0] = CAACoordinateTransformation.HoursToDegrees( cA.X() ) ;
		rdeqA[1] = cA.Y() ;
		rdeqA[0] = rdST-rdeqA[0] ;
		rdhoA = projector.unconvert( rdeqA ) ;

		cO = CAACoordinateTransformation.Horizontal2Equatorial( inaz[1], al, rdLa ) ;
		rdeqO[0] = CAACoordinateTransformation.HoursToDegrees( cO.X() ) ;
		rdeqO[1] = cO.Y() ;
		rdeqO[0] = rdST-rdeqO[0] ;
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
		c = CAACoordinateTransformation.Horizontal2Equatorial( leading?inaz[3]:inaz[2], gnal, gnLa ) ;
		gneq[0] = CAACoordinateTransformation.HoursToDegrees( c.X() ) ;
		gneq[1] = c.Y() ;
		gneq[0] = gnST-gneq[0] ;
		gnho = gn.projector.unconvert( gneq ) ;

		gnb = gn.begin() ;
		gne = gn.end() ;

		gnaz = CAACoordinateTransformation.MapTo0To360Range( gnho[0] ) ;
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

		return leading?rdhoO[0]:rdhoA[0] ;
	}

	public double intersect( CircleMeridian gn, boolean leading ) throws ParameterNotValidException {
		CAA2DCoordinate cA, cO ;
		double blA, blB, blGa, rdB ;
		double[] rdeqA = new double[2], rdeqO = new double[2], rdhoA, rdhoO ;
		double rdhoC[], rdST, rdLa ;
		double inaz[], al, gnal ;
		double gnb, gne ;
		MessageCatalog cat ;
		StringBuffer msg ;
		String fmt ;

		al = valueOf( getAngle() ) ;

		rdhoC = zenit() ;
		rdST = rdhoC[0] ;
		rdLa = rdhoC[1] ;

		rdB = 90-al ;
		blA = 90-rdLa ;
		blB = 90-gn.transformParallelLa() ;
		blGa = gn.transformParallelST()-rdST ;

		inaz = CircleParallel.intersection( rdB, 90, blA, blB, blGa ) ;

		// unconvert local rd into actual
		cA = CAACoordinateTransformation.Horizontal2Equatorial( inaz[0], al, rdLa ) ;
		rdeqA[0] = CAACoordinateTransformation.HoursToDegrees( cA.X() ) ;
		rdeqA[1] = cA.Y() ;
		rdeqA[0] = rdST-rdeqA[0] ;
		rdhoA = projector.unconvert( rdeqA ) ;

		cO = CAACoordinateTransformation.Horizontal2Equatorial( inaz[1], al, rdLa ) ;
		rdeqO[0] = CAACoordinateTransformation.HoursToDegrees( cO.X() ) ;
		rdeqO[1] = cO.Y() ;
		rdeqO[0] = rdST-rdeqO[0] ;
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

		return leading?rdhoO[0]:rdhoA[0] ;
	}

	public double[] zenit() {
		return projector.convert( 0, 90 ) ;
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

	private PostscriptEmitter dial( astrolabe.model.DialDegree peer ) {
		DialDegree dial ;

		dial = new DialDegree( this ) ;
		peer.copyValues( dial ) ;

		return dial ;
	}

	private PostscriptEmitter dial( astrolabe.model.DialHour peer ) {
		DialHour dial ;

		dial = new DialHour( this ) ;
		peer.copyValues( dial ) ;

		return dial ;
	}
}
