
package astrolabe;

import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.xml.ValidationException;

import caa.CAA2DCoordinate;
import caa.CAACoordinateTransformation;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

@SuppressWarnings("serial")
public class CircleMeridian extends astrolabe.model.CircleMeridian implements PostscriptEmitter, Baseline {

	private final static Log log = LogFactory.getLog( CircleMeridian.class ) ;

	private final static double DEFAULT_INTERVAL	= 1 ;

	private Projector projector ;

	public CircleMeridian( Projector projector ) {
		this.projector = projector ;
	}

	public double[] project( double al ) {
		return project( al, 0 ) ;
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

				r = -90 ;
			}
		} else {
			r = AstrolabeFactory.valueOf( getBegin().getAngle() ) ;
		}

		return r ;
	}

	public double end() {
		double end ;

		if ( getEnd().getAngle() == null ) {
			Object circle ;
			boolean leading ;

			leading = getBegin().getReference().getNode().equals( ApplicationConstant.AV_CIRCLE_LEADING ) ;
			try {
				circle = Registry.retrieve( getEnd().getReference().getCircle() ) ;
				end = circle instanceof CircleParallel?
						intersect( (CircleParallel) circle, leading ):
							intersect( (CircleMeridian) circle, leading ) ;
			} catch ( ParameterNotValidException e ) {
				String msg ;

				msg = MessageCatalog.message( ApplicationConstant.GC_APPLICATION, ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
				msg = MessageFormat.format( msg, new Object[] { e.getMessage(), "\""+getEnd().getReference().getCircle()+"\"" } ) ;
				log.warn( msg ) ;

				end = 90 ;
			}
		} else {
			end = AstrolabeFactory.valueOf( getEnd().getAngle() ) ;
		}

		return end ;
	}

	public void register() {
		String key ;
		double az ;
		LineString fov ;
		List<double[]> list ;

		if ( getName() != null )
			Registry.register( getName(), this ) ;

		az = AstrolabeFactory.valueOf( getAngle() ) ;

		key = MessageCatalog.message( ApplicationConstant.GC_APPLICATION, ApplicationConstant.LK_CIRCLE_AZIMUTH ) ;
		AstrolabeRegistry.registerDMS( key, az ) ;
		AstrolabeRegistry.registerHMS( key, az ) ;

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
		astrolabe.model.CircleMeridian peer ;
		CircleMeridian circle ;
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
					peer = new astrolabe.model.CircleMeridian() ;
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
					peer.getBegin().getAngle().getRational().setValue( lob[1] ) ;
					peer.setEnd( new astrolabe.model.End() ) ;
					// astrolabe.model.AngleType
					peer.getEnd().setAngle( new astrolabe.model.Angle() ) ;
					peer.getEnd().getAngle().setRational( new astrolabe.model.Rational() ) ;
					peer.getEnd().getAngle().getRational().setValue( loe[1] ) ;

					peer.setDial( getDial() ) ;
					peer.setAnnotation( getAnnotation() ) ;

					peer.validate() ;
				} catch ( ValidationException e ) {
					throw new RuntimeException( e.toString() ) ;
				}

				circle = new CircleMeridian( projector ) ;
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

	public double[] project( double al, double shift ) {
		double xy[], az ;
		Vector v, t ;

		az = AstrolabeFactory.valueOf( getAngle() ) ;	
		xy = projector.project( az, al ) ;
		v = new Vector( xy[0], xy[1] ) ;

		if ( shift != 0 ) {
			xy = tangent( al ) ;
			t = new Vector( xy[0], xy[1] ) ;
			t.apply( new double[] { 0, -1, 0, 1, 0, 0, 0, 0, 1 } ) ;
			t.scale( shift ) ;
			v.add( t ) ;
		}

		return new double[] { v.x, v.y } ;
	}

	public double[] convert( double angle ) {
		return projector.convert( AstrolabeFactory.valueOf( getAngle() ), angle ) ;
	}

	public double unconvert( double[] eq ) {
		return projector.unconvert( eq )[1] ;
	}

	public double[] tangent( double al ) {
		Vector a, b ;
		double xy[], az ;

		az = AstrolabeFactory.valueOf( getAngle() ) ;
		xy = projector.project( az, al+10./3600 ) ;
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

		for ( double al=begin ; begin>end?al>end:al<end ; al=begin>end?al-interval:al+interval ) {
			listxy.add( project( al, shift ) ) ;
			if ( lista != null )
				lista.add( al ) ;
		}

		listxy.add( project( end, shift ) ) ;
		if ( lista != null )
			lista.add( end ) ;

		return listxy ;
	}

	public double scaleMarkNth( int mark, double span ) {
		return new LinearScale( span, new double[] { begin(), end() } ).markN( mark ) ;
	}

	private double[] transform() {
		CAA2DCoordinate c ;
		double[] r = new double[3] ;
		double blB, vlA, vlD, vlAl, vlDe ;
		double gneq[], gnho[] = new double[2], az, gnaz ;
		double rdhoC[], rdST, rdLa ;

		rdhoC = zenit() ;
		rdST = rdhoC[0] ;
		rdLa = rdhoC[1] ;

		blB = 90-rdLa;
		if ( blB==0 ) {	// prevent infinity from tan
			blB = Math.lim0 ;
		}

		// convert actual gn into local
		az = AstrolabeFactory.valueOf( getAngle() ) ;
		gneq = projector.convert( az, 0 ) ;
		c = CAACoordinateTransformation.Equatorial2Horizontal(
				CAACoordinateTransformation.DegreesToHours( rdST-gneq[0] ), gneq[1], rdLa/*horizon.getLa()*/ ) ;
		gnho[0] = c.X() ;
		gnho[1] = c.Y() ;
		gnaz = gnho[0] ;
		if ( gnaz==0 ) { // prevent infinity from tan
			gnaz = Math.lim0 ;
		}


		if ( Math.tan( gnaz )>0 ) {	// QI, QIII
			vlAl = gnaz ;
			while ( vlAl>90 ) { // mapTo0To90Range
				vlAl = vlAl-90 ;
			}
			while ( vlAl<0 ) {
				vlAl = vlAl+90 ;
			}

			// Rule of Napier : cos(90-a) = sin(al)*sin(c)
			vlA = Math.acos( Math.sin( vlAl )*Math.sin( blB ) ) ;
			// Rule of Napier : cos(be) = cot(90-a)*cot(c)
			vlDe = Math.acos( Math.truncate( Math.cot( vlA )*Math.cot( blB ) ) ) ;

			r[1] = rdST-180+vlDe;
		} else { // QII, QIV
			vlAl = gnaz ;
			while ( vlAl>90 ) { // mapTo0To90Range
				vlAl = vlAl-90 ;
			}
			while ( vlAl<0 ) {
				vlAl = vlAl+90 ;
			}
			vlAl = 90-vlAl ;

			vlA = Math.acos( Math.sin( vlAl )*Math.sin( blB ) ) ;
			vlDe = Math.acos( Math.truncate( Math.cot( vlA )*Math.cot( blB ) ) ) ;

			r[1] = rdST+180-vlDe;
		}

		r[0] = 90-vlA ;

		// Rule of Napier : cos(c) = sin(90-a)*sin(90-b)
		// sin(90-b) = cos(c):sin(90-a)
		vlD = 90-Math.asin( Math.truncate( Math.cos( blB )/Math.sin( vlA ) ) ) ;

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
		CAA2DCoordinate c ;
		double gneq[], gnho[] = new double[2], az, gnaz, gnal, vlD ;
		double rdhoC[], rdST, rdLa ;

		rdhoC = zenit() ;
		rdST = rdhoC[0] ;
		rdLa = rdhoC[1] ;

		// convert actual gn into local
		az = AstrolabeFactory.valueOf( getAngle() ) ;
		gneq = projector.convert( az, 0 ) ;
		c = CAACoordinateTransformation.Equatorial2Horizontal(
				CAACoordinateTransformation.DegreesToHours( rdST-gneq[0] ), gneq[1], rdLa ) ;
		gnho[0] = c.X() ;
		gnho[1] = c.Y() ;
		gnaz = gnho[0] ;
		if ( gnaz==0 ) { // prevent infinity from tan
			gnaz = Math.lim0 ;
		}

		// convert vl az into gn
		vlD = transform()[2] ;
		gnal = Math.tan( gnaz )>0?vlaz-vlD:vlaz+vlD ;
		// convert gn az into al
		gnal = gnaz>180?gnal-90:-( gnal-270 ) ;
		// map range from -180 to 180
		gnal = gnal>180?gnal-360:gnal ;

		return gnal ;
	}

	public double intersect( CircleParallel gn, boolean leading ) throws ParameterNotValidException {
		CAA2DCoordinate cA, cO ;
		double blA, blB, blGa, gnB ;
		double[] gneqA = new double[2], gneqO = new double[2], gnhoA, gnhoO ;
		double rdhoC[], rdST, gnhoC[], gnST, gnLa ;
		double inaz[], gnaz, gnal ;
		double gnb, gne ;

		rdhoC = zenit() ;
		rdST = rdhoC[0] ;
		gnhoC = gn.zenit() ;
		gnST = gnhoC[0] ;
		gnLa = gnhoC[1] ;

		gnal = AstrolabeFactory.valueOf( gn.getAngle() ) ;

		gnB = 90-gnal ;
		blA = 90-transformParallelLa() ;
		blB = 90-gnLa ;
		blGa = gnST-transformParallelST() ;

		inaz = CircleParallel.intersection( 90, gnB, blA, blB, blGa ) ;

		// convert rd az into al
		inaz[0] = transformMeridianAl( inaz[0] ) ;
		inaz[1] = transformMeridianAl( inaz[1] ) ;

		// unconvert local gn into actual
		cA = CAACoordinateTransformation.Horizontal2Equatorial( inaz[2], gnal, gnLa ) ;
		gneqA[0] = CAACoordinateTransformation.HoursToDegrees( cA.X() ) ;
		gneqA[1] = cA.Y() ;
		gneqA[0] = rdST-gneqA[0];
		gnhoA = projector.unconvert( gneqA ) ;

		cO = CAACoordinateTransformation.Horizontal2Equatorial( inaz[3], gnal, gnLa ) ;
		gneqO[0] = CAACoordinateTransformation.HoursToDegrees( cO.X() ) ;
		gneqO[1] = cO.Y() ;
		gneqO[0] = rdST-gneqO[0];
		gnhoO = projector.unconvert( gneqO ) ;

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

		gnb = gn.begin() ;
		gne = gn.end() ;

		if ( !( gnb>gne ? gnaz>=gnb || gnaz<=gne : gnaz>=gnb && gnaz<=gne ) )
			throw new ParameterNotValidException( Double.toString( gnaz ) ) ;

		return leading?inaz[1]:inaz[0] ;
	}

	public double intersect( CircleMeridian gn, boolean leading ) throws ParameterNotValidException {
		double blA, blB, blGa ;
		double inaz[], gnal ;
		double gnb, gne ;

		blA = 90-transformParallelLa() ;
		blB = 90-gn.transformParallelLa() ;
		blGa = gn.transformParallelST()-transformParallelST() ;

		inaz = CircleParallel.intersection( 90, 90, blA, blB, blGa ) ;

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

		gnb = gn.begin() ;
		gne = gn.end() ;

		if ( gnb>gne ? gnal>=gne && gnal<=gnb : gnal>=gnb && gnal<=gne )
			throw new ParameterNotValidException( Double.toString( gnal ) ) ;

		return leading?inaz[1]:inaz[0] ;
	}

	public double[] zenit() {
		return projector.convert( 0, 90 ) ;
	}
}
