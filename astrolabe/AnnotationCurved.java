
package astrolabe;

@SuppressWarnings("serial")
public class AnnotationCurved extends astrolabe.model.AnnotationCurved implements Annotation {

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

	private double distance ;

	public AnnotationCurved( Object peer ) {
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

		distance = getDistance() ;
	}

	public void headPS( PostscriptStream ps ) {
	}

	public void emitPS( PostscriptStream ps ) {
		ps.operator.gsave() ;

		try {
			ps.array( true ) ;
			for ( int t=0 ; t<getTextCount() ; t++ ) {
				AnnotationStraight.emitPS( ps, getText( t ), size, 0,
						subscriptshrink, subscriptshift, superscriptshrink, superscriptshift ) ;
			}
			ps.array( false ) ;

			ps.operator.currentpoint() ;
			ps.operator.translate() ;

			if ( getReverse() ) {
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
		} catch ( ParameterNotValidException e ) {}
		// concern custom(PS_PROLOG) invoke. prolog definitions are considered well-defined

		ps.operator.grestore() ;
	}

	public void tailPS( PostscriptStream ps ) {
	}
}
