
package astrolabe;

import java.util.prefs.Preferences;

import astrolabe.UnicodePostscriptStream.UnicodeControlBlock;

@SuppressWarnings("serial")
public class AnnotationStraight extends astrolabe.model.AnnotationStraight implements PostscriptEmitter {

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

	public AnnotationStraight( Peer peer ) {
		Preferences node ;

		peer.setupCompanion( this ) ;

		node = Configuration.getClassNode( this, getName(), null ) ;

		subscriptshrink = Configuration.getValue( node, ApplicationConstant.PK_ANNOTATION_SUBSCRIPTSHRINK, DEFAULT_SUBSCRIPTSHRINK ) ;
		subscriptshift = Configuration.getValue( node, ApplicationConstant.PK_ANNOTATION_SUBSCRIPTSHIFT, DEFAULT_SUBSCRIPTSHIFT ) ;
		superscriptshrink = Configuration.getValue( node, ApplicationConstant.PK_ANNOTATION_SUPERSCRIPTSHRINK, DEFAULT_SUPERSCRIPTSHRINK ) ;
		superscriptshift = Configuration.getValue( node, ApplicationConstant.PK_ANNOTATION_SUPERSCRIPTSHIFT, DEFAULT_SUPERSCRIPTSHIFT ) ;

		margin = Configuration.getValue( node, ApplicationConstant.PK_ANNOTATION_MARGIN, DEFAULT_MARGIN ) ;
		rise = Configuration.getValue( node, ApplicationConstant.PK_ANNOTATION_RISE, DEFAULT_RISE ) ;
	}

	public void headPS( AstrolabePostscriptStream ps ) {
	}

	public void emitPS( AstrolabePostscriptStream ps ) {
		Layout layout ;
		Frame frame ;
		Script script ;
		int ns, n0 ;
		double p, height ;

		ps.operator.gsave() ;

		if ( getFrame() != null ) {
			layout = (Layout) AstrolabeRegistry.retrieve( ApplicationConstant.GC_LAYOUT ) ;
			frame = new Frame( getFrame(), layout ) ;

			frame.headPS( ps ) ;
			frame.emitPS( ps ) ;
			frame.tailPS( ps ) ;
		}

		ps.array( true ) ;
		for ( ns=0, n0=0, height=0 ; ns<getScriptCount() ; ns++ ) {
			script = new Script( getScript( ns ) ) ;

			p = Configuration.getValue(
					Configuration.getClassNode( script, script.getName(), null ), script.getPurpose(), -1. ) ;
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

		if ( getAnchor().equals( ApplicationConstant.AV_ANNOTATION_BOTTOMLEFT ) ) {
			ps.push( margin ) ;
			ps.push( rise ) ;
		} else if ( getAnchor().equals( ApplicationConstant.AV_ANNOTATION_BOTTOMMIDDLE ) ) {
			ps.operator.dup() ;
			ps.push( ApplicationConstant.PS_PROLOG_TWIDTH ) ;
			ps.operator.pop() ;
			ps.operator.div( -2 ) ;
			ps.push( rise ) ;
		} else if ( getAnchor().equals( ApplicationConstant.AV_ANNOTATION_BOTTOMRIGHT ) ) {
			ps.operator.dup() ;
			ps.push( ApplicationConstant.PS_PROLOG_TWIDTH ) ;
			ps.operator.pop() ;
			ps.operator.add( margin ) ;
			ps.operator.mul( -1 ) ;
			ps.push( rise ) ;
		} else if ( getAnchor().equals( ApplicationConstant.AV_ANNOTATION_MIDDLELEFT ) ) {
			ps.push( margin ) ;
			ps.push( -height/2 ) ;
		} else if ( getAnchor().equals( ApplicationConstant.AV_ANNOTATION_MIDDLE ) ) {
			ps.operator.dup() ;
			ps.push( ApplicationConstant.PS_PROLOG_TWIDTH ) ;
			ps.operator.pop() ;
			ps.operator.div( -2 ) ;
			ps.push( -height/2 ) ;
		} else if ( getAnchor().equals( ApplicationConstant.AV_ANNOTATION_MIDDLERIGHT ) ) {
			ps.operator.dup() ;
			ps.push( ApplicationConstant.PS_PROLOG_TWIDTH ) ;
			ps.operator.pop() ;
			ps.operator.add( margin ) ;
			ps.operator.mul( -1 ) ;
			ps.push( -height/2 ) ;
		} else if ( getAnchor().equals( ApplicationConstant.AV_ANNOTATION_TOPLEFT ) ) {
			ps.push( margin ) ;
			ps.push( -( height+rise ) ) ;
		} else if ( getAnchor().equals( ApplicationConstant.AV_ANNOTATION_TOPMIDDLE ) ) {
			ps.operator.dup() ;
			ps.push( ApplicationConstant.PS_PROLOG_TWIDTH ) ;
			ps.operator.pop() ;
			ps.operator.div( -2 ) ;
			ps.push( -( height+rise ) ) ;
		} else if ( getAnchor().equals( ApplicationConstant.AV_ANNOTATION_TOPRIGHT ) ) {
			ps.operator.dup() ;
			ps.push( ApplicationConstant.PS_PROLOG_TWIDTH ) ;
			ps.operator.pop() ;
			ps.operator.add( margin ) ;
			ps.operator.mul( -1 ) ;
			ps.push( -( height+rise ) ) ;
		}

		ps.operator.moveto() ;
		ps.push( ApplicationConstant.PS_PROLOG_TSHOW ) ;

		ps.operator.grestore() ;

	}

	public void tailPS( AstrolabePostscriptStream ps ) {
	}

	public static void emitPS( AstrolabePostscriptStream ps, astrolabe.model.TextType text, double height, double shift,
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
