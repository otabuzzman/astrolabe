
package astrolabe;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.exolab.castor.xml.ValidationException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

@SuppressWarnings("serial")
abstract public class CatalogType extends astrolabe.model.CatalogType implements Catalog {

	private Projector projector ;

	private Hashtable<String, astrolabe.model.Annotation[]> select ;

	private Geometry fov ; // effective field-of-view

	public CatalogType( Object peer, Projector projector ) throws ParameterNotValidException {
		String[] sv ;
		Geometry fovg, fovu ;

		this.projector = projector ;

		ApplicationHelper.setupCompanionFromPeer( this, peer ) ;
		try {
			validate() ;
		} catch ( ValidationException e ) {
			throw new ParameterNotValidException( e.toString() ) ;
		}

		select = new Hashtable<String, astrolabe.model.Annotation[]>() ;
		if ( getSelect() != null ) {
			for ( int s=0 ; s<getSelectCount() ; s++ ) {
				sv = getSelect( s ).getValue().split( "," ) ;
				for ( int v=0 ; v<sv.length ; v++ ) {
					select.put( sv[v], getSelect( s ).getAnnotation() ) ;
				}
			}
		}

		fovg = new GeometryFactory().createGeometry( ApplicationHelper.getFovGlobal() ) ;
		if ( getFov() != null ) {
			fovu = (Geometry) Registry.retrieve( getFov() ) ;
			fov = fovg.intersection( fovu ) ;
		} else {
			fov = fovg ;
		}
		ApplicationHelper.setFovEffective( fov ) ;
	}

	public Hashtable<String, CatalogRecord> read() throws ParameterNotValidException {
		URI cati ;
		URL catl ;
		File catf ;

		try {
			cati = new URI( getUrl() ) ;
			if ( cati.isAbsolute() ) {
				catf = new File( cati ) ;	
			} else {
				catf = new File( cati.getPath() ) ;
			}
			catl = catf.toURL() ;
		} catch ( URISyntaxException e ) { // URI constructor
			throw new ParameterNotValidException( e.toString() ) ;
		} catch ( MalformedURLException e ) { // URL constructor
			throw new ParameterNotValidException( e.toString() ) ;
		}

		return read( catl ) ;
	}

	public Hashtable<String, CatalogRecord> read( URL catalog ) {
		Hashtable<String, CatalogRecord> r ;
		InputStreamReader reader ;
		GZIPInputStream filter ;

		try {
			try {
				filter = new GZIPInputStream( catalog.openStream() ) ;
				reader = new InputStreamReader( filter ) ;
			} catch ( IOException e ) {
				reader = new InputStreamReader( catalog.openStream() ) ;
			}

			try {
				r = read( reader ) ;
			} finally {
				reader.close() ;
			}
		} catch ( IOException e ) {
			r = new Hashtable<String, CatalogRecord>() ;
		}

		return r ;
	}

	public Hashtable<String, CatalogRecord> read( String catalog ) {
		Hashtable<String, CatalogRecord> r ;
		StringReader reader ;

		reader = new StringReader( catalog ) ;

		try {
			r = read( reader ) ;
		} finally {
			reader.close() ;
		}

		return r ;
	}

	public Hashtable<String, CatalogRecord> read( Reader catalog ) {
		Hashtable<String, CatalogRecord> r = new Hashtable<String, CatalogRecord>() ;
		java.util.Vector<double[]> l ;
		CatalogRecord record ;
		Geometry body ;
		boolean ok ;

		while ( ( record = record( catalog ) ) != null ) {
			l = record.list( projector ) ;
			if ( l.size() == 1 ) {
				ok = fov.covers( ApplicationHelper.jtsToPoint( l.get( 0 ) ) ) ;
			} else {
				body = ApplicationHelper.jtsToPolygon( l ) ;

				ok = fov.covers( body ) || fov.overlaps( body ) ;
			}
			if ( ! ok ) {
				continue ;
			}

			r.put( record.ident(), record ) ;
		}

		return r ;
	}

	public astrolabe.model.Annotation[] annotation( CatalogRecord record ) {
		astrolabe.model.Annotation[] r ;
		Set<String> matchSet ;

		matchSet = record.matchSet( select.keySet() ) ;
		if ( matchSet.size()>0 ) {
			r = select.get( matchSet.iterator().next() ) ;
		} else {
			r = getAnnotation() ;
		}

		return r ;
	}

	abstract public CatalogRecord record( java.io.Reader catalog ) ;
	abstract public CatalogRecord[] arrange( Hashtable<String, CatalogRecord> catalog ) ;
}
