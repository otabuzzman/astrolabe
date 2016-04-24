
package astrolabe;

import java.util.Hashtable;
import java.util.prefs.BackingStoreException;

public class PostscriptStream extends PrintStream {

	private final static int DEFAULT_PRECISION = 6 ;
	private final static int DEFAULT_SCANLINE = 254 ;

	private final static String DEFAULT_FONTNAME = "/Times-Roman" ;
	private final static String DEFAULT_CHARPROC = "/question" ;

	public final PostscriptStream.Operator operator = new Operator();
	public final PostscriptStream.DSC dsc = new DSC();

	private PostscriptStream.UcBlock ucBlock[] = null ;
	private Hashtable<String, String> ucEncodingVectors = null ;

	private Hashtable<String, String> prolog = new Hashtable<String, String>() ;

	private final int precision = ApplicationHelper.getClassNode( this,
			null, null ).getInt( ApplicationConstant.PK_POSTSCRIPT_PRECISION, DEFAULT_PRECISION ) ;
	private final int scanline = ApplicationHelper.getClassNode( this,
			null, null ).getInt( ApplicationConstant.PK_POSTSCRIPT_SCANLINE, DEFAULT_SCANLINE ) ;

	public PostscriptStream( java.io.PrintStream ps ) {
		super( ps ) ;

		// Unicode 4.1.0, see file Blocks-4.1.0.txt
		ucBlock = new UcBlock[145] ;
		ucBlock[0] = new UcBlock( "0000..007F", 0x0000, 0x007F, "Basic Latin" ) ;
		ucBlock[1] = new UcBlock( "0080..00FF", 0x0080, 0x00FF, "Latin-1 Supplement" ) ;
		ucBlock[2] = new UcBlock( "0100..017F", 0x0100, 0x017F, "Latin Extended-A" ) ;
		ucBlock[3] = new UcBlock( "0180..024F", 0x0180, 0x024F, "Latin Extended-B" ) ;
		ucBlock[4] = new UcBlock( "0250..02AF", 0x0250, 0x02AF, "IPA Extensions" ) ;
		ucBlock[5] = new UcBlock( "02B0..02FF", 0x02B0, 0x02FF, "Spacing Modifier Letters" ) ;
		ucBlock[6] = new UcBlock( "0300..036F", 0x0300, 0x036F, "Combining Diacritical Marks" ) ;
		ucBlock[7] = new UcBlock( "0370..03FF", 0x0370, 0x03FF, "Greek and Coptic" ) ;
		ucBlock[8] = new UcBlock( "0400..04FF", 0x0400, 0x04FF, "Cyrillic" ) ;
		ucBlock[9] = new UcBlock( "0500..052F", 0x0500, 0x052F, "Cyrillic Supplement" ) ;
		ucBlock[10] = new UcBlock( "0530..058F", 0x0530, 0x058F, "Armenian" ) ;
		ucBlock[11] = new UcBlock( "0590..05FF", 0x0590, 0x05FF, "Hebrew" ) ;
		ucBlock[12] = new UcBlock( "0600..06FF", 0x0600, 0x06FF, "Arabic" ) ;
		ucBlock[13] = new UcBlock( "0700..074F", 0x0700, 0x074F, "Syriac" ) ;
		ucBlock[14] = new UcBlock( "0750..077F", 0x0750, 0x077F, "Arabic Supplement" ) ;
		ucBlock[15] = new UcBlock( "0780..07BF", 0x0780, 0x07BF, "Thaana" ) ;
		ucBlock[16] = new UcBlock( "0900..097F", 0x0900, 0x097F, "Devanagari" ) ;
		ucBlock[17] = new UcBlock( "0980..09FF", 0x0980, 0x09FF, "Bengali" ) ;
		ucBlock[18] = new UcBlock( "0A00..0A7F", 0x0A00, 0x0A7F, "Gurmukhi" ) ;
		ucBlock[19] = new UcBlock( "0A80..0AFF", 0x0A80, 0x0AFF, "Gujarati" ) ;
		ucBlock[20] = new UcBlock( "0B00..0B7F", 0x0B00, 0x0B7F, "Oriya" ) ;
		ucBlock[21] = new UcBlock( "0B80..0BFF", 0x0B80, 0x0BFF, "Tamil" ) ;
		ucBlock[22] = new UcBlock( "0C00..0C7F", 0x0C00, 0x0C7F, "Telugu" ) ;
		ucBlock[23] = new UcBlock( "0C80..0CFF", 0x0C80, 0x0CFF, "Kannada" ) ;
		ucBlock[24] = new UcBlock( "0D00..0D7F", 0x0D00, 0x0D7F, "Malayalam" ) ;
		ucBlock[25] = new UcBlock( "0D80..0DFF", 0x0D80, 0x0DFF, "Sinhala" ) ;
		ucBlock[26] = new UcBlock( "0E00..0E7F", 0x0E00, 0x0E7F, "Thai" ) ;
		ucBlock[27] = new UcBlock( "0E80..0EFF", 0x0E80, 0x0EFF, "Lao" ) ;
		ucBlock[28] = new UcBlock( "0F00..0FFF", 0x0F00, 0x0FFF, "Tibetan" ) ;
		ucBlock[29] = new UcBlock( "1000..109F", 0x1000, 0x109F, "Myanmar" ) ;
		ucBlock[30] = new UcBlock( "10A0..10FF", 0x10A0, 0x10FF, "Georgian" ) ;
		ucBlock[31] = new UcBlock( "1100..11FF", 0x1100, 0x11FF, "Hangul Jamo" ) ;
		ucBlock[32] = new UcBlock( "1200..137F", 0x1200, 0x137F, "Ethiopic" ) ;
		ucBlock[33] = new UcBlock( "1380..139F", 0x1380, 0x139F, "Ethiopic Supplement" ) ;
		ucBlock[34] = new UcBlock( "13A0..13FF", 0x13A0, 0x13FF, "Cherokee" ) ;
		ucBlock[35] = new UcBlock( "1400..167F", 0x1400, 0x167F, "Unified Canadian Aboriginal Syllabics" ) ;
		ucBlock[36] = new UcBlock( "1680..169F", 0x1680, 0x169F, "Ogham" ) ;
		ucBlock[37] = new UcBlock( "16A0..16FF", 0x16A0, 0x16FF, "Runic" ) ;
		ucBlock[38] = new UcBlock( "1700..171F", 0x1700, 0x171F, "Tagalog" ) ;
		ucBlock[39] = new UcBlock( "1720..173F", 0x1720, 0x173F, "Hanunoo" ) ;
		ucBlock[40] = new UcBlock( "1740..175F", 0x1740, 0x175F, "Buhid" ) ;
		ucBlock[41] = new UcBlock( "1760..177F", 0x1760, 0x177F, "Tagbanwa" ) ;
		ucBlock[42] = new UcBlock( "1780..17FF", 0x1780, 0x17FF, "Khmer" ) ;
		ucBlock[43] = new UcBlock( "1800..18AF", 0x1800, 0x18AF, "Mongolian" ) ;
		ucBlock[44] = new UcBlock( "1900..194F", 0x1900, 0x194F, "Limbu" ) ;
		ucBlock[45] = new UcBlock( "1950..197F", 0x1950, 0x197F, "Tai Le" ) ;
		ucBlock[46] = new UcBlock( "1980..19DF", 0x1980, 0x19DF, "New Tai Lue" ) ;
		ucBlock[47] = new UcBlock( "19E0..19FF", 0x19E0, 0x19FF, "Khmer Symbols" ) ;
		ucBlock[48] = new UcBlock( "1A00..1A1F", 0x1A00, 0x1A1F, "Buginese" ) ;
		ucBlock[49] = new UcBlock( "1D00..1D7F", 0x1D00, 0x1D7F, "Phonetic Extensions" ) ;
		ucBlock[50] = new UcBlock( "1D80..1DBF", 0x1D80, 0x1DBF, "Phonetic Extensions Supplement" ) ;
		ucBlock[51] = new UcBlock( "1DC0..1DFF", 0x1DC0, 0x1DFF, "Combining Diacritical Marks Supplement" ) ;
		ucBlock[52] = new UcBlock( "1E00..1EFF", 0x1E00, 0x1EFF, "Latin Extended Additional" ) ;
		ucBlock[53] = new UcBlock( "1F00..1FFF", 0x1F00, 0x1FFF, "Greek Extended" ) ;
		ucBlock[54] = new UcBlock( "2000..206F", 0x2000, 0x206F, "General Punctuation" ) ;
		ucBlock[55] = new UcBlock( "2070..209F", 0x2070, 0x209F, "Superscripts and Subscripts" ) ;
		ucBlock[56] = new UcBlock( "20A0..20CF", 0x20A0, 0x20CF, "Currency Symbols" ) ;
		ucBlock[57] = new UcBlock( "20D0..20FF", 0x20D0, 0x20FF, "Combining Diacritical Marks for Symbols" ) ;
		ucBlock[58] = new UcBlock( "2100..214F", 0x2100, 0x214F, "Letterlike Symbols" ) ;
		ucBlock[59] = new UcBlock( "2150..218F", 0x2150, 0x218F, "Number Forms" ) ;
		ucBlock[60] = new UcBlock( "2190..21FF", 0x2190, 0x21FF, "Arrows" ) ;
		ucBlock[61] = new UcBlock( "2200..22FF", 0x2200, 0x22FF, "Mathematical Operators" ) ;
		ucBlock[62] = new UcBlock( "2300..23FF", 0x2300, 0x23FF, "Miscellaneous Technical" ) ;
		ucBlock[63] = new UcBlock( "2400..243F", 0x2400, 0x243F, "Control Pictures" ) ;
		ucBlock[64] = new UcBlock( "2440..245F", 0x2440, 0x245F, "Optical Character Recognition" ) ;
		ucBlock[65] = new UcBlock( "2460..24FF", 0x2460, 0x24FF, "Enclosed Alphanumerics" ) ;
		ucBlock[66] = new UcBlock( "2500..257F", 0x2500, 0x257F, "Box Drawing" ) ;
		ucBlock[67] = new UcBlock( "2580..259F", 0x2580, 0x259F, "Block Elements" ) ;
		ucBlock[68] = new UcBlock( "25A0..25FF", 0x25A0, 0x25FF, "Geometric Shapes" ) ;
		ucBlock[69] = new UcBlock( "2600..26FF", 0x2600, 0x26FF, "Miscellaneous Symbols" ) ;
		ucBlock[70] = new UcBlock( "2700..27BF", 0x2700, 0x27BF, "Dingbats" ) ;
		ucBlock[71] = new UcBlock( "27C0..27EF", 0x27C0, 0x27EF, "Miscellaneous Mathematical Symbols-A" ) ;
		ucBlock[72] = new UcBlock( "27F0..27FF", 0x27F0, 0x27FF, "Supplemental Arrows-A" ) ;
		ucBlock[73] = new UcBlock( "2800..28FF", 0x2800, 0x28FF, "Braille Patterns" ) ;
		ucBlock[74] = new UcBlock( "2900..297F", 0x2900, 0x297F, "Supplemental Arrows-B" ) ;
		ucBlock[75] = new UcBlock( "2980..29FF", 0x2980, 0x29FF, "Miscellaneous Mathematical Symbols-B" ) ;
		ucBlock[76] = new UcBlock( "2A00..2AFF", 0x2A00, 0x2AFF, "Supplemental Mathematical Operators" ) ;
		ucBlock[77] = new UcBlock( "2B00..2BFF", 0x2B00, 0x2BFF, "Miscellaneous Symbols and Arrows" ) ;
		ucBlock[78] = new UcBlock( "2C00..2C5F", 0x2C00, 0x2C5F, "Glagolitic" ) ;
		ucBlock[79] = new UcBlock( "2C80..2CFF", 0x2C80, 0x2CFF, "Coptic" ) ;
		ucBlock[80] = new UcBlock( "2D00..2D2F", 0x2D00, 0x2D2F, "Georgian Supplement" ) ;
		ucBlock[81] = new UcBlock( "2D30..2D7F", 0x2D30, 0x2D7F, "Tifinagh" ) ;
		ucBlock[82] = new UcBlock( "2D80..2DDF", 0x2D80, 0x2DDF, "Ethiopic Extended" ) ;
		ucBlock[83] = new UcBlock( "2E00..2E7F", 0x2E00, 0x2E7F, "Supplemental Punctuation" ) ;
		ucBlock[84] = new UcBlock( "2E80..2EFF", 0x2E80, 0x2EFF, "CJK Radicals Supplement" ) ;
		ucBlock[85] = new UcBlock( "2F00..2FDF", 0x2F00, 0x2FDF, "Kangxi Radicals" ) ;
		ucBlock[86] = new UcBlock( "2FF0..2FFF", 0x2FF0, 0x2FFF, "Ideographic Description Characters" ) ;
		ucBlock[87] = new UcBlock( "3000..303F", 0x3000, 0x303F, "CJK Symbols and Punctuation" ) ;
		ucBlock[88] = new UcBlock( "3040..309F", 0x3040, 0x309F, "Hiragana" ) ;
		ucBlock[89] = new UcBlock( "30A0..30FF", 0x30A0, 0x30FF, "Katakana" ) ;
		ucBlock[90] = new UcBlock( "3100..312F", 0x3100, 0x312F, "Bopomofo" ) ;
		ucBlock[91] = new UcBlock( "3130..318F", 0x3130, 0x318F, "Hangul Compatibility Jamo" ) ;
		ucBlock[92] = new UcBlock( "3190..319F", 0x3190, 0x319F, "Kanbun" ) ;
		ucBlock[93] = new UcBlock( "31A0..31BF", 0x31A0, 0x31BF, "Bopomofo Extended" ) ;
		ucBlock[94] = new UcBlock( "31C0..31EF", 0x31C0, 0x31EF, "CJK Strokes" ) ;
		ucBlock[95] = new UcBlock( "31F0..31FF", 0x31F0, 0x31FF, "Katakana Phonetic Extensions" ) ;
		ucBlock[96] = new UcBlock( "3200..32FF", 0x3200, 0x32FF, "Enclosed CJK Letters and Months" ) ;
		ucBlock[97] = new UcBlock( "3300..33FF", 0x3300, 0x33FF, "CJK Compatibility" ) ;
		ucBlock[98] = new UcBlock( "3400..4DBF", 0x3400, 0x4DBF, "CJK Unified Ideographs Extension A" ) ;
		ucBlock[99] = new UcBlock( "4DC0..4DFF", 0x4DC0, 0x4DFF, "Yijing Hexagram Symbols" ) ;
		ucBlock[100] = new UcBlock( "4E00..9FFF", 0x4E00, 0x9FFF, "CJK Unified Ideographs" ) ;
		ucBlock[101] = new UcBlock( "A000..A48F", 0xA000, 0xA48F, "Yi Syllables" ) ;
		ucBlock[102] = new UcBlock( "A490..A4CF", 0xA490, 0xA4CF, "Yi Radicals" ) ;
		ucBlock[103] = new UcBlock( "A700..A71F", 0xA700, 0xA71F, "Modifier Tone Letters" ) ;
		ucBlock[104] = new UcBlock( "A800..A82F", 0xA800, 0xA82F, "Syloti Nagri" ) ;
		ucBlock[105] = new UcBlock( "AC00..D7AF", 0xAC00, 0xD7AF, "Hangul Syllables" ) ;
		ucBlock[106] = new UcBlock( "D800..DB7F", 0xD800, 0xDB7F, "High Surrogates" ) ;
		ucBlock[107] = new UcBlock( "DB80..DBFF", 0xDB80, 0xDBFF, "High Private Use Surrogates" ) ;
		ucBlock[108] = new UcBlock( "DC00..DFFF", 0xDC00, 0xDFFF, "Low Surrogates" ) ;
		ucBlock[109] = new UcBlock( "E000..F8FF", 0xE000, 0xF8FF, "Private Use Area" ) ;
		ucBlock[110] = new UcBlock( "F900..FAFF", 0xF900, 0xFAFF, "CJK Compatibility Ideographs" ) ;
		ucBlock[111] = new UcBlock( "FB00..FB4F", 0xFB00, 0xFB4F, "Alphabetic Presentation Forms" ) ;
		ucBlock[112] = new UcBlock( "FB50..FDFF", 0xFB50, 0xFDFF, "Arabic Presentation Forms-A" ) ;
		ucBlock[113] = new UcBlock( "FE00..FE0F", 0xFE00, 0xFE0F, "Variation Selectors" ) ;
		ucBlock[114] = new UcBlock( "FE10..FE1F", 0xFE10, 0xFE1F, "Vertical Forms" ) ;
		ucBlock[115] = new UcBlock( "FE20..FE2F", 0xFE20, 0xFE2F, "Combining Half Marks" ) ;
		ucBlock[116] = new UcBlock( "FE30..FE4F", 0xFE30, 0xFE4F, "CJK Compatibility Forms" ) ;
		ucBlock[117] = new UcBlock( "FE50..FE6F", 0xFE50, 0xFE6F, "Small Form Variants" ) ;
		ucBlock[118] = new UcBlock( "FE70..FEFF", 0xFE70, 0xFEFF, "Arabic Presentation Forms-B" ) ;
		ucBlock[119] = new UcBlock( "FF00..FFEF", 0xFF00, 0xFFEF, "Halfwidth and Fullwidth Forms" ) ;
		ucBlock[120] = new UcBlock( "FFF0..FFFF", 0xFFF0, 0xFFFF, "Specials" ) ;
		ucBlock[121] = new UcBlock( "10000..1007F", 0x10000, 0x1007F, "Linear B Syllabary" ) ;
		ucBlock[122] = new UcBlock( "10080..100FF", 0x10080, 0x100FF, "Linear B Ideograms" ) ;
		ucBlock[123] = new UcBlock( "10100..1013F", 0x10100, 0x1013F, "Aegean Numbers" ) ;
		ucBlock[124] = new UcBlock( "10140..1018F", 0x10140, 0x1018F, "Ancient Greek Numbers" ) ;
		ucBlock[125] = new UcBlock( "10300..1032F", 0x10300, 0x1032F, "Old Italic" ) ;
		ucBlock[126] = new UcBlock( "10330..1034F", 0x10330, 0x1034F, "Gothic" ) ;
		ucBlock[127] = new UcBlock( "10380..1039F", 0x10380, 0x1039F, "Ugaritic" ) ;
		ucBlock[128] = new UcBlock( "103A0..103DF", 0x103A0, 0x103DF, "Old Persian" ) ;
		ucBlock[129] = new UcBlock( "10400..1044F", 0x10400, 0x1044F, "Deseret" ) ;
		ucBlock[130] = new UcBlock( "10450..1047F", 0x10450, 0x1047F, "Shavian" ) ;
		ucBlock[131] = new UcBlock( "10480..104AF", 0x10480, 0x104AF, "Osmanya" ) ;
		ucBlock[132] = new UcBlock( "10800..1083F", 0x10800, 0x1083F, "Cypriot Syllabary" ) ;
		ucBlock[133] = new UcBlock( "10A00..10A5F", 0x10A00, 0x10A5F, "Kharoshthi" ) ;
		ucBlock[134] = new UcBlock( "1D000..1D0FF", 0x1D000, 0x1D0FF, "Byzantine Musical Symbols" ) ;
		ucBlock[135] = new UcBlock( "1D100..1D1FF", 0x1D100, 0x1D1FF, "Musical Symbols" ) ;
		ucBlock[136] = new UcBlock( "1D200..1D24F", 0x1D200, 0x1D24F, "Ancient Greek Musical Notation" ) ;
		ucBlock[137] = new UcBlock( "1D300..1D35F", 0x1D300, 0x1D35F, "Tai Xuan Jing Symbols" ) ;
		ucBlock[138] = new UcBlock( "1D400..1D7FF", 0x1D400, 0x1D7FF, "Mathematical Alphanumeric Symbols" ) ;
		ucBlock[139] = new UcBlock( "20000..2A6DF", 0x20000, 0x2A6DF, "CJK Unified Ideographs Extension B" ) ;
		ucBlock[140] = new UcBlock( "2F800..2FA1F", 0x2F800, 0x2FA1F, "CJK Compatibility Ideographs Supplement" ) ;
		ucBlock[141] = new UcBlock( "E0000..E007F", 0xE0000, 0xE007F, "Tags" ) ;
		ucBlock[142] = new UcBlock( "E0100..E01EF", 0xE0100, 0xE01EF, "Variation Selectors Supplement" ) ;
		ucBlock[143] = new UcBlock( "F0000..FFFFF", 0xF0000, 0xFFFFF, "Supplementary Private Use Area-A" ) ;
		ucBlock[144] = new UcBlock( "100000..10FFFF", 0x100000, 0x10FFFF, "Supplementary Private Use Area-B" ) ;

		ucEncodingVectors = new Hashtable<String, String>() ;
		String vector = ApplicationHelper.getClassNode( this, null, ApplicationConstant.PN_POSTSCRIPT_UNICODE ).get( ApplicationConstant.PK_POSTSCRIPT_DEFAULT, null ) ;
		if ( vector == null ) {
			vector = DEFAULT_FONTNAME ;
			for ( int cs=0 ; cs<256 ; cs++ ) {
				vector = vector+DEFAULT_CHARPROC+( cs!=255?" ":"" ) ;
			}
		}
		try {
			ucEncodingVectors.put( ApplicationConstant.PK_POSTSCRIPT_DEFAULT, vector ) ;
			String[] encoding = ApplicationHelper.getClassNode( this, null, ApplicationConstant.PN_POSTSCRIPT_UNICODE ).keys() ;
			for ( int e=0 ; e<encoding.length ; e++ ) {
				if ( encoding[e].matches( "[0-9a-fA-F]{4}\\.\\.[0-9a-fA-F]{4}-[0-9]+" ) ) {
					ucEncodingVectors.put( encoding[e], ApplicationHelper.getClassNode( this, null, ApplicationConstant.PN_POSTSCRIPT_UNICODE ).get( encoding[e], null ) ) ;
				}
			}
		} catch ( BackingStoreException e ) {
			throw new RuntimeException( e.toString() ) ;
		}
	} 

	private String truncate( double number ) {        
		java.text.NumberFormat numberFormat = java.text.NumberFormat.getInstance( java.util.Locale.UK ) ;

		numberFormat.setMaximumFractionDigits( precision ) ;
		numberFormat.setGroupingUsed( false ) ;

		return numberFormat.format( number ) ;
	} 

	public java.util.Vector ucFET( String string ) {        
		int block = -1, pblock = -1,
		chart = -1, pchart = -1, code = -1 , tcc ;
		String fe[], rt = "", t = "" ;
		java.util.Vector<java.util.Vector> FET = new java.util.Vector<java.util.Vector>() ;
		java.util.Vector<Object> fet ;
		java.text.StringCharacterIterator rti, si = new java.text.StringCharacterIterator( string ) ;

		for ( char sc=si.first() ; sc!=java.text.StringCharacterIterator.DONE; sc=si.next() ) {
			block = ucSearch( ucBlock, 0, ucBlock.length, sc ) ;
			chart = ( sc-ucBlock[block].start )/256 ;
			code = ( sc-ucBlock[block].start )%256 ;
			if ( block != pblock || chart != pchart ) {
				if ( pblock != -1 ) {
					try {
						fe = ( (String) ucEncodingVectors.get( ucBlock[pblock].block+"-"+new Integer( pchart ).toString() ) ).split( ":" ) ;
					} catch ( NullPointerException e ) {
						fe = ( (String) ucEncodingVectors.get( ApplicationConstant.PK_POSTSCRIPT_DEFAULT ) ).split( ":" ) ;
					}
					tcc = 0 ;
					rti = new java.text.StringCharacterIterator( rt ) ;
					for ( char rtc=rti.first() ; rtc!=java.text.StringCharacterIterator.DONE; rtc=rti.next() ) {
						Character c = new Character( rtc ) ;
						if ( ! c.toString().matches( "[\\p{Print} ]" ) || c.charValue() == '(' || c.charValue() == ')' ) {
							int o2 = rtc/( 8*8 ) ;
							int o1 = rtc%( 8*8 )/8 ;
							int o0 = rtc%8 ;
							t=t+"\\"+new Integer( o2 ).toString()+new Integer( o1 ).toString()+new Integer( o0 ).toString() ;
							tcc = tcc+3 ;
						} else {
							t=t+new Character( rtc ).toString() ;
							tcc++ ;
						}
						if ( tcc%( scanline-2 ) == 0 ) {
							fet = new java.util.Vector<Object>() ;
							fet.add( fe[0] ) ; fet.add( fe[1].split( " " ) ) ; fet.add( "("+t+")" ) ;
							FET.add( fet ) ;
							t = "" ;
						}
					}
					fet = new java.util.Vector<Object>() ;
					fet.add( fe[0] ) ; fet.add( fe[1].split( " " ) ) ; fet.add( "("+t+")" ) ;
					FET.add( fet ) ;
					rt = t = "" ;
				}
				pblock = block ;
				pchart = chart ;
			}
			rt = rt+new Character( (char) code ).toString() ;
		}
		try {
			fe = ( (String) ucEncodingVectors.get( ucBlock[block].block+"-"+new Integer( chart ).toString() ) ).split( ":" ) ;
		} catch ( NullPointerException e ) {
			fe = ( (String) ucEncodingVectors.get( "default" ) ).split( ":" ) ;
		}
		tcc = 0 ;
		rti = new java.text.StringCharacterIterator( rt ) ;
		for ( char rtc=rti.first() ; rtc!=java.text.StringCharacterIterator.DONE; rtc=rti.next() ) {
			Character c = new Character( rtc ) ;
			if ( ! c.toString().matches( "[\\p{Print} ]" ) || c.charValue() == '(' || c.charValue() == ')' ) {
				int o2 = rtc/( 8*8 ) ;
				int o1 = rtc%( 8*8 )/8 ;
				int o0 = rtc%8 ;
				t=t+"\\"+new Integer( o2 ).toString()+new Integer( o1 ).toString()+new Integer( o0 ).toString() ;
				tcc = tcc+3 ;
			} else {
				t=t+new Character( rtc ).toString() ;
				tcc++ ;
			}
			if ( tcc%( scanline-2 ) == 0 ) {
				fet = new java.util.Vector<Object>() ;
				fet.add( fe[0] ) ; fet.add( fe[1].split( " " ) ) ; fet.add( "("+t+")" ) ;
				FET.add( fet ) ;
				t = "" ;
			}
		}
		fet = new java.util.Vector<Object>() ;
		fet.add( fe[0] ) ; fet.add( fe[1].split( " " ) ) ; fet.add( "("+t+")" ) ;
		FET.add( fet ) ;

		return FET ;
	} 

	private static int ucSearch( PostscriptStream.UcBlock block[], int start, int end, int code ) {        
		int m = ( start+end )/2 ;

		if ( block[m].start < code ) {
			if ( block[m].end >= code ) {
				return m ;
			} else {
				return ucSearch( block, m+1, end, code ) ;
			}
		} else if ( block[m].start > code ) {
			return ucSearch( block, start, m-1, code ) ;
		} else {
			return m ;
		}
	} 

	public void array( boolean begin ) {        
		print( begin?"[\n":"]\n" ) ;
	} 

	public void proc( boolean begin ) {        
		print( begin?"{\n":"}\n" ) ;
	}

	public void push( boolean bool ) {        
		print( ( bool?"true":"false" )+"\n" ) ;
	}

	public void push( int num ) {        
		print( num+"\n" ) ;
	} 

	public void push( double num ) {        
		print( truncate( num )+"\n" ) ;
	} 

	public void push( String string ) throws ParameterNotValidException {        
		if ( ! ( string.matches( "^/" ) || string.matches( "^(.*)$" ) ) ) {
			throw new ParameterNotValidException( string ) ;
		}
		print( string+"\n" ) ;
	} 

	public void push( String[] string ) throws ParameterNotValidException {        
		this.array( true ) ;
		for ( int i=0 ; i<string.length ; i++ ) {
			push( string[i] ) ;
		}
		this.array( false ) ;
	}

	public void custom( String def ) throws ParameterNotValidException {
		if ( ! this.prolog.containsKey( "/"+def ) ) {
			throw new ParameterNotValidException( "/"+def ) ;
		}

		print( def+"\n" ) ;
	}

	public class Operator {

		public void add() {        
			print( "add\n" ) ;
		}

		public void add( int num ) {        
			push( num ) ;
			add() ;
		}

		public void add( double num ) {        
			push( num ) ;
			add() ;
		}

		public void arc() {        
			print( "arc\n" ) ;
		}

		public void begin() {        
			print( "begin\n" ) ;
		}

		public void bind() {        
			print( "bind\n" ) ;
		}

		public void charpath() {        
			print( "charpath\n" ) ;
		}

		public void charpath( String string, boolean bool ) throws ParameterNotValidException {
			push( string ) ;
			push( bool ) ;
			charpath() ;
		}

		public void cleartomark() {        
			print( "cleartomark\n" ) ;
		}

		public void clip() {        
			print( "clip\n" ) ;
		}

		public void closepath() {        
			print( "closepath\n" ) ;
		}

		public void copy( ) {
			print( "copy\n" ) ;
		}

		public void copy( int num ) {
			push( num ) ;
			copy() ;
		}

		public void counttomark() {        
			print( "counttomark\n" ) ;
		}

		public void currentdict() {        
			print( "currentdict\n" ) ;
		}

		public void currentpoint() {        
			print( "currentpoint\n" ) ;
		}

		public void def() {        
			print( "def\n" ) ;
		}

		public void definefont() {        
			print( "definefont\n" ) ;
		}

		public void dict() {        
			print( "dict\n" ) ;
		}

		public void dict( double num ) {        
			push( num ) ;
			dict() ;
		}

		public void div() {        
			print( "div\n" ) ;
		}

		public void div( double num ) {        
			push( num ) ;
			div() ;
		}

		public void dup() {        
			print( "dup\n" ) ;
		}

		public void dup( int num ) {        
			for ( int i=0 ; i<num ; i++ ) {
				dup() ;
			}
		}

		public void end() {        
			print( "end\n" ) ;
		}

		public void exch() {        
			print( "exch\n" ) ;
		}

		public void fill() {        
			print( "fill\n" ) ;
		}

		public void get( int index ) {        
			push( index ) ;
			print( "get\n" ) ;
		}

		public void grestore() {        
			print( "grestore\n" ) ;
		}

		public void gsave() {        
			print( "gsave\n" ) ;
		}

		public void lineto() {
			print( "lineto\n" ) ;
		}

		public void lineto( double[] xy ) {        
			push( xy[0] ) ;
			push( xy[1] ) ;
			lineto() ;
		}

		public void mark() {        
			print( "mark\n" ) ;
		}

		public void moveto() {        
			print( "moveto\n" ) ;
		}

		public void moveto( double[] xy ) {
			push( xy[0] ) ;
			push( xy[1] ) ;
			moveto() ;
		}

		public void mul() {        
			print( "mul\n" ) ;
		}

		public void mul( double num ) {        
			push( num ) ;
			mul() ;
		}

		public void newpath() {        
			print( "newpath\n" ) ;
		}

		public void pathbbox() {        
			print( "pathbbox\n" ) ;
		}

		public void pop() {        
			print( "pop\n" ) ;
		}

		public void pop( int num ) {        
			for ( int i=0 ; i<num ; i++ ) {
				pop() ;
			}
		}

		public void put( int index, boolean bool ) {        
			push( index ) ;
			push( bool ) ;
			print( "put\n" ) ;
		}

		public void restore( String save ) throws ParameterNotValidException {        
			push( save ) ;
			print( "restore\n" ) ;
		} 

		public void roll() {
			print( "roll\n" ) ;
		}

		public void roll( int n, int j ) {        
			push( n ) ;
			push( j ) ;
			roll() ;
		}

		public void rotate( double angle ) {        
			push( angle ) ;
			print( "rotate\n" ) ;
		}

		public void save( String save ) throws ParameterNotValidException {        
			push( save ) ;
			print( "save\n" ) ;
			def() ;
		}

		public void scale( double s ) {        
			push( s ) ;
			dup() ;
			print( "scale\n" ) ;
		}

		public void selectfont() {        
			print( "selectfont\n" ) ;
		}

		public void selectfont( String key, double scale ) throws ParameterNotValidException {        
			push( key ) ;
			push( scale ) ;
			selectfont() ;
		}

		public void setdash() {        
			print( "setdash\n" ) ;
		}

		public void setdash( double dash ) {
			array( true ) ;
			if ( dash>0 ) {
				push( dash ) ;
			}
			array( false ) ;
			push( 0 ) ;
			setdash() ;
		}

		public void setgray( double num ) {        
			push( num ) ;
			print( "setgray\n" ) ;
		} 

		public void setlinewidth( double num ) {        
			push( num ) ;
			print( "setlinewidth\n" ) ;
		}

		public void show() {        
			print( "show\n" ) ;
		}

		public void showpage() {        
			print( "showpage\n" ) ;
		}

		public void stringwidth() {        
			print( "stringwidth\n" ) ;
		}

		public void stroke() {        
			gsave() ;
			print( "stroke\n" ) ;
			grestore() ;
		}

		public void sub() {        
			print( "sub\n" ) ;
		}

		public void sub( double num ) {        
			push( num ) ;
			sub() ;
		}

		public void translate() {        
			print( "translate\n" ) ;
		}

		public void translate( double[] t ) {        
			push( t[0] ) ;
			push( t[1] ) ;
			translate() ;
		} 
	}

	public class DSC {

		public void creator( String creator ) {        
			print( "%%Creator: "+creator+"\n" ) ;
		} 

		public void creationDate( String creationDate ) {        
			print( "%%CreationDate: "+creationDate+"\n" ) ;
		} 

		public void beginProlog() {        
			print( "%%BeginProlog\n" ) ;
		} 

		public void endProlog() {        
			print( "%%EndProlog\n" ) ;
		} 

		public void endComments() {        
			print( "%%EndComments\n" ) ;
		} 

		public void beginSetup() {        
			print( "%%BeginSetup\n" ) ;
		} 

		public void endSetup() {        
			print( "%%EndSetup\n" ) ;
		} 

		public void page( String label, int ordinal ) {        
			print( "%%Page: "+( label == null ? String.valueOf( ordinal ) : label )+" "+ordinal+"\n" ) ;
		} 

		public void beginPageSetup() {        
			print( "%%BeginPageSetup\n" ) ;
		} 

		public void endPageSetup() {        
			print( "%%EndPageSetup\n" ) ;
		} 

		public void pageTrailer() {        
			print( "%%PageTrailer\n" ) ;
		} 

		public void trailer() {        
			print( "%%Trailer\n" ) ;
		} 

		public void eOF() {        
			print( "%%EOF\n" ) ;
		} 

		public void beginObject( String name, String code ) {        
			print( "%%BeginObject "+name+( code == null ? "" : code )+"\n" ) ;
		} 

		public void endObject() {        
			print( "%%EndObject\n" ) ;
		} 
	}

	private class UcBlock {

		public String block = null ;

		public int start = 0 ;
		public int end = 0 ;

		public String name = null ;

		public  UcBlock( String block, int start, int end, String name ) {        
			this.block = new String( block ) ;
			this.start = start ;
			this.end = end ;
			this.name = new String( name ) ;
		} 
	}

	public void emitDSCHeader() {
		print( "%!PS-Adobe-3.0\n" ) ;
		dsc.creator( getClass().getName() ) ;
		dsc.creationDate( new java.util.Date().toString() ) ;
		dsc.endComments() ;
	}

	public void emitDSCProlog() {
		String node, fontname[], procedure[] ;

		dsc.beginProlog() ;

		try {
			fontname = ApplicationHelper.getClassNode( this, null, ApplicationConstant.PN_POSTSCRIPT_TYPE3 ).childrenNames() ;
			for ( int f=0 ; f<fontname.length ; f++ ) {
				push( "/"+fontname[f] ) ;

				node = ApplicationConstant.PN_POSTSCRIPT_TYPE3+"/"+fontname[f] ;
				procedure = ApplicationHelper.getClassNode( this, null, node ).keys() ;

				operator.dict( procedure.length ) ;
				operator.begin() ;
				for ( int p=0 ; p<procedure.length ; p++ ) {
					emitProcedureDefintion( procedure[p],
							ApplicationHelper.getClassNode( this, null, node ).get( procedure[p], null ) ) ;
				}
				operator.currentdict() ;
				operator.end() ;

				operator.definefont() ;
				operator.pop() ;
			}
		} catch ( BackingStoreException e ) {
			throw new RuntimeException( e.toString() ) ;
		} catch ( ParameterNotValidException e ) { // emitProcedureDefintion failed
			throw new RuntimeException( e.toString() ) ;
		}

		prolog.clear() ;
		try {
			node = ApplicationConstant.PN_POSTSCRIPT_PROLOG ;
			procedure = ApplicationHelper.getClassNode( this, null, node ).keys() ;
			for ( int p=0 ; p<procedure.length ; p++ ) {
				emitProcedureDefintion( procedure[p],
						ApplicationHelper.getClassNode( this, null, node ).get( procedure[p], null ) ) ;
				prolog.put( procedure[p], procedure[p] ) ;
			}
		} catch ( BackingStoreException e ) {
			throw new RuntimeException( e.toString() ) ;
		} catch ( ParameterNotValidException e ) { // emitProcedureDefintion failed
			throw new RuntimeException( e.toString() ) ;
		}

		dsc.endProlog() ;
	}

	private void emitProcedureDefintion( String procedure, String definition ) throws ParameterNotValidException {
		String[] token ;

		push( procedure ) ;
		token = definition.split( " ") ;
		for ( int t=0 ; t<token.length ; t++ ) {
			print( token[t]+"\n" ) ;
		}
		operator.def() ;
	}

	public void emitDSCTrailer() {
		dsc.trailer() ;
		dsc.eOF() ;
	}
}
