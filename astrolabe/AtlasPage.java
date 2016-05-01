
package astrolabe;

import org.exolab.castor.xml.ValidationException;

@SuppressWarnings("serial")
public class AtlasPage extends astrolabe.model.AtlasPage implements PostscriptEmitter {

	// castor requirement for (un)marshalling
	public AtlasPage() {
	}

	public AtlasPage( Peer peer ) throws ParameterNotValidException {
		peer.setupCompanion( this ) ;
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
		MessageCatalog m ;

		m = new MessageCatalog( ApplicationConstant.GC_APPLICATION ) ;

		key = m.message( ApplicationConstant.LK_ATLASPAGE_NUM ) ;
		Registry.registerNumber( key, getNum() ) ;
		key = m.message( ApplicationConstant.LK_ATLASPAGE_TCP ) ;
		Registry.registerNumber( key, getTcp() ) ;
		key = m.message( ApplicationConstant.LK_ATLASPAGE_BCP ) ;
		Registry.registerNumber( key, getBcp() ) ;
		key = m.message( ApplicationConstant.LK_ATLASPAGE_PCP ) ;
		Registry.registerNumber( key, getPcp() ) ;
		key = m.message( ApplicationConstant.LK_ATLASPAGE_FCP ) ;
		Registry.registerNumber( key, getFcp() ) ;
		try {
			key = m.message( ApplicationConstant.LK_ATLASPAGE_P0RA ) ;
			AstrolabeRegistry.registerDMS( key, AstrolabeFactory.valueOf( getP0().getPhi() ) ) ;
			key = m.message( ApplicationConstant.LK_ATLASPAGE_P0DE ) ;
			AstrolabeRegistry.registerDMS( key, AstrolabeFactory.valueOf( getP0().getTheta() ) ) ;
			key = m.message( ApplicationConstant.LK_ATLASPAGE_P1RA ) ;
			AstrolabeRegistry.registerDMS( key, AstrolabeFactory.valueOf( getP1().getPhi() ) ) ;
			key = m.message( ApplicationConstant.LK_ATLASPAGE_P1DE ) ;
			AstrolabeRegistry.registerDMS( key, AstrolabeFactory.valueOf( getP1().getTheta() ) ) ;
			key = m.message( ApplicationConstant.LK_ATLASPAGE_P2RA ) ;
			AstrolabeRegistry.registerDMS( key, AstrolabeFactory.valueOf( getP2().getPhi() ) ) ;
			key = m.message( ApplicationConstant.LK_ATLASPAGE_P2DE ) ;
			AstrolabeRegistry.registerDMS( key, AstrolabeFactory.valueOf( getP2().getTheta() ) ) ;
			key = m.message( ApplicationConstant.LK_ATLASPAGE_P3RA ) ;
			AstrolabeRegistry.registerDMS( key, AstrolabeFactory.valueOf( getP3().getPhi() ) ) ;
			key = m.message( ApplicationConstant.LK_ATLASPAGE_P3DE ) ;
			AstrolabeRegistry.registerDMS( key, AstrolabeFactory.valueOf( getP3().getTheta() ) ) ;
			key = m.message( ApplicationConstant.LK_ATLASPAGE_ORA ) ;
			AstrolabeRegistry.registerHMS( key, AstrolabeFactory.valueOf( getOrigin().getPhi() ) ) ;
			key = m.message( ApplicationConstant.LK_ATLASPAGE_ODE ) ;
			AstrolabeRegistry.registerDMS( key, AstrolabeFactory.valueOf( getOrigin().getTheta() ) ) ;
			key = m.message( ApplicationConstant.LK_ATLASPAGE_TRA ) ;
			AstrolabeRegistry.registerDMS( key, AstrolabeFactory.valueOf( getTop().getPhi() ) ) ;
			key = m.message( ApplicationConstant.LK_ATLASPAGE_TDE ) ;
			Registry.registerNumber( key, AstrolabeFactory.valueOf( getTop().getTheta() ) ) ;
			key = m.message( ApplicationConstant.LK_ATLASPAGE_BRA ) ;
			AstrolabeRegistry.registerDMS( key, AstrolabeFactory.valueOf( getBottom().getPhi() ) ) ;
			key = m.message( ApplicationConstant.LK_ATLASPAGE_BDE ) ;
			Registry.registerNumber( key, AstrolabeFactory.valueOf( getBottom().getTheta() ) ) ;
		} catch ( ParameterNotValidException e ) {
			throw new RuntimeException( e.toString() ) ;
		}
	}
}
