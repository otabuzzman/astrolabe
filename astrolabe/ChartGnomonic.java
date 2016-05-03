
package astrolabe;

import org.exolab.castor.xml.ValidationException;

@SuppressWarnings("serial")
public class ChartGnomonic extends ChartAzimuthalType {

	public void emitPS( ApplicationPostscriptStream ps ) {
		ChartPage page ;
		double[] view ;
		AtlasGnomonic atlas ;

		super.emitPS( ps ) ;

		if ( getAtlas() != null ) {
			page = new ChartPage() ;
			getChartPage().copyValues( page ) ;

			view = page.view() ;

			try {
				atlas = new AtlasGnomonic( getAtlas(), new double[] { view[0], view[1] }, getNorthern(), this ) ;
				atlas.addAllAtlasPage() ;
			} catch ( ValidationException e ) {
				throw new RuntimeException( e.toString() ) ;
			}

			atlas.headAUX() ;
			atlas.emitAUX() ;
			atlas.tailAUX() ;

			ps.op( "gsave" ) ;

			atlas.headPS( ps ) ;
			atlas.emitPS( ps ) ;
			atlas.tailPS( ps ) ;

			ps.op( "grestore" ) ;
		}
	}

	public double distance( double value, boolean inverse ) {
		double v, a ;

		if ( inverse ) {
			v = 90-Math.atan( value ) ;
			return getNorthern()?v:-v ;
		} else {
			a = getNorthern()?value:-value ;
			return Math.tan( 90-a ) ;
		}
	}
}
