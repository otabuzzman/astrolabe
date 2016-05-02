
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
		int ns, ne ;
		double size ;
		Script script ;

		size = new Script( getScript( 0 ) ).size() ;

		if ( ! ( size>0 ) )
			return ;

		ps.operator.gsave() ;

		if ( getFrame() != null ) {
			layout = (Layout) AstrolabeRegistry.retrieve( ApplicationConstant.GC_LAYOUT ) ;
			frame = new Frame( getFrame(), layout ) ;

			frame.headPS( ps ) ;
			frame.emitPS( ps ) ;
			frame.tailPS( ps ) ;
		}

		ps.array( true ) ;
		for ( ns=0, ne=0 ; ns<getScriptCount() ; ns++ ) {
			try {
				script = new Script( getScript( ns ) ) ;

				emitPS( ps, script, script.size(), 0,
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
			ps.push( -size/2 ) ;
		} else if ( getAnchor().equals( ApplicationConstant.AV_ANNOTATION_MIDDLE ) ) {
			ps.operator.dup() ;
			ps.push( ApplicationConstant.PS_PROLOG_TWIDTH ) ;
			ps.operator.pop() ;
			ps.operator.div( -2 ) ;
			ps.push( -size/2 ) ;
		} else if ( getAnchor().equals( ApplicationConstant.AV_ANNOTATION_MIDDLERIGHT ) ) {
			ps.operator.dup() ;
			ps.push( ApplicationConstant.PS_PROLOG_TWIDTH ) ;
			ps.operator.pop() ;
			ps.operator.add( margin ) ;
			ps.operator.mul( -1 ) ;
			ps.push( -size/2 ) ;
		} else if ( getAnchor().equals( ApplicationConstant.AV_ANNOTATION_TOPLEFT ) ) {
			ps.push( margin ) ;
			ps.push( -( size+rise ) ) ;
		} else if ( getAnchor().equals( ApplicationConstant.AV_ANNOTATION_TOPMIDDLE ) ) {
			ps.operator.dup() ;
			ps.push( ApplicationConstant.PS_PROLOG_TWIDTH ) ;
			ps.operator.pop() ;
			ps.operator.div( -2 ) ;
			ps.push( -( size+rise ) ) ;
		} else if ( getAnchor().equals( ApplicationConstant.AV_ANNOTATION_TOPRIGHT ) ) {
			ps.operator.dup() ;
			ps.push( ApplicationConstant.PS_PROLOG_TWIDTH ) ;
			ps.operator.pop() ;
			ps.operator.add( margin ) ;
			ps.operator.mul( -1 ) ;
			ps.push( -( size+rise ) ) ;
		}

		ps.operator.moveto() ;
		ps.push( ApplicationConstant.PS_PROLOG_TSHOW ) ;

		ps.operator.grestore() ;

	}

	public void tailPS( AstrolabePostscriptStream ps ) {
	}

	public static void emitPS( AstrolabePostscriptStream ps, astrolabe.model.Script script, double size, double shift,
			double subscriptshrink, double subscriptshift, double superscriptshrink, double superscriptshift ) throws ParameterNotValidException {
		String s ;

		s = script.getValue() ;
		if ( s.length()==0 ) {
			throw new ParameterNotValidException( Integer.toString( s.length() ) ) ;
		}
		for ( UnicodeControlBlock unicodeControlBlock : ps.getUnicodeControlBlockArray( s ) ) {
			ps.array( true ) ;
			ps.push( unicodeControlBlock.fontname ) ;
			ps.push( unicodeControlBlock.encoding ) ;
			ps.push( size ) ;
			ps.push( shift ) ;
			ps.push( true ) ;
			ps.push( true ) ;
			ps.push( "("+unicodeControlBlock.string+")" ) ;
			ps.array( false ) ;
		}

		for ( int d=0 ; d<script.getSubscriptCount() ; d++ ) {
			emitPS( ps, new Script( script.getSubscript( d ) ),
					size*subscriptshrink, shift+size*subscriptshift,
					subscriptshrink, subscriptshift, superscriptshrink, superscriptshift ) ;
		}

		for ( int u=0 ; u<script.getSuperscriptCount() ; u++ ) {
			emitPS( ps, new Script( script.getSuperscript( u ) ),
					size*superscriptshrink, shift+size*superscriptshift,
					subscriptshrink, subscriptshift, superscriptshrink, superscriptshift ) ;
		}
	}
}
