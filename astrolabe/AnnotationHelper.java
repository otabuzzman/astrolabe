
package astrolabe;

public class AnnotationHelper extends Model {

	public static void emitPS( PostscriptStream ps, astrolabe.model.Annotation[] annotation ) throws ParameterNotValidException, InvalidUnicodeCharacterException {

		if ( annotation == null ) {
			throw new ParameterNotValidException() ;
		}

		for ( int a=0 ; a<annotation.length ; a++ ) {
			emitPS( ps, annotation[a] ) ;
		}
	}

	public static void emitPS( PostscriptStream ps, astrolabe.model.Annotation annotation ) throws ParameterNotValidException, InvalidUnicodeCharacterException {
		astrolabe.model.AnnotationStraight anS ;
		astrolabe.model.AnnotationCurved anC ;

		if ( annotation == null ) {
			throw new ParameterNotValidException() ;
		}

		if ( ( anS = annotation.getAnnotationStraight() ) != null ) {
			emitPS( ps, anS ) ;
		} else if ( ( anC = annotation.getAnnotationCurved() ) != null ) {
			emitPS( ps, anC ) ;
		}
	}

	public static void emitPS( PostscriptStream ps, astrolabe.model.AnnotationStraight annotation ) throws ParameterNotValidException, InvalidUnicodeCharacterException {
		double subscriptshrink, subscriptshift ;
		double superscriptshrink, superscriptshift ;
		double margin, rise, size ;

		if ( annotation == null ) {
			throw new ParameterNotValidException() ;
		}

		subscriptshrink = AnnotationHelper.getClassNode( null ).getDouble( "subscriptshrink", .8 ) ;
		subscriptshift = AnnotationHelper.getClassNode( null ).getDouble( "subscriptshift", -.3 ) ;
		superscriptshrink = AnnotationHelper.getClassNode( null ).getDouble( "superscriptshrink", .8 ) ;
		superscriptshift = AnnotationHelper.getClassNode( null ).getDouble( "superscriptshift", .5 ) ;

		margin = getClassNode( null ).getDouble( "margin", 1.2 ) ;
		rise = getClassNode( null ).getDouble( "rise", 1.2 ) ;

		size = getClassNode( "purpose" ).getDouble( annotation.getPurpose(), 3.8 ) ;

		ps.operator.gsave() ;

		ps.array( true ) ;
		for ( int t=0 ; t<annotation.getTextCount() ; t++ ) {
			emitPS( ps, annotation.getText( t ), size, 0,
					subscriptshrink, subscriptshift, superscriptshrink, superscriptshift ) ;
		}
		ps.array( false ) ;

		ps.operator.currentpoint() ;
		ps.operator.translate() ;

		if ( annotation.getReverse() ) {
			ps.operator.rotate( 180 ) ;
		}

		if ( annotation.getAnchor().equals( "bottomleft" ) ) {
			ps.push( margin ) ;
			ps.push( rise ) ;
		} else if ( annotation.getAnchor().equals( "bottommiddle" ) ) {
			ps.operator.dup() ;
			ps.operator.stringwidth() ;
			ps.operator.pop() ;
			ps.operator.div( -2 ) ;
			ps.push( rise ) ;
		} else if ( annotation.getAnchor().equals( "botomright" ) ) {
			ps.operator.dup() ;
			ps.operator.stringwidth() ;
			ps.operator.pop() ;
			ps.operator.add( margin ) ;
			ps.operator.mul( -1 ) ;
			ps.push( rise ) ;
		} else if ( annotation.getAnchor().equals( "middleleft" ) ) {
			ps.push( margin ) ;
			ps.push( -size/2 ) ;
		} else if ( annotation.getAnchor().equals( "middle" ) ) {
			ps.operator.dup() ;
			ps.operator.stringwidth() ;
			ps.operator.pop() ;
			ps.operator.div( -2 ) ;
			ps.push( -size/2 ) ;
		} else if ( annotation.getAnchor().equals( "middleright" ) ) {
			ps.operator.dup() ;
			ps.operator.stringwidth() ;
			ps.operator.pop() ;
			ps.operator.add( margin ) ;
			ps.operator.mul( -1 ) ;
			ps.push( -size/2 ) ;
		} else if ( annotation.getAnchor().equals( "topleft" ) ) {
			ps.push( margin ) ;
			ps.push( -( size+rise ) ) ;
		} else if ( annotation.getAnchor().equals( "topmiddle" ) ) {
			ps.operator.dup() ;
			ps.operator.stringwidth() ;
			ps.operator.pop() ;
			ps.operator.div( -2 ) ;
			ps.push( -( size+rise ) ) ;
		} else if ( annotation.getAnchor().equals( "topright" ) ) {
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

	public static void emitPS( PostscriptStream ps, astrolabe.model.AnnotationCurved annotation ) throws ParameterNotValidException, InvalidUnicodeCharacterException {
		double subscriptshrink, subscriptshift ;
		double superscriptshrink, superscriptshift ;
		double margin, rise, size ;
		double distance ;

		if ( annotation == null ) {
			throw new ParameterNotValidException() ;
		}

		subscriptshrink = AnnotationHelper.getClassNode( null ).getDouble( "subscriptshrink", .8 ) ;
		subscriptshift = AnnotationHelper.getClassNode( null ).getDouble( "subscriptshift", -.3 ) ;
		superscriptshrink = AnnotationHelper.getClassNode( null ).getDouble( "superscriptshrink", .8 ) ;
		superscriptshift = AnnotationHelper.getClassNode( null ).getDouble( "superscriptshift", .5 ) ;

		margin = getClassNode( null ).getDouble( "margin", 1.2 ) ;
		rise = getClassNode( null ).getDouble( "rise", 1.2 ) ;

		size = getClassNode( "purpose" ).getDouble( annotation.getPurpose(), 3.8 ) ;

		distance = annotation.getDistance() ;

		ps.operator.gsave() ;

		ps.array( true ) ;
		for ( int t=0 ; t<annotation.getTextCount() ; t++ ) {
			emitPS( ps, annotation.getText( t ), size, 0,
					subscriptshrink, subscriptshift, superscriptshrink, superscriptshift ) ;
		}
		ps.array( false ) ;

		ps.operator.currentpoint() ;
		ps.operator.translate() ;

		if ( annotation.getReverse() ) {
			ps.pathreverse() ;
		}

		ps.operator.mark() ;
		if ( annotation.getAnchor().equals( "bottomleft" ) ) {
			ps.pathshift( -rise ) ;
			ps.polyline() ;
			ps.pathreverse() ;
			ps.pathlength() ;
			ps.operator.div( 100 ) ;
			ps.operator.mul( distance ) ;
			ps.push( margin ) ;
			ps.operator.add() ;
		} else if ( annotation.getAnchor().equals( "bottommiddle" ) ) {
			ps.pathshift( -rise ) ;
			ps.polyline() ;
			ps.pathreverse() ;
			ps.operator.dup() ;
			ps.operator.stringwidth() ;
			ps.operator.pop() ;
			ps.operator.div( 2 ) ;
			ps.pathlength() ;
			ps.operator.div( 100 ) ;
			ps.operator.mul( distance ) ;
			ps.operator.exch() ;
			ps.operator.sub() ;
		} else if ( annotation.getAnchor().equals( "bottomright" ) ) {
			ps.pathshift( -rise ) ;
			ps.polyline() ;
			ps.pathreverse() ;
			ps.operator.dup() ;
			ps.operator.stringwidth() ;
			ps.operator.pop() ;
			ps.operator.add( margin ) ;
			ps.pathlength() ;
			ps.operator.div( 100 ) ;
			ps.operator.mul( distance ) ;
			ps.operator.exch() ;
			ps.operator.sub() ;
		} else if ( annotation.getAnchor().equals( "middleleft" ) ) {
			ps.pathshift( size/2 ) ;
			ps.polyline() ;
			ps.pathreverse() ;
			ps.pathlength() ;
			ps.operator.div( 100 ) ;
			ps.operator.mul( distance ) ;
			ps.push( margin ) ;
			ps.operator.add() ;
		} else if ( annotation.getAnchor().equals( "middle" ) ) {
			ps.pathshift( size/2 ) ;
			ps.polyline() ;
			ps.pathreverse() ;
			ps.operator.dup() ;
			ps.operator.stringwidth() ;
			ps.operator.pop() ;
			ps.operator.div( 2 ) ;
			ps.pathlength() ;
			ps.operator.div( 100 ) ;
			ps.operator.mul( distance ) ;
			ps.operator.exch() ;
			ps.operator.sub() ;
		} else if ( annotation.getAnchor().equals( "middleright" ) ) {
			ps.pathshift( size/2 ) ;
			ps.polyline() ;
			ps.pathreverse() ;
			ps.operator.dup() ;
			ps.operator.stringwidth() ;
			ps.operator.pop() ;
			ps.operator.add( margin ) ;
			ps.pathlength() ;
			ps.operator.div( 100 ) ;
			ps.operator.mul( distance ) ;
			ps.operator.exch() ;
			ps.operator.sub() ;
		} else if ( annotation.getAnchor().equals( "topleft" ) ) {
			ps.pathshift( size+rise ) ;
			ps.polyline() ;
			ps.pathreverse() ;
			ps.pathlength() ;
			ps.operator.div( 100 ) ;
			ps.operator.mul( distance ) ;
			ps.push( margin ) ;
			ps.operator.add() ;
		} else if ( annotation.getAnchor().equals( "topmiddle" ) ) {
			ps.pathshift( size+rise ) ;
			ps.polyline() ;
			ps.pathreverse() ;
			ps.operator.dup() ;
			ps.operator.stringwidth() ;
			ps.operator.pop() ;
			ps.operator.div( 2 ) ;
			ps.pathlength() ;
			ps.operator.div( 100 ) ;
			ps.operator.mul( distance ) ;
			ps.operator.exch() ;
			ps.operator.sub() ;
		} else if ( annotation.getAnchor().equals( "topright" ) ) {
			ps.pathshift( size+rise ) ;
			ps.polyline() ;
			ps.pathreverse() ;
			ps.operator.dup() ;
			ps.operator.stringwidth() ;
			ps.operator.pop() ;
			ps.operator.add( margin ) ;
			ps.pathlength() ;
			ps.operator.div( 100 ) ;
			ps.operator.mul( distance ) ;
			ps.operator.exch() ;
			ps.operator.sub() ;
		}

		ps.pathshow() ;

		ps.operator.grestore() ;
	}

	private static void emitPS( PostscriptStream ps, astrolabe.model.TextType text, double size, double shift,
			double subscriptshrink, double subscriptshift, double superscriptshrink, double superscriptshift ) throws ParameterNotValidException, InvalidUnicodeCharacterException {
		java.util.Vector FET, fet ;

		FET = ps.ucFET( replace( text.getValue() ) ) ;

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
			AnnotationHelper.emitPS( ps, text.getSubscript( d ),
					size*subscriptshrink, shift+size*subscriptshift,
					subscriptshrink, subscriptshift, superscriptshrink, superscriptshift ) ;
		}

		for ( int u=0 ; u<text.getSuperscriptCount() ; u++ ) {
			AnnotationHelper.emitPS( ps, text.getSuperscript( u ),
					size*superscriptshrink, shift+size*superscriptshift,
					subscriptshrink, subscriptshift, superscriptshrink, superscriptshift ) ;
		}
	}

	private static String replace( String string ) throws ParameterNotValidException {
		String s, k, t, v ;
		java.util.regex.Pattern p ;
		java.util.regex.Matcher m ;
		astrolabe.Registry r ;

		r = new astrolabe.Registry() ;
		t = new String( string ) ;

		p = java.util.regex.Pattern.compile( "@\\{[\\p{Alnum}]*\\}@" );
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

	private static java.util.prefs.Preferences getClassNode( String qualifier ) {
		java.lang.Package p ;
		String q, d, n ;

		p = AnnotationHelper.class.getPackage() ;
		q = qualifier != null ? "/"+qualifier : "" ;

		if ( p != null ) {
			d = "/"+p.getName().replaceAll( "\\.", "/" ) ;
		} else {
			d = "/"+"default"+"/" ;
		}
		d = d+"/"+"Annotation" ;
		n = d+q ;

		try {
			if ( ! java.util.prefs.Preferences.systemRoot().nodeExists( n ) ) {
				n = d ;
			}
		} catch ( java.util.prefs.BackingStoreException e ) {}

		return java.util.prefs.Preferences.systemRoot().node( n ) ;
	}
}
