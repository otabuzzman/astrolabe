
package astrolabe;

import org.exolab.castor.xml.ValidationException;

import caa.CAACoordinateTransformation;

@SuppressWarnings("serial")
public class AtlasPage extends astrolabe.model.AtlasPage implements PostscriptEmitter {

	public double col ;
	public double row ;

	public double[] p0xy = new double[2] ;
	public double[] p1xy = new double[2] ;
	public double[] p2xy = new double[2] ;
	public double[] p3xy = new double[2] ;

	public double[] oxy = new double[2] ;

	public double[] txy = new double[2] ;
	public double[] bxy = new double[2] ;

	public double[] p0eq = new double[2] ;
	public double[] p1eq = new double[2] ;
	public double[] p2eq = new double[2] ;
	public double[] p3eq = new double[2] ;

	public double[] oeq = new double[2] ;

	public double[] teq = new double[2] ;
	public double[] beq = new double[2] ;

	public double scale ;

	public AtlasPage() {
	}

	public void headPS( PostscriptStream ps ) {
	}

	public void emitPS( PostscriptStream ps ) {
		ps.operator.mark() ;

		ps.push( p0xy[0] ) ;
		ps.push( p0xy[1] ) ;
		ps.push( p1xy[0] ) ;
		ps.push( p1xy[1] ) ;
		ps.push( p2xy[0] ) ;
		ps.push( p2xy[1] ) ;
		ps.push( p3xy[0] ) ;
		ps.push( p3xy[1] ) ;

		try {
			ps.custom( ApplicationConstant.PS_PROLOG_POLYLINE ) ;

			ps.operator.closepath() ;
			ps.operator.stroke() ;
		} catch ( ParameterNotValidException e ) {
			throw new RuntimeException( e.toString() ) ;
		}
	}

	public void tailPS( PostscriptStream ps ) {
	}

	public astrolabe.model.AtlasPage toModel() {
		double p0ra, p0de, p1ra, p1de, p2ra, p2de, p3ra, p3de ;
		astrolabe.model.P0 p0 ;
		astrolabe.model.P1 p1 ;
		astrolabe.model.P2 p2 ;
		astrolabe.model.P3 p3 ;
		double ora, ode, tra, tde, bra, bde ;
		astrolabe.model.Origin o ;
		astrolabe.model.Top t ;
		astrolabe.model.Bottom b ;

		setScale( scale ) ;

		setP0x( p0xy[0] ) ;
		setP0y( p0xy[1] ) ;
		setP1x( p1xy[0] ) ;
		setP1y( p1xy[1] ) ;
		setP2x( p2xy[0] ) ;
		setP2y( p2xy[1] ) ;
		setP3x( p3xy[0] ) ;
		setP3y( p3xy[1] ) ;

		setOriginx( oxy[0] ) ;
		setOriginy( oxy[1] ) ;

		setTopx( txy[0] ) ;
		setTopy( txy[1] ) ;
		setBottomx( bxy[0] ) ;
		setBottomy( bxy[1] ) ;

		p0 = new astrolabe.model.P0() ;
		p1 = new astrolabe.model.P1() ;
		p2 = new astrolabe.model.P2() ;
		p3 = new astrolabe.model.P3() ;

		o = new astrolabe.model.Origin() ;

		t = new astrolabe.model.Top() ;
		b = new astrolabe.model.Bottom() ;

		p0ra = CAACoordinateTransformation.RadiansToDegrees( p0eq[0] ) ;
		p0de = CAACoordinateTransformation.RadiansToDegrees( p0eq[1] ) ;
		p1ra = CAACoordinateTransformation.RadiansToDegrees( p1eq[0] ) ;
		p1de = CAACoordinateTransformation.RadiansToDegrees( p1eq[1] ) ;
		p2ra = CAACoordinateTransformation.RadiansToDegrees( p2eq[0] ) ;
		p2de = CAACoordinateTransformation.RadiansToDegrees( p2eq[1] ) ;
		p3ra = CAACoordinateTransformation.RadiansToDegrees( p3eq[0] ) ;
		p3de = CAACoordinateTransformation.RadiansToDegrees( p3eq[1] ) ;

		ora = CAACoordinateTransformation.RadiansToDegrees( oeq[0] ) ;
		ode = CAACoordinateTransformation.RadiansToDegrees( oeq[1] ) ;

		tra = CAACoordinateTransformation.RadiansToDegrees( teq[0] ) ;
		tde = CAACoordinateTransformation.RadiansToDegrees( teq[1] ) ;
		bra = CAACoordinateTransformation.RadiansToDegrees( beq[0] ) ;
		bde = CAACoordinateTransformation.RadiansToDegrees( beq[1] ) ;

		try {
			AstrolabeFactory.modelOf( 1, p0ra, p0de, p0 ) ;
			AstrolabeFactory.modelOf( 1, p1ra, p1de, p1 ) ;
			AstrolabeFactory.modelOf( 1, p2ra, p2de, p2 ) ;
			AstrolabeFactory.modelOf( 1, p3ra, p3de, p3 ) ;

			AstrolabeFactory.modelOf( 1, ora, ode, o ) ;

			AstrolabeFactory.modelOf( 1, tra, tde, t ) ;
			AstrolabeFactory.modelOf( 1, bra, bde, b ) ;
		} catch ( ParameterNotValidException e ) {
			throw new RuntimeException( e.toString() ) ;
		}

		setP0( p0 ) ;
		setP1( p1 ) ;
		setP2( p2 ) ;
		setP3( p3 ) ;

		setOrigin( o ) ;

		setTop( t ) ;
		setBottom( b ) ;

		try {
			validate() ;
		} catch ( ValidationException e ) {
			throw new RuntimeException( e.toString() ) ;
		}

		return this ;
	}
}
