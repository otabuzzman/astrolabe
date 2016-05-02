
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
		{ "FACTOR", "'|'", "DEFINITION", "'|'" },
		{ "FACTOR", "'\u230a'", "DEFINITION", "'\u230b'" },	// floor Unicode
		{ "FACTOR", "'|>'", "DEFINITION", "'<|'" },			// floor ASCII alternative
		{ "FACTOR", "'\u2308'", "DEFINITION", "'\u2309'" },	// ceiling Unicode
		{ "FACTOR", "'|<'", "DEFINITION", "'>|'" },			// ceiling ASCII alternative
		{ Token.IGNORED, "`whitespaces`" }
	} ;

	public Object SUBSTITUTE( Object DEFINITION ) {
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
			return ( (Boolean) CONDITION ).booleanValue() && ( (Boolean) COMPARISON ).booleanValue() ;
		return ( (Boolean) CONDITION ).booleanValue() || ( (Boolean) COMPARISON ).booleanValue() ;
	}

	public Object COMPARISON( Object EXPRESSION ) {
		return EXPRESSION ;
	}

	public Object COMPARISON( Object COMPARISON, Object operator, Object EXPRESSION ) {
		switch ( types( COMPARISON, EXPRESSION) ) {
		case 0 :
		case 2 :
		case 20 :
		case 22 :
			if ( operator.equals( "<" ) )
				return ( (Long) number( COMPARISON ) ).longValue() < ( (Long) number( EXPRESSION ) ).longValue() ;
			if ( operator.equals( ">" ) )
				return ( (Long) number( COMPARISON ) ).longValue() > ( (Long) number( EXPRESSION ) ).longValue() ;
				if ( operator.equals( "<=" ) )
					return ( (Long) number( COMPARISON ) ).longValue() <= ( (Long) number( EXPRESSION ) ).longValue() ;
				if ( operator.equals( ">=" ) )
					return ( (Long) number( COMPARISON ) ).longValue() >= ( (Long) number( EXPRESSION ) ).longValue() ;
					if ( operator.equals( "==" ) )
						return ( (Long) number( COMPARISON ) ).longValue() == ( (Long) number( EXPRESSION ) ).longValue() ;
					if ( operator.equals( "!=" ) )
						return ( (Long) number( COMPARISON ) ).longValue() != ( (Long) number( EXPRESSION ) ).longValue() ;
		case 1 :
		case 21 :
			if ( operator.equals( "<" ) )
				return ( (Long) number( COMPARISON ) ).longValue() < ( (Double) number( EXPRESSION ) ).doubleValue() ;
			if ( operator.equals( ">" ) )
				return ( (Long) number( COMPARISON ) ).longValue() > ( (Double) number( EXPRESSION ) ).doubleValue() ;
				if ( operator.equals( "<=" ) )
					return ( (Long) number( COMPARISON ) ).longValue() <= ( (Double) number( EXPRESSION ) ).doubleValue() ;
				if ( operator.equals( ">=" ) )
					return ( (Long) number( COMPARISON ) ).longValue() >= ( (Double) number( EXPRESSION ) ).doubleValue() ;
					if ( operator.equals( "==" ) )
						return ( (Long) number( COMPARISON ) ).doubleValue() == ( (Double) number( EXPRESSION ) ).doubleValue() ;
					if ( operator.equals( "!=" ) )
						return ( (Long) number( COMPARISON ) ).doubleValue() != ( (Double) number( EXPRESSION ) ).doubleValue() ;
		case 10 :
		case 12 :
			if ( operator.equals( "<" ) )
				return ( (Double) number( COMPARISON ) ).doubleValue() < ( (Long) number( EXPRESSION ) ).longValue() ;
			if ( operator.equals( ">" ) )
				return ( (Double) number( COMPARISON ) ).doubleValue() > ( (Long) number( EXPRESSION ) ).longValue() ;
				if ( operator.equals( "<=" ) )
					return ( (Double) number( COMPARISON ) ).doubleValue() <= ( (Long) number( EXPRESSION ) ).longValue() ;
				if ( operator.equals( ">=" ) )
					return ( (Double) number( COMPARISON ) ).doubleValue() >= ( (Long) number( EXPRESSION ) ).longValue() ;
					if ( operator.equals( "==" ) )
						return ( (Double) number( COMPARISON ) ).doubleValue() == ( (Long) number( EXPRESSION ) ).doubleValue() ;
					if ( operator.equals( "!=" ) )
						return ( (Double) number( COMPARISON ) ).doubleValue() != ( (Long) number( EXPRESSION ) ).doubleValue() ;
		case 11 :
			if ( operator.equals( "<" ) )
				return ( (Double) number( COMPARISON ) ).doubleValue() < ( (Double) number( EXPRESSION ) ).doubleValue() ;
			if ( operator.equals( ">" ) )
				return ( (Double) number( COMPARISON ) ).doubleValue() > ( (Double) number( EXPRESSION ) ).doubleValue() ;
				if ( operator.equals( "<=" ) )
					return ( (Double) number( COMPARISON ) ).doubleValue() <= ( (Double) number( EXPRESSION ) ).doubleValue() ;
				if ( operator.equals( ">=" ) )
					return ( (Double) number( COMPARISON ) ).doubleValue() >= ( (Double) number( EXPRESSION ) ).doubleValue() ;
					if ( operator.equals( "==" ) )
						return ( (Double) number( COMPARISON ) ).doubleValue() == ( (Double) number( EXPRESSION ) ).doubleValue() ;
					if ( operator.equals( "!=" ) )
						return ( (Double) number( COMPARISON ) ).doubleValue() != ( (Double) number( EXPRESSION ) ).doubleValue() ;
		}
		if ( operator.equals( "~" ) )
			return string( COMPARISON ).matches( string( EXPRESSION ) ) ;
		return ! string( COMPARISON ).matches( string( EXPRESSION ) ) ;
	}

	public Object EXPRESSION( Object TERM ) {
		return TERM ;
	}

	public Object EXPRESSION( Object EXPRESSION, Object operator, Object TERM ) {
		switch ( types( EXPRESSION, TERM ) ) {
		case 0 :
		case 2 :
		case 20 :
			if ( operator.equals( "+" ) )
				return ( (Long) number( EXPRESSION ) ).longValue() + ( (Long) number( TERM ) ).longValue() ;
			return ( (Long) number( EXPRESSION ) ).longValue() - ( (Long) number( TERM ) ).longValue() ;
		case 1 :
		case 21 :
			if ( operator.equals( "+" ) )
				return ( (Long) number( EXPRESSION ) ).longValue() + ( (Double) number( TERM ) ).doubleValue() ;
			return ( (Long) number( EXPRESSION ) ).longValue() - ( (Double) number( TERM ) ).doubleValue() ;
		case 10 :
		case 12 :
			if ( operator.equals( "+" ) )
				return ( (Double) number( EXPRESSION ) ).doubleValue() + ( (Long) number( TERM ) ).longValue() ;
			return ( (Double) number( EXPRESSION ) ).doubleValue() - ( (Long) number( TERM ) ).longValue() ;
		case 11 :
			if ( operator.equals( "+" ) )
				return ( (Double) number( EXPRESSION ) ).doubleValue() + ( (Double) number( TERM ) ).doubleValue() ;
			return ( (Double) number( EXPRESSION ) ).doubleValue() - ( (Double) number( TERM ) ).doubleValue() ;
		case 22 :
			if ( operator.equals( "+" ) )
				return string( EXPRESSION ) + string( TERM ) ;
			return ( (Long) number( EXPRESSION ) ).longValue() - ( (Long) number( TERM ) ).longValue() ;
		}

		return null ;
	}

	public Object TERM( Object FACTOR ) {
		return FACTOR ;
	}

	public Object TERM( Object TERM, Object operator, Object FACTOR ) {
		switch ( types( TERM, FACTOR ) ) {
		case 0 :
		case 2 :
		case 20 :
		case 22 :
			if ( operator.equals( "*" ) )
				return ( (Long) number( TERM ) ).longValue() * ( (Long) number( FACTOR ) ).longValue() ;
			if ( operator.equals( "/" ) )
				return ( (Long) number( TERM ) ).longValue() / ( (Long) number( FACTOR ) ).longValue() ;
			return ( (Long) number( TERM ) ).longValue() % ( (Long) number( FACTOR ) ).longValue() ;
		case 1 :
		case 21 :
			if ( operator.equals( "*" ) )
				return ( (Long) number( TERM ) ).longValue() * ( (Double) number( FACTOR ) ).doubleValue() ;
			if ( operator.equals( "/" ) )
				return ( (Long) number( TERM ) ).longValue() / ( (Double) number( FACTOR ) ).doubleValue() ;
			return ( (Long) number( TERM ) ).longValue() % ( (Double) number( FACTOR ) ).doubleValue() ;
		case 10 :
		case 12 :
			if ( operator.equals( "*" ) )
				return ( (Double) number( TERM ) ).doubleValue() * ( (Long) number( FACTOR ) ).longValue() ;
			if ( operator.equals( "/" ) )
				return ( (Double) number( TERM ) ).doubleValue() / ( (Long) number( FACTOR ) ).longValue() ;
			return ( (Double) number( TERM ) ).doubleValue() % ( (Long) number( FACTOR ) ).longValue() ;
		case 11 :
			if ( operator.equals( "*" ) )
				return ( (Double) number( TERM ) ).doubleValue() * ( (Double) number( FACTOR ) ).doubleValue() ;
			if ( operator.equals( "/" ) )
				return ( (Double) number( TERM ) ).doubleValue() / ( (Double) number( FACTOR ) ).doubleValue() ;
			return ( (Double) number( TERM ) ).doubleValue() % ( (Double) number( FACTOR ) ).doubleValue() ;
		}

		return null ;
	}

	private int types( Object a, Object b ) {
		// Long, 	Long, 		0
		// Long,	Double,		1
		// Long,	String,		2
		// Double, 	Long, 		10
		// Double,	Double,		11
		// Double,	String,		12
		// String, 	Long, 		20
		// String,	Double,		21
		// String,	String,		22
		return
		( a instanceof Long ? 0 : a instanceof Double ? 1 : 2 )*10+
		( b instanceof Long ? 0 : b instanceof Double ? 1 : 2 ) ;
	}

	private String string( Object value ) {
		return value instanceof String ? (String) value :
			new String( value.toString() ) ;
	}

	private Object number( Object value ) {
		return value instanceof String ?
				new Long( value.toString().length() ) : value ;
	}

	public Object FACTOR( Object value ) {
		Object factor ;

		try {
			// value is a "`number`"
			return Long.valueOf( value.toString() ) ;
		} catch ( NumberFormatException e ) {}
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
		switch ( types( FACTOR, FACTOR ) ) {
		case 0 :
		case 22 :
			return -(Long) number( FACTOR ) ;
		case 11 :
			return -(Double) number( FACTOR ) ;
		}

		return null ;
	}

	public Object FACTOR( Object leftParenthesis, Object DEFINITION, Object rightParenthesis ) {
		if ( leftParenthesis.equals( "|" ) )
			if ( types( DEFINITION, DEFINITION )==11 )
				return java.lang.Math.abs( (Double) number( DEFINITION ) ) ;
			else
				return java.lang.Math.abs( (Long) number( DEFINITION ) ) ;
		if ( leftParenthesis.equals( "|>" ) || leftParenthesis.equals( "\u230a" ) )
			if ( types( DEFINITION, DEFINITION )==11 )
				return ( (Double) java.lang.Math.floor( (Double) number( DEFINITION ) ) ).longValue() ;
			else
				return ( (Double) java.lang.Math.floor( (Long) number( DEFINITION ) ) ).longValue() ;
		if ( leftParenthesis.equals( "|<" ) || leftParenthesis.equals( "\u2308" ) )
			if ( types( DEFINITION, DEFINITION )==11 )
				return ( (Double) java.lang.Math.ceil( (Double) number( DEFINITION ) ) ).longValue() ;
			else
				return ( (Double) java.lang.Math.ceil( (Long) number( DEFINITION ) ) ).longValue() ;
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
				AstrolabeRegistry.registerNumber( argv[a], new Long( argv[a+1] ) ) ;
			} catch ( NumberFormatException el ) {
				try {
					AstrolabeRegistry.registerNumber( argv[a], new Double( argv[a+1] ) ) ;
				} catch ( NumberFormatException ed ) {
					AstrolabeRegistry.registerName( argv[a], new String( argv[a+1] ) ) ;
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
