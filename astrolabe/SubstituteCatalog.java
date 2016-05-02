
package astrolabe;

public class SubstituteCatalog extends ApplicationResource {

	private final static String SK_QUALIFIER = "substitute." ;

	public SubstituteCatalog( Object clazz ) {
		super( clazz ) ;
	}

	public SubstituteCatalog( Class<?> clazz ) {
		super( clazz ) ;
	}

	public SubstituteCatalog() {
		super() ;
	}

	public String substitute( String key, String def  ) {
		return super.getString( SK_QUALIFIER+key, def ) ;
	}

	public static String substitute( Object clazz, String key, String def  ) {
		return new SubstituteCatalog( clazz ).substitute( key, def ) ;
	}

	public static String substitute( Class<?> clazz, String key, String def  ) {
		return new SubstituteCatalog( clazz ).substitute( key, def ) ;
	}
}
