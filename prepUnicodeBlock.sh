echo "
package astrolabe;

import java.util.HashMap;
import java.util.Map;

public final class UnicodeBlock {

	public String name ;
	public String nice ;

	public int start ;
	public int size ;

	private static final Map<String, UnicodeBlock> block = new HashMap<String, UnicodeBlock>() ;

	private UnicodeBlock( String name, String nice, int start, int size ) {
		this.name = name
		.trim()
		.toUpperCase()
		.replaceAll( \"[ _-]+\", \"_\" ) ;
		this.nice = nice
		.trim() ;

		this.start = start ;
		this.size = size ;

		block.put( this.name, this ) ;
	}

	public static final UnicodeBlock forName( String name ) {
		UnicodeBlock block ;
		String[] legacyName = new String[] {
				\"GREEK\",
				\"COMBINING_MARKS_FOR_SYMBOLS\" } ;
		String[] actualName = new String[] {
				\"GREEK_AND_COPTIC\",
				\"COMBINING_DIACRITICAL_MARKS_FOR_SYMBOLS\" } ;

		block = UnicodeBlock.block.get( name
				.trim()
				.toUpperCase()
				.replaceAll( \"[ _-]+\", \"_\") ) ;

		if ( block == null )
			for ( int n=0 ; n<legacyName.length ; n++ )
				if ( name.equals( legacyName[n] ) ) {
					block = UnicodeBlock.forName( actualName[n] ) ;
					break ;
				}

		return block ;
	}
"

gawk --posix '
$0~/^[0-9A-Fa-f]{4,6}\.\./ {
	if ( split( $0, sv, /; / ) != 2 )
		next ;
	if ( split( sv[1], se, /\.\./ ) != 2 )
		next ;

	gsub( /^[ \t]*/, "", sv[2] );
	gsub( /[ \t]*$/, "", sv[2] );
	id = sv[2] ;
	gsub( /[ _-]+/, "_", id ) ;
	id = toupper( id ) ;
	
	print "	public static final UnicodeBlock " id " = "
	print "		new UnicodeBlock( \"" id "\", \"" sv[2] "\", 0x" se[1] ", 0x" se[2] "-0x" se[1] "+1 ) ; "
}' $1

echo "}"
