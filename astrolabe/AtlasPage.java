
package astrolabe;

@SuppressWarnings("serial")
public class AtlasPage extends astrolabe.model.AtlasPage implements PostscriptEmitter {

	// castor requirement for (un)marshalling
	public AtlasPage() {
	}

	public void headPS( AstrolabePostscriptStream ps ) {
	}

	public void emitPS( AstrolabePostscriptStream ps ) {
		ps.array( true ) ;
		ps.push( getP0x() ) ;
		ps.push( getP0y() ) ;
		ps.push( getP1x() ) ;
		ps.push( getP1y() ) ;
		ps.push( getP2x() ) ;
		ps.push( getP2y() ) ;
		ps.push( getP3x() ) ;
		ps.push( getP3y() ) ;
		ps.array( false ) ;

		ps.operator.newpath() ;
		ps.push( ApplicationConstant.PS_PROLOG_GDRAW ) ;

		ps.operator.closepath() ;
		ps.operator.stroke() ;
	}

	public void tailPS( AstrolabePostscriptStream ps ) {
	}

	public void register() {
		String key ;
		MessageCatalog m ;

		m = new MessageCatalog( ApplicationConstant.GC_APPLICATION ) ;

		key = m.message( ApplicationConstant.LK_ATLASPAGE_NUM ) ;
		AstrolabeRegistry.registerNumber( key, getNum() ) ;
		key = m.message( ApplicationConstant.LK_ATLASPAGE_TCP ) ;
		AstrolabeRegistry.registerNumber( key, getTcp() ) ;
		key = m.message( ApplicationConstant.LK_ATLASPAGE_BCP ) ;
		AstrolabeRegistry.registerNumber( key, getBcp() ) ;
		key = m.message( ApplicationConstant.LK_ATLASPAGE_PCP ) ;
		AstrolabeRegistry.registerNumber( key, getPcp() ) ;
		key = m.message( ApplicationConstant.LK_ATLASPAGE_FCP ) ;
		AstrolabeRegistry.registerNumber( key, getFcp() ) ;

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
		key = m.message( ApplicationConstant.LK_ATLASPAGE_CRA ) ;
		AstrolabeRegistry.registerHMS( key, AstrolabeFactory.valueOf( getCenter().getPhi() ) ) ;
		key = m.message( ApplicationConstant.LK_ATLASPAGE_CDE ) ;
		AstrolabeRegistry.registerDMS( key, AstrolabeFactory.valueOf( getCenter().getTheta() ) ) ;
		key = m.message( ApplicationConstant.LK_ATLASPAGE_TRA ) ;
		AstrolabeRegistry.registerDMS( key, AstrolabeFactory.valueOf( getTop().getPhi() ) ) ;
		key = m.message( ApplicationConstant.LK_ATLASPAGE_TDE ) ;
		AstrolabeRegistry.registerNumber( key, AstrolabeFactory.valueOf( getTop().getTheta() ) ) ;
		key = m.message( ApplicationConstant.LK_ATLASPAGE_BRA ) ;
		AstrolabeRegistry.registerDMS( key, AstrolabeFactory.valueOf( getBottom().getPhi() ) ) ;
		key = m.message( ApplicationConstant.LK_ATLASPAGE_BDE ) ;
		AstrolabeRegistry.registerNumber( key, AstrolabeFactory.valueOf( getBottom().getTheta() ) ) ;
	}
}
