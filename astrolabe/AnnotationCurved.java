
package astrolabe;

import java.util.prefs.Preferences;

import org.exolab.castor.xml.ValidationException;

@SuppressWarnings("serial")
public class AnnotationCurved extends astrolabe.model.AnnotationCurved implements PostscriptEmitter {

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

	private double distance ;

	public AnnotationCurved( Peer peer ) throws ParameterNotValidException {
		Preferences node ;

		peer.setupCompanion( this ) ;
		try {
			validate() ;
		} catch ( ValidationException e ) {
			throw new ParameterNotValidException( e.toString() ) ;
		}

		node = Configuration.getClassNode( this, getName(), null ) ;

		subscriptshrink = Configuration.getValue( node, ApplicationConstant.PK_ANNOTATION_SUBSCRIPTSHRINK, DEFAULT_SUBSCRIPTSHRINK ) ;
		subscriptshift = Configuration.getValue( node, ApplicationConstant.PK_ANNOTATION_SUBSCRIPTSHIFT, DEFAULT_SUBSCRIPTSHIFT ) ;
		superscriptshrink = Configuration.getValue( node, ApplicationConstant.PK_ANNOTATION_SUPERSCRIPTSHRINK, DEFAULT_SUPERSCRIPTSHRINK ) ;
		superscriptshift = Configuration.getValue( node, ApplicationConstant.PK_ANNOTATION_SUPERSCRIPTSHIFT, DEFAULT_SUPERSCRIPTSHIFT ) ;

		margin = Configuration.getValue( node, ApplicationConstant.PK_ANNOTATION_MARGIN, DEFAULT_MARGIN ) ;
		rise = Configuration.getValue( node, ApplicationConstant.PK_ANNOTATION_RISE, DEFAULT_RISE ) ;

		distance = getDistance() ;
	}

	public void headPS( PostscriptStream ps ) {
	}

	public void emitPS( PostscriptStream ps ) {
		int nt, ne ;
		double size ;
		Text text ;

		try {
			size = new Text( getText( 0 ) ).size() ;
		} catch ( ParameterNotValidException e ) {
			throw new RuntimeException( e.toString() ) ;
		}
		if ( ! ( size>0 ) )
			return ;

		ps.operator.gsave() ;

		ps.array( true ) ;
		for ( nt=0, ne=0 ; nt<getTextCount() ; nt++ ) {
			try {
				try {
					text = new Text( getText( nt ) ) ;
				} catch ( ParameterNotValidException e ) { // should not happen
					throw new RuntimeException( e.toString() ) ;
				}
				AnnotationStraight.emitPS( ps, text, text.size(), 0,
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

		try {
			if ( new Boolean( getReverse() ).booleanValue() ) {
				ps.custom( ApplicationConstant.PS_PROLOG_PATHREVERSE ) ;
			}

			if ( getAnchor().equals( ApplicationConstant.AV_ANNOTATION_BOTTOMLEFT ) ) {
				ps.push( rise ) ;
				ps.push( true ) ;
				ps.custom( ApplicationConstant.PS_PROLOG_PATHSHIFT ) ;
				ps.custom( ApplicationConstant.PS_PROLOG_PATHLENGTH ) ;
				ps.operator.div( 100 ) ;
				ps.operator.mul( distance ) ;
				ps.push( margin ) ;
				ps.operator.add() ;
			} else if ( getAnchor().equals( ApplicationConstant.AV_ANNOTATION_BOTTOMMIDDLE ) ) {
				ps.push( rise ) ;
				ps.push( true ) ;
				ps.custom( ApplicationConstant.PS_PROLOG_PATHSHIFT ) ;
				ps.operator.dup() ;
				ps.operator.stringwidth() ;
				ps.operator.pop() ;
				ps.operator.div( 2 ) ;
				ps.custom( ApplicationConstant.PS_PROLOG_PATHLENGTH ) ;
				ps.operator.div( 100 ) ;
				ps.operator.mul( distance ) ;
				ps.operator.exch() ;
				ps.operator.sub() ;
			} else if ( getAnchor().equals( ApplicationConstant.AV_ANNOTATION_BOTTOMRIGHT ) ) {
				ps.push( rise ) ;
				ps.push( true ) ;
				ps.custom( ApplicationConstant.PS_PROLOG_PATHSHIFT ) ;
				ps.operator.dup() ;
				ps.operator.stringwidth() ;
				ps.operator.pop() ;
				ps.operator.add( margin ) ;
				ps.custom( ApplicationConstant.PS_PROLOG_PATHLENGTH ) ;
				ps.operator.div( 100 ) ;
				ps.operator.mul( distance ) ;
				ps.operator.exch() ;
				ps.operator.sub() ;
			} else if ( getAnchor().equals( ApplicationConstant.AV_ANNOTATION_MIDDLELEFT ) ) {
				ps.push( size/2 ) ;
				ps.push( true ) ;
				ps.custom( ApplicationConstant.PS_PROLOG_PATHSHIFT ) ;
				ps.custom( ApplicationConstant.PS_PROLOG_PATHLENGTH ) ;
				ps.operator.div( 100 ) ;
				ps.operator.mul( distance ) ;
				ps.push( margin ) ;
				ps.operator.add() ;
			} else if ( getAnchor().equals( ApplicationConstant.AV_ANNOTATION_MIDDLE ) ) {
				ps.push( size/2 ) ;
				ps.push( true ) ;
				ps.custom( ApplicationConstant.PS_PROLOG_PATHSHIFT ) ;
				ps.operator.dup() ;
				ps.operator.stringwidth() ;
				ps.operator.pop() ;
				ps.operator.div( 2 ) ;
				ps.custom( ApplicationConstant.PS_PROLOG_PATHLENGTH ) ;
				ps.operator.div( 100 ) ;
				ps.operator.mul( distance ) ;
				ps.operator.exch() ;
				ps.operator.sub() ;
			} else if ( getAnchor().equals( ApplicationConstant.AV_ANNOTATION_MIDDLERIGHT ) ) {
				ps.push( size/2 ) ;
				ps.push( true ) ;
				ps.custom( ApplicationConstant.PS_PROLOG_PATHSHIFT ) ;
				ps.operator.dup() ;
				ps.operator.stringwidth() ;
				ps.operator.pop() ;
				ps.operator.add( margin ) ;
				ps.custom( ApplicationConstant.PS_PROLOG_PATHLENGTH ) ;
				ps.operator.div( 100 ) ;
				ps.operator.mul( distance ) ;
				ps.operator.exch() ;
				ps.operator.sub() ;
			} else if ( getAnchor().equals( ApplicationConstant.AV_ANNOTATION_TOPLEFT ) ) {
				ps.push( -( size+rise ) ) ;
				ps.push( true ) ;
				ps.custom( ApplicationConstant.PS_PROLOG_PATHSHIFT ) ;
				ps.custom( ApplicationConstant.PS_PROLOG_PATHLENGTH ) ;
				ps.operator.div( 100 ) ;
				ps.operator.mul( distance ) ;
				ps.push( margin ) ;
				ps.operator.add() ;
			} else if ( getAnchor().equals( ApplicationConstant.AV_ANNOTATION_TOPMIDDLE ) ) {
				ps.push( -( size+rise ) ) ;
				ps.push( true ) ;
				ps.custom( ApplicationConstant.PS_PROLOG_PATHSHIFT ) ;
				ps.operator.dup() ;
				ps.operator.stringwidth() ;
				ps.operator.pop() ;
				ps.operator.div( 2 ) ;
				ps.custom( ApplicationConstant.PS_PROLOG_PATHLENGTH ) ;
				ps.operator.div( 100 ) ;
				ps.operator.mul( distance ) ;
				ps.operator.exch() ;
				ps.operator.sub() ;
			} else if ( getAnchor().equals( ApplicationConstant.AV_ANNOTATION_TOPRIGHT ) ) {
				ps.push( -( size+rise ) ) ;
				ps.push( true ) ;
				ps.custom( ApplicationConstant.PS_PROLOG_PATHSHIFT ) ;
				ps.operator.dup() ;
				ps.operator.stringwidth() ;
				ps.operator.pop() ;
				ps.operator.add( margin ) ;
				ps.custom( ApplicationConstant.PS_PROLOG_PATHLENGTH ) ;
				ps.operator.div( 100 ) ;
				ps.operator.mul( distance ) ;
				ps.operator.exch() ;
				ps.operator.sub() ;
			}

			ps.custom( ApplicationConstant.PS_PROLOG_PATHSHOW ) ;
		} catch ( ParameterNotValidException e ) {
			throw new RuntimeException( e.toString() ) ;
		}

		ps.operator.grestore() ;
	}

	public void tailPS( PostscriptStream ps ) {
	}
}
