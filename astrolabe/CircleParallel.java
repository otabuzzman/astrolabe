
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

	private final static Log log = LogFactory.getLog( CircleParallel.class ) ;

	private final static double DEFAULT_INTERVAL	= 1 ;

	private Projector projector ;

	public CircleParallel( Projector projector ) {
		this.projector = projector ;
	}

	public double begin() {
		double r ;

		if ( getBegin().getAngle() == null ) {
			Object circle ;
			boolean leading ;

			leading = getBegin().getReference().getNode().equals( ApplicationConstant.AV_CIRCLE_LEADING ) ;
			try {
				circle = Registry.retrieve( getBegin().getReference().getCircle() ) ;
				r = circle instanceof CircleParallel?
						intersect( (CircleParallel) circle, leading ):
							intersect( (CircleMeridian) circle, leading ) ;
			} catch ( ParameterNotValidException e ) {
				String msg ;

				msg = MessageCatalog.message( ApplicationConstant.GC_APPLICATION, ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
				msg = MessageFormat.format( msg, new Object[] { e.getMessage(), "\""+getBegin().getReference().getCircle()+"\"" } ) ;
				log.warn( msg ) ;

				r = 0 ;
			}
		} else {
			r = AstrolabeFactory.valueOf( getBegin().getAngle() ) ;
		}

		return CAACoordinateTransformation.MapTo0To360Range( r ) ;
	}

	public double end() {
		double r ;

		if ( getEnd().getAngle() == null ) {
			Object circle ;
			boolean leading ;

			leading = getEnd().getReference().getNode().equals( ApplicationConstant.AV_CIRCLE_LEADING ) ;
			try {
				circle = Registry.retrieve( getEnd().getReference().getCircle() ) ;
				r = circle instanceof CircleParallel?
						intersect( (CircleParallel) circle, leading ):
							intersect( (CircleMeridian) circle, leading ) ;
			} catch ( ParameterNotValidException e ) {
				String msg ;

				msg = MessageCatalog.message( ApplicationConstant.GC_APPLICATION, ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
				msg = MessageFormat.format( msg, new Object[] { e.getMessage(), "\""+getEnd().getReference().getCircle()+"\"" } ) ;
				log.warn( msg ) ;

				r = 359.9999 ;
			}
		} else {
			r = AstrolabeFactory.valueOf( getEnd().getAngle() ) ;
		}
		r = CAACoordinateTransformation.MapTo0To360Range( r ) ;

		return r ;
	}

	public void register() {
		String key ;
		double al ;
		LineString fov ;
		List<double[]> list ;


		if ( getName() != null )
			Registry.register( getName(), this ) ;

		al = AstrolabeFactory.valueOf( getAngle() ) ;

		key = MessageCatalog.message( ApplicationConstant.GC_APPLICATION, ApplicationConstant.LK_CIRCLE_ALTITUDE ) ;
		AstrolabeRegistry.registerDMS( key, al ) ;

		if ( getFov() != null ) {
			list = list( null, begin(), end(), 0 ) ;

			fov = new GeometryFactory().createLineString(
					new JTSCoordinateArraySequence( list ) ) ;

			if ( fov.isRing() ) {
				Registry.register( getFov(),
						new GeometryFactory().createPolygon(
								new GeometryFactory().createLinearRing( fov.getCoordinateSequence() ), null ) ) ;
			} else { // extend
			}
		}
	}

	public void headPS( AstrolabePostscriptStream ps ) {
		GSPaintStroke importance ;

		importance = new GSPaintStroke( getImportance(), getName() ) ;
		importance.headPS( ps ) ;
		importance.emitPS( ps ) ;
		importance.tailPS( ps ) ;
	}

	public void emitPS( AstrolabePostscriptStream ps ) {
		Geometry fov, circle ;

		fov = (Geometry) Registry.retrieve( ApplicationConstant.GC_FOVUNI ) ;

		circle = new GeometryFactory().createLineString(
				new JTSCoordinateArraySequence( list( null, begin(), end(), 0 ) ) ) ;

		emitPS( ps, !fov.covers( circle ) ) ;
	}

	public void emitPS( AstrolabePostscriptStream ps, boolean cut ) {
		ListCutter cutter ;
		Geometry fov ;
		astrolabe.model.CircleParallel peer ;
		CircleParallel circle ;
		double[] lob, loe, xy ;
		List<double[]> l ;

		if ( cut ) {
			fov = (Geometry) Registry.retrieve( ApplicationConstant.GC_FOVUNI ) ;
			cutter = new ListCutter( list( null, begin(), end(), 0 ), fov ) ;
			for ( List<double[]> s : cutter.segmentsIntersecting( true ) ) {
				lob = projector.unproject( s.get( 0 ) ) ;
				xy = s.get( s.size()-1 ) ;
				loe = projector.unproject( xy ) ; 

				try {
					peer = new astrolabe.model.CircleParallel() ;
					if ( getName() == null )
						peer.setName( ApplicationConstant.GC_NS_CUT ) ;
					else
						peer.setName( ApplicationConstant.GC_NS_CUT+getName() ) ;
					AstrolabeFactory.modelOf( peer, false ) ;

					peer.setImportance( getImportance() ) ;

					// astrolabe.model.AngleType
					peer.setAngle( new astrolabe.model.Angle() ) ;
					peer.getAngle().setRational( new astrolabe.model.Rational() ) ;
					peer.getAngle().getRational().setValue( AstrolabeFactory.valueOf( getAngle() ) ) ;

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
				peer.setupCompanion( circle ) ;
				circle.register() ;

				ps.operator.gsave() ;

				circle.headPS( ps ) ;
				circle.emitPS( ps, false ) ;
				circle.tailPS( ps ) ;

				ps.operator.grestore() ;
			}
		} else {
			l = list( null, begin(), end(), 0 ) ;
			ps.array( true ) ;
			for ( int n=0 ; n<l.size() ; n++ ) {
				xy = l.get( n ) ;
				ps.push( xy[0] ) ;
				ps.push( xy[1] ) ;
			}
			ps.array( false ) ;

			ps.operator.newpath() ;
			ps.push( ApplicationConstant.PS_PROLOG_GDRAW ) ;

			AstrolabeRegistry.registerJTSCoordinate(
					MessageCatalog.message( ApplicationConstant.GC_APPLICATION, ApplicationConstant.LK_CIRCLE_CURRENTPOINT ),
					new JTSCoordinate( l.get( l.size()-1 ) ) ) ;

			// halo stroke
			ps.operator.currentlinewidth() ;

			ps.operator.dup() ;
			ps.operator.div( 100 ) ;
			ps.push( (Double) ( Registry.retrieve( ApplicationConstant.PK_CHART_HALO ) ) ) ; 
			ps.operator.mul() ;
			ps.push( (Double) ( Registry.retrieve( ApplicationConstant.PK_CHART_HALOMIN ) ) ) ; 
			ps.push( ApplicationConstant.PS_PROLOG_MAX ) ;
			ps.push( (Double) ( Registry.retrieve( ApplicationConstant.PK_CHART_HALOMAX ) ) ) ; 
			ps.push( ApplicationConstant.PS_PROLOG_MIN ) ;

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
				PostscriptEmitter dial ;

				ps.operator.gsave() ;

				dial = AstrolabeFactory.companionOf( getDial(), (Baseline) this ) ;
				dial.headPS( ps ) ;
				dial.emitPS( ps ) ;
				dial.tailPS( ps ) ;

				ps.operator.grestore() ;
			}

			if ( getAnnotation() != null ) {
				PostscriptEmitter annotation ;

				for ( int i=0 ; i<getAnnotationCount() ; i++ ) {
					ps.operator.gsave() ;

					annotation = AstrolabeFactory.companionOf( getAnnotation( i ) ) ;
					annotation.headPS( ps ) ;
					annotation.emitPS( ps ) ;
					annotation.tailPS( ps ) ;

					ps.operator.grestore() ;
				}
			}
		}
	}

	public void tailPS( AstrolabePostscriptStream ps ) {
	}

	public double[] project( double az, double shift ) {
		double xy[], al ;
		Vector v, t ;

		al = AstrolabeFactory.valueOf( getAngle() ) ;	
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
		return projector.convert( angle, AstrolabeFactory.valueOf( getAngle() ) ) ;
	}

	public double unconvert( double[] eq ) {
		return projector.unconvert( eq )[0] ;
	}

	public double[] tangent( double az ) {
		Vector a, b ;
		double xy[], al ;

		al = AstrolabeFactory.valueOf( getAngle() ) ;
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

		interval = Configuration.getValue(
				Configuration.getClassNode( this, getName(), null ),
				ApplicationConstant.PK_CIRCLE_INTERVAL, DEFAULT_INTERVAL ) ;

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

	public double scaleMarkNth( int mark, double span ) {
		return new Wheel360Scale( span, new double[] { begin(), end() } ).markN( mark ) ;
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
		if ( 180<blGa ) {			// case i.
			blGa = -( 2*180-blGa ) ;
		} else if ( -180>blGa ) {	// case ii.
			blGa = blGa+2*180 ;
		}

		if ( blA==0 ) {	// rdLa==90
			blC = blB ;

			// intersection test
			if ( blGa<0?!( gnB>( rdB-blC ) ):!( rdB>( gnB-blC ) ) ) {
				throw new ParameterNotValidException( "+" ) ;
			}

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
			if ( blGa<0?!( gnB>( rdB-blC ) ):!( rdB>( gnB-blC ) ) ) {
				throw new ParameterNotValidException( "+" ) ;
			}

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
			if ( blGa<0?!( gnB>( rdB-blC ) ):!( rdB>( gnB-blC ) ) ) {
				throw new ParameterNotValidException( "+" ) ;
			}

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
			if ( blGa<0?!( gnB>( rdB-blC ) ):!( rdB>( gnB-blC ) ) ) {
				throw new ParameterNotValidException( "+" ) ;
			}

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

		r[0] = rdaz[0] ;
		r[1] = rdaz[1] ;
		r[2] = gnaz[0] ;
		r[3] = gnaz[1] ;

		return r ;
	}

	public double intersect( CircleParallel gn, boolean leading ) throws ParameterNotValidException {
		CAA2DCoordinate cA, cO, c ;
		double blA, blB, blGa, rdB, gnB ;
		double[] rdeqA = new double[2], rdeqO = new double[2], rdhoA, rdhoO, gneq = new double[2], gnho ;
		double rdhoC[], rdST, rdLa, gnhoC[], gnST, gnLa ;
		double inaz[], gnaz, al, gnal ;
		double gnb, gne ;

		al = AstrolabeFactory.valueOf( getAngle() ) ;
		gnal = AstrolabeFactory.valueOf( gn.getAngle() ) ;

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
		if ( !( gnb>gne ? gnaz>=gnb || gnaz<=gne : gnaz>=gnb && gnaz<=gne ) )
			throw new ParameterNotValidException( Double.toString( gnaz ) ) ;

		return leading?rdhoO[0]:rdhoA[0] ;
	}

	public double intersect( CircleMeridian gn, boolean leading ) throws ParameterNotValidException {
		CAA2DCoordinate cA, cO ;
		double blA, blB, blGa, rdB ;
		double[] rdeqA = new double[2], rdeqO = new double[2], rdhoA, rdhoO ;
		double rdhoC[], rdST, rdLa ;
		double inaz[], al, gnal ;
		double gnb, gne ;

		al = AstrolabeFactory.valueOf( getAngle() ) ;

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

		if ( gnb>gne ? gnal>=gne && gnal<=gnb : gnal>=gnb && gnal<=gne )
			throw new ParameterNotValidException( Double.toString( gnal ) ) ;

		return leading?rdhoO[0]:rdhoA[0] ;
	}

	public double[] zenit() {
		return projector.convert( 0, 90 ) ;
	}
}
