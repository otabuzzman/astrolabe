
package astrolabe;

public class MessageCatalog extends ApplicationResource {

	private final static String MK_QUALIFIER = "message." ;

	public MessageCatalog( Object clazz ) {
		super( clazz ) ;
	}

	public MessageCatalog( Class<?> clazz ) {
		super( clazz ) ;
	}

	public MessageCatalog() {
		super() ;
	}

	public String message( String key, String def  ) {
		return super.getString( MK_QUALIFIER+key, def ) ;
	}

	public static String message( Object clazz, String key, String def  ) {
		return new MessageCatalog( clazz ).message( key, def ) ;
	}

	public static String message( Class<?> clazz, String key, String def  ) {
		return new MessageCatalog( clazz ).message( key, def ) ;
	}
}
