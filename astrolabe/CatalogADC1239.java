package astrolabe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Random;
import java.util.zip.GZIPInputStream;

import caa.CAACoordinateTransformation;

@SuppressWarnings("serial")
public class CatalogADC1239 extends astrolabe.model.CatalogADC1239 implements Catalog {

	private final static String DEFAULT_STAR = "\uf811" ;

	private Projector projector ;

	private FOV fov ;
	private Hashtable<String, astrolabe.model.BodyStellar> catalog ;
	private Hashtable<String, astrolabe.model.Annotation[]> select ;

	private Hashtable<String, Object> restrict ;
	private int random ;

	public CatalogADC1239( Object peer, Projector projector ) throws ParameterNotValidException {
		String[] fov, rv, sv ;

		ApplicationHelper.setupCompanionFromPeer( this, peer ) ;

		this.projector = projector ;

		if ( getFov() != null ) {
			fov = getFov().split( "," ) ;

			this.fov = new FOV( fov[0] ) ;
			for ( int f=1 ; f<fov.length ; f++ ) {
				this.fov.insert( new FOV( fov[f] ) ) ;
			}
		}

		if ( getRestrict() != null ) {
			restrict = new Hashtable<String, Object>() ;
			rv = getRestrict().split( "," ) ;
			for ( int v=0 ; v<rv.length ; v++ ) {
				restrict.put( rv[v], "" ) ;
			}
		} else {
			restrict = null ;
		}
		random = getRandom() ;

		select = new Hashtable<String, astrolabe.model.Annotation[]>() ;
		if ( getSelect() != null ) {
			for ( int s=0 ; s<getSelectCount() ; s++ ) {
				sv = getSelect( s ).getValue().split( "," ) ;
				for ( int v=0 ; v<sv.length ; v++ ) {
					select.put( sv[v], getSelect( s ).getAnnotation() ) ;
				}
			}
		}

		this.catalog = read() ;
	}

	public void headPS(PostscriptStream ps) {
	}

	public void emitPS(PostscriptStream ps) {
		java.util.Enumeration<String> ckl ; // key list
		String ck ; // key
		BodyStellar cr ; // record

		ckl = catalog.keys();

		while ( ckl.hasMoreElements() ) {
			ck = ckl.nextElement() ;
			cr = new BodyStellar( catalog.get( ck ), projector ) ;

			ps.operator.gsave() ;

			cr.headPS( ps ) ;
			cr.emitPS( ps ) ;
			cr.tailPS( ps ) ;

			ps.operator.grestore() ;
		}
	}

	public void tailPS(PostscriptStream ps) {
	}

	public Hashtable<String, astrolabe.model.BodyStellar> read() throws ParameterNotValidException {
		URL url ;

		try {
			url = new URL( getUrl() ) ;
		} catch ( MalformedURLException e ) {
			throw new ParameterNotValidException() ;
		}

		return read( url ) ;
	}

	public Hashtable<String, astrolabe.model.BodyStellar> read( URL catalog ) throws ParameterNotValidException {
		Hashtable<String, astrolabe.model.BodyStellar> r ;
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
			r = new Hashtable<String, astrolabe.model.BodyStellar>() ;
		}

		return r ;
	}

	public Hashtable<String, astrolabe.model.BodyStellar> read( String catalog ) throws ParameterNotValidException {
		Hashtable<String, astrolabe.model.BodyStellar> r ;
		StringReader reader ;

		reader = new StringReader( catalog ) ;

		try {
			r = read( reader ) ;
		} finally {
			reader.close() ;
		}

		return r ;
	}

	public Hashtable<String, astrolabe.model.BodyStellar> read( Reader catalog ) throws ParameterNotValidException {
		Hashtable<String, astrolabe.model.BodyStellar> r = new Hashtable<String, astrolabe.model.BodyStellar>() ; // catalog record list
		BufferedReader br ;
		astrolabe.model.BodyStellar crm ;
		astrolabe.model.Annotation[] an ;
		String cl, lv[] ;
		String rams[], dems[] ;
		String id, iv[], mag ;
		double ra, de, xy[], M, m ;
		int tyc1, tyc2, tyc3 ;

		try {
			br = new BufferedReader( catalog ) ;
			while ( ( cl = br.readLine() ) != null ) {
				lv = cl.split( "\\|" ) ;

				rams = lv[3].split( " " ) ;
				ra = new Double( rams[0] ).doubleValue()
				+new Double( rams[1] ).doubleValue()/60.
				+new Double( rams[2] ).doubleValue()/3600. ;
				dems = lv[4].split( " " ) ;
				de = new Double( dems[0] ).doubleValue()
				+new Double( dems[1] ).doubleValue()/60.
				+new Double( dems[2] ).doubleValue()/3600. ;
				xy = projector.project( CAACoordinateTransformation.HoursToRadians( ra ),
						CAACoordinateTransformation.DegreesToRadians( de ) ) ;
				if ( ! fov.covers( xy ) ) {
					continue ;
				}

				M = new Double( lv[5] ).doubleValue() ;
				if ( lv[0].equals( "H" ) ) { // hipparch
					id = ( lv[0]+lv[1].trim() ) ;
					if ( lv[44].trim().equals( "" ) ) { // magnitude unavailable
						m = M ;
					} else {
						m = new Double( lv[44] ).doubleValue() ;
					}
					mag = "mag"+( (int) ( m+100.5 )-100 ) ;
				} else { // tycho
					iv = lv[1].trim().split( "[ ]+" ) ;
					tyc1 = new Integer( iv[0] ).intValue() ; // GSC region number 1-9537
					tyc2 = new Integer( iv[1] ).intValue() ; // number in region 1-12119
					tyc3 = new Integer( iv[2] ).intValue() ; // component number 1-4
					id = ( lv[0]+tyc1+"-"+tyc2+"-"+tyc3 ) ;
					mag = "mag"+( (int) ( M+100.5 )-100 ) ;
				}
				if ( ! ( restrict == null ) &&
						! restrict.containsKey( id ) &&
						! restrict.containsKey( mag ) )  {
					continue ;
				}

				crm = new astrolabe.model.BodyStellar() ;
				crm.setName( id ) ;
				crm.setGlyph( DEFAULT_STAR ) ;
				crm.setType( mag ) ;
				crm.setTurn( 0 ) ;
				crm.setSpin( 0 ) ;
				crm.setPosition( AstrolabeFactory.modelPosition(
						CAACoordinateTransformation.HoursToDegrees( ra ), de ) ) ;
				if ( ( an = select.get( id ) ) == null ) {
					if ( ( an = getAnnotation() ) != null ) {
						crm.setAnnotation( an ) ;
					}
				} else{
					crm.setAnnotation( an ) ;
				}

				r.put( id, crm ) ;
			}
		} catch ( IOException e ) {}

		if ( random>0 ) {
			Random rg = new Random() ;
			Object[] ka = r.keySet().toArray() ;

			while ( r.size()>random ) {
				r.remove( ka[ rg.nextInt( ka.length ) ] ) ;
			}
		}

		return r ;
	}
}
