
package astrolabe;

public class Layout {

	private final static String DEFAULT_LAYOUT = "100x100" ;

	private double[] frameColLE ; // left edge
	private double[] frameRowTE ; // top edge

	private double[] origin = new double[2] ; // bottom left x/y
	private double[] extent = new double[2] ; // pagesize x/y

	public Layout( double[] userspace ) {
		this( DEFAULT_LAYOUT, userspace ) ;
	}

	public Layout( String layout, double[] userspace ) {
		origin[0] = userspace[0] ;
		origin[1] = userspace[1] ;
		extent[0] = userspace[2]-userspace[0] ;
		extent[1] = userspace[3]-userspace[1] ;

		setup( layout ) ;
	}

	public void setup( String layout ) {
		String[] frameValsRaw, frameColsRaw, frameRowsRaw ;
		double[] frameColsVal, frameRowsVal ;
		double colVal, rowVal ;

		frameValsRaw = layout.split( "x" ) ;

		frameColsRaw = frameValsRaw[0].split( ":" ) ;
		frameRowsRaw = frameValsRaw[1].split( ":" ) ;

		frameColsVal = new double[ frameColsRaw.length+1 ] ;
		frameRowsVal = new double[ frameRowsRaw.length+1 ] ;
		frameColsVal[0] = 0 ;
		frameRowsVal[0] = 0 ;

		for ( int col=0 ; col<frameColsRaw.length ; col++ ) {
			colVal = new Double( frameColsRaw[col] ).doubleValue() ;
			frameColsVal[col+1] = colVal+frameColsVal[col] ;
		}
		for ( int row=0 ; row<frameRowsRaw.length ; row++ ) {
			rowVal = new Double( frameRowsRaw[row] ).doubleValue() ;
			frameRowsVal[row+1] = rowVal+frameRowsVal[row] ;
		}

		frameColLE = new double[ frameColsVal.length ] ;
		frameRowTE = new double[ frameRowsVal.length ] ;

		for ( int col=0 ; col<frameColsVal.length ; col++ ) {
			frameColLE[col] = frameColsVal[col]/frameColsVal[frameColsVal.length-1] ;
		}
		for ( int row=0 ; row<frameRowsVal.length ; row++ ) {
			frameRowTE[row] = 1-frameRowsVal[row]/frameRowsVal[frameRowsVal.length-1] ;
		}
	}

	public double[] frame( int index ) {
		int numberOfCols, numberOfRows, numberOfFrames ;
		int saveIndex ;
		int col, row ;

		numberOfCols = frameColLE.length-1 ;
		numberOfRows = frameRowTE.length-1 ;
		numberOfFrames = numberOfCols*numberOfRows ;

		saveIndex = ( index-1 )%numberOfFrames ;

		col = saveIndex%numberOfRows ;
		row = saveIndex/numberOfRows ;

		return new double[] {
				origin[0]+extent[0]*frameColLE[col],
				origin[1]+extent[1]*frameRowTE[row+1],
				origin[0]+extent[0]*frameColLE[col+1],
				origin[1]+extent[1]*frameRowTE[row] } ;
	}
}
