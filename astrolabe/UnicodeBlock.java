
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
		.replaceAll( "[ _-]+", "_" ) ;
		this.nice = nice
		.trim() ;

		this.start = start ;
		this.size = size ;

		block.put( this.name, this ) ;
	}

	public static final UnicodeBlock forName( String name ) {
		UnicodeBlock block ;
		String[] legacyName = new String[] {
				"GREEK",
				"COMBINING_MARKS_FOR_SYMBOLS" } ;
		String[] actualName = new String[] {
				"GREEK_AND_COPTIC",
				"COMBINING_DIACRITICAL_MARKS_FOR_SYMBOLS" } ;

		block = UnicodeBlock.block.get( name
				.trim()
				.toUpperCase()
				.replaceAll( "[ _-]+", "_") ) ;

		if ( block == null )
			for ( int n=0 ; n<legacyName.length ; n++ )
				if ( name.equals( legacyName[n] ) ) {
					block = UnicodeBlock.forName( actualName[n] ) ;
					break ;
				}

		return block ;
	}

	public static final UnicodeBlock BASIC_LATIN = 
		new UnicodeBlock( "BASIC_LATIN", "Basic Latin", 0x0000, 0x007F-0x0000+1 ) ; 
	public static final UnicodeBlock LATIN_1_SUPPLEMENT = 
		new UnicodeBlock( "LATIN_1_SUPPLEMENT", "Latin-1 Supplement", 0x0080, 0x00FF-0x0080+1 ) ; 
	public static final UnicodeBlock LATIN_EXTENDED_A = 
		new UnicodeBlock( "LATIN_EXTENDED_A", "Latin Extended-A", 0x0100, 0x017F-0x0100+1 ) ; 
	public static final UnicodeBlock LATIN_EXTENDED_B = 
		new UnicodeBlock( "LATIN_EXTENDED_B", "Latin Extended-B", 0x0180, 0x024F-0x0180+1 ) ; 
	public static final UnicodeBlock IPA_EXTENSIONS = 
		new UnicodeBlock( "IPA_EXTENSIONS", "IPA Extensions", 0x0250, 0x02AF-0x0250+1 ) ; 
	public static final UnicodeBlock SPACING_MODIFIER_LETTERS = 
		new UnicodeBlock( "SPACING_MODIFIER_LETTERS", "Spacing Modifier Letters", 0x02B0, 0x02FF-0x02B0+1 ) ; 
	public static final UnicodeBlock COMBINING_DIACRITICAL_MARKS = 
		new UnicodeBlock( "COMBINING_DIACRITICAL_MARKS", "Combining Diacritical Marks", 0x0300, 0x036F-0x0300+1 ) ; 
	public static final UnicodeBlock GREEK_AND_COPTIC = 
		new UnicodeBlock( "GREEK_AND_COPTIC", "Greek and Coptic", 0x0370, 0x03FF-0x0370+1 ) ; 
	public static final UnicodeBlock CYRILLIC = 
		new UnicodeBlock( "CYRILLIC", "Cyrillic", 0x0400, 0x04FF-0x0400+1 ) ; 
	public static final UnicodeBlock CYRILLIC_SUPPLEMENT = 
		new UnicodeBlock( "CYRILLIC_SUPPLEMENT", "Cyrillic Supplement", 0x0500, 0x052F-0x0500+1 ) ; 
	public static final UnicodeBlock ARMENIAN = 
		new UnicodeBlock( "ARMENIAN", "Armenian", 0x0530, 0x058F-0x0530+1 ) ; 
	public static final UnicodeBlock HEBREW = 
		new UnicodeBlock( "HEBREW", "Hebrew", 0x0590, 0x05FF-0x0590+1 ) ; 
	public static final UnicodeBlock ARABIC = 
		new UnicodeBlock( "ARABIC", "Arabic", 0x0600, 0x06FF-0x0600+1 ) ; 
	public static final UnicodeBlock SYRIAC = 
		new UnicodeBlock( "SYRIAC", "Syriac", 0x0700, 0x074F-0x0700+1 ) ; 
	public static final UnicodeBlock ARABIC_SUPPLEMENT = 
		new UnicodeBlock( "ARABIC_SUPPLEMENT", "Arabic Supplement", 0x0750, 0x077F-0x0750+1 ) ; 
	public static final UnicodeBlock THAANA = 
		new UnicodeBlock( "THAANA", "Thaana", 0x0780, 0x07BF-0x0780+1 ) ; 
	public static final UnicodeBlock DEVANAGARI = 
		new UnicodeBlock( "DEVANAGARI", "Devanagari", 0x0900, 0x097F-0x0900+1 ) ; 
	public static final UnicodeBlock BENGALI = 
		new UnicodeBlock( "BENGALI", "Bengali", 0x0980, 0x09FF-0x0980+1 ) ; 
	public static final UnicodeBlock GURMUKHI = 
		new UnicodeBlock( "GURMUKHI", "Gurmukhi", 0x0A00, 0x0A7F-0x0A00+1 ) ; 
	public static final UnicodeBlock GUJARATI = 
		new UnicodeBlock( "GUJARATI", "Gujarati", 0x0A80, 0x0AFF-0x0A80+1 ) ; 
	public static final UnicodeBlock ORIYA = 
		new UnicodeBlock( "ORIYA", "Oriya", 0x0B00, 0x0B7F-0x0B00+1 ) ; 
	public static final UnicodeBlock TAMIL = 
		new UnicodeBlock( "TAMIL", "Tamil", 0x0B80, 0x0BFF-0x0B80+1 ) ; 
	public static final UnicodeBlock TELUGU = 
		new UnicodeBlock( "TELUGU", "Telugu", 0x0C00, 0x0C7F-0x0C00+1 ) ; 
	public static final UnicodeBlock KANNADA = 
		new UnicodeBlock( "KANNADA", "Kannada", 0x0C80, 0x0CFF-0x0C80+1 ) ; 
	public static final UnicodeBlock MALAYALAM = 
		new UnicodeBlock( "MALAYALAM", "Malayalam", 0x0D00, 0x0D7F-0x0D00+1 ) ; 
	public static final UnicodeBlock SINHALA = 
		new UnicodeBlock( "SINHALA", "Sinhala", 0x0D80, 0x0DFF-0x0D80+1 ) ; 
	public static final UnicodeBlock THAI = 
		new UnicodeBlock( "THAI", "Thai", 0x0E00, 0x0E7F-0x0E00+1 ) ; 
	public static final UnicodeBlock LAO = 
		new UnicodeBlock( "LAO", "Lao", 0x0E80, 0x0EFF-0x0E80+1 ) ; 
	public static final UnicodeBlock TIBETAN = 
		new UnicodeBlock( "TIBETAN", "Tibetan", 0x0F00, 0x0FFF-0x0F00+1 ) ; 
	public static final UnicodeBlock MYANMAR = 
		new UnicodeBlock( "MYANMAR", "Myanmar", 0x1000, 0x109F-0x1000+1 ) ; 
	public static final UnicodeBlock GEORGIAN = 
		new UnicodeBlock( "GEORGIAN", "Georgian", 0x10A0, 0x10FF-0x10A0+1 ) ; 
	public static final UnicodeBlock HANGUL_JAMO = 
		new UnicodeBlock( "HANGUL_JAMO", "Hangul Jamo", 0x1100, 0x11FF-0x1100+1 ) ; 
	public static final UnicodeBlock ETHIOPIC = 
		new UnicodeBlock( "ETHIOPIC", "Ethiopic", 0x1200, 0x137F-0x1200+1 ) ; 
	public static final UnicodeBlock ETHIOPIC_SUPPLEMENT = 
		new UnicodeBlock( "ETHIOPIC_SUPPLEMENT", "Ethiopic Supplement", 0x1380, 0x139F-0x1380+1 ) ; 
	public static final UnicodeBlock CHEROKEE = 
		new UnicodeBlock( "CHEROKEE", "Cherokee", 0x13A0, 0x13FF-0x13A0+1 ) ; 
	public static final UnicodeBlock UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS = 
		new UnicodeBlock( "UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS", "Unified Canadian Aboriginal Syllabics", 0x1400, 0x167F-0x1400+1 ) ; 
	public static final UnicodeBlock OGHAM = 
		new UnicodeBlock( "OGHAM", "Ogham", 0x1680, 0x169F-0x1680+1 ) ; 
	public static final UnicodeBlock RUNIC = 
		new UnicodeBlock( "RUNIC", "Runic", 0x16A0, 0x16FF-0x16A0+1 ) ; 
	public static final UnicodeBlock TAGALOG = 
		new UnicodeBlock( "TAGALOG", "Tagalog", 0x1700, 0x171F-0x1700+1 ) ; 
	public static final UnicodeBlock HANUNOO = 
		new UnicodeBlock( "HANUNOO", "Hanunoo", 0x1720, 0x173F-0x1720+1 ) ; 
	public static final UnicodeBlock BUHID = 
		new UnicodeBlock( "BUHID", "Buhid", 0x1740, 0x175F-0x1740+1 ) ; 
	public static final UnicodeBlock TAGBANWA = 
		new UnicodeBlock( "TAGBANWA", "Tagbanwa", 0x1760, 0x177F-0x1760+1 ) ; 
	public static final UnicodeBlock KHMER = 
		new UnicodeBlock( "KHMER", "Khmer", 0x1780, 0x17FF-0x1780+1 ) ; 
	public static final UnicodeBlock MONGOLIAN = 
		new UnicodeBlock( "MONGOLIAN", "Mongolian", 0x1800, 0x18AF-0x1800+1 ) ; 
	public static final UnicodeBlock LIMBU = 
		new UnicodeBlock( "LIMBU", "Limbu", 0x1900, 0x194F-0x1900+1 ) ; 
	public static final UnicodeBlock TAI_LE = 
		new UnicodeBlock( "TAI_LE", "Tai Le", 0x1950, 0x197F-0x1950+1 ) ; 
	public static final UnicodeBlock NEW_TAI_LUE = 
		new UnicodeBlock( "NEW_TAI_LUE", "New Tai Lue", 0x1980, 0x19DF-0x1980+1 ) ; 
	public static final UnicodeBlock KHMER_SYMBOLS = 
		new UnicodeBlock( "KHMER_SYMBOLS", "Khmer Symbols", 0x19E0, 0x19FF-0x19E0+1 ) ; 
	public static final UnicodeBlock BUGINESE = 
		new UnicodeBlock( "BUGINESE", "Buginese", 0x1A00, 0x1A1F-0x1A00+1 ) ; 
	public static final UnicodeBlock PHONETIC_EXTENSIONS = 
		new UnicodeBlock( "PHONETIC_EXTENSIONS", "Phonetic Extensions", 0x1D00, 0x1D7F-0x1D00+1 ) ; 
	public static final UnicodeBlock PHONETIC_EXTENSIONS_SUPPLEMENT = 
		new UnicodeBlock( "PHONETIC_EXTENSIONS_SUPPLEMENT", "Phonetic Extensions Supplement", 0x1D80, 0x1DBF-0x1D80+1 ) ; 
	public static final UnicodeBlock COMBINING_DIACRITICAL_MARKS_SUPPLEMENT = 
		new UnicodeBlock( "COMBINING_DIACRITICAL_MARKS_SUPPLEMENT", "Combining Diacritical Marks Supplement", 0x1DC0, 0x1DFF-0x1DC0+1 ) ; 
	public static final UnicodeBlock LATIN_EXTENDED_ADDITIONAL = 
		new UnicodeBlock( "LATIN_EXTENDED_ADDITIONAL", "Latin Extended Additional", 0x1E00, 0x1EFF-0x1E00+1 ) ; 
	public static final UnicodeBlock GREEK_EXTENDED = 
		new UnicodeBlock( "GREEK_EXTENDED", "Greek Extended", 0x1F00, 0x1FFF-0x1F00+1 ) ; 
	public static final UnicodeBlock GENERAL_PUNCTUATION = 
		new UnicodeBlock( "GENERAL_PUNCTUATION", "General Punctuation", 0x2000, 0x206F-0x2000+1 ) ; 
	public static final UnicodeBlock SUPERSCRIPTS_AND_SUBSCRIPTS = 
		new UnicodeBlock( "SUPERSCRIPTS_AND_SUBSCRIPTS", "Superscripts and Subscripts", 0x2070, 0x209F-0x2070+1 ) ; 
	public static final UnicodeBlock CURRENCY_SYMBOLS = 
		new UnicodeBlock( "CURRENCY_SYMBOLS", "Currency Symbols", 0x20A0, 0x20CF-0x20A0+1 ) ; 
	public static final UnicodeBlock COMBINING_DIACRITICAL_MARKS_FOR_SYMBOLS = 
		new UnicodeBlock( "COMBINING_DIACRITICAL_MARKS_FOR_SYMBOLS", "Combining Diacritical Marks for Symbols", 0x20D0, 0x20FF-0x20D0+1 ) ; 
	public static final UnicodeBlock LETTERLIKE_SYMBOLS = 
		new UnicodeBlock( "LETTERLIKE_SYMBOLS", "Letterlike Symbols", 0x2100, 0x214F-0x2100+1 ) ; 
	public static final UnicodeBlock NUMBER_FORMS = 
		new UnicodeBlock( "NUMBER_FORMS", "Number Forms", 0x2150, 0x218F-0x2150+1 ) ; 
	public static final UnicodeBlock ARROWS = 
		new UnicodeBlock( "ARROWS", "Arrows", 0x2190, 0x21FF-0x2190+1 ) ; 
	public static final UnicodeBlock MATHEMATICAL_OPERATORS = 
		new UnicodeBlock( "MATHEMATICAL_OPERATORS", "Mathematical Operators", 0x2200, 0x22FF-0x2200+1 ) ; 
	public static final UnicodeBlock MISCELLANEOUS_TECHNICAL = 
		new UnicodeBlock( "MISCELLANEOUS_TECHNICAL", "Miscellaneous Technical", 0x2300, 0x23FF-0x2300+1 ) ; 
	public static final UnicodeBlock CONTROL_PICTURES = 
		new UnicodeBlock( "CONTROL_PICTURES", "Control Pictures", 0x2400, 0x243F-0x2400+1 ) ; 
	public static final UnicodeBlock OPTICAL_CHARACTER_RECOGNITION = 
		new UnicodeBlock( "OPTICAL_CHARACTER_RECOGNITION", "Optical Character Recognition", 0x2440, 0x245F-0x2440+1 ) ; 
	public static final UnicodeBlock ENCLOSED_ALPHANUMERICS = 
		new UnicodeBlock( "ENCLOSED_ALPHANUMERICS", "Enclosed Alphanumerics", 0x2460, 0x24FF-0x2460+1 ) ; 
	public static final UnicodeBlock BOX_DRAWING = 
		new UnicodeBlock( "BOX_DRAWING", "Box Drawing", 0x2500, 0x257F-0x2500+1 ) ; 
	public static final UnicodeBlock BLOCK_ELEMENTS = 
		new UnicodeBlock( "BLOCK_ELEMENTS", "Block Elements", 0x2580, 0x259F-0x2580+1 ) ; 
	public static final UnicodeBlock GEOMETRIC_SHAPES = 
		new UnicodeBlock( "GEOMETRIC_SHAPES", "Geometric Shapes", 0x25A0, 0x25FF-0x25A0+1 ) ; 
	public static final UnicodeBlock MISCELLANEOUS_SYMBOLS = 
		new UnicodeBlock( "MISCELLANEOUS_SYMBOLS", "Miscellaneous Symbols", 0x2600, 0x26FF-0x2600+1 ) ; 
	public static final UnicodeBlock DINGBATS = 
		new UnicodeBlock( "DINGBATS", "Dingbats", 0x2700, 0x27BF-0x2700+1 ) ; 
	public static final UnicodeBlock MISCELLANEOUS_MATHEMATICAL_SYMBOLS_A = 
		new UnicodeBlock( "MISCELLANEOUS_MATHEMATICAL_SYMBOLS_A", "Miscellaneous Mathematical Symbols-A", 0x27C0, 0x27EF-0x27C0+1 ) ; 
	public static final UnicodeBlock SUPPLEMENTAL_ARROWS_A = 
		new UnicodeBlock( "SUPPLEMENTAL_ARROWS_A", "Supplemental Arrows-A", 0x27F0, 0x27FF-0x27F0+1 ) ; 
	public static final UnicodeBlock BRAILLE_PATTERNS = 
		new UnicodeBlock( "BRAILLE_PATTERNS", "Braille Patterns", 0x2800, 0x28FF-0x2800+1 ) ; 
	public static final UnicodeBlock SUPPLEMENTAL_ARROWS_B = 
		new UnicodeBlock( "SUPPLEMENTAL_ARROWS_B", "Supplemental Arrows-B", 0x2900, 0x297F-0x2900+1 ) ; 
	public static final UnicodeBlock MISCELLANEOUS_MATHEMATICAL_SYMBOLS_B = 
		new UnicodeBlock( "MISCELLANEOUS_MATHEMATICAL_SYMBOLS_B", "Miscellaneous Mathematical Symbols-B", 0x2980, 0x29FF-0x2980+1 ) ; 
	public static final UnicodeBlock SUPPLEMENTAL_MATHEMATICAL_OPERATORS = 
		new UnicodeBlock( "SUPPLEMENTAL_MATHEMATICAL_OPERATORS", "Supplemental Mathematical Operators", 0x2A00, 0x2AFF-0x2A00+1 ) ; 
	public static final UnicodeBlock MISCELLANEOUS_SYMBOLS_AND_ARROWS = 
		new UnicodeBlock( "MISCELLANEOUS_SYMBOLS_AND_ARROWS", "Miscellaneous Symbols and Arrows", 0x2B00, 0x2BFF-0x2B00+1 ) ; 
	public static final UnicodeBlock GLAGOLITIC = 
		new UnicodeBlock( "GLAGOLITIC", "Glagolitic", 0x2C00, 0x2C5F-0x2C00+1 ) ; 
	public static final UnicodeBlock COPTIC = 
		new UnicodeBlock( "COPTIC", "Coptic", 0x2C80, 0x2CFF-0x2C80+1 ) ; 
	public static final UnicodeBlock GEORGIAN_SUPPLEMENT = 
		new UnicodeBlock( "GEORGIAN_SUPPLEMENT", "Georgian Supplement", 0x2D00, 0x2D2F-0x2D00+1 ) ; 
	public static final UnicodeBlock TIFINAGH = 
		new UnicodeBlock( "TIFINAGH", "Tifinagh", 0x2D30, 0x2D7F-0x2D30+1 ) ; 
	public static final UnicodeBlock ETHIOPIC_EXTENDED = 
		new UnicodeBlock( "ETHIOPIC_EXTENDED", "Ethiopic Extended", 0x2D80, 0x2DDF-0x2D80+1 ) ; 
	public static final UnicodeBlock SUPPLEMENTAL_PUNCTUATION = 
		new UnicodeBlock( "SUPPLEMENTAL_PUNCTUATION", "Supplemental Punctuation", 0x2E00, 0x2E7F-0x2E00+1 ) ; 
	public static final UnicodeBlock CJK_RADICALS_SUPPLEMENT = 
		new UnicodeBlock( "CJK_RADICALS_SUPPLEMENT", "CJK Radicals Supplement", 0x2E80, 0x2EFF-0x2E80+1 ) ; 
	public static final UnicodeBlock KANGXI_RADICALS = 
		new UnicodeBlock( "KANGXI_RADICALS", "Kangxi Radicals", 0x2F00, 0x2FDF-0x2F00+1 ) ; 
	public static final UnicodeBlock IDEOGRAPHIC_DESCRIPTION_CHARACTERS = 
		new UnicodeBlock( "IDEOGRAPHIC_DESCRIPTION_CHARACTERS", "Ideographic Description Characters", 0x2FF0, 0x2FFF-0x2FF0+1 ) ; 
	public static final UnicodeBlock CJK_SYMBOLS_AND_PUNCTUATION = 
		new UnicodeBlock( "CJK_SYMBOLS_AND_PUNCTUATION", "CJK Symbols and Punctuation", 0x3000, 0x303F-0x3000+1 ) ; 
	public static final UnicodeBlock HIRAGANA = 
		new UnicodeBlock( "HIRAGANA", "Hiragana", 0x3040, 0x309F-0x3040+1 ) ; 
	public static final UnicodeBlock KATAKANA = 
		new UnicodeBlock( "KATAKANA", "Katakana", 0x30A0, 0x30FF-0x30A0+1 ) ; 
	public static final UnicodeBlock BOPOMOFO = 
		new UnicodeBlock( "BOPOMOFO", "Bopomofo", 0x3100, 0x312F-0x3100+1 ) ; 
	public static final UnicodeBlock HANGUL_COMPATIBILITY_JAMO = 
		new UnicodeBlock( "HANGUL_COMPATIBILITY_JAMO", "Hangul Compatibility Jamo", 0x3130, 0x318F-0x3130+1 ) ; 
	public static final UnicodeBlock KANBUN = 
		new UnicodeBlock( "KANBUN", "Kanbun", 0x3190, 0x319F-0x3190+1 ) ; 
	public static final UnicodeBlock BOPOMOFO_EXTENDED = 
		new UnicodeBlock( "BOPOMOFO_EXTENDED", "Bopomofo Extended", 0x31A0, 0x31BF-0x31A0+1 ) ; 
	public static final UnicodeBlock CJK_STROKES = 
		new UnicodeBlock( "CJK_STROKES", "CJK Strokes", 0x31C0, 0x31EF-0x31C0+1 ) ; 
	public static final UnicodeBlock KATAKANA_PHONETIC_EXTENSIONS = 
		new UnicodeBlock( "KATAKANA_PHONETIC_EXTENSIONS", "Katakana Phonetic Extensions", 0x31F0, 0x31FF-0x31F0+1 ) ; 
	public static final UnicodeBlock ENCLOSED_CJK_LETTERS_AND_MONTHS = 
		new UnicodeBlock( "ENCLOSED_CJK_LETTERS_AND_MONTHS", "Enclosed CJK Letters and Months", 0x3200, 0x32FF-0x3200+1 ) ; 
	public static final UnicodeBlock CJK_COMPATIBILITY = 
		new UnicodeBlock( "CJK_COMPATIBILITY", "CJK Compatibility", 0x3300, 0x33FF-0x3300+1 ) ; 
	public static final UnicodeBlock CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A = 
		new UnicodeBlock( "CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A", "CJK Unified Ideographs Extension A", 0x3400, 0x4DBF-0x3400+1 ) ; 
	public static final UnicodeBlock YIJING_HEXAGRAM_SYMBOLS = 
		new UnicodeBlock( "YIJING_HEXAGRAM_SYMBOLS", "Yijing Hexagram Symbols", 0x4DC0, 0x4DFF-0x4DC0+1 ) ; 
	public static final UnicodeBlock CJK_UNIFIED_IDEOGRAPHS = 
		new UnicodeBlock( "CJK_UNIFIED_IDEOGRAPHS", "CJK Unified Ideographs", 0x4E00, 0x9FFF-0x4E00+1 ) ; 
	public static final UnicodeBlock YI_SYLLABLES = 
		new UnicodeBlock( "YI_SYLLABLES", "Yi Syllables", 0xA000, 0xA48F-0xA000+1 ) ; 
	public static final UnicodeBlock YI_RADICALS = 
		new UnicodeBlock( "YI_RADICALS", "Yi Radicals", 0xA490, 0xA4CF-0xA490+1 ) ; 
	public static final UnicodeBlock MODIFIER_TONE_LETTERS = 
		new UnicodeBlock( "MODIFIER_TONE_LETTERS", "Modifier Tone Letters", 0xA700, 0xA71F-0xA700+1 ) ; 
	public static final UnicodeBlock SYLOTI_NAGRI = 
		new UnicodeBlock( "SYLOTI_NAGRI", "Syloti Nagri", 0xA800, 0xA82F-0xA800+1 ) ; 
	public static final UnicodeBlock HANGUL_SYLLABLES = 
		new UnicodeBlock( "HANGUL_SYLLABLES", "Hangul Syllables", 0xAC00, 0xD7AF-0xAC00+1 ) ; 
	public static final UnicodeBlock HIGH_SURROGATES = 
		new UnicodeBlock( "HIGH_SURROGATES", "High Surrogates", 0xD800, 0xDB7F-0xD800+1 ) ; 
	public static final UnicodeBlock HIGH_PRIVATE_USE_SURROGATES = 
		new UnicodeBlock( "HIGH_PRIVATE_USE_SURROGATES", "High Private Use Surrogates", 0xDB80, 0xDBFF-0xDB80+1 ) ; 
	public static final UnicodeBlock LOW_SURROGATES = 
		new UnicodeBlock( "LOW_SURROGATES", "Low Surrogates", 0xDC00, 0xDFFF-0xDC00+1 ) ; 
	public static final UnicodeBlock PRIVATE_USE_AREA = 
		new UnicodeBlock( "PRIVATE_USE_AREA", "Private Use Area", 0xE000, 0xF8FF-0xE000+1 ) ; 
	public static final UnicodeBlock CJK_COMPATIBILITY_IDEOGRAPHS = 
		new UnicodeBlock( "CJK_COMPATIBILITY_IDEOGRAPHS", "CJK Compatibility Ideographs", 0xF900, 0xFAFF-0xF900+1 ) ; 
	public static final UnicodeBlock ALPHABETIC_PRESENTATION_FORMS = 
		new UnicodeBlock( "ALPHABETIC_PRESENTATION_FORMS", "Alphabetic Presentation Forms", 0xFB00, 0xFB4F-0xFB00+1 ) ; 
	public static final UnicodeBlock ARABIC_PRESENTATION_FORMS_A = 
		new UnicodeBlock( "ARABIC_PRESENTATION_FORMS_A", "Arabic Presentation Forms-A", 0xFB50, 0xFDFF-0xFB50+1 ) ; 
	public static final UnicodeBlock VARIATION_SELECTORS = 
		new UnicodeBlock( "VARIATION_SELECTORS", "Variation Selectors", 0xFE00, 0xFE0F-0xFE00+1 ) ; 
	public static final UnicodeBlock VERTICAL_FORMS = 
		new UnicodeBlock( "VERTICAL_FORMS", "Vertical Forms", 0xFE10, 0xFE1F-0xFE10+1 ) ; 
	public static final UnicodeBlock COMBINING_HALF_MARKS = 
		new UnicodeBlock( "COMBINING_HALF_MARKS", "Combining Half Marks", 0xFE20, 0xFE2F-0xFE20+1 ) ; 
	public static final UnicodeBlock CJK_COMPATIBILITY_FORMS = 
		new UnicodeBlock( "CJK_COMPATIBILITY_FORMS", "CJK Compatibility Forms", 0xFE30, 0xFE4F-0xFE30+1 ) ; 
	public static final UnicodeBlock SMALL_FORM_VARIANTS = 
		new UnicodeBlock( "SMALL_FORM_VARIANTS", "Small Form Variants", 0xFE50, 0xFE6F-0xFE50+1 ) ; 
	public static final UnicodeBlock ARABIC_PRESENTATION_FORMS_B = 
		new UnicodeBlock( "ARABIC_PRESENTATION_FORMS_B", "Arabic Presentation Forms-B", 0xFE70, 0xFEFF-0xFE70+1 ) ; 
	public static final UnicodeBlock HALFWIDTH_AND_FULLWIDTH_FORMS = 
		new UnicodeBlock( "HALFWIDTH_AND_FULLWIDTH_FORMS", "Halfwidth and Fullwidth Forms", 0xFF00, 0xFFEF-0xFF00+1 ) ; 
	public static final UnicodeBlock SPECIALS = 
		new UnicodeBlock( "SPECIALS", "Specials", 0xFFF0, 0xFFFF-0xFFF0+1 ) ; 
	public static final UnicodeBlock LINEAR_B_SYLLABARY = 
		new UnicodeBlock( "LINEAR_B_SYLLABARY", "Linear B Syllabary", 0x10000, 0x1007F-0x10000+1 ) ; 
	public static final UnicodeBlock LINEAR_B_IDEOGRAMS = 
		new UnicodeBlock( "LINEAR_B_IDEOGRAMS", "Linear B Ideograms", 0x10080, 0x100FF-0x10080+1 ) ; 
	public static final UnicodeBlock AEGEAN_NUMBERS = 
		new UnicodeBlock( "AEGEAN_NUMBERS", "Aegean Numbers", 0x10100, 0x1013F-0x10100+1 ) ; 
	public static final UnicodeBlock ANCIENT_GREEK_NUMBERS = 
		new UnicodeBlock( "ANCIENT_GREEK_NUMBERS", "Ancient Greek Numbers", 0x10140, 0x1018F-0x10140+1 ) ; 
	public static final UnicodeBlock OLD_ITALIC = 
		new UnicodeBlock( "OLD_ITALIC", "Old Italic", 0x10300, 0x1032F-0x10300+1 ) ; 
	public static final UnicodeBlock GOTHIC = 
		new UnicodeBlock( "GOTHIC", "Gothic", 0x10330, 0x1034F-0x10330+1 ) ; 
	public static final UnicodeBlock UGARITIC = 
		new UnicodeBlock( "UGARITIC", "Ugaritic", 0x10380, 0x1039F-0x10380+1 ) ; 
	public static final UnicodeBlock OLD_PERSIAN = 
		new UnicodeBlock( "OLD_PERSIAN", "Old Persian", 0x103A0, 0x103DF-0x103A0+1 ) ; 
	public static final UnicodeBlock DESERET = 
		new UnicodeBlock( "DESERET", "Deseret", 0x10400, 0x1044F-0x10400+1 ) ; 
	public static final UnicodeBlock SHAVIAN = 
		new UnicodeBlock( "SHAVIAN", "Shavian", 0x10450, 0x1047F-0x10450+1 ) ; 
	public static final UnicodeBlock OSMANYA = 
		new UnicodeBlock( "OSMANYA", "Osmanya", 0x10480, 0x104AF-0x10480+1 ) ; 
	public static final UnicodeBlock CYPRIOT_SYLLABARY = 
		new UnicodeBlock( "CYPRIOT_SYLLABARY", "Cypriot Syllabary", 0x10800, 0x1083F-0x10800+1 ) ; 
	public static final UnicodeBlock KHAROSHTHI = 
		new UnicodeBlock( "KHAROSHTHI", "Kharoshthi", 0x10A00, 0x10A5F-0x10A00+1 ) ; 
	public static final UnicodeBlock BYZANTINE_MUSICAL_SYMBOLS = 
		new UnicodeBlock( "BYZANTINE_MUSICAL_SYMBOLS", "Byzantine Musical Symbols", 0x1D000, 0x1D0FF-0x1D000+1 ) ; 
	public static final UnicodeBlock MUSICAL_SYMBOLS = 
		new UnicodeBlock( "MUSICAL_SYMBOLS", "Musical Symbols", 0x1D100, 0x1D1FF-0x1D100+1 ) ; 
	public static final UnicodeBlock ANCIENT_GREEK_MUSICAL_NOTATION = 
		new UnicodeBlock( "ANCIENT_GREEK_MUSICAL_NOTATION", "Ancient Greek Musical Notation", 0x1D200, 0x1D24F-0x1D200+1 ) ; 
	public static final UnicodeBlock TAI_XUAN_JING_SYMBOLS = 
		new UnicodeBlock( "TAI_XUAN_JING_SYMBOLS", "Tai Xuan Jing Symbols", 0x1D300, 0x1D35F-0x1D300+1 ) ; 
	public static final UnicodeBlock MATHEMATICAL_ALPHANUMERIC_SYMBOLS = 
		new UnicodeBlock( "MATHEMATICAL_ALPHANUMERIC_SYMBOLS", "Mathematical Alphanumeric Symbols", 0x1D400, 0x1D7FF-0x1D400+1 ) ; 
	public static final UnicodeBlock CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B = 
		new UnicodeBlock( "CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B", "CJK Unified Ideographs Extension B", 0x20000, 0x2A6DF-0x20000+1 ) ; 
	public static final UnicodeBlock CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT = 
		new UnicodeBlock( "CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT", "CJK Compatibility Ideographs Supplement", 0x2F800, 0x2FA1F-0x2F800+1 ) ; 
	public static final UnicodeBlock TAGS = 
		new UnicodeBlock( "TAGS", "Tags", 0xE0000, 0xE007F-0xE0000+1 ) ; 
	public static final UnicodeBlock VARIATION_SELECTORS_SUPPLEMENT = 
		new UnicodeBlock( "VARIATION_SELECTORS_SUPPLEMENT", "Variation Selectors Supplement", 0xE0100, 0xE01EF-0xE0100+1 ) ; 
	public static final UnicodeBlock SUPPLEMENTARY_PRIVATE_USE_AREA_A = 
		new UnicodeBlock( "SUPPLEMENTARY_PRIVATE_USE_AREA_A", "Supplementary Private Use Area-A", 0xF0000, 0xFFFFF-0xF0000+1 ) ; 
	public static final UnicodeBlock SUPPLEMENTARY_PRIVATE_USE_AREA_B = 
		new UnicodeBlock( "SUPPLEMENTARY_PRIVATE_USE_AREA_B", "Supplementary Private Use Area-B", 0x100000, 0x10FFFF-0x100000+1 ) ; 
}
