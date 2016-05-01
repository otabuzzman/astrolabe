
package astrolabe;

import org.exolab.castor.xml.ValidationException;

@SuppressWarnings("serial")
public class AtlasPage extends astrolabe.model.AtlasPage implements PostscriptEmitter {

	public AtlasPage() {
		astrolabe.model.P0 p0 ;
		astrolabe.model.P1 p1 ;
		astrolabe.model.P2 p2 ;
		astrolabe.model.P3 p3 ;
		astrolabe.model.Origin o ;
		astrolabe.model.Top t ;
		astrolabe.model.Bottom b ;

		p0 = new astrolabe.model.P0() ;
		p0.setR( new astrolabe.model.R() ) ;
		p0.getR().setValue( 1 ) ;
		p0.setPhi( new astrolabe.model.Phi() ) ;
		p0.setTheta( new astrolabe.model.Theta() ) ;
		setP0( p0 ) ;

		p1 = new astrolabe.model.P1() ;
		p1.setR( new astrolabe.model.R() ) ;
		p1.getR().setValue( 1 ) ;
		p1.setPhi( new astrolabe.model.Phi() ) ;
		p1.setTheta( new astrolabe.model.Theta() ) ;
		setP1( p1 ) ;

		p2 = new astrolabe.model.P2() ;
		p2.setR( new astrolabe.model.R() ) ;
		p2.getR().setValue( 1 ) ;
		p2.setPhi( new astrolabe.model.Phi() ) ;
		p2.setTheta( new astrolabe.model.Theta() ) ;
		setP2( p2 ) ;

		p3 = new astrolabe.model.P3() ;
		p3.setR( new astrolabe.model.R() ) ;
		p3.getR().setValue( 1 ) ;
		p3.setPhi( new astrolabe.model.Phi() ) ;
		p3.setTheta( new astrolabe.model.Theta() ) ;
		setP3( p3 ) ;

		o = new astrolabe.model.Origin() ;
		o.setR( new astrolabe.model.R() ) ;
		o.getR().setValue( 1 ) ;
		o.setPhi( new astrolabe.model.Phi() ) ;
		o.setTheta( new astrolabe.model.Theta() ) ;
		setOrigin( o ) ;

		t = new astrolabe.model.Top() ;
		t.setR( new astrolabe.model.R() ) ;
		t.getR().setValue( 1 ) ;
		t.setPhi( new astrolabe.model.Phi() ) ;
		t.setTheta( new astrolabe.model.Theta() ) ;
		setTop( t ) ;

		b = new astrolabe.model.Bottom() ;
		b.setR( new astrolabe.model.R() ) ;
		b.getR().setValue( 1 ) ;
		b.setPhi( new astrolabe.model.Phi() ) ;
		b.setTheta( new astrolabe.model.Theta() ) ;
		setBottom( b ) ;

		try {
			validate() ;
		} catch ( ValidationException e ) {
			throw new RuntimeException( e.toString() ) ;
		}
	}

	public AtlasPage( Object peer ) throws ParameterNotValidException {
		ApplicationHelper.setupCompanionFromPeer( this, peer ) ;
		try {
			validate() ;
		} catch ( ValidationException e ) {
			throw new ParameterNotValidException( e.toString() ) ;
		}
	}

	public void headPS( PostscriptStream ps ) {
	}

	public void emitPS( PostscriptStream ps ) {
		ps.operator.mark() ;

		ps.push( getP0x() ) ;
		ps.push( getP0y() ) ;
		ps.push( getP1x() ) ;
		ps.push( getP1y() ) ;
		ps.push( getP2x() ) ;
		ps.push( getP2y() ) ;
		ps.push( getP3x() ) ;
		ps.push( getP3y() ) ;

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

	public void register() {
		String key ;

		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ATLASPAGE_NUM ) ;
		ApplicationHelper.registerNumber( key, getNum() ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ATLASPAGE_TCP ) ;
		ApplicationHelper.registerNumber( key, getTcp() ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ATLASPAGE_BCP ) ;
		ApplicationHelper.registerNumber( key, getBcp() ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ATLASPAGE_PCP ) ;
		ApplicationHelper.registerNumber( key, getPcp() ) ;
		key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ATLASPAGE_FCP ) ;
		ApplicationHelper.registerNumber( key, getFcp() ) ;
		try {
			key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ATLASPAGE_P0RA ) ;
			ApplicationHelper.registerDMS( key, AstrolabeFactory.valueOf( getP0().getPhi() ) ) ;
			key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ATLASPAGE_P0DE ) ;
			ApplicationHelper.registerDMS( key, AstrolabeFactory.valueOf( getP0().getTheta() ) ) ;
			key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ATLASPAGE_P1RA ) ;
			ApplicationHelper.registerDMS( key, AstrolabeFactory.valueOf( getP1().getPhi() ) ) ;
			key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ATLASPAGE_P1DE ) ;
			ApplicationHelper.registerDMS( key, AstrolabeFactory.valueOf( getP1().getTheta() ) ) ;
			key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ATLASPAGE_P2RA ) ;
			ApplicationHelper.registerDMS( key, AstrolabeFactory.valueOf( getP2().getPhi() ) ) ;
			key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ATLASPAGE_P2DE ) ;
			ApplicationHelper.registerDMS( key, AstrolabeFactory.valueOf( getP2().getTheta() ) ) ;
			key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ATLASPAGE_P3RA ) ;
			ApplicationHelper.registerDMS( key, AstrolabeFactory.valueOf( getP3().getPhi() ) ) ;
			key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ATLASPAGE_P3DE ) ;
			ApplicationHelper.registerDMS( key, AstrolabeFactory.valueOf( getP3().getTheta() ) ) ;
			key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ATLASPAGE_ORA ) ;
			ApplicationHelper.registerDMS( key, AstrolabeFactory.valueOf( getOrigin().getPhi() ) ) ;
			key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ATLASPAGE_ODE ) ;
			ApplicationHelper.registerDMS( key, AstrolabeFactory.valueOf( getOrigin().getTheta() ) ) ;
			key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ATLASPAGE_TRA ) ;
			ApplicationHelper.registerDMS( key, AstrolabeFactory.valueOf( getTop().getPhi() ) ) ;
			key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ATLASPAGE_TDE ) ;
			ApplicationHelper.registerNumber( key, AstrolabeFactory.valueOf( getTop().getTheta() ) ) ;
			key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ATLASPAGE_BRA ) ;
			ApplicationHelper.registerDMS( key, AstrolabeFactory.valueOf( getBottom().getPhi() ) ) ;
			key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ATLASPAGE_BDE ) ;
			ApplicationHelper.registerNumber( key, AstrolabeFactory.valueOf( getBottom().getTheta() ) ) ;
		} catch ( ParameterNotValidException e ) {
			throw new RuntimeException( e.toString() ) ;
		}
	}

	public static void modelOf( astrolabe.model.AngleType angle, boolean discrete, boolean time, double value ) {
		Rational v ;
		DMS dms ;

		v = new Rational( value ) ;

		if (discrete ) {
			dms = new DMS( v.getValue() ) ;
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
