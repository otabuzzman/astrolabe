
package astrolabe;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.zip.GZIPInputStream;

import com.vividsolutions.jts.geom.Geometry;

@SuppressWarnings("serial")
abstract public class CatalogType extends astrolabe.model.CatalogType {

	private Hashtable<String, Integer> select ;

	public CatalogType( Peer peer, Projector projector ) {
		String[] sv ;
		Geometry fov, fovu, fove ;

		peer.setupCompanion( this ) ;

		select = new Hashtable<String, Integer>() ;
		if ( getSelect() != null ) {
			for ( int s=0 ; s<getSelectCount() ; s++ ) {
				sv = getSelect( s ).getValue().split( "," ) ;
				for ( int v=0 ; v<sv.length ; v++ ) {
					select.put( sv[v], s ) ;
				}
			}
		}

		if ( getFov() == null ) {
			fov = (Geometry) AstrolabeRegistry.retrieve( ApplicationConstant.GC_FOVUNI ) ;
		} else {
			fovu = (Geometry) AstrolabeRegistry.retrieve( ApplicationConstant.GC_FOVUNI ) ;
			fove = (Geometry) AstrolabeRegistry.retrieve( getFov() ) ;
			fov = fovu.intersection( fove ) ;
		}
		Registry.register( ApplicationConstant.GC_FOVEFF, fov ) ;
	}

	public Reader reader() throws URISyntaxException, MalformedURLException {
		InputStreamReader r ;
		URI cURI ;
		URL cURL ;
		File cFile ;
		InputStream cIS ;
		GZIPInputStream cF ;

		cURI = new URI( getUrl() ) ;
		if ( cURI.isAbsolute() ) {
			cFile = new File( cURI ) ;	
		} else {
			cFile = new File( cURI.getPath() ) ;
		}
		cURL = cFile.toURL() ;

		try {
			cIS = cURL.openStream() ;
		} catch ( IOException e ) {
			throw new RuntimeException ( e.toString() ) ;
		}

		try {
			cF = new GZIPInputStream( cIS ) ;
			r = new InputStreamReader( cF ) ;
		} catch ( IOException e ) {
			r = new InputStreamReader( cIS ) ;
		}

		return r ;
	}

	public astrolabe.model.Select[] getSelect( List<String> ident ) {
		astrolabe.model.Select[] r ;
		List<astrolabe.model.Select> selL ;
		List<Integer> intL = new java.util.Vector<Integer>() ;
		Comparator<Integer> c = new Comparator<Integer>() {
			public int compare( Integer a, Integer b ) {
				int x, y ;

				x = a.intValue() ;
				y = b.intValue() ;

				return ( x<y?1:
					x>y?-1:
						0 ) ;
			}
		} ;

		for ( String key : ident ) {
			if ( select.containsKey( key ) )
				intL.add( select.get( key ) ) ;
		}

		if ( intL.size()>0 ) {
			Collections.sort( intL, c ) ;

			selL = new java.util.Vector<astrolabe.model.Select>() ;
			for ( int s : intL )
				selL.add( getSelect( s ) ) ;
			r = selL.toArray( new astrolabe.model.Select[0] ) ;
		} else
			r = null ;

		return r ;
	}

	abstract public void addAllCatalogRecord() ;
}
