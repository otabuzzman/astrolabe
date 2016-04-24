
package astrolabe;

import caa.CAADate;

public class Main {

	public static void main( String[] argv ) {
		Registry registry ;

		try {
			try {
				java.util.prefs.Preferences.importPreferences( new java.io.FileInputStream( "astrolabe.preferences" ) ) ;
			} catch ( java.io.FileNotFoundException exception ) {}

			registry = new Registry() ;

			// Read instance.
			astrolabe.model.Astrolabe al ;

			al = (astrolabe.model.Astrolabe) astrolabe.model.Astrolabe.unmarshal( new java.io.FileReader( argv[0] ) ) ;

			// Get epoch.
			CAADate epoch ;

			epoch = Model.condense( al.getDate() ) ;
			ReplacementHelper.registerYMD( "epoch", epoch ) ;

			// Chart processing.
			for ( int ch=0 ; ch<al.getChartCount() ; ch++ ) {				
				astrolabe.model.ChartType chT ;
				Chart chart = null ;
				PostscriptStream ps ;

				ps = new PostscriptStream( new PrintStream( System.out ) ) ;

				if ( ( chT = al.getChart( ch ).getChartStereographic() ) != null ) {
					chart = new ChartStereographic( chT, ps ) ;
				} else if ( ( chT = al.getChart( ch ).getChartOrthographic() ) != null ) {
					chart = new ChartOrthographic( chT, ps ) ;
				} else if ( ( chT = al.getChart( ch ).getChartEquidistant() ) != null ) {
					chart = new ChartEquidistant( chT, ps ) ;
				} else if ( ( chT = al.getChart( ch ).getChartGnomonic() ) != null ) {
					chart = new ChartGnomonic( chT, ps ) ;
				} else if ( ( chT = al.getChart( ch ).getChartEqualarea() ) != null ) {
					chart = new ChartEqualarea( chT, ps ) ;
				}

				ps.emitDCSHeader() ;
				ps.emitDCSProlog() ;

				ps.dcs.beginSetup() ;

				// Set origin at center of page.
				ps.pagesize() ;
				ps.operator.mul( .5 ) ;
				ps.operator.exch() ;
				ps.operator.mul( .5 ) ;
				ps.operator.exch() ;
				ps.operator.translate() ;

				ps.dcs.endSetup() ;

				chart.initPS( ps ) ;

				ps.dcs.page( chT.getName(), ch ) ;

				// Horizon processing.
				for ( int ho=0 ; ho<chT.getHorizonCount() ; ho++ ) {
					astrolabe.model.HorizonType hoT ;
					Horizon horizon = null ;

					ps.operator.gsave() ;

					if ( ( hoT = chT.getHorizon( ho ).getHorizonLocal() ) != null  ) {
						horizon = new HorizonLocal( hoT, epoch ) ;
					} else if ( ( hoT = chT.getHorizon( ho ).getHorizonEquatorial() ) != null  ) {
						horizon = new HorizonEquatorial( hoT ) ;
					} else if ( ( hoT = chT.getHorizon( ho ).getHorizonEcliptical() ) != null  ) {
						horizon = new HorizonEcliptical( hoT, epoch ) ;
					} else if ( ( hoT = chT.getHorizon( ho ).getHorizonGalactic() ) != null  ) {
						horizon = new HorizonGalactic( hoT ) ;
					}

					horizon.initPS( ps ) ;

					// Circle processing.
					for ( int cl=0 ; cl<hoT.getCircleCount() ; cl++ ) {
						astrolabe.model.CircleType clT ;
						Circle circle = null ;

						ps.operator.gsave() ;

						if ( ( clT = hoT.getCircle( cl ).getCircleParallel() ) != null ) {
							circle = new CircleParallel( clT, chart, horizon ) ;
						} else if ( ( clT = hoT.getCircle( cl ).getCircleMeridian() ) != null ) {
							circle = new CircleMeridian( clT, chart, horizon ) ;
						} else if ( ( clT = hoT.getCircle( cl ).getCircleSouthernPolar() ) != null ) {
							circle = new CircleSouthernPolar( clT, chart, horizon, epoch ) ;
						} else if ( ( clT = hoT.getCircle( cl ).getCircleNorthernPolar() ) != null ) {
							circle = new CircleNorthernPolar( clT, chart, horizon, epoch ) ;
						} else if ( ( clT = hoT.getCircle( cl ).getCircleSouthernTropic() ) != null ) {
							circle = new CircleSouthernTropic( clT, chart, horizon, epoch ) ;
						} else if ( ( clT = hoT.getCircle( cl ).getCircleNorthernTropic() ) != null ) {
							circle = new CircleNorthernTropic( clT, chart, horizon, epoch ) ;
						}

						try {
							registry.register( clT.getName(), circle ) ;
						} catch ( ParameterNotValidException e ) {}

						circle.initPS( ps ) ;

						java.util.Vector<astrolabe.Vector> vV ;
						java.util.Vector<double[]> vD ;

						vV = circle.cartesianList() ;
						vD = CircleHelper.convertCartesianVectorToDouble( vV ) ;
						ps.polyline( vD ) ;
						ps.operator.stroke() ;

						// Circle annotation processing.
						ps.operator.gsave() ;
						AnnotationHelper.emitPS( ps, clT.getAnnotation() ) ;
						ps.operator.grestore() ;

						// Dial processing.
						try {
							astrolabe.model.DialType dlT ;
							Dial dial = null ;

							if ( ( dlT = clT.getDial().getDialDegrees() ) != null ) {
								dial = new DialDegrees( dlT, circle ) ;
							} /* else if ( ( dlT = clT.getDial().getDialSunTrue() ) != null ) {
								dial = new DialSunTrue( dlT, circle ) ;
							} else if ( ( dlT = clT.getDial().getDialSunMean() ) != null ) {
								dial = new DialSunMean( dlT, circle ) ;
							} */

							ps.operator.gsave() ;
							dial.emitPS( ps ) ;

							// Dial annotation processing.
							ps.operator.gsave() ;
							AnnotationHelper.emitPS( ps, dlT.getAnnotation() ) ;
							ps.operator.grestore() ;

							ps.operator.grestore() ;
						} catch ( NullPointerException e ) {}

						ps.operator.grestore() ;
					} // Circle processing.

					ps.operator.grestore() ;
				} // Horizon processing.

				ps.operator.showpage() ;
				ps.dcs.pageTrailer() ;

				ps.dcs.trailer() ;
				ps.dcs.eOF() ;

				chart.rollup() ;
			} // Chart processing.

		} catch ( Exception exception ) {
			exception.printStackTrace() ;
			System.exit( 1 ) ;
		}

		System.exit( 0 ) ;
	} 

	public java.util.prefs.Preferences getClassNode( String instance, String qualifier ) {        
		String i = instance != null ? "/"+instance : "" ;
		String q = qualifier != null ? "/"+qualifier : "" ;
		String d = "/"+this.getClass().getName().replaceAll( "\\.", "/" ).split( "\\$", 2 )[0] ;
		String n = d+i+q ;

		try {
			if ( ! java.util.prefs.Preferences.systemRoot().nodeExists( n ) ) {
				n = d+q ;
			}
		} catch ( java.util.prefs.BackingStoreException e ) {}

		return java.util.prefs.Preferences.systemRoot().node( n ) ;
	} 

	public java.util.prefs.Preferences getNestedClassNode( String instance, String qualifier ) {        
		String q = qualifier != null ? "/"+qualifier : "" ;
		String c[] = this.getClass().getName().replaceAll( "\\.", "/" ).split( "\\$", 2 ) ;
		String s = c[1].replaceAll( "[$]", "/" ) ;
		String n = "/"+c[0]+( instance != null? "/"+instance+"/" : "/" )+s+q ;

		try {
			if ( ! java.util.prefs.Preferences.systemRoot().nodeExists( n ) ) {
				n = "/"+c[0]+"/"+s+q ;
			}
		} catch ( java.util.prefs.BackingStoreException e ) {}

		return java.util.prefs.Preferences.systemRoot().node( n ) ;
	} 

	public java.util.prefs.Preferences getPackageNode( String instance, String qualifier ) {        
		java.lang.Package p = this.getClass().getPackage() ;
		String i = instance != null ? "/"+instance : "" ;
		String q = qualifier != null ? "/"+qualifier : "" ;
		String d, n ;

		if ( p != null ) {
			d = "/"+p.getName().replaceAll( "\\.", "/" ) ;
			n = d+i+q ;
			try {
				if ( ! java.util.prefs.Preferences.systemRoot().nodeExists( n ) ) {
					n = d+q ;
				}
			} catch ( java.util.prefs.BackingStoreException e ) {}
		} else {
			d = "/"+"default" ;
			n = d+i+q ;
			try {
				if ( ! java.util.prefs.Preferences.systemRoot().nodeExists( n ) ) {
					n = d+q ;
				}
			} catch ( java.util.prefs.BackingStoreException e ) {}
		}

		return java.util.prefs.Preferences.systemRoot().node( n ) ;
	} 
}
