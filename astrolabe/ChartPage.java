
package astrolabe;

import java.util.Date;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

@SuppressWarnings("serial")
public class ChartPage extends astrolabe.model.ChartPage implements PostscriptEmitter {

	private double[] layCLeftEdge ;
	private double[] layRTopEdge ;

	// configuration key (CK_)
	private final static String CK_PSUNIT		= "psunit" ;
	private final static String CK_LAYOUT		= "layout" ;

	private final static double DEFAULT_PSUNIT	= 2.834646 ;
	private final static String DEFAULT_LAYOUT	= "100x100" ;

	public double[] size() {
		String sv, sd[] ;

		sv = Configuration.getValue( this, getSize(), null ) ;
		if ( sv == null ) {
			sd = getSize().split( "x" ) ;
		} else {
			sd = sv.split( "x" ) ;
		}

		return new double[] {
				new Double( sd[0] ).doubleValue(),
				new Double( sd[1] ).doubleValue() } ;
	}

	public double[] view() {
		double[] size ;

		size = size() ;

		return new double[] {
				size[0]*getView()/100,
				size[1]*getView()/100 } ;
	}

	public void headPS( ApplicationPostscriptStream ps ) {
	}

	public void emitPS( ApplicationPostscriptStream ps ) {
		double[] size, view ;
		double psunit ;
		long seed ;

		size = size() ;
		view = view() ;

		psunit = Configuration.getValue( this, CK_PSUNIT, DEFAULT_PSUNIT ) ;

		ps.dsc.beginSetup() ;

		ps.dict( true ) ;
		ps.push( "/PageSize" ) ;
		ps.array( true ) ;
		ps.push( size[0]*psunit ) ;
		ps.push( size[1]*psunit ) ;
		ps.array( false ) ;
		ps.dict( false ) ;
		ps.operator.setpagedevice() ;

		seed = new Date().getTime()/1000 ;
		ps.operator.srand( seed ) ;

		ps.dsc.endSetup() ;

		ps.dsc.beginPageSetup() ;

		ps.operator.scale( psunit ) ;

		ps.dsc.endPageSetup() ;

		if ( size[0]>view[0] ) {
			ps.array( true ) ;
			ps.push( -view[0]/2 ) ;
			ps.push( view[1]/2 ) ;
			ps.push( view[0]/2 ) ;
			ps.push( view[1]/2 ) ;
			ps.push( view[0]/2 ) ;
			ps.push( -view[1]/2 ) ;
			ps.push( -view[0]/2 ) ;
			ps.push( -view[1]/2 ) ;
			ps.array( false ) ;

			ps.operator.newpath() ;
			ps.gdraw() ;

			ps.operator.closepath() ;
			ps.operator.stroke() ;

			ps.push( -view[0]/2 ) ;
			ps.push( view[1]/2 ) ;
			ps.operator.moveto() ;
		} else {
			ps.push( -size[0]/2 ) ;
			ps.push( size[1]/2 ) ;
			ps.operator.moveto() ;
		}
	}

	public void tailPS( ApplicationPostscriptStream ps ) {
	}

	public double[] getFrameDef( int num ) {
		String layout ;
		String[] layVRaw, layCRaw, layRRaw ;
		double[] layCVal, layRVal ;
		double colVal, rowVal ;
		int numC, numR, numF ;
		int i, col, row ;
		double[] size ;
		double ox, oy, dx, dy ;

		if ( layCLeftEdge == null ) {
			layout = Configuration.getValue( this, CK_LAYOUT, DEFAULT_LAYOUT ) ;
			layVRaw = layout.split( "x" ) ;
			layCRaw = layVRaw[0].split( ":" ) ;
			layRRaw = layVRaw[1].split( ":" ) ;

			layCVal = new double[ layCRaw.length+1 ] ;
			layRVal = new double[ layRRaw.length+1 ] ;
			layCVal[0] = 0 ;
			layRVal[0] = 0 ;

			for ( int c=0 ; c<layCRaw.length ; c++ ) {
				colVal = new Double( layCRaw[c] ).doubleValue() ;
				layCVal[c+1] = colVal+layCVal[c] ;
			}
			for ( int r=0 ; r<layRRaw.length ; r++ ) {
				rowVal = new Double( layRRaw[r] ).doubleValue() ;
				layRVal[r+1] = rowVal+layRVal[r] ;
			}

			layCLeftEdge = new double[ layCVal.length ] ;
			layRTopEdge = new double[ layRVal.length ] ;

			for ( int c=0 ; c<layCVal.length ; c++ ) {
				layCLeftEdge[c] = layCVal[c]/layCVal[layCVal.length-1] ;
			}
			for ( int r=0 ; r<layRVal.length ; r++ ) {
				layRTopEdge[r] = 1-layRVal[r]/layRVal[layRVal.length-1] ;
			}
		}

		numC = layCLeftEdge.length-1 ;
		numR = layRTopEdge.length-1 ;
		numF = numC*numR ;

		i = ( num-1 )%numF ;
		col = i%numC ;
		row = i/numC ;

		size = size() ;

		ox = -size[0]/2+size[0]*layCLeftEdge[col] ;
		oy = -size[1]/2+size[1]*layRTopEdge[row+1] ;

		dx = size[0]*( layCLeftEdge[col+1]-layCLeftEdge[col] ) ;
		dy = size[1]*( layRTopEdge[row]-layRTopEdge[row+1] ) ;

		return new double[] { ox, oy, dx, dy } ;
	}

	public Geometry getSizeGeometry() {
		double size[], x, y ;

		size = size() ;
		x = size[0]/2 ;
		y = size[1]/2 ;

		return getGeometry( new double[] { -x, -y, x, y } ) ;
	}

	public Geometry getViewGeometry() {
		double view[], x, y ;

		view = view() ;
		x = view[0]/2 ;
		y = view[1]/2 ;

		return getGeometry( new double[] { -x, -y, x, y } ) ;
	}

	public Geometry getFrameGeometry( int num ) {
		double frame[], x, y ;

		frame = getFrameDef( num ) ;
		x = frame[0] ;
		y = frame[1] ;

		return getGeometry( new double[] { x, y, x+frame[2], y+frame[3] } ) ;
	}

	private Geometry getGeometry( double[] xy ) {
		Coordinate[] c ;

		c = new Coordinate[] {
				new Coordinate( xy[0], xy[3] ),
				new Coordinate( xy[2], xy[3] ),
				new Coordinate( xy[2], xy[1] ),
				new Coordinate( xy[0], xy[1] ),
				new Coordinate( xy[0], xy[3] )
		} ;

		return new GeometryFactory().createPolygon(
				new GeometryFactory().createLinearRing( c ), null ) ;
	}
}
