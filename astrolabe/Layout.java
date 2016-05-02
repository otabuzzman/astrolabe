
package astrolabe;

public class Layout {

	private double originx ;
	private double originy ;
	private double extentx ;
	private double extenty ;

	private double[] frameColLE ; // left edge
	private double[] frameRowTE ; // top edge

	public Layout( String layout, double[] dimension ) {
		String[] frameValsRaw, frameColsRaw, frameRowsRaw ;
		double[] frameColsVal, frameRowsVal ;
		double colVal, rowVal ;

		originx = dimension[0] ;
		originy = dimension[1] ;
		extentx = dimension[2]-dimension[0] ;
		extenty = dimension[3]-dimension[1] ;

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

		col = saveIndex%numberOfCols ;
		row = saveIndex/numberOfCols ;

		return new double[] {
				originx+extentx*frameColLE[col],
				originy+extenty*frameRowTE[row+1],
				originx+extentx*frameColLE[col+1],
				originy+extenty*frameRowTE[row] } ;
	}
}
