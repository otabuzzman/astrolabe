
package astrolabe;

public class ChartStereographic implements Chart {

	private Astrolabe astrolabe ;

	private double unit ;
	private boolean northern ;

	private double[] pagesize ;
	private double scale ;

	private Process viewer ;
	private java.io.PrintStream ps ;

	private String name ;

	public ChartStereographic( astrolabe.model.ChartType chT, Astrolabe astrolabe ) {
		String pagesize ;

		this.astrolabe = astrolabe ;

		name = chT.getName() ;

		pagesize = ApplicationHelper.getClassNode( this, name, ApplicationConstant.PN_CHART_PAGESIZE ).get( chT.getPagesize(), "210x297" /*a4*/ ) ;
		this.pagesize = parsePagesize( pagesize ) ;

		unit = ApplicationHelper.getClassNode( this, name, null ).getDouble( ApplicationConstant.PK_CHART_UNIT, 2.834646 ) ;
		scale = java.lang.Math.min( this.pagesize[0], this.pagesize[1] )/2/Math.goldensection ;
		scale = scale*chT.getScale()/100 ;

		northern = chT.getHemisphere().equals( ApplicationConstant.AV_CHART_NORTHERN ) ;
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

	public boolean viewer( PostscriptStream ps ) {
		boolean r ;
		String c ;
		int i ;

		try {
			c = ApplicationHelper.getClassNode( this, name, null ).get( ApplicationConstant.PK_CHART_VIEWER, null ) ;

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

	public Astrolabe dotDot() {
		return astrolabe ;
	}
}
