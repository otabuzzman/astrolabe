
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
			ps.pathreverse() ;
		}

		ps.operator.mark() ;
		if ( anT.getAnchor().equals( ApplicationConstant.AV_ANNOTATION_BOTTOMLEFT ) ) {
			ps.pathshift( -rise ) ;
			ps.polyline() ;
			ps.pathreverse() ;
			ps.pathlength() ;
			ps.operator.div( 100 ) ;
			ps.operator.mul( distance ) ;
			ps.push( margin ) ;
			ps.operator.add() ;
		} else if ( anT.getAnchor().equals( ApplicationConstant.AV_ANNOTATION_BOTTOMMIDDLE ) ) {
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
		} else if ( anT.getAnchor().equals( ApplicationConstant.AV_ANNOTATION_BOTTOMRIGHT ) ) {
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
		} else if ( anT.getAnchor().equals( ApplicationConstant.AV_ANNOTATION_MIDDLELEFT ) ) {
			ps.pathshift( size/2 ) ;
			ps.polyline() ;
			ps.pathreverse() ;
			ps.pathlength() ;
			ps.operator.div( 100 ) ;
			ps.operator.mul( distance ) ;
			ps.push( margin ) ;
			ps.operator.add() ;
		} else if ( anT.getAnchor().equals( ApplicationConstant.AV_ANNOTATION_MIDDLE ) ) {
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
		} else if ( anT.getAnchor().equals( ApplicationConstant.AV_ANNOTATION_MIDDLERIGHT ) ) {
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
		} else if ( anT.getAnchor().equals( ApplicationConstant.AV_ANNOTATION_TOPLEFT ) ) {
			ps.pathshift( size+rise ) ;
			ps.polyline() ;
			ps.pathreverse() ;
			ps.pathlength() ;
			ps.operator.div( 100 ) ;
			ps.operator.mul( distance ) ;
			ps.push( margin ) ;
			ps.operator.add() ;
		} else if ( anT.getAnchor().equals( ApplicationConstant.AV_ANNOTATION_TOPMIDDLE ) ) {
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
		} else if ( anT.getAnchor().equals( ApplicationConstant.AV_ANNOTATION_TOPRIGHT ) ) {
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
}
