
package astrolabe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import fri.patterns.interpreter.parsergenerator.Lexer;
import fri.patterns.interpreter.parsergenerator.Parser;
import fri.patterns.interpreter.parsergenerator.ParserTables;
import fri.patterns.interpreter.parsergenerator.Token;
import fri.patterns.interpreter.parsergenerator.lexer.LexerBuilder;
import fri.patterns.interpreter.parsergenerator.lexer.LexerException;
import fri.patterns.interpreter.parsergenerator.parsertables.LALRParserTables;
import fri.patterns.interpreter.parsergenerator.parsertables.ParserBuildException;
import fri.patterns.interpreter.parsergenerator.semantics.ReflectSemantic;
import fri.patterns.interpreter.parsergenerator.syntax.Syntax;
import fri.patterns.interpreter.parsergenerator.syntax.SyntaxException;
import fri.patterns.interpreter.parsergenerator.syntax.builder.SyntaxSeparation;

public class ParserSubstitute extends ReflectSemantic {

	private Lexer lexer ;
	private Parser parser ;

	private static String [][] rules = {
		{ "SUBSTITUTE", "DEFINITION" }, // single top level rule to avoid warning
		{ "DEFINITION", "CONDITION" },
		{ "DEFINITION", "CONDITION", "'?'", "DEFINITION", "':'", "DEFINITION" },
		{ "CONDITION", "COMPARISON" },
		{ "CONDITION", "CONDITION", "'&&'", "COMPARISON" },
		{ "CONDITION", "CONDITION", "'||'", "COMPARISON" },
		{ "COMPARISON", "EXPRESSION" },
		{ "COMPARISON", "COMPARISON", "'<'", "EXPRESSION" },
		{ "COMPARISON", "COMPARISON", "'>'", "EXPRESSION" },
		{ "COMPARISON", "COMPARISON", "'<='", "EXPRESSION" },
		{ "COMPARISON", "COMPARISON", "'>='", "EXPRESSION" },
		{ "COMPARISON", "COMPARISON", "'=='", "EXPRESSION" },
		{ "COMPARISON", "COMPARISON", "'!='", "EXPRESSION" },
		{ "COMPARISON", "COMPARISON", "'~'", "EXPRESSION" },
		{ "COMPARISON", "COMPARISON", "'!~'", "EXPRESSION" },
		{ "EXPRESSION", "TERM" },
		{ "EXPRESSION", "EXPRESSION", "'+'", "TERM" },
		{ "EXPRESSION", "EXPRESSION", "'-'", "TERM" },
		{ "TERM", "FACTOR" },
		{ "TERM", "TERM", "'*'", "FACTOR" },
		{ "TERM", "TERM", "'/'", "FACTOR" },
		{ "TERM", "TERM", "'%'", "FACTOR" },
		{ "FACTOR", "`number`" },
		{ "FACTOR", "`stringdef`" },
		{ "FACTOR", "verbatim" },
		{ "verbatim", "`char`" },
		{ "verbatim", "verbatim", "`char`" },
		{ "FACTOR", "'-'", "FACTOR" },
		{ "FACTOR", "'('", "DEFINITION", "')'" },
		{ Token.IGNORED, "`whitespaces`" }
	} ;

	public Object SUBSTITUTE( Object mark, Object DEFINITION, Object kram ) {
		return DEFINITION ;
	}

	public Object DEFINITION( Object CONDITION ) {
		return CONDITION ;
	}

	public Object DEFINITION( Object CONDITION, Object question, Object TRUE, Object colon, Object FALSE ) {
		if ( ( (Boolean) CONDITION ).booleanValue() )
			return TRUE ;
		return FALSE ;
	}

	public Object CONDITION( Object COMPARISON ) {
		return COMPARISON ;
	}

	public Object CONDITION( Object CONDITION, Object operator, Object COMPARISON ) {
		if ( operator.equals( "&&" ) )
			return ( (Boolean) CONDITION ).booleanValue()&&( (Boolean) COMPARISON ).booleanValue() ;
		return ( (Boolean) CONDITION ).booleanValue()||( (Boolean) COMPARISON ).booleanValue() ;
	}

	public Object COMPARISON( Object EXPRESSION ) {
		return EXPRESSION ;
	}

	public Object COMPARISON( Object COMPARISON, Object operator, Object EXPRESSION ) {
		if ( operator.equals( "<" ) )
			return number( COMPARISON ).doubleValue()<number( EXPRESSION ).doubleValue() ;
		if ( operator.equals( ">" ) )
			return number( COMPARISON ).doubleValue()>number( EXPRESSION ).doubleValue() ;
		if ( operator.equals( "<=" ) )
			return number( COMPARISON ).doubleValue()<=number( EXPRESSION ).doubleValue() ;
		if ( operator.equals( ">=" ) )
			return number( COMPARISON ).doubleValue()>=number( EXPRESSION ).doubleValue() ;
		if ( operator.equals( "==" ) )
			return number( COMPARISON ).doubleValue()==number( EXPRESSION ).doubleValue() ;
		if ( operator.equals( "!=" ) )
			return number( COMPARISON ).doubleValue()!=number( EXPRESSION ).doubleValue() ;
		if ( operator.equals( "~" ) )
			return string( COMPARISON ).matches( string( EXPRESSION ) ) ;
		return ! string( COMPARISON ).matches( string( EXPRESSION ) ) ;
	}

	public Object EXPRESSION( Object TERM ) {
		return TERM ;
	}

	public Object EXPRESSION( Object EXPRESSION, Object operator, Object TERM ) {
		if ( operator.equals( "+" ) )
			return number( EXPRESSION )+number( TERM ) ;
		return number( EXPRESSION )-number( TERM ) ;
	}

	public Object TERM( Object FACTOR ) {
		return FACTOR ;
	}

	public Object TERM( Object TERM, Object operator, Object FACTOR ) {
		if ( operator.equals( "*" ) )
			return number( TERM )*number( FACTOR ) ;
		if ( operator.equals( "/" ) )
			return number( TERM )/number( FACTOR ) ;
		return number( TERM )%number( FACTOR ) ;
	}

	private String string( Object value ) {
		return value instanceof String ? (String) value :
			new String( value.toString() ) ;
	}

	private Double number( Object value ) {
		return value instanceof Double ? (Double) value :
			value instanceof Long ? new Double( value.toString() ) :
				new Double( value.toString().length() ) ;
	}

	public Object FACTOR( Object value ) {
		Object factor ;

		try {
			// value is a "`number`"
			return Double.valueOf( value.toString() ) ;
		} catch ( NumberFormatException e ) {}
		if ( value.toString().matches( "\".*\"" ) )
			// value is a "`stringdef`"
			return value.toString().substring( 1, value.toString().length()-1 ) ;
		factor = Registry.retrieve( value.toString() ) ;
		if ( factor == null )
			// value is a literal
			return value ;
		try {
			// registry value is a "`number`"
			return Long.valueOf( factor.toString() ) ;
		} catch ( NumberFormatException e ) {}
		try {
			// registry value is a "`number`"
			return Double.valueOf( factor.toString() ) ;
		} catch ( NumberFormatException e ) {}
		// registry value should be a string
		return factor.toString() ;
	}

	public Object FACTOR( Object minus, Object FACTOR ) {
		return new Double( -( (Double) FACTOR ).doubleValue() ) ;
	}

	public Object FACTOR( Object leftParenthesis, Object DEFINITION, Object rightParenthesis ) {
		return DEFINITION ;
	}

	public Object verbatim( Object verbatim ) {
		return verbatim ;
	}

	public Object verbatim( Object verbatim, Object rune ) {
		return verbatim.toString()+rune.toString() ;
	}

	public ParserSubstitute() {
		SyntaxSeparation synSep ;
		ParserTables prsTab ;
		LexerBuilder lexBld ;

		try {
			SyntaxSeparation.DEBUG = false ;
			synSep = new SyntaxSeparation( new Syntax( rules ) ) ;
			prsTab = new LALRParserTables( synSep.getParserSyntax() ) ;
			LexerBuilder.DEBUG = false ;
			lexBld = new LexerBuilder( synSep.getLexerSyntax(), synSep.getIgnoredSymbols() ) ;
		} catch ( SyntaxException e ) { // SyntaxSeparation(), LexerBuilder()
			throw new RuntimeException( e.toString() ) ;
		} catch ( ParserBuildException e ) { // LALRParserTables()
			throw new RuntimeException( e.toString() ) ;
		} catch ( LexerException e ) { // LexerBuilder()
			throw new RuntimeException( e.toString() ) ;
		}


		lexer = lexBld.getLexer() ;
		lexer.setDebug( false ) ;

		parser = new Parser( prsTab ) ;
		parser.setDebug( false ) ;		
	}

	public String parse( String string ) {
		try {
			lexer.setInput( string ) ;
			parser.parse( lexer, this ) ;
		} catch ( IOException e ) {
			throw new RuntimeException( e.toString() ) ;
		}

		return new String( parser.getResult().toString() ) ;
	}

	public static void main( String[] argv ) {
		BufferedReader r ;
		String s ;

		for ( int a=0 ; a<argv.length-1 ; a=a+2 ) {
			try {
				Registry.registerNumber( argv[a], new Long( argv[a+1] ) ) ;
			} catch ( NumberFormatException el ) {
				try {
					Registry.registerNumber( argv[a], new Double( argv[a+1] ) ) ;
				} catch ( NumberFormatException ed ) {
					Registry.registerName( argv[a], new String( argv[a+1] ) ) ;
				}
			}
		}

		try {
			r = new BufferedReader( new InputStreamReader( System.in ) ) ;
			while ( ( s = r.readLine() ) != null )
				System.out.println( new ParserSubstitute().parse( s ) ) ;
		} catch ( IOException e ) {
			System.exit( 1 ) ;
		}
	}
}
