
package astrolabe;

public class ChartStereographic implements Chart {

	private double unit ;
	private boolean northern ;

	private double[] pagesize ;
	private double scale ;

	private Process viewer ;
	private java.io.PrintStream ps ;

	public ChartStereographic( astrolabe.model.ChartType chT, PostscriptStream ps ) {
		String pagesize ;

		unit = ApplicationHelper.getClassNode( this, chT.getName(), null ).getDouble( ApplicationConstant.PK_CHART_UNIT, 2.834646 ) ;

		pagesize = ApplicationHelper.getClassNode( this, chT.getName(), ApplicationConstant.PN_CHART_PAGESIZE ).get( chT.getPagesize(), "210x297" /*a4*/ ) ;
		this.pagesize = parsePagesize( pagesize ) ;

		scale = java.lang.Math.min( this.pagesize[0], this.pagesize[1] )/2/Math.goldensection ;
		scale = scale*chT.getScale()/100 ;

		northern = chT.getHemisphere().equals( ApplicationConstant.AV_CHART_NORTHERN ) ;

		setupViewerProcess( ApplicationHelper.getClassNode( this, chT.getName(), null ).get( ApplicationConstant.PK_CHART_VIEWER, null ), ps ) ;
	}

	public void emitPS( PostscriptStream ps ) throws ParameterNotValidException {
		ps.dsc.beginSetup() ;
		// Set origin at center of page.
		ps.custom( ApplicationConstant.PS_PROLOG_PAGESIZE ) ;
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

	public double[] parsePagesize( String pagesize ) {
		double[] r = new double[2] ;
		String[] ps ;

		ps = pagesize.split( "x" ) ;

		r[0] = new Double( ps[0] ).doubleValue() ;
		r[1] = new Double( ps[1] ).doubleValue() ;

		return r ;
	}

	public boolean setupViewerProcess( String command, PostscriptStream ps ) {
		boolean r ;
		String c ;
		int i ;

		try {
			c = new String( command ) ;

			while ( ( i=c.indexOf( '%' ) )>0 ) {
				switch ( c.charAt( i+1 ) ) {
				case 'w':
					c = c.substring( 0, i )+pagesize[0]*unit+c.substring( i+2, c.length() ) ;
					break ;
				case 'h':
					c = c.substring( 0, i )+pagesize[1]*unit+c.substring( i+2, c.length() ) ;
					break ;
				case 'p':
					String pagesize ;

					pagesize = this.pagesize[0]+"x"+this.pagesize[1] ;
					c = c.substring( 0, i )+pagesize+c.substring( i+2, c.length() ) ;
					break ;
				}
			}

			this.viewer = Runtime.getRuntime().exec( c.split( " " ) ) ;
			this.ps = new java.io.PrintStream( this.viewer.getOutputStream() ) ;

			ps.addPrintStream( this.ps ) ;

			this.viewer.getInputStream().close() ;
			this.viewer.getErrorStream().close() ;

			r = true ;
		} catch ( Exception e ) {
			r = false ;
		}

		return r ;
	}

	public void setNorthern( boolean northern ) {
		this.northern = northern ;
	}

	public void setPagesize( double[] pagesize ) {
		this.pagesize = pagesize ;
	}

	public void setScale( double scale ) {
		this.scale = scale ;
	}

	public void setUnit( double unit ) {
		this.unit = unit ;
	}
}
