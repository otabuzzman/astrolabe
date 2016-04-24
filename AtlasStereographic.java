
import java.io.File;
import java.io.FileReader;
import java.util.Hashtable;

import caa.CAACoordinateTransformation;
import astrolabe.ApplicationConstant;
import astrolabe.ApplicationHelper;
import astrolabe.Astrolabe;
import astrolabe.AstrolabeReader;
import astrolabe.ChartStereographic;
import astrolabe.Math;
import astrolabe.ParameterNotValidException;
import astrolabe.PostscriptStream;
import astrolabe.Registry;
import astrolabe.Vector;

@SuppressWarnings("serial")
public class AtlasStereographic {

	private final static String DEFAULT_PAGESIZE = "210x297" ;
	private final static String DEFAULT_PRACTICALITY	= "0" ;
	private final static String DEFAULT_IMPORTANCE		= ".1" ;

	private final static double DEFAULT_UNKNOWN = 1234.5678 ;

	private final static String PK_ATLAS_PAGESIZE	= "pagesize" ;
	private final static String PK_ATLAS_STARTRA	= "startRA" ;
	private final static String PK_ATLAS_SHIFTRA	= "shiftRA" ;
	private final static String PK_ATLAS_STARTDE	= "startDE" ;
	private final static String PK_ATLAS_SHIFTDE	= "shiftDE" ;
	private final static String PK_ATLAS_OVERLAPPINGX	= "overlappingX" ;
	private final static String PK_ATLAS_OVERLAPPINGY	= "overlappingY" ;
	private final static String PK_ATLAS_PRACTICALITY	= "practicality" ;
	private final static String PK_ATLAS_IMPORTANCE		= "importance" ;

	private static Hashtable<String, String> standardsize = new Hashtable<String, String>() ;
	static {
		standardsize.put( "a0", "840x1188" ) ;
		standardsize.put( "a1", "594x840" ) ;
		standardsize.put( "a2", "420x594" ) ;
		standardsize.put( "a3", "297x420" ) ;
		standardsize.put( "a4", "210x297" ) ;
		standardsize.put( "a5", "149x210" ) ;
		standardsize.put( "a6", "105x149" ) ;
	}

	private double[] atlassize = new double[2] ;

	private boolean northern ;

	private String practicality ;
	private String importance ;

	private double cx, cy ; // chart sizes
	private double scale ;

	private double a, b ; // relative page sizes
	private double ea, eb ; // a b extents in percent

	private double deA, deS ; // first (outermost) declination at deA, shift next by deD
	private double raA, raS ; // first (topmost) page row at raA, shift next by raD

	private double m90[] = new double[] { 0, -1, 0, 1, 0, 0, 0, 0, 1 } ; // rotation matrix, 90 degrees counter-clockwise
	private double m90c[] = new double[] { 0, 1, 0, -1, 0, 0, 0, 0, 1 } ; // rotation matrix, 90 degrees clockwise

	private ChartStereographic chart ;

	public AtlasStereographic( Object peer ) throws ParameterNotValidException {
		String[] chartsize ;

		chart = new ChartStereographic( peer ) ;

		chartsize = ApplicationHelper.getClassNode( chart, chart.getName(),
				ApplicationConstant.PN_CHART_PAGESIZE ).get( chart.getPagesize(), DEFAULT_PAGESIZE /*a4*/ ).split( "x" ) ;
		cx = new Double( chartsize[0] ).doubleValue() ;
		cy = new Double( chartsize[1] ).doubleValue() ;

		scale = java.lang.Math.min( cx, cy )/2/Math.goldensection ;
		scale = scale*chart.getScale()/100 ;

		northern = chart.getHemisphere().equals( ApplicationConstant.AV_CHART_NORTHERN ) ;

		String atlassize = astrolabe.ApplicationHelper.getClassNode( this, chart.getName(), null ).get( PK_ATLAS_PAGESIZE, null ) ;
		if ( atlassize==null ) {
			atlassize = astrolabe.ApplicationHelper.getClassNode( this, null, null ).get( PK_ATLAS_PAGESIZE, DEFAULT_PAGESIZE ) ;
		}
		if ( standardsize.containsKey( atlassize ) ) {
			atlassize = standardsize.get( atlassize ) ;
		}
		String[] axy = atlassize.split( "x" ) ;
		this.atlassize[0] = new Double( axy[0] ).doubleValue() ;
		this.atlassize[1] = new Double( axy[1] ).doubleValue() ;


		practicality = astrolabe.ApplicationHelper.getClassNode( this, chart.getName(), null ).get( PK_ATLAS_PRACTICALITY, null ) ;
		if ( practicality==null ) {
			practicality = astrolabe.ApplicationHelper.getClassNode( this, null, null ).get( PK_ATLAS_PRACTICALITY, DEFAULT_PRACTICALITY ) ;
		}
		importance = astrolabe.ApplicationHelper.getClassNode( this, chart.getName(), null ).get( PK_ATLAS_IMPORTANCE, null ) ;
		if ( importance==null ) {
			importance = astrolabe.ApplicationHelper.getClassNode( this, null, null ).get( PK_ATLAS_IMPORTANCE, DEFAULT_IMPORTANCE ) ;
		}

		raA = astrolabe.ApplicationHelper.getClassNode( this, chart.getName(), null ).getDouble( PK_ATLAS_STARTRA, DEFAULT_UNKNOWN ) ;
		if ( raA==DEFAULT_UNKNOWN ) {
			raA = astrolabe.ApplicationHelper.getClassNode( this, null, null ).getDouble( PK_ATLAS_STARTRA, 0 ) ;
		}
		raA = CAACoordinateTransformation.HoursToRadians( raA ) ;
		raS = astrolabe.ApplicationHelper.getClassNode( this, chart.getName(), null ).getDouble( PK_ATLAS_SHIFTRA, DEFAULT_UNKNOWN ) ;
		if ( raS==DEFAULT_UNKNOWN ) {
			raS = astrolabe.ApplicationHelper.getClassNode( this, null, null ).getDouble( PK_ATLAS_SHIFTRA, 0 ) ;
		}
		raS = CAACoordinateTransformation.HoursToRadians( raS ) ;

		deA = astrolabe.ApplicationHelper.getClassNode( this, chart.getName(), null ).getDouble( PK_ATLAS_STARTDE, DEFAULT_UNKNOWN ) ;
		if ( deA==DEFAULT_UNKNOWN ) {
			deA = astrolabe.ApplicationHelper.getClassNode( this, null, null ).getDouble( PK_ATLAS_STARTDE, 0 ) ;
		}
		deA = CAACoordinateTransformation.DegreesToRadians( deA ) ;
		deS = astrolabe.ApplicationHelper.getClassNode( this, chart.getName(), null ).getDouble( PK_ATLAS_SHIFTDE, DEFAULT_UNKNOWN ) ;
		if ( deS==DEFAULT_UNKNOWN ) {
			deS = astrolabe.ApplicationHelper.getClassNode( this, null, null ).getDouble( PK_ATLAS_SHIFTDE, 0 ) ;
		}
		deS = CAACoordinateTransformation.DegreesToRadians( deS ) ;
		deS = northern?deS:-deS ;

		ea = astrolabe.ApplicationHelper.getClassNode( this, chart.getName(), null ).getDouble( PK_ATLAS_OVERLAPPINGX, DEFAULT_UNKNOWN ) ;
		if ( ea==DEFAULT_UNKNOWN ) {
			ea = astrolabe.ApplicationHelper.getClassNode( this, null, null ).getDouble( PK_ATLAS_OVERLAPPINGX, 0 ) ;
		}
		eb = astrolabe.ApplicationHelper.getClassNode( this, chart.getName(), null ).getDouble( PK_ATLAS_OVERLAPPINGY, DEFAULT_UNKNOWN ) ;
		if ( eb==DEFAULT_UNKNOWN ) {
			eb = astrolabe.ApplicationHelper.getClassNode( this, null, null ).getDouble( PK_ATLAS_OVERLAPPINGY, 0 ) ;
		}
	}

	public void headPS( PostscriptStream ps ) {
		ps.emitDSCHeader() ;
		ps.emitDSCProlog() ;

		chart.headPS( ps ) ;

		ApplicationHelper.emitPSPracticality( ps, practicality ) ;
		ApplicationHelper.emitPSImportance( ps, importance ) ;
	}

	public void emitPS( PostscriptStream ps ) {
		int nra, nde ; // number of pages per single declinaton, number of page rows
		double rad, tan ; // |v0| (radius), tan derived from a and ea
		java.util.Vector<String> info = new java.util.Vector<String>() ;
		java.util.Vector<Vector> page ;
		double de, ra, dim[], s ;
		double[] p0xy, p1xy, p2xy, p3xy ;
		double[] p0eq, p1eq, p2eq, p3eq, oeq, beq ;
		Vector vp, vc, vb ;

		p0xy = new double[2] ;
		p1xy = new double[2] ;
		p2xy = new double[2] ;
		p3xy = new double[2] ;

		nde = (int) ( ( chart.unproject( 0, 0 )[1]-deA )/deS )+1 ;
		de = deA+nde*deS ;

		for ( int cde=0 ; cde<nde ; cde++ ) { 
			de = de-deS ;

			rad = chart.project( 0, de )[0] ;
			b = rad-chart.project( 0, de+deS )[0] ;
			a = b*( 1+eb/100. )*( cx/cy ) ;

			tan = ( a/2*( 1-ea/100. ) )/rad ;
			nra = (int) ( Math.rad180/java.lang.Math.atan( tan ) ) ;

			for ( int cra=0 ; cra<nra ; cra++ ) {
				ra = raA+raS*cde+Math.rad360/nra*cra ;

				ps.operator.mark() ;
				page = page( ra, de ) ;
				for ( int p=0 ; p<page.size() ; p++ ) {
					vp = page.get( p ) ;
					ps.push( vp.x ) ;
					ps.push( vp.y ) ;
				}
				try {
					ps.custom( ApplicationConstant.PS_PROLOG_POLYLINE ) ;

					ps.operator.closepath() ;
					ps.operator.stroke() ;
				} catch ( ParameterNotValidException e ) {
					throw new RuntimeException( e.toString() ) ;
				}

				// atlas page cartesian outline in chart page coords
				vp = page.get( 0 ) ;
				p0xy[0] = vp.x ;
				p0xy[1] = vp.y ;
				vp = page.get( 1 ) ;
				p1xy[0] = vp.x ;
				p1xy[1] = vp.y ;
				vp = page.get( 2 ) ;
				p2xy[0] = vp.x ;
				p2xy[1] = vp.y ;
				vp = page.get( 3 ) ;
				p3xy[0] = vp.x ;
				p3xy[1] = vp.y ;

				// atlas page equatorial bounds
				p0eq = chart.unproject( p0xy ) ;
				p0eq[0] = CAACoordinateTransformation.RadiansToDegrees( p0eq[0] ) ;
				p0eq[1] = CAACoordinateTransformation.RadiansToDegrees( p0eq[1] ) ;
				p1eq = chart.unproject( p1xy ) ;
				p1eq[0] = CAACoordinateTransformation.RadiansToDegrees( p1eq[0] ) ;
				p1eq[1] = CAACoordinateTransformation.RadiansToDegrees( p1eq[1] ) ;
				p2eq = chart.unproject( p2xy ) ;
				p2eq[0] = CAACoordinateTransformation.RadiansToDegrees( p2eq[0] ) ;
				p2eq[1] = CAACoordinateTransformation.RadiansToDegrees( p2eq[1] ) ;
				p3eq = chart.unproject( p3xy ) ;
				p3eq[0] = CAACoordinateTransformation.RadiansToDegrees( p3eq[0] ) ;
				p3eq[1] = CAACoordinateTransformation.RadiansToDegrees( p3eq[1] ) ;

				// atlas page equatorial center (origin)
				vc = center( page ) ;
				oeq = chart.unproject( vc.x, vc.y ) ;
				oeq[0] = CAACoordinateTransformation.RadiansToDegrees( oeq[0] ) ;
				oeq[1] = CAACoordinateTransformation.RadiansToDegrees( oeq[1] ) ;

				// atlas page outmost RA
				dim = edges( page ) ;
				vb = new Vector( vc ) ;
				vb.scale( vb.abs()+dim[1]/2 ) ;
				beq = chart.unproject( vb.x, vb.y ) ;
				beq[0] = CAACoordinateTransformation.RadiansToDegrees( beq[0] ) ;
				beq[1] = CAACoordinateTransformation.RadiansToDegrees( beq[1] ) ;

				// atlas page scale
				s = atlassize[0]/dim[0]*100 ;

				info.add( " "+cra+" "+cde
						+" "+truncate( p0xy[0], 2 )
						+" "+truncate( p0xy[1], 2 )
						+" "+truncate( p1xy[0], 2 )
						+" "+truncate( p1xy[1], 2 )
						+" "+truncate( p2xy[0], 2 )
						+" "+truncate( p2xy[1], 2 )
						+" "+truncate( p3xy[0], 2 )
						+" "+truncate( p3xy[1], 2 )
						+" "+truncate( p0eq[0], 2 )
						+" "+truncate( p0eq[1], 2 )
						+" "+truncate( p1eq[0], 2 )
						+" "+truncate( p1eq[1], 2 )
						+" "+truncate( p2eq[0], 2 )
						+" "+truncate( p2eq[1], 2 )
						+" "+truncate( p3eq[0], 2 )
						+" "+truncate( p3eq[1], 2 )
						+" "+truncate( oeq[0], 2 )
						+" "+truncate( oeq[1], 2 )
						+" "+truncate( beq[0], 2 )
						+" "+truncate( beq[1], 2 )
						+" "+truncate( s, 0 )
				) ;
			}
		}

		for ( String line : info ) {
			ps.comment( line ) ;
		}
	}

	public void tailPS( PostscriptStream ps ) {	
		chart.tailPS( ps ) ;

		ps.emitDSCTrailer() ;
	}

	public java.util.Vector<Vector> page( double ra, double de ) {
		java.util.Vector<Vector> r = new java.util.Vector<Vector>() ;
		Vector v0, v1, v2, v3, v4 ; // radius vector v0, edge vectors
		Vector vp0, vp1, vp2, vp3 ; // point vectors
		double[] xy ;

		xy = chart.project( ra, de ) ;
		v0 = new Vector( xy[0], xy[1] ) ;

		v1 = new Vector( v0 )
		.mul( -1 )
		.apply( m90 )
		.scale( ( northern?a:-a )/2 ) ;

		vp0 = new Vector( v0 )
		.add( v1 ) ;

		v2 = new Vector( v1 )
		.mul( -1 )
		.apply( m90 )
		.scale( 2*v1.abs()/( cx/cy ) ) ;

		vp1 = new Vector( vp0 )
		.add( v2 ) ;

		v3 = new Vector( v0 )
		.mul( -1 )
		.apply( m90c )
		.scale( ( northern?a:-a )/2 ) ;

		vp3 = new Vector( v0 )
		.add( v3 ) ;

		v4 = new Vector( v3 )
		.mul( -1 )
		.apply( m90c )
		.scale( 2*v3.abs()/( cx/cy ) ) ;

		vp2 = new Vector( vp3 )
		.add( v4 ) ;

		r.add( vp0 ) ;
		r.add( vp1 ) ;
		r.add( vp2 ) ;
		r.add( vp3 ) ;

		return r ;
	}

	public Vector center( java.util.Vector<Vector> page ) {
		Vector v0, v2, v20, vc ;

		v0 = page.get( 0 ) ;
		v2 = page.get( 2 ) ;

		v20 = new Vector( v0 ).sub( v2 ) ;
		v20.scale( v20.abs()/2 ) ;

		vc = new Vector( v2 ).add( v20 ) ;

		return vc ;
	}

	public double[] edges( java.util.Vector<Vector> page ) {
		double[] r = new double[2] ;
		Vector v0, va, vb ;

		v0 = page.get( 0 ) ;
		va = new Vector( page.get( 3 ) ) ;
		va.sub( v0 ) ;
		vb = new Vector( page.get( 1 ) ) ;
		vb.sub( v0 ) ;

		r[0] = va.abs() ;
		r[1] = vb.abs() ;

		return r ;
	}

	private static double truncate( double v, int e10 ) {
		double r, d ;

		d = java.lang.Math.pow( 10, e10 ) ;
		r = (long) ( v*d )/d ;

		return r ;
	}

	public static void main( String[] argv ) {
		File f ;
		FileReader m ;
		Astrolabe a ;
		PostscriptStream ps ;
		AtlasStereographic atlas ;

		try {
			f = new File( argv[0] ) ;
			m = new FileReader( f ) ;
			a = new AstrolabeReader().read( m ) ;

			ps = Astrolabe.initPS() ;

			for ( astrolabe.model.Chart chart : a.getChart() ) {
				if ( chart.getChartStereographic().getName() == null ) {
					chart.getChartStereographic().setName( f.getName().split( "\\." )[0]+"-"+
							chart.getChartStereographic().getHemisphere() ) ;
				}

				atlas = new AtlasStereographic( chart.getChartStereographic() ) ;

				atlas.headPS( ps ) ;
				atlas.emitPS( ps ) ;
				atlas.tailPS( ps ) ;
			}

			Registry.remove() ;
		} catch ( Exception e ) {
			e.printStackTrace() ;
			System.exit( 1 ) ;
		}

		System.exit( 0 ) ;
	}
}
