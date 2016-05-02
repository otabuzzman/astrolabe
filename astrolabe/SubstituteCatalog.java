
package astrolabe;

public class SubstituteCatalog extends ApplicationResource {

	private final static String SK_QUALIFIER = "substitute." ;

	public SubstituteCatalog( String catalog, Object clazz ) {
		super( catalog, clazz ) ;
	}

	public SubstituteCatalog( String catalog, Class<?> clazz ) {
		super( catalog, clazz ) ;
	}

	public SubstituteCatalog( String catalog ) {
		super( catalog ) ;
	}

	public String substitute( String key, String def  ) {
		return super.getString( SK_QUALIFIER+key, def ) ;
	}

	public static String substitute( String catalog, Object clazz, String key, String def  ) {
		return new SubstituteCatalog( catalog, clazz ).substitute( key, def ) ;
	}

	public static String substitute( String catalog, Class<?> clazz, String key, String def  ) {
		return new SubstituteCatalog( catalog, clazz ).substitute( key, def ) ;
	}

	public static String substittue( String catalog, String key, String def  ) {
		return new SubstituteCatalog( catalog ).substitute( key, def ) ;
	}
}
