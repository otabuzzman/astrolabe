
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

	private final static double DEFAULT_INTERVAL = 1 ;

	private Projector projector ;

	private double interval ;

	private double az ;

	private double begin ;
	private double end ;

	public CircleMeridian( Peer peer, Projector projector ) {
		setup( peer, projector ) ;
	}

	public void setup( Peer peer, Projector projector ) {
		MessageCatalog m ;
		String key ;

		m = new MessageCatalog( ApplicationConstant.GC_APPLICATION ) ;

		peer.setupCompanion( this ) ;

		this.projector = projector ;

		interval = Configuration.getValue(
				Configuration.getClassNode( this, getName(), null ),
				ApplicationConstant.PK_CIRCLE_INTERVAL, DEFAULT_INTERVAL ) ;

		az = AstrolabeFactory.valueOf( getAngle() ) ;
		key = m.message( ApplicationConstant.LK_CIRCLE_AZIMUTH ) ;
		AstrolabeRegistry.registerDMS( key, az ) ;
		AstrolabeRegistry.registerHMS( key, az ) ;

		if ( getBegin().getImmediate() == null ) {
			Object circle ;
			boolean leading ;

			leading = getBegin().getReference().getNode().equals( ApplicationConstant.AV_CIRCLE_LEADING ) ;
			try {
				circle = Registry.retrieve( getBegin().getReference().getCircle() ) ;
				begin = circle instanceof CircleParallel?
						intersect( (CircleParallel) circle, leading ):
							intersect( (CircleMeridian) circle, leading ) ;
			} catch ( ParameterNotValidException e ) {
				String msg ;

				msg = m.message( ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
				msg = MessageFormat.format( msg, new Object[] { e.getMessage(), "\""+getBegin().getReference().getCircle()+"\"" } ) ;
				log.warn( msg ) ;

				begin = -90 ;
			}
		} else {
			begin = AstrolabeFactory.valueOf( getBegin().getImmediate() ) ;
		}

		if ( getEnd().getImmediate() == null ) {
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

				msg = m.message( ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
				msg = MessageFormat.format( msg, new Object[] { e.getMessage(), "\""+getEnd().getReference().getCircle()+"\"" } ) ;
				log.warn( msg ) ;

				end = 90 ;
			}
		} else {
			end = AstrolabeFactory.valueOf( getEnd().getImmediate() ) ;
		}

		if ( getName() != null ) {
			Registry.register( getName(), this ) ;
		}

		if ( getFov() != null ) {
			LineString circle ;

			circle = new GeometryFactory().createLineString(
					new JTSCoordinateArraySequence( list() ) ) ;

			if ( circle.isRing() ) {
				Registry.register( getFov(),
						new GeometryFactory().createPolygon(
								new GeometryFactory().createLinearRing( circle.getCoordinateSequence() ), null ) ) ;
			} else { // extend
			}
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
		double xy[] ;

		xy = projector.project( az, al+10./3600 ) ;
		a = new Vector( xy[0], xy[1] ) ;
		xy = projector.project( az, al ) ;
		b = new Vector( xy[0], xy[1] ) ;

		a.sub( b ) ;

		return new double[] { a.x, a.y } ;
	}

	public List<double[]> list( List<Double> list ) {
		return list( null, begin, end, 0 ) ;
	}

	public List<double[]> list( List<Double> list, double shift ) {
		return list( null, begin, end, shift ) ;
	}

	public List<double[]> list( List<Double> list, double begin, double end, double shift ) {
		List<double[]> r = new java.util.Vector<double[]>() ;
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

	public List<double[]> list() {
		return list( null, begin, end, 0 ) ;
	}

	public List<double[]> list( double shift ) {
		return list( null, begin, end, shift ) ;
	}

	public List<double[]> list( double begin, double end, double shift ) {
		return list( null, begin, end, shift ) ;
	}

	public void headPS( AstrolabePostscriptStream ps ) {
		ElementImportance importance ;

		importance = new ElementImportance( getImportance() ) ;
		importance.headPS( ps ) ;
		importance.emitPS( ps ) ;
		importance.tailPS( ps ) ;
	}

	public void emitPS( AstrolabePostscriptStream ps ) {
		Geometry fov, circle ;

		fov = (Geometry) AstrolabeRegistry.retrieve( ApplicationConstant.GC_FOVUNI ) ;
		circle = new GeometryFactory().createLineString(
				new JTSCoordinateArraySequence( list() ) ) ;

		emitPS( ps, !fov.covers( circle ) ) ;
	}

	public void emitPS( AstrolabePostscriptStream ps, boolean cut ) {
		ListCutter cutter ;
		Geometry fov ;
		astrolabe.model.CircleMeridian peer ;
		CircleMeridian circle ;
		double[] lob, loe ;
		astrolabe.model.Annotation aC = null ;
		astrolabe.model.AnnotationCurved aCM, aCC ; // model/cut annotation
		astrolabe.model.AnnotationStraight aSM, aSC ; // model/cut annotation
		astrolabe.model.Text tC ;
		List<double[]> l ;
		double[] xy ;
		String ns ;

		if ( cut ) {
			fov = (Geometry) AstrolabeRegistry.retrieve( ApplicationConstant.GC_FOVUNI ) ;
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
				xy = s.get( s.size()-1 ) ;
				loe = projector.unproject( xy ) ; 

				ns = new JTSCoordinate( xy ).quadrant(
						new JTSCoordinate( fov.getEnvelope().getCoordinates()[0] ),
						new JTSCoordinate( fov.getEnvelope().getCoordinates()[2] ) )+":" ;

				peer.getAngle().getRational().setValue( az ) ;
				peer.getBegin().getImmediate().getRational().setValue( lob[1] ) ;
				peer.getEnd().getImmediate().getRational().setValue( loe[1] ) ;
				AstrolabeFactory.modelOf( peer, false ) ;

				peer.setImportance( getImportance() ) ;

				peer.setDial( getDial() ) ;

				try {
					for ( astrolabe.model.Annotation annotation : getAnnotation() ) {
						aCM = annotation.getAnnotationCurved() ;
						if ( aCM != null && aCM.getName() != null ) {
							aCC = new astrolabe.model.AnnotationCurved() ;
							aCC.setName( ns+aCM.getName() ) ;

							for ( astrolabe.model.Text tM : aCM.getText() ) {
								tC = new astrolabe.model.Text() ;
								tC.setName( ns+tM.getName() ) ;
								tC.setValue( tM.getValue() ) ;
								AstrolabeFactory.modelOf( tC, false ) ;

								tC.setName( tM.getName() ) ;

								tC.setSubscript( tM.getSubscript() ) ;
								tC.setSuperscript( tM.getSuperscript() ) ;

								aCC.addText( tC ) ;
							}

							AstrolabeFactory.modelOf( aCC, false ) ;

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
							aSC.setName( ns+aSM.getName() ) ;

							for ( astrolabe.model.Text tM : aSM.getText() ) {
								tC = new astrolabe.model.Text() ;
								tC.setName( ns+tM.getName() ) ;
								tC.setValue( tM.getValue() ) ;
								AstrolabeFactory.modelOf( tC, false ) ;

								tC.setName( tM.getName() ) ;

								tC.setSubscript( tM.getSubscript() ) ;
								tC.setSuperscript( tM.getSuperscript() ) ;

								aSC.addText( tC ) ;
							}

							AstrolabeFactory.modelOf( aSC, false ) ;

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

						peer.validate() ;
					}
				} catch ( ValidationException e ) {
					throw new RuntimeException( e.toString() ) ;
				}

				circle = new CircleMeridian( peer, projector ) ;

				ps.operator.gsave() ;

				circle.headPS( ps ) ;
				circle.emitPS( ps, false ) ;
				circle.tailPS( ps ) ;

				ps.operator.grestore() ;
			}
		} else {
			l = list() ;
			ps.operator.mark() ;
			for ( int n=l.size() ; n>0 ; n-- ) {
				xy = l.get( n-1 ) ;
				ps.push( xy[0] ) ;
				ps.push( xy[1] ) ;
			}

			ps.custom( ApplicationConstant.PS_CUSTOM_POLYLINE ) ;

			// halo stroke
			ps.operator.currentlinewidth() ;
			ps.operator.dup();
			ps.push( (Double) ( AstrolabeRegistry.retrieve( ApplicationConstant.PK_CHART_HALOMAX ) ) ) ; 
			ps.push( (Double) ( AstrolabeRegistry.retrieve( ApplicationConstant.PK_CHART_HALOMIN ) ) ) ; 
			ps.push( (Double) ( AstrolabeRegistry.retrieve( ApplicationConstant.PK_CHART_HALO ) ) ) ; 
			ps.custom( ApplicationConstant.PS_CUSTOM_HALO ) ;
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

	private double[] transform() {
		CAA2DCoordinate c ;
		double[] r = new double[3] ;
		double blB, vlA, vlD, vlAl, vlDe ;
		double gneq[], gnho[] = new double[2], gnaz ;
		double rdhoC[], rdST, rdLa ;

		rdhoC = zenit() ;
		rdST = rdhoC[0] ;
		rdLa = rdhoC[1] ;

		blB = 90-rdLa;//horizon.getLa() ;
		if ( blB==0 ) {	// prevent infinity from tan
			blB = Math.lim0 ;
		}

		// convert actual gn into local
		gneq = projector.convert( this.az, 0 ) ;
		c = CAACoordinateTransformation.Equatorial2Horizontal(
				CAACoordinateTransformation.DegreesToHours( rdST/*horizon.getST()*/-gneq[0] ), gneq[1], rdLa/*horizon.getLa()*/ ) ;
		gnho[0] = c.X() ;
		gnho[1] = c.Y() ;
		gnaz = gnho[0] ;
		if ( gnaz==0 ) {	// prevent infinity from tan
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

			r[1] = rdST-180+vlDe;//horizon.getST()-180+vlDe ;
		} else {							// QII, QIV
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

			r[1] = rdST+180-vlDe;//horizon.getST()+180-vlDe ;
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
		double gneq[], gnho[] = new double[2], gnaz, gnal, vlD ;
		double rdhoC[], rdST, rdLa ;

		rdhoC = zenit() ;
		rdST = rdhoC[0] ;
		rdLa = rdhoC[1] ;

		// convert actual gn into local
		gneq = projector.convert( this.az, 0 ) ;
		c = CAACoordinateTransformation.Equatorial2Horizontal(
				CAACoordinateTransformation.DegreesToHours( rdST/*horizon.getST()*/-gneq[0] ), gneq[1], rdLa/*horizon.getLa()*/ ) ;
		gnho[0] = c.X() ;
		gnho[1] = c.Y() ;
		gnaz = gnho[0] ;
		if ( gnaz==0 ) {	// prevent infinity from tan
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

		rdhoC = zenit() ;
		rdST = rdhoC[0] ;
		gnhoC = gn.zenit() ;
		gnST = gnhoC[0] ;
		gnLa = gnhoC[1] ;

		gnal = AstrolabeFactory.valueOf( gn.getAngle() ) ;

		gnB = 90-gnal ;
		blA = 90-transformParallelLa() ;
		blB = 90-gnLa ;//gn.dotDot().getLa() ;
		blGa = gnST/*gn.dotDot().getST()*/-transformParallelST() ;

		inaz = CircleParallel.intersection( 90, gnB, blA, blB, blGa ) ;

		// convert rd az into al
		inaz[0] = transformMeridianAl( inaz[0] ) ;
		inaz[1] = transformMeridianAl( inaz[1] ) ;

		// unconvert local gn into actual
		cA = CAACoordinateTransformation.Horizontal2Equatorial( inaz[2], gnal, gnLa/*gn.dotDot().getLa()*/ ) ;
		gneqA[0] = CAACoordinateTransformation.HoursToDegrees( cA.X() ) ;
		gneqA[1] = cA.Y() ;
		gneqA[0] = rdST-gneqA[0];//horizon.getST()-gneqA[0] ;
		gnhoA = projector.unconvert( gneqA ) ;

		cO = CAACoordinateTransformation.Horizontal2Equatorial( inaz[3], gnal, gnLa/*gn.dotDot().getLa()*/ ) ;
		gneqO[0] = CAACoordinateTransformation.HoursToDegrees( cO.X() ) ;
		gneqO[1] = cO.Y() ;
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

		if ( ! gn.probe( gnaz ) ) {
			throw new ParameterNotValidException( Double.toString( gnaz ) ) ;
		}

		return leading?inaz[1]:inaz[0] ;
	}

	public double intersect( CircleMeridian gn, boolean leading ) throws ParameterNotValidException {
		double blA, blB, blGa ;
		double inaz[], gnal ;

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

		if ( ! gn.probe( gnal ) ) {
			throw new ParameterNotValidException( Double.toString( gnal ) ) ;
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
		return projector.convert( 0, 90 ) ;
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
