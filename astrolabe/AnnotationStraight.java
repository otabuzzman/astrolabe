
package astrolabe;

import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SuppressWarnings("serial")
public class AnnotationStraight extends astrolabe.model.AnnotationStraight implements Annotation {

	private final static Log log = LogFactory.getLog( AnnotationStraight.class ) ;

	private final static double DEFAULT_SUBSCRIPTSHRINK = .8 ;
	private final static double DEFAULT_SUBSCRIPTSHIFT = -.3 ;
	private final static double DEFAULT_SUPERSCRIPTSHRINK = .8 ;
	private final static double DEFAULT_SUPERSCRIPTSHIFT = .5 ;

	private final static double DEFAULT_MARGIN = 1.2 ;
	private final static double DEFAULT_RISE = 1.2 ;

	private final static double DEFAULT_PURPOSE = 3.8 ;

	private double subscriptshrink ;
	private double subscriptshift ;
	private double superscriptshrink ;
	private double superscriptshift ;

	private double margin ;
	private double rise ;

	private double size ;

	public AnnotationStraight( Object peer ) {
		ApplicationHelper.setupCompanionFromPeer( this, peer ) ;


		subscriptshrink = ApplicationHelper.getClassNode( this,
				getName(), null ).getDouble( ApplicationConstant.PK_ANNOTATION_SUBSCRIPTSHRINK, DEFAULT_SUBSCRIPTSHRINK ) ;
		subscriptshift = ApplicationHelper.getClassNode( this,
				getName(), null ).getDouble( ApplicationConstant.PK_ANNOTATION_SUBSCRIPTSHIFT, DEFAULT_SUBSCRIPTSHIFT ) ;
		superscriptshrink = ApplicationHelper.getClassNode( this,
				getName(), null ).getDouble( ApplicationConstant.PK_ANNOTATION_SUPERSCRIPTSHRINK, DEFAULT_SUPERSCRIPTSHRINK ) ;
		superscriptshift = ApplicationHelper.getClassNode( this,
				getName(), null ).getDouble( ApplicationConstant.PK_ANNOTATION_SUPERSCRIPTSHIFT, DEFAULT_SUPERSCRIPTSHIFT ) ;

		margin = ApplicationHelper.getClassNode( this,
				getName(), null ).getDouble( ApplicationConstant.PK_ANNOTATION_MARGIN, DEFAULT_MARGIN ) ;
		rise = ApplicationHelper.getClassNode( this,
				getName(), null ).getDouble( ApplicationConstant.PK_ANNOTATION_RISE, DEFAULT_RISE ) ;

		size = ApplicationHelper.getClassNode( this,
				getName(), ApplicationConstant.PN_ANNOTATION_PURPOSE ).getDouble( getPurpose(), DEFAULT_PURPOSE ) ;
	}

	public void headPS( PostscriptStream ps ) {
	}

	public void emitPS( PostscriptStream ps ) {
		ps.operator.gsave() ;

		ps.array( true ) ;
		for ( int t=0 ; t<getTextCount() ; t++ ) {
			emitPS( ps, getText( t ), size, 0,
					subscriptshrink, subscriptshift, superscriptshrink, superscriptshift ) ;
		}
		ps.array( false ) ;

		ps.operator.currentpoint() ;
		ps.operator.translate() ;

		if ( getRadiant() ) {
			ps.operator.rotate( 90 ) ;
		}

		if ( getReverse() ) {
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

	public void tailPS( PostscriptStream ps ) {
	}

	public static void emitPS( PostscriptStream ps, astrolabe.model.TextType text, double size, double shift,
			double subscriptshrink, double subscriptshift, double superscriptshrink, double superscriptshift ) {
		java.util.Vector FET, fet ;

		FET = ps.ucFET( substitute( text.getValue() ) ) ;
		try {
			for ( int e=0 ; e<FET.size() ; e++ ) {
				fet = (java.util.Vector) FET.get( e ) ;
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
		} catch ( ParameterNotValidException e ) {}
		// concerns push(FET) invoke. FET chars and strings are considered well-defined

		for ( int d=0 ; d<text.getSubscriptCount() ; d++ ) {
			emitPS( ps, text.getSubscript( d ),
					size*subscriptshrink, shift+size*subscriptshift,
					subscriptshrink, subscriptshift, superscriptshrink, superscriptshift ) ;
		}

		for ( int u=0 ; u<text.getSuperscriptCount() ; u++ ) {
			emitPS( ps, text.getSuperscript( u ),
					size*superscriptshrink, shift+size*superscriptshift,
					subscriptshrink, subscriptshift, superscriptshrink, superscriptshift ) ;
		}
	}

	private static String substitute( String string ) {
		String s, k, t, v ;
		java.util.regex.Pattern p ;
		java.util.regex.Matcher m ;

		t = new String( string ) ;

		p = java.util.regex.Pattern.compile( ApplicationConstant.LP_SUBSTITUTE ) ;
		m = p.matcher( t ) ;

		while ( m.find( 0 ) ) {
			s = t.substring( m.start(), m.end() ) ;
			k = s.substring( 2, s.length()-2 ) ;
			try {
				v = (String) Registry.retrieve( k ) ;
			} catch ( ParameterNotValidException e ) {
				try {
					String msg ;

					msg = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ANNOTATION_NOSUBSTITUTE ) ;
					msg = MessageFormat.format( msg, new Object[] { k, string } ) ;

					log.info( msg ) ;
				} catch ( ParameterNotValidException ee ) {}

				v = s ;
			}
			t = m.replaceFirst( v ) ;
			m = p.matcher( t ) ;
		}

		return t ;
	}
}
