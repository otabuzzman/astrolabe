
package astrolabe;

public class MessageCatalog extends ApplicationResource {

	private final static String MK_QUALIFIER = "message." ;

	public MessageCatalog( String catalog, Object clazz ) {
		super( catalog, clazz ) ;
	}

	public MessageCatalog( String catalog, Class<?> clazz ) {
		super( catalog, clazz ) ;
	}

	public MessageCatalog( String catalog ) {
		super( catalog ) ;
	}

	public String message( String key, String def  ) {
		return super.getString( MK_QUALIFIER+key, def ) ;
	}

	public static String message( String catalog, Object clazz, String key, String def  ) {
		return new MessageCatalog( catalog, clazz ).message( key, def ) ;
	}

	public static String message( String catalog, Class<?> clazz, String key, String def  ) {
		return new MessageCatalog( catalog, clazz ).message( key, def ) ;
	}

	public static String message( String catalog, String key, String def  ) {
		return new MessageCatalog( catalog ).message( key, def ) ;
	}
}
