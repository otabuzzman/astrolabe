
package astrolabe;

import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import caa.CAA2DCoordinate;
import caa.CAACoordinateTransformation;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;

@SuppressWarnings("serial")
public class CircleMeridian extends astrolabe.model.CircleMeridian implements PostscriptEmitter, Baseline {

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

		currentpoint = new astrolabe.Coordinate( posVecOfScaleMarkVal( end() ) ) ;
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
		Configuration conf ;
		int segmin ;
		Coordinate[] ccrc, ccut ;
		ChartPage page ;
		Geometry fov, cut, tmp ;
		astrolabe.model.Annotation annotation ;
		PostscriptEmitter emitter ;

		conf = new Configuration( this ) ;
		segmin = conf.getValue( CK_SEGMIN, DEFAULT_SEGMIN ) ;

		page = (ChartPage) Registry.retrieve( ChartPage.class.getName() ) ;
		if ( page != null )
			fov = FieldOfView.makeGeometry( page.getViewRectangle(), true ) ;
		else
			fov = null ;

		ccrc = list( begin(), end() ) ;

		if ( fov == null )
			cut = new GeometryFactory().createLineString( ccrc ) ;
		else {
			tmp = new GeometryFactory().createLineString( ccrc ) ;
			cut = fov.intersection( tmp ) ;
		}

		for ( int i=0 ; cut.getNumGeometries()>i ; i++ ) {
			ccut = cut.getGeometryN( i ).getCoordinates() ;

			if ( segmin>ccut.length )
				continue ;

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

			if ( getDialDeg() != null ) {
				emitter = new DialDeg( this ) ;
				getDialDeg().copyValues( emitter ) ;

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

	public void tailPS( ApplicationPostscriptStream ps ) {
	}

	public Vector posVecOfScaleMarkVal( double al ) {
		Coordinate xy ;
		double az ;

		az = valueOf( getAngle() ) ;	
		xy = projector.project( converter.convert( new Coordinate( az, al ), false ), false ) ;

		return new Vector( xy ) ;
	}

	public double valOfScaleMarkN( int mark, double span ) {
		return new LinearScale( span, new double[] { begin(), end() } ).markN( mark ) ;
	}

	public Coordinate[] list( double begin, double end ) {
		List<Coordinate> list ;
		double interval, distance ;

		interval = Configuration.getValue( this, CK_INTERVAL, DEFAULT_INTERVAL ) ;

		list = new java.util.Vector<Coordinate>() ;

		for ( double al=begin ; begin>end?al>end:al<end ; al=begin>end?al-interval:al+interval )
			list.add( posVecOfScaleMarkVal( al ) ) ;
		list.add( posVecOfScaleMarkVal( end ) ) ;

		distance = Configuration.getValue( this, CK_DISTANCE, DEFAULT_DISTANCE ) ;
		if ( distance>0 && list.size()>2 )
			return DouglasPeuckerSimplifier.simplify( new GeometryFactory().createLineString( list.toArray( new Coordinate[0] ) ), distance ).getCoordinates() ;
		return list.toArray( new Coordinate[0] ) ;
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
}
