
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
	private double epoch ;

	private FOV fov ;

	private Hashtable<String, CatalogRecord> catalog ;

	private Hashtable<String, astrolabe.model.Annotation[]> select ;

	public CatalogType( Object peer, Projector projector, double epoch ) throws ParameterNotValidException {
		String[] fov, sv ;

		this.projector = projector ;
		this.epoch = epoch ;

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

	public void headPS( PostscriptStream ps ) {
	}

	public void emitPS( PostscriptStream ps ) {
		astrolabe.model.Annotation[] annotation ;
		Set<String> matchSet ;
		Body body ;

		for ( CatalogRecord record : arrange( catalog ) ) {
			try {
				body = AstrolabeFactory.companionOf( record.toBody( epoch ), projector, 0 ) ;
			} catch ( ParameterNotValidException e ) {
				throw new RuntimeException( e.toString() ) ;
			}

			annotation = getAnnotation() ;

			matchSet = record.matchSet( select.keySet() ) ;
			if ( matchSet.size()>0 ) {
				annotation = select.get( matchSet.iterator().next() ) ;
			}

			if ( annotation != null ) {
				body.setAnnotation( annotation ) ;

				record.register() ;
			}

			ps.operator.gsave() ;

			body.headPS( ps ) ;
			body.emitPS( ps ) ;
			body.tailPS( ps ) ;

			ps.operator.grestore() ;
		}
	}

	public void tailPS( PostscriptStream ps ) {
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

			r.put( record.ident(), record ) ;
		}

		return r ;
	}

	public CatalogRecord entry( String ident ) {
		return catalog.get( ident ) ;
	}

	abstract public CatalogRecord record( java.io.Reader catalog ) ;
	abstract public CatalogRecord[] arrange( Hashtable<String, CatalogRecord> catalog ) ;
}
