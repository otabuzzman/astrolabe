
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

	public void headPS( ApplicationPostscriptStream ps ) {
	}

	public void emitPS( ApplicationPostscriptStream ps ) {
		Configuration conf ;
		double subscriptshrink, subscriptshift ;
		double superscriptshrink, superscriptshift ;
		double margin, rise ;
		astrolabe.model.Script script ;
		int ns, n0 ;
		double p, height ;

		conf = new Configuration( this ) ;

		subscriptshrink = conf.getValue( CK_SUBSCRIPTSHRINK, DEFAULT_SUBSCRIPTSHRINK );
		subscriptshift = conf.getValue( CK_SUBSCRIPTSHIFT, DEFAULT_SUBSCRIPTSHIFT ) ;
		superscriptshrink = conf.getValue( CK_SUPERSCRIPTSHRINK, DEFAULT_SUPERSCRIPTSHRINK ) ;
		superscriptshift = conf.getValue( CK_SUPERSCRIPTSHIFT, DEFAULT_SUPERSCRIPTSHIFT ) ;

		margin = conf.getValue( CK_MARGIN, DEFAULT_MARGIN ) ;
		rise = conf.getValue( CK_RISE, DEFAULT_RISE ) ;

		ps.op( "gsave" ) ;

		ps.array( true ) ;
		for ( ns=0, n0=0, height=0 ; ns<getScriptCount() ; ns++ ) {
			script = new astrolabe.model.Script() ;
			getScript( ns ).copyValues( script ) ;

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
			ps.op( "pop" ) ;
			ps.op( "grestore" ) ;

			return ;
		}

		ps.op( "currentpoint" ) ;
		ps.op( "translate" ) ;

		if ( new Boolean( getReverse() ).booleanValue() ) {
			ps.op( "gpath" ) ;
			ps.op( "grev" ) ;
			ps.op( "newpath" ) ;
			ps.op( "gdraw" ) ;
		}

		if ( getAnchor().equals( AV_BOTTOMLEFT ) ) {
			ps.op( "gpath" ) ;
			ps.push( rise ) ;
			ps.op( "neg" ) ;
			ps.op( "gmove" ) ;
			ps.op( "dup" ) ;
			ps.op( "newpath" ) ;
			ps.op( "gdraw" ) ;
			ps.op( "glen" ) ;
			ps.push( 100 ) ;
			ps.op( "div" ) ;
			ps.push( getDistance() ) ;
			ps.op( "mul" ) ;
			ps.push( margin ) ;
			ps.op( "add" ) ;
		} else if ( getAnchor().equals( AV_BOTTOMMIDDLE ) ) {
			ps.op( "gpath" ) ;
			ps.push( rise ) ;
			ps.op( "neg" ) ;
			ps.op( "gmove" ) ;
			ps.op( "newpath" ) ;
			ps.op( "gdraw" ) ;
			ps.op( "dup" ) ;
			ps.op( "twidth" ) ;
			ps.op( "pop" ) ;
			ps.push( 2 ) ;
			ps.op( "div" ) ;
			ps.op( "gpath") ;
			ps.op( "glen" ) ;
			ps.push( 100 ) ;
			ps.op( "div" ) ;
			ps.push( getDistance() ) ;
			ps.op( "mul" ) ;
			ps.op( "exch" ) ;
			ps.op( "sub" ) ;
		} else if ( getAnchor().equals( AV_BOTTOMRIGHT ) ) {
			ps.op( "gpath" ) ;
			ps.push( rise ) ;
			ps.op( "neg" ) ;
			ps.op( "gmove" ) ;
			ps.op( "newpath" ) ;
			ps.op( "gdraw" ) ;
			ps.op( "dup" ) ;
			ps.op( "twidth" ) ;
			ps.op( "pop" ) ;
			ps.push( margin ) ;
			ps.op( "add" ) ;
			ps.op( "gpath" ) ;
			ps.op( "glen" ) ;
			ps.push( 100 ) ;
			ps.op( "div" ) ;
			ps.push( getDistance() ) ;
			ps.op( "mul" ) ;
			ps.op( "exch" ) ;
			ps.op( "sub" ) ;
		} else if ( getAnchor().equals( AV_MIDDLELEFT ) ) {
			ps.op( "gpath" ) ;
			ps.push( height/2 ) ;
			ps.op( "neg" ) ;
			ps.op( "gmove" ) ;
			ps.op( "dup" ) ;
			ps.op( "newpath" ) ;
			ps.op( "gdraw" ) ;
			ps.op( "glen" ) ;
			ps.push( 100 ) ;
			ps.op( "div" ) ;
			ps.push( getDistance() ) ;
			ps.op( "mul" ) ;
			ps.push( margin ) ;
			ps.op( "add" ) ;
		} else if ( getAnchor().equals( AV_MIDDLE ) ) {
			ps.op( "gpath" ) ;
			ps.push( height/2 ) ;
			ps.op( "neg" ) ;
			ps.op( "gmove" ) ;
			ps.op( "newpath" ) ;
			ps.op( "gdraw" ) ;
			ps.op( "dup" ) ;
			ps.op( "twidth" ) ;
			ps.op( "pop" ) ;
			ps.push( 2 ) ;
			ps.op( "div" ) ;
			ps.op( "gpath" ) ;
			ps.op( "glen" ) ;
			ps.push( 100 ) ;
			ps.op( "div" ) ;
			ps.push( getDistance() ) ;
			ps.op( "mul" ) ;
			ps.op( "exch" ) ;
			ps.op( "sub" ) ;
		} else if ( getAnchor().equals( AV_MIDDLERIGHT ) ) {
			ps.op( "gpath" ) ;
			ps.push( height/2 ) ;
			ps.op( "neg" ) ;
			ps.op( "gmove" ) ;
			ps.op( "newpath" ) ;
			ps.op( "gdraw" ) ;
			ps.op( "dup" ) ;
			ps.op( "twidth" ) ;
			ps.op( "dup" ) ;
			ps.push( margin ) ;
			ps.op( "add" ) ;
			ps.op( "gpath" ) ;
			ps.op( "glen" ) ;
			ps.push( 100 ) ;
			ps.op( "div" ) ;
			ps.push( getDistance() ) ;
			ps.op( "mul" ) ;
			ps.op( "exch" ) ;
			ps.op( "sub" ) ;
		} else if ( getAnchor().equals( AV_TOPLEFT ) ) {
			ps.op( "gpath" ) ;
			ps.push( -( height+rise ) ) ;
			ps.op( "neg" ) ;
			ps.op( "gmove" ) ;
			ps.op( "dup" ) ;
			ps.op( "newpath" ) ;
			ps.op( "gdraw" ) ;
			ps.op( "glen" ) ;
			ps.push( 100 ) ;
			ps.op( "div" ) ;
			ps.push( getDistance() ) ;
			ps.op( "mul" ) ;
			ps.push( margin ) ;
			ps.op( "add" ) ;
		} else if ( getAnchor().equals( AV_TOPMIDDLE ) ) {
			ps.op( "gpath" ) ;
			ps.push( -( height+rise ) ) ;
			ps.op( "neg" ) ;
			ps.op( "gmove" ) ;
			ps.op( "newpath" ) ;
			ps.op( "gdraw" ) ;
			ps.op( "dup" ) ;
			ps.op( "twidth" ) ;
			ps.op( "pop" ) ;
			ps.push( 2 ) ;
			ps.op( "div" ) ;
			ps.op( "gpath" ) ;
			ps.op( "glen" ) ;
			ps.push( 100 ) ;
			ps.op( "div" ) ;
			ps.push( getDistance() ) ;
			ps.op( "mul" ) ;
			ps.op( "exch" ) ;
			ps.op( "sub" ) ;
		} else if ( getAnchor().equals( AV_TOPRIGHT ) ) {
			ps.op( "gpath" ) ;
			ps.push( -( height+rise ) ) ;
			ps.op( "neg" ) ;
			ps.op( "gmove") ;
			ps.op( "newpath" ) ;
			ps.op( "gdraw" ) ;
			ps.op( "dup" ) ;
			ps.op( "twidth" ) ;
			ps.op( "pop" ) ;
			ps.push( margin ) ;
			ps.op( "add" ) ;
			ps.op( "gpath" ) ;
			ps.op( "glen" ) ;
			ps.push( 100 ) ;
			ps.op( "div" ) ;
			ps.push( getDistance() ) ;
			ps.op( "mul" ) ;
			ps.op( "exch" ) ;
			ps.op( "sub" ) ;
		}

		ps.op( "tpath" ) ;

		ps.op( "grestore" ) ;
	}

	public void tailPS( ApplicationPostscriptStream ps ) {
	}
}
