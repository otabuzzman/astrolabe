
package astrolabe;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashSet;

public class PostscriptStream extends FilterOutputStream {

	private final static HashSet<String> op = new HashSet<String>( Arrays.asList( "$error", ".begintransparencygroup", ".begintransparencymask", ".bytestring", ".charboxpath", ".currentaccuratecurves", ".currentblendmode", ".currentcurvejoin", ".currentdashadapt", ".currentdotlength", ".currentfilladjust2", ".currentlimitclamp", ".currentopacityalpha", ".currentoverprintmode", ".currentrasterop", ".currentshapealpha", ".currentsourcetransparent", ".currenttextknockout", ".currenttexturetransparent", ".dashpath", ".dicttomark", ".discardtransparencygroup", ".discardtransparencymask", ".endtransparencygroup", ".endtransparencymask", ".execn", ".filename", ".filename", ".fileposition", ".forceput", ".forceundef", ".forgetsave", ".getbitsrect", ".getdevice", ".inittransparencymask", ".knownget", ".locksafe", ".makeoperator", ".namestring", ".oserrno", ".oserrorstring", ".peekstring", ".rectappend", ".runandhide", ".setaccuratecurves", ".setblendmode", ".setcurvejoin", ".setdashadapt", ".setdebug", ".setdefaultmatrix", ".setdotlength", ".setfilladjust2", ".setlimitclamp", ".setmaxlength", ".setopacityalpha", ".setoverprintmode", ".setrasterop", ".setsafe", ".setshapealpha", ".setsourcetransparent", ".settextknockout", ".settexturetransparent", ".stringbreak", ".stringmatch", ".tempfile", ".type1decrypt", ".type1encrypt", ".type1execchar", ".unread", "=", "==", "FontDirectory", "GlobalFontDirectory", "ISOLatin1Encoding", "SharedFontDirectory", "StandardEncoding", "UserObject", "UserObjects", "abs", "add", "aload", "anchorsearch", "and", "arc", "arccos", "arcn", "arcsin", "arct", "arcto", "array", "ashow", "astore", "atan", "awidthshow", "begin", "bind", "bitshift", "bytesavailable", "cachestatus", "ceiling", "charpath", "clear", "cleardictstack", "cleartomark", "clip", "clippath", "cliprestore", "clipsave", "closefile", "closepath", "colorimage", "composefont", "concat", "concatmatrix", "copy", "copydevice", "copypage", "copyscanlines", "cos", "count", "countdictstack", "countexecstack", "counttomark", "cshow", "currentblackgeneration", "currentcacheparams", "currentcmykcolor", "currentcmykcolor", "currentcolor", "currentcolorrendering", "currentcolorscreen", "currentcolorspace", "currentcolorspace", "currentcolortransfer", "currentdash", "currentdevice", "currentdevparams", "currentdict", "currentfile", "currentflat", "currentfont", "currentglobal", "currentgray", "currentgstate", "currenthalftone", "currenthsbcolor", "currentlinecap", "currentlinejoin", "currentlinewidth", "currentmatrix", "currentmiterlimit", "currentobjectformat", "currentoverprint", "currentpacking", "currentpagedevice", "currentpagedevice", "currentpoint", "currentrgbcolor", "currentscreen", "currentshared", "currentsmoothness", "currentstrokeadjust", "currentsystemparams", "currenttransfer", "currentundercolorremoval", "currentuserparams", "cvi", "cvlit", "cvn", "cvr", "cvrs", "cvs", "cvx", "def", "defaultmatrix", "definefont", "defineresource", "defineuserobject", "deletefile", "dict", "dictstack", "div", "dtransform", "dup", "echo", "end", "eoclip", "eofill", "eq", "erasepage", "errordict", "exch", "exec", "execform", "execstack", "execuserobject", "executeonly", "executive", "exit", "exp", "false", "file", "filenameforall", "fileposition", "fill", "filter", "findcolorrendering", "finddevice", "findencoding", "findfont", "findlibfile", "findprotodevice", "findresource", "flattenpath", "floor", "flush", "flushfile", "flushpage", "for", "forall", "gcheck", "ge", "get", "getdeviceprops", "getenv", "getinterval", "globaldict", "glyphshow", "grestore", "grestoreall", "gsave", "gstate", "gt", "idetmatrix", "idiv", "idtransform", "if", "ifelse", "image", "imagemask", "index", "ineofill", "infill", "initclip", "initgraphics", "initmatrix", "instroke", "inueofill", "inufill", "inustroke", "inustroke", "invertmatrix", "itransform", "known", "kshow", "languagelevel", "le", "length", "lineto", "ln", "load", "log", "loop", "lt", "makefont", "makeimagedevice", "makepattern", "makewordimagedevice", "mark", "matrix", "max", "maxlength", "min", "mod", "moveto", "mul", "ne", "neg", "newpath", "noaccess", "nor", "not", "null", "nulldevice", "or", "packedarray", "pathbbox", "pathforall", "pop", "print", "printobject", "product", "prompt", "pstack", "put", "putdeviceprops", "putinterval", "quit", "rand", "rcheck", "rcurveto", "read", "readhexstring", "readline", "readonly", "readstring", "realtime", "rectclip", "rectfill", "rectstroke", "rectstroke", "renamefile", "repeat", "resetfile", "resourceforall", "resourcestatus", "restore", "reversepath", "revision", "rlineto", "rmoveto", "roll", "rootfont", "rotate", "round", "rrand", "run", "save", "scale", "scalefont", "scheck", "search", "selectfont", "serialnumber", "setbbox", "setblackgeneration", "setblackgeneration", "setcachedevice", "setcachedevice2", "setcachelimit", "setcacheparams", "setcharwidth", "setcmykcolor", "setcolor", "setcolorrendering", "setcolorscreen", "setcolorscreen", "setcolorspace", "setcolortranfer", "setcolortransfer", "setdash", "setdevice", "setdevparams", "setfileposition", "setflat", "setfont", "setglobal", "setgray", "setgstate", "sethalftone", "sethsbcolor", "setlinecap", "setlinejoin", "setlinewidth", "setmatrix", "setmiterlimit", "setobjectformat", "setoverprint", "setpacking", "setpagedevice", "setpagedevice", "setpattern", "setrgbcolor", "setscreen", "setshared", "setsmoothness", "setstrokeadjust", "setsystemparams", "settransfer", "setucacheparams", "setundercolorremoval", "setuserparams", "setvmthreshold", "setvmthreshold", "shareddict", "shfill", "show", "showpage", "sin", "sqrt", "srand", "stack", "start", "startjob", "status", "statusdict", "stop", "stopped", "store", "string", "stringwidth", "stroke", "strokepath", "sub", "systemdict", "token", "token", "transform", "translate", "true", "truncate", "type", "uappend", "ucache", "ucachestatus", "ueofill", "ueofill", "ufill", "undef", "undefinefont", "undefineresource", "undefineresource", "undefineuserobject", "upath", "userdict", "usertime", "ustroke", "ustrokepath", "version", "vmreclaim", "vmstatus", "wcheck", "where", "widthshow", "write", "writehexstring", "writeobject", "writestring", "xcheck", "xor", "xshow", "xyshow", "yshow" ) ) ;
	private final static HashSet<String> dc = new HashSet<String>( Arrays.asList( "!PS-Adobe-3.0", "!PS-Adobe-3.0", "Query", "%+", "%?BeginFeatureQuery:", "%?BeginPrinterQuery:", "%?BeginQuery:", "%?BeginResourceListQuery:", "%?BeginResourceQuery:", "%?BeginVMStatus:", "%?EndFeatureQuery", "%?EndPrinterQuery", "%?EndQuery", "%?EndResourceListQuery", "%?EndResourceQuery", "%?EndVMStatus", "%BeginCustomColor:", "%BeginData:", "%BeginDefaults", "%BeginDocument:", "%BeginEmulation:", "%BeginExitServer:", "%BeginFeature:", "%BeginObject:", "%BeginPageSetup:", "%BeginPreview:", "%BeginProcessColor:", "%BeginProlog", "%BeginResource:", "%BeginSetup", "%BoundingBox:", "%CMYKCustomColor:", "%Copyright:", "%CreationDate:", "%Creator:", "%DocumentCustomColors:", "%DocumentData:", "%DocumentMedia:", "%DocumentNeededResources:", "%DocumentPrinterRequired:", "%DocumentProcessColors:", "%DocumentSuppliedResources:", "%EOF", "%Emulation:", "%EndComments", "%EndCustomColor", "%EndData", "%EndDefaults", "%EndDocument", "%EndEmulation", "%EndExitServer", "%EndFeature", "%EndObject", "%EndPageSetup", "%EndPreview", "%EndProcessColor", "%EndProlog", "%EndResource", "%EndSetup", "%Extensions:", "%For:", "%IncludeDocument:", "%IncludeFeature:", "%IncludeResource:", "%LanguageLevel:", "%OperatorIntervention:", "%OperatorMessage:", "%Orientation:", "%Page:", "%PageBoundingBox:", "%PageCustomColors", "%PageMedia:", "%PageOrientation:", "%PageProcessColors", "%PageRequirements:", "%PageResources:", "%PageTrailer", "%Pages:", "%ProofMode:", "%RGBCustomColor:", "%Requirements:", "%Routing:", "%Title:", "%Trailer", "%VMlocation:", "%VMusage:", "%Version:" ) ) ;

	// configuration key (CK_)
	private final static String CK_PRECISION 	= "precision" ;

	private final static int DEFAULT_PRECISION	= 6 ;

	private final static NumberFormat numberFormat = NumberFormat.getInstance( java.util.Locale.UK ) ;

	public PostscriptStream( OutputStream out ) {
		super( out ) ;

		Class<?> clazz ;
		int precision ;

		clazz = this.getClass() ;
		while ( ! clazz.getSimpleName().equals( "PostscriptStream" ) )
			clazz = clazz.getSuperclass() ;

		precision = Configuration.getValue( clazz, CK_PRECISION, DEFAULT_PRECISION ) ;

		numberFormat.setMaximumFractionDigits( precision ) ;
		numberFormat.setGroupingUsed( false ) ;
	} 

	public boolean op( String op ) {
		boolean valid = PostscriptStream.op.contains( op ) ;

		if ( valid )
			print( op+'\n' ) ;

		return valid ;
	}

	public boolean dc( String dc, String[] keyword ) {
		boolean valid = PostscriptStream.dc.contains( dc ) ;
		StringBuffer buffer ;

		if ( valid ) {
			buffer = new StringBuffer( dc ) ;

			if ( keyword != null )
				for ( String k : keyword )
					buffer.append( ' '+k ) ;

			comment( buffer.toString() ) ;
		}

		return valid ;
	}

	public void array( boolean begin ) {        
		print( begin?"[\n":"]\n" ) ;
	} 

	public void proc( boolean begin ) {        
		print( begin?"{\n":"}\n" ) ;
	}

	public void dict( boolean begin ) {        
		print( begin?"<<\n":">>\n" ) ;
	}

	public void push( boolean bool ) {        
		print( ( bool?"true":"false" )+'\n' ) ;
	}

	public void push( int num ) {   
		print( num+"\n" ) ;
	} 

	public void push( long num ) {   
		print( num+"\n" ) ;
	} 

	public void push( double num ) {        
		print( numberFormat.format( num )+'\n' ) ;
	} 

	public void comment( String def ) {
		print( '%'+def+'\n' ) ;
	}

	public boolean script( String postscript ) {
		boolean valid = parse( postscript ) ;

		if ( valid )
			print( postscript+'\n' ) ;

		return valid ;
	} 

	private void print( String def ) {
		try {
			write( def.getBytes() ) ;
			flush() ;
			flush() ;
		} catch ( IOException e ) {
			throw new RuntimeException( e.toString() ) ;
		}
	}

	// todo
	private boolean parse( String postscript ) {        
		return true ;
	} 
}
