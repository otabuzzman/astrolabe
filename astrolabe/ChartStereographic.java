
package astrolabe;

public class ChartStereographic extends Model implements Chart {

	protected double unit ;
	protected boolean northern ;

	private java.util.Hashtable<String, double[]> pageformat ;
	protected String pagesize ;
	protected double scale ;

	private Process viewer ;
	private java.io.PrintStream ps ;

	public ChartStereographic( astrolabe.model.ChartType chT, PostscriptStream ps ) {
		pageformat = new java.util.Hashtable<String, double[]>() ;

		pageformat.put( "a0", new double[] { 2380, 3368 } ) ;
		pageformat.put( "a1", new double[] { 1684, 2380 } ) ;
		pageformat.put( "a2", new double[] { 1190, 1684 } ) ;
		pageformat.put( "a3", new double[] { 842, 1190 } ) ;
		pageformat.put( "a4", new double[] { 595, 842 } ) ;
		pageformat.put( "a5", new double[] { 421, 595 } ) ;
		pageformat.put( "a6", new double[] { 297, 421 } ) ;

		unit = getClassNode( chT.getName(), null ).getDouble( "unit", 2.834646 ) ;

		pagesize = chT.getPagesize() ;
		scale = getClassNode( chT.getName(), "scale" ).getDouble( pagesize, 1 ) ;

		northern = chT.getHemisphere().equals( "northern" ) ? true : /*"southern"*/ false ;

		setupUserPagesize( pagesize ) ;
		setupViewerProcess( chT.getName(), ps ) ;
	}

	public void initPS( PostscriptStream ps ) {
		ps.dsc.beginSetup() ;
		// Set origin at center of page.
		ps.pagesize() ;
		ps.operator.mul( .5 ) ;
		ps.operator.exch() ;
		ps.operator.mul( .5 ) ;
		ps.operator.exch() ;
		ps.operator.translate() ;
		ps.dsc.endSetup() ;

		ps.dsc.beginPageSetup() ;
		ps.operator.scale( unit ) ;
		ps.dsc.endPageSetup() ;
	}

	public Vector project( double[] eq ) {
		return project( eq[0], eq[1] ) ;
	}

	public Vector project( double RA, double d ) {
		Vector r = new Vector( 0, 0 ) ;
		double rad90 = caa.CAACoordinateTransformation.DegreesToRadians( 90 ) ;

		r.setX( java.lang.Math.tan( ( rad90-d )/2 )*java.lang.Math.cos( RA ) ) ;
		r.setY( -java.lang.Math.tan( ( rad90-d )/2 )*java.lang.Math.sin( RA ) ) ;

		return r.scale( scale ) ;
	}

	public double[] unproject( Vector xy ) {
		return unproject( xy.getX(), xy.getY() ) ;
	}

	public double[] unproject( double x, double y ) {
		return null ;
	}

	public boolean isNorthern() {
		return northern ;
	}

	public boolean isSouthern() {
		return ! northern ;
	}

	public void rollup( PostscriptStream ps ) {
		try {
			ps.delPrintStream( this.ps ) ;
		} catch ( ParameterNotValidException e ) {}

		viewer.destroy() ;
	}

	protected boolean setupUserPagesize( String pagesize ) {
		boolean r ;

		if ( pagesize.matches( "[0-9]+(\\.[0-9]+)?x[0-9]+(\\.[0-9]+)?" ) ) {
			String wh[] ;
			double w, h ;

			wh = pagesize.split( "x" ) ;
			w = new Double( wh[0] ).doubleValue()*unit ;
			h = new Double( wh[1] ).doubleValue()*unit ;

			pageformat.put( pagesize, new double[] { w, h } ) ;

			r = true ;
		} else {
			r = false ;
		}

		return r ;
	}

	protected boolean setupViewerProcess( String node, PostscriptStream ps ) {
		boolean r ;
		String viewer ;
		int i ;

		try {
			viewer = getClassNode( node, null ).get( "viewer", null ) ;

			while ( ( i=viewer.indexOf( '%' ) )>0 ) {
				switch ( viewer.charAt( i+1 ) ) {
				case 'w':
					viewer = viewer.substring( 0, i )+pageformat.get( pagesize )[0]+viewer.substring( i+2, viewer.length() ) ;
					break ;
				case 'h':
					viewer = viewer.substring( 0, i )+pageformat.get( pagesize )[1]+viewer.substring( i+2, viewer.length() ) ;
					break ;
				case 'p':
					viewer = viewer.substring( 0, i )+pagesize+viewer.substring( i+2, viewer.length() ) ;
					break ;
				}
			}

			this.viewer = Runtime.getRuntime().exec( viewer.split( " " ) ) ;
			this.ps = new java.io.PrintStream( this.viewer.getOutputStream() ) ;

			ps.addPrintStream( this.ps ) ;

			r = true ;
		} catch ( Exception e ) {
			r = false ;
		}

		return r ;
	}
}
