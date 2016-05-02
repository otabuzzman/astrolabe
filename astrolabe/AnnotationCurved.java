
package astrolabe;

@SuppressWarnings("serial")
public class AnnotationCurved extends astrolabe.model.AnnotationCurved implements PostscriptEmitter {

	// attribute value (AV_)
	private final static String AV_TOPLEFT					= "topleft" ;
	private final static String AV_TOPMIDDLE				= "topmiddle" ;
	private final static String AV_TOPRIGHT					= "topright" ;
	private final static String AV_MIDDLELEFT				= "middleleft" ;
	private final static String AV_MIDDLE					= "middle" ;
	private final static String AV_MIDDLERIGHT				= "middleright" ;
	private final static String AV_BOTTOMLEFT				= "bottomleft" ;
	private final static String AV_BOTTOMMIDDLE				= "bottommiddle" ;
	private final static String AV_BOTTOMRIGHT				= "bottomright" ;

	// configuration key (CK_)
	private final static String CK_SUBSCRIPTSHIFT			= "subscriptshift" ;
	private final static String CK_SUPERSCRIPTSHIFT			= "superscriptshift" ;
	private final static String CK_SUBSCRIPTSHRINK			= "subscriptshrink" ;
	private final static String CK_SUPERSCRIPTSHRINK		= "superscriptshrink" ;

	private final static String CK_MARGIN					= "margin" ;
	private final static String CK_RISE						= "rise" ;

	private final static double DEFAULT_SUBSCRIPTSHRINK		= .8 ;
	private final static double DEFAULT_SUBSCRIPTSHIFT		= -.3 ;
	private final static double DEFAULT_SUPERSCRIPTSHRINK	= .8 ;
	private final static double DEFAULT_SUPERSCRIPTSHIFT	= .5 ;

	private final static double DEFAULT_MARGIN				= 1.2 ;
	private final static double DEFAULT_RISE				= 1.2 ;

	private double subscriptshrink ;
	private double subscriptshift ;
	private double superscriptshrink ;
	private double superscriptshift ;

	private double margin ;
	private double rise ;

	public void register() {
		Configuration conf ;

		conf = new Configuration( this ) ;

		subscriptshrink = conf.getValue( CK_SUBSCRIPTSHRINK, DEFAULT_SUBSCRIPTSHRINK ) ;
		subscriptshift = conf.getValue( CK_SUBSCRIPTSHIFT, DEFAULT_SUBSCRIPTSHIFT ) ;
		superscriptshrink = conf.getValue( CK_SUPERSCRIPTSHRINK, DEFAULT_SUPERSCRIPTSHRINK ) ;
		superscriptshift = conf.getValue( CK_SUPERSCRIPTSHIFT, DEFAULT_SUPERSCRIPTSHIFT ) ;

		margin = conf.getValue( CK_MARGIN, DEFAULT_MARGIN ) ;
		rise = conf.getValue( CK_RISE, DEFAULT_RISE ) ;
	}

	public void headPS( ApplicationPostscriptStream ps ) {
	}

	public void emitPS( ApplicationPostscriptStream ps ) {
		astrolabe.model.Script script ;
		int ns, n0 ;
		double p, height ;

		ps.operator.gsave() ;

		ps.array( true ) ;
		for ( ns=0, n0=0, height=0 ; ns<getScriptCount() ; ns++ ) {
			script = new astrolabe.model.Script() ;
			getScript( ns ).setupCompanion( script ) ;

			p = Configuration.getValue( script, script.getPurpose(), -1. ) ;
			if ( p<0 )
				p = Double.valueOf( script.getPurpose() ) ;

			if ( p==0 )
				n0++ ;
			else {
				AnnotationStraight.emitPS( ps, script, p, 0,
						subscriptshrink, subscriptshift, superscriptshrink, superscriptshift ) ;
				if ( height==0 )
					height = p ;
			}
		}
		ps.array( false ) ;

		if ( n0==ns ) {
			ps.operator.pop() ;
			ps.operator.grestore() ;

			return ;
		}

		ps.operator.currentpoint() ;
		ps.operator.translate() ;

		if ( new Boolean( getReverse() ).booleanValue() ) {
			ps.gpath() ;
			ps.grev() ;
			ps.operator.newpath() ;
			ps.gdraw() ;
		}

		if ( getAnchor().equals( AV_BOTTOMLEFT ) ) {
			ps.gpath() ;
			ps.push( rise ) ;
			ps.operator.neg() ;
			ps.gmove() ;
			ps.operator.dup() ;
			ps.operator.newpath() ;
			ps.gdraw() ;
			ps.glen() ;
			ps.operator.div( 100 ) ;
			ps.operator.mul( getDistance() ) ;
			ps.push( margin ) ;
			ps.operator.add() ;
		} else if ( getAnchor().equals( AV_BOTTOMMIDDLE ) ) {
			ps.gpath() ;
			ps.push( rise ) ;
			ps.operator.neg() ;
			ps.gmove() ;
			ps.operator.newpath() ;
			ps.gdraw() ;
			ps.operator.dup() ;
			ps.twidth() ;
			ps.operator.pop() ;
			ps.operator.div( 2 ) ;
			ps.gpath() ;
			ps.glen() ;
			ps.operator.div( 100 ) ;
			ps.operator.mul( getDistance() ) ;
			ps.operator.exch() ;
			ps.operator.sub() ;
		} else if ( getAnchor().equals( AV_BOTTOMRIGHT ) ) {
			ps.gpath() ;
			ps.push( rise ) ;
			ps.operator.neg() ;
			ps.gmove() ;
			ps.operator.newpath() ;
			ps.gdraw() ;
			ps.operator.dup() ;
			ps.twidth() ;
			ps.operator.pop() ;
			ps.operator.add( margin ) ;
			ps.gpath() ;
			ps.glen() ;
			ps.operator.div( 100 ) ;
			ps.operator.mul( getDistance() ) ;
			ps.operator.exch() ;
			ps.operator.sub() ;
		} else if ( getAnchor().equals( AV_MIDDLELEFT ) ) {
			ps.gpath() ;
			ps.push( height/2 ) ;
			ps.operator.neg() ;
			ps.gmove() ;
			ps.operator.dup() ;
			ps.operator.newpath() ;
			ps.gdraw() ;
			ps.glen() ;
			ps.operator.div( 100 ) ;
			ps.operator.mul( getDistance() ) ;
			ps.push( margin ) ;
			ps.operator.add() ;
		} else if ( getAnchor().equals( AV_MIDDLE ) ) {
			ps.gpath() ;
			ps.push( height/2 ) ;
			ps.operator.neg() ;
			ps.gmove() ;
			ps.operator.newpath() ;
			ps.gdraw() ;
			ps.operator.dup() ;
			ps.twidth() ;
			ps.operator.pop() ;
			ps.operator.div( 2 ) ;
			ps.gpath() ;
			ps.glen() ;
			ps.operator.div( 100 ) ;
			ps.operator.mul( getDistance() ) ;
			ps.operator.exch() ;
			ps.operator.sub() ;
		} else if ( getAnchor().equals( AV_MIDDLERIGHT ) ) {
			ps.gpath() ;
			ps.push( height/2 ) ;
			ps.operator.neg() ;
			ps.gmove() ;
			ps.operator.newpath() ;
			ps.gdraw() ;
			ps.operator.dup() ;
			ps.twidth() ;
			ps.operator.pop() ;
			ps.operator.add( margin ) ;
			ps.gpath() ;
			ps.glen() ;
			ps.operator.div( 100 ) ;
			ps.operator.mul( getDistance() ) ;
			ps.operator.exch() ;
			ps.operator.sub() ;
		} else if ( getAnchor().equals( AV_TOPLEFT ) ) {
			ps.gpath() ;
			ps.push( -( height+rise ) ) ;
			ps.operator.neg() ;
			ps.gmove() ;
			ps.operator.dup() ;
			ps.operator.newpath() ;
			ps.gdraw() ;
			ps.glen() ;
			ps.operator.div( 100 ) ;
			ps.operator.mul( getDistance() ) ;
			ps.push( margin ) ;
			ps.operator.add() ;
		} else if ( getAnchor().equals( AV_TOPMIDDLE ) ) {
			ps.gpath() ;
			ps.push( -( height+rise ) ) ;
			ps.operator.neg() ;
			ps.gmove() ;
			ps.operator.newpath() ;
			ps.gdraw() ;
			ps.operator.dup() ;
			ps.twidth() ;
			ps.operator.pop() ;
			ps.operator.div( 2 ) ;
			ps.gpath() ;
			ps.glen() ;
			ps.operator.div( 100 ) ;
			ps.operator.mul( getDistance() ) ;
			ps.operator.exch() ;
			ps.operator.sub() ;
		} else if ( getAnchor().equals( AV_TOPRIGHT ) ) {
			ps.gpath() ;
			ps.push( -( height+rise ) ) ;
			ps.operator.neg() ;
			ps.gmove() ;
			ps.operator.newpath() ;
			ps.gdraw() ;
			ps.operator.dup() ;
			ps.twidth() ;
			ps.operator.pop() ;
			ps.operator.add( margin ) ;
			ps.gpath() ;
			ps.glen() ;
			ps.operator.div( 100 ) ;
			ps.operator.mul( getDistance() ) ;
			ps.operator.exch() ;
			ps.operator.sub() ;
		}

		ps.tpath() ;

		ps.operator.grestore() ;
	}

	public void tailPS( ApplicationPostscriptStream ps ) {
	}
}
