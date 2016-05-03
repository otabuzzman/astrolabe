
package astrolabe;

import astrolabe.UnicodePostscriptStream.UnicodeControlBlock;

@SuppressWarnings("serial")
public class AnnotationStraight extends astrolabe.model.AnnotationStraight implements PostscriptEmitter {

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

	private final static String DEFAULT_ANCHOR				= "0:0" ; // bottomleft

	public void headPS( ApplicationPostscriptStream ps ) {
	}

	public void emitPS( ApplicationPostscriptStream ps ) {
		Configuration conf ;
		double subscriptshrink, subscriptshift ;
		double superscriptshrink, superscriptshift ;
		double margin, rise ;
		astrolabe.model.Script script ;
		ChartPage page ;
		int num, ns, n0 ;
		double p, height ;
		String[] xyRaw ;
		double[] frame, xyVal ;

		conf = new Configuration( this ) ;

		subscriptshrink = conf.getValue( CK_SUBSCRIPTSHRINK, DEFAULT_SUBSCRIPTSHRINK );
		subscriptshift = conf.getValue( CK_SUBSCRIPTSHIFT, DEFAULT_SUBSCRIPTSHIFT ) ;
		superscriptshrink = conf.getValue( CK_SUPERSCRIPTSHRINK, DEFAULT_SUPERSCRIPTSHRINK ) ;
		superscriptshift = conf.getValue( CK_SUPERSCRIPTSHIFT, DEFAULT_SUPERSCRIPTSHIFT ) ;

		margin = conf.getValue( CK_MARGIN, DEFAULT_MARGIN ) ;
		rise = conf.getValue( CK_RISE, DEFAULT_RISE ) ;

		ps.op( "gsave" ) ;

		if ( getFrame() != null ) {
			page = (ChartPage) Registry.retrieve( ChartPage.class.getName() ) ;
			if ( page != null ) {
				num = Integer.parseInt( getFrame().getNumber() ) ;
				frame = page.getFrameDef( num ) ;

				xyRaw = Configuration.getValue( this, getFrame().getAnchor(), DEFAULT_ANCHOR )
				.split( ":" ) ;

				xyVal = new double[2] ;
				xyVal[0] = frame[0]+frame[2]*new Double( xyRaw[0] ).doubleValue() ;
				xyVal[1] = frame[1]+frame[3]*new Double( xyRaw[1] ).doubleValue() ;

				ps.push( xyVal[0] ) ;
				ps.push( xyVal[1] ) ;
				ps.op( "moveto" ) ;
			}
		}

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

		if ( getRadiant() ) {
			ps.push( 90 ) ;
			ps.op( "rotate" ) ;
		}

		if ( new Boolean( getReverse() ).booleanValue() ) {
			ps.push( 180 ) ;
			ps.op( "rotate" ) ;
		}

		if ( getAnchor().equals( AV_BOTTOMLEFT ) ) {
			ps.push( margin ) ;
			ps.push( rise ) ;
		} else if ( getAnchor().equals( AV_BOTTOMMIDDLE ) ) {
			ps.op( "dup" ) ;
			ps.op( "twidth" ) ;
			ps.op( "pop" ) ;
			ps.push( -2 ) ;
			ps.op( "div" ) ;
			ps.push( rise ) ;
		} else if ( getAnchor().equals( AV_BOTTOMRIGHT ) ) {
			ps.op( "dup" ) ;
			ps.op( "twidth" ) ;
			ps.op( "pop" ) ;
			ps.push( margin ) ;
			ps.op( "add" ) ;
			ps.push( -1 ) ;
			ps.op( "mul" ) ;
			ps.push( rise ) ;
		} else if ( getAnchor().equals( AV_MIDDLELEFT ) ) {
			ps.push( margin ) ;
			ps.push( -height/2 ) ;
		} else if ( getAnchor().equals( AV_MIDDLE ) ) {
			ps.op( "dup" ) ;
			ps.op( "twidth" ) ;
			ps.op( "pop" ) ;
			ps.push( -2 ) ;
			ps.op( "div" ) ;
			ps.push( -height/2 ) ;
		} else if ( getAnchor().equals( AV_MIDDLERIGHT ) ) {
			ps.op( "dup" ) ;
			ps.op( "twidth" ) ;
			ps.op( "pop" ) ;
			ps.push( margin ) ;
			ps.op( "add" ) ;
			ps.push( -1 ) ;
			ps.op( "mul" ) ;
			ps.push( -height/2 ) ;
		} else if ( getAnchor().equals( AV_TOPLEFT ) ) {
			ps.push( margin ) ;
			ps.push( -( height+rise ) ) ;
		} else if ( getAnchor().equals( AV_TOPMIDDLE ) ) {
			ps.op( "dup" ) ;
			ps.op( "twidth" ) ;
			ps.op( "pop" ) ;
			ps.push( -2 ) ;
			ps.op( "div" ) ;
			ps.push( -( height+rise ) ) ;
		} else if ( getAnchor().equals( AV_TOPRIGHT ) ) {
			ps.op( "dup" ) ;
			ps.op( "twidth" ) ;
			ps.op( "pop" ) ;
			ps.push( margin ) ;
			ps.op( "add" ) ;
			ps.push( -1 ) ;
			ps.op( "mul" ) ;
			ps.push( -( height+rise ) ) ;
		}

		ps.op( "moveto" ) ;
		ps.op( "tshow" ) ;

		ps.op( "grestore" ) ;
	}

	public void tailPS( ApplicationPostscriptStream ps ) {
	}

	public static void emitPS( ApplicationPostscriptStream ps, astrolabe.model.TextType text, double height, double shift,
			double subscriptshrink, double subscriptshift, double superscriptshrink, double superscriptshift ) {
		if ( text.getValue().length()>0 )
			for ( UnicodeControlBlock unicodeControlBlock : ps.getUnicodeControlBlockArray( text.getValue() ) ) {
				ps.array( true ) ;
				ps.script( unicodeControlBlock.fontname ) ;
				ps.array( true ) ;
				for ( String e : unicodeControlBlock.encoding )
					ps.script( e ) ;
				ps.array( false ) ;
				ps.push( height ) ;
				ps.push( shift ) ;
				ps.push( true ) ;
				ps.push( true ) ;
				ps.script( "("+unicodeControlBlock.string+")" ) ;
				ps.array( false ) ;
			}

		for ( int d=0 ; d<text.getSubscriptCount() ; d++ ) {
			emitPS( ps, text.getSubscript( d ),
					height*subscriptshrink, shift+height*subscriptshift,
					subscriptshrink, subscriptshift, superscriptshrink, superscriptshift ) ;
		}

		for ( int u=0 ; u<text.getSuperscriptCount() ; u++ ) {
			emitPS( ps, text.getSuperscript( u ),
					height*superscriptshrink, shift+height*superscriptshift,
					subscriptshrink, subscriptshift, superscriptshrink, superscriptshift ) ;
		}
	}
}
