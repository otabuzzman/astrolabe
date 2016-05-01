
package astrolabe;

import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.xml.ValidationException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

import caa.CAACoordinateTransformation;

@SuppressWarnings("serial")
public class CircleMeridian extends astrolabe.model.CircleMeridian implements PostscriptEmitter, Baseline {

	private final static Log log = LogFactory.getLog( CircleMeridian.class ) ;

	private final static double DEFAULT_INTERVAL = 1 ;
	private final static String DEFAULT_IMPORTANCE = ".1:0" ;

	private Projector projector ;

	private double interval ;

	private double az ;

	private double begin ;
	private double end ;

	public CircleMeridian() {
	}

	public CircleMeridian( Object peer, Projector projector ) throws ParameterNotValidException {
		setup( peer, projector ) ;
	}

	public void setup( Object peer, Projector projector ) throws ParameterNotValidException {
		String key ;
		Polygon fov ;

		ApplicationHelper.setupCompanionFromPeer( this, peer ) ;
		try {
			validate() ;
		} catch ( ValidationException e ) {
			throw new ParameterNotValidException( e.toString() ) ;
		}

		this.projector = projector ;

		interval = CAACoordinateTransformation.DegreesToRadians(
				ApplicationHelper.getPreferencesKV(
						ApplicationHelper.getClassNode( this, getName(), null ),
						ApplicationConstant.PK_CIRCLE_INTERVAL, DEFAULT_INTERVAL ) ) ;

		az = AstrolabeFactory.valueOf( getAngle() ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_CIRCLE_AZIMUTH ) ;
		ApplicationHelper.registerDMS( key, az, 2 ) ;
		ApplicationHelper.registerHMS( key, az, 2 ) ;

		try {
			begin = AstrolabeFactory.valueOf( getBegin().getImmediate() ) ;
		} catch ( ParameterNotValidException e ) {
			Object circle ;
			boolean leading ;

			circle = Registry.retrieve( getBegin().getReference().getCircle() ) ;
			leading = getBegin().getReference().getNode().equals( ApplicationConstant.AV_CIRCLE_LEADING ) ;
			try {
				begin = circle instanceof CircleParallel?
						intersect( (CircleParallel) circle, leading ):
							intersect( (CircleMeridian) circle, leading ) ;
			} catch ( ParameterNotValidException ee ) {
				String msg ;

				msg = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
				msg = MessageFormat.format( msg, new Object[] { "\""+getBegin().getReference().getCircle()+"\"", ee.toString() } ) ;
				log.warn( msg ) ;

				begin = -Math.rad90 ;
			}
		}
		try {
			end = AstrolabeFactory.valueOf( getEnd().getImmediate() ) ;
		} catch ( ParameterNotValidException e ) {
			Object circle ;
			boolean leading ;

			circle = Registry.retrieve( getEnd().getReference().getCircle() ) ;
			leading = getBegin().getReference().getNode().equals( ApplicationConstant.AV_CIRCLE_LEADING ) ;
			try {
				end = circle instanceof CircleParallel?
						intersect( (CircleParallel) circle, leading ):
							intersect( (CircleMeridian) circle, leading ) ;
			} catch ( ParameterNotValidException ee ) {
				String msg ;

				msg = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
				msg = MessageFormat.format( msg, new Object[] { "\""+getBegin().getReference().getCircle()+"\"", ee.toString() } ) ;
				log.warn( msg ) ;

				end = Math.rad90 ;
			}
		}

		if ( getName() != null ) {
			Registry.register( getName(), this ) ;
		}

		if ( getFov() != null ) {
			try {
				fov = (Polygon) Registry.retrieve( getFov() ) ;
			} catch ( ParameterNotValidException e ) {
				fov = new GeometryFactory().createPolygon( null, null ) ;
			}
			Registry.register( getFov(), ApplicationHelper.extendFov( fov, list() ) ) ;		
		}
	}

	public double[] project( double al ) {
		return project( al, 0 ) ;
	}

	public double[] project( double al, double shift ) {
		double[] xy ;
		Vector v, t ;

		xy = projector.project( az, al ) ;
		v = new Vector( xy[0], xy[1] ) ;

		if ( shift != 0 ) {
			xy = tangent( al ) ;
			t = new Vector( xy[0], xy[1] ) ;
			t.apply( new double[] { 0, -1, 0, 1, 0, 0, 0, 0, 1 } ) ; // rotate 90 degrees counter clockwise
			t.scale( shift ) ;
			v.add( t ) ;
		}

		return new double[] { v.x, v.y } ;
	}

	public double[] convert( double angle ) {
		return projector.convert( az, angle ) ;
	}

	public double unconvert( double[] eq ) {
		return projector.unconvert( eq )[1] ;
	}

	public double[] tangent( double al ) {
		Vector a, b ;
		double d, xy[] ;

		d = CAACoordinateTransformation.DegreesToRadians( 10./3600 ) ;

		xy = projector.project( az, al+d ) ;
		a = new Vector( xy[0], xy[1] ) ;
		xy = projector.project( az, al ) ;
		b = new Vector( xy[0], xy[1] ) ;

		a.sub( b ) ;

		return new double[] { a.x, a.y } ;
	}

	public java.util.Vector<double[]> list( java.util.Vector<Double> list ) {
		return list( null, begin, end, 0 ) ;
	}

	public java.util.Vector<double[]> list( java.util.Vector<Double> list, double shift ) {
		return list( null, begin, end, shift ) ;
	}

	public java.util.Vector<double[]> list( java.util.Vector<Double> list, double begin, double end, double shift ) {
		java.util.Vector<double[]> r = new java.util.Vector<double[]>() ;
		double g ;

		r.add( project( begin, shift ) ) ;
		if ( list != null ) {
			list.add( begin ) ;
		}

		g = mapIndexToRange( begin, end ) ;
		for ( double al=begin>end?begin-g:begin+g ; begin>end?al>end:al<end ; al=begin>end?al-interval:al+interval ) {
			r.add( project( al, shift ) ) ;
			if ( list != null ) {
				list.add( al ) ;
			}
		}

		r.add( project( end, shift ) ) ;
		if ( list != null ) {
			list.add( end ) ;
		}

		return r ;
	}

	public java.util.Vector<double[]> list() {
		return list( null, begin, end, 0 ) ;
	}

	public java.util.Vector<double[]> list( double shift ) {
		return list( null, begin, end, shift ) ;
	}

	public java.util.Vector<double[]> list( double begin, double end, double shift ) {
		return list( null, begin, end, shift ) ;
	}

	public void headPS( PostscriptStream ps ) {
		String importance ;

		importance = ApplicationHelper.getPreferencesKV(
				ApplicationHelper.getClassNode( this, getName(), ApplicationConstant.PN_CIRCLE_IMPORTANCE ),
				getImportance(), DEFAULT_IMPORTANCE ) ;

		ApplicationHelper.emitPSImportance( ps, importance ) ;
	}

	public void emitPS( PostscriptStream ps ) {
		Geometry gf, gl ;

		gf = ApplicationHelper.getFovGlobal() ;
		gl = ApplicationHelper.jtsToPolygon( list() ) ;

		emitPS( ps, !gf.covers( gl ) ) ;
	}

	public void emitPS( PostscriptStream ps, boolean cut ) {
		ListCutter cutter ;
		Geometry fov ;
		astrolabe.model.CircleMeridian peer ;
		CircleMeridian circle ;
		double[] lob, loe ;
		double a, ab, ae ;
		astrolabe.model.Annotation aC = null ;
		astrolabe.model.AnnotationCurved aCM, aCC ; // model/cut annotation
		astrolabe.model.AnnotationStraight aSM, aSC ; // model/cut annotation
		astrolabe.model.Text tC ;
		java.util.Vector<double[]> l ;
		double[] xy ;
		String m ;

		if ( cut ) {
			fov = ApplicationHelper.getFovGlobal() ;
			cutter = new ListCutter( list(), fov ) ;
			for ( List<double[]> s : cutter.segmentsIntersecting( true ) ) {
				peer = new astrolabe.model.CircleMeridian() ;
				// astrolabe.model.AngleType
				peer.setAngle( new astrolabe.model.Angle() ) ;
				peer.getAngle().setRational( new astrolabe.model.Rational() ) ;

				peer.setBegin( new astrolabe.model.Begin() ) ;
				// astrolabe.model.AngleType
				peer.getBegin().setImmediate( new astrolabe.model.Immediate() ) ;
				peer.getBegin().getImmediate().setRational( new astrolabe.model.Rational() ) ;
				peer.setEnd( new astrolabe.model.End() ) ;
				// astrolabe.model.AngleType
				peer.getEnd().setImmediate( new astrolabe.model.Immediate() ) ;
				peer.getEnd().getImmediate().setRational( new astrolabe.model.Rational() ) ;
				if ( getName() != null ) {
					peer.setName( ApplicationConstant.GC_NS_CUT+getName() ) ;
				}

				lob = projector.unproject( s.get( 0 ) ) ;
				lob[0] = ApplicationHelper.mapTo0To360Range( lob[0] ) ;
				xy = s.get( s.size()-1 ) ;
				m = ApplicationHelper.getFovNSMark( xy ) ;
				loe = projector.unproject( xy ) ; 
				loe[0] = ApplicationHelper.mapTo0To360Range( loe[0] ) ;

				try {
					a = CAACoordinateTransformation.RadiansToDegrees( az ) ;
					ab = CAACoordinateTransformation.RadiansToDegrees( lob[1] ) ;
					ae = CAACoordinateTransformation.RadiansToDegrees( loe[1] ) ;
					peer.getAngle().getRational().setValue( a ) ;
					peer.getBegin().getImmediate().getRational().setValue( ab ) ;
					peer.getEnd().getImmediate().getRational().setValue( ae ) ;
					AstrolabeFactory.modelOf( peer ) ;
				} catch ( ParameterNotValidException e ) {
					throw new RuntimeException( e.toString() ) ;
				}

				peer.setImportance( getImportance() ) ;

				peer.setDial( getDial() ) ;

				try {
					for ( astrolabe.model.Annotation annotation : getAnnotation() ) {
						aCM = annotation.getAnnotationCurved() ;
						if ( aCM != null && aCM.getName() != null ) {
							aCC = new astrolabe.model.AnnotationCurved() ;
							aCC.setName( m+aCM.getName() ) ;

							for ( astrolabe.model.Text tM : aCM.getText() ) {
								tC = new astrolabe.model.Text() ;
								tC.setName( m+tM.getName() ) ;
								tC.setValue( tM.getValue() ) ;
								AstrolabeFactory.modelOf( tC ) ;

								tC.setName( tM.getName() ) ;

								tC.setSubscript( tM.getSubscript() ) ;
								tC.setSuperscript( tM.getSuperscript() ) ;

								aCC.addText( tC ) ;
							}

							AstrolabeFactory.modelOf( aCC ) ;

							aCC.setName( aCM.getName() ) ;
							// setPurpose already done by AstrolabeFactory.modelOf()
							// aCC.setPurpose( aCM.getPurpose() ) ;
							// setAnchor already done by AstrolabeFactory.modelOf()
							// aCC.setAnchor( aCM.getAnchor() ) ;
							aCC.setReverse( aCM.getReverse() ) ;
							aCC.setDistance( aCM.getDistance() ) ;

							aC = new astrolabe.model.Annotation() ;
							aC.setAnnotationCurved( aCC ) ;
						}
						aSM = annotation.getAnnotationStraight() ;
						if ( aSM != null && aSM.getName() != null ) {
							aSC = new astrolabe.model.AnnotationStraight() ;
							aSC.setName( m+aSM.getName() ) ;

							for ( astrolabe.model.Text tM : aSM.getText() ) {
								tC = new astrolabe.model.Text() ;
								tC.setName( m+tM.getName() ) ;
								tC.setValue( tM.getValue() ) ;
								AstrolabeFactory.modelOf( tC ) ;

								tC.setName( tM.getName() ) ;

								tC.setSubscript( tM.getSubscript() ) ;
								tC.setSuperscript( tM.getSuperscript() ) ;

								aSC.addText( tC ) ;
							}

							AstrolabeFactory.modelOf( aSC ) ;

							aSC.setName( aSM.getName() ) ;
							// setPurpose already done by AstrolabeFactory.modelOf()
							// aSC.setPurpose( aSM.getPurpose() ) ;
							// setAnchor already done by AstrolabeFactory.modelOf()
							// aSC.setAnchor( aSM.getAnchor() ) ;
							aSC.setReverse( aSM.getReverse() ) ;
							aSC.setRadiant( aSM.getRadiant() ) ;

							aC = new astrolabe.model.Annotation() ;
							aC.setAnnotationStraight( aSC ) ;
						}
						peer.addAnnotation( aC ) ;
					}
				} catch ( ParameterNotValidException e ) {
					throw new RuntimeException( e.toString() ) ;
				}

				try {
					circle = new CircleMeridian( peer, projector ) ;

					ps.operator.gsave() ;

					circle.headPS( ps ) ;
					circle.emitPS( ps, false ) ;
					circle.tailPS( ps ) ;

					ps.operator.grestore() ;
				} catch ( ParameterNotValidException e ) {}
			}
		} else {
			l = list() ;
			ps.operator.mark() ;
			for ( int n=l.size() ; n>0 ; n-- ) {
				xy = l.get( n-1 ) ;
				ps.push( xy[0] ) ;
				ps.push( xy[1] ) ;
			}
			try {
				ps.custom( ApplicationConstant.PS_PROLOG_POLYLINE ) ;

				// halo stroke
				ps.operator.currentlinewidth() ;
				ps.operator.dup();
				ps.push( (Double) ( Registry.retrieve( ApplicationConstant.PK_CHART_HALOMAX ) ) ) ; 
				ps.push( (Double) ( Registry.retrieve( ApplicationConstant.PK_CHART_HALOMIN ) ) ) ; 
				ps.push( (Double) ( Registry.retrieve( ApplicationConstant.PK_CHART_HALO ) ) ) ; 
				ps.custom( ApplicationConstant.PS_PROLOG_HALO ) ;
				ps.operator.mul( 2 ) ;
				ps.operator.add() ;
				ps.operator.gsave() ;
				ps.operator.setlinewidth() ;
				ps.operator.setlinecap( 2 ) ;
				ps.operator.setgray( 1 ) ;
				ps.operator.stroke() ;
				ps.operator.grestore() ;
			} catch ( ParameterNotValidException e ) {
				throw new RuntimeException( e.toString() ) ;
			}
			ps.operator.gsave() ;
			ps.operator.stroke() ;
			ps.operator.grestore() ;

			// Dial processing.
			if ( getDial() != null ) {
				try {
					PostscriptEmitter dial ;

					ps.operator.gsave() ;

					dial = AstrolabeFactory.companionOf( getDial(), (Baseline) this ) ;
					dial.headPS( ps ) ;
					dial.emitPS( ps ) ;
					dial.tailPS( ps ) ;

					ps.operator.grestore() ;
				} catch ( ParameterNotValidException e ) {
					throw new RuntimeException( e.toString() ) ;
				}
			}

			if ( getAnnotation() != null ) {
				try {
					ApplicationHelper.emitPS( ps, getAnnotation() ) ;
				} catch ( ParameterNotValidException e ) {
					throw new RuntimeException( e.toString() ) ;
				}
			}
		}
	}

	public void tailPS( PostscriptStream ps ) {
	}

	private double[] transform() {
		double[] r = new double[3] ;
		double blB, vlA, vlD, vlAl, vlDe ;
		double gneq[], gnho[], gnaz ;
		double rdhoC[], rdST, rdLa ;

		rdhoC = zenit() ;
		rdST = rdhoC[0] ;
		rdLa = rdhoC[1] ;

		blB = Math.rad90-rdLa;//horizon.getLa() ;
		if ( blB==0 ) {	// prevent infinity from tan
			blB = Math.lim0 ;
		}

		// convert actual gn into local
		gneq = projector.convert( this.az, 0 ) ;
		gnho = ApplicationHelper.equatorial2Horizontal( rdST/*horizon.getST()*/-gneq[0], gneq[1], rdLa/*horizon.getLa()*/ ) ;
		gnaz = gnho[0] ;
		if ( gnaz==0 ) {	// prevent infinity from tan
			gnaz = Math.lim0 ;
		}


		if ( java.lang.Math.tan( gnaz )>0 ) {	// QI, QIII
			vlAl = ApplicationHelper.mapTo0To90Range( gnaz ) ;

			// Rule of Napier : cos(90-a) = sin(al)*sin(c)
			vlA = java.lang.Math.acos( java.lang.Math.sin( vlAl )*java.lang.Math.sin( blB ) ) ;
			// Rule of Napier : cos(be) = cot(90-a)*cot(c)
			vlDe = java.lang.Math.acos( Math.truncate( Math.cot( vlA )*Math.cot( blB ) ) ) ;

			r[1] = rdST-Math.rad180+vlDe;//horizon.getST()-Math.rad180+vlDe ;
		} else {							// QII, QIV
			vlAl = Math.rad90-ApplicationHelper.mapTo0To90Range( gnaz ) ;

			vlA = java.lang.Math.acos( java.lang.Math.sin( vlAl )*java.lang.Math.sin( blB ) ) ;
			vlDe = java.lang.Math.acos( Math.truncate( Math.cot( vlA )*Math.cot( blB ) ) ) ;

			r[1] = rdST+Math.rad180-vlDe;//horizon.getST()+Math.rad180-vlDe ;
		}

		r[0] = Math.rad90-vlA ;

		// Rule of Napier : cos(c) = sin(90-a)*sin(90-b)
		// sin(90-b) = cos(c):sin(90-a)
		vlD = Math.rad90-java.lang.Math.asin( Math.truncate( java.lang.Math.cos( blB )/java.lang.Math.sin( vlA ) ) ) ;

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
		double rdhoC[], rdST, rdLa ;

		rdhoC = zenit() ;
		rdST = rdhoC[0] ;
		rdLa = rdhoC[1] ;

		// convert actual gn into local
		gneq = projector.convert( this.az, 0 ) ;
		gnho = ApplicationHelper.equatorial2Horizontal( rdST/*horizon.getST()*/-gneq[0], gneq[1], rdLa/*horizon.getLa()*/ ) ;
		gnaz = gnho[0] ;
		if ( gnaz==0 ) {	// prevent infinity from tan
			gnaz = Math.lim0 ;
		}

		// convert vl az into gn
		vlD = transform()[2] ;
		gnal = java.lang.Math.tan( gnaz )>0?vlaz-vlD:vlaz+vlD ;
		// convert gn az into al
		gnal = gnaz>Math.rad180?gnal-Math.rad90:-( gnal-Math.rad270 ) ;
		// map range from -180 to 180
		gnal = gnal>Math.rad180?gnal-Math.rad360:gnal ;

		return gnal ;
	}

	public double intersect( CircleParallel gn, boolean leading ) throws ParameterNotValidException {
		double blA, blB, blGa, gnB ;
		double[] gneqA, gneqO, gnhoA, gnhoO ;
		double rdhoC[], rdST, gnhoC[], gnST, gnLa ;
		double inaz[], gnaz, gnal ;

		rdhoC = zenit() ;
		rdST = rdhoC[0] ;
		gnhoC = gn.zenit() ;
		gnST = gnhoC[0] ;
		gnLa = gnhoC[1] ;

		gnal = AstrolabeFactory.valueOf( gn.getAngle() ) ;

		gnB = Math.rad90-gnal ;
		blA = Math.rad90-transformParallelLa() ;
		blB = Math.rad90-gnLa ;//gn.dotDot().getLa() ;
		blGa = gnST/*gn.dotDot().getST()*/-transformParallelST() ;

		inaz = CircleParallel.intersection( Math.rad90, gnB, blA, blB, blGa ) ;

		// convert rd az into al
		inaz[0] = transformMeridianAl( inaz[0] ) ;
		inaz[1] = transformMeridianAl( inaz[1] ) ;

		// unconvert local gn into actual
		gneqA = ApplicationHelper.horizontal2Equatorial( inaz[2], gnal, gnLa/*gn.dotDot().getLa()*/ ) ;
		gneqA[0] = rdST-gneqA[0];//horizon.getST()-gneqA[0] ;
		gnhoA = projector.unconvert( gneqA ) ;

		gneqO = ApplicationHelper.horizontal2Equatorial( inaz[3], gnal, gnLa/*gn.dotDot().getLa()*/ ) ;
		gneqO[0] = rdST-gneqO[0];//horizon.getST()-gneqO[0] ;
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

		if ( ! gn.probe( ApplicationHelper.mapToNTo360Range( gnaz ) ) ) {
			throw new ParameterNotValidException( new Double( gnaz ).toString() ) ;
		}

		return leading?inaz[1]:inaz[0] ;
	}

	public double intersect( CircleMeridian gn, boolean leading ) throws ParameterNotValidException {
		double blA, blB, blGa ;
		double inaz[], gnal ;

		blA = Math.rad90-transformParallelLa() ;
		blB = Math.rad90-gn.transformParallelLa() ;
		blGa = gn.transformParallelST()-transformParallelST() ;

		inaz = CircleParallel.intersection( Math.rad90, Math.rad90, blA, blB, blGa ) ;

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

		if ( ! gn.probe( ApplicationHelper.mapToNTo360Range( gnal ) ) ) {
			throw new ParameterNotValidException( new Double( gnal ).toString() ) ;
		}

		return leading?inaz[1]:inaz[0] ;
	}

	public boolean probe( double al ) {
		return CircleParallel.probe( al, begin, end ) ;
	}

	public double mapIndexToScale( int index ) {
		return CircleParallel.mapIndexToAngleOfScale( index, interval, begin, end ) ;
	}

	public double mapIndexToScale( double span ) {
		return CircleParallel.mapIndexToAngleOfScale( 0, span, begin, end ) ;
	}

	public double mapIndexToScale( int index, double span ) {
		return CircleParallel.mapIndexToAngleOfScale( index, span, begin, end ) ;
	}

	public double[] zenit() {
		return projector.convert( 0, Math.rad90 ) ;
	}

	public double mapIndexToRange() {
		return CircleParallel.gap( 0, interval, begin , end ) ;
	}

	public double mapIndexToRange( double begin, double end ) {
		return CircleParallel.gap( 0, interval, begin , end ) ;
	}

	public double mapIndexToRange( int index, double begin, double end ) {
		return CircleParallel.gap( index, interval, begin , end ) ;
	}
}
