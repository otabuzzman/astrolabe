
package astrolabe;

import org.exolab.castor.xml.ValidationException;

@SuppressWarnings("serial")
public class ChartEquidistant extends ChartAzimuthalType {

	public void emitPS( ApplicationPostscriptStream ps ) {
		ChartPage page ;
		double[] view ;
		AtlasEquidistant atlas ;

		super.emitPS( ps ) ;

		if ( getAtlas() != null ) {
			page = new ChartPage() ;
			getChartPage().copyValues( page ) ;

			view = page.view() ;

			try {
				atlas = new AtlasEquidistant( getAtlas(), new double[] { view[0], view[1] }, getNorthern(), this ) ;
				atlas.addAllAtlasPage() ;
			} catch ( ValidationException e ) {
				throw new RuntimeException( e.toString() ) ;
			}

			atlas.headAUX() ;
			atlas.emitAUX() ;
			atlas.tailAUX() ;

			ps.operator.gsave() ;

			atlas.headPS( ps ) ;
			atlas.emitPS( ps ) ;
			atlas.tailPS( ps ) ;

			ps.operator.grestore() ;
		}
	}

	public double distance( double value, boolean inverse ) {
		double v, a ;

		if ( inverse ) {
			v = 90-value*90 ;
			return getNorthern()?v:-v ;
		} else {
			a = getNorthern()?value:-value ;
			return ( 90-a )/90 ;
		}
	}
}
