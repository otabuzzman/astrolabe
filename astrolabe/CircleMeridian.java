
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

	// qualifier key (QK_)
	private final static String QK_AZIMUTH			= "azimuth" ;
	private final static String QK_TERMINUS			= "terminius" ;

	private final static Log log = LogFactory.getLog( CircleMeridian.class ) ;

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

			leading = getBegin().getReference().getNode().equals( AV_LEADING ) ;
			try {
				circle = Registry.retrieve( getBegin().getReference().getCircle() ) ;
				r = circle instanceof CircleParallel?
						intersect( (CircleParallel) circle, leading ):
							intersect( (CircleMeridian) circle, leading ) ;
			} catch ( ParameterNotValidException e ) {
				log.warn( e.getMessage() ) ;

				r = -90 ;
			}
		} else {
			r = valueOf( getBegin().getAngle() ) ;
		}

		return r ;
	}

	public double end() {
		double end ;

		if ( getEnd().getAngle() == null ) {
			Object circle ;
			boolean leading ;

			leading = getBegin().getReference().getNode().equals( AV_LEADING ) ;
			try {
				circle = Registry.retrieve( getEnd().getReference().getCircle() ) ;
				end = circle instanceof CircleParallel?
						intersect( (CircleParallel) circle, leading ):
							intersect( (CircleMeridian) circle, leading ) ;
			} catch ( ParameterNotValidException e ) {
				log.warn( e.getMessage() ) ;

				end = 90 ;
			}
		} else {
			end = valueOf( getEnd().getAngle() ) ;
		}

		return end ;
	}

	public void register() {
		Point point ;
		double az ;
		DMS dms ;

		point = new Point( convert( end() ) ) ;
		point.register( this, QK_TERMINUS ) ;

		az = valueOf( getAngle() ) ;
		dms = new DMS( az/15 ) ;
		dms.register( this, QK_AZIMUTH ) ;
	}

	public void degister() {
		Point.degister( this, QK_TERMINUS ) ;

		DMS.degister( this, QK_AZIMUTH ) ;
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
		astrolabe.model.CircleMeridian peer ;
		CircleMeridian circle ;
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
					peer = new astrolabe.model.CircleMeridian() ;
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

	public double[] project( double al, double shift ) {
		double xy[], az ;
		Vector v, t ;

		az = valueOf( getAngle() ) ;	
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
		return projector.convert( valueOf( getAngle() ), angle ) ;
	}

	public double unconvert( double[] eq ) {
		return projector.unconvert( eq )[1] ;
	}

	public double[] tangent( double al ) {
		Vector a, b ;
		double xy[], az ;

		az = valueOf( getAngle() ) ;
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

		interval = Configuration.getValue( this, CK_INTERVAL, DEFAULT_INTERVAL ) ;

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

	public LineString getCircleGeometry() {
		List<double[]> list;
		LineString line ;

		list = list( null, begin(), end(), 0 ) ;
		line = new GeometryFactory().createLineString( new JTSCoordinateArraySequence( list ) ) ;

		return line ;
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
		az = valueOf( getAngle() ) ;
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
		az = valueOf( getAngle() ) ;
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
		MessageCatalog cat ;
		StringBuffer msg ;
		String fmt ;

		rdhoC = zenit() ;
		rdST = rdhoC[0] ;
		gnhoC = gn.zenit() ;
		gnST = gnhoC[0] ;
		gnLa = gnhoC[1] ;

		gnal = valueOf( gn.getAngle() ) ;

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

		return leading?inaz[1]:inaz[0] ;
	}

	public double intersect( CircleMeridian gn, boolean leading ) throws ParameterNotValidException {
		double blA, blB, blGa ;
		double inaz[], gnal ;
		double gnb, gne ;
		MessageCatalog cat ;
		StringBuffer msg ;
		String fmt ;

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

		return leading?inaz[1]:inaz[0] ;
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
