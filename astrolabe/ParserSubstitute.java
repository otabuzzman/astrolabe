
package astrolabe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;

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
		{ "CONDITION", "EXPRESSION" },
		{ "CONDITION", "CONDITION", "'<'", "EXPRESSION" },
		{ "CONDITION", "CONDITION", "'>'", "EXPRESSION" },
		{ "CONDITION", "CONDITION", "'<='", "EXPRESSION" },
		{ "CONDITION", "CONDITION", "'>='", "EXPRESSION" },
		{ "CONDITION", "CONDITION", "'=='", "EXPRESSION" },
		{ "CONDITION", "CONDITION", "'!='", "EXPRESSION" },
		{ "CONDITION", "CONDITION", "'~'", "EXPRESSION" },
		{ "CONDITION", "CONDITION", "'!~'", "EXPRESSION" },
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

	public Object DEFINITION( Object COMPARISON ) {
		return COMPARISON ;
	}

	public Object DEFINITION( Object CONDITION, Object question, Object ifDEFINITION, Object colon, Object DEFINITION ) {
		if ( ( (Boolean) CONDITION ).booleanValue() )
			return ifDEFINITION ;
		return DEFINITION ;
	}

	public Object CONDITION( Object EXPRESSION ) {
		return EXPRESSION ;
	}

	public Object CONDITION( Object CONDITION, Object operator, Object EXPRESSION ) {
		boolean longCONDITION = CONDITION instanceof Long ;
		boolean longEXPRESSION = EXPRESSION instanceof Long ;

		if ( operator.equals( "<" ) )
			return new Boolean( ( longCONDITION?( (Long) CONDITION ).longValue():( (Double) CONDITION ).doubleValue() )<
					( longEXPRESSION?( (Long) EXPRESSION ).longValue():( (Double) EXPRESSION ).doubleValue() ) ) ;
		if ( operator.equals( ">" ) )
			return new Boolean( ( longCONDITION?( (Long) CONDITION ).longValue():( (Double) CONDITION ).doubleValue() )>
			( longEXPRESSION?( (Long) EXPRESSION ).longValue():( (Double) EXPRESSION ).doubleValue() ) ) ;
		if ( operator.equals( "<=" ) )
			return new Boolean( ( longCONDITION?( (Long) CONDITION ).longValue():( (Double) CONDITION ).doubleValue() )<=
				( longEXPRESSION?( (Long) EXPRESSION ).longValue():( (Double) EXPRESSION ).doubleValue() ) ) ;
		if ( operator.equals( ">=" ) )
			return new Boolean( ( longCONDITION?( (Long) CONDITION ).longValue():( (Double) CONDITION ).doubleValue() )>=
				( longEXPRESSION?( (Long) EXPRESSION ).longValue():( (Double) EXPRESSION ).doubleValue() ) ) ;
		if ( operator.equals( "==" ) )
			return new Boolean( ( longCONDITION?( (Long) CONDITION ).longValue():( (Double) CONDITION ).doubleValue() )==
				( longEXPRESSION?( (Long) EXPRESSION ).longValue():( (Double) EXPRESSION ).doubleValue() ) ) ;
		if ( operator.equals( "!=" ) )
			return new Boolean( ( longCONDITION?( (Long) CONDITION ).longValue():( (Double) CONDITION ).doubleValue() )!=
				( longEXPRESSION?( (Long) EXPRESSION ).longValue():( (Double) EXPRESSION ).doubleValue() ) ) ;
		if ( operator.equals( "~" ) )
			return new Boolean( CONDITION.toString().matches( EXPRESSION.toString() ) ) ;
		return new Boolean( ! CONDITION.toString().matches( EXPRESSION.toString() ) ) ;
	}

	public Object EXPRESSION( Object TERM ) {
		return TERM ;
	}

	public Object EXPRESSION( Object EXPRESSION, Object operator, Object TERM ) {
		boolean longEXPRESSION = EXPRESSION instanceof Long ;
		boolean longTERM = TERM instanceof Long ;
		Double result ;

		if ( operator.equals( "+" ) )
			result = new Double( ( longEXPRESSION?( (Long) EXPRESSION ).longValue():( (Double) EXPRESSION ).doubleValue() )+
					( longTERM?( (Long) TERM ).longValue():( (Double) TERM ).doubleValue() ) ) ;
		else 
			result = new Double( ( longEXPRESSION?( (Long) EXPRESSION ).longValue():( (Double) EXPRESSION ).doubleValue() )-
					( longTERM?( (Long) TERM ).longValue():( (Double) TERM ).doubleValue() ) ) ;
		if ( longEXPRESSION || longTERM )
			return new Long( result.longValue() ) ;
		return result ;
	}

	public Object TERM( Object FACTOR ) {
		return FACTOR ;
	}

	public Object TERM( Object TERM, Object operator, Object FACTOR ) {
		boolean longTERM = TERM instanceof Long ;
		boolean longFACTOR = FACTOR instanceof Long ;
		Double result ;

		if ( operator.equals( "*" ) )
			result = new Double( ( longTERM?( (Long) TERM ).longValue():( (Double) TERM ).doubleValue() )*
					( longFACTOR?( (Long) FACTOR ).longValue():( (Double) FACTOR ).doubleValue() ) ) ;
		else if ( operator.equals( "/" ) )
			result = new Double( ( longTERM?( (Long) TERM ).longValue():( (Double) TERM ).doubleValue() )/
					( longFACTOR?( (Long) FACTOR ).longValue():( (Double) FACTOR ).doubleValue() ) ) ;
		else
			result = new Double( ( longTERM?( (Long) TERM ).longValue():( (Double) TERM ).doubleValue() )%
					( longFACTOR?( (Long) FACTOR ).longValue():( (Double) FACTOR ).doubleValue() ) ) ;
		if ( longTERM || longFACTOR )
			return new Long( result.longValue() ) ;
		return result ;
	}

	public Object FACTOR( Object value ) {
		try {
			// value is a "`number`"
			return valueNumber( value ) ;
		} catch ( NumberFormatException eV ) {
			if ( value.toString().matches( "\".*\"" ) )
				// value is a "`stringdef`"
				return value.toString().substring( 1, value.toString().length()-1 ) ;
			Object objectFACTOR = Registry.retrieve( value.toString() ) ;
			if ( objectFACTOR != null ) {
				// value is a valid registry key
				try {
					return valueNumber( objectFACTOR ) ;
				} catch ( NumberFormatException eO ) {
					return objectFACTOR ;
				}
			}
			// value is invalid
			String msg ;

			msg = MessageCatalog.message( ApplicationConstant.GC_APPLICATION, ApplicationConstant.LK_MESSAGE_PARAMETERNOTAVLID ) ;
			msg = MessageFormat.format( msg, new Object[] { "\""+value.toString()+"\"", "" } ) ;

			throw new RuntimeException ( msg ) ;
		}
	}

	private static Object valueNumber( Object value ) throws NumberFormatException {
		try {
			// value is a long "`number`"
			return Long.valueOf( value.toString() ) ;
		} catch ( NumberFormatException e ) {
			// value is a double "`number`"
			return Double.valueOf( value.toString() ) ;
		}
	}

	public Object FACTOR( Object minus, Object FACTOR ) {
		if ( FACTOR instanceof Long )
			return new Long( -( (Long) FACTOR ).longValue() ) ;
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

		for ( int a=1 ; a<argv.length-1 ; a=a+2 )
			try {
				Registry.registerNumber( argv[a], new Long( argv[a+1] ) ) ;
			} catch ( NumberFormatException el ) {
				try {
					Registry.registerNumber( argv[a], new Double( argv[a+1] ) ) ;
				} catch ( NumberFormatException ed ) {
					Registry.registerName( argv[a], new String( argv[a+1] ) ) ;
				}
			}

			try {
				r = new BufferedReader( new InputStreamReader( System.in ) ) ;
				while ( ( s = r.readLine() ) != null )
					System.err.println( new ParserSubstitute().parse( s ) ) ;
			} catch ( IOException e ) {
				System.exit( 1 ) ;
			}
	}
}
