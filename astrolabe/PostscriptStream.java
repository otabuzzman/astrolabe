
package astrolabe;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.NumberFormat;

public class PostscriptStream extends FilterOutputStream {

	// configuration key (CK_)
	private final static String CK_PRECISION 	= "precision" ;

	private final static int DEFAULT_PRECISION	= 6 ;

	private final static NumberFormat numberFormat = NumberFormat.getInstance( java.util.Locale.UK ) ;

	public final Operator operator = new Operator();
	public final DSC dsc = new DSC();

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
		print( ( bool?"true":"false" )+"\n" ) ;
	}

	public void push( int num ) {        
		print( num+"\n" ) ;
	} 

	public void push( long num ) {        
		print( num+"\n" ) ;
	} 

	public void push( double num ) {        
		print( numberFormat.format( num )+"\n" ) ;
	} 

	public void push( String string ) throws ParameterNotValidException {
		if ( ! string.matches( "[\\p{Print}\\n\\r\\t ]+" ) )
			throw new ParameterNotValidException( ParameterNotValidError.errmsg( string , null ) ) ;

		print( string+"\n" ) ;
	} 

	protected void print( String def ) {
		try {
			write( def.getBytes() ) ;
			flush() ;
			flush() ;
		} catch ( IOException e ) {
			throw new RuntimeException( e.toString() ) ;
		}
	}

	public void comment( String def ) {
		print( "%"+def+"\n" ) ;
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

		public void currentlinewidth() {        
			print( "currentlinewidth\n" ) ;
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

		public void neg() {        
			print( "neg\n" ) ;
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

		public void rand() {        
			print( "rand\n" ) ;
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

		public void setgray() { 
			print( "setgray\n" ) ;
		} 

		public void setgray( double num ) {        
			push( num ) ;
			setgray() ;
		} 

		public void setlinecap() {        
			print( "setlinecap\n" ) ;
		}

		public void setlinecap( int num ) {        
			push( num%3 ) ;
			setlinecap() ;
		}

		public void setlinewidth() {        
			print( "setlinewidth\n" ) ;
		}

		public void setlinewidth( double num ) {        
			push( num ) ;
			setlinewidth() ;
		}

		public void setpagedevice() {        
			print( "setpagedevice\n" ) ;
		}

		public void setrgbcolor( double r, double g, double b ) {
			push( r ) ;
			push( g ) ;
			push( b ) ;
			setrgbcolor() ;
		}

		public void setrgbcolor() {        
			print( "setrgbcolor\n" ) ;
		}

		public void show() {        
			print( "show\n" ) ;
		}

		public void showpage() {        
			print( "showpage\n" ) ;
		}

		public void srand() {        
			print( "srand\n" ) ;
		}

		public void srand( long num ) {        
			push( num ) ;
			srand() ;
		}

		public void stringwidth() {        
			print( "stringwidth\n" ) ;
		}

		public void stroke() {        
			print( "stroke\n" ) ;
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
}
