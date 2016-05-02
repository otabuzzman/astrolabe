
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
		Layout layout ;
		Frame frame ;
		int number, ns, n0 ;
		double p, height ;

		ps.operator.gsave() ;

		if ( getFrame() != null ) {
			layout = (Layout) Registry.retrieve( ApplicationConstant.GC_LAYOUT ) ;
			number = Integer.parseInt( getFrame().getNumber() ) ;

			frame = new Frame( layout.frame( number ) ) ;
			getFrame().setupCompanion( frame ) ;

			frame.headPS( ps ) ;
			frame.emitPS( ps ) ;
			frame.tailPS( ps ) ;
		}

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

		if ( getRadiant() ) {
			ps.operator.rotate( 90 ) ;
		}

		if ( new Boolean( getReverse() ).booleanValue() ) {
			ps.operator.rotate( 180 ) ;
		}

		if ( getAnchor().equals( AV_BOTTOMLEFT ) ) {
			ps.push( margin ) ;
			ps.push( rise ) ;
		} else if ( getAnchor().equals( AV_BOTTOMMIDDLE ) ) {
			ps.operator.dup() ;
			ps.twidth() ;
			ps.operator.pop() ;
			ps.operator.div( -2 ) ;
			ps.push( rise ) ;
		} else if ( getAnchor().equals( AV_BOTTOMRIGHT ) ) {
			ps.operator.dup() ;
			ps.twidth() ;
			ps.operator.pop() ;
			ps.operator.add( margin ) ;
			ps.operator.mul( -1 ) ;
			ps.push( rise ) ;
		} else if ( getAnchor().equals( AV_MIDDLELEFT ) ) {
			ps.push( margin ) ;
			ps.push( -height/2 ) ;
		} else if ( getAnchor().equals( AV_MIDDLE ) ) {
			ps.operator.dup() ;
			ps.twidth() ;
			ps.operator.pop() ;
			ps.operator.div( -2 ) ;
			ps.push( -height/2 ) ;
		} else if ( getAnchor().equals( AV_MIDDLERIGHT ) ) {
			ps.operator.dup() ;
			ps.twidth() ;
			ps.operator.pop() ;
			ps.operator.add( margin ) ;
			ps.operator.mul( -1 ) ;
			ps.push( -height/2 ) ;
		} else if ( getAnchor().equals( AV_TOPLEFT ) ) {
			ps.push( margin ) ;
			ps.push( -( height+rise ) ) ;
		} else if ( getAnchor().equals( AV_TOPMIDDLE ) ) {
			ps.operator.dup() ;
			ps.twidth() ;
			ps.operator.pop() ;
			ps.operator.div( -2 ) ;
			ps.push( -( height+rise ) ) ;
		} else if ( getAnchor().equals( AV_TOPRIGHT ) ) {
			ps.operator.dup() ;
			ps.twidth() ;
			ps.operator.pop() ;
			ps.operator.add( margin ) ;
			ps.operator.mul( -1 ) ;
			ps.push( -( height+rise ) ) ;
		}

		ps.operator.moveto() ;
		ps.tshow() ;

		ps.operator.grestore() ;

	}

	public void tailPS( ApplicationPostscriptStream ps ) {
	}

	public static void emitPS( ApplicationPostscriptStream ps, astrolabe.model.TextType text, double height, double shift,
			double subscriptshrink, double subscriptshift, double superscriptshrink, double superscriptshift ) {
		if ( text.getValue().length()>0 )
			for ( UnicodeControlBlock unicodeControlBlock : ps.getUnicodeControlBlockArray( text.getValue() ) ) {
				ps.array( true ) ;
				ps.push( unicodeControlBlock.fontname ) ;
				ps.push( unicodeControlBlock.encoding ) ;
				ps.push( height ) ;
				ps.push( shift ) ;
				ps.push( true ) ;
				ps.push( true ) ;
				ps.push( "("+unicodeControlBlock.string+")" ) ;
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
