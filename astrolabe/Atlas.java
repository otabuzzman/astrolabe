
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
public class Atlas extends astrolabe.model.Atlas implements Companion {

	private final static String DEFAULT_PRACTICALITY = "0" ;
	private final static String DEFAULT_IMPORTANCE = ".4:0" ;
	private final static double DEFAULT_OVERLAP = 10. ;
	private final static double DEFAULT_LIMITDE	= 0 ;
	private final static String DEFAULT_INTERVALUNITSH = "21600:10800:7200:3600:1800:1200:600:300:120:60:30:20:10:5:2:1" ;
	private final static String DEFAULT_INTERVALUNITSD = "324000:216000:108000:72000:36000:18000:7200:3600:1800:1200:600:300:120:60:30:20:10:5:2:1" ;

	private Projector projector ;

	private double[] pagesize = new double[2] ;

	private boolean northern ;

	// castor requirement for (un)marshalling
	public Atlas() {
	}

	public Atlas( Object peer, Projector projector, double[] pagesize, boolean northern ) throws ParameterNotValidException {
		ApplicationHelper.setupCompanionFromPeer( this, peer ) ;
		try {
			validate() ;
		} catch ( ValidationException e ) {
			throw new ParameterNotValidException( e.toString() ) ;
		}

		this.projector = projector ;

		this.pagesize[0] = pagesize[0] ;
		this.pagesize[1] = pagesize[1] ;

		this.northern = northern ;
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
		for ( PostscriptEmitter atlasPage : volume() ) {

			ps.operator.gsave() ;

			atlasPage.headPS( ps ) ;
			atlasPage.emitPS( ps ) ;
			atlasPage.tailPS( ps ) ;

			ps.operator.grestore() ;
		}
	}

	public void tailPS( PostscriptStream ps ) {
	}

	public void addToModel( Object[] modelWithArgs ) {
		double[] checkerTransform ;
		double edgeBeg, edgeEnd ;
		int checkerRA, checkerDe ;
		astrolabe.model.HorizonType horizon ;
		astrolabe.model.Circle circle ;
		AtlasPage atlasPage ;
		int intervalNumber ;
		double circleNumber ;
		String[] checker ;
		double ra, de ;

		checker = getChecker().split( "x" ) ;
		checkerRA = new Integer( checker[0] ).intValue() ;
		checkerDe = new Integer( checker[1] ).intValue() ;

		horizon = (astrolabe.model.HorizonType) modelWithArgs[0] ;
		horizon.setCatalog( getCatalog() ) ;

		atlasPage = (AtlasPage) modelWithArgs[1] ;

		if ( checkerDe>1 ) { // CircleParallel
			edgeBeg = northern?atlasPage.p0eq[1]:-atlasPage.p0eq[1] ;
			edgeEnd = northern?atlasPage.teq[1]:-atlasPage.teq[1] ;

			intervalNumber = checkerDe-2 ;
			checkerTransform = checkerTransform( edgeBeg, edgeEnd, intervalNumber, intervalUnitD() ) ;

			de = checkerTransform[0] ;
			horizon.addCircle( checkerDeToModel( northern?de:-de ) ) ;
			circleNumber = intervalNumber+1 ;
			for ( int i=1 ; i<circleNumber ; i++ ) {
				de = checkerTransform[0]+i*checkerTransform[1] ;
				circle = checkerDeToModel( northern?de:-de ) ;
				horizon.addCircle( circle ) ;
			}
		}

		if ( checkerRA>1 ) { // CircleMeridian
			edgeBeg = atlasPage.row>0?atlasPage.p1eq[0]:atlasPage.p2eq[0] ;
			edgeEnd = atlasPage.row>0?atlasPage.p2eq[0]:atlasPage.p1eq[0] ;
			if ( edgeBeg>edgeEnd ) {
				edgeEnd += Math.rad360 ;
			}

			intervalNumber = checkerRA-2 ;
			checkerTransform = checkerTransform( edgeBeg, edgeEnd, intervalNumber, intervalUnitH() ) ;
			checkerTransform[0] = ApplicationHelper.mapTo0To360Range( checkerTransform[0] ) ;

			ra = checkerTransform[0] ;
			ra = ApplicationHelper.mapTo0To360Range( ra ) ;
			circle = checkerRAToModel( ra ) ;
			horizon.addCircle( circle ) ;
			if ( atlasPage.row>0 ) {
				circleNumber = intervalNumber+1 ;
			} else { // atlasPage.row == 0
				flipCircleRange( circle.getCircleMeridian() ) ;

				circleNumber = java.lang.Math.ceil( Math.rad360/checkerTransform[1] ) ;
			}
			for ( int i=1 ; i<circleNumber ; i++ ) {
				ra = checkerTransform[0]+i*checkerTransform[1] ;
				ra = ApplicationHelper.mapTo0To360Range( ra ) ;
				circle = checkerRAToModel( ra ) ;
				horizon.addCircle( circle ) ;
				if ( atlasPage.row == 0 ) {
					flipCircleRange( circle.getCircleMeridian() ) ;
				}
			}
		}
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

	public void marshal( OutputStream xmls, String charset ) {
		Preferences node ;
		Marshaller marshaller ;
		Mapping mapping ;
		String mapn ;
		Writer xmlw ;

		node = ApplicationHelper.getClassNode( this, getName(), null ) ;

		mapn = ApplicationHelper.getPreferencesKV( node,
				ApplicationConstant.PK_ATLAS_URLMODELMAP, getClass().getSimpleName()+".map" ) ;

		try {
			for ( AtlasPage atlaspage : volume() ) {
				addAtlasPage( atlaspage.toModel() ) ;
			}
			setPagesizex( pagesize[0] ) ;
			setPagesizey( pagesize[1] ) ;

			mapping = new Mapping() ;
			mapping.loadMapping( mapn ) ;

			xmlw = new OutputStreamWriter( xmls, charset ) ;

			marshaller = new Marshaller( xmlw ) ;
			marshaller.setMapping( mapping );
			marshaller.setEncoding( charset ) ;

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

	public java.util.Vector<AtlasPage> volume() {
		java.util.Vector<AtlasPage> r = new java.util.Vector<AtlasPage>() ;
		int nra, nde ; // number of pages per single declination, number of page rows
		double a, b, rad, tan ; // atlas page x, y, |v0| (radius), tan derived from a plus overlap
		double overlap, spanDe, originDe, extentDe ;
		double de, dde, sde, ra, dim[] ;
		java.util.Vector<Vector> dp ;
		AtlasPage atlaspage ;
		Vector vc, vb, vt ;
		Preferences node ;

		node = ApplicationHelper.getClassNode( this, getName(), null ) ;
		overlap = ApplicationHelper.getPreferencesKV( node,
				ApplicationConstant.PK_ATLAS_OVERLAP, DEFAULT_OVERLAP ) ;

		try {
			originDe = AstrolabeFactory.valueOf( getOrigin() )[2] ;
			extentDe = AstrolabeFactory.valueOf( getExtent() )[2] ;

			spanDe = AstrolabeFactory.valueOf( getAtlasTypeChoice().getSpanDe() ) ;
		} catch ( ParameterNotValidException e ) {
			throw new RuntimeException( e.toString() ) ;
		}

		dde = originDe-extentDe ;
		nde = (int) ( dde/spanDe+1 ) ;
		sde = ( dde-spanDe )/( nde-1 ) ;
		de = originDe-spanDe ;

		for ( int cde=0 ; cde<nde ; cde++ ) {
			rad = projector.project( 0, de )[0] ;
			b = rad-projector.project( 0, de+spanDe )[0] ;
			a = b*( pagesize[0]/pagesize[1] ) ;

			tan = ( a/2*( 1-overlap/100. ) )/rad ;
			nra = (int) ( Math.rad180/java.lang.Math.atan( tan ) ) ;

			for ( int cra=0 ; cra<nra ; cra++ ) {
				ra = Math.rad360/nra*cra ;

				dp = page( ra, de, a, b, northern ) ;
				atlaspage = new AtlasPage() ;
				atlaspage.col = cra ;
				atlaspage.row = cde ;

				atlaspage.p0xy = dp.get( 0 ).toArray() ;
				atlaspage.p1xy = dp.get( 1 ).toArray() ;
				atlaspage.p2xy = dp.get( 2 ).toArray() ;
				atlaspage.p3xy = dp.get( 3 ).toArray() ;

				atlaspage.p0eq = projector.unproject( atlaspage.p0xy ) ;
				atlaspage.p0eq[0] = ApplicationHelper.mapTo0To360Range( atlaspage.p0eq[0] ) ;
				atlaspage.p1eq = projector.unproject( atlaspage.p1xy ) ;
				atlaspage.p1eq[0] = ApplicationHelper.mapTo0To360Range( atlaspage.p1eq[0] ) ;
				atlaspage.p2eq = projector.unproject( atlaspage.p2xy ) ;
				atlaspage.p2eq[0] = ApplicationHelper.mapTo0To360Range( atlaspage.p2eq[0] ) ;
				atlaspage.p3eq = projector.unproject( atlaspage.p3xy ) ;
				atlaspage.p3eq[0] = ApplicationHelper.mapTo0To360Range( atlaspage.p3eq[0] ) ;

				// atlas page equatorial center (origin)
				vc = pageCenter( dp ) ;
				atlaspage.oxy[0] = vc.x ;
				atlaspage.oxy[1] = vc.y ;
				atlaspage.oeq = projector.unproject( vc.x, vc.y ) ;
				atlaspage.oeq[0] = ApplicationHelper.mapTo0To360Range( atlaspage.oeq[0] ) ;

				// declination in middle of atlas page bottom
				dim = pageEdges( dp ) ;
				vb = new Vector( vc ) ;
				vb.scale( vc.abs()+dim[1]/2 ) ;
				atlaspage.bxy[0] = vb.x ;
				atlaspage.bxy[1] = vb.y ;
				atlaspage.beq = projector.unproject( vb.x, vb.y ) ;
				atlaspage.beq[0] = ApplicationHelper.mapTo0To360Range( atlaspage.beq[0] ) ;

				// declination in middle center of atlas page top
				vt = new Vector( vc ) ;
				vt.scale( vc.abs()-dim[1]/2 ) ;
				atlaspage.txy[0] = vt.x ;
				atlaspage.txy[1] = vt.y ;
				atlaspage.teq = projector.unproject( vt.x, vt.y ) ;
				atlaspage.teq[0] = ApplicationHelper.mapTo0To360Range( atlaspage.teq[0] ) ;

				// atlas page scale
				atlaspage.scale = pagesize[0]/dim[0]*100 ;

				r.add( atlaspage ) ;
			}

			de = de-sde ;
		}

		return r ;
	}

	private java.util.Vector<Vector> page( double ra, double de, double a, double b, boolean northern ) {
		java.util.Vector<Vector> r = new java.util.Vector<Vector>() ;
		Vector v0, v1, v2, v3, v4 ; // radius vector v0, edge vectors
		Vector vp0, vp1, vp2, vp3 ; // point vectors
		double m90[] = new double[] { 0, -1, 0, 1, 0, 0, 0, 0, 1 } ; // rotation matrix, plane xy, 90 degrees counter-clockwise
		double m90c[] = new double[] { 0, 1, 0, -1, 0, 0, 0, 0, 1 } ; // rotation matrix, plane xy, 90 degrees clockwise
		double x, rab, xy[] ;

		x = java.lang.Math.abs( a ) ;
		rab = pagesize[0]/pagesize[1] ;

		xy = projector.project( ra, de ) ;
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

	private Vector pageCenter( java.util.Vector<Vector> page ) {
		Vector v0, v2, v20, vc ;

		v0 = page.get( 0 ) ;
		v2 = page.get( 2 ) ;

		v20 = new Vector( v0 ).sub( v2 ) ;
		v20.scale( v20.abs()/2 ) ;

		vc = new Vector( v2 ).add( v20 ) ;

		return vc ;
	}

	private double[] pageEdges( java.util.Vector<Vector> page ) {
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

		aS = new astrolabe.model.AnnotationStraight() ;
		if ( getName() != null ) {
			name = ApplicationConstant.GC_NS_ATL+getName() ;

			cP.setName( name ) ;
			aS.setName( name ) ;
		}

		a = new astrolabe.model.Annotation() ;
		a.setAnnotationStraight( aS ) ;

		cP.addAnnotation( a ) ;

		designator = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_CIRCLE_ALTITUDE ) ;
		indicator = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_DMS_DEGREES ) ;
		tVal = "@{"+designator+indicator+"}@" ;
		tGen = new astrolabe.model.Text() ;
		tGen.setValue( tVal+ApplicationHelper.getLocalizedString( ApplicationConstant.LK_TEXT_DMS_DEGREES ) ) ;
		tDMS.add( tGen ) ;

		indicator = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_DMS_DEGREEMINUTES ) ;
		tGen = new astrolabe.model.Text() ;
		tVal = "@{"+designator+indicator+"}@" ;
		tGen.setValue( tVal+ApplicationHelper.getLocalizedString( ApplicationConstant.LK_TEXT_DMS_MINUTES ) ) ;
		tDMS.add( tGen ) ;

		indicator = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_DMS_DEGREESECONDS ) ;
		tGen = new astrolabe.model.Text() ;
		tVal = "@{"+designator+indicator+"}@" ;
		tGen.setValue( tVal+ApplicationHelper.getLocalizedString( ApplicationConstant.LK_TEXT_DMS_SECONDS ) ) ;
		tDMS.add( tGen ) ;

		indicator = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_DMS_DEGREEFRACTION ) ;
		tGen = new astrolabe.model.Text() ;
		tVal = ".@{"+designator+indicator+"}@" ;
		tGen.setValue( tVal ) ;
		tDMS.add( tGen ) ;

		cPAng = CAACoordinateTransformation.RadiansToDegrees( de ) ;
		cPAng = new Rational( cPAng ).getValue() ;
		dms = new DMS( cPAng ) ;
		rDMS = dms.relevant() ;
		for ( int t=rDMS[0] ; t<rDMS[1] ; t++ ) {
			tGen = tDMS.get( t ) ;
			if ( t == rDMS[0] ) {
				indicator = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_SIG_BOTH ) ;
				tVal = tGen.getValue() ;
				tGen.setValue( "@{"+designator+indicator+"}@"+tVal ) ;
			}
			aS.addText( tGen ) ;
		}

		try {
			AstrolabeFactory.modelOf( aS ) ;
			AstrolabeFactory.modelOf( cPAng, 0, 360, cP ) ;
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

		aS = new astrolabe.model.AnnotationStraight() ;
		if ( getName() != null ) {
			name = ApplicationConstant.GC_NS_ATL+getName() ;

			cM.setName( name ) ;
			aS.setName( name ) ;
		}

		a = new astrolabe.model.Annotation() ;
		a.setAnnotationStraight( aS ) ;

		cM.addAnnotation( a ) ;

		designator = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_CIRCLE_AZIMUTH ) ;
		indicator = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_HMS_HOURS ) ;
		tVal = "@{"+designator+indicator+"}@" ;
		tGen = new astrolabe.model.Text() ;
		tGen.setValue( tVal ) ;
		tDMS.add( tGen ) ;

		tSup = new astrolabe.model.Superscript() ;
		tSup.setValue( ApplicationHelper.getLocalizedString( ApplicationConstant.LK_TEXT_HMS_HOURS ) ) ;
		tGen.addSuperscript( tSup ) ;

		indicator = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_HMS_HOURMINUTES ) ;
		tGen = new astrolabe.model.Text() ;
		tVal = "@{"+designator+indicator+"}@" ;
		tGen.setValue( tVal ) ;
		tDMS.add( tGen ) ;

		tSup = new astrolabe.model.Superscript() ;
		tSup.setValue( ApplicationHelper.getLocalizedString( ApplicationConstant.LK_TEXT_HMS_MINUTES ) ) ;
		tGen.addSuperscript( tSup ) ;

		indicator = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_HMS_HOURSECONDS ) ;
		tGen = new astrolabe.model.Text() ;
		tVal = "@{"+designator+indicator+"}@" ;
		tGen.setValue( tVal ) ;
		tDMS.add( tGen ) ;

		tSup = new astrolabe.model.Superscript() ;
		tSup.setValue( ApplicationHelper.getLocalizedString( ApplicationConstant.LK_TEXT_HMS_SECONDS ) ) ;
		tGen.addSuperscript( tSup ) ;

		indicator = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_HMS_HOURFRACTION ) ;
		tGen = new astrolabe.model.Text() ;
		tVal = "@{"+designator+indicator+"}@" ;
		tGen.setValue( tVal ) ;
		tDMS.add( tGen ) ;

		cMAng = CAACoordinateTransformation.RadiansToDegrees( ra ) ;
		cMAng = new Rational( cMAng ).getValue() ;
		dms = new DMS( cMAng ) ;
		rDMS = dms.relevant() ;
		for ( int t=rDMS[0] ; t<rDMS[1] ; t++ ) {
			tGen = tDMS.get( t ) ;
			if ( t == rDMS[0] ) {
				indicator = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_SIG_MATH ) ;
				tVal = tGen.getValue() ;
				tGen.setValue( "@{"+designator+indicator+"}@"+tVal ) ;
			}
			aS.addText( tDMS.get( t ) ) ;
		}

		try {
			AstrolabeFactory.modelOf( aS ) ;

			node = ApplicationHelper.getClassNode( this, getName(), null ) ;
			cMEnd = ApplicationHelper.getPreferencesKV( node, ApplicationConstant.PK_ATLAS_LIMITDE, DEFAULT_LIMITDE ) ;
			AstrolabeFactory.modelOf( cMAng, northern?-90:90, cMEnd, cM ) ;
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
