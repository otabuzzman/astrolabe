
package astrolabe;

public class AnnotationStraight implements Annotation {

	private astrolabe.model.AnnotationStraight anT ;

	private double subscriptshrink ;
	private double subscriptshift ;
	private double superscriptshrink ;
	private double superscriptshift ;

	private double margin ;
	private double rise ;

	private double size ;

	public AnnotationStraight( astrolabe.model.AnnotationType anT ) {
		this.anT = (astrolabe.model.AnnotationStraight) anT ;

		subscriptshrink = ApplicationHelper.getClassNode( this, anT.getName(), null ).getDouble( ApplicationConstant.PK_ANNOTATION_SUBSCRIPTSHRINK, .8 ) ;
		subscriptshift = ApplicationHelper.getClassNode( this, anT.getName(), null ).getDouble( ApplicationConstant.PK_ANNOTATION_SUBSCRIPTSHIFT, -.3 ) ;
		superscriptshrink = ApplicationHelper.getClassNode( this, anT.getName(), null ).getDouble( ApplicationConstant.PK_ANNOTATION_SUPERSCRIPTSHRINK, .8 ) ;
		superscriptshift = ApplicationHelper.getClassNode( this, anT.getName(), null ).getDouble( ApplicationConstant.PK_ANNOTATION_SUPERSCRIPTSHIFT, .5 ) ;

		margin = ApplicationHelper.getClassNode( this, anT.getName(), null ).getDouble( ApplicationConstant.PK_ANNOTATION_MARGIN, 1.2 ) ;
		rise = ApplicationHelper.getClassNode( this, anT.getName(), null ).getDouble( ApplicationConstant.PK_ANNOTATION_RISE, 1.2 ) ;

		size = ApplicationHelper.getClassNode( this, anT.getName(), ApplicationConstant.PN_ANNOTATION_PURPOSE ).getDouble( anT.getPurpose(), 3.8 ) ;
	}

	public void emitPS( PostscriptStream ps ) throws ParameterNotValidException {
		ps.operator.gsave() ;

		ps.array( true ) ;
		for ( int t=0 ; t<anT.getTextCount() ; t++ ) {
			emitPS( ps, anT.getText( t ), size, 0,
					subscriptshrink, subscriptshift, superscriptshrink, superscriptshift ) ;
		}
		ps.array( false ) ;

		ps.operator.currentpoint() ;
		ps.operator.translate() ;

		if ( ( (astrolabe.model.AnnotationStraight) anT ).getRadiant() ) {
			ps.operator.rotate( 90 ) ;
		}

		if ( anT.getReverse() ) {
			ps.operator.rotate( 180 ) ;
		}

		if ( anT.getAnchor().equals( ApplicationConstant.AV_ANNOTATION_BOTTOMLEFT ) ) {
			ps.push( margin ) ;
			ps.push( rise ) ;
		} else if ( anT.getAnchor().equals( ApplicationConstant.AV_ANNOTATION_BOTTOMMIDDLE ) ) {
			ps.operator.dup() ;
			ps.operator.stringwidth() ;
			ps.operator.pop() ;
			ps.operator.div( -2 ) ;
			ps.push( rise ) ;
		} else if ( anT.getAnchor().equals( ApplicationConstant.AV_ANNOTATION_BOTTOMRIGHT ) ) {
			ps.operator.dup() ;
			ps.operator.stringwidth() ;
			ps.operator.pop() ;
			ps.operator.add( margin ) ;
			ps.operator.mul( -1 ) ;
			ps.push( rise ) ;
		} else if ( anT.getAnchor().equals( ApplicationConstant.AV_ANNOTATION_MIDDLELEFT ) ) {
			ps.push( margin ) ;
			ps.push( -size/2 ) ;
		} else if ( anT.getAnchor().equals( ApplicationConstant.AV_ANNOTATION_MIDDLE ) ) {
			ps.operator.dup() ;
			ps.operator.stringwidth() ;
			ps.operator.pop() ;
			ps.operator.div( -2 ) ;
			ps.push( -size/2 ) ;
		} else if ( anT.getAnchor().equals( ApplicationConstant.AV_ANNOTATION_MIDDLERIGHT ) ) {
			ps.operator.dup() ;
			ps.operator.stringwidth() ;
			ps.operator.pop() ;
			ps.operator.add( margin ) ;
			ps.operator.mul( -1 ) ;
			ps.push( -size/2 ) ;
		} else if ( anT.getAnchor().equals( ApplicationConstant.AV_ANNOTATION_TOPLEFT ) ) {
			ps.push( margin ) ;
			ps.push( -( size+rise ) ) ;
		} else if ( anT.getAnchor().equals( ApplicationConstant.AV_ANNOTATION_TOPMIDDLE ) ) {
			ps.operator.dup() ;
			ps.operator.stringwidth() ;
			ps.operator.pop() ;
			ps.operator.div( -2 ) ;
			ps.push( -( size+rise ) ) ;
		} else if ( anT.getAnchor().equals( ApplicationConstant.AV_ANNOTATION_TOPRIGHT ) ) {
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

	public static void emitPS( PostscriptStream ps, astrolabe.model.TextType text, double size, double shift,
			double subscriptshrink, double subscriptshift, double superscriptshrink, double superscriptshift ) throws ParameterNotValidException {
		java.util.Vector FET, fet ;

		FET = ps.ucFET( substitute( text.getValue() ) ) ;

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

	private static String substitute( String string ) throws ParameterNotValidException {
		String s, k, t, v ;
		java.util.regex.Pattern p ;
		java.util.regex.Matcher m ;
		astrolabe.Registry r ;

		r = new astrolabe.Registry() ;
		t = new String( string ) ;

		p = java.util.regex.Pattern.compile( ApplicationConstant.LP_SUBSTITUTE ) ;
		m = p.matcher( t ) ;

		while ( m.find( 0 ) ) {
			s = t.substring( m.start(), m.end() ) ;
			k = s.substring( 2, s.length()-2 ) ;
			v = (String) r.retrieve( k ) ;
			t = m.replaceFirst( v ) ;
			m = p.matcher( t ) ;
		}

		return t ;
	}
}
