
package astrolabe;

public class AnnotationCurved implements Annotation {

	private astrolabe.model.AnnotationCurved anT ;

	private double subscriptshrink ;
	private double subscriptshift ;
	private double superscriptshrink ;
	private double superscriptshift ;

	private double margin ;
	private double rise ;

	private double size ;

	private double distance ;

	public AnnotationCurved( astrolabe.model.AnnotationType anT ) {
		this.anT = (astrolabe.model.AnnotationCurved) anT ;

		subscriptshrink = ApplicationHelper.getClassNode( this, anT.getName(), null ).getDouble( ApplicationConstant.PK_ANNOTATION_SUBSCRIPTSHRINK, .8 ) ;
		subscriptshift = ApplicationHelper.getClassNode( this, anT.getName(), null ).getDouble( ApplicationConstant.PK_ANNOTATION_SUBSCRIPTSHIFT, -.3 ) ;
		superscriptshrink = ApplicationHelper.getClassNode( this, anT.getName(), null ).getDouble( ApplicationConstant.PK_ANNOTATION_SUPERSCRIPTSHRINK, .8 ) ;
		superscriptshift = ApplicationHelper.getClassNode( this, anT.getName(), null ).getDouble( ApplicationConstant.PK_ANNOTATION_SUPERSCRIPTSHIFT, .5 ) ;

		margin = ApplicationHelper.getClassNode( this, anT.getName(), null ).getDouble( ApplicationConstant.PK_ANNOTATION_MARGIN, 1.2 ) ;
		rise = ApplicationHelper.getClassNode( this, anT.getName(), null ).getDouble( ApplicationConstant.PK_ANNOTATION_RISE, 1.2 ) ;

		size = ApplicationHelper.getClassNode( this, anT.getName(), ApplicationConstant.PN_ANNOTATION_PURPOSE ).getDouble( anT.getPurpose(), 3.8 ) ;

		distance = this.anT.getDistance() ;
	}

	public void emitPS( PostscriptStream ps ) throws ParameterNotValidException {
		ps.operator.gsave() ;

		ps.array( true ) ;
		for ( int t=0 ; t<anT.getTextCount() ; t++ ) {
			AnnotationStraight.emitPS( ps, anT.getText( t ), size, 0,
					subscriptshrink, subscriptshift, superscriptshrink, superscriptshift ) ;
		}
		ps.array( false ) ;

		ps.operator.currentpoint() ;
		ps.operator.translate() ;

		if ( anT.getReverse() ) {
			ps.custom( ApplicationConstant.PS_PROLOG_PATHREVERSE ) ;
		}

		ps.operator.mark() ;
		if ( anT.getAnchor().equals( ApplicationConstant.AV_ANNOTATION_BOTTOMLEFT ) ) {
			ps.push( -rise ) ;
			ps.custom( ApplicationConstant.PS_PROLOG_PATHSHIFT ) ;
			ps.custom( ApplicationConstant.PS_PROLOG_POLYLINE ) ;
			ps.custom( ApplicationConstant.PS_PROLOG_PATHREVERSE ) ;
			ps.custom( ApplicationConstant.PS_PROLOG_PATHLENGTH ) ;
			ps.operator.div( 100 ) ;
			ps.operator.mul( distance ) ;
			ps.push( margin ) ;
			ps.operator.add() ;
		} else if ( anT.getAnchor().equals( ApplicationConstant.AV_ANNOTATION_BOTTOMMIDDLE ) ) {
			ps.push( -rise ) ;
			ps.custom( ApplicationConstant.PS_PROLOG_PATHSHIFT ) ;
			ps.custom( ApplicationConstant.PS_PROLOG_POLYLINE ) ;
			ps.custom( ApplicationConstant.PS_PROLOG_PATHREVERSE ) ;
			ps.operator.dup() ;
			ps.operator.stringwidth() ;
			ps.operator.pop() ;
			ps.operator.div( 2 ) ;
			ps.custom( ApplicationConstant.PS_PROLOG_PATHLENGTH ) ;
			ps.operator.div( 100 ) ;
			ps.operator.mul( distance ) ;
			ps.operator.exch() ;
			ps.operator.sub() ;
		} else if ( anT.getAnchor().equals( ApplicationConstant.AV_ANNOTATION_BOTTOMRIGHT ) ) {
			ps.push( -rise ) ;
			ps.custom( ApplicationConstant.PS_PROLOG_PATHSHIFT ) ;
			ps.custom( ApplicationConstant.PS_PROLOG_POLYLINE ) ;
			ps.custom( ApplicationConstant.PS_PROLOG_PATHREVERSE ) ;
			ps.operator.dup() ;
			ps.operator.stringwidth() ;
			ps.operator.pop() ;
			ps.operator.add( margin ) ;
			ps.custom( ApplicationConstant.PS_PROLOG_PATHLENGTH ) ;
			ps.operator.div( 100 ) ;
			ps.operator.mul( distance ) ;
			ps.operator.exch() ;
			ps.operator.sub() ;
		} else if ( anT.getAnchor().equals( ApplicationConstant.AV_ANNOTATION_MIDDLELEFT ) ) {
			ps.push( size/2 ) ;
			ps.custom( ApplicationConstant.PS_PROLOG_PATHSHIFT ) ;
			ps.custom( ApplicationConstant.PS_PROLOG_POLYLINE ) ;
			ps.custom( ApplicationConstant.PS_PROLOG_PATHREVERSE ) ;
			ps.custom( ApplicationConstant.PS_PROLOG_PATHLENGTH ) ;
			ps.operator.div( 100 ) ;
			ps.operator.mul( distance ) ;
			ps.push( margin ) ;
			ps.operator.add() ;
		} else if ( anT.getAnchor().equals( ApplicationConstant.AV_ANNOTATION_MIDDLE ) ) {
			ps.push( size/2 ) ;
			ps.custom( ApplicationConstant.PS_PROLOG_PATHSHIFT ) ;
			ps.custom( ApplicationConstant.PS_PROLOG_POLYLINE ) ;
			ps.custom( ApplicationConstant.PS_PROLOG_PATHREVERSE ) ;
			ps.operator.dup() ;
			ps.operator.stringwidth() ;
			ps.operator.pop() ;
			ps.operator.div( 2 ) ;
			ps.custom( ApplicationConstant.PS_PROLOG_PATHLENGTH ) ;
			ps.operator.div( 100 ) ;
			ps.operator.mul( distance ) ;
			ps.operator.exch() ;
			ps.operator.sub() ;
		} else if ( anT.getAnchor().equals( ApplicationConstant.AV_ANNOTATION_MIDDLERIGHT ) ) {
			ps.push( size/2 ) ;
			ps.custom( ApplicationConstant.PS_PROLOG_PATHSHIFT ) ;
			ps.custom( ApplicationConstant.PS_PROLOG_POLYLINE ) ;
			ps.custom( ApplicationConstant.PS_PROLOG_PATHREVERSE ) ;
			ps.operator.dup() ;
			ps.operator.stringwidth() ;
			ps.operator.pop() ;
			ps.operator.add( margin ) ;
			ps.custom( ApplicationConstant.PS_PROLOG_PATHLENGTH ) ;
			ps.operator.div( 100 ) ;
			ps.operator.mul( distance ) ;
			ps.operator.exch() ;
			ps.operator.sub() ;
		} else if ( anT.getAnchor().equals( ApplicationConstant.AV_ANNOTATION_TOPLEFT ) ) {
			ps.push( size+rise ) ;
			ps.custom( ApplicationConstant.PS_PROLOG_PATHSHIFT ) ;
			ps.custom( ApplicationConstant.PS_PROLOG_POLYLINE ) ;
			ps.custom( ApplicationConstant.PS_PROLOG_PATHREVERSE ) ;
			ps.custom( ApplicationConstant.PS_PROLOG_PATHLENGTH ) ;
			ps.operator.div( 100 ) ;
			ps.operator.mul( distance ) ;
			ps.push( margin ) ;
			ps.operator.add() ;
		} else if ( anT.getAnchor().equals( ApplicationConstant.AV_ANNOTATION_TOPMIDDLE ) ) {
			ps.push( size+rise ) ;
			ps.custom( ApplicationConstant.PS_PROLOG_PATHSHIFT ) ;
			ps.custom( ApplicationConstant.PS_PROLOG_POLYLINE ) ;
			ps.custom( ApplicationConstant.PS_PROLOG_PATHREVERSE ) ;
			ps.operator.dup() ;
			ps.operator.stringwidth() ;
			ps.operator.pop() ;
			ps.operator.div( 2 ) ;
			ps.custom( ApplicationConstant.PS_PROLOG_PATHLENGTH ) ;
			ps.operator.div( 100 ) ;
			ps.operator.mul( distance ) ;
			ps.operator.exch() ;
			ps.operator.sub() ;
		} else if ( anT.getAnchor().equals( ApplicationConstant.AV_ANNOTATION_TOPRIGHT ) ) {
			ps.push( size+rise ) ;
			ps.custom( ApplicationConstant.PS_PROLOG_PATHSHIFT ) ;
			ps.custom( ApplicationConstant.PS_PROLOG_POLYLINE ) ;
			ps.custom( ApplicationConstant.PS_PROLOG_PATHREVERSE ) ;
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

		ps.operator.grestore() ;
	}
}
