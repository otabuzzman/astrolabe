
package astrolabe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;

import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.ValidationException;

import astrolabe.model.AngleType;

import caa.CAACoordinateTransformation;

@SuppressWarnings("serial")
abstract public class AtlasAzimuthalType extends astrolabe.model.AtlasAzimuthalType {

	private final static double DEFAULT_LIMITDE	= 0 ;
	private final static String DEFAULT_INTERVALUNITSH = "21600:10800:7200:3600:1800:1200:600:300:120:60:30:20:10:5:2:1" ;
	private final static String DEFAULT_INTERVALUNITSD = "324000:216000:108000:72000:36000:18000:7200:3600:1800:1200:600:300:120:60:30:20:10:5:2:1" ;

	private final static int M_CIRCLEMERIDIAN = 1 ;
	private final static int M_CIRCLEPARALLEL = 2 ;

	// castor requirement for (un)marshalling
	public AtlasAzimuthalType() {
	}

	public AtlasAzimuthalType( Peer peer ) {
		peer.setupCompanion( this ) ;
	}

	public void addAllAtlasPage() throws ValidationException {
		Projector projector ;
		boolean northern ;
		ChartPage chartPage ;
		AtlasPage atlasPage ;
		astrolabe.model.AtlasPage modelAtlasPage ;
		AngleType angle ;
		int nra, nde, grid[], tcp, bcp ;
		Vector p0, p0d, p1, p1d, p2, p3 ;
		Vector va, vb, vcen, vtop, vbot ;
		double spanRA, spanDe, originRA, originDe, extentRA, extentDe ;
		double ra, de, a, b, sina, r, cen, de0, sde, ra0, sra ;
		double rado, rade, rad, ho, he, h, s, c, p, g ;
		double[] xy, eq ;
		double r90[] = new double[] {
				0, -1, 0,
				1, 0, 0,
				0, 0, 1 } ;
		double r90c[] = new double[] {
				0, 1, 0,
				-1, 0, 0,
				0, 0, 1 } ;

		projector = projector() ;

		northern = projector.project( 0, 0 )[0]>0 ;

		originRA = AstrolabeFactory.valueOf( getOrigin() )[1] ;
		originDe = AstrolabeFactory.valueOf( getOrigin() )[2] ;
		extentRA = AstrolabeFactory.valueOf( getExtent() )[1] ;
		extentDe = AstrolabeFactory.valueOf( getExtent() )[2] ;

		chartPage = new ChartPage( getChartAzimuthalType().getChartPage() ) ;
		r = chartPage.realx()/chartPage.realy() ;

		setChartpagerealx( chartPage.realx() ) ;
		setChartpagerealy( chartPage.realy() ) ;

		angle = getAtlasAzimuthalTypeChoice().getSpanMeridian() ;
		if ( angle == null ) {
			angle = getAtlasAzimuthalTypeChoice().getSpanParallel() ;
			spanDe = AstrolabeFactory.valueOf( angle ) ;

			de0 = originDe-spanDe ;
			nde = (int) ( java.lang.Math.abs( extentDe/spanDe )+1 ) ;
			sde = ( extentDe-spanDe )/( nde-1 ) ;

			grid = new int[ nde ] ;

			for ( int num=1, cde=0 ; cde<nde ; cde++ ) {
				de = de0-cde*sde ;

				xy = projector.project( originRA, de ) ;
				p0d = new Vector( xy ) ;
				xy = projector.project( originRA, de+spanDe ) ;
				p1d = new Vector( xy ) ;

				vb = new Vector( p1d )
				.sub( p0d ) ;
				b = vb.abs() ;
				a = b*r ;
				va = new Vector( vb )
				.scale( a )
				.apply( northern?r90:r90c ) ;

				sina = a/( 2*p0d.abs() ) ;
				spanRA = Math.asin( sina )*2 ;

				ra0 = originRA ;
				nra = (int) ( java.lang.Math.abs( extentRA/spanRA )+1 ) ;
				sra = ( extentRA-spanRA )/( nra-1 ) ;

				grid[cde] = nra ;

				for ( int cra=0 ; cra<nra ; cra++ ) {
					ra = ra0+cra*sra ;

					xy = projector.project( ra, de ) ;
					p0 = new Vector( xy ) ;
					xy = projector.project( ra+spanRA, de ) ;
					p3 = new Vector( xy ) ;

					va = new Vector( p3 )
					.sub( p0 ) ;
					a = va.abs() ;
					b = a/r ;
					vb = new Vector( va )
					.scale( b )
					.apply( northern?r90c:r90 ) ;

					p1 = new Vector( p0 )
					.add( vb ) ;
					p2 = new Vector( p3 )
					.add( vb ) ;

					vcen = new Vector( p3 )
					.sub( p1 ) ;
					cen = vcen.abs()/2 ;
					vcen
					.scale( cen )
					.add( p1 ) ;

					vtop = new Vector( va )
					.scale( a/2 )
					.add( p1 ) ;

					vbot = new Vector( va )
					.scale( a/2 )
					.add( p0 ) ;

					atlasPage = new AtlasPage() ;

					atlasPage.setNum( num++ ) ;

					atlasPage.setScale( chartPage.realx()/a*100 ) ;

					atlasPage.setTcp( 0 ) ;
					atlasPage.setBcp( 0 ) ;
					atlasPage.setPcp( 0 ) ;
					atlasPage.setFcp( 0 ) ;

					atlasPage.setP0x( p0.x ) ;
					atlasPage.setP0y( p0.y ) ;
					atlasPage.setP1x( p1.x ) ;
					atlasPage.setP1y( p1.y ) ;
					atlasPage.setP2x( p2.x ) ;
					atlasPage.setP2y( p2.y ) ;
					atlasPage.setP3x( p3.x ) ;
					atlasPage.setP3y( p3.y ) ;

					eq = projector.unproject( p0.x, p0.y ) ;
					atlasPage.setP0( new astrolabe.model.P0() ) ;
					atlasPage.getP0().setPhi( new astrolabe.model.Phi() ) ;
					atlasPage.getP0().setTheta( new astrolabe.model.Theta() ) ;
					modelOf( atlasPage.getP0().getPhi(), false, false, eq[0] ) ;
					modelOf( atlasPage.getP0().getTheta(), false, false, eq[1] ) ;

					eq = projector.unproject( p1.x, p1.y ) ;
					atlasPage.setP1( new astrolabe.model.P1() ) ;
					atlasPage.getP1().setPhi( new astrolabe.model.Phi() ) ;
					atlasPage.getP1().setTheta( new astrolabe.model.Theta() ) ;
					modelOf( atlasPage.getP1().getPhi(), false, false, eq[0] ) ;
					modelOf( atlasPage.getP1().getTheta(), false, false, eq[1] ) ;

					eq = projector.unproject( p2.x, p2.y ) ;
					atlasPage.setP2( new astrolabe.model.P2() ) ;
					atlasPage.getP2().setPhi( new astrolabe.model.Phi() ) ;
					atlasPage.getP2().setTheta( new astrolabe.model.Theta() ) ;
					modelOf( atlasPage.getP2().getPhi(), false, false, eq[0] ) ;
					modelOf( atlasPage.getP2().getTheta(), false, false, eq[1] ) ;

					eq = projector.unproject( p3.x, p3.y ) ;
					atlasPage.setP3( new astrolabe.model.P3() ) ;
					atlasPage.getP3().setPhi( new astrolabe.model.Phi() ) ;
					atlasPage.getP3().setTheta( new astrolabe.model.Theta() ) ;
					modelOf( atlasPage.getP3().getPhi(), false, false, eq[0] ) ;
					modelOf( atlasPage.getP3().getTheta(), false, false, eq[1] ) ;

					// atlas page equatorial center (origin)
					atlasPage.setOriginx( vcen.x ) ;
					atlasPage.setOriginy( vcen.y ) ;
					eq = projector.unproject( vcen.x, vcen.y ) ;
					eq[0] = CAACoordinateTransformation.DegreesToHours( eq[0] ) ;
					atlasPage.setOrigin( new astrolabe.model.Origin() ) ;
					atlasPage.getOrigin().setPhi( new astrolabe.model.Phi() ) ;
					atlasPage.getOrigin().setTheta( new astrolabe.model.Theta() ) ;
					modelOf( atlasPage.getOrigin().getPhi(), true, true, eq[0] ) ;
					modelOf( atlasPage.getOrigin().getTheta(), true, false, eq[1] ) ;

					// declination in middle center of atlas page top
					atlasPage.setTopx( vtop.x ) ;
					atlasPage.setTopy( vtop.y ) ;
					eq = projector.unproject( vtop.x, vtop.y ) ;
					atlasPage.setTop( new astrolabe.model.Top() ) ;
					atlasPage.getTop().setPhi( new astrolabe.model.Phi() ) ;
					atlasPage.getTop().setTheta( new astrolabe.model.Theta() ) ;
					modelOf( atlasPage.getTop().getPhi(), false, false, eq[0] ) ;
					modelOf( atlasPage.getTop().getTheta(), false, false, eq[1] ) ;

					atlasPage.setBottomx( vbot.x ) ;
					atlasPage.setBottomy( vbot.y ) ;
					eq = projector.unproject( vbot.x, vbot.y ) ;
					atlasPage.setBottom( new astrolabe.model.Bottom() ) ;
					atlasPage.getBottom().setPhi( new astrolabe.model.Phi() ) ;
					atlasPage.getBottom().setTheta( new astrolabe.model.Theta() ) ;
					modelOf( atlasPage.getBottom().getPhi(), false, false, eq[0] ) ;
					modelOf( atlasPage.getBottom().getTheta(), false, false, eq[1] ) ;

					atlasPage.validate() ;

					addAtlasPage( atlasPage ) ;
				}
			}
		} else {
			spanRA = AstrolabeFactory.valueOf( angle ) ;

			ra0 = originRA ;
			nra = (int) ( java.lang.Math.abs( extentRA/spanRA )+1 ) ;
			sra = ( extentRA-spanRA )/( nra-1 ) ;

			rado = new Vector( projector.project( originRA, originDe ) )
			.abs() ;
			rade = new Vector( projector.project( originRA+extentRA, originDe-extentDe ) )
			.abs() ;

			ho = Math.cos( spanRA/2 )*rado ;
			he = Math.cos( spanRA/2 )*rade ;

			h = he ;
			a = 2*Math.sin( spanRA/2 )*rade ;
			b = a/r ;
			p = b/h ;

			s = b ;
			for ( nde=1 ; ! ( he-s<ho ) ; nde++ ) {
				h = h-b ;
				b = h*p ;
				s = s+b ;
			}
			c = ( h-ho )/b ;

			grid = new int[ nde ] ;

			for ( int num=1, cde=0 ; cde<nde ; cde++ ) {
				h = cde==0?ho/( 1-p ):	// 1st ring
					cde==nde-1?he:		// Nth ring
						h/( 1-p )*( 1-( ( 1-c )/( nde-1 )*p ) ) ;

				rad = h/Math.cos( spanRA/2 ) ;
				de = projector.unproject( rad, 0 )[1] ;

				grid[cde] = nra ;

				for ( int cra=0 ; cra<nra ; cra++ ) {
					ra = ra0+cra*sra ;

					xy = projector.project( ra, de ) ;
					p0 = new Vector( xy ) ;
					xy = projector.project( ra+spanRA, de ) ;
					p3 = new Vector( xy ) ;

					va = new Vector( p3 )
					.sub( p0 ) ;
					a = va.abs() ;
					b = a/r ;
					vb = new Vector( va )
					.scale( b )
					.apply( northern?r90c:r90 ) ;

					p1 = new Vector( p0 )
					.add( vb ) ;
					p2 = new Vector( p3 )
					.add( vb ) ;

					vcen = new Vector( p3 )
					.sub( p1 ) ;
					cen = vcen.abs()/2 ;
					vcen
					.scale( cen )
					.add( p1 ) ;

					vtop = new Vector( va )
					.scale( a/2 )
					.add( p1 ) ;

					vbot = new Vector( va )
					.scale( a/2 )
					.add( p0 ) ;

					atlasPage = new AtlasPage() ;

					atlasPage.setNum( num++ ) ;

					atlasPage.setScale( chartPage.realx()/a*100 ) ;

					atlasPage.setTcp( 0 ) ;
					atlasPage.setBcp( 0 ) ;
					atlasPage.setPcp( 0 ) ;
					atlasPage.setFcp( 0 ) ;

					atlasPage.setP0x( p0.x ) ;
					atlasPage.setP0y( p0.y ) ;
					atlasPage.setP1x( p1.x ) ;
					atlasPage.setP1y( p1.y ) ;
					atlasPage.setP2x( p2.x ) ;
					atlasPage.setP2y( p2.y ) ;
					atlasPage.setP3x( p3.x ) ;
					atlasPage.setP3y( p3.y ) ;

					eq = projector.unproject( p0.x, p0.y ) ;
					atlasPage.setP0( new astrolabe.model.P0() ) ;
					atlasPage.getP0().setPhi( new astrolabe.model.Phi() ) ;
					atlasPage.getP0().setTheta( new astrolabe.model.Theta() ) ;
					modelOf( atlasPage.getP0().getPhi(), false, false, eq[0] ) ;
					modelOf( atlasPage.getP0().getTheta(), false, false, eq[1] ) ;

					eq = projector.unproject( p1.x, p1.y ) ;
					atlasPage.setP1( new astrolabe.model.P1() ) ;
					atlasPage.getP1().setPhi( new astrolabe.model.Phi() ) ;
					atlasPage.getP1().setTheta( new astrolabe.model.Theta() ) ;
					modelOf( atlasPage.getP1().getPhi(), false, false, eq[0] ) ;
					modelOf( atlasPage.getP1().getTheta(), false, false, eq[1] ) ;

					eq = projector.unproject( p2.x, p2.y ) ;
					atlasPage.setP2( new astrolabe.model.P2() ) ;
					atlasPage.getP2().setPhi( new astrolabe.model.Phi() ) ;
					atlasPage.getP2().setTheta( new astrolabe.model.Theta() ) ;
					modelOf( atlasPage.getP2().getPhi(), false, false, eq[0] ) ;
					modelOf( atlasPage.getP2().getTheta(), false, false, eq[1] ) ;

					eq = projector.unproject( p3.x, p3.y ) ;
					atlasPage.setP3( new astrolabe.model.P3() ) ;
					atlasPage.getP3().setPhi( new astrolabe.model.Phi() ) ;
					atlasPage.getP3().setTheta( new astrolabe.model.Theta() ) ;
					modelOf( atlasPage.getP3().getPhi(), false, false, eq[0] ) ;
					modelOf( atlasPage.getP3().getTheta(), false, false, eq[1] ) ;

					// atlas page equatorial center (origin)
					atlasPage.setOriginx( vcen.x ) ;
					atlasPage.setOriginy( vcen.y ) ;
					eq = projector.unproject( vcen.x, vcen.y ) ;
					eq[0] = CAACoordinateTransformation.DegreesToHours( eq[0] ) ;
					atlasPage.setOrigin( new astrolabe.model.Origin() ) ;
					atlasPage.getOrigin().setPhi( new astrolabe.model.Phi() ) ;
					atlasPage.getOrigin().setTheta( new astrolabe.model.Theta() ) ;
					modelOf( atlasPage.getOrigin().getPhi(), true, true, eq[0] ) ;
					modelOf( atlasPage.getOrigin().getTheta(), true, false, eq[1] ) ;

					// declination in middle center of atlas page top
					atlasPage.setTopx( vtop.x ) ;
					atlasPage.setTopy( vtop.y ) ;
					eq = projector.unproject( vtop.x, vtop.y ) ;
					atlasPage.setTop( new astrolabe.model.Top() ) ;
					atlasPage.getTop().setPhi( new astrolabe.model.Phi() ) ;
					atlasPage.getTop().setTheta( new astrolabe.model.Theta() ) ;
					modelOf( atlasPage.getTop().getPhi(), false, false, eq[0] ) ;
					modelOf( atlasPage.getTop().getTheta(), false, false, eq[1] ) ;

					atlasPage.setBottomx( vbot.x ) ;
					atlasPage.setBottomy( vbot.y ) ;
					eq = projector.unproject( vbot.x, vbot.y ) ;
					atlasPage.setBottom( new astrolabe.model.Bottom() ) ;
					atlasPage.getBottom().setPhi( new astrolabe.model.Phi() ) ;
					atlasPage.getBottom().setTheta( new astrolabe.model.Theta() ) ;
					modelOf( atlasPage.getBottom().getPhi(), false, false, eq[0] ) ;
					modelOf( atlasPage.getBottom().getTheta(), false, false, eq[1] ) ;

					atlasPage.validate() ;

					addAtlasPage( atlasPage ) ;
				}
			}
		}

		for ( int cra=0 ; cra<grid[0] ; cra++ ) {
			modelAtlasPage = getAtlasPage( cra ) ;
			modelAtlasPage.setPcp( cra ) ;
			modelAtlasPage.setFcp( cra+2 ) ;
		}
		getAtlasPage( 0 ).setPcp( grid[0] ) ;
		getAtlasPage( grid[0]-1 ).setFcp( 1 ) ;

		for ( int cde=1, num=0 ; cde<grid.length ; cde++ ) {
			g = (double) grid[cde-1]/grid[cde] ;

			if ( cde>1 )
				num = num+grid[cde-2] ;

			for ( int cra=0 ; cra<grid[cde] ; cra++ ) {
				tcp = num+(int) ( g*cra+.5 )+1 ;
				bcp = num+grid[cde-1]+cra+1 ;
				getAtlasPage( tcp-1 ).setBcp( bcp ) ;
				modelAtlasPage = getAtlasPage( bcp-1 ) ;
				modelAtlasPage.setTcp( tcp ) ;
				modelAtlasPage.setPcp( bcp-1 ) ;
				modelAtlasPage.setFcp( bcp+1 ) ;
			}
			getAtlasPage( num+grid[cde-1] ).setPcp( num+grid[cde-1]+grid[cde] ) ;
			getAtlasPage( num+grid[cde-1]+grid[cde]-1 ).setFcp( num+grid[cde-1]+1 ) ;
		}
	}

	public void toModel( astrolabe.model.ChartAzimuthalType chartAzimuthalType, int atlaspage ) {
		double[] checkerTransform ;
		double p0de, tde, p1ra, p2ra ;
		double lde, era, ede ;
		int checker ;
		boolean northern ;
		AtlasPage atlasPage ;
		astrolabe.model.Horizon h ;
		astrolabe.model.HorizonEquatorial hE ;
		double circleNumber ;
		double ra, de ;

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

		lde = Configuration.getValue(
				Configuration.getClassNode( this, getName(), null ),
				ApplicationConstant.PK_ATLAS_LIMITDE, DEFAULT_LIMITDE ) ;

		p1ra = AstrolabeFactory.valueOf( atlasPage.getP1().getPhi() ) ;
		p2ra = AstrolabeFactory.valueOf( atlasPage.getP2().getPhi() ) ;
		era = CAACoordinateTransformation.MapTo0To360Range( p2ra-p1ra+360 ) ;

		checker = getCheckerParallel().getValue() ;
		if ( checker>1 ) {
			p0de = AstrolabeFactory.valueOf( atlasPage.getP0().getTheta() ) ;
			tde = AstrolabeFactory.valueOf( atlasPage.getTop().getTheta() ) ;
			if ( era>180 )
				ede = java.lang.Math.abs( ( northern?90:-90 )-p0de ) ;
			else
				ede = java.lang.Math.abs( tde-p0de ) ;
			if ( northern ) {
				checkerTransform = checkerTransform( p0de, ede, checker, intervalD() ) ;
				de = checkerTransform[0] ;
			} else {
				checkerTransform = checkerTransform( -p0de, ede, checker, intervalD() ) ;
				de = -checkerTransform[0] ;
			}
			hE.addCircle( modelOf( M_CIRCLEPARALLEL, de, 0, 360 ) ) ;

			circleNumber = checker-1 ;
			for ( int i=1 ; i<circleNumber ; i++ ) {
				if ( northern )
					de = de+checkerTransform[1] ;
				else
					de = de-checkerTransform[1] ;
				hE.addCircle( modelOf( M_CIRCLEPARALLEL, de, 0, 360 ) ) ;
			}
		}

		checker = getCheckerMeridian().getValue() ;
		if ( checker>1 ) {
			if ( era>180 ) {
				checkerTransform = checkerTransform( p2ra, era, checker, intervalH() ) ;
				ra = CAACoordinateTransformation.MapTo0To360Range( checkerTransform[0] ) ;

				hE.addCircle( modelOf( M_CIRCLEMERIDIAN, ra, lde, northern?-90:90 ) ) ;
				circleNumber = java.lang.Math.ceil( 360/checkerTransform[1] ) ;
			} else {
				checkerTransform = checkerTransform( p1ra, era, checker, intervalH() ) ;
				ra = CAACoordinateTransformation.MapTo0To360Range( checkerTransform[0] ) ;

				hE.addCircle( modelOf( M_CIRCLEMERIDIAN, ra, northern?-90:90, lde ) ) ;
				circleNumber = checker-1 ;
			}

			for ( int i=1 ; i<circleNumber ; i++ ) {
				ra = CAACoordinateTransformation.MapTo0To360Range( ra+checkerTransform[1] ) ;
				if ( era>180 )
					hE.addCircle( modelOf( M_CIRCLEMERIDIAN, ra, lde, northern?-90:90 ) ) ;
				else
					hE.addCircle( modelOf( M_CIRCLEMERIDIAN, ra, northern?-90:90, lde ) ) ;
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

	private static double[] checkerTransform( double b, double e, int n, double[] interval ) {
		double[] r = new double[2] ;
		double g0, gN ;

		// 1. find g0 for smallest interval that fits n times into e-b
		for ( int i=0 ; i<interval.length ; i++ ) {
			g0 = ( java.lang.Math.floor( b/interval[i] )+1 )*interval[i] ;
			gN = g0+( n-2 )*interval[i] ;
			if ( gN<b+e ) {
				r[0] = g0 ;

				break ;
			}
		}
		// 2. find biggest interval that fits n times into e-g0
		for ( int i=0 ; i<interval.length ; i++ ) {
			gN = r[0]/*g0*/+( n-2 )*interval[i] ;
			if ( gN<b+e ) {
				r[1] = interval[i] ;

				break ;
			}
		}

		return r ;
	}

	private double[] intervalH() {
		String[] iP ;
		double[] iV ;

		iP = Configuration.getValue(
				Configuration.getClassNode( this, getName(), null ),
				ApplicationConstant.PK_ATLAS_INTERVALUNITSH, DEFAULT_INTERVALUNITSH ).split( ":" ) ;
		iV = new double[ iP.length ] ;
		for ( int iu=0 ; iu<iP.length ; iu++ ) {
			iV[iu] = new Double( iP[iu] ).doubleValue()*1./3600*15 ;
		}

		return iV ;
	}

	private double[] intervalD() {
		String[] iP ;
		double[] iV ;

		iP = Configuration.getValue(
				Configuration.getClassNode( this, getName(), null ),
				ApplicationConstant.PK_ATLAS_INTERVALUNITSD, DEFAULT_INTERVALUNITSD ).split( ":" ) ;
		iV = new double[ iP.length ] ;
		for ( int iu=0 ; iu<iP.length ; iu++ ) {
			iV[iu] = new Double( iP[iu] ).doubleValue()*1./3600 ;
		}

		return iV ;
	}

	private astrolabe.model.Circle modelOf( int model, double a, double b, double e ) {
		astrolabe.model.Circle r ;
		astrolabe.model.CircleMeridian cM ;
		astrolabe.model.CircleParallel cP ;

		r = new astrolabe.model.Circle() ;

		switch ( model ) {
		case M_CIRCLEMERIDIAN:
			cM = new astrolabe.model.CircleMeridian() ;
			if ( getName() == null )
				cM.setName( ApplicationConstant.GC_NS_ATL ) ;
			else
				cM.setName( ApplicationConstant.GC_NS_ATL+getName() ) ;
			AstrolabeFactory.modelOf( cM, false ) ;
			modelOf( cM, a, b, e ) ;

			cM.addAnnotation( getCheckerMeridian().getAnnotation() ) ;

			r.setCircleMeridian( cM ) ;
			break ;
		case M_CIRCLEPARALLEL:
			cP = new astrolabe.model.CircleParallel() ;
			if ( getName() == null )
				cP.setName( ApplicationConstant.GC_NS_ATL ) ;
			else
				cP.setName( ApplicationConstant.GC_NS_ATL+getName() ) ;
			AstrolabeFactory.modelOf( cP, false ) ;
			modelOf( cP, a, b, e ) ;

			cP.addAnnotation( getCheckerParallel().getAnnotation() ) ;

			r.setCircleParallel( cP ) ;
			break ;
		default:
			return null ;
		}

		return r ;
	}
	private static void modelOf( astrolabe.model.CircleMeridian circle, double a, double b, double e ) {
		circle.setAngle( new astrolabe.model.Angle() ) ;
		circle.getAngle().setRational( new astrolabe.model.Rational() ) ;
		circle.getAngle()
		.getRational().setValue( a ) ;

		circle.setBegin( new astrolabe.model.Begin() ) ;
		circle.getBegin().setAngle( new astrolabe.model.Angle() ) ;
		circle.getBegin().getAngle().setRational( new astrolabe.model.Rational() ) ;
		circle.getBegin().getAngle()
		.getRational().setValue( b ) ;

		circle.setEnd( new astrolabe.model.End() ) ;
		circle.getEnd().setAngle( new astrolabe.model.Angle() ) ;
		circle.getEnd().getAngle().setRational( new astrolabe.model.Rational() ) ;
		circle.getEnd().getAngle()
		.getRational().setValue( e ) ;
	}

	private static void modelOf( astrolabe.model.CircleParallel circle, double a, double b, double e ) {
		circle.setAngle( new astrolabe.model.Angle() ) ;
		circle.getAngle().setRational( new astrolabe.model.Rational() ) ;
		circle.getAngle()
		.getRational().setValue( a ) ;

		circle.setBegin( new astrolabe.model.Begin() ) ;
		circle.getBegin().setAngle( new astrolabe.model.Angle() ) ;
		circle.getBegin().getAngle().setRational( new astrolabe.model.Rational() ) ;
		circle.getBegin().getAngle()
		.getRational().setValue( b ) ;

		circle.setEnd( new astrolabe.model.End() ) ;
		circle.getEnd().setAngle( new astrolabe.model.Angle() ) ;
		circle.getEnd().getAngle().setRational( new astrolabe.model.Rational() ) ;
		circle.getEnd().getAngle()
		.getRational().setValue( e ) ;
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
