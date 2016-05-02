
package astrolabe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.ValidationException;

import caa.CAACoordinateTransformation;

@SuppressWarnings("serial")
abstract public class AtlasAzimuthalType extends astrolabe.model.AtlasAzimuthalType {

	private final static double DEFAULT_OVERLAP = 10. ;
	private final static double DEFAULT_LIMITDE	= 0 ;
	private final static String DEFAULT_INTERVALUNITSH = "21600:10800:7200:3600:1800:1200:600:300:120:60:30:20:10:5:2:1" ;
	private final static String DEFAULT_INTERVALUNITSD = "324000:216000:108000:72000:36000:18000:7200:3600:1800:1200:600:300:120:60:30:20:10:5:2:1" ;

	// castor requirement for (un)marshalling
	public AtlasAzimuthalType() {
	}

	public AtlasAzimuthalType( Peer peer ) {
		peer.setupCompanion( this ) ;
	}

	public void addAllAtlasPage() throws ValidationException {
		int nra, nde, grid[], num ; // number of pages per single declination, number of page rows
		double a, b, rab, rad, tan ; // atlas page x, y, |v0| (radius), tan derived from a plus overlap
		double overlap, spanDe, originDe, extentDe ;
		double de, dde, sde, ra, dim[] ;
		astrolabe.model.ChartAzimuthalType chartAzimuthalType ;
		boolean northern ;
		ChartPage chartPage ;
		double chartPageX, chartPageY ;
		Projector projector ;
		List<Vector> dp ;
		AtlasPage atlasPage ;
		Vector vc, vb, vt ;
		double[] xy, eq ;

		chartAzimuthalType = getChartAzimuthalType() ;
		northern = chartAzimuthalType.getNorthern() ;
		chartPage = new ChartPage( chartAzimuthalType.getChartPage() ) ;
		chartPageX = chartPage.realx() ;
		chartPageY = chartPage.realy() ;

		projector = projector() ;

		setChartpagerealx( chartPageX ) ;
		setChartpagerealy( chartPageY ) ;

		overlap = Configuration.getValue(
				Configuration.getClassNode( this, getName(), null ),
				ApplicationConstant.PK_ATLAS_OVERLAP, DEFAULT_OVERLAP ) ;

		originDe = AstrolabeFactory.valueOf( getOrigin() )[2] ;
		extentDe = AstrolabeFactory.valueOf( getExtent() )[2] ;

		spanDe = AstrolabeFactory.valueOf( getAtlasAzimuthalTypeChoice().getSpanParallel() ) ;

		rab = chartPageX/chartPageY ;

		dde = originDe-extentDe ;
		nde = (int) ( dde/spanDe+1 ) ;
		sde = ( dde-spanDe )/( nde-1 ) ;
		de = originDe-spanDe ;

		grid = new int[nde] ;
		num = 1 ;

		for ( int cde=0 ; cde<nde ; cde++ ) {
			rad = projector.project( 0, de )[0] ;
			b = rad-projector.project( 0, de+spanDe )[0] ;
			a = b*rab ;

			tan = ( a/2*( 1-overlap/100. ) )/rad ;
			nra = (int) ( 180/Math.atan( tan ) ) ;

			grid[cde] = nra ;

			for ( int cra=0 ; cra<nra ; cra++ ) {
				ra = 360./nra*cra ;

				xy = projector.project( ra, de ) ;
				dp = page( xy, a, b, rab, northern ) ;
				atlasPage = new AtlasPage() ;

				atlasPage.setTcp( 0 ) ;
				atlasPage.setBcp( 0 ) ;
				atlasPage.setPcp( 0 ) ;
				atlasPage.setFcp( 0 ) ;

				atlasPage.setNum( num++ ) ;

				atlasPage.setCol( cra ) ;
				atlasPage.setRow( cde ) ;

				xy = dp.get( 0 ).toArray() ;
				atlasPage.setP0x( xy[0] ) ;
				atlasPage.setP0y( xy[1] ) ;
				xy = dp.get( 1 ).toArray() ;
				atlasPage.setP1x( xy[0] ) ;
				atlasPage.setP1y( xy[1] ) ;
				xy = dp.get( 2 ).toArray() ;
				atlasPage.setP2x( xy[0] ) ;
				atlasPage.setP2y( xy[1] ) ;
				xy = dp.get( 3 ).toArray() ;
				atlasPage.setP3x( xy[0] ) ;
				atlasPage.setP3y( xy[1] ) ;

				eq = projector.unproject( atlasPage.getP0x(), atlasPage.getP0y() ) ;
				atlasPage.setP0( new astrolabe.model.P0() ) ;
				atlasPage.getP0().setPhi( new astrolabe.model.Phi() ) ;
				atlasPage.getP0().setTheta( new astrolabe.model.Theta() ) ;
				modelOf( atlasPage.getP0().getPhi(), false, false, eq[0] ) ;
				modelOf( atlasPage.getP0().getTheta(), false, false, eq[1] ) ;

				eq = projector.unproject( atlasPage.getP1x(), atlasPage.getP1y() ) ;
				atlasPage.setP1( new astrolabe.model.P1() ) ;
				atlasPage.getP1().setPhi( new astrolabe.model.Phi() ) ;
				atlasPage.getP1().setTheta( new astrolabe.model.Theta() ) ;
				modelOf( atlasPage.getP1().getPhi(), false, false, eq[0] ) ;
				modelOf( atlasPage.getP1().getTheta(), false, false, eq[1] ) ;

				eq = projector.unproject( atlasPage.getP2x(), atlasPage.getP2y() ) ;
				atlasPage.setP2( new astrolabe.model.P2() ) ;
				atlasPage.getP2().setPhi( new astrolabe.model.Phi() ) ;
				atlasPage.getP2().setTheta( new astrolabe.model.Theta() ) ;
				modelOf( atlasPage.getP2().getPhi(), false, false, eq[0] ) ;
				modelOf( atlasPage.getP2().getTheta(), false, false, eq[1] ) ;

				eq = projector.unproject( atlasPage.getP3x(), atlasPage.getP3y() ) ;
				atlasPage.setP3( new astrolabe.model.P3() ) ;
				atlasPage.getP3().setPhi( new astrolabe.model.Phi() ) ;
				atlasPage.getP3().setTheta( new astrolabe.model.Theta() ) ;
				modelOf( atlasPage.getP3().getPhi(), false, false, eq[0] ) ;
				modelOf( atlasPage.getP3().getTheta(), false, false, eq[1] ) ;

				// atlas page equatorial center (origin)
				vc = pageCenter( dp ) ;
				atlasPage.setOriginx( vc.x ) ;
				atlasPage.setOriginy( vc.y ) ;
				eq = projector.unproject( vc.x, vc.y ) ;
				eq[0] = CAACoordinateTransformation.DegreesToHours( eq[0] ) ;
				atlasPage.setOrigin( new astrolabe.model.Origin() ) ;
				atlasPage.getOrigin().setPhi( new astrolabe.model.Phi() ) ;
				atlasPage.getOrigin().setTheta( new astrolabe.model.Theta() ) ;
				modelOf( atlasPage.getOrigin().getPhi(), true, true, eq[0] ) ;
				modelOf( atlasPage.getOrigin().getTheta(), true, false, eq[1] ) ;

				// declination in middle center of atlas page top
				dim = pageEdge( dp ) ;
				vt = new Vector( vc ) ;
				vt.scale( vc.abs()-dim[1]/2 ) ;
				atlasPage.setTopx( vt.x ) ;
				atlasPage.setTopy( vt.y ) ;
				eq = projector.unproject( vt.x, vt.y ) ;
				atlasPage.setTop( new astrolabe.model.Top() ) ;
				atlasPage.getTop().setPhi( new astrolabe.model.Phi() ) ;
				atlasPage.getTop().setTheta( new astrolabe.model.Theta() ) ;
				modelOf( atlasPage.getTop().getPhi(), false, false, eq[0] ) ;
				modelOf( atlasPage.getTop().getTheta(), false, false, eq[1] ) ;

				// declination in middle of atlas page bottom
				vb = new Vector( vc ) ;
				vb.scale( vc.abs()+dim[1]/2 ) ;
				atlasPage.setBottomx( vb.x ) ;
				atlasPage.setBottomy( vb.y ) ;
				eq = projector.unproject( vb.x, vb.y ) ;
				atlasPage.setBottom( new astrolabe.model.Bottom() ) ;
				atlasPage.getBottom().setPhi( new astrolabe.model.Phi() ) ;
				atlasPage.getBottom().setTheta( new astrolabe.model.Theta() ) ;
				modelOf( atlasPage.getBottom().getPhi(), false, false, eq[0] ) ;
				modelOf( atlasPage.getBottom().getTheta(), false, false, eq[1] ) ;

				// atlas page scale
				atlasPage.setScale( chartPageX/dim[0]*100 ) ;

				atlasPage.validate() ;

				addAtlasPage( atlasPage ) ;
			}

			de = de-sde ;
		}

		pageConnect( grid ) ;
	}

	public void toModel( astrolabe.model.ChartAzimuthalType chartAzimuthalType, int atlaspage ) {
		double[] checkerTransform ;
		double edgeBeg, edgeEnd ;
		double p0de, tde, p1ra, p2ra ;
		int checkerRA, checkerDe ;
		boolean northern ;
		AtlasPage atlasPage ;
		astrolabe.model.Horizon h ;
		astrolabe.model.HorizonEquatorial hE ;
		astrolabe.model.Circle c ;
		int intervalNumber ;
		double circleNumber ;
		double ra, de ;

		if ( getCheckerMeridian() == null )
			checkerRA = 1 ;
		else
			checkerRA = getCheckerMeridian().getValue() ;
		if ( getCheckerParallel() == null )
			checkerDe = 1 ;
		else
			checkerDe = getCheckerParallel().getValue() ;

		if ( checkerDe == 1 && checkerRA == 1 )
			return ;

		northern = getChartAzimuthalType().getNorthern() ;

		atlasPage = (AtlasPage) getAtlasPage( atlaspage ) ;

		hE = new astrolabe.model.HorizonEquatorial() ;
		if ( getName() == null )
			hE.setName( ApplicationConstant.GC_NS_ATL ) ;
		else
			hE.setName( ApplicationConstant.GC_NS_ATL+getName() ) ;
		AstrolabeFactory.modelOf( hE, false ) ;

		h = new astrolabe.model.Horizon() ;
		h.setHorizonEquatorial( hE ) ;

		chartAzimuthalType.setScale( atlasPage.getScale() ) ;
		chartAzimuthalType.setOrigin( atlasPage.getOrigin() ) ;
		chartAzimuthalType.addHorizon( 0, h ) ;
		chartAzimuthalType.setAtlasPage( atlasPage ) ;

		if ( checkerDe>1 ) { // CircleParallel
			p0de = AstrolabeFactory.valueOf( atlasPage.getP0().getTheta() ) ;
			tde = AstrolabeFactory.valueOf( atlasPage.getTop().getTheta() ) ;

			edgeBeg = northern?p0de:-p0de ;
			edgeEnd = northern?tde:-tde ;

			intervalNumber = checkerDe-2 ;
			checkerTransform = checkerTransform( edgeBeg, edgeEnd, intervalNumber, intervalUnitD() ) ;

			de = checkerTransform[0] ;
			hE.addCircle( checkerDeToModel( northern?de:-de ) ) ;
			circleNumber = intervalNumber+1 ;
			for ( int i=1 ; i<circleNumber ; i++ ) {
				de = checkerTransform[0]+i*checkerTransform[1] ;
				c = checkerDeToModel( northern?de:-de ) ;
				hE.addCircle( c ) ;
			}
		}

		if ( checkerRA>1 ) { // CircleMeridian
			p1ra = AstrolabeFactory.valueOf( atlasPage.getP1().getPhi() ) ;
			p2ra = AstrolabeFactory.valueOf( atlasPage.getP2().getPhi() ) ;

			edgeBeg = atlasPage.getRow()>0?p1ra:p2ra ;
			edgeEnd = atlasPage.getRow()>0?p2ra:p1ra ;
			if ( edgeBeg>edgeEnd ) {
				edgeEnd += 360 ;
			}

			intervalNumber = checkerRA-2 ;
			checkerTransform = checkerTransform( edgeBeg, edgeEnd, intervalNumber, intervalUnitH() ) ;

			ra = checkerTransform[0] ;
			ra = CAACoordinateTransformation.MapTo0To360Range( ra ) ;
			c = checkerRAToModel( ra, northern ) ;
			hE.addCircle( c ) ;
			if ( atlasPage.getRow()>0 ) {
				circleNumber = intervalNumber+1 ;
			} else { // atlasPage.row == 0
				flipCircleRange( c.getCircleMeridian() ) ;

				circleNumber = java.lang.Math.ceil( 360/checkerTransform[1] ) ;
			}
			for ( int i=1 ; i<circleNumber ; i++ ) {
				ra = checkerTransform[0]+i*checkerTransform[1] ;
				ra = CAACoordinateTransformation.MapTo0To360Range( ra ) ;
				c = checkerRAToModel( ra, northern ) ;
				hE.addCircle( c ) ;
				if ( atlasPage.getRow() == 0 ) {
					flipCircleRange( c.getCircleMeridian() ) ;
				}
			}
		}
	}

	public void headAUX() {
	}

	public void emitAUX() {
		URI xmlu ;
		File xmlf ;

		if ( getMarshal() != null ) {
			try {
				xmlu = new URI( getMarshal() ) ;
				if ( xmlu.isAbsolute() ) {
					xmlf = new File( xmlu ) ;	
				} else {
					xmlf = new File( xmlu.getPath() ) ;
				}
				while ( ! xmlf.createNewFile() ) {
					xmlf.delete() ;
				}

				marshal( new FileOutputStream( xmlf ), "UTF-8" ) ;
			} catch ( URISyntaxException e ) {
				throw new RuntimeException( e.toString() ) ; // URI constructor
			} catch ( IOException e ) {
				throw new RuntimeException( e.toString() ) ; // File.createNewFile()
			}
		}
	}

	public void tailAUX() {
	}

	public void headPS( AstrolabePostscriptStream ps ) {
	}

	public void emitPS( AstrolabePostscriptStream ps ) {
		AtlasPage atlasPage ;

		for ( int ap=0 ; ap<getAtlasPageCount() ; ap++ ) {
			atlasPage = (AtlasPage) getAtlasPage( ap ) ;

			ps.operator.gsave() ;

			atlasPage.headPS( ps ) ;
			atlasPage.emitPS( ps ) ;
			atlasPage.tailPS( ps ) ;

			ps.operator.grestore() ;
		}
	}

	public void tailPS( AstrolabePostscriptStream ps ) {
	}

	abstract public astrolabe.model.ChartAzimuthalType getChartAzimuthalType() ;
	abstract public Projector projector() ;

	private void marshal( OutputStream xmls, String charset ) {
		Marshaller marshaller ;
		Mapping mapping ;
		String mapn ;
		Writer xmlw ;

		// map file creation:
		// 1. make AtlasStereographic.map (e.g.)
		// 2. remove unused class definitions from AtlasStereographic.map
		// 3. remove unused field definitions from AtlasStereographic and AtlasPage
		// 4. remove required attribute from field definitions for Phi and Theta
		// 5. remove package model from class definitions AtlasStereographic, AtlasPage, DMS and Rational
		mapn = Configuration.getValue(
				Configuration.getClassNode( this, getName(), null ),
				ApplicationConstant.PK_ATLAS_URLMODELMAP, getClass().getSimpleName()+".map" ) ;

		try {
			mapping = new Mapping() ;
			mapping.loadMapping( mapn ) ;

			xmlw = new OutputStreamWriter( xmls, charset ) ;

			marshaller = new Marshaller( xmlw ) ;
			marshaller.setMapping( mapping );
			marshaller.setEncoding( charset ) ;

			marshaller.setSuppressNamespaces( true ) ;

			// suppress xsi:type attribute (implies xmlns:xsi attribute) in marshaller output
			marshaller.setSuppressXSIType( true ) ;

			marshaller.marshal( this );

			xmls.flush() ;
			xmls.close() ;
		} catch ( MappingException e ) {
			throw new RuntimeException( e.toString() ) ; // Mapping constructor
		} catch ( ValidationException e ) {
			throw new RuntimeException( e.toString() ) ; // Marshaller.marshal
		} catch ( MarshalException e ) {
			throw new RuntimeException( e.toString() ) ; // Marshaller.marshal
		} catch ( IOException e ) {
			throw new RuntimeException( e.toString() ) ; // Mapping.loadMapping(), Marshaller constructor
		}
	}

	private List<Vector> page( double[] xy, double a, double b, double rab, boolean northern ) {
		List<Vector> r = new java.util.Vector<Vector>() ;
		Vector v0, v1, v2, v3, v4 ; // radius vector v0, edge vectors
		Vector vp0, vp1, vp2, vp3 ; // point vectors
		double m90[] = new double[] { 0, -1, 0, 1, 0, 0, 0, 0, 1 } ; // rotation matrix, plane xy, 90 degrees counter-clockwise
		double m90c[] = new double[] { 0, 1, 0, -1, 0, 0, 0, 0, 1 } ; // rotation matrix, plane xy, 90 degrees clockwise
		double x ;

		x = java.lang.Math.abs( a ) ;

		v0 = new Vector( xy[0], xy[1] ) ;

		v1 = new Vector( v0 )
		.mul( -1 )
		.apply( northern?m90c:m90 )
		.scale( x/2 ) ;

		vp0 = new Vector( v0 )
		.add( v1 ) ;

		v2 = new Vector( v1 )
		.mul( -1 )
		.apply( northern?m90c:m90 )
		.scale( 2*v1.abs()/rab ) ;

		vp1 = new Vector( vp0 )
		.add( v2 ) ;

		v3 = new Vector( v0 )
		.mul( -1 )
		.apply( northern?m90:m90c )
		.scale( x/2 ) ;

		vp3 = new Vector( v0 )
		.add( v3 ) ;

		v4 = new Vector( v3 )
		.mul( -1 )
		.apply( northern?m90:m90c )
		.scale( 2*v3.abs()/rab ) ;

		vp2 = new Vector( vp3 )
		.add( v4 ) ;

		r.add( vp0 ) ;
		r.add( vp1 ) ;
		r.add( vp2 ) ;
		r.add( vp3 ) ;

		return r ;
	}

	private Vector pageCenter( List<Vector> page ) {
		Vector v0, v2, v20, vc ;

		v0 = page.get( 0 ) ;
		v2 = page.get( 2 ) ;

		v20 = new Vector( v0 ).sub( v2 ) ;
		v20.scale( v20.abs()/2 ) ;

		vc = new Vector( v2 ).add( v20 ) ;

		return vc ;
	}

	private double[] pageEdge( List<Vector> page ) {
		double[] r = new double[2] ;
		Vector v0, va, vb ;

		v0 = page.get( 0 ) ;
		va = new Vector( page.get( 3 ) ) ;
		va.sub( v0 ) ;
		vb = new Vector( page.get( 1 ) ) ;
		vb.sub( v0 ) ;

		r[0] = va.abs() ;
		r[1] = vb.abs() ;

		return r ;
	}

	private void pageConnect( int[] grid ) {
		int pl ; // lower page index relative to declination ring
		astrolabe.model.AtlasPage pcAP, plAP, pfAP ; // current, lower, lower following atlas page
		astrolabe.model.AtlasPage pc0AP ; // first atlas page of current declination ring
		int nde, nra ; // number of declination rings, number of pages in current declination ring
		double ora ;

		// 1st pass: connect upper/lower pages
		nde = grid.length ;
		for ( int dec=0 ; dec<nde-1 ; dec++ ) { // for each declination ring
			for ( int rac=0 ; rac<grid[dec] ; rac++ ) { // for each RA page in declination ring
				pcAP = getAtlasPage( pageIndex( grid, dec, rac ) ) ;
				// fare#75: lower page of first in declination ring is always the first page in that declination ring.
				// thus set to 0 when rac equals 0 because the given computation of pl fails if oeq[0] equals rad360.
				if ( rac > 0 ) {
					ora = AstrolabeFactory.valueOf( pcAP.getOrigin().getPhi() ) ;
					pl = (int) ( ora*grid[dec+1]/360 ) ;
				} else {
					pl = 0 ;
				}
				// look ahead one page in lower ring, set its tcp to current num in advance in case that page will be skipped
				if ( pl < grid[dec+1]-1 ) {
					pfAP = getAtlasPage( pageIndex( grid, dec+1, pl+1 ) ) ;
					pfAP.setTcp( pcAP.getNum() ) ;
				}
				plAP = getAtlasPage( pageIndex( grid, dec+1, pl ) ) ;
				pcAP.setBcp( plAP.getNum() ) ;
				plAP.setTcp( pcAP.getNum() ) ;
			}
		}
		// 2nd pass: connect previous/following pages
		for ( int dec=0 ; dec<nde ; dec++ ) {
			nra = grid[dec] ;
			pc0AP = getAtlasPage( pageIndex( grid, dec, 0 ) ) ;
			for ( int rac=0 ; rac<nra ; rac++ ) {
				pcAP = getAtlasPage( pageIndex( grid, dec, rac ) ) ;
				pcAP.setFcp( pc0AP.getNum()+( rac+nra+1 )%nra ) ;
				pcAP.setPcp( pc0AP.getNum()+( rac+nra-1 )%nra ) ;
			}
		}
	}

	private int pageIndex( int[] grid, int ide, int ira ) {
		int index = 0 ;

		for ( int cde=0 ; cde<ide ; cde++ ) {
			index += grid[cde] ;
		}
		index += ira ;

		return index ;
	}

	private astrolabe.model.Circle checkerDeToModel( double de ) {
		astrolabe.model.Circle c ;
		astrolabe.model.CircleParallel cP ;
		astrolabe.model.Annotation a ;

		cP = new astrolabe.model.CircleParallel() ;
		if ( getName() == null )
			cP.setName( ApplicationConstant.GC_NS_ATL ) ;
		else
			cP.setName( ApplicationConstant.GC_NS_ATL+getName() ) ;
		AstrolabeFactory.modelOf( cP, false ) ;

		c = new astrolabe.model.Circle() ;
		c.setCircleParallel( cP ) ;
		// astrolabe.model.AngleType
		cP.setAngle( new astrolabe.model.Angle() ) ;
		cP.getAngle().setRational( new astrolabe.model.Rational() ) ;

		cP.setBegin( new astrolabe.model.Begin() ) ;
		// astrolabe.model.AngleType
		cP.getBegin().setAngle( new astrolabe.model.Angle() ) ;
		cP.getBegin().getAngle().setRational( new astrolabe.model.Rational() ) ;
		cP.setEnd( new astrolabe.model.End() ) ;
		// astrolabe.model.AngleType
		cP.getEnd().setAngle( new astrolabe.model.Angle() ) ;
		cP.getEnd().getAngle().setRational( new astrolabe.model.Rational() ) ;

		cP.getAngle().getRational().setValue( de ) ;
		cP.getBegin().getAngle().getRational().setValue( 0 ) ;
		cP.getEnd().getAngle().getRational().setValue( 360 ) ;

		a = new astrolabe.model.Annotation() ;
		a.setAnnotationStraight( getCheckerParallel().getAnnotationStraight() ) ;

		cP.addAnnotation( a ) ;

		return c ;
	}

	private astrolabe.model.Circle checkerRAToModel( double ra, boolean northern ) {
		astrolabe.model.Circle c ;
		astrolabe.model.CircleMeridian cM ;
		astrolabe.model.Annotation a ;
		double cMEnd ;

		cM = new astrolabe.model.CircleMeridian() ;
		if ( getName() == null )
			cM.setName( ApplicationConstant.GC_NS_ATL ) ;
		else
			cM.setName( ApplicationConstant.GC_NS_ATL+getName() ) ;
		AstrolabeFactory.modelOf( cM, false ) ;

		c = new astrolabe.model.Circle() ;
		c.setCircleMeridian( cM ) ;
		// astrolabe.model.AngleType
		cM.setAngle( new astrolabe.model.Angle() ) ;
		cM.getAngle().setRational( new astrolabe.model.Rational() ) ;

		cM.setBegin( new astrolabe.model.Begin() ) ;
		// astrolabe.model.AngleType
		cM.getBegin().setAngle( new astrolabe.model.Angle() ) ;
		cM.getBegin().getAngle().setRational( new astrolabe.model.Rational() ) ;
		cM.setEnd( new astrolabe.model.End() ) ;
		// astrolabe.model.AngleType
		cM.getEnd().setAngle( new astrolabe.model.Angle() ) ;
		cM.getEnd().getAngle().setRational( new astrolabe.model.Rational() ) ;

		cMEnd = Configuration.getValue(
				Configuration.getClassNode( this, getName(), null ),
				ApplicationConstant.PK_ATLAS_LIMITDE, DEFAULT_LIMITDE ) ;

		cM.getAngle().getRational().setValue( ra ) ;
		cM.getBegin().getAngle().getRational().setValue( northern?-90:90 ) ;
		cM.getEnd().getAngle().getRational().setValue( cMEnd ) ;

		a = new astrolabe.model.Annotation() ;
		a.setAnnotationStraight( getCheckerMeridian().getAnnotationStraight() ) ;

		cM.addAnnotation( a ) ;

		return c ;
	}

	private double[] checkerTransform( double edgeBeg, double edgeEnd, int intervalNumber, double[] intervalUnit ) {
		double[] r = new double[2] ;
		double g0, gN ;

		// 1. find g0 for smallest interval that fits intervalNumber times into edgeEnd-edgeBeg
		for ( int i=0 ; i<intervalUnit.length ; i++ ) {
			g0 = graduation0( edgeBeg, intervalUnit[i] ) ;
			gN = g0+intervalNumber*intervalUnit[i] ;
			if ( gN<edgeEnd ) {
				r[0] = g0 ;

				break ;
			}
		}
		// 2. find biggest interval that fits intervalNumber times into edgeEnd-edgeBeg regarding g0 in 1.
		for ( int i=0 ; i<intervalUnit.length ; i++ ) {
			gN = r[0]/*g0*/+intervalNumber*intervalUnit[i] ;
			if ( gN<edgeEnd ) {
				r[1] = intervalUnit[i] ;

				break ;
			}
		}

		return r ;
	}

	private double graduation0( double edgeBeg, double interval ) {
		return ( java.lang.Math.floor( edgeBeg/interval )+1 )*interval ;
	}

	private double[] intervalUnitH() {
		String[] iuP ;
		double[] iuV ;

		iuP = Configuration.getValue(
				Configuration.getClassNode( this, getName(), null ),
				ApplicationConstant.PK_ATLAS_INTERVALUNITSH, DEFAULT_INTERVALUNITSH ).split( ":" ) ;
		iuV = new double[ iuP.length ] ;
		for ( int iu=0 ; iu<iuP.length ; iu++ ) {
			iuV[iu] = new Double( iuP[iu] ).doubleValue()*1./3600*15 ;
		}

		return iuV ;
	}

	private double[] intervalUnitD() {
		String[] iuP ;
		double[] iuV ;

		iuP = Configuration.getValue(
				Configuration.getClassNode( this, getName(), null ),
				ApplicationConstant.PK_ATLAS_INTERVALUNITSD, DEFAULT_INTERVALUNITSD ).split( ":" ) ;
		iuV = new double[ iuP.length ] ;
		for ( int iu=0 ; iu<iuP.length ; iu++ ) {
			iuV[iu] = new Double( iuP[iu] ).doubleValue()*1./3600 ;
		}

		return iuV ;
	}

	private void flipCircleRange( astrolabe.model.CircleType circle ) {
		double b, e ;

		b = circle.getBegin().getAngle().getRational().getValue() ;
		e = circle.getEnd().getAngle().getRational().getValue() ;

		circle.getBegin().getAngle().getRational().setValue( e ) ;
		circle.getEnd().getAngle().getRational().setValue( b ) ;
	}

	public static void modelOf( astrolabe.model.AngleType angle, boolean discrete, boolean time, double value ) {
		Rational v ;
		DMS dms ;

		v = new Rational( value ) ;

		if (discrete ) {
			dms = new DMS( value ) ;
			if ( time ) {
				angle.setHMS( new astrolabe.model.HMS() ) ;
				angle.getHMS().setHrs( dms.getDeg() ) ;
				angle.getHMS().setMin( dms.getMin() ) ;
				angle.getHMS().setSec( dms.getSec() ) ;
			} else {
				angle.setDMS( dms ) ;
			}
		} else {
			angle.setRational( v ) ;
		}
	}
}
