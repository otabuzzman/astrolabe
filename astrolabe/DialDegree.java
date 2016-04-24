
package astrolabe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("serial")
public class DialDegree extends astrolabe.model.DialDegree implements Dial {

	@SuppressWarnings("unused")
	private final static double DEFAULT_NONE_SPACE = .1 ;
	@SuppressWarnings("unused")
	private final static double DEFAULT_NONE_THICKNESS = 0 ;
	@SuppressWarnings("unused")
	private final static double DEFAULT_NONE_LINEWIDTH = 0 ;

	@SuppressWarnings("unused")
	private final static double DEFAULT_LINE_SPACE = 1 ;
	@SuppressWarnings("unused")
	private final static double DEFAULT_LINE_THICKNESS = .2 ;
	@SuppressWarnings("unused")
	private final static double DEFAULT_LINE_LINEWIDTH = 0 ;

	@SuppressWarnings("unused")
	private final static double DEFAULT_RAIL_SPACE = 1 ;
	@SuppressWarnings("unused")
	private final static double DEFAULT_RAIL_THICKNESS = 1.2 ;
	@SuppressWarnings("unused")
	private final static double DEFAULT_RAIL_LINEWIDTH = .01 ;

	private final static double DEFAULT_RISE = 3.2 ;

	private Circle circle ;

	private Method headPSBaseline ;
	private Method emitPSBaseline ;
	private Method tailPSBaseline ;

	private double rise ;

	private double space ;
	private double thickness ;
	private double linewidth ;

	private double unit ;

	protected DialDegree () {
	}

	public DialDegree( astrolabe.model.DialDegree peer, Circle circle ) {
		setup( peer, circle, Math.rad1 ) ;
	}

	public DialDegree( astrolabe.model.DialDegree peer, Circle circle, double unit ) {
		setup( peer, circle, unit ) ;
	}

	public void setup( Object peer, Circle circle, double unit ) {
		ApplicationHelper.setupCompanionFromPeer( this, peer ) ;

		this.circle = circle ;	
		this.unit = unit ;

		try {
			Class c ;
			String bl, blm, blf, node ;
			double ds, dt, dl ;

			c = Class.forName( "astrolabe.DialDegree" ) ;

			bl = getBaseline() ;
			blm = bl.replaceFirst( ".", bl.substring( 0, 1 ).toUpperCase() ) ;
			headPSBaseline = c.getDeclaredMethod( "headPSBaseline"+blm, new Class[] { Class.forName( "astrolabe.PostscriptStream" ) } ) ;
			emitPSBaseline = c.getDeclaredMethod( "emitPSBaseline"+blm, new Class[] { Class.forName( "astrolabe.PostscriptStream" ) } ) ;
			tailPSBaseline = c.getDeclaredMethod( "tailPSBaseline"+blm, new Class[] { Class.forName( "astrolabe.PostscriptStream" ) } ) ;

			blf = blm.toUpperCase() ;
			ds = c.getDeclaredField( "DEFAULT_"+blf+"_SPACE" ).getDouble( this ) ;
			dt = c.getDeclaredField( "DEFAULT_"+blf+"_THICKNESS" ).getDouble( this ) ;
			dl = c.getDeclaredField( "DEFAULT_"+blf+"_LINEWIDTH" ).getDouble( this ) ;

			node = ApplicationConstant.PN_DIAL_BASELINE+"/"+blm ;
			space = ApplicationHelper.getClassNode( this, getName(),
					node ).getDouble( ApplicationConstant.PK_DIAL_SPACE, ds ) ;
			thickness = ApplicationHelper.getClassNode( this, getName(),
					node ).getDouble( ApplicationConstant.PK_DIAL_THICKNESS, dt ) ;
			linewidth = ApplicationHelper.getClassNode( this, getName(),
					node ).getDouble( ApplicationConstant.PK_DIAL_LINEWIDTH, dl ) ;
		} catch ( ClassNotFoundException e ) {
		} catch ( NoSuchMethodException e ) {
			e.printStackTrace() ;
		} catch ( NoSuchFieldException e ) {
			e.printStackTrace() ;
		} catch ( IllegalAccessException e ) {
			e.printStackTrace() ;
		}

		rise = ApplicationHelper.getClassNode( this, getName(),
				ApplicationConstant.PN_DIAL_ANNOTATION ).getDouble( ApplicationConstant.PK_DIAL_RISE, DEFAULT_RISE ) ;
	}

	public void headPS( PostscriptStream ps ) {
		try {
			headPSBaseline.invoke( this, new Object[] { ps } ) ;
		} catch ( IllegalAccessException e ) {
		} catch ( InvocationTargetException e ) {
		}
	}

	public void emitPS( PostscriptStream ps ) throws ParameterNotValidException {
		java.util.Vector<double[]> v ;
		double[] xy ;

		try {
			emitPSBaseline.invoke( this, new Object[] { ps } ) ;
		} catch ( IllegalAccessException e ) {
		} catch ( InvocationTargetException e ) {
		}

		emitPSGraduation( ps ) ;

		v = circle.list( getReflect()?-( ( space+thickness )+rise ):( space+thickness )+rise ) ;
		ps.operator.mark() ;
		for ( int c=v.size() ; c>0 ; c-- ) {
			xy = (double[]) v.get( c-1 ) ;
			ps.push( xy[0] ) ;
			ps.push( xy[1] ) ;
		}
		ps.custom( ApplicationConstant.PS_PROLOG_POLYLINE ) ;
	}

	public void tailPS( PostscriptStream ps ) {
		try {
			tailPSBaseline.invoke( this, new Object[] { ps } ) ;
		} catch ( IllegalAccessException e ) {
		} catch ( InvocationTargetException e ) {
		}
	}

	@SuppressWarnings("unused")
	private void headPSBaselineNone( PostscriptStream ps ) {
	}

	@SuppressWarnings("unused")
	private void headPSBaselineLine( PostscriptStream ps ) {
		ps.operator.setlinewidth( thickness ) ;
	}

	@SuppressWarnings("unused")
	private void headPSBaselineRail( PostscriptStream ps ) {
		ps.operator.setlinewidth( linewidth ) ;
	}

	@SuppressWarnings("unused")
	private void emitPSBaselineNone( PostscriptStream ps ) {
	}

	@SuppressWarnings("unused")
	private void emitPSBaselineLine( PostscriptStream ps ) throws ParameterNotValidException {
		java.util.Vector<double[]> v ;
		double b, e ;
		int ns ;

		v = new java.util.Vector<double[]>() ;

		try { // baseline
			for ( ns=0 ; ; ns++ ) {
				b = mapIndexToAngleOfScale( ns, getGraduationSpan().getSpan() ) ;
				e = mapIndexToAngleOfScale( ns+1, getGraduationSpan().getSpan() ) ;

				// in case that quantity handles dates this happens on turn of the year
				if ( e<b ) {
					continue ;
				}

				v.addAll( circle.list( b, e, getReflect()?-space:space ) ) ;
			}
		} catch ( ParameterNotValidException ePNV ) {
			double[] xy ;

			ps.operator.mark() ;
			for ( int c=v.size() ; c>0 ; c-- ) {
				xy = (double[]) v.get( c-1 ) ;
				ps.push( xy[0] ) ;
				ps.push( xy[1] ) ;
			}
			ps.custom( ApplicationConstant.PS_PROLOG_POLYLINE ) ;
			ps.operator.stroke() ;
		}
	}

	@SuppressWarnings("unused")
	private void emitPSBaselineRail( PostscriptStream ps ) throws ParameterNotValidException {
		java.util.Vector<double[]> vDFw = null, vDRv = null ;
		double b, e, s, span ;
		int nss = 0 ;

		span = getGraduationSpan().getSpan()/getGraduationSpan().getDivision() ;

		try { // baseline
			for ( ; ; nss++ ) {
				b = mapIndexToAngleOfScale( nss, span ) ;
				e = mapIndexToAngleOfScale( nss+1, span ) ;

				// happens on turn of the year
				if ( e<b ) {
					continue ;
				}

				s = nss%2==0?space:space+linewidth/2 ;
				s = getReflect()?-s:s ;			
				vDFw = circle.list( b, e, s ) ;

				s = space+( nss%2==0?thickness:thickness-linewidth/2 ) ;
				s = getReflect()?-s:s ;
				vDRv = circle.list( b, e, s ) ;

				vDRv = ApplicationHelper.reverseVector( vDRv ) ;
				vDFw.addAll( vDRv ) ;

				if ( nss%2 == 0 ) { // subunit filled
					double[] xy ;

					ps.operator.mark() ;
					for ( int c=vDFw.size() ; c>0 ; c-- ) {
						xy = (double[]) vDFw.get( c-1 ) ;
						ps.push( xy[0] ) ;
						ps.push( xy[1] ) ;
					}
					ps.custom( ApplicationConstant.PS_PROLOG_POLYLINE ) ;
					ps.operator.closepath() ;
					ps.operator.fill() ;
				} else { // subunit unfilled
					java.util.Vector<double[]> fw, rv ;
					double[] xy ;

					fw = new java.util.Vector<double[]>( vDFw.subList( 0, vDFw.size()/2 ) ) ;
					rv = new java.util.Vector<double[]>( vDFw.subList( vDFw.size()/2, vDFw.size() ) ) ;

					ps.operator.mark() ;
					for ( int c=fw.size() ; c>0 ; c-- ) {
						xy = (double[]) fw.get( c-1 ) ;
						ps.push( xy[0] ) ;
						ps.push( xy[1] ) ;
					}
					ps.custom( ApplicationConstant.PS_PROLOG_POLYLINE ) ;
					ps.operator.stroke() ;
					ps.operator.mark() ;
					for ( int c=rv.size() ; c>0 ; c-- ) {
						xy = (double[]) rv.get( c-1 ) ;
						ps.push( xy[0] ) ;
						ps.push( xy[1] ) ;
					}
					ps.custom( ApplicationConstant.PS_PROLOG_POLYLINE ) ;
					ps.operator.stroke() ;
				}
			}
		} catch ( ParameterNotValidException ePNV ) {
			if ( nss%2 == 0 ) { // close unfilled subunit
				java.util.Vector<double[]> vector ;
				double[] xy ;

				vector = new java.util.Vector<double[]>( vDFw.subList( vDFw.size()/2-1, vDFw.size()/2+1 ) ) ;

				xy = (double[]) vector.get( 1 ) ;
				ps.push( xy[0] ) ;
				ps.push( xy[1] ) ;
				xy = (double[]) vector.get( 0 ) ;
				ps.push( xy[0] ) ;
				ps.push( xy[1] ) ;
				ps.custom( ApplicationConstant.PS_PROLOG_LINE ) ;
				ps.operator.stroke() ;
			}
		}
	}

	@SuppressWarnings("unused")
	private void tailPSBaselineNone( PostscriptStream ps ) {
	}

	@SuppressWarnings("unused")
	private void tailPSBaselineLine( PostscriptStream ps ) {
	}

	@SuppressWarnings("unused")
	private void tailPSBaselineRail( PostscriptStream ps ) {
	}

	private void emitPSGraduation( PostscriptStream ps ) throws ParameterNotValidException {
		int ns ;
		double a ;
		double[] o, t ;
		Graduation g ;


		for ( ns=1 ; ; ns++ ) {
			try {
				a = mapIndexToAngleOfScale( ns, getGraduationSpan().getSpan() ) ;
			} catch ( ParameterNotValidException e ) {
				break ;
			}

			register( ns ) ;

			o = circle.project( a, getReflect()?-( space+thickness ):space+thickness ) ;
			t = circle.tangent( a ) ;
			if ( getReflect() ) {
				t[0] = -t[0] ;
				t[1] = -t[1] ;
			}

			g = new GraduationSpan( getGraduationSpan(), o, t ) ;
			try { // half
				if ( isIndexAligned( ns, getGraduationHalf().getSpan() ) ) {
					g = new GraduationHalf( getGraduationHalf(), o, t ) ;
				}
			} catch ( NullPointerException e ) {}
			try { // full
				if ( isIndexAligned( ns, getGraduationFull().getSpan() ) ) {
					g = new GraduationFull( getGraduationFull(), o, t ) ;
				}
			} catch ( NullPointerException e ) {}

			ps.operator.gsave() ;

			g.headPS( ps ) ;
			g.emitPS( ps ) ;
			g.tailPS( ps ) ;

			ps.operator.grestore() ;
		}
	}

	public double mapIndexToAngleOfScale( int index, double span ) throws ParameterNotValidException {
		double r ;

		r = circle.mapIndexToAngleOfScale( index, span*unit ) ;
		if ( ! circle.probe( r ) || r>Math.rad360 ) {
			throw new ParameterNotValidException() ;
		}

		return r ;
	}

	public boolean isIndexAligned( int index, double span ) {
		double a, b ;

		a = circle.mapIndexToAngleOfScale( index, getGraduationSpan().getSpan()*unit ) ;
		b = span*unit ;

		return Math.isLim0( a-(int) ( a/b )*b ) ;
	}

	public void register( int index ) {
		double a ;
		String key ;

		try {
			a = circle.mapIndexToAngleOfScale( index, getGraduationSpan().getSpan()*unit ) ;

			key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_DIAL_DEGREE ) ;
			ApplicationHelper.registerDMS( key, a, 2 ) ;
			key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_DIAL_HOUR ) ;
			ApplicationHelper.registerTime( key, a, 2 ) ;
			key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_DIAL_AZIMUTHTIME ) ;
			ApplicationHelper.registerTime( key, a+Math.rad180/*12h*/, 2 ) ;
		} catch ( ParameterNotValidException  e ) {}
	}
}
