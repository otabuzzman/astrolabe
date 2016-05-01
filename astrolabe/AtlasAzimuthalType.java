
package astrolabe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.prefs.Preferences;

import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.ValidationException;

import caa.CAACoordinateTransformation;

@SuppressWarnings("serial")
abstract class AtlasAzimuthalType extends astrolabe.model.AtlasAzimuthalType implements Atlas, AuxiliaryEmitter {

	private final static String DEFAULT_PRACTICALITY = "0" ;
	private final static String DEFAULT_IMPORTANCE = ".4:0" ;
	private final static double DEFAULT_OVERLAP = 10. ;
	private final static double DEFAULT_LIMITDE	= 0 ;
	private final static String DEFAULT_INTERVALUNITSH = "21600:10800:7200:3600:1800:1200:600:300:120:60:30:20:10:5:2:1" ;
	private final static String DEFAULT_INTERVALUNITSD = "324000:216000:108000:72000:36000:18000:7200:3600:1800:1200:600:300:120:60:30:20:10:5:2:1" ;

	private ChartAzimuthalType chart ;

	// castor requirement for (un)marshalling
	public AtlasAzimuthalType() {
	}

	public AtlasAzimuthalType( Object peer ) throws ParameterNotValidException {
		ApplicationHelper.setupCompanionFromPeer( this, peer ) ;
		try {
			validate() ;
		} catch ( ValidationException e ) {
			throw new ParameterNotValidException( e.toString() ) ;
		}
	}

	public void headPS( PostscriptStream ps ) {
		String practicality, importance ;
		Preferences node ;

		node = ApplicationHelper.getClassNode( this, getName(), null ) ;

		practicality = ApplicationHelper.getPreferencesKV( node,
				ApplicationConstant.PK_ATLAS_PRACTICALITY, DEFAULT_PRACTICALITY ) ;
		importance = ApplicationHelper.getPreferencesKV( node,
				ApplicationConstant.PK_ATLAS_IMPORTANCE, DEFAULT_IMPORTANCE ) ;

		ApplicationHelper.emitPSPracticality( ps, practicality ) ;
		ApplicationHelper.emitPSImportance( ps, importance ) ;
	}

	public void emitPS( PostscriptStream ps ) {
		for ( PostscriptEmitter atlasPage : (AtlasPage[]) getAtlasPage() ) {

			ps.operator.gsave() ;

			atlasPage.headPS( ps ) ;
			atlasPage.emitPS( ps ) ;
			atlasPage.tailPS( ps ) ;

			ps.operator.grestore() ;
		}
	}

	public void tailPS( PostscriptStream ps ) {
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

	public astrolabe.model.Chart[] toModel() {
		astrolabe.model.Chart[] model ;
		double[] checkerTransform ;
		double edgeBeg, edgeEnd ;
		double p0de, tde, p1ra, p2ra ;
		int checkerRA, checkerDe ;
		AtlasPage atlasPage ;
		astrolabe.model.Chart ch ;
		ChartAzimuthalType cA ;
		astrolabe.model.Horizon h ;
		astrolabe.model.HorizonEquatorial hE ;
		astrolabe.model.Circle c ;
		int intervalNumber ;
		double circleNumber ;
		String checker[], name ;
		double ra, de ;

		chart = chartAzimuthalType() ;

		removeAllAtlasPage() ;
		addAllAtlasPage() ;

		checker = getChecker().split( "x" ) ;
		checkerRA = new Integer( checker[0] ).intValue() ;
		checkerDe = new Integer( checker[1] ).intValue() ;

		if ( getName() != null ) {
			name = ApplicationConstant.GC_NS_ATL+getName() ;
		} else {
			name = null ;
		}

		model = new astrolabe.model.Chart[ getAtlasPageCount() ] ;

		for ( int m=0 ; m<getAtlasPageCount() ; m++ ) {
			atlasPage = (AtlasPage) getAtlasPage( m ) ;

			hE = new astrolabe.model.HorizonEquatorial() ;
			hE.setName( name ) ;
			// astrolabe.model.AngleType
			hE.setLatitude( new astrolabe.model.Latitude() ) ;
			hE.getLatitude().setRational( new astrolabe.model.Rational() ) ;
			hE.getLatitude().getRational().setValue( 90 ) ;

			h = new astrolabe.model.Horizon() ;
			h.setHorizonEquatorial( hE ) ;

			cA = chartAzimuthalType() ;
			cA.setName( name ) ;
			cA.setScale( atlasPage.getScale() ) ;
			cA.setOrigin( atlasPage.getOrigin() ) ;
			cA.addHorizon( 0, h ) ;

			ch = chart( cA ) ;

			if ( checkerDe>1 ) { // CircleParallel
				try {
					p0de = AstrolabeFactory.valueOf( atlasPage.getP0().getTheta() ) ;
					tde = AstrolabeFactory.valueOf( atlasPage.getTop().getTheta() ) ;
				} catch ( ParameterNotValidException e ) {
					throw new RuntimeException( e.toString() ) ;
				}
				edgeBeg = cA.northern?p0de:-p0de ;
				edgeEnd = cA.northern?tde:-tde ;

				intervalNumber = checkerDe-2 ;
				checkerTransform = checkerTransform( edgeBeg, edgeEnd, intervalNumber, intervalUnitD() ) ;

				de = checkerTransform[0] ;
				hE.addCircle( checkerDeToModel( cA.northern?de:-de ) ) ;
				circleNumber = intervalNumber+1 ;
				for ( int i=1 ; i<circleNumber ; i++ ) {
					de = checkerTransform[0]+i*checkerTransform[1] ;
					c = checkerDeToModel( cA.northern?de:-de ) ;
					hE.addCircle( c ) ;
				}
			}

			if ( checkerRA>1 ) { // CircleMeridian
				try {
					p1ra = AstrolabeFactory.valueOf( atlasPage.getP1().getPhi() ) ;
					p2ra = AstrolabeFactory.valueOf( atlasPage.getP2().getPhi() ) ;
				} catch ( ParameterNotValidException e ) {
					throw new RuntimeException( e.toString() ) ;
				}
				edgeBeg = atlasPage.getRow()>0?p1ra:p2ra ;
				edgeEnd = atlasPage.getRow()>0?p2ra:p1ra ;
				if ( edgeBeg>edgeEnd ) {
					edgeEnd += Math.rad360 ;
				}

				intervalNumber = checkerRA-2 ;
				checkerTransform = checkerTransform( edgeBeg, edgeEnd, intervalNumber, intervalUnitH() ) ;
				checkerTransform[0] = ApplicationHelper.mapTo0To360Range( checkerTransform[0] ) ;

				ra = checkerTransform[0] ;
				ra = ApplicationHelper.mapTo0To360Range( ra ) ;
				c = checkerRAToModel( ra ) ;
				hE.addCircle( c ) ;
				if ( atlasPage.getRow()>0 ) {
					circleNumber = intervalNumber+1 ;
				} else { // atlasPage.row == 0
					flipCircleRange( c.getCircleMeridian() ) ;

					circleNumber = java.lang.Math.ceil( Math.rad360/checkerTransform[1] ) ;
				}
				for ( int i=1 ; i<circleNumber ; i++ ) {
					ra = checkerTransform[0]+i*checkerTransform[1] ;
					ra = ApplicationHelper.mapTo0To360Range( ra ) ;
					c = checkerRAToModel( ra ) ;
					hE.addCircle( c ) ;
					if ( atlasPage.getRow() == 0 ) {
						flipCircleRange( c.getCircleMeridian() ) ;
					}
				}
			}

			if ( checkerDe == 1 && checkerRA == 1 ) {
				hE.addCircle( new astrolabe.model.Circle() ) ;
				hE.getCircle( 0 ).setCircleParallel( new astrolabe.model.CircleParallel() ) ;
				hE.getCircle( 0 ).getCircleParallel().setAngle( new astrolabe.model.Angle() ) ;
				hE.getCircle( 0 ).getCircleParallel().setBegin( new astrolabe.model.Begin() ) ;
				hE.getCircle( 0 ).getCircleParallel().getBegin().setImmediate( new astrolabe.model.Immediate() ) ;
				AtlasPage.modelOf( hE.getCircle( 0 ).getCircleParallel().getBegin().getImmediate(), false, false, 0 ) ;
				hE.getCircle( 0 ).getCircleParallel().setEnd( new astrolabe.model.End() ) ;
				hE.getCircle( 0 ).getCircleParallel().getEnd().setImmediate( new astrolabe.model.Immediate() ) ;
				AtlasPage.modelOf( hE.getCircle( 0 ).getCircleParallel().getEnd().getImmediate(), false, false, 1./3600./100. ) ;
			}

			try {
				AstrolabeFactory.modelOf( hE ) ;
			} catch ( ParameterNotValidException e ) {
				throw new RuntimeException( e.toString() ) ;
			}

			model[m] = ch ;
		}

		return model ;
	}

	abstract ChartAzimuthalType chartAzimuthalType() ;
	abstract astrolabe.model.Chart chart( astrolabe.model.ChartAzimuthalType chart ) ;

	private void marshal( OutputStream xmls, String charset ) {
		Preferences node ;
		Marshaller marshaller ;
		Mapping mapping ;
		String mapn ;
		Writer xmlw ;

		node = ApplicationHelper.getClassNode( this, getName(), null ) ;

		// map file creation:
		// 1. make AtlasStereographic.map (e.g.)
		// 2. remove unused class definitions from AtlasStereographic.map
		// 3. remove unused field definitions from AtlasStereographic and AtlasPage
		// 4. remove required attribute from field definitions for Phi and Theta
		// 5. remove package model from class definitions AtlasStereographic, AtlasPage, DMS and Rational
		mapn = ApplicationHelper.getPreferencesKV( node,
				ApplicationConstant.PK_ATLAS_URLMODELMAP, getClass().getSimpleName()+".map" ) ;

		try {
			mapping = new Mapping() ;
			mapping.loadMapping( mapn ) ;

			xmlw = new OutputStreamWriter( xmls, charset ) ;

			marshaller = new Marshaller( xmlw ) ;
			marshaller.setMapping( mapping );
			marshaller.setEncoding( charset ) ;

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

	private void addAllAtlasPage() {
		int nra, nde, grid[], num ; // number of pages per single declination, number of page rows
		double a, b, rad, tan ; // atlas page x, y, |v0| (radius), tan derived from a plus overlap
		double overlap, spanDe, originDe, extentDe ;
		double de, dde, sde, ra, dim[] ;
		java.util.Vector<Vector> dp ;
		AtlasPage atlasPage ;
		Vector vc, vb, vt ;
		double[] xy, eq ;
		Preferences node ;

		setPagesizex( chart.pagesize[0] ) ;
		setPagesizey( chart.pagesize[1] ) ;

		node = ApplicationHelper.getClassNode( this, getName(), null ) ;
		overlap = ApplicationHelper.getPreferencesKV( node,
				ApplicationConstant.PK_ATLAS_OVERLAP, DEFAULT_OVERLAP ) ;

		try {
			originDe = AstrolabeFactory.valueOf( getOrigin() )[2] ;
			extentDe = AstrolabeFactory.valueOf( getExtent() )[2] ;

			spanDe = AstrolabeFactory.valueOf( getAtlasAzimuthalTypeChoice().getSpanDe() ) ;
		} catch ( ParameterNotValidException e ) {
			throw new RuntimeException( e.toString() ) ;
		}

		dde = originDe-extentDe ;
		nde = (int) ( dde/spanDe+1 ) ;
		sde = ( dde-spanDe )/( nde-1 ) ;
		de = originDe-spanDe ;

		grid = new int[nde] ;
		num = 1 ;

		for ( int cde=0 ; cde<nde ; cde++ ) {
			rad = chart.project( 0, de )[0] ;
			b = rad-chart.project( 0, de+spanDe )[0] ;
			a = b*( chart.pagesize[0]/chart.pagesize[1] ) ;

			tan = ( a/2*( 1-overlap/100. ) )/rad ;
			nra = (int) ( Math.rad180/java.lang.Math.atan( tan ) ) ;

			grid[cde] = nra ;

			for ( int cra=0 ; cra<nra ; cra++ ) {
				ra = Math.rad360/nra*cra ;

				dp = page( ra, de, a, b ) ;
				atlasPage = new AtlasPage() ;

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

				eq = chart.unproject( atlasPage.getP0x(), atlasPage.getP0y() ) ;
				eq[0] = CAACoordinateTransformation.RadiansToDegrees( eq[0] ) ;
				eq[0] = CAACoordinateTransformation.MapTo0To360Range( eq[0] ) ;
				eq[1] = CAACoordinateTransformation.RadiansToDegrees( eq[1] ) ;
				AtlasPage.modelOf( atlasPage.getP0().getPhi(), false, false, eq[0] ) ;
				AtlasPage.modelOf( atlasPage.getP0().getTheta(), false, false, eq[1] ) ;

				eq = chart.unproject( atlasPage.getP1x(), atlasPage.getP1y() ) ;
				eq[0] = CAACoordinateTransformation.RadiansToDegrees( eq[0] ) ;
				eq[0] = CAACoordinateTransformation.MapTo0To360Range( eq[0] ) ;
				eq[1] = CAACoordinateTransformation.RadiansToDegrees( eq[1] ) ;
				AtlasPage.modelOf( atlasPage.getP1().getPhi(), false, false, eq[0] ) ;
				AtlasPage.modelOf( atlasPage.getP1().getTheta(), false, false, eq[1] ) ;

				eq = chart.unproject( atlasPage.getP2x(), atlasPage.getP2y() ) ;
				eq[0] = CAACoordinateTransformation.RadiansToDegrees( eq[0] ) ;
				eq[0] = CAACoordinateTransformation.MapTo0To360Range( eq[0] ) ;
				eq[1] = CAACoordinateTransformation.RadiansToDegrees( eq[1] ) ;
				AtlasPage.modelOf( atlasPage.getP2().getPhi(), false, false, eq[0] ) ;
				AtlasPage.modelOf( atlasPage.getP2().getTheta(), false, false, eq[1] ) ;

				eq = chart.unproject( atlasPage.getP3x(), atlasPage.getP3y() ) ;
				eq[0] = CAACoordinateTransformation.RadiansToDegrees( eq[0] ) ;
				eq[0] = CAACoordinateTransformation.MapTo0To360Range( eq[0] ) ;
				eq[1] = CAACoordinateTransformation.RadiansToDegrees( eq[1] ) ;
				AtlasPage.modelOf( atlasPage.getP3().getPhi(), false, false, eq[0] ) ;
				AtlasPage.modelOf( atlasPage.getP3().getTheta(), false, false, eq[1] ) ;

				// atlas page equatorial center (origin)
				vc = pageCenter( dp ) ;
				atlasPage.setOriginx( vc.x ) ;
				atlasPage.setOriginy( vc.y ) ;
				eq = chart.unproject( vc.x, vc.y ) ;
				eq[0] = CAACoordinateTransformation.RadiansToHours( eq[0] ) ;
				eq[0] = CAACoordinateTransformation.MapTo0To24Range( eq[0] ) ;
				eq[1] = CAACoordinateTransformation.RadiansToDegrees( eq[1] ) ;
				AtlasPage.modelOf( atlasPage.getOrigin().getPhi(), true, true, eq[0] ) ;
				AtlasPage.modelOf( atlasPage.getOrigin().getTheta(), true, false, eq[1] ) ;

				// declination in middle center of atlas page top
				dim = pageEdge( dp ) ;
				vt = new Vector( vc ) ;
				vt.scale( vc.abs()-dim[1]/2 ) ;
				atlasPage.setTopx( vt.x ) ;
				atlasPage.setTopy( vt.y ) ;
				eq = chart.unproject( vt.x, vt.y ) ;
				eq[0] = CAACoordinateTransformation.RadiansToDegrees( eq[0] ) ;
				eq[0] = CAACoordinateTransformation.MapTo0To360Range( eq[0] ) ;
				eq[1] = CAACoordinateTransformation.RadiansToDegrees( eq[1] ) ;
				AtlasPage.modelOf( atlasPage.getTop().getPhi(), false, false, eq[0] ) ;
				AtlasPage.modelOf( atlasPage.getTop().getTheta(), false, false, eq[1] ) ;

				// declination in middle of atlas page bottom
				vb = new Vector( vc ) ;
				vb.scale( vc.abs()+dim[1]/2 ) ;
				atlasPage.setBottomx( vb.x ) ;
				atlasPage.setBottomy( vb.y ) ;
				eq = chart.unproject( vb.x, vb.y ) ;
				eq[0] = CAACoordinateTransformation.RadiansToDegrees( eq[0] ) ;
				eq[0] = CAACoordinateTransformation.MapTo0To360Range( eq[0] ) ;
				eq[1] = CAACoordinateTransformation.RadiansToDegrees( eq[1] ) ;
				AtlasPage.modelOf( atlasPage.getBottom().getPhi(), false, false, eq[0] ) ;
				AtlasPage.modelOf( atlasPage.getBottom().getTheta(), false, false, eq[1] ) ;

				// atlas page scale
				atlasPage.setScale( chart.pagesize[0]/dim[0]*100 ) ;

				addAtlasPage( atlasPage ) ;
			}

			de = de-sde ;
		}

		pageConnect( grid ) ;
	}

	private java.util.Vector<Vector> page( double ra, double de, double a, double b ) {
		java.util.Vector<Vector> r = new java.util.Vector<Vector>() ;
		Vector v0, v1, v2, v3, v4 ; // radius vector v0, edge vectors
		Vector vp0, vp1, vp2, vp3 ; // point vectors
		double m90[] = new double[] { 0, -1, 0, 1, 0, 0, 0, 0, 1 } ; // rotation matrix, plane xy, 90 degrees counter-clockwise
		double m90c[] = new double[] { 0, 1, 0, -1, 0, 0, 0, 0, 1 } ; // rotation matrix, plane xy, 90 degrees clockwise
		double x, rab, xy[] ;

		x = java.lang.Math.abs( a ) ;
		rab = chart.pagesize[0]/chart.pagesize[1] ;

		xy = chart.project( ra, de ) ;
		v0 = new Vector( xy[0], xy[1] ) ;

		v1 = new Vector( v0 )
		.mul( -1 )
		.apply( chart.northern?m90c:m90 )
		.scale( x/2 ) ;

		vp0 = new Vector( v0 )
		.add( v1 ) ;

		v2 = new Vector( v1 )
		.mul( -1 )
		.apply( chart.northern?m90c:m90 )
		.scale( 2*v1.abs()/rab ) ;

		vp1 = new Vector( vp0 )
		.add( v2 ) ;

		v3 = new Vector( v0 )
		.mul( -1 )
		.apply( chart.northern?m90:m90c )
		.scale( x/2 ) ;

		vp3 = new Vector( v0 )
		.add( v3 ) ;

		v4 = new Vector( v3 )
		.mul( -1 )
		.apply( chart.northern?m90:m90c )
		.scale( 2*v3.abs()/rab ) ;

		vp2 = new Vector( vp3 )
		.add( v4 ) ;

		r.add( vp0 ) ;
		r.add( vp1 ) ;
		r.add( vp2 ) ;
		r.add( vp3 ) ;

		return r ;
	}

	private Vector pageCenter( java.util.Vector<Vector> page ) {
		Vector v0, v2, v20, vc ;

		v0 = page.get( 0 ) ;
		v2 = page.get( 2 ) ;

		v20 = new Vector( v0 ).sub( v2 ) ;
		v20.scale( v20.abs()/2 ) ;

		vc = new Vector( v2 ).add( v20 ) ;

		return vc ;
	}

	private double[] pageEdge( java.util.Vector<Vector> page ) {
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
					try {
						ora = AstrolabeFactory.valueOf( pcAP.getOrigin().getPhi() ) ;
					} catch ( ParameterNotValidException e ) {
						throw new RuntimeException( e.toString() ) ;
					}
					pl = (int) ( ora*grid[dec+1]/Math.rad360 ) ;
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
		astrolabe.model.AnnotationStraight aS ;
		astrolabe.model.Text tGen ;
		String name, designator, indicator, tVal ;
		java.util.Vector<astrolabe.model.Text> tDMS ;
		int[] rDMS ;
		DMS dms ;
		double cPAng ;

		tDMS = new java.util.Vector<astrolabe.model.Text>() ;

		cP = new astrolabe.model.CircleParallel() ;

		c = new astrolabe.model.Circle() ;
		c.setCircleParallel( cP ) ;
		// astrolabe.model.AngleType
		cP.setAngle( new astrolabe.model.Angle() ) ;
		cP.getAngle().setRational( new astrolabe.model.Rational() ) ;

		cP.setBegin( new astrolabe.model.Begin() ) ;
		// astrolabe.model.AngleType
		cP.getBegin().setImmediate( new astrolabe.model.Immediate() ) ;
		cP.getBegin().getImmediate().setRational( new astrolabe.model.Rational() ) ;
		cP.setEnd( new astrolabe.model.End() ) ;
		// astrolabe.model.AngleType
		cP.getEnd().setImmediate( new astrolabe.model.Immediate() ) ;
		cP.getEnd().getImmediate().setRational( new astrolabe.model.Rational() ) ;

		aS = new astrolabe.model.AnnotationStraight() ;
		if ( getName() != null ) {
			name = ApplicationConstant.GC_NS_ATL+getName() ;
		} else {
			name = null ;
		}

		cP.setName( name ) ;
		aS.setName( name ) ;

		a = new astrolabe.model.Annotation() ;
		a.setAnnotationStraight( aS ) ;

		cP.addAnnotation( a ) ;

		designator = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_CIRCLE_ALTITUDE ) ;
		indicator = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_INDICTAOR_DMS_DEGREES ) ;
		tVal = "@{"+designator+indicator+"}@" ;
		tGen = new astrolabe.model.Text() ;
		tGen.setName( name ) ;
		tGen.setValue( tVal+ApplicationHelper.getLocalizedString( ApplicationConstant.LK_DMS_DEGREES ) ) ;
		try {
			AstrolabeFactory.modelOf( tGen ) ;
		} catch ( ParameterNotValidException e ) {
			throw new RuntimeException( e.toString() ) ;
		}
		tDMS.add( tGen ) ;

		indicator = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_INDICTAOR_DMS_DEGREEMINUTES ) ;
		tVal = "@{"+designator+indicator+"}@" ;
		tGen = new astrolabe.model.Text() ;
		tGen.setName( name ) ;
		tGen.setValue( tVal+ApplicationHelper.getLocalizedString( ApplicationConstant.LK_DMS_MINUTES ) ) ;
		try {
			AstrolabeFactory.modelOf( tGen ) ;
		} catch ( ParameterNotValidException e ) {
			throw new RuntimeException( e.toString() ) ;
		}
		tDMS.add( tGen ) ;

		indicator = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_INDICTAOR_DMS_DEGREESECONDS ) ;
		tVal = "@{"+designator+indicator+"}@" ;
		tGen = new astrolabe.model.Text() ;
		tGen.setName( name ) ;
		tGen.setValue( tVal+ApplicationHelper.getLocalizedString( ApplicationConstant.LK_DMS_SECONDS ) ) ;
		try {
			AstrolabeFactory.modelOf( tGen ) ;
		} catch ( ParameterNotValidException e ) {
			throw new RuntimeException( e.toString() ) ;
		}
		tDMS.add( tGen ) ;

		indicator = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_INDICTAOR_DMS_DEGREEFRACTION ) ;
		tVal = ".@{"+designator+indicator+"}@" ;
		tGen = new astrolabe.model.Text() ;
		tGen.setName( name ) ;
		tGen.setValue( tVal ) ;
		try {
			AstrolabeFactory.modelOf( tGen ) ;
		} catch ( ParameterNotValidException e ) {
			throw new RuntimeException( e.toString() ) ;
		}
		tDMS.add( tGen ) ;

		cPAng = CAACoordinateTransformation.RadiansToDegrees( de ) ;
		cPAng = new Rational( cPAng ).getValue() ;
		dms = new DMS( cPAng ) ;
		rDMS = dms.relevant() ;
		for ( int t=rDMS[0] ; t<rDMS[1] ; t++ ) {
			tGen = tDMS.get( t ) ;
			if ( t == rDMS[0] ) {
				indicator = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_INDICTAOR_SIG_BOTH ) ;
				tVal = tGen.getValue() ;
				tGen.setValue( "@{"+designator+indicator+"}@"+tVal ) ;
			}
			aS.addText( tGen ) ;
		}

		try {
			AstrolabeFactory.modelOf( aS ) ;
			cP.getAngle().getRational().setValue( cPAng ) ;
			cP.getBegin().getImmediate().getRational().setValue( 0 ) ;
			cP.getEnd().getImmediate().getRational().setValue( 360 ) ;
			AstrolabeFactory.modelOf( cP ) ;
		} catch ( ParameterNotValidException e ) {
			throw new RuntimeException( e.toString() ) ;
		}

		return c ;
	}

	private astrolabe.model.Circle checkerRAToModel( double ra ) {
		astrolabe.model.Circle c ;
		astrolabe.model.CircleMeridian cM ;
		astrolabe.model.Annotation a ;
		astrolabe.model.AnnotationStraight aS ;
		astrolabe.model.Text tGen ;
		astrolabe.model.Superscript tSup ;
		String name, designator, indicator, tVal ;
		java.util.Vector<astrolabe.model.Text> tDMS ;
		int[] rDMS ;
		DMS dms ;
		double cMAng, cMEnd ;
		Preferences node ;

		tDMS = new java.util.Vector<astrolabe.model.Text>() ;

		cM = new astrolabe.model.CircleMeridian() ;

		c = new astrolabe.model.Circle() ;
		c.setCircleMeridian( cM ) ;
		// astrolabe.model.AngleType
		cM.setAngle( new astrolabe.model.Angle() ) ;
		cM.getAngle().setRational( new astrolabe.model.Rational() ) ;

		cM.setBegin( new astrolabe.model.Begin() ) ;
		// astrolabe.model.AngleType
		cM.getBegin().setImmediate( new astrolabe.model.Immediate() ) ;
		cM.getBegin().getImmediate().setRational( new astrolabe.model.Rational() ) ;
		cM.setEnd( new astrolabe.model.End() ) ;
		// astrolabe.model.AngleType
		cM.getEnd().setImmediate( new astrolabe.model.Immediate() ) ;
		cM.getEnd().getImmediate().setRational( new astrolabe.model.Rational() ) ;

		aS = new astrolabe.model.AnnotationStraight() ;
		if ( getName() != null ) {
			name = ApplicationConstant.GC_NS_ATL+getName() ;
		} else {
			name = null ;
		}

		cM.setName( name ) ;
		aS.setName( name ) ;

		a = new astrolabe.model.Annotation() ;
		a.setAnnotationStraight( aS ) ;

		cM.addAnnotation( a ) ;

		designator = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_CIRCLE_AZIMUTH ) ;
		indicator = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_INDICTAOR_HMS_HOURS ) ;
		tVal = "@{"+designator+indicator+"}@" ;
		tGen = new astrolabe.model.Text() ;
		tGen.setName( name ) ;
		tGen.setValue( tVal ) ;
		try {
			AstrolabeFactory.modelOf( tGen ) ;
		} catch ( ParameterNotValidException e ) {
			throw new RuntimeException( e.toString() ) ;
		}
		tDMS.add( tGen ) ;

		tSup = new astrolabe.model.Superscript() ;
		tSup.setValue( ApplicationHelper.getLocalizedString( ApplicationConstant.LK_HMS_HOURS ) ) ;
		tGen.addSuperscript( tSup ) ;

		indicator = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_INDICTAOR_HMS_HOURMINUTES ) ;
		tVal = "@{"+designator+indicator+"}@" ;
		tGen = new astrolabe.model.Text() ;
		tGen.setName( name ) ;
		tGen.setValue( tVal ) ;
		try {
			AstrolabeFactory.modelOf( tGen ) ;
		} catch ( ParameterNotValidException e ) {
			throw new RuntimeException( e.toString() ) ;
		}
		tDMS.add( tGen ) ;

		tSup = new astrolabe.model.Superscript() ;
		tSup.setValue( ApplicationHelper.getLocalizedString( ApplicationConstant.LK_HMS_MINUTES ) ) ;
		tGen.addSuperscript( tSup ) ;

		indicator = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_INDICTAOR_HMS_HOURSECONDS ) ;
		tVal = "@{"+designator+indicator+"}@" ;
		tGen = new astrolabe.model.Text() ;
		tGen.setName( name ) ;
		tGen.setValue( tVal ) ;
		try {
			AstrolabeFactory.modelOf( tGen ) ;
		} catch ( ParameterNotValidException e ) {
			throw new RuntimeException( e.toString() ) ;
		}
		tDMS.add( tGen ) ;

		tSup = new astrolabe.model.Superscript() ;
		tSup.setValue( ApplicationHelper.getLocalizedString( ApplicationConstant.LK_HMS_SECONDS ) ) ;
		tGen.addSuperscript( tSup ) ;

		indicator = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_INDICTAOR_HMS_HOURFRACTION ) ;
		tVal = "@{"+designator+indicator+"}@" ;
		tGen = new astrolabe.model.Text() ;
		tGen.setName( name ) ;
		tGen.setValue( tVal ) ;
		try {
			AstrolabeFactory.modelOf( tGen ) ;
		} catch ( ParameterNotValidException e ) {
			throw new RuntimeException( e.toString() ) ;
		}
		tDMS.add( tGen ) ;

		cMAng = CAACoordinateTransformation.RadiansToDegrees( ra ) ;
		cMAng = new Rational( cMAng ).getValue() ;
		dms = new DMS( cMAng ) ;
		rDMS = dms.relevant() ;
		for ( int t=rDMS[0] ; t<rDMS[1] ; t++ ) {
			tGen = tDMS.get( t ) ;
			if ( t == rDMS[0] ) {
				indicator = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_INDICTAOR_SIG_MATH ) ;
				tVal = tGen.getValue() ;
				tGen.setValue( "@{"+designator+indicator+"}@"+tVal ) ;
			}
			aS.addText( tDMS.get( t ) ) ;
		}

		try {
			AstrolabeFactory.modelOf( aS ) ;

			node = ApplicationHelper.getClassNode( this, getName(), null ) ;
			cMEnd = ApplicationHelper.getPreferencesKV( node, ApplicationConstant.PK_ATLAS_LIMITDE, DEFAULT_LIMITDE ) ;
			cM.getAngle().getRational().setValue( cMAng ) ;
			cM.getBegin().getImmediate().getRational().setValue( chart.northern?-90:90 ) ;
			cM.getEnd().getImmediate().getRational().setValue( cMEnd ) ;
			AstrolabeFactory.modelOf( cM ) ;
		} catch ( ParameterNotValidException e ) {
			throw new RuntimeException( e.toString() ) ;
		}

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
		Preferences node ;
		String[] iuP ;
		double[] iuV ;

		node = ApplicationHelper.getClassNode( this, getName(), null ) ;
		iuP = ApplicationHelper.getPreferencesKV( node,
				ApplicationConstant.PK_ATLAS_INTERVALUNITSH, DEFAULT_INTERVALUNITSH ).split( ":" ) ;
		iuV = new double[ iuP.length ] ;
		for ( int iu=0 ; iu<iuP.length ; iu++ ) {
			iuV[iu] = new Double( iuP[iu] ).doubleValue()*
			CAACoordinateTransformation.DegreesToRadians( 1./3600 )*15 ;
		}

		return iuV ;
	}

	private double[] intervalUnitD() {
		Preferences node ;
		String[] iuP ;
		double[] iuV ;

		node = ApplicationHelper.getClassNode( this, getName(), null ) ;
		iuP = ApplicationHelper.getPreferencesKV( node,
				ApplicationConstant.PK_ATLAS_INTERVALUNITSD, DEFAULT_INTERVALUNITSD ).split( ":" ) ;
		iuV = new double[ iuP.length ] ;
		for ( int iu=0 ; iu<iuP.length ; iu++ ) {
			iuV[iu] = new Double( iuP[iu] ).doubleValue()*
			CAACoordinateTransformation.DegreesToRadians( 1./3600 ) ;
		}

		return iuV ;
	}

	private void flipCircleRange( astrolabe.model.CircleType circle ) {
		double b, e ;

		b = circle.getBegin().getImmediate().getRational().getValue() ;
		e = circle.getEnd().getImmediate().getRational().getValue() ;

		circle.getBegin().getImmediate().getRational().setValue( e ) ;
		circle.getEnd().getImmediate().getRational().setValue( b ) ;
	}
}
