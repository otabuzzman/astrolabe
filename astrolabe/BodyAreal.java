
package astrolabe;

import java.util.List;
import java.util.prefs.Preferences;

import org.exolab.castor.xml.ValidationException;

import com.vividsolutions.jts.geom.Geometry;

import caa.CAACoordinateTransformation;

@SuppressWarnings("serial")
public class BodyAreal extends astrolabe.model.BodyAreal implements PostscriptEmitter {

	private Projector projector ;

	private final static String DEFAULT_IMPORTANCE = ".36:1:1.8" ;

	private java.util.Vector<double[]> outline ;

	public BodyAreal( Object peer, Projector projector ) throws ParameterNotValidException {
		PolygonSpherical polygon ;
		String key ;
		double rad1 ;

		ApplicationHelper.setupCompanionFromPeer( this, peer ) ;
		try {
			validate() ;
		} catch ( ValidationException e ) {
			throw new ParameterNotValidException( e.toString() ) ;
		}

		this.projector = projector ;

		outline = AstrolabeFactory.valueOf( getPosition() ) ;
		if ( outline.size()>2 ) {
			polygon = new PolygonSpherical( outline ) ;
		} else {
			polygon = null ;
		}

		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_BODY_STERADIAN ) ;
		ApplicationHelper.registerDMS( key, polygon==null?0:polygon.area(), 2 ) ;
		rad1 = CAACoordinateTransformation.DegreesToRadians( 1 ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_BODY_SQUAREDEGREE ) ;
		ApplicationHelper.registerDMS( key, polygon==null?0:polygon.area()/( rad1*rad1 ), 2 ) ;		
	}

	public void headPS( PostscriptStream ps ) {
		Preferences node ;
		String importance ;

		node = ApplicationHelper.getClassNode( this, getName(), getType() ) ;

		importance = ApplicationHelper.getPreferencesKV( node, ApplicationConstant.PK_BODY_IMPORTANCE, DEFAULT_IMPORTANCE ) ;

		ApplicationHelper.emitPSImportance( ps, importance ) ;
	}

	public void emitPS( PostscriptStream ps ) {
		emitPS( ps, true ) ;
	}

	public void emitPS( PostscriptStream ps, boolean cut ) {
		ListCutter cutter ;
		Geometry fov ;
		astrolabe.model.BodyAreal peer ;
		BodyAreal body ;
		astrolabe.model.Position position ;
		double phi, the ;
		double[] lo, xy = null ;
		Vector z, p ;
		double a ;

		if ( cut ) {
			fov = ApplicationHelper.getFovEffective() ;
			if ( fov == null ) {
				fov = ApplicationHelper.getFovGlobal() ;
			}
			cutter = new ListCutter( list(), fov ) ;
			for ( List<double[]> segment : cutter.segmentsInterior() ) {
				peer = new astrolabe.model.BodyAreal() ;

				for ( double[] coordinate : segment ) {
					lo = projector.unproject( coordinate ) ;
					lo[0] = ApplicationHelper.mapTo0To360Range( lo[0] ) ;

					phi = CAACoordinateTransformation.RadiansToDegrees( lo[0] ) ;
					the = CAACoordinateTransformation.RadiansToDegrees( lo[1] ) ;

					position = new astrolabe.model.Position() ;
					// astrolabe.model.SphericalType
					position.setR( new astrolabe.model.R() ) ;
					position.getR().setValue( 1 ) ;
					// astrolabe.model.AngleType
					position.setPhi( new astrolabe.model.Phi() ) ;
					position.getPhi().setRational( new astrolabe.model.Rational() ) ;
					position.getPhi().getRational().setValue( phi ) ;  
					// astrolabe.model.AngleType
					position.setTheta( new astrolabe.model.Theta() ) ;
					position.getTheta().setRational( new astrolabe.model.Rational() ) ;
					position.getTheta().getRational().setValue( the ) ;  

					peer.addPosition( position ) ;
				}

				if ( getName() != null ) {
					peer.setName( ApplicationConstant.GC_NS_CUT+getName() ) ;
				}

				peer.setType( getType() ) ;

				peer.setAnnotation( getAnnotation() ) ;

				try {
					body = new BodyAreal( peer, projector ) ;

					ps.operator.gsave();

					body.headPS( ps ) ;
					body.emitPS( ps, false ) ;
					body.tailPS( ps ) ;

					ps.operator.grestore();
				} catch ( ParameterNotValidException e ) {}
			}
		} else {
			ps.operator.mark() ;

			for ( int n=outline.size() ; n>0 ; n-- ) {
				lo = outline.get( n-1 ) ;

				xy = projector.project( lo[1], lo[2] ) ;
				ps.push( xy[0] ) ;
				ps.push( xy[1] ) ;
			}
			try {
				ps.custom( ApplicationConstant.PS_PROLOG_LISTREDUCE ) ;
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

				ps.operator.gsave() ;
				ps.operator.stroke() ;
				ps.operator.grestore() ;
			} catch ( ParameterNotValidException e ) {
				throw new RuntimeException( e.toString() ) ;			
			}

			lo = outline.get( outline.size()-1 ) ;
			xy = projector.project( lo[1], lo[2] ) ;
			p = new Vector( xy[0], xy[1] ) ;
			xy = projector.project( 0, Math.rad90 ) ;
			z = new Vector( xy[0], xy[1] ) ; // zenit

			z.sub( p ) ;

			a = java.lang.Math.atan2( z.y, z.x )-Math.rad90 ;
			a = CAACoordinateTransformation.RadiansToDegrees( a ) ;

			ps.operator.rotate( a ) ;

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

	public java.util.Vector<double[]> list() {
		java.util.Vector<double[]> r = new java.util.Vector<double[]>() ;
		double[] lo, xy ;

		for ( int n=0 ; n<outline.size() ; n++ ) {
			lo = outline.get( n ) ;
			xy = projector.project( lo[1], lo[2] ) ;
			r.add( xy ) ;
		}

		return r ;
	}
}
