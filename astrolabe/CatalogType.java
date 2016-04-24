
package astrolabe;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.exolab.castor.xml.ValidationException;

@SuppressWarnings("serial")
abstract public class CatalogType extends astrolabe.model.CatalogType implements Catalog {

	private Projector projector ;

	private FOV fov ;

	private java.util.Vector<CatalogRecord> catalog ;

	private Hashtable<String, astrolabe.model.Annotation[]> select ;

	public CatalogType( Object peer, Projector projector ) throws ParameterNotValidException {
		String[] fov, sv ;

		this.projector = projector ;

		ApplicationHelper.setupCompanionFromPeer( this, peer ) ;
		try {
			validate() ;
		} catch ( ValidationException e ) {
			throw new ParameterNotValidException( e.toString() ) ;
		}

		if ( getFov() != null ) {
			fov = getFov().split( "," ) ;

			this.fov = new FOV( fov[0] ) ;
			for ( int f=1 ; f<fov.length ; f++ ) {
				this.fov.insert( new FOV( fov[f] ) ) ;
			}
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
	}

	public void headPS(PostscriptStream ps) {
	}

	public void emitPS(PostscriptStream ps) {
		CatalogRecord catalogRecord ;
		Body body ;
		astrolabe.model.Annotation[] annotation ;
		Set matchSet ;

		for ( int r=0 ; r<catalog.size() ; r++ ) {
			catalogRecord = catalog.get( r ) ;

			try {
				body = AstrolabeFactory.companionOf( catalogRecord.toBody(), projector, 0 ) ;
			} catch ( ParameterNotValidException e ) {
				throw new RuntimeException( e.toString() ) ;
			}

			annotation = getAnnotation() ;

			matchSet = catalogRecord.matchSet( select.keySet() ) ;
			if ( matchSet.size()>0 ) {
				annotation = select.get( matchSet.iterator().next() ) ;
			}

			if ( annotation != null ) {
				body.setAnnotation( annotation ) ;

				catalogRecord.register() ;
			}

			ps.operator.gsave() ;

			body.headPS( ps ) ;
			body.emitPS( ps ) ;
			body.tailPS( ps ) ;

			ps.operator.grestore() ;
		}
	}

	public void tailPS(PostscriptStream ps) {
	}

	public void read() throws ParameterNotValidException {
		URL url ;

		try {
			url = new URL( getUrl() ) ;
		} catch ( MalformedURLException e ) {
			throw new ParameterNotValidException( e.toString() ) ;
		}

		this.catalog = read( url ) ;
	}

	public java.util.Vector<CatalogRecord> read( URL catalog ) {
		java.util.Vector<CatalogRecord> r ;
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
			r = new java.util.Vector<CatalogRecord>() ;
		}

		return r ;
	}

	public java.util.Vector<CatalogRecord> read( String catalog ) {
		java.util.Vector<CatalogRecord> r ;
		StringReader reader ;

		reader = new StringReader( catalog ) ;

		try {
			r = read( reader ) ;
		} finally {
			reader.close() ;
		}

		return r ;
	}

	public java.util.Vector<CatalogRecord> read( Reader catalog ) {
		java.util.Vector<CatalogRecord> r = new java.util.Vector<CatalogRecord>() ;
		java.util.Vector<double[]> l ;
		CatalogRecord record ;
		boolean ok ;

		while ( ( record = record( catalog ) ) != null ) {
			if ( fov != null ) {
				l = record.list( projector ) ;
				if ( l.size() == 1 ) {
					ok = fov.covers( l.get( 0 ) ) ;
				} else {
					ok = fov.covers( l ) ;
				}
				if ( ! ok ) {
					continue ;
				}
			}					

			r.add( record ) ;
		}

		return r ;
	}

	abstract public CatalogRecord record( java.io.Reader catalog ) ;
}
