
package astrolabe;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Locale;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

@SuppressWarnings("serial")
public class Astrolabe extends astrolabe.model.Astrolabe implements PostscriptEmitter {

	// configuration key (CK_)
	private final static String CK_VIEWER		= "viewer" ;
	private final static String CK_LIBCAA		= "libcaa" ;

	private final static String DEFAULT_LIBCAA	= "caa" ;

	public Astrolabe() {
		Locale dl ;
		String ln, lc[] ;

		ln = getLocale() ;
		if ( ln != null ) {			
			lc = ln.split( "_" ) ;
			dl = new Locale( lc[0], lc[1] ) ;

			Locale.setDefault( dl ) ;
		}
	}

	public void headPS( ApplicationPostscriptStream ps ) {
		ps.emitDSCHeader() ;
		ps.emitDSCProlog() ;
	}

	public void emitPS( ApplicationPostscriptStream ps ) {
		astrolabe.model.Chart chart ;

		for ( int ch=0 ; ch<getChartCount() ; ch++ ) {				
			chart = getChart( ch ) ;

			if ( chart.getChartStereographic() != null ) {
				chart( ps, chart.getChartStereographic() ) ;
			} else if ( chart.getChartOrthographic() != null ) {
				chart( ps, chart.getChartOrthographic() ) ;
			} else if (  chart.getChartEquidistant() != null ) {
				chart( ps, chart.getChartEquidistant() ) ;
			} else { // chart.getChartGnomonic() != null
				chart( ps, chart.getChartGnomonic() ) ;
			}
		}
	}

	public void tailPS( ApplicationPostscriptStream ps ) {
		ps.emitDSCTrailer() ;
	}

	public static void main( String[] argv ) {
		File f ;
		FileInputStream s ;
		InputStreamReader r ;
		Astrolabe astrolabe ;
		double epoch ;
		String viewerDecl ;
		Process viewerProc ;
		TeeOutputStream out ;
		ApplicationPostscriptStream ps ;

		try {
			f = new File( argv[0] ) ;
			s = new FileInputStream( f ) ;
			r = new InputStreamReader( s, "UTF-8" ) ;
			astrolabe = new Astrolabe() ;
			readModel( r ).copyValues( astrolabe ) ;

			epoch = valueOf( astrolabe.getEpoch() ) ;
			Registry.register( Epoch.RK_EPOCH, epoch ) ;

			out =  new TeeOutputStream( System.out ) ;

			// Configuration.verbose() ;
			// ApplicationResource.verbose() ;

			Configuration.init() ;

			viewerDecl = Configuration.getValue(
					Astrolabe.class, CK_VIEWER, null ) ;
			if ( viewerDecl == null ) {
				viewerProc = null ;
			} else {
				try {
					viewerProc = Runtime.getRuntime().exec( viewerDecl.trim().split( "\\p{Space}+" ) ) ;

					viewerProc.getInputStream().close() ;
					viewerProc.getErrorStream().close() ;

					out.add( viewerProc.getOutputStream() ) ;
				} catch ( IOException e ) {
					viewerDecl = null ;
					viewerProc = null ;
				}
			}

			ps = new ApplicationPostscriptStream( out ) ;

			astrolabe.headPS( ps ) ;
			astrolabe.emitPS( ps ) ;
			astrolabe.tailPS( ps ) ;

			ps.flush() ;
			ps.flush() ;
			ps.close() ;

			if ( viewerDecl != null )
				viewerProc.waitFor() ;

			Registry.degister( Epoch.RK_EPOCH ) ;
			Registry.remove() ;
		} catch ( Exception e ) {
			e.printStackTrace() ;
			System.exit( 1 ) ;
		}

		System.exit( 0 ) ;
	} 

	public static astrolabe.model.Astrolabe readModel( Reader model ) {
		try {
			return (astrolabe.model.Astrolabe) astrolabe.model.Astrolabe.unmarshal( model ) ;
		} catch ( MarshalException e ) {
			throw new RuntimeException( e.toString() ) ;
		} catch ( ValidationException e ) {
			throw new RuntimeException( e.toString() ) ;
		}
	}

	public static astrolabe.model.Astrolabe readModel( String model ) {
		StringReader r ;
		astrolabe.model.Astrolabe a ;

		r = new StringReader( model ) ;

		try {
			a = readModel( r ) ;
		} finally {
			r.close() ;
		}

		return a ;
	}

	private void chart( ApplicationPostscriptStream ps, astrolabe.model.ChartStereographic peer ) {
		ChartStereographic chart ;
		ChartPage page ;

		chart = new ChartStereographic() ;
		peer.copyValues( chart ) ;

		page = new ChartPage() ;
		peer.getChartPage().copyValues( page ) ;

		Registry.register( ChartPage.RK_CHARTPAGE, page ) ;
		emitPS( ps, chart ) ;
		Registry.degister( ChartPage.RK_CHARTPAGE ) ;
	}

	private void chart( ApplicationPostscriptStream ps, astrolabe.model.ChartOrthographic peer ) {
		ChartOrthographic chart ;
		ChartPage page ;

		chart = new ChartOrthographic() ;
		peer.copyValues( chart ) ;

		page = new ChartPage() ;
		peer.getChartPage().copyValues( page ) ;

		Registry.register( ChartPage.RK_CHARTPAGE, page ) ;
		emitPS( ps, chart ) ;
		Registry.degister( ChartPage.RK_CHARTPAGE ) ;
	}

	private void chart( ApplicationPostscriptStream ps, astrolabe.model.ChartEquidistant peer ) {
		ChartEquidistant chart ;
		ChartPage page ;

		chart = new ChartEquidistant() ;
		peer.copyValues( chart ) ;

		page = new ChartPage() ;
		peer.getChartPage().copyValues( page ) ;

		Registry.register( ChartPage.RK_CHARTPAGE, page ) ;
		emitPS( ps, chart ) ;
		Registry.degister( ChartPage.RK_CHARTPAGE ) ;
	}

	private void chart( ApplicationPostscriptStream ps, astrolabe.model.ChartGnomonic peer ) {
		ChartGnomonic chart ;
		ChartPage page ;

		chart = new ChartGnomonic() ;
		peer.copyValues( chart ) ;

		page = new ChartPage() ;
		peer.getChartPage().copyValues( page ) ;

		Registry.register( ChartPage.RK_CHARTPAGE, page ) ;
		emitPS( ps, chart ) ;
		Registry.degister( ChartPage.RK_CHARTPAGE ) ;
	}

	private void emitPS( ApplicationPostscriptStream ps, PostscriptEmitter emitter ) {
		ps.operator.gsave() ;

		emitter.headPS( ps ) ;
		emitter.emitPS( ps ) ;
		emitter.tailPS( ps ) ;

		ps.operator.grestore() ;
	}

	static {
		String libcaa ;

		Configuration.init() ;

		libcaa = Configuration.getValue(
				Astrolabe.class, CK_LIBCAA, DEFAULT_LIBCAA ) ;
		System.loadLibrary( libcaa ) ;
	}
}
