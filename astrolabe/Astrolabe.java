
package astrolabe;

import caa.CAADate;

public class Astrolabe extends Model {

	private astrolabe.model.AstrolabeType alT ;

	public Astrolabe( astrolabe.model.AstrolabeType alT ) {
		this.alT = alT ;
	}

	public void emitPS( PostscriptStream ps ) throws ParameterNotValidException {
		Registry registry ;
		CAADate epoch ;

		registry = new Registry() ;

		epoch = Model.condense( alT.getDate() ) ;
		ReplacementHelper.registerYMD( "epoch", epoch ) ;

		ps.emitDSCHeader() ; // DSCHeader.ps
		ps.emitDSCProlog() ; // DSCProlog.ps

		// Chart processing.
		for ( int ch=0 ; ch<alT.getChartCount() ; ch++ ) {				
			astrolabe.model.ChartType chT ;
			Chart chart = null ;

			if ( ( chT = alT.getChart( ch ).getChartStereographic() ) != null ) {
				chart = new ChartStereographic( chT, ps ) ;
			} else if ( ( chT = alT.getChart( ch ).getChartOrthographic() ) != null ) {
				chart = new ChartOrthographic( chT, ps ) ;
			} else if ( ( chT = alT.getChart( ch ).getChartEquidistant() ) != null ) {
				chart = new ChartEquidistant( chT, ps ) ;
			} else if ( ( chT = alT.getChart( ch ).getChartGnomonic() ) != null ) {
				chart = new ChartGnomonic( chT, ps ) ;
			} else if ( ( chT = alT.getChart( ch ).getChartEqualarea() ) != null ) {
				chart = new ChartEqualarea( chT, ps ) ;
			}

			chart.initPS( ps ) ;

			ps.dsc.page( chT.getName(), ch ) ;

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
						Quantity quantity = null ;

						if ( ( dlT = clT.getDial().getDialAngle() ) != null ) {
							quantity = new QuantityAngle( circle ) ;
						} else if ( ( dlT = clT.getDial().getDialTime() ) != null ) {
							quantity = new QuantityTime( circle ) ;
						} else if ( ( dlT = clT.getDial().getDialDate() ) != null ) {
							astrolabe.model.DialDate dlD ;
							Sun sun = null ;

							dlD = (astrolabe.model.DialDate) dlT ;

							if ( dlD.getSun().equals( "apparent" ) ) {
								sun = new SunApparent() ;
							} else if ( dlD.getSun().equals( "mean" ) ) {
								sun = new SunMean() ;
							} else if ( dlD.getSun().equals( "true" ) ) {
								sun = new SunTrue() ;
							}

							quantity = new QuantityDate( circle, sun, epoch ) ;
						}

						ps.operator.gsave() ;

						try {
							new Dial( dlT, circle, quantity ).emitPS( ps ) ;
						} catch ( Exception e ) {
							e.printStackTrace() ;
						}

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
			ps.dsc.pageTrailer() ;

			chart.rollup( ps ) ;
		} // Chart processing.

		ps.emitDSCTrailer() ; // DSCTrailer.ps
	}
}
