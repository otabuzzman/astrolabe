
package astrolabe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.prefs.Preferences;

import org.exolab.castor.xml.ValidationException;

import caa.CAACoordinateTransformation;

import com.vividsolutions.jts.geom.Polygon;

@SuppressWarnings("serial")
abstract public class ChartAzimuthalType extends astrolabe.model.ChartAzimuthalType implements Companion, Projector {

	private final static double DEFAULT_UNIT = 2.834646 ;
	private final static double DEFAULT_HALO = 4 ;
	private final static double DEFAULT_HALOMIN = .08 ;
	private final static double DEFAULT_HALOMAX = .4 ;
	private final static String DEFAULT_PRACTICALITY = "0" ;
	private final static String DEFAULT_IMPORTANCE = ".1:0" ;

	private double unit ;
	private boolean northern ;

	private double[] pagesize = new double[2] ;
	private double scale ;

	private double[] origin = new double[2] ;

	private double halo ;
	private double halomin ;
	private double halomax ;

	public ChartAzimuthalType( Object peer ) throws ParameterNotValidException {
		Preferences node ;
		String psa, psd[] ;
		Layout layout ;
		double[] origin ;
		double ms, mv, mu ;

		ApplicationHelper.setupCompanionFromPeer( this, peer ) ;
		try {
			validate() ;
		} catch ( ValidationException e ) {
			throw new ParameterNotValidException( e.toString() ) ;
		}

		if ( ( psa = ApplicationHelper.getPreferencesKV(
				ApplicationHelper.getClassNode( this, getName(), ApplicationConstant.PN_CHART_PAGESIZE ),
				getPagesize(), null ) ) == null ) {
			psd = getPagesize().split( "x" ) ;
		} else {
			psd = psa.split( "x" ) ;
		}
		pagesize[0] = new Double( psd[0] ).doubleValue() ;
		pagesize[1] = new Double( psd[1] ).doubleValue() ;

		ApplicationHelper.setFovGlobal( fov() ) ;

		layout = new Layout( getLayout(), new double[] {
			-pagesize[0]/2,
			-pagesize[1]/2,
			pagesize[0]/2,
			pagesize[1]/2 } ) ;
		Registry.register( ApplicationConstant.GC_LAYOUT, layout ) ;

		node = ApplicationHelper.getClassNode( this, getName(), null ) ;

		unit = ApplicationHelper.getPreferencesKV( node, ApplicationConstant.PK_CHART_UNIT, DEFAULT_UNIT ) ;

		mu = java.lang.Math.min( pagesize[0], pagesize[1] )/2/Math.goldensection ;
		mv = getViewport()/100. ;
		ms = getScale()/100. ;
		scale = mu*mv*ms ;

		northern = getHemisphere().equals( ApplicationConstant.AV_CHART_NORTHERN ) ;

		origin = AstrolabeFactory.valueOf( getOrigin() ) ;
		this.origin[0] = origin[1] ;
		this.origin[1] = origin[2] ;

		halo = ApplicationHelper.getPreferencesKV( node, ApplicationConstant.PK_CHART_HALO, DEFAULT_HALO ) ;
		halomin = ApplicationHelper.getPreferencesKV( node, ApplicationConstant.PK_CHART_HALOMIN, DEFAULT_HALOMIN ) ;
		halomax = ApplicationHelper.getPreferencesKV( node, ApplicationConstant.PK_CHART_HALOMAX, DEFAULT_HALOMAX ) ;
		Registry.register( ApplicationConstant.PK_CHART_HALO, new Double( halo ) ) ;
		Registry.register( ApplicationConstant.PK_CHART_HALOMIN, new Double( halomin ) ) ;
		Registry.register( ApplicationConstant.PK_CHART_HALOMAX, new Double( halomax ) ) ;
	}

	public double[] project( double RA, double d ) {
		return project( new double[] { RA, d } ) ;
	}

	public double[] project( double[] eq ) {
		double[] r ;
		Vector vp, vo, vZ, vY ;
		CoordinateSystem cs ;

		vp = new Vector( polarToWorld( hemisphereToPolar( eq ) ) ) ;

		vo = new Vector( polarToWorld( hemisphereToPolar( origin ) ) ) ;
		if ( vo.abs()>0 ) {
			vZ = new Vector( 0, 0, 1 ) ;
			vY = new Vector( vo ).mul( -1 ) ;
			cs = new CoordinateSystem( vo, vZ, vY ) ;
			vp.set( cs.local( vp.toArray() ) ) ;
		}

		vp.mul( scale ) ;

		r = new double[] { vp.x, vp.y } ;

		return r ;
	}

	public double[] unproject( double x, double y ) {
		return unproject( new double[] { x, y } ) ;
	}

	public double[] unproject( double[] xy ) {
		double[] r ;
		Vector vp, vo, vZ, vY ;
		CoordinateSystem cs ;

		vp = new Vector( xy ) ;
		vp.mul( 1/scale ) ;

		vo = new Vector( polarToWorld( hemisphereToPolar( origin ) ) ) ;
		if ( vo.abs()>0 ) {
			vZ = new Vector( 0, 0, 1 ) ;
			vY = new Vector( vo ).mul( -1 ) ;
			cs = new CoordinateSystem( vo, vZ, vY ) ;
			vp.set( cs.world( vp.toArray() ) ) ;
		}

		r = polarToHemisphere( worldToPolar( new double[] { vp.x, vp.y } ) ) ;

		return r ;
	}

	public double[] convert( double[] eq ) {
		return eq ;
	}

	public double[] convert( double RA, double d ) {
		return new double[] { RA, d } ;
	}

	public double[] unconvert( double[] eq ) {
		return eq ;
	}

	public double[] unconvert( double RA, double d ) {
		return new double[] { RA, d } ;
	}

	public void headPS( PostscriptStream ps ) {
		String practicality, importance ;
		double pagesizex, pagesizey ;

		ps.dsc.beginSetup() ;

		// Set pagesize
		try {
			pagesizex = pagesize[0]*unit ;
			pagesizey = pagesize[1]*unit ;

			ps.dict( true ) ;
			ps.push( "/PageSize" ) ;
			ps.array( true ) ;
			ps.push( pagesizex ) ;
			ps.push( pagesizey ) ;
			ps.array( false ) ;
			ps.dict( false ) ;

			ps.operator.setpagedevice() ;
		} catch ( ParameterNotValidException e ) {}

		// Set origin at center of page.
		try {
			ps.custom( ApplicationConstant.PS_PROLOG_PAGESIZE ) ;
		} catch ( ParameterNotValidException e ) {
			throw new RuntimeException( e.toString() ) ;
		}
		ps.operator.mul( .5 ) ;
		ps.operator.exch() ;
		ps.operator.mul( .5 ) ;
		ps.operator.exch() ;
		ps.operator.translate() ;
		ps.dsc.endSetup() ;

		ps.dsc.beginPageSetup() ;

		ps.operator.scale( unit ) ;

		practicality = ApplicationHelper.getPreferencesKV(
				ApplicationHelper.getClassNode( this, getName(), null ),
				ApplicationConstant.PK_CHART_PRACTICALITY, DEFAULT_PRACTICALITY ) ;
		importance = ApplicationHelper.getPreferencesKV(
				ApplicationHelper.getClassNode( this, getName(), null ),
				ApplicationConstant.PK_CHART_IMPORTANCE, DEFAULT_IMPORTANCE ) ;
		ApplicationHelper.emitPSPracticality( ps, practicality ) ;
		ApplicationHelper.emitPSImportance( ps, importance ) ;

		ps.dsc.endPageSetup() ;

		ps.dsc.page( getName(), 1 ) ;
	}

	public void emitPS( PostscriptStream ps ) {
		PostscriptEmitter atlas ;

		if ( 100.>getViewport() ) {
			ps.operator.mark() ;
			for ( double[] xy : ApplicationHelper.jtsToVector( fov() ) ) {
				ps.push( xy[0] ) ;
				ps.push( xy[1] ) ;
			}
			try {
				ps.custom( ApplicationConstant.PS_PROLOG_POLYLINE ) ;

				ps.operator.closepath() ;
				ps.operator.stroke() ;
			} catch ( ParameterNotValidException e ) {
				throw new RuntimeException( e.toString() ) ;
			}
		}

		if ( getAtlas() != null ) {
			ps.operator.gsave() ;

			try {
				atlas = new Atlas( getAtlas(), this, pagesize, northern ) ;
			} catch ( ParameterNotValidException e ) {
				throw new RuntimeException( e.toString() ) ;
			}

			atlas.headPS( ps ) ;
			atlas.emitPS( ps ) ;
			atlas.tailPS( ps ) ;

			ps.operator.grestore() ;
		}
	}

	public void tailPS( PostscriptStream ps ) {
		ps.operator.showpage() ;
		ps.dsc.pageTrailer() ;
	}

	public void addToModel( Object[] modelWithArgs ) throws ParameterNotValidException {
		astrolabe.model.AstrolabeType astrolabe ;
		astrolabe.model.Chart c ;
		astrolabe.model.ChartAzimuthalType cA ;
		String atlasName, classNameAbs, classNameRel ;
		Method methodSetClass ;
		astrolabe.model.Horizon h ;
		astrolabe.model.HorizonEquatorial hE ;
		double oRA, ode ;
		Atlas atlas ;

		if ( getAtlas() == null ) {
			String msg ;

			msg = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
			msg = MessageFormat.format( msg, new Object[] { "getAtlas()", null } ) ;

			throw new ParameterNotValidException( msg ) ;
		}
		if ( getAtlas().getName() != null ) {
			atlasName = ApplicationConstant.GC_NS_ATL+getAtlas().getName() ;
		} else {
			atlasName = null ;
		}

		astrolabe = (astrolabe.model.AstrolabeType) modelWithArgs[0] ;

		try {
			atlas = new Atlas( getAtlas(), this, pagesize, northern ) ;
		} catch ( ParameterNotValidException e ) {
			throw new RuntimeException( e.toString() ) ;
		}

		for ( AtlasPage atlasPage : atlas.volume() ) {
			hE = new astrolabe.model.HorizonEquatorial() ;
			hE.setName( atlasName ) ;
			atlas.addToModel( new Object[] { hE, atlasPage } ) ;

			h = new astrolabe.model.Horizon() ;
			h.setHorizonEquatorial( hE ) ;

			classNameRel = getClass().getSimpleName() ;
			classNameAbs = "astrolabe.model."+classNameRel ;
			try {
				cA = (astrolabe.model.ChartAzimuthalType) Class.forName( classNameAbs ).newInstance() ;
				cA.setName( atlasName ) ;
				cA.addHorizon( h ) ;

				c = new astrolabe.model.Chart() ;

				methodSetClass = c.getClass().getDeclaredMethod( "set"+classNameRel, new Class[] { Class.forName( classNameAbs ) } ) ;
				methodSetClass.invoke( c, new Object[] { cA } ) ;

				AstrolabeFactory.modelOf( hE ) ;

				oRA = CAACoordinateTransformation.RadiansToDegrees( atlasPage.oeq[0] ) ;
				ode = CAACoordinateTransformation.RadiansToDegrees( atlasPage.oeq[1] ) ;
				AstrolabeFactory.modelOf( new double[] { 1, oRA, ode }, cA ) ;

				cA.setScale( atlasPage.scale ) ;

				astrolabe.addChart( c ) ;
			} catch ( InstantiationException e ) {
				throw new RuntimeException( e.toString() ) ;
			} catch ( NoSuchMethodException e ) {
				throw new RuntimeException( e.toString() ) ;
			} catch ( ClassNotFoundException e ) {
				throw new RuntimeException( e.toString() ) ;
			} catch ( InvocationTargetException e ) {
				throw new RuntimeException( e.toString() ) ;
			} catch ( IllegalAccessException e ) {
				throw new RuntimeException( e.toString() ) ;
			} catch ( ParameterNotValidException e ) {
				throw new RuntimeException( e.toString() ) ;
			}
		}
	}

	public void emitAUX() {
		Companion atlas ;

		if ( getAtlas() != null ) {
			try {
				atlas = new Atlas( getAtlas(), this, pagesize, northern ) ;
			} catch ( ParameterNotValidException e ) {
				throw new RuntimeException( e.toString() ) ;
			}

			atlas.emitAUX() ;
		}
	}

	public double[] hemisphereToPolar( double[] eq ) {
		return northern?flipNorthern( eq ):flipSouthern( eq ) ;
	}

	public double[] polarToHemisphere( double[] p ) {
		return northern?flipNorthern( p ):flipSouthern( p ) ;
	}

	private double[] flipNorthern( double[] eq ) {
		return new double[] { ApplicationHelper.mapTo0To360Range( -eq[0] ), eq[1] } ;
	}

	private double[] flipSouthern( double[] eq ) {
		return new double[] { ApplicationHelper.mapTo0To360Range( Math.rad180+eq[0] ), -eq[1] } ;
	}

	public double[] polarToWorld( double[] eq ) {
		double d ;

		d = thetaToDistance( eq[1] ) ;

		return new double[] {
				d*java.lang.Math.cos( eq[0] ),
				d*java.lang.Math.sin( eq[0] ) } ;
	}

	public double[] worldToPolar( double[] xy ) {
		return new double[] {
				ApplicationHelper.mapTo0To360Range( java.lang.Math.atan2( xy[1], xy[0] ) ),
				distanceToTheta( java.lang.Math.sqrt( xy[0]*xy[0]+xy[1]*xy[1] ) ) } ;
	}

	abstract double thetaToDistance( double de ) ;
	abstract double distanceToTheta( double d ) ;

	private Polygon fov() {
		Polygon r ;
		java.util.Vector<double[]> l ;
		double viewport, fovx, fovy ;

		viewport = getViewport() ;

		fovx = ( pagesize[0]/2 )*viewport/100 ;
		fovy = ( pagesize[1]/2 )*viewport/100 ;

		l = new java.util.Vector<double[]>() ;
		l.add( new double[] { -fovx, -fovy } ) ; // bottom left
		l.add( new double[] { -fovx, fovy } ) ; // top left
		l.add( new double[] { fovx, fovy } ) ; // top right
		l.add( new double[] { fovx, -fovy } ) ; // bottom right

		r = ApplicationHelper.jtsToPolygon( l ) ;

		return r ;
	}
}
