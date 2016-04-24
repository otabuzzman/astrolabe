
package astrolabe;

import org.exolab.castor.xml.ValidationException;

import caa.CAADate;

@SuppressWarnings("serial")
public class BodyMoon extends astrolabe.model.BodyMoon implements Body, Baseline {

	private final static double DEFAULT_SEGMENT = 1 ;
	private final static double DEFAULT_STRETCH = 0 ;
	private final static double DEFAULT_LINEWIDTH = .72 ;

	private Projector projector ;

	private double segment ;
	private double stretch ;
	private double linewidth ;

	private double jdAy ;
	private double jdOy ;

	public BodyMoon( Object peer, double epoch, Projector projector ) throws ParameterNotValidException {
		CAADate d = new CAADate() ;
		long y ;

		ApplicationHelper.setupCompanionFromPeer( this, peer ) ;
		try {
			validate() ;
		} catch ( ValidationException e ) {
			throw new ParameterNotValidException( e.toString() ) ;
		}

		d.Set( epoch, true ) ;
		y = d.Year() ;
		d.Set( y, 1, 1, 0, 0, 0, true ) ;
		jdAy = d.Julian() ;
		d.Set( y, 12, 31, 0, 0, 0, true ) ;
		jdOy = d.Julian() ;
		d.delete() ;

		this.projector = projector ;

		segment = ApplicationHelper.getClassNode( this,
				getName(), null ).getDouble( ApplicationConstant.PK_BODY_SEGMENT, DEFAULT_SEGMENT ) ;
		stretch = ApplicationHelper.getClassNode( this,
				getName(), null ).getDouble( ApplicationConstant.PK_BODY_STRETCH, DEFAULT_STRETCH ) ;
		linewidth = ApplicationHelper.getClassNode( this,
				getName(), null ).getDouble( ApplicationConstant.PK_BODY_LINEWIDTH, DEFAULT_LINEWIDTH ) ;
	}

	public void headPS( PostscriptStream ps ) {
		ps.operator.setlinewidth( linewidth ) ;
	}

	public void emitPS( PostscriptStream ps ) {
		java.util.Vector<double[]> v ;
		double[] xy ;

		v = list( jdAy, jdOy, 0 ) ;
		ps.operator.mark() ;
		for ( int n=v.size() ; n>0 ; n-- ) {
			xy = (double[]) v.get( n-1 ) ;
			ps.push( xy[0] ) ;
			ps.push( xy[1] ) ;
		}
		try {
			ps.custom( ApplicationConstant.PS_PROLOG_POLYLINE ) ;

			ps.operator.gsave() ;
			ps.operator.setlinecap( 2 ) ;
			ps.custom( ApplicationConstant.PS_PROLOG_HALOSTROKE ) ;
			ps.operator.grestore() ;

		} catch ( ParameterNotValidException e ) {
			throw new RuntimeException( e.toString() ) ;
		}
		ps.operator.gsave() ;
		ps.operator.stroke() ;
		ps.operator.grestore() ;

		// Dial processing.
		if ( getDialDay() != null ) {
			Dial dial ;

			ps.operator.gsave() ;

			try {
				dial = new DialDay( getDialDay(), this ) ;
				dial.headPS( ps ) ;
				dial.emitPS( ps ) ;
				dial.tailPS( ps ) ;
			} catch ( ParameterNotValidException e ) {} // DialDay validated in constructor

			ps.operator.grestore() ;
		}

		if ( getAnnotation() != null ) {
			try {
				ApplicationHelper.emitPS( ps, getAnnotation() ) ;
			} catch ( ParameterNotValidException e ) {
				throw new RuntimeException( e.toString() ) ;
			}
		}
	}

	public void tailPS( PostscriptStream ps ) {
	}

	public double[] project(double jd) {
		return project( jd, 0 ) ;
	}

	public double[] project( double jd, double shift ) {
		double[] ec, xy ;
		Vector v, t ;

		ec = new double[2] ;
		ec[0] = ApplicationHelper.moonEclipticLongitude( jd ) ;
		ec[1] = ApplicationHelper.moonEclipticLatitude( jd ) ;
		ec[1] = ec[1]+( jd-jdAy )*Math.rad90/90*stretch ;
		xy = projector.project( ec ) ;
		v = new Vector( xy[0], xy[1] ) ;

		if ( shift != 0 ) {
			xy = tangent( jd ) ;
			t = new Vector( xy[0], xy[1] ) ;
			t.apply( new double[] { 0, -1, 0, 1, 0, 0, 0, 0, 1 } ) ; // rotate 90 degrees counter clockwise
			t.scale( shift ) ;
			v.add( t ) ;
		}

		return new double[] { v.x, v.y } ;
	}

	public double[] tangent( double jd ) {
		double[] ec, xy ;
		Vector v, t ;

		ec = new double[2] ;
		ec[0] = ApplicationHelper.moonEclipticLongitude( jd+1./86400 ) ;
		ec[1] = ApplicationHelper.moonEclipticLatitude( jd+1./86400 ) ;
		xy = projector.project( ec ) ;
		v = new Vector( xy[0], xy[1] ) ;
		ec[0] = ApplicationHelper.moonEclipticLongitude( jd ) ;
		ec[1] = ApplicationHelper.moonEclipticLatitude( jd ) ;
		xy = projector.project( ec ) ;
		t = new Vector( xy[0], xy[1] ) ;

		v.sub( t ) ;

		return new double[] { v.x, v.y } ;
	}

	public java.util.Vector<double[]> list() {
		return list( jdAy, jdOy, 0 ) ;
	}

	public java.util.Vector<double[]> list( double shift ) {
		return list( jdAy, jdOy, shift ) ;
	}

	public java.util.Vector<double[]> list( double jdA, double jdO, double shift ) {
		java.util.Vector<double[]> r = new java.util.Vector<double[]>() ;
		double g ;

		r.add( project( jdA, shift ) ) ;

		g = mapIndexToRange( jdA, jdO ) ;
		for ( double jd=jdA+g ; jd<jdO ; jd=jd+segment ) {
			r.add( project( jd, shift ) ) ;
		}

		r.add( project( jdO, shift ) ) ;

		return r ;
	}

	public boolean probe( double jd ) {
		return jd>=jdAy&&jd<=jdOy ;
	}

	public double mapIndexToScale( int index ) {
		return mapIndexToScale( index, segment, jdAy, jdOy ) ;
	}

	public double mapIndexToScale( double span ) {
		return mapIndexToScale( 0, span, jdAy, jdOy ) ;
	}

	public double mapIndexToScale( int index, double span ) {
		return mapIndexToScale( index, span, jdAy, jdOy ) ;
	}

	private static double mapIndexToScale( int index, double span, double jdA, double jdO ) {
		return index<0?jdO:jdA+index*span ;
	}
	public double mapIndexToRange() {
		return BodyPlanet.gap( 0, segment, jdAy , jdOy ) ;
	}

	public double mapIndexToRange( double jdA, double jdO ) {
		return BodyPlanet.gap( 0, segment, jdA , jdO ) ;
	}

	public double mapIndexToRange( int index, double jdA, double jdO ) {
		return BodyPlanet.gap( index, segment, jdA , jdO ) ;
	}
}
