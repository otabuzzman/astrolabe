
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
	public final static String CK_VERBOSE		= "verbose" ;
	public final static boolean DEFAULT_VERBOSE	= false ;

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
		Epoch epoch ;
		astrolabe.model.Chart chart ;
		ParserAttribute parser ;

		parser = new ParserAttribute() ;
		Registry.register( ParserAttribute.class.getName(), parser ) ;

		epoch = new Epoch() ;
		if ( getEpoch() != null )
			getEpoch().copyValues( epoch ) ;
		Registry.register( Epoch.class.getName(), epoch ) ;

		for ( int ch=0 ; ch<getChartCount() ; ch++ ) {				
			chart = getChart( ch ) ;

			if ( chart.getChartAzimuthal() != null ) {
				chart( ps, chart.getChartAzimuthal() ) ;
			} else { // chart.getChartCylindrical() != null
				chart( ps, chart.getChartPseudoCylindrical() ) ;
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
		String viewerDecl ;
		Process viewerProc ;
		TeeOutputStream out ;
		ApplicationPostscriptStream ps ;
		boolean verbose ;

		try {
			Configuration.init() ;

			verbose = Configuration.getValue( Astrolabe.class, CK_VERBOSE, DEFAULT_VERBOSE ) ;

			if ( verbose ) {
				Configuration.verbose() ;
				ApplicationResource.verbose() ;
			}

			viewerDecl = Configuration.getValue( Astrolabe.class, CK_VIEWER, null ) ;
			out =  new TeeOutputStream( System.out ) ;

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

			f = new File( argv[0] ) ;
			s = new FileInputStream( f ) ;
			r = new InputStreamReader( s, "UTF-8" ) ;
			astrolabe = new Astrolabe() ;
			readModel( r ).copyValues( astrolabe ) ;

			ps = new ApplicationPostscriptStream( out ) ;

			astrolabe.headPS( ps ) ;
			astrolabe.emitPS( ps ) ;
			astrolabe.tailPS( ps ) ;

			ps.flush() ;
			ps.flush() ;
			ps.close() ;

			if ( viewerDecl != null )
				viewerProc.waitFor() ;

			Registry.degister( ParserAttribute.class.getName() ) ;
			Registry.degister( Epoch.class.getName() ) ;
			Registry.remove() ;

			if ( verbose ) {
				Configuration.verbose() ;
				ApplicationResource.verbose() ;
			}
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

	private void chart( ApplicationPostscriptStream ps, astrolabe.model.ChartAzimuthal peer ) {
		ChartAzimuthal chart ;
		ChartPage page ;
		String key ;

		chart = new ChartAzimuthal( peer ) ;

		page = new ChartPage() ;
		peer.getChartPage().copyValues( page ) ;

		key = ChartPage.class.getName() ;
		Registry.register( key, page ) ;
		emitPS( ps, chart ) ;
		Registry.degister( key ) ;
	}

	private void chart( ApplicationPostscriptStream ps, astrolabe.model.ChartPseudoCylindrical peer ) {
		ChartPseudoCylindrical chart ;
		ChartPage page ;
		String key ;

		chart = new ChartPseudoCylindrical( peer ) ;

		page = new ChartPage() ;
		peer.getChartPage().copyValues( page ) ;

		key = ChartPage.class.getName() ;
		Registry.register( key, page ) ;
		emitPS( ps, chart ) ;
		Registry.degister( key ) ;
	}

	private void emitPS( ApplicationPostscriptStream ps, PostscriptEmitter emitter ) {
		ps.op( "gsave" ) ;

		emitter.headPS( ps ) ;
		emitter.emitPS( ps ) ;
		emitter.tailPS( ps ) ;

		ps.op( "grestore" ) ;
	}

	static {
		String libcaa ;

		Configuration.init() ;

		libcaa = Configuration.getValue(
				Astrolabe.class, CK_LIBCAA, DEFAULT_LIBCAA ) ;
		System.loadLibrary( libcaa ) ;
	}
}
