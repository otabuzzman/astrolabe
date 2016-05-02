
package astrolabe;

import java.util.List;

import org.exolab.castor.xml.ValidationException;

import com.vividsolutions.jts.geom.Geometry;

import caa.CAADate;
import caa.CAAMoon;

@SuppressWarnings("serial")
public class BodyMoon extends astrolabe.model.BodyMoon implements PostscriptEmitter, Baseline {

	// configuration key (CK_)
	private final static String CK_INTERVAL			= "interval" ;
	private final static String CK_STRETCH			= "stretch" ;

	private final static String CK_HALO				= "halo" ;
	private final static String CK_HALOMIN			= "halomin" ;
	private final static String CK_HALOMAX			= "halomax" ;

	private final static double DEFAULT_INTERVAL	= 1 ;
	private final static double DEFAULT_STRETCH		= 0 ;

	private final static double DEFAULT_HALO		= 4 ;
	private final static double DEFAULT_HALOMIN		= .08 ;
	private final static double DEFAULT_HALOMAX		= .4 ;

	private Projector projector ;

	public BodyMoon( Projector projector ) {
		this.projector = projector ;
	}

	public double[] epoch() {
		double epochgc, epochlo ;
		double jdAy, jdOy ;
		CAADate epoch ;
		long year ;

		epochgc = ( (Double) Registry.retrieve( ApplicationConstant.GC_EPOCH ) ).doubleValue() ;

		epoch = new CAADate() ;
		epoch.Set( epochgc, true ) ;

		year = epoch.Year() ;
		epoch.Set( year, 1, 1, 0, 0, 0, true ) ;
		jdAy = epoch.Julian() ;
		epoch.Set( year, 12, 31, 0, 0, 0, true ) ;
		jdOy = epoch.Julian() ;

		if ( getEpoch() != null ) {
			epochlo = ApplicationFactory.valueOf( getEpoch() ) ;
			epoch.Set( epochlo, true ) ;

			year = epoch.Year() ;
			epoch.Set( year, 1, 1, 0, 0, 0, true ) ;
			jdAy = epoch.Julian() ;
			epoch.Set( year, 12, 31, 0, 0, 0, true ) ;
			jdOy = epoch.Julian() ;

			epoch.Set( epochlo, true ) ;
			if ( getEpoch().getA() != null ) {
				jdAy = ApplicationFactory.valueOf( getEpoch().getA() ) ;
				jdOy = epoch.Julian() ;
			}
			if ( getEpoch().getO() != null ) {
				if ( getEpoch().getA() == null )
					jdAy = epoch.Julian() ;
				jdOy = ApplicationFactory.valueOf( getEpoch().getO() ) ;
			}
		}

		epoch.delete() ;

		return new double[] { jdAy, jdOy } ;
	}

	public void headPS( ApplicationPostscriptStream ps ) {
		GSPaintStroke nature ;

		nature = new GSPaintStroke( getNature() ) ;

		nature.headPS( ps ) ;
		nature.emitPS( ps ) ;
		nature.tailPS( ps ) ;
	}

	public void emitPS( ApplicationPostscriptStream ps ) {
		emitPS( ps, true ) ;
	}

	public void emitPS( ApplicationPostscriptStream ps, boolean cut ) {
		Configuration conf ;
		ListCutter cutter ;
		Geometry fov ;
		astrolabe.model.BodyMoon peer ;
		BodyMoon body ;
		List<int[]> listid ;
		List<Double> listjd ;
		double jdAe, jdOe ;
		List<double[]> l ;
		double[] epoch, xy ;

		epoch = epoch() ;

		if ( cut ) {
			fov = (Geometry) Registry.retrieve( ApplicationConstant.GC_FOVEFF ) ;
			if ( fov == null ) {
				fov = (Geometry) Registry.retrieve( ApplicationConstant.GC_FOVUNI ) ;
			}

			listjd = new java.util.Vector<Double>() ;
			cutter = new ListCutter( list( listjd, epoch[0], epoch[1], 0 ), fov ) ;

			listid = new java.util.Vector<int[]>() ;
			cutter.segmentsInterior( listid ) ;
			for ( int[] jdid : listid ) {
				jdAe = listjd.get( jdid[0] ) ;
				jdOe = listjd.get( jdid[1] ) ;

				peer = new astrolabe.model.BodyMoon() ;
				if ( getName() == null )
					peer.setName( ApplicationConstant.GC_NS_CUT ) ;
				else
					peer.setName( ApplicationConstant.GC_NS_CUT+getName() ) ;

				peer.setStretch( getStretch() ) ;
				peer.setNature( getNature() ) ;

				peer.setAnnotation( getAnnotation() ) ;

				peer.setEpoch( new astrolabe.model.Epoch() ) ;
				peer.getEpoch().setA( new astrolabe.model.A() ) ;
				peer.getEpoch().getA().setJD( new astrolabe.model.JD() ) ;
				peer.getEpoch().setJD( new astrolabe.model.JD() ) ;

				peer.getEpoch().getA().getJD().setValue( jdAe ) ;
				peer.getEpoch().getJD().setValue( jdOe ) ;

				peer.setDialDay( getDialDay() ) ;

				try {
					peer.validate() ;
				} catch ( ValidationException e ) {
					throw new RuntimeException( e.toString() ) ;
				}

				body = new BodyMoon( projector ) ;
				peer.setupCompanion( body ) ;

				ps.operator.gsave();

				body.headPS( ps ) ;
				body.emitPS( ps, false ) ;
				body.tailPS( ps ) ;

				ps.operator.grestore() ;
			}
		} else {
			l = list( null, epoch[0], epoch[1], 0 ) ;
			ps.array( true ) ;
			for ( int n=0 ; n<l.size() ; n++ ) {
				xy = l.get( n ) ;
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

			if ( getDialDay() != null ) {
				PostscriptEmitter dial ;

				dial = new DialDay( this ) ;
				getDialDay().setupCompanion( dial ) ;

				ps.operator.gsave() ;

				dial.headPS( ps ) ;
				dial.emitPS( ps ) ;
				dial.tailPS( ps ) ;

				ps.operator.grestore() ;
			}

			if ( getAnnotation() != null ) {
				PostscriptEmitter annotation ;

				for ( int i=0 ; i<getAnnotationCount() ; i++ ) {
					annotation = ApplicationFactory.companionOf( getAnnotation( i ) ) ;

					ps.operator.gsave() ;

					annotation.headPS( ps ) ;
					annotation.emitPS( ps ) ;
					annotation.tailPS( ps ) ;

					ps.operator.grestore() ;
				}
			}
		}
	}

	public void tailPS( ApplicationPostscriptStream ps ) {
	}

	public double[] project( double jd, double shift ) {
		double[] ec, xy ;
		Vector v, t ;

		ec = convert( jd ) ;
		xy = projector.project( ec ) ;
		v = new Vector( xy[0], xy[1] ) ;

		if ( shift != 0 ) {
			xy = tangent( jd ) ;
			t = new Vector( xy[0], xy[1] ) ;
			t.apply( new double[] { 0, -1, 0, 1, 0, 0, 0, 0, 1 } ) ; // rotate 90 degrees counter clockwise
			t.scale( shift ) ;
			v.add( t ) ;
		}

		return new double[] { v.x, v.y } ;
	}

	public double[] convert( double jd ) {
		double[] r = new double[2] ;
		double stretch ;

		if ( getStretch() )
			stretch = Configuration.getValue( this, CK_STRETCH, DEFAULT_STRETCH ) ;
		else
			stretch = 0 ;

		r[0] = CAAMoon.EclipticLongitude( jd ) ;
		r[1] = CAAMoon.EclipticLatitude( jd ) ;
		r[1] = r[1]+( jd-epoch()[0] )*stretch ;

		return r ;
	}

	public double unconvert( double[] eq ) {
		return Double.NaN ;
	}

	public double[] tangent( double jd ) {
		double[] ec, xy ;
		Vector v, t ;

		ec = convert( jd+1./86400 ) ;
		xy = projector.project( ec ) ;
		v = new Vector( xy[0], xy[1] ) ;
		ec = convert( jd ) ;
		xy = projector.project( ec ) ;
		t = new Vector( xy[0], xy[1] ) ;

		v.sub( t ) ;

		return new double[] { v.x, v.y } ;
	}

	public List<double[]> list( List<Double> listjd, double jdA, double jdO, double shift ) {
		List<double[]> listxy ;
		double interval ;
		double d, e, g ;

		interval = Configuration.getValue( this, CK_INTERVAL, DEFAULT_INTERVAL ) ;

		listxy = new java.util.Vector<double[]>() ;

		listxy.add( project( jdA, shift ) ) ;
		if ( listjd != null )
			listjd.add( jdA ) ;

		d = jdO-jdA ;
		e = d-(int) ( d/interval )*interval ;
		g = ( Math.isLim0( e )?interval:e )/2 ;

		for ( double jd=jdA+g ; jd<jdO ; jd=jd+interval ) {
			listxy.add( project( jd, shift ) ) ;
			if ( listjd != null )
				listjd.add( jd ) ;
		}

		listxy.add( project( jdO, shift ) ) ;
		if ( listjd != null )
			listjd.add( jdO ) ;

		return listxy ;
	}

	public double scaleMarkNth( int mark, double span ) {
		return new LinearScale( span, epoch() ).markN( mark ) ;
	}
}
