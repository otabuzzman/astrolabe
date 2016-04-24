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

import org.exolab.castor.xml.ValidationException;

import caa.CAACoordinateTransformation;

@SuppressWarnings("serial")
public class CatalogADC5050 extends astrolabe.model.CatalogADC5050 implements Catalog {

	private final static String DEFAULT_STAR = "\uf811" ;

	private Projector projector ;

	private FOV fov ;
	private Hashtable<String, astrolabe.model.BodyStellar> catalog ;
	private Hashtable<String, astrolabe.model.Annotation[]> select ;

	private Hashtable<String, Object> restrict ;
	private int random ;

	private class Designation {
		public int flamsteed ;
		public String bayer ;
		public int bayerindex ;
		public String constellation ;
	}

	private static Hashtable<String, String> bayer ;
	private Hashtable<String, Designation> designation = new Hashtable<String, Designation>() ;

	public CatalogADC5050( Object peer, Projector projector ) throws ParameterNotValidException {
		String[] fov, rv, sv ;

		ApplicationHelper.setupCompanionFromPeer( this, peer ) ;
		try {
			validate() ;
		} catch ( ValidationException e ) {
			throw new ParameterNotValidException( e.toString() ) ;
		}

		this.projector = projector ;

		if ( getFov() != null ) {
			fov = getFov().split( "," ) ;

			this.fov = new FOV( fov[0] ) ;
			for ( int f=1 ; f<fov.length ; f++ ) {
				this.fov.insert( new FOV( fov[f] ) ) ;
			}
		}

		restrict = new Hashtable<String, Object>() ;
		if ( getRestrict() != null ) {
			restrict = new Hashtable<String, Object>() ;
			rv = getRestrict().split( "," ) ;
			for ( int v=0 ; v<rv.length ; v++ ) {
				restrict.put( rv[v], "" ) ;
			}
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
		BodyStellar cr ; // record
		astrolabe.model.BodyStellar cmr ;
		Designation d ;
		String key ;

		ckl = catalog.keys();

		while ( ckl.hasMoreElements() ) {
			cmr = catalog.get( ckl.nextElement() ) ; 

			if ( designation.containsKey( cmr.getName() ) ) {
				d = designation.get( cmr.getName() ) ;

				if ( d.flamsteed>0 ) {
					key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC5050_FLAMSTEED ) ;
					ApplicationHelper.registerNumber( key, d.flamsteed ) ;
				}
				if ( d.bayer != null ) {
					key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC5050_BAYER ) ;
					ApplicationHelper.registerName( key, d.bayer ) ;
				}
				if ( d.bayerindex>0 ) {
					key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC5050_BAYERINDEX ) ;
					ApplicationHelper.registerNumber( key, d.bayerindex ) ;
				}
				if ( d.constellation != null ) {
					key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC5050_CONSTELLATION ) ;
					ApplicationHelper.registerName( key, d.constellation ) ;
				}
			}

			ps.operator.gsave() ;

			try {
				cr = new BodyStellar( cmr, projector ) ;
				cr.headPS( ps ) ;
				cr.emitPS( ps ) ;
				cr.tailPS( ps ) ;
			} catch ( ParameterNotValidException e ) {} // BodyStellar validated in read() 

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
			throw new ParameterNotValidException( e.toString() ) ;
		}

		return read( url ) ;
	}

	public Hashtable<String, astrolabe.model.BodyStellar> read( URL catalog ) {
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

	public Hashtable<String, astrolabe.model.BodyStellar> read( String catalog ) {
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

	public Hashtable<String, astrolabe.model.BodyStellar> read( Reader catalog ) {
		Hashtable<String, astrolabe.model.BodyStellar> r = new Hashtable<String, astrolabe.model.BodyStellar>() ; // catalog record list
		BufferedReader br ;
		astrolabe.model.BodyStellar crm ;
		astrolabe.model.Annotation[] an ;
		String cl, dv ;
		String id, mag ;
		double ra, de, xy[], m ;
		Designation d ;

		try {
			br = new BufferedReader( catalog ) ;
			while ( ( cl = br.readLine() ) != null ) {
				if ( cl.substring( 75, 83 ).trim().equals( "" ) ) { // record removed from BSC
					continue ;
				}
				ra = new Double( cl.substring( 75, 77 ) ).doubleValue()
				+new Double( cl.substring( 77, 79 ) ).doubleValue()/60.
				+new Double( cl.substring( 79, 83 ) ).doubleValue()/3600. ;
				de = new Double( cl.substring( 83, 86 ) ).doubleValue()
				+new Double( cl.substring( 86, 88 ) ).doubleValue()/60.
				+new Double( cl.substring( 88, 90 ) ).doubleValue()/3600. ;
				xy = projector.project( CAACoordinateTransformation.HoursToRadians( ra ),
						CAACoordinateTransformation.DegreesToRadians( de ) ) ;
				if ( ! fov.covers( xy ) ) {
					continue ;
				}

				id = "BSC"+cl.substring( 0, 4 ).trim() ;
				m = new Double( cl.substring( 102, 107 ) ).doubleValue() ;
				mag = "mag"+( (int) ( m+100.5 )-100 ) ;
				if ( ! cl.substring( 4, 14 ).trim().equals( "" ) ) {
					d = new Designation() ;
					dv = cl.substring( 4, 7 ).trim() ;
					if ( ! dv.equals( "" ) ) {
						d.flamsteed = new Integer( dv ).intValue() ;
					}
					dv = cl.substring( 7, 10 ).trim() ;
					if ( ! dv.equals( "" ) ) {
						d.bayer = bayer.get( dv ) ;
					}
					dv = cl.substring( 10, 11 ).trim() ;
					if ( ! dv.equals( "" ) ) {
						d.bayerindex = new Integer( dv ).intValue() ;
					}
					dv = cl.substring( 11, 14 ).trim() ;
					if ( ! dv.equals( "" ) ) {
						d.constellation = cl.substring( 11, 14 ).trim() ;
					}
					designation.put( id, d ) ;
				}
				if ( ! restrict.containsKey( id ) &&
						! restrict.containsKey( mag ) &&
						! designation.containsKey( id ) ) {
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
				if ( ( an = select.get( id ) ) == null &&
						( an = select.get( mag ) ) == null ) {
					if ( ( an = getAnnotation() ) != null ) {
						crm.setAnnotation( an ) ;
					}
				} else{
					crm.setAnnotation( an ) ;
				}

				r.put( id, crm ) ;
			}
		} catch ( IOException e ) {
			throw new RuntimeException( e.toString() ) ;
		} catch ( ParameterNotValidException e ) { // validation failed for Position
			throw new RuntimeException( e.toString() ) ;
		}

		if ( random>0 ) {
			Random rg = new Random() ;
			Object[] ka = r.keySet().toArray() ;

			while ( r.size()>random ) {
				r.remove( ka[ rg.nextInt( ka.length ) ] ) ;
			}
		}

		return r ;
	}

	static {
		bayer = new Hashtable<String, String>() ;

		bayer.put( "Alp", "\u03b1" ) ;
		bayer.put( "Bet", "\u03b2" ) ;
		bayer.put( "Chi", "\u03c7" ) ;
		bayer.put( "Del", "\u03b4" ) ;
		bayer.put( "Eps", "\u03b5" ) ;
		bayer.put( "Eta", "\u03b7" ) ;
		bayer.put( "Gam", "\u03b3" ) ;
		bayer.put( "Iot", "\u03b9" ) ;
		bayer.put( "Kap", "\u03ba" ) ;
		bayer.put( "Lam", "\u03bb" ) ;
		bayer.put( "Mu", "\u03bc" ) ;
		bayer.put( "Nu", "\u03bd" ) ;
		bayer.put( "Ome", "\u03c9" ) ;
		bayer.put( "Omi", "\u03bf" ) ;
		bayer.put( "Phi", "\u03c6" ) ;
		bayer.put( "Pi", "\u03c0" ) ;
		bayer.put( "Psi", "\u03c8" ) ;
		bayer.put( "Rho", "\u03c1" ) ;
		bayer.put( "Sig", "\u03c3" ) ;
		bayer.put( "Tau", "\u03c4" ) ;
		bayer.put( "The", "\u03b8" ) ;
		bayer.put( "Ups", "\u03c5" ) ;
		bayer.put( "Xi", "\u03be" ) ;
		bayer.put( "Zet", "\u03b6" ) ;
	}
}
