
package astrolabe;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;

import caa.CAACoordinateTransformation;

@SuppressWarnings("serial")
public class CatalogADC6042 extends astrolabe.model.CatalogADC6042 implements Catalog {

	private Projector projector ;

	private StreamTokenizer tokenizer ;

	private FOV fov ;
	private java.util.Hashtable<String, CatalogRecord> catalog ;

	private class CatalogLine {
		public double ra ;
		public double de ;
		public String name ;
	}

	private java.util.Hashtable<String, String> abbreviation ;
	private java.util.Hashtable<String, String> nominative ;
	private java.util.Hashtable<String, String> genitive ;

	private java.util.Hashtable<String, astrolabe.model.AnnotationStraight[]> select ;

	public CatalogADC6042( Object peer, Projector projector ) throws ParameterNotValidException {
		String[] fov, sv ;

		ApplicationHelper.setupCompanionFromPeer( this, peer ) ;

		this.projector = projector ;

		if ( getFov() != null ) {
			fov = getFov().split( "," ) ;

			this.fov = new FOV( fov[0] ) ;
			for ( int f=1 ; f<fov.length ; f++ ) {
				this.fov.insert( new FOV( fov[f] ) ) ;
			}
		}

		select = new java.util.Hashtable<String, astrolabe.model.AnnotationStraight[]>() ;
		if ( getSelect() != null ) {
			for ( int s=0 ; s<getSelectCount() ; s++ ) {
				sv = getSelect( s ).getValue().split( "," ) ;
				for ( int v=0 ; v<sv.length ; v++ ) {
					if ( abbreviation.containsKey( sv[v] ) ) { // validate
						select.put( sv[v], getSelect( s ).getAnnotationStraight() ) ;
					} else {
						throw new ParameterNotValidException() ;
					}
				}
			}
		}

		this.catalog = read() ;
	}

	public void headPS( PostscriptStream ps ) {
	}

	public void emitPS( PostscriptStream ps ) {
		java.util.Enumeration<String> ckl ; // constellation key list
		String ck ; // constellation key
		String key, name ;
		CatalogRecord cr ;

		ckl = catalog.keys();

		while ( ckl.hasMoreElements() ) {
			ck = ckl.nextElement() ;

			try {
				key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC6049_CONSTELLATION ) ;
				name = ApplicationHelper.getLocalizedString( ApplicationConstant.LN_ADC6049+"."+ck ) ;
				ApplicationHelper.registerName( key, name ) ;
				key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC6049_ABBREVIATION ) ;
				ApplicationHelper.registerName( key, abbreviation.get( ck ) ) ;
				key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC6049_NOMINATIVE ) ;
				ApplicationHelper.registerName( key, nominative.get( ck ) ) ;
				key = ApplicationHelper.getLocalizedString( ApplicationConstant.LK_ADC6049_GENITIVE ) ;
				ApplicationHelper.registerName( key, genitive.get( ck ) ) ;
			} catch ( ParameterNotValidException e ) {}

			cr = catalog.get( ck ) ;

			ps.operator.gsave() ;

			cr.headPS( ps ) ;
			cr.emitPS( ps ) ;
			cr.tailPS( ps ) ;

			ps.operator.grestore() ;
		}
	}

	public void tailPS( PostscriptStream ps ) {
	}

	public java.util.Hashtable<String, CatalogRecord> read() throws ParameterNotValidException {
		URL url ;

		try {
			url = new URL( getUrl() ) ;
		} catch ( MalformedURLException e ) {
			throw new ParameterNotValidException() ;
		}

		return read( url ) ;
	}

	public java.util.Hashtable<String, CatalogRecord> read( URL catalog ) throws ParameterNotValidException {
		java.util.Hashtable<String, CatalogRecord> r ;
		InputStreamReader reader ;

		try {
			reader = new InputStreamReader( catalog.openStream() ) ;

			try {
				r = read( reader ) ;
			} finally {
				reader.close() ;
			}
		} catch ( IOException e ) {
			r = new java.util.Hashtable<String, CatalogRecord>() ;
		}

		return r ;
	}

	public java.util.Hashtable<String, CatalogRecord> read( String catalog ) throws ParameterNotValidException {
		java.util.Hashtable<String, CatalogRecord> r ;
		StringReader reader ;

		reader = new StringReader( catalog ) ;

		try {
			r = read( reader ) ;
		} finally {
			reader.close() ;
		}

		return r ;
	}

	public java.util.Hashtable<String, CatalogRecord> read( Reader catalog ) throws ParameterNotValidException {
		java.util.Hashtable<String, CatalogRecord> r = new java.util.Hashtable<String, CatalogRecord>() ; // catalog record list
		java.util.Vector<astrolabe.model.Position> pl ; // position list
		astrolabe.model.BodyAreal crm = null ;	// catalog record model
		CatalogLine clp, clt ;					// this/previous catalog line
		BodyAreal cr ;							// catalog record

		tokenizer = new StreamTokenizer( catalog ) ;

		clp = clt = readCatalogLine() ;

		// init position list
		pl = new java.util.Vector<astrolabe.model.Position>() ;
		pl.add( AstrolabeFactory.modelPosition( clt.ra, clt.de ) ) ;

		while ( ( clt = readCatalogLine() ) != null ) {
			if ( clt.name.equals( clp.name ) ) { // this record
				pl.add( AstrolabeFactory.modelPosition( clt.ra, clt.de ) ) ;
			} else { // next record
				crm = modelBodyAreal( clp.name, pl ) ;
				cr = new BodyAreal( crm, projector ) ;
				if ( fov.covers( cr.list() ) ) {
					r.put( clp.name, cr ) ;
				}

				pl = new java.util.Vector<astrolabe.model.Position>() ;
				pl.add( AstrolabeFactory.modelPosition( clt.ra, clt.de ) ) ;

				clp = clt ;
			}
		}
		crm = modelBodyAreal( clp.name, pl ) ;
		cr = new BodyAreal( crm, projector ) ;
		if ( fov.covers( cr.list() ) ) {
			r.put( clp.name, cr ) ;
		}

		return r ;
	}

	private CatalogLine readCatalogLine() {
		CatalogLine r = new CatalogLine() ;

		try {
			tokenizer.nextToken() ;
			if ( tokenizer.ttype == StreamTokenizer.TT_EOF ) {
				return null ;
			}
			r.ra = CAACoordinateTransformation.HoursToDegrees( tokenizer.nval ) ;
			tokenizer.nextToken() ;
			if ( tokenizer.ttype == 43 /*plus*/ ) {
				tokenizer.nextToken() ; // ignore
			}
			r.de = tokenizer.nval ;
			tokenizer.nextToken() ;
			r.name = new String( tokenizer.sval ) ;
			tokenizer.nextToken() ; // skip
		} catch ( IOException e ) {}

		return r ;
	}

	private astrolabe.model.BodyAreal modelBodyAreal( String name, java.util.Vector<astrolabe.model.Position> position ) {
		astrolabe.model.BodyAreal bA ;
		astrolabe.model.AnnotationStraight[] aS ;

		bA = new astrolabe.model.BodyAreal() ;

		bA.setName( name ) ;
		bA.setType( ApplicationConstant.AV_BODY_CONSTELLATION ) ;

		for ( int p=0 ; p<position.size() ; p++ ) {
			bA.addPosition( position.get( p ) ) ;
		}

		if ( ( aS = select.get( name ) ) == null ) {
			if ( ( aS = getAnnotationStraight() ) != null ) { // default
				bA.setAnnotationStraight( aS ) ;
			}
		} else {
			bA.setAnnotationStraight( aS ) ;
		}

		return bA ;
	}

	{
		abbreviation = new java.util.Hashtable<String, String>() ;

		abbreviation.put( "AND", "And" ) ;
		abbreviation.put( "ANT", "Ant" ) ;
		abbreviation.put( "APS", "Aps" ) ;
		abbreviation.put( "AQL", "Aql" ) ;
		abbreviation.put( "AQR", "Aqr" ) ;
		abbreviation.put( "ARA", "Ara" ) ;
		abbreviation.put( "ARI", "Ari" ) ;
		abbreviation.put( "AUR", "Aur" ) ;
		abbreviation.put( "BOO", "Boo" ) ;
		abbreviation.put( "CAE", "Cae" ) ;
		abbreviation.put( "CAM", "Cam" ) ;
		abbreviation.put( "CAP", "Cap" ) ;
		abbreviation.put( "CAR", "Car" ) ;
		abbreviation.put( "CAS", "Cas" ) ;
		abbreviation.put( "CEN", "Cen" ) ;
		abbreviation.put( "CEP", "Cep" ) ;
		abbreviation.put( "CET", "Cet" ) ;
		abbreviation.put( "CHA", "Cha" ) ;
		abbreviation.put( "CIR", "Cir" ) ;
		abbreviation.put( "CMA", "CMa" ) ;
		abbreviation.put( "CMI", "CMi" ) ;
		abbreviation.put( "CNC", "Cnc" ) ;
		abbreviation.put( "COL", "Col" ) ;
		abbreviation.put( "COM", "Com" ) ;
		abbreviation.put( "CRA", "CrA" ) ;
		abbreviation.put( "CRB", "CrB" ) ;
		abbreviation.put( "CRT", "Crt" ) ;
		abbreviation.put( "CRU", "Cru" ) ;
		abbreviation.put( "CRV", "Crv" ) ;
		abbreviation.put( "CVN", "CVn" ) ;
		abbreviation.put( "CYG", "Cyg" ) ;
		abbreviation.put( "DEL", "Del" ) ;
		abbreviation.put( "DOR", "Dor" ) ;
		abbreviation.put( "DRA", "Dra" ) ;
		abbreviation.put( "EQU", "Equ" ) ;
		abbreviation.put( "ERI", "Eri" ) ;
		abbreviation.put( "FOR", "For" ) ;
		abbreviation.put( "GEM", "Gem" ) ;
		abbreviation.put( "GRU", "Gru" ) ;
		abbreviation.put( "HER", "Her" ) ;
		abbreviation.put( "HOR", "Hor" ) ;
		abbreviation.put( "HYA", "Hya" ) ;
		abbreviation.put( "HYI", "Hyi" ) ;
		abbreviation.put( "IND", "Ind" ) ;
		abbreviation.put( "LAC", "Lac" ) ;
		abbreviation.put( "LEO", "Leo" ) ;
		abbreviation.put( "LEP", "Lep" ) ;
		abbreviation.put( "LIB", "Lib" ) ;
		abbreviation.put( "LMI", "LMi" ) ;
		abbreviation.put( "LUP", "Lup" ) ;
		abbreviation.put( "LYN", "Lyn" ) ;
		abbreviation.put( "LYR", "Lyr" ) ;
		abbreviation.put( "MEN", "Men" ) ;
		abbreviation.put( "MIC", "Mic" ) ;
		abbreviation.put( "MON", "Mon" ) ;
		abbreviation.put( "MUS", "Mus" ) ;
		abbreviation.put( "NOR", "Nor" ) ;
		abbreviation.put( "OCT", "Oct" ) ;
		abbreviation.put( "OPH", "Oph" ) ;
		abbreviation.put( "ORI", "Ori" ) ;
		abbreviation.put( "PAV", "Pav" ) ;
		abbreviation.put( "PEG", "Peg" ) ;
		abbreviation.put( "PER", "Per" ) ;
		abbreviation.put( "PHE", "Phe" ) ;
		abbreviation.put( "PIC", "Pic" ) ;
		abbreviation.put( "PSA", "PsA" ) ;
		abbreviation.put( "PSC", "Psc" ) ;
		abbreviation.put( "PUP", "Pup" ) ;
		abbreviation.put( "PYX", "Pyx" ) ;
		abbreviation.put( "RET", "Ret" ) ;
		abbreviation.put( "SCL", "Scl" ) ;
		abbreviation.put( "SCO", "Sco" ) ;
		abbreviation.put( "SCT", "Sct" ) ;
		abbreviation.put( "SER1", "Ser" ) ;
		abbreviation.put( "SER2", "Ser" ) ;
		abbreviation.put( "SEX", "Sex" ) ;
		abbreviation.put( "SGE", "Sge" ) ;
		abbreviation.put( "SGR", "Sgr" ) ;
		abbreviation.put( "TAU", "Tau" ) ;
		abbreviation.put( "TEL", "Tel" ) ;
		abbreviation.put( "TRA", "TrA" ) ;
		abbreviation.put( "TRI", "Tri" ) ;
		abbreviation.put( "TUC", "Tuc" ) ;
		abbreviation.put( "UMA", "UMa" ) ;
		abbreviation.put( "UMI", "UMi" ) ;
		abbreviation.put( "VEL", "Vel" ) ;
		abbreviation.put( "VIR", "Vir" ) ;
		abbreviation.put( "VOL", "Vol" ) ;
		abbreviation.put( "VUL", "Vul" ) ;

		nominative = new java.util.Hashtable<String, String>() ;

		nominative.put( "AND", "Andromeda" ) ;
		nominative.put( "ANT", "Antlia" ) ;
		nominative.put( "APS", "Apus" ) ;
		nominative.put( "AQL", "Aquila" ) ;
		nominative.put( "AQR", "Aquarius" ) ;
		nominative.put( "ARA", "Ara" ) ;
		nominative.put( "ARI", "Aries" ) ;
		nominative.put( "AUR", "Auriga" ) ;
		nominative.put( "BOO", "Bootes" ) ;
		nominative.put( "CAE", "Caelum" ) ;
		nominative.put( "CAM", "Camelopardalis" ) ;
		nominative.put( "CAP", "Capricornus" ) ;
		nominative.put( "CAR", "Carina" ) ;
		nominative.put( "CAS", "Cassiopeia" ) ;
		nominative.put( "CEN", "Centaurus" ) ;
		nominative.put( "CEP", "Cepheus" ) ;
		nominative.put( "CET", "Cetus" ) ;
		nominative.put( "CHA", "Chamaeleon" ) ;
		nominative.put( "CIR", "Circinus" ) ;
		nominative.put( "CMA", "Canis Major" ) ;
		nominative.put( "CMI", "Canis Minor" ) ;
		nominative.put( "CNC", "Cancer" ) ;
		nominative.put( "COL", "Columba" ) ;
		nominative.put( "COM", "Coma Berenices" ) ;
		nominative.put( "CRA", "Corona Australis" ) ;
		nominative.put( "CRB", "Corona Borealis" ) ;
		nominative.put( "CRT", "Crater" ) ;
		nominative.put( "CRU", "Crux" ) ;
		nominative.put( "CRV", "Corvus" ) ;
		nominative.put( "CVN", "Canes Venatici" ) ;
		nominative.put( "CYG", "Cygnus" ) ;
		nominative.put( "DEL", "Delphinus" ) ;
		nominative.put( "DOR", "Dorado" ) ;
		nominative.put( "DRA", "Draco" ) ;
		nominative.put( "EQU", "Equuleus" ) ;
		nominative.put( "ERI", "Eridanus" ) ;
		nominative.put( "FOR", "Fornax" ) ;
		nominative.put( "GEM", "Gemini" ) ;
		nominative.put( "GRU", "Grus" ) ;
		nominative.put( "HER", "Hercules" ) ;
		nominative.put( "HOR", "Horologium" ) ;
		nominative.put( "HYA", "Hydra" ) ;
		nominative.put( "HYI", "Hydrus" ) ;
		nominative.put( "IND", "Indus" ) ;
		nominative.put( "LAC", "Lacerta" ) ;
		nominative.put( "LEO", "Leo" ) ;
		nominative.put( "LEP", "Lepus" ) ;
		nominative.put( "LIB", "Libra" ) ;
		nominative.put( "LMI", "Leo Minor" ) ;
		nominative.put( "LUP", "Lupus" ) ;
		nominative.put( "LYN", "Lynx" ) ;
		nominative.put( "LYR", "Lyra" ) ;
		nominative.put( "MEN", "Mensa" ) ;
		nominative.put( "MIC", "Microscopium" ) ;
		nominative.put( "MON", "Monoceros" ) ;
		nominative.put( "MUS", "Musca" ) ;
		nominative.put( "NOR", "Norma" ) ;
		nominative.put( "OCT", "Octans" ) ;
		nominative.put( "OPH", "Ophiuchus" ) ;
		nominative.put( "ORI", "Orion" ) ;
		nominative.put( "PAV", "Pavo" ) ;
		nominative.put( "PEG", "Pegasus" ) ;
		nominative.put( "PER", "Perseus" ) ;
		nominative.put( "PHE", "Phoenix" ) ;
		nominative.put( "PIC", "Pictor" ) ;
		nominative.put( "PSA", "Piscis Austrinus" ) ;
		nominative.put( "PSC", "Pisces" ) ;
		nominative.put( "PUP", "Puppis" ) ;
		nominative.put( "PYX", "Pyxis" ) ;
		nominative.put( "RET", "Reticulum" ) ;
		nominative.put( "SCL", "Sculptor" ) ;
		nominative.put( "SCO", "Scorpius" ) ;
		nominative.put( "SCT", "Scutum" ) ;
		nominative.put( "SER1", "Serpens (Caput)" ) ;
		nominative.put( "SER2", "Serpens (Cauda)" ) ;
		nominative.put( "SEX", "Sextans" ) ;
		nominative.put( "SGE", "Sagitta" ) ;
		nominative.put( "SGR", "Sagittarius" ) ;
		nominative.put( "TAU", "Taurus" ) ;
		nominative.put( "TEL", "Telescopium" ) ;
		nominative.put( "TRA", "Triangulum Australe" ) ;
		nominative.put( "TRI", "Triangulum" ) ;
		nominative.put( "TUC", "Tucana" ) ;
		nominative.put( "UMA", "Ursa Major" ) ;
		nominative.put( "UMI", "Ursa Minor" ) ;
		nominative.put( "VEL", "Vela" ) ;
		nominative.put( "VIR", "Virgo" ) ;
		nominative.put( "VOL", "Volans" ) ;
		nominative.put( "VUL", "Vulpecula" ) ;

		genitive = new java.util.Hashtable<String, String>() ;

		genitive.put( "AND", "Andromedae" ) ;
		genitive.put( "ANT", "Antliae" ) ;
		genitive.put( "APS", "Apodis" ) ;
		genitive.put( "AQL", "Aquilae" ) ;
		genitive.put( "AQR", "Aquarii" ) ;
		genitive.put( "ARA", "Arae" ) ;
		genitive.put( "ARI", "Arietis" ) ;
		genitive.put( "AUR", "Aurigae" ) ;
		genitive.put( "BOO", "Bootis" ) ;
		genitive.put( "CAE", "Caeli" ) ;
		genitive.put( "CAM", "Camelopardalis" ) ;
		genitive.put( "CAP", "Capricorni" ) ;
		genitive.put( "CAR", "Carinae" ) ;
		genitive.put( "CAS", "Cassiopeiae" ) ;
		genitive.put( "CEN", "Centauri" ) ;
		genitive.put( "CEP", "Cephei" ) ;
		genitive.put( "CET", "Ceti" ) ;
		genitive.put( "CHA", "Chamaeleontis" ) ;
		genitive.put( "CIR", "Circini" ) ;
		genitive.put( "CMA", "Canis Majoris" ) ;
		genitive.put( "CMI", "Canis Minoris" ) ;
		genitive.put( "CNC", "Cancri" ) ;
		genitive.put( "COL", "Columbae" ) ;
		genitive.put( "COM", "Comae Berenices" ) ;
		genitive.put( "CRA", "Coronae Australis" ) ;
		genitive.put( "CRB", "Coronae Borealis" ) ;
		genitive.put( "CRT", "Crateris" ) ;
		genitive.put( "CRU", "Crucis" ) ;
		genitive.put( "CRV", "Corvi" ) ;
		genitive.put( "CVN", "Canum Venaticorum" ) ;
		genitive.put( "CYG", "Cygni" ) ;
		genitive.put( "DEL", "Delphini" ) ;
		genitive.put( "DOR", "Doradus" ) ;
		genitive.put( "DRA", "Draconis" ) ;
		genitive.put( "EQU", "Equulei" ) ;
		genitive.put( "ERI", "Eridani" ) ;
		genitive.put( "FOR", "Fornacis" ) ;
		genitive.put( "GEM", "Geminorum" ) ;
		genitive.put( "GRU", "Gruis" ) ;
		genitive.put( "HER", "Herculis" ) ;
		genitive.put( "HOR", "Horologii" ) ;
		genitive.put( "HYA", "Hydrae" ) ;
		genitive.put( "HYI", "Hydri" ) ;
		genitive.put( "IND", "Indi" ) ;
		genitive.put( "LAC", "Lacertae" ) ;
		genitive.put( "LEO", "Leonis" ) ;
		genitive.put( "LEP", "Leporis" ) ;
		genitive.put( "LIB", "Librae" ) ;
		genitive.put( "LMI", "Leonis Minoris" ) ;
		genitive.put( "LUP", "Lupi" ) ;
		genitive.put( "LYN", "Lyncis" ) ;
		genitive.put( "LYR", "Lyrae" ) ;
		genitive.put( "MEN", "Mensae" ) ;
		genitive.put( "MIC", "Microscopii" ) ;
		genitive.put( "MON", "Monocerotis" ) ;
		genitive.put( "MUS", "Muscae" ) ;
		genitive.put( "NOR", "Normae" ) ;
		genitive.put( "OCT", "Octantis" ) ;
		genitive.put( "OPH", "Ophiuchi" ) ;
		genitive.put( "ORI", "Orionis" ) ;
		genitive.put( "PAV", "Pavonis" ) ;
		genitive.put( "PEG", "Pegasi" ) ;
		genitive.put( "PER", "Persei" ) ;
		genitive.put( "PHE", "Phoenicis" ) ;
		genitive.put( "PIC", "Pictoris" ) ;
		genitive.put( "PSA", "Piscis Austrini" ) ;
		genitive.put( "PSC", "Piscium" ) ;
		genitive.put( "PUP", "Puppis" ) ;
		genitive.put( "PYX", "Pyxidis" ) ;
		genitive.put( "RET", "Reticuli" ) ;
		genitive.put( "SCL", "Sculptoris" ) ;
		genitive.put( "SCO", "Scorpii" ) ;
		genitive.put( "SCT", "Scuti" ) ;
		genitive.put( "SER1", "Serpentis" ) ;
		genitive.put( "SER2", "Serpentis" ) ;
		genitive.put( "SEX", "Sextantis" ) ;
		genitive.put( "SGE", "Sagittae" ) ;
		genitive.put( "SGR", "Sagittarii" ) ;
		genitive.put( "TAU", "Tauri" ) ;
		genitive.put( "TEL", "Telescopii" ) ;
		genitive.put( "TRA", "Trainguli Australis" ) ;
		genitive.put( "TRI", "Trianguli" ) ;
		genitive.put( "TUC", "Tucanae" ) ;
		genitive.put( "UMA", "Ursae Majoris" ) ;
		genitive.put( "UMI", "Ursae Minoris" ) ;
		genitive.put( "VEL", "Velorum" ) ;
		genitive.put( "VIR", "Virginis" ) ;
		genitive.put( "VOL", "Volantis" ) ;
		genitive.put( "VUL", "Vulpeculae" ) ;
	}
}
