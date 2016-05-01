
package astrolabe;

import org.exolab.castor.xml.ValidationException;

@SuppressWarnings("serial")
public class AtlasPage extends astrolabe.model.AtlasPage implements PostscriptEmitter {

	public AtlasPage() {
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
			ApplicationHelper.registerHMS( key, AstrolabeFactory.valueOf( getOrigin().getPhi() ) ) ;
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
}
