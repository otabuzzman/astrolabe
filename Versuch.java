import astrolabe.Circle;
import astrolabe.document.Postscript;
import astrolabe.utilities.Sphere;
import caa.CAACoordinateTransformation;
import caa.CAADate;

/*
 * Created on 15.06.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author jschuck
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Versuch extends astrolabe.Common {
	public void name() {
		System.out.println( this.getClass().getName() ) ;
	}
	public class SubClass {
		public void name() {
			System.out.println( this.getClass().getName() ) ;
		}
		public class SubSubClass {
			public void name() {
				System.out.println( this.getClass().getName() ) ;
			}
			public class SubSubSubClass {
				public void name() {
					System.out.println( this.getClass().getName().replaceAll( "\\.", "/" ).split( "\\$", 2 )[0] ) ;
				}
				public class SubSubSubSubClass extends astrolabe.Common {
					public void name() {
						System.out.println( this.getClass().getName() ) ;
						java.util.prefs.Preferences a = getPackageNode( null, null ) ;
						java.util.prefs.Preferences b = getClassNode( null, null ) ;
						java.util.prefs.Preferences c = getSubClassNode( null, null ) ;
						a = getPackageNode( "zappa", null ) ;
						b = getClassNode( "zappa", null ) ;
						c = getSubClassNode( "zappa", null ) ;
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		int yr = new Integer( args[0] ).intValue() ;
		int mn = new Integer( args[1] ).intValue() ;
		double dy = new Double( args[2] ).doubleValue() ;
		int h = new Integer( args[3] ).intValue() ;
		int m = new Integer( args[4] ).intValue() ;
		double s = new Double( args[5] ).doubleValue() ;
		
		CAADate date = new CAADate( yr, mn, dy, h, m, s, true ) ;
		
		java.util.prefs.Preferences p = java.util.prefs.Preferences.systemRoot() ;
		String n = date.getClass().getName() ;
		String timestamp= String.valueOf(System.currentTimeMillis());
		
		try {
			p.exportSubtree( System.out ) ;
		} catch ( Exception e ) {}

		double rad = Math.PI/180.0 ;
		double deg = 180.0/Math.PI ;
		
		double arc90 = 90.0*rad ;
		double arc180 = 180.0*rad ;
		
		double la = 51.4833*rad, st = 86.2604*rad ;
		double laC = 27.4*rad, stC = 192.25*rad ;
		double dSt = st-stC ;
		
		double a = Sphere.LawOfEdgeCosine( la, laC, dSt) ;
		double beta = Sphere.LawOfAngleSine( dSt, la, a ) ;
		double gamma = Sphere.LawOfAngleSine( dSt, laC, a ) ;
		double alpha = Sphere.LawOfAngleCosine( arc90-gamma, arc90-beta, a ) ;
		double b = Sphere.LawOfEdgeSine( a, arc90-beta, alpha ) ;
		
		double k = Sphere.LawOfHalfAngles( arc90, arc90, arc90 )*deg ;
		k = Sphere.LawOfHalfAngles( arc90-10*rad, arc90, ( arc90-la ) )*deg ;
		k = Sphere.LawOfHalfAngles( arc90-20*rad, arc90, ( arc90-la ) )*deg ;
		k = Sphere.LawOfHalfAngles( arc90-30*rad, arc90, ( arc90-la ) )*deg ;
		k = Sphere.LawOfHalfAngles( arc90-40*rad, arc90, ( arc90-la ) )*deg ;
		k = Sphere.LawOfHalfAngles( arc90-50*rad, arc90, ( arc90-la ) )*deg ;
		k = Sphere.LawOfHalfAngles( arc90-60*rad, arc90, ( arc90-la ) )*deg ;
		
		k = Sphere.LawOfHalfEdges( arc90, arc90, arc90 )/rad ;

		for ( double al=0.0 ; al<arc90 ; al=al+10.0*rad ) {
			System.out.print( ( b+Math.asin( Math.tan( al )/Math.tan( arc180-alpha ) ) )*deg+"\n" ) ;
		}
		
		Versuch c = new Versuch() ;
		c.name() ;
		Versuch.SubClass sc = c.new SubClass() ;
		sc.name() ;
		Versuch.SubClass.SubSubClass ssc = sc.new SubSubClass() ;
		ssc.name() ;
		Versuch.SubClass.SubSubClass.SubSubSubClass sssc = ssc.new SubSubSubClass() ;
		sssc.name() ;
		Versuch.SubClass.SubSubClass.SubSubSubClass.SubSubSubSubClass ssssc = sssc.new SubSubSubSubClass() ;
		ssssc.name() ;
		
		double[] crd = new double[2] ;
		Postscript d = null ;
		try {
			d = new Postscript() ;
		} catch (Exception e) {}
        d.dcs.page( null, 1 ) ;
        d.dcs.beginPageSetup() ;
        d.dcs.endPageSetup() ;
        crd[0] = 0 ; crd[1] = 0 ;
        d.operator.moveto( crd ) ;
        crd[0] = 10 ; crd[1] = 0 ;
        d.operator.lineto( crd ) ;
        crd[0] = 10 ; crd[1] = 10 ;
        d.operator.lineto( crd ) ;
        crd[0] = 0 ; crd[1] = 10 ;
        d.operator.lineto( crd ) ;
        d.operator.stroke() ;
        d.operator.showpage() ;
        d.dcs.pageTrailer() ;
        d.dcs.trailer() ;
        d.dcs.eOF() ;
        
        java.util.regex.Pattern pt = java.util.regex.Pattern.compile( "(top|middle|bottom)([\\d]|[1-9][\\d]|100){0,1}(left|middle|right)" ) ;
        java.util.regex.Matcher mt = pt.matcher( "middle7middle" ) ;
        boolean bb = mt.matches() ;
        String r = mt.group( 3 ) ; // Group 1, 2, 3
        double ddd = new Double( "1.2" ).doubleValue() ;
        
        int cc = (int) '\u03bc' ;
        System.out.print( '\u00bc' ) ;
		System.exit( 0 ) ;		
	}
}
