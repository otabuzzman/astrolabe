
package astrolabe;

import java.util.prefs.Preferences;

@SuppressWarnings("serial")
public class AnnotationCurved extends astrolabe.model.AnnotationCurved implements PostscriptEmitter {

	private final static double DEFAULT_SUBSCRIPTSHRINK = .8 ;
	private final static double DEFAULT_SUBSCRIPTSHIFT = -.3 ;
	private final static double DEFAULT_SUPERSCRIPTSHRINK = .8 ;
	private final static double DEFAULT_SUPERSCRIPTSHIFT = .5 ;

	private final static double DEFAULT_MARGIN = 1.2 ;
	private final static double DEFAULT_RISE = 1.2 ;

	private double subscriptshrink ;
	private double subscriptshift ;
	private double superscriptshrink ;
	private double superscriptshift ;

	private double margin ;
	private double rise ;

	private double distance ;

	public AnnotationCurved( Peer peer ) {
		Preferences node ;

		peer.setupCompanion( this ) ;

		node = Configuration.getClassNode( this, getName(), null ) ;

		subscriptshrink = Configuration.getValue( node, ApplicationConstant.PK_ANNOTATION_SUBSCRIPTSHRINK, DEFAULT_SUBSCRIPTSHRINK ) ;
		subscriptshift = Configuration.getValue( node, ApplicationConstant.PK_ANNOTATION_SUBSCRIPTSHIFT, DEFAULT_SUBSCRIPTSHIFT ) ;
		superscriptshrink = Configuration.getValue( node, ApplicationConstant.PK_ANNOTATION_SUPERSCRIPTSHRINK, DEFAULT_SUPERSCRIPTSHRINK ) ;
		superscriptshift = Configuration.getValue( node, ApplicationConstant.PK_ANNOTATION_SUPERSCRIPTSHIFT, DEFAULT_SUPERSCRIPTSHIFT ) ;

		margin = Configuration.getValue( node, ApplicationConstant.PK_ANNOTATION_MARGIN, DEFAULT_MARGIN ) ;
		rise = Configuration.getValue( node, ApplicationConstant.PK_ANNOTATION_RISE, DEFAULT_RISE ) ;

		distance = getDistance() ;
	}

	public void headPS( AstrolabePostscriptStream ps ) {
	}

	public void emitPS( AstrolabePostscriptStream ps ) {
		int ns, ne ;
		double size ;
		Script script ;

		size = new Script( getScript( 0 ) ).size() ;

		if ( ! ( size>0 ) )
			return ;

		ps.operator.gsave() ;

		ps.array( true ) ;
		for ( ns=0, ne=0 ; ns<getScriptCount() ; ns++ ) {
			try {
				script = new Script( getScript( ns ) ) ;

				AnnotationStraight.emitPS( ps, script, script.size(), 0,
						subscriptshrink, subscriptshift, superscriptshrink, superscriptshift ) ;
			} catch ( ParameterNotValidException e ) {
				ne++ ;
			}
		}
		ps.array( false ) ;
		if ( ne==ns ) {
			ps.operator.pop() ; // array
			ps.operator.grestore() ;

			return ;
		}

		ps.operator.currentpoint() ;
		ps.operator.translate() ;

		if ( new Boolean( getReverse() ).booleanValue() ) {
			ps.custom( ApplicationConstant.PS_CUSTOM_PATHREVERSE ) ;
		}

		if ( getAnchor().equals( ApplicationConstant.AV_ANNOTATION_BOTTOMLEFT ) ) {
			ps.push( rise ) ;
			ps.push( true ) ;
			ps.custom( ApplicationConstant.PS_CUSTOM_PATHSHIFT ) ;
			ps.custom( ApplicationConstant.PS_CUSTOM_PATHLENGTH ) ;
			ps.operator.div( 100 ) ;
			ps.operator.mul( distance ) ;
			ps.push( margin ) ;
			ps.operator.add() ;
		} else if ( getAnchor().equals( ApplicationConstant.AV_ANNOTATION_BOTTOMMIDDLE ) ) {
			ps.push( rise ) ;
			ps.push( true ) ;
			ps.custom( ApplicationConstant.PS_CUSTOM_PATHSHIFT ) ;
			ps.operator.dup() ;
			ps.operator.stringwidth() ;
			ps.operator.pop() ;
			ps.operator.div( 2 ) ;
			ps.custom( ApplicationConstant.PS_CUSTOM_PATHLENGTH ) ;
			ps.operator.div( 100 ) ;
			ps.operator.mul( distance ) ;
			ps.operator.exch() ;
			ps.operator.sub() ;
		} else if ( getAnchor().equals( ApplicationConstant.AV_ANNOTATION_BOTTOMRIGHT ) ) {
			ps.push( rise ) ;
			ps.push( true ) ;
			ps.custom( ApplicationConstant.PS_CUSTOM_PATHSHIFT ) ;
			ps.operator.dup() ;
			ps.operator.stringwidth() ;
			ps.operator.pop() ;
			ps.operator.add( margin ) ;
			ps.custom( ApplicationConstant.PS_CUSTOM_PATHLENGTH ) ;
			ps.operator.div( 100 ) ;
			ps.operator.mul( distance ) ;
			ps.operator.exch() ;
			ps.operator.sub() ;
		} else if ( getAnchor().equals( ApplicationConstant.AV_ANNOTATION_MIDDLELEFT ) ) {
			ps.push( size/2 ) ;
			ps.push( true ) ;
			ps.custom( ApplicationConstant.PS_CUSTOM_PATHSHIFT ) ;
			ps.custom( ApplicationConstant.PS_CUSTOM_PATHLENGTH ) ;
			ps.operator.div( 100 ) ;
			ps.operator.mul( distance ) ;
			ps.push( margin ) ;
			ps.operator.add() ;
		} else if ( getAnchor().equals( ApplicationConstant.AV_ANNOTATION_MIDDLE ) ) {
			ps.push( size/2 ) ;
			ps.push( true ) ;
			ps.custom( ApplicationConstant.PS_CUSTOM_PATHSHIFT ) ;
			ps.operator.dup() ;
			ps.operator.stringwidth() ;
			ps.operator.pop() ;
			ps.operator.div( 2 ) ;
			ps.custom( ApplicationConstant.PS_CUSTOM_PATHLENGTH ) ;
			ps.operator.div( 100 ) ;
			ps.operator.mul( distance ) ;
			ps.operator.exch() ;
			ps.operator.sub() ;
		} else if ( getAnchor().equals( ApplicationConstant.AV_ANNOTATION_MIDDLERIGHT ) ) {
			ps.push( size/2 ) ;
			ps.push( true ) ;
			ps.custom( ApplicationConstant.PS_CUSTOM_PATHSHIFT ) ;
			ps.operator.dup() ;
			ps.operator.stringwidth() ;
			ps.operator.pop() ;
			ps.operator.add( margin ) ;
			ps.custom( ApplicationConstant.PS_CUSTOM_PATHLENGTH ) ;
			ps.operator.div( 100 ) ;
			ps.operator.mul( distance ) ;
			ps.operator.exch() ;
			ps.operator.sub() ;
		} else if ( getAnchor().equals( ApplicationConstant.AV_ANNOTATION_TOPLEFT ) ) {
			ps.push( -( size+rise ) ) ;
			ps.push( true ) ;
			ps.custom( ApplicationConstant.PS_CUSTOM_PATHSHIFT ) ;
			ps.custom( ApplicationConstant.PS_CUSTOM_PATHLENGTH ) ;
			ps.operator.div( 100 ) ;
			ps.operator.mul( distance ) ;
			ps.push( margin ) ;
			ps.operator.add() ;
		} else if ( getAnchor().equals( ApplicationConstant.AV_ANNOTATION_TOPMIDDLE ) ) {
			ps.push( -( size+rise ) ) ;
			ps.push( true ) ;
			ps.custom( ApplicationConstant.PS_CUSTOM_PATHSHIFT ) ;
			ps.operator.dup() ;
			ps.operator.stringwidth() ;
			ps.operator.pop() ;
			ps.operator.div( 2 ) ;
			ps.custom( ApplicationConstant.PS_CUSTOM_PATHLENGTH ) ;
			ps.operator.div( 100 ) ;
			ps.operator.mul( distance ) ;
			ps.operator.exch() ;
			ps.operator.sub() ;
		} else if ( getAnchor().equals( ApplicationConstant.AV_ANNOTATION_TOPRIGHT ) ) {
			ps.push( -( size+rise ) ) ;
			ps.push( true ) ;
			ps.custom( ApplicationConstant.PS_CUSTOM_PATHSHIFT ) ;
			ps.operator.dup() ;
			ps.operator.stringwidth() ;
			ps.operator.pop() ;
			ps.operator.add( margin ) ;
			ps.custom( ApplicationConstant.PS_CUSTOM_PATHLENGTH ) ;
			ps.operator.div( 100 ) ;
			ps.operator.mul( distance ) ;
			ps.operator.exch() ;
			ps.operator.sub() ;
		}

		ps.custom( ApplicationConstant.PS_CUSTOM_PATHSHOW ) ;

		ps.operator.grestore() ;
	}

	public void tailPS( AstrolabePostscriptStream ps ) {
	}
}
