
package astrolabe;

import java.text.MessageFormat;
import java.util.Hashtable;
import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.xml.ValidationException;

@SuppressWarnings("serial")
public class AnnotationStraight extends astrolabe.model.AnnotationStraight implements PostscriptEmitter {

	private final static Log log = LogFactory.getLog( AnnotationStraight.class ) ;

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

	public AnnotationStraight( Object peer ) throws ParameterNotValidException {
		Preferences node ;

		ApplicationHelper.setupCompanionFromPeer( this, peer ) ;
		try {
			validate() ;
		} catch ( ValidationException e ) {
			throw new ParameterNotValidException( e.toString() ) ;
		}

		node = ApplicationHelper.getClassNode( this, getName(), null ) ;

		subscriptshrink = ApplicationHelper.getPreferencesKV( node, ApplicationConstant.PK_ANNOTATION_SUBSCRIPTSHRINK, DEFAULT_SUBSCRIPTSHRINK ) ;
		subscriptshift = ApplicationHelper.getPreferencesKV( node, ApplicationConstant.PK_ANNOTATION_SUBSCRIPTSHIFT, DEFAULT_SUBSCRIPTSHIFT ) ;
		superscriptshrink = ApplicationHelper.getPreferencesKV( node, ApplicationConstant.PK_ANNOTATION_SUPERSCRIPTSHRINK, DEFAULT_SUPERSCRIPTSHRINK ) ;
		superscriptshift = ApplicationHelper.getPreferencesKV( node, ApplicationConstant.PK_ANNOTATION_SUPERSCRIPTSHIFT, DEFAULT_SUPERSCRIPTSHIFT ) ;

		margin = ApplicationHelper.getPreferencesKV( node, ApplicationConstant.PK_ANNOTATION_MARGIN, DEFAULT_MARGIN ) ;
		rise = ApplicationHelper.getPreferencesKV( node, ApplicationConstant.PK_ANNOTATION_RISE, DEFAULT_RISE ) ;
	}

	public void headPS( PostscriptStream ps ) {
	}

	public void emitPS( PostscriptStream ps ) {
		Layout layout ;
		Frame frame ;
		int nt, ne ;
		double size ;
		Text text ;

		try {
			size = new Text( getText( 0 ) ).size() ;
		} catch ( ParameterNotValidException e ) {
			throw new RuntimeException( e.toString() ) ;
		}

		ps.operator.gsave() ;

		if ( getFrame() != null ) {
			try {
				layout = (Layout) Registry.retrieve( ApplicationConstant.GC_LAYOUT ) ;

				frame = new Frame( getFrame(), layout ) ;

				frame.headPS( ps ) ;
				frame.emitPS( ps ) ;
				frame.tailPS( ps ) ;
			} catch ( ParameterNotValidException e ) {} // Registry.retrieve()
		}

		ps.array( true ) ;
		for ( nt=0, ne=0 ; nt<getTextCount() ; nt++ ) {
			try {
				try {
					text = new Text( getText( nt ) ) ;
				} catch ( ParameterNotValidException e ) { // should not happen
					throw new RuntimeException( e.toString() ) ;
				}
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
			double subscriptshrink, double subscriptshift, double superscriptshrink, double superscriptshift ) throws ParameterNotValidException {
		java.util.Vector<java.util.Vector<Object>> FET ;
		java.util.Vector<Object> fet ;
		String t ;

		t = substitute( text.getValue() ) ;
		if ( t.length()==0 ) {
			String msg ;

			msg = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
			msg = MessageFormat.format( msg, new Object[] { "\"\"", text.getValue() } ) ;
			throw new ParameterNotValidException( msg ) ;
		}

		FET = ps.ucFET( t ) ;
		try {
			for ( int e=0 ; e<FET.size() ; e++ ) {
				fet = (java.util.Vector<Object>) FET.get( e ) ;
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
		} catch ( ParameterNotValidException e ) {
			throw new RuntimeException( e.toString() ) ;
		}

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

	public static String substitute( String string ) {
		return substitute( string, new Hashtable<String, String>() ) ;
	}

	public static String substitute( String string, Hashtable<String, String> registry ) {
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
				v = registry.get( k ) ;
				if ( v == null ) {
					v = (String) Registry.retrieve( k ) ;
				}
				t = m.replaceFirst( v ) ;
			} catch ( ParameterNotValidException e ) {
				String msg ;

				msg = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
				msg = MessageFormat.format( msg, new Object[] { "\""+t+"\"", "\""+string+"\"" } ) ;
				log.warn( msg ) ;

				t = m.replaceFirst( "" ) ;
			}
			m = p.matcher( t ) ;
		}

		return t ;
	}
}
