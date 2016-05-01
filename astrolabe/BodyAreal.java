
package astrolabe;

import java.util.List;

import org.exolab.castor.xml.ValidationException;

import caa.CAACoordinateTransformation;

import com.vividsolutions.jts.geom.Geometry;

@SuppressWarnings("serial")
public class BodyAreal extends astrolabe.model.BodyAreal implements PostscriptEmitter {

	private Projector projector ;

	private List<double[]> outline ;

	public BodyAreal( Peer peer, Projector projector ) {
		PolygonSphere polygon ;
		List<double[]> outline ;
		double lo, la ;
		double sr, sd ;
		MessageCatalog m ;
		String key ;

		peer.setupCompanion( this ) ;

		this.projector = projector ;

		this.outline = AstrolabeFactory.valueOf( getPosition() ) ;

		sr = 0 ;
		sd = 0 ;

		if ( getPositionCount()>2 ) {
			outline = new java.util.Vector<double[]>() ;
			for ( double[] v : this.outline ) {
				lo = CAACoordinateTransformation.MapTo0To360Range( v[1] ) ;
				if ( lo>180 )
					lo = lo-360 ;

				la = CAACoordinateTransformation.MapTo0To360Range( v[2] ) ;
				if ( la>180 )
					la = la-360 ;
				if ( la>90 )
					la = 180-la ;
				if ( la<-90 )
					la = -180-la ;

				outline.add( new double[] { 1, lo, la } ) ;
			}			
			polygon = new PolygonSphere( outline ) ;
			sr = polygon.area() ;
			if ( sr>( 2*java.lang.Math.PI ) )
				sr = 4*java.lang.Math.PI-sr ;
			sd = sr/java.lang.Math.pow( ( 2*java.lang.Math.PI/360. ), 2 ) ;
		}

		m = new MessageCatalog( ApplicationConstant.GC_APPLICATION ) ;

		key = m.message( ApplicationConstant.LK_BODY_STERADIAN ) ;
		AstrolabeRegistry.registerDMS( key, sr ) ;
		key = m.message( ApplicationConstant.LK_BODY_SQUAREDEGREE ) ;
		AstrolabeRegistry.registerDMS( key, sd ) ;		
	}

	public void headPS( AstrolabePostscriptStream ps ) {
		ElementImportance importance ;

		importance = new ElementImportance( getImportance() ) ;
		importance.headPS( ps ) ;
		importance.emitPS( ps ) ;
		importance.tailPS( ps ) ;
	}

	public void emitPS( AstrolabePostscriptStream ps ) {
		emitPS( ps, true ) ;
	}

	public void emitPS( AstrolabePostscriptStream ps, boolean cut ) {
		ListCutter cutter ;
		List<List<double[]>> segmentList ;
		List<double[]> segment ;
		Geometry fov ;
		astrolabe.model.BodyAreal peer ;
		BodyAreal body ;
		astrolabe.model.Position position ;
		double[] lo, xy = null ;
		Vector z, p ;
		double lm , lc ;
		int ia ;
		double a ;

		if ( cut ) {
			fov = (Geometry) Registry.retrieve( ApplicationConstant.GC_FOVEFF ) ;
			if ( fov == null ) {
				fov = (Geometry) AstrolabeRegistry.retrieve( ApplicationConstant.GC_FOVUNI ) ;
			}

			cutter = new ListCutter( list(), fov ) ;
			segmentList = cutter.segmentsIntersecting( true ) ;

			lm = 0 ;
			ia = 0 ;
			for ( int is=0 ; is<segmentList.size() ; is++ ) {
				lc = Vector.length( segmentList.get( is ) ) ;
				if ( lc>lm ) {
					lm = lc ;
					ia = is ;
				}
			}

			for ( int is=0 ; is<segmentList.size() ; is++ ) {
				segment = segmentList.get( is ) ;
				peer = new astrolabe.model.BodyAreal() ;
				peer.setName( ApplicationConstant.GC_NS_CUT+getName() ) ;

				peer.setImportance( getImportance() ) ;

				for ( double[] coordinate : segment ) {
					lo = projector.unproject( coordinate ) ;

					position = new astrolabe.model.Position() ;
					// astrolabe.model.SphericalType
					position.setR( new astrolabe.model.R() ) ;
					position.getR().setValue( 1 ) ;
					// astrolabe.model.AngleType
					position.setPhi( new astrolabe.model.Phi() ) ;
					position.getPhi().setRational( new astrolabe.model.Rational() ) ;
					position.getPhi().getRational().setValue( lo[0] ) ;  
					// astrolabe.model.AngleType
					position.setTheta( new astrolabe.model.Theta() ) ;
					position.getTheta().setRational( new astrolabe.model.Rational() ) ;
					position.getTheta().getRational().setValue( lo[1] ) ;  

					peer.addPosition( position ) ;
				}

				if ( is == ia ) {
					peer.setAnnotation( getAnnotation() ) ;
				}

				try {
					peer.validate() ;
				} catch ( ValidationException e ) {
					throw new RuntimeException( e.toString() ) ;
				}

				body = new BodyAreal( peer, projector ) ;

				ps.operator.gsave();

				body.headPS( ps ) ;
				body.emitPS( ps, false ) ;
				body.tailPS( ps ) ;

				ps.operator.grestore();
			}
		} else {
			ps.operator.mark() ;

			for ( int n=outline.size() ; n>0 ; n-- ) {
				lo = outline.get( n-1 ) ;

				xy = projector.project( lo[1], lo[2] ) ;
				ps.push( xy[0] ) ;
				ps.push( xy[1] ) ;
			}

			ps.custom( ApplicationConstant.PS_CUSTOM_LISTREDUCE ) ;
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
			ps.operator.setdash( 0 ) ;
			ps.operator.setgray( 1 ) ;
			ps.operator.stroke() ;
			ps.operator.grestore() ;

			ps.operator.gsave() ;
			ps.operator.stroke() ;
			ps.operator.grestore() ;

			lo = outline.get( outline.size()-1 ) ;
			xy = projector.project( lo[1], lo[2] ) ;
			p = new Vector( xy[0], xy[1] ) ;
			xy = projector.project( 0, 90 ) ;
			z = new Vector( xy[0], xy[1] ) ; // zenit

			z.sub( p ) ;

			a = Math.atan2( z.y, z.x )-90 ;

			ps.operator.rotate( a ) ;

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

	public List<double[]> list() {
		List<double[]> r = new java.util.Vector<double[]>() ;
		double[] lo, xy ;

		for ( int n=0 ; n<outline.size() ; n++ ) {
			lo = outline.get( n ) ;
			xy = projector.project( lo[1], lo[2] ) ;
			r.add( xy ) ;
		}

		return r ;
	}
}
