
package astrolabe;

import java.util.regex.Pattern;

import org.exolab.castor.util.RegExpEvaluator;

public class CastorJREEvaluator implements RegExpEvaluator {

	private Pattern pattern ;

	public CastorJREEvaluator() {
		pattern = null ;
	}

	public boolean matches( String string ) {
		boolean b ;

		if ( pattern == null )
			b = true ;
		else
			b = pattern.matcher( string ).find() ;
		return b ;
	}

	public void setExpression(String expression ) {
		if ( expression == null )
			pattern = null ;
		else
			pattern = Pattern.compile( expression ) ;
	}

}
