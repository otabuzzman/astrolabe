
package astrolabe;

import java.util.List;
import java.util.prefs.Preferences;

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
		int nt, ne ;
		double size ;
		Text text ;

		size = new Text( getText( 0 ) ).size() ;

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
		for ( nt=0, ne=0 ; nt<getTextCount() ; nt++ ) {
			try {
				text = new Text( getText( nt ) ) ;

				emitPS( ps, text, text.size(), 0,
						subscriptshrink, subscriptshift, superscriptshrink, superscriptshift ) ;
			} catch ( ParameterNotValidException e ) {
				ne++ ;
			}
		}
		ps.array( false ) ;
		if ( ne==nt ) {
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
			ps.operator.stringwidth() ;
			ps.operator.pop() ;
			ps.operator.div( -2 ) ;
			ps.push( rise ) ;
		} else if ( getAnchor().equals( ApplicationConstant.AV_ANNOTATION_BOTTOMRIGHT ) ) {
			ps.operator.dup() ;
			ps.operator.stringwidth() ;
			ps.operator.pop() ;
			ps.operator.add( margin ) ;
			ps.operator.mul( -1 ) ;
			ps.push( rise ) ;
		} else if ( getAnchor().equals( ApplicationConstant.AV_ANNOTATION_MIDDLELEFT ) ) {
			ps.push( margin ) ;
			ps.push( -size/2 ) ;
		} else if ( getAnchor().equals( ApplicationConstant.AV_ANNOTATION_MIDDLE ) ) {
			ps.operator.dup() ;
			ps.operator.stringwidth() ;
			ps.operator.pop() ;
			ps.operator.div( -2 ) ;
			ps.push( -size/2 ) ;
		} else if ( getAnchor().equals( ApplicationConstant.AV_ANNOTATION_MIDDLERIGHT ) ) {
			ps.operator.dup() ;
			ps.operator.stringwidth() ;
			ps.operator.pop() ;
			ps.operator.add( margin ) ;
			ps.operator.mul( -1 ) ;
			ps.push( -size/2 ) ;
		} else if ( getAnchor().equals( ApplicationConstant.AV_ANNOTATION_TOPLEFT ) ) {
			ps.push( margin ) ;
			ps.push( -( size+rise ) ) ;
		} else if ( getAnchor().equals( ApplicationConstant.AV_ANNOTATION_TOPMIDDLE ) ) {
			ps.operator.dup() ;
			ps.operator.stringwidth() ;
			ps.operator.pop() ;
			ps.operator.div( -2 ) ;
			ps.push( -( size+rise ) ) ;
		} else if ( getAnchor().equals( ApplicationConstant.AV_ANNOTATION_TOPRIGHT ) ) {
			ps.operator.dup() ;
			ps.operator.stringwidth() ;
			ps.operator.pop() ;
			ps.operator.add( margin ) ;
			ps.operator.mul( -1 ) ;
			ps.push( -( size+rise ) ) ;
		}

		ps.operator.moveto() ;
		ps.operator.show() ;

		ps.operator.grestore() ;
	}

	public void tailPS( AstrolabePostscriptStream ps ) {
	}

	public static void emitPS( AstrolabePostscriptStream ps, Text text, double size, double shift,
			double subscriptshrink, double subscriptshift, double superscriptshrink, double superscriptshift ) throws ParameterNotValidException {
		List<List<Object>> FET ;
		List<Object> fet ;
		String t ;

		t = text.getValue() ;
		if ( t.length()==0 ) {
			throw new ParameterNotValidException( Integer.toString( t.length() ) ) ;
		}

		FET = ps.ucFET( t ) ;

		for ( int e=0 ; e<FET.size() ; e++ ) {
			fet = FET.get( e ) ;
			ps.array( true ) ;
			ps.push( (String) fet.get( 0 ) ) ;
			ps.push( (String[]) fet.get( 1 ) ) ;
			ps.push( size ) ;
			ps.push( shift ) ;
			ps.push( true ) ;
			ps.push( true ) ;
			ps.push( (String) fet.get( 2 ) ) ;
			ps.array( false ) ;
		}

		for ( int d=0 ; d<text.getSubscriptCount() ; d++ ) {
			emitPS( ps, new Text( text.getSubscript( d ) ),
					size*subscriptshrink, shift+size*subscriptshift,
					subscriptshrink, subscriptshift, superscriptshrink, superscriptshift ) ;
		}

		for ( int u=0 ; u<text.getSuperscriptCount() ; u++ ) {
			emitPS( ps, new Text( text.getSuperscript( u ) ),
					size*superscriptshrink, shift+size*superscriptshift,
					subscriptshrink, subscriptshift, superscriptshrink, superscriptshift ) ;
		}
	}
}
