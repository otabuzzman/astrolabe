
package astrolabe;

import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.xml.ValidationException;

import caa.CAA2DCoordinate;
import caa.CAACoordinateTransformation;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

@SuppressWarnings("serial")
public class CircleMeridian extends astrolabe.model.CircleMeridian implements PostscriptEmitter, Baseline, Converter {

	// qualifier key (QK_)
	private final static String QK_AZIMUTH			= "azimuth" ;
	private final static String QK_TERMINUS			= "terminus" ;

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

	private Converter converter ;
	private Projector projector ;

	public CircleMeridian( Converter converter, Projector projector ) {
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
		astrolabe.Coordinate currentpoint ;
		double az ;
		DMS dms ;

		currentpoint = new astrolabe.Coordinate( positionOfScaleMarkValue( end(), 0 ) ) ;
		currentpoint.register( this, QK_TERMINUS ) ;

		az = valueOf( getAngle() ) ;
		dms = new DMS( az/15 ) ;
		dms.register( this, QK_AZIMUTH ) ;
	}

	public void degister() {
		astrolabe.Coordinate.degister( this, QK_TERMINUS ) ;

		DMS.degister( this, QK_AZIMUTH ) ;
	}

	public void headPS( ApplicationPostscriptStream ps ) {
		String gstate ;

		if ( ( gstate = Configuration.getValue( this, getImportance(), null ) ) == null )
			return ;
		ps.script( gstate ) ;
	}

	public void emitPS( ApplicationPostscriptStream ps ) {
		emitPS( ps, true ) ;
	}

	private void emitPS( ApplicationPostscriptStream ps, boolean cut ) {
		Configuration conf ;
		ListCutter cutter ;
		List<Coordinate[]> segment ;
		Coordinate[] list ;
		ChartPage page ;
		Geometry fov ;
		astrolabe.model.CircleMeridian peer ;
		CircleMeridian circle ;
		Coordinate lob, loe ;
		astrolabe.model.Annotation annotation ;
		astrolabe.model.Dial dial ;
		PostscriptEmitter emitter ;

		if ( cut ) {
			page = (ChartPage) Registry.retrieve( ChartPage.class.getName() ) ;

			if ( page == null ) {
				emitPS( ps, false ) ;

				return ;
			}

			list = list( null, begin(), end(), 0 ) ;
			fov = page.getViewGeometry() ;
			cutter = new ListCutter( list, fov ) ;
			segment = cutter.segmentsIntersecting( true ) ;

			for ( Coordinate[] s : segment ) {
				lob = converter.convert( projector.project( s[0], true ), true ) ;
				loe = converter.convert( projector.project( s[s.length-1], true ), true ) ; 

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
					peer.getBegin().getAngle().getRational().setValue( lob.y ) ;
					peer.setEnd( new astrolabe.model.End() ) ;
					// astrolabe.model.AngleType
					peer.getEnd().setAngle( new astrolabe.model.Angle() ) ;
					peer.getEnd().getAngle().setRational( new astrolabe.model.Rational() ) ;
					peer.getEnd().getAngle().getRational().setValue( loe.y ) ;

					peer.setDial( getDial() ) ;
					peer.setAnnotation( getAnnotation() ) ;

					peer.validate() ;
				} catch ( ValidationException e ) {
					throw new RuntimeException( e.toString() ) ;
				}

				circle = new CircleMeridian( converter, projector ) ;
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
			for ( Coordinate xy : list ) {
				ps.push( xy.x ) ;
				ps.push( xy.y ) ;
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

	public Coordinate positionOfScaleMarkValue( double al, double shift ) {
		Coordinate xy ;
		double az ;
		Vector v, t ;

		az = valueOf( getAngle() ) ;	
		xy = projector.project( converter.convert( new Coordinate( az, al ), false ), false ) ;
		v = new Vector( xy ) ;

		if ( shift != 0 ) {
			xy = directionOfScaleMarkValue( al ) ;
			t = new Vector( xy ) ;
			t.apply( new double[] { 0, -1, 0, 1, 0, 0, 0, 0, 1 } ) ;
			t.scale( shift ) ;
			v.add( t ) ;
		}

		return new Coordinate( v.x, v.y ) ;
	}

	public Coordinate directionOfScaleMarkValue( double al ) {
		Vector a, b ;
		double az ;
		Coordinate xy ;

		az = valueOf( getAngle() ) ;
		xy = projector.project( converter.convert( new Coordinate( az, al+10./3600 ), false ), false ) ;
		a = new Vector( xy ) ;
		xy = projector.project( converter.convert( new Coordinate( az, al ), false ), false ) ;
		b = new Vector( xy ) ;

		a.sub( b ) ;

		return new Coordinate( a.x, a.y ) ;
	}

	public double valueOfScaleMarkN( int mark, double span ) {
		return new LinearScale( span, new double[] { begin(), end() } ).markN( mark ) ;
	}

	public Coordinate[] list( final List<Double> lista, double begin, double end, double shift ) {
		List<Coordinate> listxy ;
		double interval ;

		interval = Configuration.getValue( this, CK_INTERVAL, DEFAULT_INTERVAL ) ;

		listxy = new java.util.Vector<Coordinate>() ;

		for ( double al=begin ; begin>end?al>end:al<end ; al=begin>end?al-interval:al+interval ) {
			listxy.add( positionOfScaleMarkValue( al, shift ) ) ;
			if ( lista != null )
				lista.add( al ) ;
		}

		listxy.add( positionOfScaleMarkValue( end, shift ) ) ;
		if ( lista != null )
			lista.add( end ) ;

		return listxy.toArray( new Coordinate[0] ) ;
	}

	public Coordinate convert( Coordinate local, boolean inverse ) {
		if ( inverse )
			return converter.convert( local, true ) ;
		return converter.convert( new Coordinate( valueOf( getAngle() ), local.y ), false ) ;
	}

	public LineString getCircleGeometry() {
		Coordinate[] list;
		LineString line ;

		list = list( null, begin(), end(), 0 ) ;
		line = new GeometryFactory().createLineString( list ) ;

		return line ;
	}

	private double[] transform() {
		double[] r = new double[3] ;
		double blB, vlA, vlD, vlAl, vlDe ;
		double az, gnaz, rdST, rdLa ;
		Coordinate rdhoC, gneq, gnho ;

		rdhoC = zenit() ;
		rdST = rdhoC.x ;
		rdLa = rdhoC.y ;

		blB = 90-rdLa;
		if ( blB==0 ) {	// prevent infinity from tan
			blB = Math.lim0 ;
		}

		// convert actual gn into local
		az = valueOf( getAngle() ) ;
		gneq = converter.convert( new Coordinate( az, 0 ), false ) ;
		gnho = new astrolabe.Coordinate( CAACoordinateTransformation.Equatorial2Horizontal(
				CAACoordinateTransformation.DegreesToHours( rdST-gneq.x ), gneq.y, rdLa/*horizon.getLa()*/ ) ) ;
		gnaz = gnho.x ;
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
		double az, gnaz, gnal, vlD ;
		double rdST, rdLa ;
		Coordinate rdhoC, gneq, gnho ;

		rdhoC = zenit() ;
		rdST = rdhoC.x ;
		rdLa = rdhoC.y ;

		// convert actual gn into local
		az = valueOf( getAngle() ) ;
		gneq = converter.convert( new Coordinate( az, 0 ), false ) ;
		gnho = new astrolabe.Coordinate( CAACoordinateTransformation.Equatorial2Horizontal(
				CAACoordinateTransformation.DegreesToHours( rdST-gneq.x ), gneq.y, rdLa ) ) ;
		gnaz = gnho.x ;
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
		double rdST, gnST, gnLa ;
		double inaz[], gnaz, gnal ;
		double gnb, gne ;
		Coordinate rdhoC, gnhoC, gneqA, gneqO, gnhoA, gnhoO ;
		MessageCatalog cat ;
		StringBuffer msg ;
		String fmt ;

		rdhoC = zenit() ;
		rdST = rdhoC.x ;
		gnhoC = gn.zenit() ;
		gnST = gnhoC.x ;
		gnLa = gnhoC.y ;

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
		gneqA = new Coordinate( rdST-CAACoordinateTransformation.HoursToDegrees( cA.X() ), cA.Y() ) ;
		gnhoA = converter.convert( gneqA, true ) ;

		cO = CAACoordinateTransformation.Horizontal2Equatorial( inaz[3], gnal, gnLa ) ;
		gneqO = new Coordinate( rdST-CAACoordinateTransformation.HoursToDegrees( cO.X() ), cO.Y() ) ;
		gnhoO = converter.convert( gneqO, true ) ;

		// sort associated values
		if ( inaz[0]>inaz[1] ) {
			double v ;

			v = inaz[0] ;
			inaz[0] = inaz[1] ;
			inaz[1] = v ;

			v = gnhoA.x ;
			gnhoA.x = gnhoO.x ;
			gnhoO.x = v ;
		}

		gnaz = leading?gnhoO.x:gnhoA.x ;

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
